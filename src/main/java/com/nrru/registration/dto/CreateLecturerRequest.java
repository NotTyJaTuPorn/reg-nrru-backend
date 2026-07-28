package com.nrru.registration.dto;

import lombok.Data;

@Data
public class CreateLecturerRequest {
    // User info
    private String loginId;
    private String password;
    private String email;
    private String titleTh;
    private String firstNameTh;
    private String lastNameTh;
    private String firstNameEn;
    private String lastNameEn;
    private String phoneNumber;

    // Lecturer-specific info
    private String lecturerCode;
    private String academicRank;
    private Long departmentId;
}
