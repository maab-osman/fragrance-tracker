package com.maab.fragrance_tracker.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.maab.fragrance_tracker.model.Perfume;
import com.maab.fragrance_tracker.model.Review;
import com.maab.fragrance_tracker.model.User;
import com.maab.fragrance_tracker.repository.UserRepository;
import com.maab.fragrance_tracker.service.PerfumeService;
import com.maab.fragrance_tracker.service.ReviewService;

@Configuration
@Profile("dev")
public class DevDataLoader {

    @Bean
    CommandLineRunner seedDev(UserRepository userRepository, PerfumeService perfumeService, PasswordEncoder passwordEncoder, ReviewService reviewService) {
        return args -> {
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User("admin", "admin@example.com", passwordEncoder.encode("admin"));
                admin.setAdmin(true);
                userRepository.save(admin);
            }

            // add some catalog perfumes if none exist
            if (perfumeService.findAll().isEmpty()) {
                Perfume p1 = new Perfume();
                p1.setName("Citrus Sunrise");
                p1.setBrand("Softala House");
                p1.setDescription("A bright citrus fragrance perfect for daytime.");
                p1.setSeason("SPRING");
                p1.setOccasion("DAY");
                p1.setFragranceNotes(List.of("Lemon","Bergamot","Grapefruit"));
                p1.setCollectionStatus("CATALOG");
                perfumeService.save(p1);

                Perfume p2 = new Perfume();
                p2.setName("Nocturne Oud");
                p2.setBrand("Softala House");
                p2.setDescription("Deep woody oud for evenings and formal events.");
                p2.setSeason("WINTER");
                p2.setOccasion("NIGHT");
                p2.setFragranceNotes(List.of("Oud","Sandalwood","Amber"));
                p2.setCollectionStatus("CATALOG");
                perfumeService.save(p2);

                // sample review
                Review r = new Review();
                r.setPerfume(p2);
                r.setRating(5);
                r.setComment("Amazing depth and longevity.");
                r.setUser(userRepository.findByUsername("admin").orElse(null));
                reviewService.saveReview(r);
            }
        };
    }
}
