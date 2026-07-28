package com.nrru.registration.service;

import com.nrru.registration.entity.Course;
import com.nrru.registration.entity.Enrollment;
import com.nrru.registration.entity.Student;
import com.nrru.registration.repository.CourseRepository;
import com.nrru.registration.repository.EnrollmentRepository;
import com.nrru.registration.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class LecturerService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public LecturerService(StudentRepository studentRepository, CourseRepository courseRepository, EnrollmentRepository enrollmentRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public List<Student> getAdvisees(Long lecturerUserId) {
        return studentRepository.findByUserAdvisor_UserId(lecturerUserId);
    }

    public List<Course> getTaughtCourses(Long lecturerUserId) {
        return courseRepository.findByLecturer_UserId(lecturerUserId);
    }

    public List<Enrollment> getEnrolledStudentsForCourse(Long courseId, Long lecturerUserId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        if (course.getLecturer() == null || !course.getLecturer().getUserId().equals(lecturerUserId)) {
            throw new RuntimeException("Unauthorized to view students for this course");
        }

        return enrollmentRepository.findByCourseCourseId(courseId);
    }

    public Enrollment updateStudentGrade(Long courseId, Long studentId, String grade, Long lecturerUserId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        if (course.getLecturer() == null || !course.getLecturer().getUserId().equals(lecturerUserId)) {
            throw new RuntimeException("Unauthorized to update grades for this course");
        }

        Enrollment enrollment = enrollmentRepository.findByStudentStudentIdAndCourseCourseId(studentId, courseId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        enrollment.setFinalGrade(grade);
        return enrollmentRepository.save(enrollment);
    }

    public List<Enrollment> getAdviseeSchedule(Long studentId, Long lecturerUserId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        if (student.getUserAdvisor() == null || !student.getUserAdvisor().getUserId().equals(lecturerUserId)) {
            throw new RuntimeException("Unauthorized: This student is not your advisee");
        }
        return enrollmentRepository.findByStudentStudentId(studentId);
    }
}
