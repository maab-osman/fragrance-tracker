package com.maab.fragrance_tracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.maab.fragrance_tracker.model.User;
import com.maab.fragrance_tracker.repository.UserRepository;

import java.util.Optional;

/**
 * Service layer for user management operations.
 * 
 * Handles user registration, authentication, and profile management with
 * password encoding and validation. Supports both traditional and OAuth2 authentication.
 * 
 * @author Maab Osman
 * @version 1.0
 */
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Registers a new user with the provided credentials.
     * 
     * Validates that username and email are unique, encodes the password
     * before storage, and saves the user to the database.
     * 
     * @param user the user to register with plaintext password
     * @return the saved user entity with encoded password
     * @throws RuntimeException if username or email already exists
     */
    public User registerUser(User user) {
        // Check if username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }
    
    /**
     * Finds a user by email or username.
     * 
     * Used primarily for OAuth2 authentication to check if a user already
     * exists before creating a new account.
     * 
     * @param emailOrUsername the email or username to search for
     * @return the user if found, null otherwise
     */
    public User findByEmailOrUsername(String emailOrUsername) {
        Optional<User> userByEmail = userRepository.findByEmail(emailOrUsername);
        if (userByEmail.isPresent()) {
            return userByEmail.get();
        }
        Optional<User> userByUsername = userRepository.findByUsername(emailOrUsername);
        return userByUsername.orElse(null);
    }
}