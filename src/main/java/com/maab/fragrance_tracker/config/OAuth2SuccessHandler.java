package com.maab.fragrance_tracker.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.maab.fragrance_tracker.model.User;
import com.maab.fragrance_tracker.service.UserService;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles successful OAuth2 authentication by creating or retrieving users.
 * 
 * When a user successfully authenticates via OAuth2 (Google, GitHub, etc.),
 * this handler extracts their profile information and creates a local User
 * account if one doesn't already exist.
 * 
 * @author Maab Osman
 * @version 1.0
 */
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(OAuth2SuccessHandler.class);
    
    @Autowired
    private UserService userService;
    
    /**
     * Handles successful OAuth2 authentication.
     * 
     * Extracts user information from OAuth2 provider and either creates a new
     * user account or logs in existing user. Redirects to dashboard after success.
     * 
     * @param request the HTTP request
     * @param response the HTTP response
     * @param authentication the authentication token containing OAuth2 user info
     * @throws IOException if I/O error occurs
     * @throws ServletException if servlet error occurs
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauth2User = authToken.getPrincipal();
        String provider = authToken.getAuthorizedClientRegistrationId();
        
        logger.info("OAuth2 login successful for provider: {}", provider);
        
        // Extract user information from OAuth2 provider
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        
        // For GitHub, use login attribute instead of name
        if (email == null && "github".equals(provider)) {
            String login = oauth2User.getAttribute("login");
            email = login != null ? login + "@github.com" : null;
        }
        
        // Create or retrieve user
        try {
            if (email != null) {
                User user = userService.findByEmailOrUsername(email);
                if (user == null) {
                    // Create new user from OAuth2 profile
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUsername(name != null ? name.replace(" ", "").toLowerCase() : email.split("@")[0]);
                    newUser.setPassword("oauth2-" + provider); // Non-functional password for OAuth2 users
                    userService.registerUser(newUser);
                    logger.info("Created new user from OAuth2 provider: {}", provider);
                }
            }
        } catch (Exception e) {
            logger.error("Error creating/retrieving user for OAuth2 provider: {}", provider, e);
        }
        
        // Redirect to dashboard after successful authentication
        response.sendRedirect("/dashboard");
    }
}
