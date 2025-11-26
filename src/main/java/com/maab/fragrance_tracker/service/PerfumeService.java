package com.maab.fragrance_tracker.service;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maab.fragrance_tracker.model.Perfume;
import com.maab.fragrance_tracker.model.User;
import com.maab.fragrance_tracker.repository.PerfumeRepository;

/**
 * Service layer for managing perfume operations and recommendations.
 * 
 * Provides CRUD operations for perfumes and an intelligent recommendation engine
 * that analyzes user preferences based on their fragrance collection and returns
 * personalized suggestions using a scoring algorithm.
 * 
 * @author Maab Osman
 * @version 1.0
 */
@Service
@Transactional(readOnly = true)
public class PerfumeService {

    private final PerfumeRepository perfumeRepository;
    private final ReviewService reviewService;

    /**
     * Constructs a PerfumeService with dependency injection.
     * 
     * @param perfumeRepository Spring Data JPA repository for Perfume entities
     * @param reviewService Service for accessing review data and ratings
     */
    public PerfumeService(PerfumeRepository perfumeRepository, ReviewService reviewService) {
        this.perfumeRepository = perfumeRepository;
        this.reviewService = reviewService;
    }

   /**
    * Retrieves all perfumes belonging to a specific user.
    * 
    * @param user the user whose perfumes should be retrieved
    * @return a list of Perfume objects owned by the user
    */
   public List<Perfume> findByUser(User user) {
    return perfumeRepository.findByUser(user);
}

    @Transactional
    public Perfume save(Perfume perfume) {
        return perfumeRepository.save(perfume);
    }

    public Optional<Perfume> findById(Long id) {
        return perfumeRepository.findById(id);
    }

    public List<Perfume> findByName(String name) {
        return perfumeRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Perfume> findByUserAndName(User user, String name) {
        List<Perfume> userPerfumes = perfumeRepository.findByUser(user);
        return userPerfumes.stream()
                .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();
    }

    @Transactional
    public void deleteById(Long id) {
        perfumeRepository.deleteById(id);
    }

    public List<Perfume> findAll() {
        return perfumeRepository.findAll();
    }

    /**
     * Finds random catalog perfumes (admin-added only).
     * Only returns perfumes with user = null (admin catalog items).
     */
    public List<Perfume> findRandom(int limit) {
        List<Perfume> all = perfumeRepository.findAll();
        // Filter to only admin-added perfumes (user = null)
        List<Perfume> catalogOnly = all.stream()
            .filter(p -> p.getUser() == null)
            .toList();
        java.util.Collections.shuffle(catalogOnly);
        return catalogOnly.stream().limit(limit).toList();
    }

    /**
     * Finds latest catalog perfumes (admin-added only).
     * Only returns perfumes with user = null (admin catalog items).
     */
    public List<Perfume> findLatest(int limit) {
        org.springframework.data.domain.PageRequest pr = org.springframework.data.domain.PageRequest.of(0, limit * 2, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "id"));
        List<Perfume> all = perfumeRepository.findAll(pr).getContent();
        // Filter to only admin-added perfumes (user = null)
        return all.stream()
            .filter(p -> p.getUser() == null)
            .limit(limit)
            .toList();
    }

