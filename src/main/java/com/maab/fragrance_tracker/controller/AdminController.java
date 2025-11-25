package com.maab.fragrance_tracker.controller;

import java.util.Arrays;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.maab.fragrance_tracker.model.Perfume;
import com.maab.fragrance_tracker.service.PerfumeService;

@Controller
public class AdminController {

    private final PerfumeService perfumeService;

    public AdminController(PerfumeService perfumeService) {
        this.perfumeService = perfumeService;
    }

    @GetMapping("/admin/catalog")
    public String catalogPage(Model model) {
        model.addAttribute("perfumes", perfumeService.findAll());
        return "admin/catalog";
    }

    @PostMapping("/admin/catalog")
    public String addCatalogPerfume(@RequestParam String name,
                                    @RequestParam String brand,
                                    @RequestParam(required = false) String description,
                                    @RequestParam(required = false) String season,
                                    @RequestParam(required = false) String occasion,
                                    @RequestParam(required = false) String notes) {

        Perfume p = new Perfume();
        p.setName(name);
        p.setBrand(brand);
        p.setDescription(description);
        p.setSeason(season);
        p.setOccasion(occasion);
        if (notes != null && !notes.isEmpty()) {
            p.setFragranceNotes(Arrays.stream(notes.split(",")).map(String::trim).toList());
        }
        p.setCollectionStatus("CATALOG");
        // catalog items are not tied to any user (user == null)
        perfumeService.save(p);
        return "redirect:/admin/catalog";
    }

    @PostMapping("/admin/catalog/delete")
    public String deleteCatalogPerfume(@RequestParam Long id) {
        perfumeService.deleteById(id);
        return "redirect:/admin/catalog";
    }

    @PostMapping("/admin/catalog/edit")
    public String editCatalogPerfume(@RequestParam Long id,
                                     @RequestParam String name,
                                     @RequestParam String brand,
                                     @RequestParam(required = false) String description,
                                     @RequestParam(required = false) String season,
                                     @RequestParam(required = false) String occasion,
                                     @RequestParam(required = false) String notes) {
        Perfume existing = perfumeService.findById(id).orElse(null);
        if (existing != null) {
            existing.setName(name);
            existing.setBrand(brand);
            existing.setDescription(description);
            existing.setSeason(season);
            existing.setOccasion(occasion);
            if (notes != null) {
                existing.setFragranceNotes(Arrays.stream(notes.split(",")).map(String::trim).toList());
            }
            perfumeService.save(existing);
        }
        return "redirect:/admin/catalog";
    }
}
