package com.nrru.registration.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "login_id", unique = true, nullable = false, length = 100)
    private String loginId;

    @Column(name = "password_hash", nullable = false, length = 250)
    private String passwordHash;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "title_th", length = 20)
    private String titleTh;

    @Column(name = "first_name_th", nullable = false, length = 100)
    private String firstNameTh;

    @Column(name = "last_name_th", nullable = false, length = 100)
    private String lastNameTh;

    @Column(name = "title_en", length = 20)
    private String titleEn;

    @Column(name = "first_name_en", length = 100)
    private String firstNameEn;

    @Column(name = "last_name_en", length = 100)
    private String lastNameEn;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "role_name", nullable = false, length = 20)
    private String roleName;

    @Column(name = "pdpa_consent_flag")
    private Boolean pdpaConsentFlag = false;

    @Column(name = "pdpa_consent_datetime")
    private LocalDateTime pdpaConsentDatetime;

    @Column(name = "is_active_flag")
    private Boolean isActiveFlag = true;

    @Column(name = "last_login_datetime")
    private LocalDateTime lastLoginDatetime;

    @Column(name = "created_datetime")
    private LocalDateTime createdDatetime = LocalDateTime.now();

    @Column(name = "updated_datetime")
    private LocalDateTime updatedDatetime = LocalDateTime.now();
}
