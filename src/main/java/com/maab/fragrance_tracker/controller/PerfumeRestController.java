package com.maab.fragrance_tracker.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    public ResponseEntity<Perfume> updatePerfume(@PathVariable Long id, @Valid @RequestBody Perfume updated) {
        var existingOpt = perfumeService.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Perfume existing = existingOpt.get();
        updated.setId(id);
        updated.setUser(existing.getUser());
        Perfume saved = perfumeService.save(updated);
        return ResponseEntity.ok(saved);
    }

    /**
     * DELETE /api/perfumes/{id} - Delete a perfume.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerfume(@PathVariable Long id) {
        var existingOpt = perfumeService.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        perfumeService.deleteById(id);
        return ResponseEntity.noContent().build();
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
