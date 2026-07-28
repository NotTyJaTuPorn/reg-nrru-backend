package com.nrru.registration.controller;

import com.nrru.registration.entity.Course;
import com.nrru.registration.entity.Enrollment;
import com.nrru.registration.entity.Student;
import com.nrru.registration.service.LecturerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lecturers")
public class LecturerController {

    private final LecturerService lecturerService;

    public LecturerController(LecturerService lecturerService) {
        this.lecturerService = lecturerService;
    }

    private Long getUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    @GetMapping("/advisees")
    public ResponseEntity<?> getMyAdvisees(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        List<Student> advisees = lecturerService.getAdvisees(userId);
        return ResponseEntity.ok(advisees);
    }

    @GetMapping("/courses")
    public ResponseEntity<?> getMyCourses(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        List<Course> courses = lecturerService.getTaughtCourses(userId);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/courses/{courseId}/students")
    public ResponseEntity<?> getCourseStudents(@PathVariable Long courseId, HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        try {
            List<Enrollment> enrollments = lecturerService.getEnrolledStudentsForCourse(courseId, userId);
            return ResponseEntity.ok(enrollments);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/courses/{courseId}/students/{studentId}/grade")
    public ResponseEntity<?> updateStudentGrade(
            @PathVariable Long courseId,
            @PathVariable Long studentId,
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        String grade = body.get("grade");
        try {
            Enrollment enrollment = lecturerService.updateStudentGrade(courseId, studentId, grade, userId);
            return ResponseEntity.ok(enrollment);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/advisees/{studentId}/schedule")
    public ResponseEntity<?> getAdviseeSchedule(@PathVariable Long studentId, HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        try {
            List<Enrollment> schedule = lecturerService.getAdviseeSchedule(studentId, userId);
            return ResponseEntity.ok(schedule);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
