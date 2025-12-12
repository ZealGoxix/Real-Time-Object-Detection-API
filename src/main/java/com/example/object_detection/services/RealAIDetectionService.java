package com.example.object_detection.services;

import com.example.object_detection.dto.SimpleDetectionResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class RealAIDetectionService {
    
    private final WebClient webClient;
    
    public RealAIDetectionService() {
        this.webClient = WebClient.builder()
            .baseUrl("http://localhost:5000")  // Your Python service!
            .build();
    }
    
    public SimpleDetectionResponse detectObjects(MultipartFile imageFile) {
        try {
            // Convert MultipartFile to temporary File
            File tempFile = File.createTempFile("upload-", ".jpg");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(imageFile.getBytes());
            }
            
            // Build the multipart request
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("image", new FileSystemResource(tempFile));
            
            // Call the Python AI service
            AIResponse aiResponse = webClient.post()
                .uri("/detect")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(AIResponse.class)
                .block();  // Wait for response (for simplicity)
            
            // Clean up temp file
            tempFile.delete();
            
            // Convert AI response to our simple format
            if (aiResponse.success) {
                List<String> detections = new ArrayList<>();
                for (AIDetection detection : aiResponse.detections) {
                    detections.add(String.format("%s (%.0f%%)", 
                        detection.label, 
                        detection.confidence * 100));
                }
                return new SimpleDetectionResponse(
                    true,
                    "Detected " + aiResponse.count + " object(s)",
                    detections
                );
            } else {
                return new SimpleDetectionResponse(
                    false,
                    "AI service failed",
                    List.of()
                );
            }
            
        } catch (WebClientResponseException e) {
            return new SimpleDetectionResponse(
                false,
                "AI service error: " + e.getMessage(),
                List.of()
            );
        } catch (Exception e) {
            return new SimpleDetectionResponse(
                false,
                "Error: " + e.getMessage(),
                List.of()
            );
        }
    }
    
    // Inner classes to match the Python service JSON response
    private static class AIResponse {
        public boolean success;
        public List<AIDetection> detections;
        public int count;
    }
    
    private static class AIDetection {
        public String label;
        public double confidence;
        public Box box;
    }
    
    private static class Box {
        public int x;
        public int y;
        public int width;
        public int height;
    }
}