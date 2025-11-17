package com.maab.fragrance_tracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maab.fragrance_tracker.model.Perfume;
import com.maab.fragrance_tracker.model.User;

public interface PerfumeRepository extends JpaRepository<Perfume, Long> {
    List<Perfume> findByBrand(String brand);
    List<Perfume> findByNameContainingIgnoreCase(String name);
    List<Perfume> findBySeason(String season);

    List<Perfume> findByUser(User user);
    
    //Find by user and other criteria
    List<Perfume> findByUserAndBrand(User user, String brand);
    List<Perfume> findByUserAndSeason(User user, String season);

    public List<Perfume> findAllByUser(User user);
}
