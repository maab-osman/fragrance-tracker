package com.maab.fragrance_tracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maab.fragrance_tracker.model.Perfume;
import com.maab.fragrance_tracker.model.Review;
import com.maab.fragrance_tracker.model.User;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByPerfumeId(Long perfumeId);
    List<Review> findByPerfume(Perfume perfume);
    List<Review> findByUserId(User user);
    List<Review> findByPerfumeAndUser(Perfume perfume, User user);
    List<Review> findByRating(int rating);
    


}
