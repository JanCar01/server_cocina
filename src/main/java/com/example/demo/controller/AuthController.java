package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.security.JwtService;
import com.example.demo.security.UserDetailsServiceImpl;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    public AuthController(UserService userService, JwtService jwtService, UserDetailsServiceImpl userDetailsService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    public static class RegisterRequest {
        public String username;
        public String email;
        public String password;
    }

    public static class LoginRequest {
        public String email;
        public String password;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            User u = userService.register(req.username, req.email, req.password);

            return ResponseEntity.ok(
                    Map.of(
                            "user", Map.of("id", u.getId(), "username", u.getUsername(), "email", u.getEmail()),
                            "accessToken", jwtService.generateAccessToken(u.getEmail()),
                            "refreshToken", jwtService.generateRefreshToken(u.getEmail())
                    )
            );
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            User u = userService.loginByEmail(req.email, req.password);

            return ResponseEntity.ok(
                    Map.of(
                            "user", Map.of("id", u.getId(), "username", u.getUsername(), "email", u.getEmail()),
                            "accessToken", jwtService.generateAccessToken(u.getEmail()),
                            "refreshToken", jwtService.generateRefreshToken(u.getEmail())
                    )
            );
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> req) {

        String refreshToken = req.get("refreshToken");

        if (!jwtService.isTokenValid(refreshToken)) {
            return ResponseEntity.badRequest().body("Invalid refresh token");
        }

        String email = jwtService.extractEmail(refreshToken);

        return ResponseEntity.ok(
                Map.of("accessToken", jwtService.generateAccessToken(email))
        );
    }
}
