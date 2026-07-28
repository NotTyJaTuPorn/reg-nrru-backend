package com.nrru.registration.repository;

import com.nrru.registration.entity.RegistrationSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RegistrationSlotRepository extends JpaRepository<RegistrationSlot, Long> {

    @Query("SELECT r FROM RegistrationSlot r WHERE r.isActiveFlag = true AND :now BETWEEN r.slotStartDatetime AND r.slotEndDatetime")
    List<RegistrationSlot> findActiveSlotsAt(@Param("now") LocalDateTime now);
}
