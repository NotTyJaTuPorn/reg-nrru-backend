package com.nrru.registration.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;


@Entity
@Table(name = "faculties")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "faculty_id")
    private Long facultyId;

    @Column(name = "faculty_code", unique = true, nullable = false, length = 50)
    private String facultyCode;

    @Column(name = "faculty_name_th", nullable = false, length = 255)
    private String facultyNameTh;

    @Column(name = "faculty_name_en", length = 255)
    private String facultyNameEn;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
