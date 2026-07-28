package com.nrru.registration.dto;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String loginId;
    private String password;
    private String email;
    private String titleTh;
    private String firstNameTh;
    private String lastNameTh;
    private String titleEn;
    private String firstNameEn;
    private String lastNameEn;
    private String phoneNumber;
    private String roleName; // STUDENT, LECTURER, ADMIN
}
