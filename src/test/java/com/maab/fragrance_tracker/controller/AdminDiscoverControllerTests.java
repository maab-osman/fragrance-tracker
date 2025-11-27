package com.maab.fragrance_tracker.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.maab.fragrance_tracker.model.Perfume;
import com.maab.fragrance_tracker.model.User;
import com.maab.fragrance_tracker.repository.PerfumeRepository;
import com.maab.fragrance_tracker.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class AdminDiscoverControllerTests {

    @Autowired MockMvc mockMvc;
    @Autowired UserRepository userRepository;
    @Autowired PerfumeRepository perfumeRepository;
    @Autowired com.maab.fragrance_tracker.repository.ReviewRepository reviewRepository;

    Long catalogId;

    @BeforeEach
    void setup() {
    // Clean in FK-safe order
    reviewRepository.deleteAll();
    perfumeRepository.deleteAll();
    userRepository.deleteAll();

    User admin = new User("admin", "admin@example.com", "secret1");
        admin.setAdmin(true);
        userRepository.save(admin);

    User user = new User("alice", "alice@example.com", "secret1");
        userRepository.save(user);

        Perfume catalog = new Perfume("Catalog", "Brand");
        catalog.setCollectionStatus("CATALOG");
        catalogId = perfumeRepository.save(catalog).getId();
    }

    @Test
    @WithMockUser(username = "alice")
    void non_admin_cannot_delete_catalog() throws Exception {
        mockMvc.perform(delete("/api/perfumes/" + catalogId))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin")
    void admin_can_delete_catalog() throws Exception {
        mockMvc.perform(delete("/api/perfumes/" + catalogId))
            .andExpect(status().isOk());
    }
}
