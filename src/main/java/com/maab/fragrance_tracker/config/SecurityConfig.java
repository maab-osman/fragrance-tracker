package com.maab.fragrance_tracker.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration for authentication and authorization.
 * 
 * Configures security rules including:
 * - Public endpoints (/register, /login, static resources)
 * - Authenticated endpoints (dashboard, collection)
 * - OAuth2 social login (Google, GitHub)
 * - Form-based login with Spring Security
 * 
 * @author Maab Osman
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private OAuth2SuccessHandler oauth2SuccessHandler;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/register", "/css/**", "/js/**", "/img/**", "/fonts/**", "/h2-console/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .successHandler(oauth2SuccessHandler)  // Use custom success handler
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            );

        // For H2 console (remove in production)
        http.csrf(csrf -> csrf.disable());
        http.headers(headers -> headers.disable());

        return http.build();
    }
    

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
