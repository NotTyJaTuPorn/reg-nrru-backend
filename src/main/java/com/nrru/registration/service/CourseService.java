package com.nrru.registration.service;

import com.nrru.registration.entity.Course;
import com.nrru.registration.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> getCoursesBySemester(String semesterCode, String academicYear) {
        return courseRepository.findBySemesterCodeAndAcademicYear(semesterCode, academicYear);
    }

    public Optional<Course> findByIdWithLock(Long courseId) {
        return courseRepository.findByIdWithLock(courseId);
    }

    public Optional<Course> findById(Long courseId) {
        return courseRepository.findById(courseId);
    }

    public int getAvailableSeats(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบวิชานี้"));
        return course.getSeatCapacity() - course.getEnrolledStudentCount();
    }

    public boolean hasAvailableSeats(Long courseId) {
        return getAvailableSeats(courseId) > 0;
    }

    public Course updateEnrolledCount(Long courseId, int delta) {
        Course course = courseRepository.findByIdWithLock(courseId)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบวิชานี้"));

        int newCount = course.getEnrolledStudentCount() + delta;
        if (newCount < 0) {
            throw new IllegalArgumentException("จำนวนนักศึกษาติดลบ");
        }
        if (newCount > course.getSeatCapacity()) {
            throw new IllegalStateException("ที่นั่งเต็ม");
        }

        course.setEnrolledStudentCount(newCount);
        return courseRepository.save(course);
    }
}
