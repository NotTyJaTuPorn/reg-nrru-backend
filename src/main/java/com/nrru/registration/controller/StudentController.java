package com.nrru.registration.controller;

import com.nrru.registration.entity.Student;
import com.nrru.registration.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getMyProfile(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        Student student = studentService.findByUserId(userId).orElse(null);
        if (student == null) {
            return ResponseEntity.status(404).body(Map.of("error", "ไม่พบข้อมูลนักศึกษาสำหรับผู้ใช้งานนี้"));
        }

        return ResponseEntity.ok(student);
    }
}
