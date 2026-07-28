package com.nrru.registration.service;

import com.nrru.registration.dto.*;
import com.nrru.registration.entity.*;
import com.nrru.registration.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final LecturerRepository lecturerRepository;
    private final CourseRepository courseRepository;
    private final FacultyRepository facultyRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(UserRepository userRepository,
                        StudentRepository studentRepository,
                        LecturerRepository lecturerRepository,
                        CourseRepository courseRepository,
                        FacultyRepository facultyRepository,
                        DepartmentRepository departmentRepository,
                        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.lecturerRepository = lecturerRepository;
        this.courseRepository = courseRepository;
        this.facultyRepository = facultyRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ===================== STATS =====================

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalStudents", studentRepository.count());
        stats.put("totalLecturers", lecturerRepository.count());
        stats.put("totalCourses", courseRepository.count());
        stats.put("totalFaculties", facultyRepository.count());
        stats.put("totalDepartments", departmentRepository.count());
        return stats;
    }

    // ===================== STUDENTS =====================

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Transactional
    public Student createStudent(CreateStudentRequest req) {
        if (userRepository.existsByLoginId(req.getLoginId())) {
            throw new IllegalArgumentException("Login ID นี้ถูกใช้งานแล้ว: " + req.getLoginId());
        }
        if (studentRepository.existsByStudentCode(req.getStudentCode())) {
            throw new IllegalArgumentException("รหัสนักศึกษานี้ถูกใช้งานแล้ว: " + req.getStudentCode());
        }

        Department dept = departmentRepository.findById(req.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบภาควิชา ID: " + req.getDepartmentId()));

        // 1. Create User record
        User user = new User();
        user.setLoginId(req.getLoginId());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setEmail(req.getEmail());
        user.setTitleTh(req.getTitleTh());
        user.setFirstNameTh(req.getFirstNameTh());
        user.setLastNameTh(req.getLastNameTh());
        user.setFirstNameEn(req.getFirstNameEn());
        user.setLastNameEn(req.getLastNameEn());
        user.setPhoneNumber(req.getPhoneNumber());
        user.setRoleName("STUDENT");
        user.setPdpaConsentFlag(true);
        user.setIsActiveFlag(true);
        user.setCreatedDatetime(LocalDateTime.now());
        user.setUpdatedDatetime(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        // 2. Create Student record
        Student student = new Student();
        student.setUser(savedUser);
        student.setStudentCode(req.getStudentCode());
        student.setCurrentYear(req.getCurrentYear());
        student.setDepartment(dept);
        student.setCreatedDatetime(LocalDateTime.now());
        student.setUpdatedDatetime(LocalDateTime.now());
        return studentRepository.save(student);
    }

    @Transactional
    public void deleteStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบนักศึกษา ID: " + studentId));
        Long userId = student.getUser().getUserId();
        studentRepository.deleteById(studentId);
        userRepository.deleteById(userId);
    }

    // ===================== LECTURERS =====================

    public List<Lecturer> getAllLecturers() {
        return lecturerRepository.findAll();
    }

    @Transactional
    public Lecturer createLecturer(CreateLecturerRequest req) {
        if (userRepository.existsByLoginId(req.getLoginId())) {
            throw new IllegalArgumentException("Login ID นี้ถูกใช้งานแล้ว: " + req.getLoginId());
        }
        if (lecturerRepository.existsByLecturerCode(req.getLecturerCode())) {
            throw new IllegalArgumentException("รหัสอาจารย์นี้ถูกใช้งานแล้ว: " + req.getLecturerCode());
        }

        Department dept = departmentRepository.findById(req.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบภาควิชา ID: " + req.getDepartmentId()));

        // 1. Create User record
        User user = new User();
        user.setLoginId(req.getLoginId());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setEmail(req.getEmail());
        user.setTitleTh(req.getTitleTh());
        user.setFirstNameTh(req.getFirstNameTh());
        user.setLastNameTh(req.getLastNameTh());
        user.setFirstNameEn(req.getFirstNameEn());
        user.setLastNameEn(req.getLastNameEn());
        user.setPhoneNumber(req.getPhoneNumber());
        user.setRoleName("LECTURER");
        user.setPdpaConsentFlag(true);
        user.setIsActiveFlag(true);
        user.setCreatedDatetime(LocalDateTime.now());
        user.setUpdatedDatetime(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        // 2. Create Lecturer record
        Lecturer lecturer = new Lecturer();
        lecturer.setUserId(savedUser.getUserId());
        lecturer.setLecturerCode(req.getLecturerCode());
        lecturer.setAcademicRank(req.getAcademicRank());
        lecturer.setDepartment(dept);
        lecturer.setCreatedDatetime(LocalDateTime.now());
        lecturer.setUpdatedDatetime(LocalDateTime.now());
        return lecturerRepository.save(lecturer);
    }

    @Transactional
    public void deleteLecturer(Long lecturerId) {
        Lecturer lecturer = lecturerRepository.findById(lecturerId)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบอาจารย์ ID: " + lecturerId));
        long userId = lecturer.getUserId();
        lecturerRepository.deleteById(lecturerId);
        userRepository.deleteById(userId);
    }

    // ===================== COURSES =====================

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Transactional
    public Course createCourse(CreateCourseRequest req) {
        Faculty faculty = facultyRepository.findById(req.getFacultyId())
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบคณะ ID: " + req.getFacultyId()));

        User lecturer = null;
        if (req.getLecturerUserId() != null) {
            lecturer = userRepository.findById(req.getLecturerUserId())
                    .orElseThrow(() -> new IllegalArgumentException("ไม่พบอาจารย์ userId: " + req.getLecturerUserId()));
        }

        Course course = new Course();
        course.setCourseCode(req.getCourseCode());
        course.setCourseNameTh(req.getCourseNameTh());
        course.setCourseNameEn(req.getCourseNameEn());
        course.setCreditCount(req.getCreditCount());
        course.setSeatCapacity(req.getSeatCapacity());
        course.setEnrolledStudentCount(0);
        course.setLecturer(lecturer);
        course.setFaculty(faculty);
        course.setSemesterCode(req.getSemesterCode());
        course.setAcademicYear(req.getAcademicYear());
        course.setCourseDescription(req.getCourseDescription());
        course.setIsOpenForRegistration(req.getIsOpenForRegistration() != null ? req.getIsOpenForRegistration() : true);
        course.setCreatedDatetime(LocalDateTime.now());
        course.setUpdatedDatetime(LocalDateTime.now());
        return courseRepository.save(course);
    }

    @Transactional
    public Course updateCourse(Long courseId, CreateCourseRequest req) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบรายวิชา ID: " + courseId));

        Faculty faculty = facultyRepository.findById(req.getFacultyId())
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบคณะ ID: " + req.getFacultyId()));

        User lecturer = null;
        if (req.getLecturerUserId() != null) {
            lecturer = userRepository.findById(req.getLecturerUserId())
                    .orElseThrow(() -> new IllegalArgumentException("ไม่พบอาจารย์ userId: " + req.getLecturerUserId()));
        }

        course.setCourseCode(req.getCourseCode());
        course.setCourseNameTh(req.getCourseNameTh());
        course.setCourseNameEn(req.getCourseNameEn());
        course.setCreditCount(req.getCreditCount());
        course.setSeatCapacity(req.getSeatCapacity());
        course.setLecturer(lecturer);
        course.setFaculty(faculty);
        course.setSemesterCode(req.getSemesterCode());
        course.setAcademicYear(req.getAcademicYear());
        course.setCourseDescription(req.getCourseDescription());
        if (req.getIsOpenForRegistration() != null) {
            course.setIsOpenForRegistration(req.getIsOpenForRegistration());
        }
        course.setUpdatedDatetime(LocalDateTime.now());
        return courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(Long courseId) {
        courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบรายวิชา ID: " + courseId));
        courseRepository.deleteById(courseId);
    }

    // ===================== FACULTIES =====================

    public List<Faculty> getAllFaculties() {
        return facultyRepository.findAll();
    }

    @Transactional
    public Faculty createFaculty(CreateFacultyRequest req) {
        if (facultyRepository.existsByFacultyCode(req.getFacultyCode())) {
            throw new IllegalArgumentException("รหัสคณะนี้ถูกใช้งานแล้ว: " + req.getFacultyCode());
        }
        Faculty faculty = new Faculty();
        faculty.setFacultyCode(req.getFacultyCode());
        faculty.setFacultyNameTh(req.getFacultyNameTh());
        faculty.setFacultyNameEn(req.getFacultyNameEn());
        return facultyRepository.save(faculty);
    }

    @Transactional
    public void deleteFaculty(Long facultyId) {
        facultyRepository.findById(facultyId)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบคณะ ID: " + facultyId));
        facultyRepository.deleteById(facultyId);
    }

    // ===================== DEPARTMENTS =====================

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Transactional
    public Department createDepartment(CreateDepartmentRequest req) {
        if (departmentRepository.existsByDepartmentCode(req.getDepartmentCode())) {
            throw new IllegalArgumentException("รหัสภาควิชานี้ถูกใช้งานแล้ว: " + req.getDepartmentCode());
        }
        Faculty faculty = facultyRepository.findById(req.getFacultyId())
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบคณะ ID: " + req.getFacultyId()));

        Department dept = new Department();
        dept.setFaculty(faculty);
        dept.setDepartmentCode(req.getDepartmentCode());
        dept.setDepartmentNameTh(req.getDepartmentNameTh());
        dept.setDepartmentNameEn(req.getDepartmentNameEn());
        return departmentRepository.save(dept);
    }

    @Transactional
    public void deleteDepartment(Long departmentId) {
        departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบภาควิชา ID: " + departmentId));
        departmentRepository.deleteById(departmentId);
    }

    // ===================== USERS (generic) =====================

    public List<User> getUsersByRole(String role) {
        if (role != null && !role.isBlank()) {
            return userRepository.findAll().stream()
                    .filter(u -> role.equalsIgnoreCase(u.getRoleName()))
                    .toList();
        }
        return userRepository.findAll();
    }
}
