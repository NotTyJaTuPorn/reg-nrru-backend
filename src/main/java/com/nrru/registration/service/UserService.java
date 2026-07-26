package com.nrru.registration.service;

import com.nrru.registration.entity.User;
import com.nrru.registration.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId);
    }

    public User createUser(String loginId, String password, String email,
                           String firstName, String lastName, String role) {
        User user = new User();
        user.setLoginId(loginId);
        user.setPasswordHash(passwordEncoder.encode(password)); // เข้ารหัสรหัสผ่าน
        user.setEmail(email);
        user.setFirstNameTh(firstName);
        user.setLastNameTh(lastName);
        user.setRoleName(role);
        user.setPdpaConsentFlag(true);
        return userRepository.save(user);
    }

    public boolean existsByLoginId(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }
}
