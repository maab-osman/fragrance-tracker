package com.maab.fragrance_tracker.config;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.maab.fragrance_tracker.model.User;
import com.maab.fragrance_tracker.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@ConditionalOnClass(name = "org.springframework.security.oauth2.client.registration.ClientRegistrationRepository")
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(OAuth2SuccessHandler.class);

    private final UserService userService;

    public OAuth2SuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauth2 = token.getPrincipal();
        String provider = token.getAuthorizedClientRegistrationId(); // e.g., "google" | "github"

        // Common attributes (providers differ)
        String email = oauth2.getAttribute("email");
        String name  = oauth2.getAttribute("name");
        String login = oauth2.getAttribute("login"); // GitHub username

        // Fallbacks:
        if (email == null && "github".equalsIgnoreCase(provider)) {
            // GitHub can hide email; synthesize a stable pseudo-email for local account mapping
            if (login != null) email = login + "@users.noreply.github.com";
        }
        if (name == null && login != null) {
            name = login;
        }
        if (name == null && email != null) {
            name = email.split("@")[0];
        }

        if (email == null) {
            // As a last resort, create a unique pseudo-email so we can store the user locally
            email = "user-" + UUID.randomUUID() + "@oauth2.local";
        }

        String desiredUsername = toUsername(name != null ? name : email);

        try {
            User existing = userService.findByEmailOrUsername(email);
            if (existing == null) {
                // Create or reuse a unique username; password is random & encoded
                userService.registerOauthUser(email, desiredUsername, provider);
                log.info("Created OAuth2 user via {}: {}", provider, email);
            } else {
                log.info("OAuth2 user exists ({}): {}", provider, email);
            }
        } catch (Exception e) {
            log.error("OAuth2 provisioning failed for provider={} email={}", provider, email, e);
            // You can redirect to an error page if you want:
            response.sendRedirect("/login?error");
            return;
        }

        response.sendRedirect("/dashboard");
    }

    private String toUsername(String raw) {
        if (raw == null) return "user" + UUID.randomUUID().toString().substring(0, 8);
        String cleaned = raw.toLowerCase(Locale.ROOT)
                            .replaceAll("[^a-z0-9_\\-]", "");
        if (cleaned.isBlank()) cleaned = "user" + UUID.randomUUID().toString().substring(0, 8);
        return cleaned.length() > 32 ? cleaned.substring(0, 32) : cleaned;
    }
}
