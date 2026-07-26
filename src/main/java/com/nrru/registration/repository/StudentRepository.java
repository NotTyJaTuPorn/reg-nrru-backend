package com.nrru.registration.repository;

import com.nrru.registration.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentCode(String studentCode);

    Optional<Student> findByUser_UserId(Long userId);

    boolean existsByStudentCode(String studentCode);

    long countByDepartment_DepartmentId(Long departmentId);
}
