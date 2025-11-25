package com.maab.fragrance_tracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

import com.maab.fragrance_tracker.model.User;
import com.maab.fragrance_tracker.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles user authentication flows including registration and login.
 * 
 * Provides endpoints for user registration, login, and dashboard access with
 * full server-side validation and error handling for security and data integrity.
 * 
 * @author Maab Osman
 * @version 1.0
 */
@Controller
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/")
    public String home() {
        return "home";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        // Server-side validation (JSR-380)
        if (result.hasErrors()) {
            logger.warn("Validation failed for user registration: {}", result.getAllErrors());
            return "register";
        }
        
        try {
            userService.registerUser(user);
            return "redirect:/login?registered";
        } catch (RuntimeException e) {
            // Log full stacktrace to help diagnose the root cause
            logger.error("Error registering user (username={})", user.getUsername(), e);
            // Surface the exception class as well as its message so UI shows more context
            String err = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage() + " (" + e.getClass().getSimpleName() + ")";
            model.addAttribute("error", err);
            model.addAttribute("user", user);
            return "register";
        }
    }

    // JSON endpoint for SPA/frontend registration
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUserJson(@RequestBody User user) {
        try {
            userService.registerUser(user);
            return ResponseEntity.ok().body(java.util.Map.of("message", "registered"));
        } catch (RuntimeException e) {
            logger.error("Error registering user (json) username={}", user.getUsername(), e);
            String err = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage() + " (" + e.getClass().getSimpleName() + ")";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(java.util.Map.of("message", err));
        }
    }
    
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}