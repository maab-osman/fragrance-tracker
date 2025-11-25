package com.maab.fragrance_tracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DiscoverPageController {

    @GetMapping("/discover")
    public String discoverPage() {
        return "discover";
    }
}
