package com.nrru.registration.dto;

import lombok.Data;

@Data
public class CreateFacultyRequest {
    private String facultyCode;
    private String facultyNameTh;
    private String facultyNameEn;
}
