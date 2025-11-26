package com.maab.fragrance_tracker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.maab.fragrance_tracker.model.User;
import com.maab.fragrance_tracker.repository.UserRepository;
import com.maab.fragrance_tracker.service.PerfumeService;
import com.maab.fragrance_tracker.service.ReviewService;

/**
 * Admin discovery and management controller.
 * Provides admin-specific features for managing the perfume catalog and reviews.
 */
@Controller
public class AdminDiscoverController {

    private final PerfumeService perfumeService;
    private final ReviewService reviewService;
    private final UserRepository userRepository;

    public AdminDiscoverController(PerfumeService perfumeService, ReviewService reviewService, UserRepository userRepository) {
        this.perfumeService = perfumeService;
        this.reviewService = reviewService;
        this.userRepository = userRepository;
    }

    /**
     * Shows admin-specific discovery page with delete functionality.
     */
    @GetMapping("/admin/discover")
    public String adminDiscover() {
        try {
            User currentUser = getCurrentUser();
            System.out.println("[DEBUG] adminDiscover() - Current user: " + currentUser);
            
            // Verify user is admin
            if (currentUser == null) {
                System.out.println("[DEBUG] adminDiscover() - Current user is null, redirecting to login");
                return "redirect:/login";
            }
            
            boolean isAdmin = currentUser.isAdmin();
            System.out.println("[DEBUG] adminDiscover() - User: " + currentUser.getUsername() + ", isAdmin: " + isAdmin);
            
            if (!isAdmin) {
                System.out.println("[DEBUG] adminDiscover() - User is not admin, redirecting to discover");
                return "redirect:/discover";
            }
            
            System.out.println("[DEBUG] adminDiscover() - User is admin, loading template");
            return "admin-discover";
        } catch (Exception e) {
            System.err.println("[ERROR] adminDiscover() - Exception: " + e.getMessage());
            return "redirect:/error";
        }
    }

    /**
     * Deletes a perfume from the catalog.
     * Only admins can delete perfumes.
     * 
     * @param perfumeId the ID of the perfume to delete
     * @return success response or error
     */
    @DeleteMapping("/api/perfumes/{id}")
    @ResponseBody
    public ResponseEntity<?> deletePerfume(@PathVariable("id") Long perfumeId) {
        User currentUser = getCurrentUser();
        
        // Check admin privilege
        if (currentUser == null || !currentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(java.util.Map.of("error", "Admin access required"));
        }

        try {
            perfumeService.deletePerfume(perfumeId);
            return ResponseEntity.ok(java.util.Map.of("message", "Perfume deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(java.util.Map.of("error", "Failed to delete perfume: " + e.getMessage()));
        }
    }

    /**
     * Deletes a review.
     * Only admins can delete reviews.
     * 
     * @param reviewId the ID of the review to delete
     * @return success response or error
     */
    @DeleteMapping("/api/reviews/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteReview(@PathVariable("id") Long reviewId) {
        User currentUser = getCurrentUser();
        
        // Check admin privilege
        if (currentUser == null || !currentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(java.util.Map.of("error", "Admin access required"));
        }

        try {
            reviewService.deleteReview(reviewId);
            return ResponseEntity.ok(java.util.Map.of("message", "Review deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(java.util.Map.of("error", "Failed to delete review: " + e.getMessage()));
        }
    }

    /**
     * Gets the currently authenticated user from Spring Security context.
     */
    private User getCurrentUser() {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null) {
                System.out.println("[DEBUG] getCurrentUser() - Authentication is null");
                return null;
            }
            
            Object principal = authentication.getPrincipal();
            System.out.println("[DEBUG] getCurrentUser() - Principal type: " + (principal != null ? principal.getClass().getSimpleName() : "null"));
            
            if (principal instanceof UserDetails userDetails) {
                String username = userDetails.getUsername();
                System.out.println("[DEBUG] getCurrentUser() - Looking up user: " + username);
                var user = userRepository.findByUsername(username);
                System.out.println("[DEBUG] getCurrentUser() - User found: " + user.isPresent());
                return user.orElse(null);
            }
            
            System.out.println("[DEBUG] getCurrentUser() - Principal is not UserDetails");
            return null;
        } catch (Exception e) {
            System.err.println("[ERROR] getCurrentUser() - Exception: " + e.getMessage());
            return null;
        }
    }
}
