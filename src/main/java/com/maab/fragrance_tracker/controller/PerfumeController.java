package com.maab.fragrance_tracker.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.maab.fragrance_tracker.model.Perfume;
import com.maab.fragrance_tracker.model.Review;
import com.maab.fragrance_tracker.model.User;
import com.maab.fragrance_tracker.repository.UserRepository;
import com.maab.fragrance_tracker.service.PerfumeService;
import com.maab.fragrance_tracker.service.ReviewService;

@Controller
@RequestMapping("/perfumes")
public class PerfumeController {

    private final PerfumeService perfumeService;
    @Autowired
    private final UserRepository userRepository; 

    public PerfumeController(PerfumeService perfumeService, UserRepository userRepository) {
        this.perfumeService = perfumeService;
        this.userRepository = userRepository;
    }

    // Predefined options for forms
    private final List<String> seasons = Arrays.asList("SPRING", "SUMMER", "FALL", "WINTER");
    private final List<String> occasions = Arrays.asList("DAY", "NIGHT", "FORMAL", "CASUAL", "OFFICE");
    private final List<String> commonNotes = Arrays.asList(
        "Bergamot", "Lemon", "Orange", "Lavender", "Rose", "Jasmine",
        "Sandalwood", "Vanilla", "Amber", "Musk", "Patchouli", "Oud"
    );

@GetMapping
public String listPerfumes(Model model) {
    // GET CURRENT USER like teacher showed
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    User currentUser = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found"));
    
    System.out.println("DEBUG: Current user: " + currentUser.getUsername());
    
    // Only show current user's perfumes
    model.addAttribute("perfumes", perfumeService.findByUser(currentUser));
    return "perfumes/list";
}

    @GetMapping("/search")
    public String searchByName(@RequestParam String name, Model model) {
        // GET CURRENT USER
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        User currentUser = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Search only current user's perfumes by name
        List<Perfume> results = perfumeService.findByUserAndName(currentUser, name);
        model.addAttribute("perfumes", results);
        model.addAttribute("searchTerm", name);
        return "perfumes/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("perfume", new Perfume());
        model.addAttribute("seasons", seasons);
        model.addAttribute("occasions", occasions);
        model.addAttribute("commonNotes", commonNotes);
        return "perfumes/add";
    }

    @PostMapping("/add")
public String addPerfume(@ModelAttribute Perfume perfume) {
    // GET CURRENT USER and assign to perfume
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    User currentUser = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found"));
    
    perfume.setUser(currentUser); 
    perfumeService.save(perfume);
    return "redirect:/perfumes";
}

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        var opt = perfumeService.findById(id);
        if (opt.isEmpty()) {
            return "error/404";
        }
        Perfume perfume = opt.get();

        // Guard: disallow editing catalog items for non-admins and ensure ownership
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
            .orElse(null);
        boolean isAdmin = currentUser != null && currentUser.isAdmin();

        if ("CATALOG".equalsIgnoreCase(perfume.getCollectionStatus()) && !isAdmin) {
            return "redirect:/perfumes?error=forbidden";
        }
        if (!isAdmin) {
            if (perfume.getUser() == null || !perfume.getUser().getId().equals(currentUser.getId())) {
                return "redirect:/perfumes?error=forbidden";
            }
        }

        model.addAttribute("perfume", perfume);
        model.addAttribute("seasons", seasons);
        model.addAttribute("occasions", occasions);
        model.addAttribute("commonNotes", commonNotes);
        return "perfumes/edit";
    }

    @PostMapping("/edit/{id}")
    public String updatePerfume(@AuthenticationPrincipal UserDetails userDetails,
                                @PathVariable Long id,
                                @ModelAttribute Perfume updated) {
        var existingOpt = perfumeService.findById(id);
        if (existingOpt.isEmpty()) {
            return "error/404";
        }
        Perfume existing = existingOpt.get();

        // Identify current user and role
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
            .orElse(null);
        boolean isAdmin = currentUser != null && currentUser.isAdmin();

        // Block edits to catalog entries unless admin
        if ("CATALOG".equalsIgnoreCase(existing.getCollectionStatus()) && !isAdmin) {
            return "redirect:/perfumes?error=forbidden";
        }

        // For non-admins, ensure ownership
        if (!isAdmin) {
            if (existing.getUser() == null || !existing.getUser().getId().equals(currentUser.getId())) {
                return "redirect:/perfumes?error=forbidden";
            }
        }

        // Whitelist updates; mutate collection instead of replacing
        existing.setName(updated.getName());
        existing.setBrand(updated.getBrand());
        existing.setDescription(updated.getDescription());
        existing.setSeason(updated.getSeason());
        existing.setOccasion(updated.getOccasion());
        if (updated.getFragranceNotes() != null) {
            existing.getFragranceNotes().clear();
            existing.getFragranceNotes().addAll(updated.getFragranceNotes());
        }

        perfumeService.save(existing);
        return "redirect:/perfumes";
    }

    @GetMapping("/delete/{id}")
    public String deletePerfume(@PathVariable Long id) {
        var existingOpt = perfumeService.findById(id);
        if (existingOpt.isEmpty()) {
            return "error/404";
        }
        perfumeService.deleteById(id);
        return "redirect:/perfumes";
    }
    @Autowired
private ReviewService reviewService;

@GetMapping("/{id}/reviews")
public String viewPerfumeReviews(@PathVariable Long id) {
    // Redirect to discover page - reviews are now shown in modal on discover page
    return "redirect:/discover";
}

@PostMapping("/{id}/reviews")
public String addReview(@PathVariable Long id, @ModelAttribute Review review) {
    // Get current user
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
    User currentUser = userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new RuntimeException("User not found"));
    
    var opt = perfumeService.findById(id);
    if (opt.isEmpty()) {
        return "error/404";
    }
    Perfume perfume = opt.get();
    
    review.setUser(currentUser);
    review.setPerfume(perfume);
    reviewService.saveReview(review);
    
    return "redirect:/discover";
}
public String addFromDiscoverToMyCollection(@PathVariable("id") Long perfumeId,
                                                @AuthenticationPrincipal UserDetails principal,
                                                RedirectAttributes redirectAttributes) {

        if (principal == null) {
            // just in case, but usually Spring will already redirect to login
            return "redirect:/login";
        }

        User user = userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new IllegalStateException("Logged in user not found"));

        perfumeService.addPerfumeFromCatalogToUser(perfumeId, user);

        redirectAttributes.addFlashAttribute("successMessage", "Perfume added to your collection!");
        return "redirect:/my-collection"; // adjust to your actual route
    }
}

