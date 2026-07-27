package com.nrru.registration.repository;

import com.nrru.registration.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByCourse_CourseId(Long courseId);

    @Query("SELECT s FROM Schedule s " +
            "JOIN Enrollment e ON e.course.courseId = s.course.courseId " +
            "WHERE e.student.studentId = :studentId " +
            "AND e.enrollmentStatus = 'REGISTERED' " +
            "AND s.weekdayCode = :weekday " +
            "AND ((s.startTime < :endTime AND s.endTime > :startTime))")
    List<Schedule> findConflictingSchedules(@Param("studentId") Long studentId,
                                            @Param("weekday") String weekday,
                                            @Param("startTime") LocalTime startTime,
                                            @Param("endTime") LocalTime endTime);
}