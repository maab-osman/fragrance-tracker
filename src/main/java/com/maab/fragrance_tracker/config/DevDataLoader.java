package com.maab.fragrance_tracker.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.maab.fragrance_tracker.model.Perfume;
import com.maab.fragrance_tracker.model.Review;
import com.maab.fragrance_tracker.model.User;
import com.maab.fragrance_tracker.repository.UserRepository;
import com.maab.fragrance_tracker.service.PerfumeService;
import com.maab.fragrance_tracker.service.ReviewService;

@Configuration
public class DevDataLoader {

    @Bean
    CommandLineRunner seedDev(UserRepository userRepository, PerfumeService perfumeService, PasswordEncoder passwordEncoder, ReviewService reviewService) {
        return args -> {
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User("admin", "admin@example.com", passwordEncoder.encode("admin123"));
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

                Perfume p3 = new Perfume();
                p3.setName("Vanilla Dream");
                p3.setBrand("Aurora Scents");
                p3.setDescription("Warm vanilla and tonka bean for cozy nights in.");
                p3.setSeason("WINTER");
                p3.setOccasion("NIGHT");
                p3.setFragranceNotes(List.of("Vanilla", "Tonka Bean", "Cocoa"));
                p3.setCollectionStatus("CATALOG");
                perfumeService.save(p3);

                Perfume p4 = new Perfume();
                p4.setName("Rose Garden Walk");
                p4.setBrand("Floral Muse");
                p4.setDescription("Fresh dewy rose with a soft musky base.");
                p4.setSeason("SPRING");
                p4.setOccasion("DAY");
                p4.setFragranceNotes(List.of("Rose", "Peony", "White Musk"));
                p4.setCollectionStatus("CATALOG");
                perfumeService.save(p4);

                Perfume p5 = new Perfume();
                p5.setName("Office Breeze");
                p5.setBrand("Minimalist Lab");
                p5.setDescription("Clean, soapy musk ideal for everyday office wear.");
                p5.setSeason("ALL");
                p5.setOccasion("DAY");
                p5.setFragranceNotes(List.of("Aldehydes", "Musk", "Neroli"));
                p5.setCollectionStatus("CATALOG");
                perfumeService.save(p5);

                Perfume p6 = new Perfume();
                p6.setName("Evening Velvet");
                p6.setBrand("Noir Atelier");
                p6.setDescription("Velvety plum and amber for date nights.");
                p6.setSeason("FALL");
                p6.setOccasion("NIGHT");
                p6.setFragranceNotes(List.of("Plum", "Amber", "Patchouli"));
                p6.setCollectionStatus("CATALOG");
                perfumeService.save(p6);

                Perfume p7 = new Perfume();
                p7.setName("Summer Sea Salt");
                p7.setBrand("Coastal Notes");
                p7.setDescription("Salty marine accord with citrus and driftwood.");
                p7.setSeason("SUMMER");
                p7.setOccasion("DAY");
                p7.setFragranceNotes(List.of("Sea Salt", "Lime", "Driftwood"));
                p7.setCollectionStatus("CATALOG");
                perfumeService.save(p7);

                Perfume p8 = new Perfume();
                p8.setName("Library Leather");
                p8.setBrand("Archive Parfums");
                p8.setDescription("Leather, paper and vanilla evoking an old bookshop.");
                p8.setSeason("FALL");
                p8.setOccasion("EVENING");
                p8.setFragranceNotes(List.of("Leather", "Cedar", "Vanilla"));
                p8.setCollectionStatus("CATALOG");
                perfumeService.save(p8);

                Perfume p9 = new Perfume();
                p9.setName("Matcha Morning");
                p9.setBrand("Tea Rituals");
                p9.setDescription("Soft green tea with jasmine and a hint of citrus.");
                p9.setSeason("SPRING");
                p9.setOccasion("DAY");
                p9.setFragranceNotes(List.of("Green Tea", "Jasmine", "Lemon Zest"));
                p9.setCollectionStatus("CATALOG");
                perfumeService.save(p9);

                Perfume p10 = new Perfume();
                p10.setName("Gold Souk Amber");
                p10.setBrand("Oriental Stories");
                p10.setDescription("Resinous amber with spices inspired by Middle Eastern souks.");
                p10.setSeason("WINTER");
                p10.setOccasion("NIGHT");
                p10.setFragranceNotes(List.of("Amber", "Cardamom", "Labdanum"));
                p10.setCollectionStatus("CATALOG");
                perfumeService.save(p10);

                Perfume p11 = new Perfume();
                p11.setName("Cotton Cloud");
                p11.setBrand("Soft Skin Co.");
                p11.setDescription("Skin-scent musk, very minimal and clean.");
                p11.setSeason("ALL");
                p11.setOccasion("DAY");
                p11.setFragranceNotes(List.of("White Musk", "Powder", "Iris"));
                p11.setCollectionStatus("CATALOG");
                perfumeService.save(p11);

                Perfume p12 = new Perfume();
                p12.setName("Spiced Chai Hug");
                p12.setBrand("Comfort House");
                p12.setDescription("Cardamom, cinnamon and creamy sandalwood.");
                p12.setSeason("FALL");
                p12.setOccasion("EVENING");
                p12.setFragranceNotes(List.of("Cardamom", "Cinnamon", "Sandalwood"));
                p12.setCollectionStatus("CATALOG");
                perfumeService.save(p12);   



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
