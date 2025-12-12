package com.example.object_detection.dto;

import java.util.List;

public class SimpleDetectionResponse {
    private boolean success;
    private String message;
    private List<String> detections;
    
    // Constructors
    public SimpleDetectionResponse() {}
    
    public SimpleDetectionResponse(boolean success, String message, List<String> detections) {
        this.success = success;
        this.message = message;
        this.detections = detections;
    }
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public List<String> getDetections() { return detections; }
    public void setDetections(List<String> detections) { this.detections = detections; }
}