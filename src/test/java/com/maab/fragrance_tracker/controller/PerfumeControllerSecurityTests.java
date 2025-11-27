package com.maab.fragrance_tracker.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
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
class PerfumeControllerSecurityTests {

    @Autowired MockMvc mockMvc;
    @Autowired UserRepository userRepository;
    @Autowired PerfumeRepository perfumeRepository;
    @Autowired com.maab.fragrance_tracker.repository.ReviewRepository reviewRepository;

    Long catalogId;
    Long aliceOwnedId;

    @BeforeEach
    void setup() {
    // Clean in FK-safe order
    reviewRepository.deleteAll();
    perfumeRepository.deleteAll();
    userRepository.deleteAll();

    User admin = new User("admin", "admin@example.com", "secret1");
        admin.setAdmin(true);
        userRepository.save(admin);

    User alice = new User("alice", "alice@example.com", "secret1");
        alice.setAdmin(false);
        alice = userRepository.save(alice);

    User bob = new User("bob", "bob@example.com", "secret1");
        userRepository.save(bob);

        Perfume catalog = new Perfume("Catalog Scent", "Brand");
        catalog.setCollectionStatus("CATALOG");
        catalogId = perfumeRepository.save(catalog).getId();

        Perfume owned = new Perfume("Alice Scent", "Brand");
        owned.setCollectionStatus("OWNED");
        owned.setUser(alice);
        aliceOwnedId = perfumeRepository.save(owned).getId();
    }

    @Test
    @WithMockUser(username = "alice")
    void nonAdmin_cannot_GET_edit_catalog() throws Exception {
        mockMvc.perform(get("/perfumes/edit/" + catalogId))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/perfumes?error=forbidden"));
    }

    @Test
    @WithMockUser(username = "bob")
    void nonOwner_cannot_GET_edit_others_owned() throws Exception {
        mockMvc.perform(get("/perfumes/edit/" + aliceOwnedId))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/perfumes?error=forbidden"));
    }

    @Test
    @WithMockUser(username = "alice")
    void nonAdmin_cannot_POST_edit_catalog() throws Exception {
        mockMvc.perform(post("/perfumes/edit/" + catalogId)
            .param("name", "New Name")
            .param("brand", "Brand")
            .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/perfumes?error=forbidden"));
    }

    @Test
    @WithMockUser(username = "alice")
    void owner_can_POST_edit_own_perfume() throws Exception {
        mockMvc.perform(post("/perfumes/edit/" + aliceOwnedId)
            .param("name", "Updated Alice Scent")
            .param("brand", "Brand")
            .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/perfumes"));
    }
}
