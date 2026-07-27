package com.nrru.registration.service;

import com.nrru.registration.dto.ApiResponse;
import com.nrru.registration.entity.Course;
import com.nrru.registration.entity.Student;
import com.nrru.registration.entity.Enrollment;
import com.nrru.registration.entity.Schedule;
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

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             CourseRepository courseRepository,
                             StudentRepository studentRepository,
                             ScheduleRepository scheduleRepository,
                             CoursePrerequisiteRepository prerequisiteRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.scheduleRepository = scheduleRepository;
        this.prerequisiteRepository = prerequisiteRepository;
    }

    @Transactional
    public ApiResponse enrollStudent(Long studentId, Long courseId) {

        // 1. ดึงข้อมูลนักศึกษา
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบข้อมูลนักศึกษา"));

        // 2. 🔥 ล็อกแถวรายวิชา (Pessimistic Lock) ป้องกัน Over-booking
        Course course = courseRepository.findByIdWithLock(courseId)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบรายวิชา"));

        // 3. เช็คว่ามีที่นั่งว่างไหม (ใช้ค่าล่าสุดจาก Database)
        if (course.getEnrolledStudentCount() >= course.getSeatCapacity()) {
            return new ApiResponse("ที่นั่งเต็มแล้ว!", false);
        }

        // 4. เช็คว่านักศึกษาคนนี้ลงวิชานี้ไปแล้วหรือยัง (ป้องกันการลงซ้ำ)
        boolean alreadyEnrolled = enrollmentRepository
                .existsByStudentStudentIdAndCourseCourseId(studentId, courseId);
        if (alreadyEnrolled) {
            return new ApiResponse("คุณลงทะเบียนวิชานี้ไปแล้ว!", false);
        }

        // 5. เช็ควิชาบังคับก่อน (Prerequisite)
        List<Long> prereqIds = prerequisiteRepository.findPrerequisiteCourseIds(courseId);
        for (Long prereqId : prereqIds) {
            boolean passed = enrollmentRepository
                    .existsByStudentStudentIdAndCourseCourseIdAndEnrollmentStatus(studentId, prereqId, "REGISTERED");
            if (!passed) {
                return new ApiResponse("คุณยังไม่ผ่านวิชาบังคับก่อน!", false);
            }
        }

        // 6. เช็คเวลาเรียนทับซ้อน/ตารางชนกัน (Schedule Conflict)
        List<Schedule> newCourseSchedules = scheduleRepository.findByCourse_CourseId(courseId);
        for (Schedule schedule : newCourseSchedules) {
            List<Schedule> conflicts = scheduleRepository.findConflictingSchedules(
                    studentId,
                    schedule.getWeekdayCode(),
                    schedule.getStartTime(),
                    schedule.getEndTime()
            );
            if (!conflicts.isEmpty()) {
                return new ApiResponse("เวลาเรียนทับซ้อนกับวิชาอื่นที่คุณลงทะเบียนไว้!", false);
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
        int currentCount = course.getEnrolledStudentCount() != null ? course.getEnrolledStudentCount() : 0;
        course.setEnrolledStudentCount(currentCount + 1);
        courseRepository.save(course);

        return new ApiResponse("ลงทะเบียนสำเร็จ!", true);
    }

    @Transactional
    public ApiResponse dropCourse(Long studentId, Long courseId) {
        // 1. ค้นหาข้อมูลการลงทะเบียน
        Enrollment enrollment = enrollmentRepository
                .findByStudentStudentIdAndCourseCourseId(studentId, courseId)
                .orElse(null);

        if (enrollment == null) {
            return new ApiResponse("ไม่พบข้อมูลการลงทะเบียนวิชานี้", false);
        }

        if ("DROPPED".equals(enrollment.getEnrollmentStatus())) {
            return new ApiResponse("คุณได้ถอนวิชานี้ไปแล้ว!", false);
        }

        // 2. ปรับสถานะเป็น DROPPED
        enrollment.setEnrollmentStatus("DROPPED");
        enrollmentRepository.save(enrollment);

        // 3. ลดจำนวนนักศึกษาที่ลงทะเบียนในวิชานี้
        Course course = courseRepository.findByIdWithLock(courseId).orElse(null);
        if (course != null && course.getEnrolledStudentCount() != null && course.getEnrolledStudentCount() > 0) {
            course.setEnrolledStudentCount(course.getEnrolledStudentCount() - 1);
            courseRepository.save(course);
        }

        return new ApiResponse("ถอนวิชาสำเร็จ!", true);
    }

    public List<Enrollment> getMySchedule(Long studentId) {
        return enrollmentRepository.findByStudentStudentIdAndEnrollmentStatus(studentId, "REGISTERED");
    }
}
