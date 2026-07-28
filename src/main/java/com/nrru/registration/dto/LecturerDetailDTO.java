package com.nrru.registration.dto;

import com.nrru.registration.entity.Department;
import lombok.Data;

@Data
public class LecturerDetailDTO {
    private long lecturerId;
    private long userId;
    private String lecturerCode;
    private String academicRank;
    private Department department;

    // User details
    private String loginId;
    private String email;
    private String titleTh;
    private String firstNameTh;
    private String lastNameTh;
    private String titleEn;
    private String firstNameEn;
    private String lastNameEn;
    private String phoneNumber;
}
