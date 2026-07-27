package com.nrru.registration.service;

import com.nrru.registration.dto.ApiResponse;
import com.nrru.registration.entity.Course;
import com.nrru.registration.entity.Student;
import com.nrru.registration.entity.Enrollment;
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

        // 6. สร้าง Enrollment record
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentStatus("REGISTERED");
        enrollment.setEnrolledAt(LocalDateTime.now());

        enrollmentRepository.save(enrollment);

        // 7. อัปเดตจำนวนนักศึกษาที่ลงทะเบียน
        courseRepository.save(course);

        return new ApiResponse("ลงทะเบียนสำเร็จ!", true);
    }
}
