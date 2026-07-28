package com.nrru.registration.repository;

import com.nrru.registration.entity.RegistrationSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationSlotRepository extends JpaRepository<RegistrationSlot, Long> {
}
