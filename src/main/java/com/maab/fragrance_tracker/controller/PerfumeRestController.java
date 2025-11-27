package com.maab.fragrance_tracker.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maab.fragrance_tracker.model.Perfume;
import com.maab.fragrance_tracker.model.User;
import com.maab.fragrance_tracker.repository.UserRepository;
import com.maab.fragrance_tracker.service.PerfumeService;

import jakarta.validation.Valid;

/**
 * REST API controller for Perfume CRUD operations.
 * Secured with Spring Security (OAuth2 / Form login).
 * API documentation available at: /swagger-ui.html
 */
@RestController
@RequestMapping("/api/perfumes")
public class PerfumeRestController {

    private final PerfumeService perfumeService;
    private final UserRepository userRepository;

    public PerfumeRestController(PerfumeService perfumeService, UserRepository userRepository) {
        this.perfumeService = perfumeService;
        this.userRepository = userRepository;
    }

    /**
     * GET /api/perfumes - Get all perfumes for the current user.
     */
    @GetMapping
    public ResponseEntity<List<Perfume>> getAllPerfumes() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return ResponseEntity.ok(perfumeService.findByUser(currentUser));
    }

    /**
     * GET /api/perfumes/{id} - Get a single perfume by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Perfume> getPerfumeById(@PathVariable Long id) {
        return perfumeService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/perfumes - Create a new perfume.
     */
    @PostMapping
    public ResponseEntity<Perfume> createPerfume(@Valid @RequestBody Perfume perfume) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        perfume.setUser(currentUser);
        Perfume saved = perfumeService.save(perfume);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * PUT /api/perfumes/{id} - Update an existing perfume.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePerfume(@PathVariable Long id, @Valid @RequestBody Perfume updated) {
        var existingOpt = perfumeService.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Perfume existing = existingOpt.get();

        // Identify current user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UserDetails userDetails)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElse(null);

        boolean isAdmin = currentUser != null && currentUser.isAdmin();

        // Block edits to CATALOG items unless admin
        if ("CATALOG".equalsIgnoreCase(existing.getCollectionStatus()) && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(java.util.Map.of("error", "Only admins can edit catalog perfumes"));
        }

        // For non-admins, only allow editing own perfumes
        if (!isAdmin) {
            if (existing.getUser() == null || !existing.getUser().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(java.util.Map.of("error", "You can only edit your own perfumes"));
            }
        }

        // Whitelist fields to update and preserve ownership and system fields
        existing.setName(updated.getName());
        existing.setBrand(updated.getBrand());
        existing.setDescription(updated.getDescription());
        existing.setSeason(updated.getSeason());
        existing.setOccasion(updated.getOccasion());
        if (updated.getFragranceNotes() != null) {
            existing.getFragranceNotes().clear();
            existing.getFragranceNotes().addAll(updated.getFragranceNotes());
        }
        // Preserve user, collectionStatus, and sourcePerfumeId
        // existing.setUser(existing.getUser()); // unchanged
        // existing.setCollectionStatus(existing.getCollectionStatus()); // unchanged
        // existing.setSourcePerfumeId(existing.getSourcePerfumeId()); // unchanged

        Perfume saved = perfumeService.save(existing);
        return ResponseEntity.ok(saved);
    }

    /**
     * GET /api/perfumes/search?name=... - Search perfumes by name.
     */
    @GetMapping("/search")
    public ResponseEntity<List<Perfume>> searchByName(String name) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Perfume> results = perfumeService.findByUserAndName(currentUser, name);
        return ResponseEntity.ok(results);
    }
}
