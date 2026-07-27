package com.nrru.registration.repository;

import com.nrru.registration.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudentStudentId(Long studentId);

    Optional<Enrollment> findByStudentStudentIdAndCourseCourseId(Long studentId, Long courseId);

    boolean existsByStudentStudentIdAndCourseCourseId(Long studentId, Long courseId);

    long countByCourseCourseIdAndEnrollmentStatus(Long courseId, String status);

    List<Enrollment> findByStudentStudentIdAndEnrollmentStatus(Long studentId, String status);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.courseId = :courseId AND e.enrollmentStatus = 'REGISTERED'")
    long countRegisteredStudentsByCourseId(@Param("courseId") Long courseId);

    boolean existsByStudentStudentIdAndCourseCourseIdAndEnrollmentStatus(Long studentId, Long prereqId, String status);
}