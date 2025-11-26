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

                // 20 additional random perfumes
                Perfume p13 = new Perfume();
                p13.setName("Lavender Fields Forever");
                p13.setBrand("Provence Essence");
                p13.setDescription("Classic lavender with herbal notes.");
                p13.setSeason("SUMMER");
                p13.setOccasion("DAY");
                p13.setFragranceNotes(List.of("Lavender", "Herbaceous", "White Musk"));
                p13.setCollectionStatus("CATALOG");
                perfumeService.save(p13);

                Perfume p14 = new Perfume();
                p14.setName("Midnight Mystery");
                p14.setBrand("Dark Alchemist");
                p14.setDescription("Mysterious blend of incense and dark woods.");
                p14.setSeason("WINTER");
                p14.setOccasion("NIGHT");
                p14.setFragranceNotes(List.of("Incense", "Oud", "Smoke"));
                p14.setCollectionStatus("CATALOG");
                perfumeService.save(p14);

                Perfume p15 = new Perfume();
                p15.setName("Peach Paradise");
                p15.setBrand("Fruity Dreams");
                p15.setDescription("Juicy peach with warm amber base.");
                p15.setSeason("SUMMER");
                p15.setOccasion("DAY");
                p15.setFragranceNotes(List.of("Peach", "Amber", "Vanilla"));
                p15.setCollectionStatus("CATALOG");
                perfumeService.save(p15);

                Perfume p16 = new Perfume();
                p16.setName("Forest Walk");
                p16.setBrand("Nature's Breath");
                p16.setDescription("Pine, cedarwood and moss in perfect harmony.");
                p16.setSeason("FALL");
                p16.setOccasion("DAY");
                p16.setFragranceNotes(List.of("Pine", "Cedar", "Moss"));
                p16.setCollectionStatus("CATALOG");
                perfumeService.save(p16);

                Perfume p17 = new Perfume();
                p17.setName("Honey Narcissus");
                p17.setBrand("Golden Hours");
                p17.setDescription("Sweet honey with white florals.");
                p17.setSeason("SPRING");
                p17.setOccasion("DAY");
                p17.setFragranceNotes(List.of("Honey", "Narcissus", "Iris"));
                p17.setCollectionStatus("CATALOG");
                perfumeService.save(p17);

                Perfume p18 = new Perfume();
                p18.setName("Smoke & Mirrors");
                p18.setBrand("Avant Garde");
                p18.setDescription("Smoky vetiver with mysterious depth.");
                p18.setSeason("FALL");
                p18.setOccasion("EVENING");
                p18.setFragranceNotes(List.of("Vetiver", "Smoke", "Leather"));
                p18.setCollectionStatus("CATALOG");
                perfumeService.save(p18);

                Perfume p19 = new Perfume();
                p19.setName("Tropical Escape");
                p19.setBrand("Island Vibes");
                p19.setDescription("Coconut, pineapple and jasmine for vacation vibes.");
                p19.setSeason("SUMMER");
                p19.setOccasion("DAY");
                p19.setFragranceNotes(List.of("Coconut", "Pineapple", "Jasmine"));
                p19.setCollectionStatus("CATALOG");
                perfumeService.save(p19);

                Perfume p20 = new Perfume();
                p20.setName("Caramel Desire");
                p20.setBrand("Sweet Indulgence");
                p20.setDescription("Caramel, vanilla and creamy tonka bean.");
                p20.setSeason("FALL");
                p20.setOccasion("EVENING");
                p20.setFragranceNotes(List.of("Caramel", "Vanilla", "Tonka Bean"));
                p20.setCollectionStatus("CATALOG");
                perfumeService.save(p20);

                Perfume p21 = new Perfume();
                p21.setName("Ocean Mineral");
                p21.setBrand("Salty Shores");
                p21.setDescription("Fresh marine with mineral notes and ambroxan.");
                p21.setSeason("SUMMER");
                p21.setOccasion("DAY");
                p21.setFragranceNotes(List.of("Ambroxan", "Sea Salt", "Algae"));
                p21.setCollectionStatus("CATALOG");
                perfumeService.save(p21);

                Perfume p22 = new Perfume();
                p22.setName("Royal Jasmine");
                p22.setBrand("Palace Luxe");
                p22.setDescription("Rich jasmine sambac with sandalwood.");
                p22.setSeason("SPRING");
                p22.setOccasion("EVENING");
                p22.setFragranceNotes(List.of("Jasmine Sambac", "Sandalwood", "Musk"));
                p22.setCollectionStatus("CATALOG");
                perfumeService.save(p22);

                Perfume p23 = new Perfume();
                p23.setName("Ginger Spice");
                p23.setBrand("Warm Comfort");
                p23.setDescription("Fresh ginger with warming spices and amber.");
                p23.setSeason("FALL");
                p23.setOccasion("DAY");
                p23.setFragranceNotes(List.of("Ginger", "Black Pepper", "Amber"));
                p23.setCollectionStatus("CATALOG");
                perfumeService.save(p23);

                Perfume p24 = new Perfume();
                p24.setName("Magnolia Whisper");
                p24.setBrand("Blossom Haven");
                p24.setDescription("Delicate magnolia with soft vanilla and musk.");
                p24.setSeason("SPRING");
                p24.setOccasion("DAY");
                p24.setFragranceNotes(List.of("Magnolia", "Vanilla", "White Musk"));
                p24.setCollectionStatus("CATALOG");
                perfumeService.save(p24);

                Perfume p25 = new Perfume();
                p25.setName("Black Pepper Dream");
                p25.setBrand("Spice Masters");
                p25.setDescription("Sharp black pepper balanced with creamy notes.");
                p25.setSeason("WINTER");
                p25.setOccasion("EVENING");
                p25.setFragranceNotes(List.of("Black Pepper", "Tonka Bean", "Vanilla"));
                p25.setCollectionStatus("CATALOG");
                perfumeService.save(p25);

                Perfume p26 = new Perfume();
                p26.setName("Apple Blossom");
                p26.setBrand("Orchard Fresh");
                p26.setDescription("Apple blossom with crisp bergamot and green notes.");
                p26.setSeason("SPRING");
                p26.setOccasion("DAY");
                p26.setFragranceNotes(List.of("Apple Blossom", "Bergamot", "Green Leaves"));
                p26.setCollectionStatus("CATALOG");
                perfumeService.save(p26);

                Perfume p27 = new Perfume();
                p27.setName("Sandalwood Sunset");
                p27.setBrand("Twilight Perfumes");
                p27.setDescription("Creamy sandalwood with rose and amber.");
                p27.setSeason("FALL");
                p27.setOccasion("EVENING");
                p27.setFragranceNotes(List.of("Sandalwood", "Rose", "Amber"));
                p27.setCollectionStatus("CATALOG");
                perfumeService.save(p27);

                Perfume p28 = new Perfume();
                p28.setName("Yuzu Citrus");
                p28.setBrand("Asian Garden");
                p28.setDescription("Japanese yuzu with floral and creamy base.");
                p28.setSeason("SPRING");
                p28.setOccasion("DAY");
                p28.setFragranceNotes(List.of("Yuzu", "Jasmine", "Coconut"));
                p28.setCollectionStatus("CATALOG");
                perfumeService.save(p28);

                Perfume p29 = new Perfume();
                p29.setName("Violet Mystique");
                p29.setBrand("Floral Enigma");
                p29.setDescription("Powdery violet with iris and almond milk.");
                p29.setSeason("SPRING");
                p29.setOccasion("DAY");
                p29.setFragranceNotes(List.of("Violet Leaf", "Iris", "Almond"));
                p29.setCollectionStatus("CATALOG");
                perfumeService.save(p29);

                Perfume p30 = new Perfume();
                p30.setName("Vetiver Authority");
                p30.setBrand("Masculine Edge");
                p30.setDescription("Crisp vetiver with tobacco and leather notes.");
                p30.setSeason("FALL");
                p30.setOccasion("EVENING");
                p30.setFragranceNotes(List.of("Vetiver", "Tobacco", "Leather"));
                p30.setCollectionStatus("CATALOG");
                perfumeService.save(p30);

                Perfume p31 = new Perfume();
                p31.setName("Patchouli Paradise");
                p31.setBrand("Earthy Vibes");
                p31.setDescription("Deep patchouli with musk and amber for warmth.");
                p31.setSeason("FALL");
                p31.setOccasion("EVENING");
                p31.setFragranceNotes(List.of("Patchouli", "Musk", "Amber"));
                p31.setCollectionStatus("CATALOG");
                perfumeService.save(p31);

                Perfume p32 = new Perfume();
                p32.setName("Bergamot Bright");
                p32.setBrand("Citrus Collective");
                p32.setDescription("Zesty bergamot with neroli and lemon.");
                p32.setSeason("SPRING");
                p32.setOccasion("DAY");
                p32.setFragranceNotes(List.of("Bergamot", "Neroli", "Lemon"));
                p32.setCollectionStatus("CATALOG");
                perfumeService.save(p32);

                Perfume p33 = new Perfume();
                p33.setName("Fir Tree Escape");
                p33.setBrand("Mountain Lodge");
                p33.setDescription("Fir needle, cypress and hints of pine.");
                p33.setSeason("WINTER");
                p33.setOccasion("DAY");
                p33.setFragranceNotes(List.of("Fir", "Cypress", "Pine"));
                p33.setCollectionStatus("CATALOG");
                perfumeService.save(p33);

                Perfume p34 = new Perfume();
                p34.setName("Cinnamon Toast");
                p34.setBrand("Bakery Bliss");
                p34.setDescription("Warm cinnamon with almond and amber.");
                p34.setSeason("FALL");
                p34.setOccasion("DAY");
                p34.setFragranceNotes(List.of("Cinnamon", "Almond", "Amber"));
                p34.setCollectionStatus("CATALOG");
                perfumeService.save(p34);

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
