package com.maab.fragrance_tracker.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.maab.fragrance_tracker.model.Perfume;
import com.maab.fragrance_tracker.model.Review;
import com.maab.fragrance_tracker.repository.ReviewRepository;

/**
 * Service layer for managing user reviews and ratings.
 * 
 * Handles review operations including creation, retrieval, and rating calculations
 * for perfumes. Provides methods to compute average ratings used in the recommendation
 * engine and perfume discovery features.
 * 
 * @author Maab Osman
 * @version 1.0
 */
@Service
public class ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    /**
     * Persists a new review to the database.
     * 
     * @param review the Review object to save
     * @return the saved Review object with generated ID
     */
    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }
    
    public List<Review> getReviewsByPerfume(Perfume perfume) {
        return reviewRepository.findByPerfume(perfume);
    }
    
    public double getAverageRating(Perfume perfume) {
        List<Review> reviews = reviewRepository.findByPerfume(perfume);
        if (reviews.isEmpty()) return 0.0;
        
        return reviews.stream()
            .mapToInt(Review::getRating)
            .average()
            .orElse(0.0);
    }
    
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }
}