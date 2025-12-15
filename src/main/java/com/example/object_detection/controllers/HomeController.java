package com.example.object_detection.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller  // NOT @RestController - we're returning HTML, not JSON
public class HomeController {
    
    @GetMapping("/")
    public String home() {
        return "index";  // This looks for src/main/resources/templates/index.html
    }
}