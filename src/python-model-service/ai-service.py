from flask import Flask, request, jsonify
from flask_cors import CORS
from transformers import YolosImageProcessor, YolosForObjectDetection # model
from PIL import Image
import torch
import tempfile
import os

app = Flask(__name__)
CORS(app)  # Allows your Spring Boot app to call this

# Load the YOLO model (this happens once when starting)
print("🔍 Loading YOLO model from Hugging Face...")
model = YolosForObjectDetection.from_pretrained("hustvl/yolos-tiny")  # Smaller, faster model
processor = YolosImageProcessor.from_pretrained("hustvl/yolos-tiny") # preprocesses image
print("✅ Model loaded and ready!")

@app.route('/health', methods=['GET'])
def health():
    return jsonify({"status": "ready", "model": "YOLOS-tiny"})

@app.route('/detect', methods=['POST'])
def detect():
    """Accepts an image file, runs object detection"""
    if 'image' not in request.files:
        return jsonify({"error": "No image provided"}), 400
    
    # Save uploaded image to temp file
    image_file = request.files['image']
    temp_file = tempfile.NamedTemporaryFile(delete=False, suffix='.jpg')
    image_file.save(temp_file.name)
    
    try:
        # Process image
        image = Image.open(temp_file.name).convert('RGB')
        
        # Prepare for model
        inputs = processor(images=image, return_tensors="pt")
        
        # Run detection
        with torch.no_grad():
            outputs = model(**inputs)
        
        # Process results
        target_sizes = torch.tensor([image.size[::-1]])  # [height, width]
        results = processor.post_process_object_detection(
            outputs, 
            threshold=0.7, 
            target_sizes=target_sizes
        )[0]
        
        # Format detections
        detections = []
        for score, label, box in zip(results["scores"], results["labels"], results["boxes"]):
            detections.append({
                "label": model.config.id2label[label.item()],
                "confidence": round(score.item(), 3),
                "box": {
                    "x": round(box[0].item()),
                    "y": round(box[1].item()),
                    "width": round(box[2].item() - box[0].item()),
                    "height": round(box[3].item() - box[1].item())
                }
            })
        
        return jsonify({
            "success": True,
            "detections": detections,
            "count": len(detections)
        })
        
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    finally:
        # Clean up temp file
        os.unlink(temp_file.name)

if __name__ == '__main__':
    print("🚀 Starting AI service on http://localhost:5000")
    print("   - POST /detect  : Upload image for detection")
    print("   - GET  /health  : Check if service is running")
    app.run(host='0.0.0.0', port=5000, debug=False)