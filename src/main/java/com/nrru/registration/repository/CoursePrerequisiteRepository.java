package com.nrru.registration.repository;

import com.nrru.registration.entity.CoursePrerequisite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoursePrerequisiteRepository extends JpaRepository<CoursePrerequisite, Long> {

    @Query("SELECT cp.prerequisiteCourse.courseId FROM CoursePrerequisite cp WHERE cp.course.courseId = :courseId")
    List<Long> findPrerequisiteCourseIds(@Param("courseId") Long courseId);
}
