package com.nrru.registration.controller;

import com.nrru.registration.dto.ApiResponse;
import com.nrru.registration.dto.EnrollRequest;
import com.nrru.registration.entity.Enrollment;
import com.nrru.registration.entity.Student;
import com.nrru.registration.service.EnrollmentService;
import com.nrru.registration.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final StudentService studentService;  // ✅ Inject StudentService

    public EnrollmentController(EnrollmentService enrollmentService, StudentService studentService) {
        this.enrollmentService = enrollmentService;
        this.studentService = studentService;
    }

    // ✅ ลงทะเบียนวิชา
    @PostMapping
    public ResponseEntity<ApiResponse> enroll(@RequestBody EnrollRequest request,
                                              HttpServletRequest httpRequest) {
        // 1. ดึง userId จาก Request Attribute (ที่ Filter ตั้งไว้)
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(new ApiResponse("Unauthorized", false));
        }

        // 2. ค้นหา Student จาก userId
        Student student = studentService.findByUserId(userId)
                .orElse(null);
        if (student == null) {
            return ResponseEntity.status(404).body(new ApiResponse("ไม่พบข้อมูลนักศึกษาสำหรับผู้ใช้งานนี้", false));
        }

        // 3. ลงทะเบียน
        ApiResponse response = enrollmentService.enrollStudent((long) student.getStudentId(), request.getCourseId());
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    // ✅ ถอนวิชา
    @DeleteMapping("/{courseId}")
    public ResponseEntity<ApiResponse> drop(@PathVariable Long courseId,
                                            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(new ApiResponse("Unauthorized", false));
        }

        Student student = studentService.findByUserId(userId)
                .orElse(null);
        if (student == null) {
            return ResponseEntity.status(404).body(new ApiResponse("ไม่พบข้อมูลนักศึกษาสำหรับผู้ใช้งานนี้", false));
        }

        ApiResponse response = enrollmentService.dropCourse((long) student.getStudentId(), courseId);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    // ✅ ดูตารางเรียนของฉัน
    @GetMapping("/me")
    public ResponseEntity<?> getMySchedule(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        Student student = studentService.findByUserId(userId)
                .orElse(null);
        if (student == null) {
            return ResponseEntity.status(404).body(new ApiResponse("ไม่พบข้อมูลนักศึกษาสำหรับผู้ใช้งานนี้", false));
        }

        List<Enrollment> schedule = enrollmentService.getMySchedule((long) student.getStudentId());
        return ResponseEntity.ok(schedule);
    }
}