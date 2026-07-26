package com.nrru.registration.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "course_code", unique = true, nullable = false, length = 20)
    private String courseCode;

    @Column(name = "course_name_th", nullable = false, length = 250)
    private String courseNameTh;

    @Column(name = "course_name_en", nullable = false, length = 250)
    private String courseNameEn;

    @Column(name = "credit_count", nullable = false)
    private Integer creditCount;

    @Column(name = "seat_capacity")
    private Integer seatCapacity;

    @Column(name = "enrolled_student_count")
    private Integer enrolledStudentCount = 0;

    @ManyToOne
    @JoinColumn(name = "lecturer_user_id")
    private User lecturer;

    @ManyToOne
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    @Column(name = "semester_code", nullable = false, length = 10)
    private String semesterCode;

    @Column(name = "academic_year", nullable = false)
    private Integer academicYear;

    @Column(name = "course_description")
    private String courseDescription;

    @Column(name = "is_open_for_registration")
    private Boolean isOpenForRegistration = true;

    @Column(name = "created_datetime")
    private LocalDateTime createdDatetime = LocalDateTime.now();

    @Column(name = "updated_datetime")
    private LocalDateTime updatedDatetime = LocalDateTime.now();
}
