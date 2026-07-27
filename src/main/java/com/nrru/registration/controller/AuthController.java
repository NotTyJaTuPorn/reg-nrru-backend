package com.nrru.registration.controller;

import com.nrru.registration.config.JwtUtil;
import com.nrru.registration.entity.User;
import com.nrru.registration.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String loginId = request.get("loginId");
        String password = request.get("password");

        // ค้นหาผู้ใช้จากฐานข้อมูล
        var userOptional = userService.findByLoginId(loginId);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }

        User user = userOptional.get();

        // ตรวจสอบรหัสผ่าน (เข้ารหัสด้วย BCrypt)
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }

        // สร้าง JWT Token
        String token = jwtUtil.generateToken(user.getLoginId(), user.getRoleName(), user.getUserId());

        return ResponseEntity.ok(Map.of(
            "token", token,
            "role", user.getRoleName(),
            "loginId", user.getLoginId(),
            "fullName", user.getFirstNameTh() + " " + user.getLastNameTh()
        ));
    }

    // ✅ Test endpoint (เช็คว่า API ทำงาน)
    @GetMapping("/test")
    public String test() {
        return "✅ Auth API is working!";
    }
}