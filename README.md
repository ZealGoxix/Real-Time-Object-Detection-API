# Real-Time-Object-Detection-API
Spring, YOLO Model

🚀 How to Run Everything

Terminal 1: Start the Python AI Service
bash
cd python-ai-service
python ai_service.py
Wait for: "✅ Model loaded and ready!"

Terminal 2: Start Your Spring Boot App
bash
./mvnw spring-boot:run 

Terminal 3: Test It!
bash

# Test the Python service directly
curl -X POST -F "image=@src/images/download.jfif" http://localhost:5000/detect

# Test your Spring Boot app (which calls Python)
curl -X POST -F "image=@src/images/download.jfif" http://localhost:8080/api/detect

# Now with Frontend
Open browser on port 8080
