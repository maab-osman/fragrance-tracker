package com.maab.fragrance_tracker.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

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
        User currentUser = getCurrentUser();
        // Verify user is admin
        if (currentUser == null || !currentUser.isAdmin()) {
            return "redirect:/discover";
        }
        return "admin-discover";
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
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return userRepository.findByUsername(username).orElse(null);
        }
        return null;
    }
}
