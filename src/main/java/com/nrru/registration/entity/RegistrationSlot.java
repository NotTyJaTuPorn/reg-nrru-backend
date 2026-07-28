package com.nrru.registration.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "registration_slots")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "slot_id")
    private long slotId;

    @Column(name = "semester_code", nullable = false, length = 10)
    private String semesterCode;

    @Column(name = "academic_year", nullable = false)
    private int academicYear;

    @Column(name = "target_year")
    private Integer targetYear;

    @Column(name = "slot_start_datetime", nullable = false)
    private LocalDateTime slotStartDatetime;

    @Column(name = "slot_end_datetime", nullable = false)
    private LocalDateTime slotEndDatetime;

    @Column(name = "is_active_flag")
    private Boolean isActiveFlag = true;

    @Column(name = "created_datetime")
    private LocalDateTime createdDatetime = LocalDateTime.now();
}
