package com.maab.fragrance_tracker.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.maab.fragrance_tracker.model.Perfume;
import com.maab.fragrance_tracker.service.PerfumeService;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final PerfumeService perfumeService;

    public AdminController(PerfumeService perfumeService) {
        this.perfumeService = perfumeService;
    }

    // GET /admin/catalog?page=0&size=20
    @GetMapping("/catalog")
    public String catalogPage(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "20") int size,
                              Model model) {

        // If you have a service/repo method for catalog-only:
        // Page<Perfume> catalog = perfumeService.findCatalog(PageRequest.of(page, size));
        // If not, use all for now:
        Page<Perfume> catalog = perfumeService.findAllPaged(PageRequest.of(page, size));

        model.addAttribute("perfumes", catalog.getContent());
        model.addAttribute("page", catalog);
        return "admin/catalog";
    }

    @PostMapping("/catalog")
    public String addCatalogPerfume(@RequestParam String name,
                                    @RequestParam String brand,
                                    @RequestParam(required = false) String description,
                                    @RequestParam(required = false) String season,
                                    @RequestParam(required = false) String occasion,
                                    @RequestParam(required = false) String notes) {

        String n = trimOrNull(name);
        String b = trimOrNull(brand);
        if (n == null || b == null) {
            // Name and brand are required; redirect back quietly
            return "redirect:/admin/catalog?error=missing";
        }

        // Prevent duplicates in catalog by name+brand (+status)
        boolean exists = perfumeService.existsByNameBrandAndStatus(n, b, "CATALOG");
        if (exists) {
            return "redirect:/admin/catalog?info=exists";
        }

        Perfume p = new Perfume();
        p.setName(n);
        p.setBrand(b);
        p.setDescription(trimOrNull(description));
        p.setSeason(trimOrNull(season));
        p.setOccasion(trimOrNull(occasion));
        p.setCollectionStatus("CATALOG"); // catalog item; user == null

        List<String> parsedNotes = parseNotes(notes);
        p.setFragranceNotes(parsedNotes);  // if you use @ElementCollection List<String>

        perfumeService.save(p);
        return "redirect:/admin/catalog?ok=added";
    }

    @PostMapping("/catalog/edit")
    public String editCatalogPerfume(@RequestParam Long id,
                                     @RequestParam String name,
                                     @RequestParam String brand,
                                     @RequestParam(required = false) String description,
                                     @RequestParam(required = false) String season,
                                     @RequestParam(required = false) String occasion,
                                     @RequestParam(required = false) String notes) {

        Perfume existing = perfumeService.findById(id).orElse(null);
        if (existing == null) {
            return "redirect:/admin/catalog?error=notfound";
        }

        String n = trimOrNull(name);
        String b = trimOrNull(brand);
        if (n == null || b == null) {
            return "redirect:/admin/catalog?error=missing";
        }

        existing.setName(n);
        existing.setBrand(b);
        existing.setDescription(trimOrNull(description));
        existing.setSeason(trimOrNull(season));
        existing.setOccasion(trimOrNull(occasion));
        existing.setCollectionStatus("CATALOG"); // keep it catalog

        List<String> parsedNotes = parseNotes(notes);
        existing.setFragranceNotes(parsedNotes);  // clear if empty

        perfumeService.save(existing);
        return "redirect:/admin/catalog?ok=updated";
    }

    @PostMapping("/catalog/delete")
    public String deleteCatalogPerfume(@RequestParam Long id) {
        // If you have FK reviews → ensure cascade or delete reviews first
        perfumeService.deleteById(id);
        return "redirect:/admin/catalog?ok=deleted";
    }

    // --- helpers ---

    private static String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static List<String> parseNotes(String notes) {
        String t = trimOrNull(notes);
        if (t == null) return List.of();
        return Arrays.stream(t.split(","))
                     .map(String::trim)
                     .filter(x -> !x.isEmpty())
                     .toList();
    }
}
