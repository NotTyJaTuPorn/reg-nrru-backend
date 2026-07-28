package com.nrru.registration.repository;

import com.nrru.registration.entity.Course;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository  extends JpaRepository<Course, Long> {
    Optional<Course> findByCourseCode(String courseCode);

    List<Course> findBySemesterCodeAndAcademicYear(String semesterCode, Integer academicYear);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Course c WHERE c.courseId = :courseId")
    Optional<Course> findByIdWithLock(@Param("courseId") Long courseId);

    long countBySemesterCodeAndAcademicYear(String semesterCode, Integer academicYear);

    List<Course> findByLecturer_UserId(Long lecturerUserId);
}
