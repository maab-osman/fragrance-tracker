package com.maab.fragrance_tracker.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
 

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
import com.maab.fragrance_tracker.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class DiscoverControllerTests {

    @Autowired MockMvc mockMvc;
    @Autowired PerfumeRepository perfumeRepository;
    @Autowired UserRepository userRepository;
    @Autowired ObjectMapper objectMapper;
    @Autowired com.maab.fragrance_tracker.repository.ReviewRepository reviewRepository;

    Long catalogId;

    @BeforeEach
    void seed() {
    // Clean in FK-safe order
    reviewRepository.deleteAll();
    perfumeRepository.deleteAll();
    userRepository.deleteAll();

        // three catalog items (user = null)
        for (int i = 0; i < 3; i++) {
            Perfume p = new Perfume("Cat" + i, "Brand" + i);
            p.setCollectionStatus("CATALOG");
            catalogId = perfumeRepository.save(p).getId();
        }

        // one user for collection and reviews
    User alice = new User("alice", "alice@example.com", "secret1");
        userRepository.save(alice);
    }

    @Test
    void discover_public_endpoint_returns_list() throws Exception {
        mockMvc.perform(get("/api/discover?mode=recommended&limit=2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void add_to_collection_requires_auth() throws Exception {
        var body = java.util.Map.of("perfumeId", catalogId);
        mockMvc.perform(post("/api/collection")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "alice")
    void add_to_collection_then_duplicate_conflict() throws Exception {
        var body = java.util.Map.of("perfumeId", catalogId);
        mockMvc.perform(post("/api/collection")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isOk());

        // Duplicate should be 409
        mockMvc.perform(post("/api/collection")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isConflict());
    }

    @Test
    void get_reviews_requires_auth_due_to_security_config() throws Exception {
        mockMvc.perform(get("/api/perfumes/" + catalogId + "/reviews"))
            .andExpect(status().is3xxRedirection()); // formLogin redirects unauthenticated to /login
    }
}