    /**
     * Generates personalized perfume recommendations for a user.
     * 
     * This method analyzes the user's fragrance collection to build a preference profile
     * (favorite notes, seasons, occasions) and scores all available perfumes against this profile.
     * Results are cached to improve performance on repeated calls for the same user.
     * 
     * Advanced Spring Boot Feature: This method uses @Cacheable for method-level caching,
     * which is an advanced Spring feature not typically covered in introductory courses.
     * It improves application performance by caching recommendations per user.
     * 
     * @param user the user for whom recommendations are generated
     * @param limit maximum number of recommendations to return
     * @return a list of recommended perfumes sorted by relevance score (descending)
     */
    @Cacheable(value = "recommendations", key = "#user.id + '_' + #limit")
    public List<Perfume> recommendForUser(User user, int limit) {
        if (user == null) {
            return findLatest(limit);
        }

        List<Perfume> userPerfumes = perfumeRepository.findByUser(user);
        if (userPerfumes.isEmpty()) {
            return findLatest(limit);
        }

        // Build user preference profile: notes, season, occasion
        java.util.Map<String, Integer> noteFreq = new java.util.HashMap<>();
        java.util.Map<String, Integer> seasonFreq = new java.util.HashMap<>();
        java.util.Map<String, Integer> occasionFreq = new java.util.HashMap<>();

        for (Perfume p : userPerfumes) {
            if (p.getFragranceNotes() != null) {
                for (String n : p.getFragranceNotes()) {
                    noteFreq.merge(n.toLowerCase(), 1, Integer::sum);
                }
            }
            if (p.getSeason() != null) seasonFreq.merge(p.getSeason().toLowerCase(), 1, Integer::sum);
            if (p.getOccasion() != null) occasionFreq.merge(p.getOccasion().toLowerCase(), 1, Integer::sum);
        }

        List<Perfume> candidates = perfumeRepository.findAll().stream()
                .filter(p -> p.getUser() == null || !p.getUser().equals(user)) // prefer catalog items
                .toList();

        // Score candidates
        java.util.Map<Perfume, Double> scores = new java.util.HashMap<>();
        for (Perfume c : candidates) {
            double score = 0.0;
            if (c.getSeason() != null && seasonFreq.containsKey(c.getSeason().toLowerCase())) score += 3.0;
            if (c.getOccasion() != null && occasionFreq.containsKey(c.getOccasion().toLowerCase())) score += 3.0;
            if (c.getFragranceNotes() != null) {
                for (String n : c.getFragranceNotes()) {
                    score += noteFreq.getOrDefault(n.toLowerCase(), 0);
                }
            }
            // popularity boost from reviews
            double avg = reviewService.getAverageRating(c);
            int reviewCount = reviewService.getReviewsByPerfume(c).size();
            score += avg * Math.log(1 + reviewCount + 1);
            scores.put(c, score);
        }

        // sort by score desc, fallback to latest/random if low scores
        List<Perfume> sorted = scores.entrySet().stream()
                .sorted(java.util.Map.Entry.<Perfume, Double>comparingByValue(Comparator.reverseOrder()))
                .map(java.util.Map.Entry::getKey)
                .filter(p -> scores.get(p) > 0)
                .limit(limit)
                .toList();

        if (sorted.isEmpty()) {
            // fallback to trending then random
            List<Perfume> trending = findLatest(limit);
            if (!trending.isEmpty()) return trending;
            return findRandom(limit);
        }

        return sorted;
    }

    public Page<Perfume> findAllPaged(PageRequest of) {
        return perfumeRepository.findAll(of);
    }

public Page<Perfume> findCatalog(Pageable pageable) {
    return perfumeRepository.findAllByCollectionStatusOrderByIdDesc("CATALOG", pageable);
}
public boolean existsByNameBrandAndStatus(String name, String brand, String status) {
    return perfumeRepository.existsByNameAndBrandAndCollectionStatus(name, brand, status);
}

    /**
     * Deletes a perfume by ID (admin-only operation).
     * This removes the perfume from the catalog.
     * 
     * @param perfumeId the ID of the perfume to delete
     */
    public void deletePerfume(Long perfumeId) {
        perfumeRepository.deleteById(perfumeId);
    }

    public Perfume addPerfumeFromCatalogToUser(Long catalogPerfumeId, User user) {
        Perfume catalogPerfume = perfumeRepository.findById(catalogPerfumeId)
                .orElseThrow(() -> new NoSuchElementException("Catalog perfume not found with id " + catalogPerfumeId));

        Perfume personal = new Perfume();
        personal.setName(catalogPerfume.getName());
        personal.setBrand(catalogPerfume.getBrand());
        personal.setDescription(catalogPerfume.getDescription());
        personal.setSeason(catalogPerfume.getSeason());
        personal.setOccasion(catalogPerfume.getOccasion());
        personal.setFragranceNotes(catalogPerfume.getFragranceNotes());
        
        // this is the key difference: this perfume belongs to a specific user
        personal.setUser(user);
        personal.setCollectionStatus("OWNED"); // or "WISHLIST" depending on your logic

        return perfumeRepository.save(personal);
    }


}
