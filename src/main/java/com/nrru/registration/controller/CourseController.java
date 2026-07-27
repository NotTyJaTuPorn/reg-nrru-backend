package com.nrru.registration.controller;

import com.nrru.registration.entity.Course;
import com.nrru.registration.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<List<Course>> getCourses(
            @RequestParam String semester,
            @RequestParam Integer year) {
        List<Course> courses = courseService.getCoursesBySemester(semester, year);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{courseId}/seats")
    public ResponseEntity<Map<String, Object>> getAvailableSeats(@PathVariable Long courseId) {
        int available = courseService.getAvailableSeats(courseId);
        Map<String, Object> response = new HashMap<>();
        response.put("courseId", courseId);
        response.put("availableSeats", available);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long courseId) {
        Course course = courseService.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบวิชา"));
        return ResponseEntity.ok(course);
    }
}
