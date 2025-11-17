package com.maab.fragrance_tracker.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.maab.fragrance_tracker.model.Perfume;
import com.maab.fragrance_tracker.model.Review;
import com.maab.fragrance_tracker.repository.ReviewRepository;


@Service
public class ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
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