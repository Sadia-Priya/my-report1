package com.reportsystem.auth.controller;

import com.reportsystem.auth.model.User;
import com.reportsystem.auth.repository.UserRepository;
import com.reportsystem.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // POST /api/auth/signup   body: { fullName, email, password }
    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@RequestBody Map<String, String> body) {
        String fullName = body.get("fullName");
        String email = body.get("email");
        String password = body.get("password");

        if (fullName == null || fullName.isBlank() || email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "নাম এবং ইমেইল দিন"));
        }
        if (password == null || password.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("message", "পাসওয়ার্ড কমপক্ষে ৬ অক্ষরের হতে হবে"));
        }

        email = email.trim().toLowerCase();

        if (userRepository.findByEmail(email) != null) {
            return ResponseEntity.badRequest().body(Map.of("message", "এই ইমেইল দিয়ে আগেই অ্যাকাউন্ট খোলা হয়েছে"));
        }

        User user = new User();
        user.setFullName(fullName.trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        userRepository.save(user);

        String token = jwtUtil.createToken(email);
        return ResponseEntity.ok(Map.of("token", token, "fullName", user.getFullName()));
    }

    // POST /api/auth/login   body: { email, password }
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "ইমেইল এবং পাসওয়ার্ড দিন"));
        }

        User user = userRepository.findByEmail(email.trim().toLowerCase());

        // user nai, ba password bhul
        if (user == null || !passwordEncoder.matches(password, user.getPasswordHash())) {
            return ResponseEntity.status(401).body(Map.of("message", "ইমেইল অথবা পাসওয়ার্ড ভুল"));
        }

        String token = jwtUtil.createToken(user.getEmail());
        return ResponseEntity.ok(Map.of("token", token, "fullName", user.getFullName()));
    }
}