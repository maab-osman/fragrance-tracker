package com.maab.fragrance_tracker.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles logout requests.
 * 
 * Spring Security requires logout to be a POST request for security reasons (CSRF protection).
 * However, users typically click links (GET requests). This controller provides a GET endpoint
 * that performs the logout and redirects to login.
 */
@Controller
public class LogoutController {

    /**
     * Handles GET /logout requests.
     * Performs logout via Spring Security and redirects to login page.
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] logout() - Exception: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/login";
    }
}
