package com.nrru.registration.dto;

import lombok.Data;

@Data
public class CreateCourseRequest {
    private String courseCode;
    private String courseNameTh;
    private String courseNameEn;
    private Integer creditCount;
    private Integer seatCapacity;
    private Long lecturerUserId;   // user_id of the lecturer (nullable)
    private Long facultyId;
    private String semesterCode;
    private Integer academicYear;
    private String courseDescription;
    private Boolean isOpenForRegistration;
}
