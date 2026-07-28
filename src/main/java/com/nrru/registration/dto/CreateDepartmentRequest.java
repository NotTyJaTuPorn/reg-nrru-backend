package com.nrru.registration.dto;

import lombok.Data;

@Data
public class CreateDepartmentRequest {
    private Long facultyId;
    private String departmentCode;
    private String departmentNameTh;
    private String departmentNameEn;
}
