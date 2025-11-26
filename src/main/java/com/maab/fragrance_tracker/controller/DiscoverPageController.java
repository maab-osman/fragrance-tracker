package com.maab.fragrance_tracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DiscoverPageController {

    @GetMapping("/discover")
    public String discoverPage() {
        try {
            System.out.println("[DEBUG] discoverPage() - Loading discover template");
            return "discover";
        } catch (Exception e) {
            System.err.println("[ERROR] discoverPage() - Exception: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/dashboard";
        }
    }
}
