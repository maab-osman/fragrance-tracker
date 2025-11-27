package com.maab.fragrance_tracker.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maab.fragrance_tracker.model.Perfume;
import com.maab.fragrance_tracker.model.User;
import com.maab.fragrance_tracker.repository.PerfumeRepository;
import com.maab.fragrance_tracker.repository.ReviewRepository;
import com.maab.fragrance_tracker.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class ReviewsControllerTests {

    @Autowired MockMvc mockMvc;
    @Autowired PerfumeRepository perfumeRepository;
    @Autowired UserRepository userRepository;
    @Autowired ReviewRepository reviewRepository;
    @Autowired ObjectMapper objectMapper;

    Long perfumeId;

    @BeforeEach
    void setup() {
        // Clean in FK-safe order
        reviewRepository.deleteAll();
        perfumeRepository.deleteAll();
        userRepository.deleteAll();

        // Seed one catalog perfume
        Perfume p = new Perfume("TestCat", "BrandX");
        p.setCollectionStatus("CATALOG");
        perfumeId = perfumeRepository.save(p).getId();

        // Seed one user used for authentication in tests
        User bob = new User("bob", "bob@example.com", "secret1");
        userRepository.save(bob);
    }

    @Test
    void post_review_requires_auth_redirects_to_login() throws Exception {
        var body = Map.of("rating", 5, "comment", "Nice");
        mockMvc.perform(post("/api/perfumes/" + perfumeId + "/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "bob")
    void post_review_rejects_invalid_rating() throws Exception {
        var badLow = Map.of("rating", 0, "comment", "too low");
        mockMvc.perform(post("/api/perfumes/" + perfumeId + "/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badLow)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists());

        var badHigh = Map.of("rating", 6, "comment", "too high");
        mockMvc.perform(post("/api/perfumes/" + perfumeId + "/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badHigh)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @WithMockUser(username = "bob")
    void post_review_success_then_get_reviews() throws Exception {
        var ok = Map.of("rating", 4, "comment", "great scent");
        mockMvc.perform(post("/api/perfumes/" + perfumeId + "/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ok)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));

        // Auth required for GET; verify basic shape
        mockMvc.perform(get("/api/perfumes/" + perfumeId + "/reviews"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalReviews").value(1))
            .andExpect(jsonPath("$.averageRating").value(4.0))
            .andExpect(jsonPath("$.reviews[0].username").value("bob"));
    }
}
