package com.maab.fragrance_tracker.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maab.fragrance_tracker.dto.PerfumeDto;
import com.maab.fragrance_tracker.model.Perfume;
import com.maab.fragrance_tracker.model.Review;
import com.maab.fragrance_tracker.model.User;
import com.maab.fragrance_tracker.repository.UserRepository;
import com.maab.fragrance_tracker.service.PerfumeService;
import com.maab.fragrance_tracker.service.ReviewService;

@RestController
public class DiscoverController {

    private final PerfumeService perfumeService;
    private final UserRepository userRepository;
    private final ReviewService reviewService;

    public DiscoverController(PerfumeService perfumeService, UserRepository userRepository, ReviewService reviewService) {
        this.perfumeService = perfumeService;
        this.userRepository = userRepository;
        this.reviewService = reviewService;
    }

    @GetMapping("/api/discover")
    public ResponseEntity<List<PerfumeDto>> discover(@RequestParam(required = false, defaultValue = "recommended") String mode,
                                                     @RequestParam(required = false, defaultValue = "8") int limit) {
        try {
            System.out.println("[DEBUG] discover() - mode: " + mode + ", limit: " + limit);
            User currentUser = getCurrentUser();
            System.out.println("[DEBUG] discover() - currentUser: " + (currentUser != null ? currentUser.getUsername() : "null"));
            
            List<Perfume> list;
            try {
                list = switch (mode) {
                    case "random" -> {
                        System.out.println("[DEBUG] discover() - loading random mode");
                        yield perfumeService.findRandom(limit);
                    }
                    case "trending" -> {
                        System.out.println("[DEBUG] discover() - loading trending mode");
                        yield perfumeService.findLatest(limit);
                    }
                    default -> {
                        System.out.println("[DEBUG] discover() - loading recommended mode");
                        yield perfumeService.recommendForUser(currentUser, limit);
                    }
                };
            } catch (Exception e) {
                System.err.println("[ERROR] discover() - Exception loading mode " + mode + ": " + e.getMessage());
                list = java.util.Collections.emptyList();
            }

            System.out.println("[DEBUG] discover() - found " + list.size() + " perfumes");
            
            final List<Long> userOwnedIds = currentUser != null ? perfumeService.findByUser(currentUser).stream().map(Perfume::getId).collect(Collectors.toList()) : java.util.Collections.emptyList();

            List<PerfumeDto> dtos = list.stream().map(p -> new PerfumeDto(p.getId(), p.getName(), p.getBrand(),
                    p.getDescription()==null?"":(p.getDescription().length()>120?p.getDescription().substring(0,120)+"...":p.getDescription()),
                    List.of(p.getSeason(), p.getOccasion()).stream().filter(s->s!=null).collect(Collectors.toList()),
                    currentUser!=null && userOwnedIds.contains(p.getId())
            )).collect(Collectors.toList());

            System.out.println("[DEBUG] discover() - returning " + dtos.size() + " DTOs");
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            System.err.println("[ERROR] discover() - Exception: " + e.getMessage());
            return ResponseEntity.status(500).body(java.util.Collections.emptyList());
        }
    }

