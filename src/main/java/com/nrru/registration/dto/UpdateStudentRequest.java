package com.nrru.registration.dto;

import lombok.Data;

@Data
public class UpdateStudentRequest {
    private String password; // Optional: only update if provided
    private String email;
    private String titleTh;
    private String firstNameTh;
    private String lastNameTh;
    private String firstNameEn;
    private String lastNameEn;
    private String phoneNumber;

    private int currentYear;
    private Long departmentId;
    private Boolean tuitionPaid;
}
