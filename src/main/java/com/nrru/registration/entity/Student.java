package com.nrru.registration.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private int studentId;

    @OneToOne
    @JoinColumn(name = "user_id",  unique = true, nullable = false)
    private User user;

    @Column(name = "student_code", unique = true, nullable = false, length = 20)
    private String studentCode;

    @Column(name = "current_year", nullable = false)
    private int currentYear;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne
    @JoinColumn(name = "advisor_user_id")
    private User userAdvisor;

    @Column(name = "cumulative_gpa", precision = 3, scale = 2)
    private BigDecimal cumulativeGpa = BigDecimal.ZERO;

    @Column(name = "accumulated_credits")
    private Integer accumulatedCredits = 0;

    @Column(name = "tuition_paid")
    private Boolean tuitionPaid = false;

    @Column(name = "created_datetime")
    private LocalDateTime createdDatetime = LocalDateTime.now();

    @Column(name = "updated_datetime")
    private LocalDateTime updatedDatetime = LocalDateTime.now();
}
