package com.nrru.registration.service;

import com.nrru.registration.dto.ApiResponse;
import com.nrru.registration.entity.Course;
import com.nrru.registration.entity.Enrollment;
import com.nrru.registration.entity.Schedule;
import com.nrru.registration.entity.Student;
import com.nrru.registration.exception.*;
import com.nrru.registration.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final ScheduleRepository scheduleRepository;
    private final CoursePrerequisiteRepository prerequisiteRepository;
    private final RegistrationSlotRepository registrationSlotRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             CourseRepository courseRepository,
                             StudentRepository studentRepository,
                             ScheduleRepository scheduleRepository,
                             CoursePrerequisiteRepository prerequisiteRepository,
                             RegistrationSlotRepository registrationSlotRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.scheduleRepository = scheduleRepository;
        this.prerequisiteRepository = prerequisiteRepository;
        this.registrationSlotRepository = registrationSlotRepository;
    }

    private void validateRegistrationWindow(Student student) {
        LocalDateTime now = LocalDateTime.now();
        List<com.nrru.registration.entity.RegistrationSlot> activeSlots = registrationSlotRepository.findActiveSlotsAt(now);

        // Filter slot matching target year if specified
        com.nrru.registration.entity.RegistrationSlot validSlot = activeSlots.stream()
                .filter(s -> s.getTargetYear() == null || s.getTargetYear() == student.getCurrentYear())
                .findFirst()
                .orElse(null);

        if (validSlot == null) {
            throw new RuntimeException("ระบบปิดให้บริการลงทะเบียนเรียนในขณะนี้");
        }

        // Check confirmation status if slot is REGULAR
        String type = validSlot.getSlotType() != null ? validSlot.getSlotType() : "REGULAR";
        if ("REGULAR".equalsIgnoreCase(type) && Boolean.TRUE.equals(student.getIsRegistrationConfirmed())) {
            throw new RuntimeException("ท่านได้ยืนยันการลงทะเบียนเรียนเรียบร้อยแล้ว ไม่สามารถแก้ไขรายวิชาได้ในขณะนี้ (สามารถแก้ไขได้ในช่วงเปิดระบบเพิ่ม-ลดรายวิชา)");
        }
    }

    @Transactional
    public ApiResponse enrollStudent(Long studentId, Long courseId) {

        // 1. ดึงข้อมูลนักศึกษา
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบข้อมูลนักศึกษา"));

        // 1.1 เช็คช่วงเวลาลงทะเบียนและการยืนยัน
        validateRegistrationWindow(student);

        // 2. 🔥 ล็อกแถวรายวิชา (Pessimistic Lock) ป้องกัน Over-booking
        Course course = courseRepository.findByIdWithLock(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบรายวิชา"));

        // 3. เช็คว่ามีที่นั่งว่างไหม
        if (course.getEnrolledStudentCount() >= course.getSeatCapacity()) {
            throw new SeatFullException("ที่นั่งเต็มแล้ว!");
        }

        // 4. เช็คว่านักศึกษาคนนี้ลงวิชานี้ไปแล้วหรือยัง (ป้องกันการลงซ้ำ)
        boolean alreadyEnrolled = enrollmentRepository
                .existsByStudentStudentIdAndCourseCourseId(studentId, courseId);
        if (alreadyEnrolled) {
            throw new RuntimeException("คุณลงทะเบียนวิชานี้ไปแล้ว!");
        }

        // 5. เช็ควิชาบังคับก่อน (Prerequisite)
        List<Long> prereqIds = prerequisiteRepository.findPrerequisiteCourseIds(courseId);
        for (Long prereqId : prereqIds) {
            boolean passed = enrollmentRepository
                    .existsByStudentStudentIdAndCourseCourseIdAndEnrollmentStatus(studentId, prereqId, "REGISTERED");
            if (!passed) {
                throw new PrerequisiteNotMetException("คุณยังไม่ผ่านวิชาบังคับก่อน!");
            }
        }

        // 6. เช็คเวลาเรียนทับซ้อน
        List<Schedule> newCourseSchedules = scheduleRepository.findByCourse_CourseId(courseId);
        for (Schedule schedule : newCourseSchedules) {
            List<Schedule> conflicts = scheduleRepository.findConflictingSchedules(
                    studentId,
                    schedule.getWeekdayCode(),
                    schedule.getStartTime(),
                    schedule.getEndTime()
            );
            if (!conflicts.isEmpty()) {
                throw new ScheduleConflictException("เวลาเรียนทับซ้อนกับวิชาอื่นที่คุณลงทะเบียนไว้!");
            }
        }

        // 7. สร้าง Enrollment record
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentStatus("REGISTERED");
        enrollment.setEnrolledAt(LocalDateTime.now());

        enrollmentRepository.save(enrollment);

        // 8. อัปเดตจำนวนนักศึกษาที่ลงทะเบียน
        course.setEnrolledStudentCount(course.getEnrolledStudentCount() + 1);
        courseRepository.save(course);

        return new ApiResponse("ลงทะเบียนสำเร็จ!", true);
    }

    @Transactional
    public ApiResponse dropCourse(Long studentId, Long courseId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบข้อมูลนักศึกษา"));

        // เช็คช่วงเวลาลงทะเบียนและการยืนยัน
        validateRegistrationWindow(student);

        // 1. ค้นหาข้อมูลการลงทะเบียน
        Enrollment enrollment = enrollmentRepository
                .findByStudentStudentIdAndCourseCourseId(studentId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบข้อมูลการลงทะเบียนวิชานี้"));

        if ("DROPPED".equals(enrollment.getEnrollmentStatus())) {
            throw new RuntimeException("คุณได้ถอนวิชานี้ไปแล้ว!");
        }

        // 2. ปรับสถานะเป็น DROPPED
        enrollment.setEnrollmentStatus("DROPPED");
        enrollmentRepository.save(enrollment);

        // 3. ลดจำนวนนักศึกษาที่ลงทะเบียน
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบรายวิชา"));

        if (course.getEnrolledStudentCount() > 0) {
            course.setEnrolledStudentCount(course.getEnrolledStudentCount() - 1);
            courseRepository.save(course);
        }

        return new ApiResponse("ถอนวิชาสำเร็จ!", true);
    }

    @Transactional
    public ApiResponse confirmRegistration(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบข้อมูลนักศึกษา"));

        student.setIsRegistrationConfirmed(true);
        studentRepository.save(student);
        return new ApiResponse("ยืนยันการลงทะเบียนเรียนเรียบร้อยแล้ว!", true);
    }

    public java.util.Map<String, Object> getRegistrationStatus(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบข้อมูลนักศึกษา"));

        LocalDateTime now = LocalDateTime.now();
        List<com.nrru.registration.entity.RegistrationSlot> activeSlots = registrationSlotRepository.findActiveSlotsAt(now);

        com.nrru.registration.entity.RegistrationSlot validSlot = activeSlots.stream()
                .filter(s -> s.getTargetYear() == null || s.getTargetYear() == student.getCurrentYear())
                .findFirst()
                .orElse(null);

        boolean isConfirmed = Boolean.TRUE.equals(student.getIsRegistrationConfirmed());
        boolean canModify = false;
        String message = "ระบบปิดให้บริการลงทะเบียนเรียนในขณะนี้";

        if (validSlot != null) {
            String slotType = validSlot.getSlotType() != null ? validSlot.getSlotType() : "REGULAR";
            if ("ADD_DROP".equalsIgnoreCase(slotType)) {
                canModify = true;
                message = "อยู่ในช่วงเวลาเพิ่ม-ลดรายวิชา (Add/Drop Period)";
            } else {
                if (isConfirmed) {
                    canModify = false;
                    message = "ยืนยันการลงทะเบียนเรียนเรียบร้อยแล้ว (ล็อกการแก้ไขจนกว่าจะถึงช่วงเพิ่ม-ลดรายวิชา)";
                } else {
                    canModify = true;
                    message = "อยู่ในช่วงเวลาเปิดลงทะเบียนเรียนตามปกติ";
                }
            }
        }

        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("isConfirmed", isConfirmed);
        result.put("activeSlot", validSlot);
        result.put("canModify", canModify);
        result.put("message", message);
        return result;
    }

    public List<Enrollment> getMySchedule(Long studentId) {
        return enrollmentRepository.findByStudentStudentIdAndEnrollmentStatus(studentId, "REGISTERED");
    }
}