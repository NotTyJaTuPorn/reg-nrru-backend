package com.nrru.registration.service;

import com.nrru.registration.entity.Student;
import com.nrru.registration.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Optional<Student> findByUserId(Long userId) {
        return studentRepository.findByUser_UserId(userId);
    }

    public Optional<Student> findByStudentCode(String studentCode) {
        return studentRepository.findByStudentCode(studentCode);
    }

    public boolean existsByStudentCode(String studentCode) {
        return studentRepository.existsByStudentCode(studentCode);
    }
}
