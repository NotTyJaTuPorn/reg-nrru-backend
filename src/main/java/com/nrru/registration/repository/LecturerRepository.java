package com.nrru.registration.repository;

import com.nrru.registration.entity.Lecturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LecturerRepository extends JpaRepository<Lecturer, Long> {
    Optional<Lecturer> findByLecturerCode(String lecturerCode);
    Optional<Lecturer> findByUserId(long userId);
    boolean existsByLecturerCode(String lecturerCode);
    List<Lecturer> findByDepartment_DepartmentId(Long departmentId);
}
