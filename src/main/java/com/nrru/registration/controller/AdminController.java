package com.nrru.registration.controller;

import com.nrru.registration.dto.*;
import com.nrru.registration.entity.*;
import com.nrru.registration.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ===================== STATS =====================

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    // ===================== STUDENTS =====================

    @GetMapping("/students")
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(adminService.getAllStudents());
    }

    @PostMapping("/students")
    public ResponseEntity<?> createStudent(@RequestBody CreateStudentRequest req) {
        try {
            Student student = adminService.createStudent(req);
            return ResponseEntity.ok(Map.of("message", "เพิ่มนักศึกษาสำเร็จ", "studentId", student.getStudentId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/students/{studentId}")
    public ResponseEntity<?> updateStudent(@PathVariable Long studentId, @RequestBody UpdateStudentRequest req) {
        try {
            Student student = adminService.updateStudent(studentId, req);
            return ResponseEntity.ok(Map.of("message", "แก้ไขข้อมูลนักศึกษาสำเร็จ", "studentId", student.getStudentId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/students/{studentId}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long studentId) {
        try {
            adminService.deleteStudent(studentId);
            return ResponseEntity.ok(Map.of("message", "ลบข้อมูลนักศึกษาสำเร็จ"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ===================== LECTURERS =====================

    @GetMapping("/lecturers")
    public ResponseEntity<List<LecturerDetailDTO>> getAllLecturers() {
        return ResponseEntity.ok(adminService.getAllLecturers());
    }

    @PostMapping("/lecturers")
    public ResponseEntity<?> createLecturer(@RequestBody CreateLecturerRequest req) {
        try {
            Lecturer lecturer = adminService.createLecturer(req);
            return ResponseEntity.ok(Map.of("message", "เพิ่มอาจารย์สำเร็จ", "lecturerId", lecturer.getLecturerId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/lecturers/{lecturerId}")
    public ResponseEntity<?> updateLecturer(@PathVariable Long lecturerId, @RequestBody UpdateLecturerRequest req) {
        try {
            LecturerDetailDTO lecturer = adminService.updateLecturer(lecturerId, req);
            return ResponseEntity.ok(Map.of("message", "แก้ไขข้อมูลอาจารย์สำเร็จ", "lecturerId", lecturer.getLecturerId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/lecturers/{lecturerId}")
    public ResponseEntity<?> deleteLecturer(@PathVariable Long lecturerId) {
        try {
            adminService.deleteLecturer(lecturerId);
            return ResponseEntity.ok(Map.of("message", "ลบข้อมูลอาจารย์สำเร็จ"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ===================== COURSES =====================

    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(adminService.getAllCourses());
    }

    @PostMapping("/courses")
    public ResponseEntity<?> createCourse(@RequestBody CreateCourseRequest req) {
        try {
            Course course = adminService.createCourse(req);
            return ResponseEntity.ok(Map.of("message", "เพิ่มรายวิชาสำเร็จ", "courseId", course.getCourseId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/courses/{courseId}")
    public ResponseEntity<?> updateCourse(@PathVariable Long courseId, @RequestBody CreateCourseRequest req) {
        try {
            Course course = adminService.updateCourse(courseId, req);
            return ResponseEntity.ok(Map.of("message", "แก้ไขรายวิชาสำเร็จ", "courseId", course.getCourseId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/courses/{courseId}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long courseId) {
        try {
            adminService.deleteCourse(courseId);
            return ResponseEntity.ok(Map.of("message", "ลบรายวิชาสำเร็จ"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ===================== FACULTIES =====================

    @GetMapping("/faculties")
    public ResponseEntity<List<Faculty>> getAllFaculties() {
        return ResponseEntity.ok(adminService.getAllFaculties());
    }

    @PostMapping("/faculties")
    public ResponseEntity<?> createFaculty(@RequestBody CreateFacultyRequest req) {
        try {
            Faculty faculty = adminService.createFaculty(req);
            return ResponseEntity.ok(Map.of("message", "เพิ่มคณะสำเร็จ", "facultyId", faculty.getFacultyId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/faculties/{facultyId}")
    public ResponseEntity<?> deleteFaculty(@PathVariable Long facultyId) {
        try {
            adminService.deleteFaculty(facultyId);
            return ResponseEntity.ok(Map.of("message", "ลบคณะสำเร็จ"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ===================== DEPARTMENTS =====================

    @GetMapping("/departments")
    public ResponseEntity<List<Department>> getAllDepartments() {
        return ResponseEntity.ok(adminService.getAllDepartments());
    }

    @PostMapping("/departments")
    public ResponseEntity<?> createDepartment(@RequestBody CreateDepartmentRequest req) {
        try {
            Department dept = adminService.createDepartment(req);
            return ResponseEntity.ok(Map.of("message", "เพิ่มภาควิชาสำเร็จ", "departmentId", dept.getDepartmentId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/departments/{departmentId}")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long departmentId) {
        try {
            adminService.deleteDepartment(departmentId);
            return ResponseEntity.ok(Map.of("message", "ลบภาควิชาสำเร็จ"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ===================== USERS =====================

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers(@RequestParam(required = false) String role) {
        return ResponseEntity.ok(adminService.getUsersByRole(role));
    }

    // ===================== REGISTRATION SLOTS =====================

    @GetMapping("/registration-slots")
    public ResponseEntity<List<RegistrationSlot>> getAllRegistrationSlots() {
        return ResponseEntity.ok(adminService.getAllRegistrationSlots());
    }

    @PostMapping("/registration-slots")
    public ResponseEntity<?> createRegistrationSlot(@RequestBody RegistrationSlot slot) {
        try {
            RegistrationSlot created = adminService.createRegistrationSlot(slot);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/registration-slots/{id}")
    public ResponseEntity<?> updateRegistrationSlot(@PathVariable Long id, @RequestBody RegistrationSlot slot) {
        try {
            RegistrationSlot updated = adminService.updateRegistrationSlot(id, slot);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/registration-slots/{id}")
    public ResponseEntity<?> deleteRegistrationSlot(@PathVariable Long id) {
        try {
            adminService.deleteRegistrationSlot(id);
            return ResponseEntity.ok(Map.of("message", "ลบช่วงเวลาลงทะเบียนสำเร็จ"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
