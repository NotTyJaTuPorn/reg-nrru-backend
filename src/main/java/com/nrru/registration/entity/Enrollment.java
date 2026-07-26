package com.nrru.registration.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private Long enrollmentId;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "enrollment_datetime")
    private LocalDateTime enrollmentDatetime;

    @Column(name = "enrollment_status", length = 20)
    private String enrollmentStatus = "REGISTERED";

    @Column(name = "final_grade", length = 2)
    private String finalGrade;

    @Column(name = "created_datetime")
    private LocalDateTime createdDatetime = LocalDateTime.now();

    @Column(name = "updated_datetime")
    private LocalDateTime updatedDatetime = LocalDateTime.now();
}