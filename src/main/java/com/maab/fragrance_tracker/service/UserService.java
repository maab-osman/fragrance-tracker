package com.maab.fragrance_tracker.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.maab.fragrance_tracker.model.User;
import com.maab.fragrance_tracker.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // comes from PasswordConfig (not SecurityConfig)

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Traditional registration with plaintext password (will be encoded). */
    @Transactional
    public User registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) user.setRole("ROLE_USER");
        user.setEnabled(true);
        return userRepository.save(user);
    }

    /** OAuth2 registration path: generate a safe password and unique username. */
    @Transactional
    public User registerOauthUser(String email, String desiredUsername, String provider) {
        // If email exists, just return it (idempotent)
        Optional<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.isPresent()) return byEmail.get();

        String uniqueUsername = ensureUniqueUsername(desiredUsername);

        User u = new User();
        u.setEmail(email);
        u.setUsername(uniqueUsername);
        // Random non-usable password; still encoded to satisfy schema/security
        u.setPassword(passwordEncoder.encode("oauth2:" + provider + ":" + UUID.randomUUID()));
        u.setEnabled(true);
        u.setRole("ROLE_USER");
        return userRepository.save(u);
    }

    public User findByEmailOrUsername(String emailOrUsername) {
        return userRepository.findByEmail(emailOrUsername)
                .or(() -> userRepository.findByUsername(emailOrUsername))
                .orElse(null);
    }

    private String ensureUniqueUsername(String base) {
        String candidate = (base == null || base.isBlank()) ? "user" : base;
        candidate = candidate.toLowerCase().replaceAll("[^a-z0-9_\\-]", "");
        if (candidate.isBlank()) candidate = "user";

        String current = candidate;
        int i = 1;
        while (userRepository.existsByUsername(current)) {
            current = candidate + i;
            i++;
            if (current.length() > 50) { // stay within your column length
                current = current.substring(0, 50);
            }
        }
        return current;
    }
}
