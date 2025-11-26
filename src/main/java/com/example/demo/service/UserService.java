package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.annotation.PostConstruct;

@Service
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    // 🔹 Registrar nuevo usuario con username + email + password
    public User register(String username, String email, String rawPassword) {
        if (repo.findByUsername(username) != null) {
            throw new RuntimeException("Username already in use");
        }
        if (repo.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already in use");
        }


        String hashed = passwordEncoder.encode(rawPassword);

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(hashed);

        return repo.save(user);
    }

    // 🔹 Login usando email + password
    public User loginByEmail(String email, String rawPassword) {
        User user = repo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println("📥 Raw password recibida: " + rawPassword);
        System.out.println("🔐 Password en BD: " + user.getPassword());
        System.out.println("🔍 BCrypt check: " + passwordEncoder.matches(rawPassword, user.getPassword()));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }
    @PostConstruct
    public void generateTestHash() {
        System.out.println("HASH 456: " + passwordEncoder.encode("456"));
    }

    // 🔹 (Opcional) Login por username para compatibilidad
    public User loginByUsername(String username, String rawPassword) {
        User user = repo.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }

    public User getUser(String username) {
        User user = repo.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return user;
    }
    public User getUserByEmail(String email) {
        return repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

}
