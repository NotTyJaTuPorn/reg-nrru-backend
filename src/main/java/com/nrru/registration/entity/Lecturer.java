package com.nrru.registration.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "lecturers")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Lecturer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecturer_id")
    private long lecturerId;

    @Column(name = "user_id", unique = true, nullable = false)
    private long userId;

    @Column(name = "lecturer_code", unique = true, nullable = false, length = 20)
    private String lecturerCode;

    @Column(name = "academic_rank")
    private String academicRank;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "created_datetime")
    private LocalDateTime createdDatetime = LocalDateTime.now();

    @Column(name = "updated_datetime")
    private LocalDateTime updatedDatetime = LocalDateTime.now();
}
