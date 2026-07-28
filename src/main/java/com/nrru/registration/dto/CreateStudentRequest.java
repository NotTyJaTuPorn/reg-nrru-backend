package com.nrru.registration.dto;

import lombok.Data;

@Data
public class CreateStudentRequest {
    // User info
    private String loginId;       // เช่น 6600005
    private String password;
    private String email;
    private String titleTh;
    private String firstNameTh;
    private String lastNameTh;
    private String firstNameEn;
    private String lastNameEn;
    private String phoneNumber;

    // Student-specific info
    private String studentCode;   // เช่น 6600005 (usually same as loginId)
    private int currentYear;
    private Long departmentId;
}
