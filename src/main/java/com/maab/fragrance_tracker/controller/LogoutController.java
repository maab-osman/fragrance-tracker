package com.maab.fragrance_tracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Handles logout requests.
 * 
 * Spring Security requires logout to be a POST request for security reasons (CSRF protection).
 * However, users typically click links (GET requests). This controller provides a GET endpoint
 * that shows a logout confirmation form, which then POSTs to Spring Security's /logout endpoint.
 */
@Controller
public class LogoutController {

    /**
     * Displays logout confirmation page.
     * This handles GET /logout requests and shows a simple confirmation before actual logout.
     */
    @GetMapping("/logout")
    public String showLogoutForm() {
        return "redirect:/dashboard";
    }

    /**
     * Actually performs the logout via Spring Security.
     * This will be handled by Spring Security's LogoutFilter.
     */
    @PostMapping("/logout")
    public String logout() {
        // Spring Security's LogoutFilter will handle this
        // This method is here for clarity, but logout happens via filter chain
        return "redirect:/";
    }
}
