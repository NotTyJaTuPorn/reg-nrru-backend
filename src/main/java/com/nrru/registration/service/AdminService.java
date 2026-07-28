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
    private final RegistrationSlotRepository registrationSlotRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(UserRepository userRepository,
                        StudentRepository studentRepository,
                        LecturerRepository lecturerRepository,
                        CourseRepository courseRepository,
                        FacultyRepository facultyRepository,
                        DepartmentRepository departmentRepository,
                        RegistrationSlotRepository registrationSlotRepository,
                        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.lecturerRepository = lecturerRepository;
        this.courseRepository = courseRepository;
        this.facultyRepository = facultyRepository;
        this.departmentRepository = departmentRepository;
        this.registrationSlotRepository = registrationSlotRepository;
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
        student.setTuitionPaid(req.getTuitionPaid() != null ? req.getTuitionPaid() : false);
        student.setCreatedDatetime(LocalDateTime.now());
        student.setUpdatedDatetime(LocalDateTime.now());
        return studentRepository.save(student);
    }

    @Transactional
    public Student updateStudent(Long studentId, UpdateStudentRequest req) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบนักศึกษา ID: " + studentId));

        Department dept = departmentRepository.findById(req.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบภาควิชา ID: " + req.getDepartmentId()));

        // Update User
        User user = student.getUser();
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        }
        user.setEmail(req.getEmail());
        user.setTitleTh(req.getTitleTh());
        user.setFirstNameTh(req.getFirstNameTh());
        user.setLastNameTh(req.getLastNameTh());
        user.setFirstNameEn(req.getFirstNameEn());
        user.setLastNameEn(req.getLastNameEn());
        user.setPhoneNumber(req.getPhoneNumber());
        user.setUpdatedDatetime(LocalDateTime.now());
        userRepository.save(user);

        // Update Student
        student.setCurrentYear(req.getCurrentYear());
        student.setDepartment(dept);
        if (req.getTuitionPaid() != null) {
            student.setTuitionPaid(req.getTuitionPaid());
        }
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

    public List<LecturerDetailDTO> getAllLecturers() {
        List<Lecturer> lecturers = lecturerRepository.findAll();
        return lecturers.stream().map(lec -> {
            LecturerDetailDTO dto = new LecturerDetailDTO();
            dto.setLecturerId(lec.getLecturerId());
            dto.setUserId(lec.getUserId());
            dto.setLecturerCode(lec.getLecturerCode());
            dto.setAcademicRank(lec.getAcademicRank());
            dto.setDepartment(lec.getDepartment());

            userRepository.findById(lec.getUserId()).ifPresent(user -> {
                dto.setLoginId(user.getLoginId());
                dto.setEmail(user.getEmail());
                dto.setTitleTh(user.getTitleTh());
                dto.setFirstNameTh(user.getFirstNameTh());
                dto.setLastNameTh(user.getLastNameTh());
                dto.setTitleEn(user.getTitleEn());
                dto.setFirstNameEn(user.getFirstNameEn());
                dto.setLastNameEn(user.getLastNameEn());
                dto.setPhoneNumber(user.getPhoneNumber());
            });
            return dto;
        }).toList();
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
    public LecturerDetailDTO updateLecturer(Long lecturerId, UpdateLecturerRequest req) {
        Lecturer lecturer = lecturerRepository.findById(lecturerId)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบอาจารย์ ID: " + lecturerId));

        Department dept = departmentRepository.findById(req.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบภาควิชา ID: " + req.getDepartmentId()));

        // Update user
        User user = userRepository.findById(lecturer.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบผู้ใช้งาน ID: " + lecturer.getUserId()));

        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        }
        user.setEmail(req.getEmail());
        user.setTitleTh(req.getTitleTh());
        user.setFirstNameTh(req.getFirstNameTh());
        user.setLastNameTh(req.getLastNameTh());
        user.setFirstNameEn(req.getFirstNameEn());
        user.setLastNameEn(req.getLastNameEn());
        user.setPhoneNumber(req.getPhoneNumber());
        user.setUpdatedDatetime(LocalDateTime.now());
        userRepository.save(user);

        // Update lecturer
        lecturer.setAcademicRank(req.getAcademicRank());
        lecturer.setDepartment(dept);
        lecturer.setUpdatedDatetime(LocalDateTime.now());
        lecturerRepository.save(lecturer);

        // Map and return LecturerDetailDTO
        LecturerDetailDTO dto = new LecturerDetailDTO();
        dto.setLecturerId(lecturer.getLecturerId());
        dto.setUserId(lecturer.getUserId());
        dto.setLecturerCode(lecturer.getLecturerCode());
        dto.setAcademicRank(lecturer.getAcademicRank());
        dto.setDepartment(lecturer.getDepartment());

        dto.setLoginId(user.getLoginId());
        dto.setEmail(user.getEmail());
        dto.setTitleTh(user.getTitleTh());
        dto.setFirstNameTh(user.getFirstNameTh());
        dto.setLastNameTh(user.getLastNameTh());
        dto.setTitleEn(user.getTitleEn());
        dto.setFirstNameEn(user.getFirstNameEn());
        dto.setLastNameEn(user.getLastNameEn());
        dto.setPhoneNumber(user.getPhoneNumber());

        return dto;
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

    // ===================== REGISTRATION SLOTS =====================

    public List<RegistrationSlot> getAllRegistrationSlots() {
        return registrationSlotRepository.findAll();
    }

    @Transactional
    public RegistrationSlot createRegistrationSlot(RegistrationSlot slot) {
        slot.setCreatedDatetime(LocalDateTime.now());
        return registrationSlotRepository.save(slot);
    }

    @Transactional
    public RegistrationSlot updateRegistrationSlot(Long id, RegistrationSlot req) {
        RegistrationSlot slot = registrationSlotRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบช่วงเวลาลงทะเบียน ID: " + id));
        slot.setSemesterCode(req.getSemesterCode());
        slot.setAcademicYear(req.getAcademicYear());
        slot.setTargetYear(req.getTargetYear());
        slot.setSlotStartDatetime(req.getSlotStartDatetime());
        slot.setSlotEndDatetime(req.getSlotEndDatetime());
        if (req.getIsActiveFlag() != null) {
            slot.setIsActiveFlag(req.getIsActiveFlag());
        }
        return registrationSlotRepository.save(slot);
    }

    @Transactional
    public void deleteRegistrationSlot(Long id) {
        RegistrationSlot slot = registrationSlotRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบช่วงเวลาลงทะเบียน ID: " + id));
        registrationSlotRepository.delete(slot);
    }
}
