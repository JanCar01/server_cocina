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
        private String username;
        private String email;
        private String password;
    
        public String getUsername() {
            return username;
        }
        public void setUsername(String username) {
            this.username = username;
        }
    
        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }
    
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class LoginRequest {
        private String email;
        private String password;
    
        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }
    
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        System.out.println("📥 REGISTER req.username = " + req.getUsername());
        System.out.println("📥 REGISTER req.email    = " + req.getEmail());
        System.out.println("📥 REGISTER req.password = " + req.getPassword());
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
        System.out.println("📥 LOGIN req.email    = " + req.getEmail());
        System.out.println("📥 LOGIN req.password = " + req.getPassword());
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
