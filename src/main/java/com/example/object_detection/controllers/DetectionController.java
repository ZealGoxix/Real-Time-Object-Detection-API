package com.example.object_detection.controllers;

import com.example.object_detection.dto.SimpleDetectionResponse;
import java.util.List; // Add this import for the List class
import com.example.object_detection.services.RealAIDetectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/detect")
public class DetectionController {
    
    @Autowired
    private RealAIDetectionService detectionService;  // Changed!
    
    @PostMapping
    public SimpleDetectionResponse detectObjects(
            @RequestParam("image") MultipartFile image) {
        
        if (image.isEmpty()) {
            return new SimpleDetectionResponse(
                false, 
                "Please upload an image file", 
                List.of()
            );
        }
        
        System.out.println("📡 Sending to AI service: " + image.getOriginalFilename());
        
        // This now calls the REAL AI service!
        return detectionService.detectObjects(image);
    }
    
    @GetMapping("/health")
    public String health() {
        return "Spring Boot app is running. AI service should be at localhost:5000";
    }
}