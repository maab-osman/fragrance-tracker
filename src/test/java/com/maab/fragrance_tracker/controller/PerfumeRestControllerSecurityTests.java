package com.maab.fragrance_tracker.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
class PerfumeRestControllerSecurityTests {

    @Autowired MockMvc mockMvc;
    @Autowired UserRepository userRepository;
    @Autowired PerfumeRepository perfumeRepository;
    @Autowired com.maab.fragrance_tracker.repository.ReviewRepository reviewRepository;
    @Autowired ObjectMapper objectMapper;

    Long catalogId;
    Long aliceOwnedId;

    @BeforeEach
    void initData() {
    // Clean in FK-safe order
    reviewRepository.deleteAll();
    perfumeRepository.deleteAll();
    userRepository.deleteAll();

    User admin = new User("admin", "admin@example.com", "secret1");
        admin.setAdmin(true);
        userRepository.save(admin);

    User alice = new User("alice", "alice@example.com", "secret1");
        userRepository.save(alice);

    User bob = new User("bob", "bob@example.com", "secret1");
        userRepository.save(bob);

        Perfume catalog = new Perfume("Catalog", "Brand");
        catalog.setCollectionStatus("CATALOG");
        catalogId = perfumeRepository.save(catalog).getId();

        Perfume owned = new Perfume("Alice One", "Brand");
        owned.setCollectionStatus("OWNED");
        owned.setUser(userRepository.findByUsername("alice").orElseThrow());
        aliceOwnedId = perfumeRepository.save(owned).getId();
    }

    @Test
    @WithMockUser(username = "alice")
    void owner_can_update_own_non_catalog() throws Exception {
        var payload = new Perfume();
        payload.setName("Alice Updated");
        payload.setBrand("Brand");
        payload.setDescription("Desc");

        mockMvc.perform(put("/api/perfumes/" + aliceOwnedId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("Alice Updated")));
    }

    @Test
    @WithMockUser(username = "bob")
    void non_owner_cannot_update_someone_else() throws Exception {
        var payload = new Perfume();
        payload.setName("Hacked");
        payload.setBrand("Brand");

        mockMvc.perform(put("/api/perfumes/" + aliceOwnedId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "alice")
    void non_admin_cannot_update_catalog() throws Exception {
        var payload = new Perfume();
        payload.setName("Nope");
        payload.setBrand("Brand");

        mockMvc.perform(put("/api/perfumes/" + catalogId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin")
    void admin_can_update_catalog() throws Exception {
        var payload = new Perfume();
        payload.setName("Catalog Updated");
        payload.setBrand("Brand");

        mockMvc.perform(put("/api/perfumes/" + catalogId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("Catalog Updated")));
    }
}