    @PostMapping("/api/collection")
    public ResponseEntity<?> addToCollection(@RequestBody Map<String, Object> body) {
        Long perfumeId = ((Number) body.get("perfumeId")).longValue();
        var opt = perfumeService.findById(perfumeId);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error","Perfume not found"));
        }
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error","Unauthorized"));
        }

        // Refresh user with eagerly loaded perfumes to ensure we have all owned perfumes
        currentUser = userRepository.findByIdWithPerfumes(currentUser.getId()).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error","User not found"));
        }

        // Check if user already has a clone of this catalog perfume by sourcePerfumeId
        Perfume sourcePerfume = opt.get();
        boolean alreadyOwned = currentUser.getPerfumes().stream()
            .anyMatch(p -> p.getSourcePerfumeId() != null && p.getSourcePerfumeId().equals(sourcePerfume.getId()));
        
        if (alreadyOwned) {
            return ResponseEntity.status(409).body(Map.of("error","Perfume already in your collection"));
        }

        Perfume source = opt.get();
        // clone basic fields to create a user-owned instance
        Perfume mine = new Perfume();
        mine.setName(source.getName());
        mine.setBrand(source.getBrand());
        mine.setDescription(source.getDescription());
        mine.setSeason(source.getSeason());
        mine.setOccasion(source.getOccasion());
        // copy notes list to avoid sharing the same collection instance (causes Hibernate "shared references" error)
        if (source.getFragranceNotes() != null) {
            mine.setFragranceNotes(new java.util.ArrayList<>(source.getFragranceNotes()));
        }
        mine.setUser(currentUser);
        mine.setCollectionStatus("OWNED");
        mine.setSourcePerfumeId(source.getId());  // Track which catalog perfume this was cloned from
        perfumeService.save(mine);

        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/api/perfumes/{id}/reviews")
    public ResponseEntity<?> getReviews(@PathVariable Long id) {
        try {
            System.out.println("[DEBUG] getReviews() - perfumeId: " + id);
            var opt = perfumeService.findById(id);
            if (opt.isEmpty()) {
                System.out.println("[ERROR] getReviews() - Perfume not found: " + id);
                return ResponseEntity.badRequest().body(Map.of("error", "Perfume not found"));
            }
            Perfume perfume = opt.get();
            List<Review> reviews = reviewService.getReviewsByPerfume(perfume);
            double avgRating = reviewService.getAverageRating(perfume);
            
            System.out.println("[DEBUG] getReviews() - found " + reviews.size() + " reviews");
            
            List<Map<String, Object>> reviewDtos = reviews.stream().map(r -> {
                Map<String, Object> dto = new HashMap<>();
                dto.put("id", r.getId());
                dto.put("username", r.getUser().getUsername());
                dto.put("rating", r.getRating());
                dto.put("comment", r.getComment());
                dto.put("createdAt", r.getCreatedAt().toString());
                return dto;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(Map.of(
                "reviews", reviewDtos,
                "averageRating", avgRating,
                "totalReviews", reviews.size()
            ));
        } catch (Exception e) {
            System.err.println("[ERROR] getReviews() - Exception: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/api/perfumes/{id}/reviews")
    public ResponseEntity<?> addReview(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            System.out.println("[DEBUG] addReview() - perfumeId: " + id);
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                System.out.println("[ERROR] addReview() - User not authenticated");
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
            }
            
            System.out.println("[DEBUG] addReview() - User: " + currentUser.getUsername());
            var opt = perfumeService.findById(id);
            if (opt.isEmpty()) {
                System.out.println("[ERROR] addReview() - Perfume not found: " + id);
                return ResponseEntity.badRequest().body(Map.of("error", "Perfume not found"));
            }
            
            Perfume perfume = opt.get();
            int rating = ((Number) body.get("rating")).intValue();
            String comment = (String) body.get("comment");
            
            System.out.println("[DEBUG] addReview() - rating: " + rating + ", comment: " + comment);
            
            if (rating < 1 || rating > 5) {
                System.out.println("[ERROR] addReview() - Invalid rating: " + rating);
                return ResponseEntity.badRequest().body(Map.of("error", "Rating must be between 1 and 5"));
            }
            
            Review review = new Review();
            review.setUser(currentUser);
            review.setPerfume(perfume);
            review.setRating(rating);
            review.setComment(comment);
            review.setCreatedAt(LocalDateTime.now());
            
            reviewService.saveReview(review);
            
            System.out.println("[DEBUG] addReview() - Review saved successfully");
            return ResponseEntity.ok(Map.of("success", true, "message", "Review added successfully"));
        } catch (Exception e) {
            System.err.println("[ERROR] addReview() - Exception: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    private User getCurrentUser() {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth==null) return null;
            Object principal = auth.getPrincipal();
            if (principal instanceof UserDetails ud) {
                return userRepository.findByUsername(ud.getUsername()).orElse(null);
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }
}
