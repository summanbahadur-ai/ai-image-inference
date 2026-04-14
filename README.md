# AI Image Inspection Backend

A **Spring Boot backend** that accepts image uploads and delegates image inference to a **Python FastAPI service running YOLOv8**.

Inference is executed **asynchronously** to keep APIs responsive while heavy ML processing happens in the background.

---

# Project Overview

This project demonstrates how **AI/ML inference is integrated into a real backend system**.

Instead of exposing the ML model directly, the system uses a **backend orchestration layer** responsible for:

- API handling and validation
- inspection lifecycle management
- asynchronous execution
- communication with a Python inference service

The **Python service** runs a **YOLOv8 object detection model** and returns structured results.

---

# Architecture

Client → Spring Boot Backend → Async Worker → Python FastAPI (YOLOv8)

---

# Key Features

- REST API to upload inspection images
- Image storage on disk (DB stores relative paths)
- **Asynchronous inference execution**
- Java ↔ Python microservice communication
- **YOLOv8 real-time object detection**
- Inspection lifecycle tracking:
  
`PENDING → PROCESSING → DONE / FAILED`

- Optional sync endpoint for debugging
- Bounding box visualization support

---

# Tech Stack

### Backend
- Java 17
- Spring Boot
- Spring Web + WebClient
- Spring Async (`@Async`)
- Spring Data JPA / Hibernate
- MySQL

### Inference Service
- Python
- FastAPI
- Ultralytics YOLOv8
- OpenCV
- Uvicorn

---

# Asynchronous Processing
ThreadPoolTaskExecutor
corePoolSize: 4
maxPoolSize: 8
queueCapacity: 200


### Why Async?

ML inference is CPU intensive and can take time to complete.

Running it asynchronously ensures:
- HTTP requests return quickly
- backend threads are not blocked
- the system scales better under load

---

# YOLOv8 Object Detection

The FastAPI service loads a **YOLOv8 model** at startup.

Example model:


### Why Async?

ML inference is CPU intensive and can take time to complete.

Running it asynchronously ensures:
- HTTP requests return quickly
- backend threads are not blocked
- the system scales better under load

---

# YOLOv8 Object Detection

The FastAPI service loads a **YOLOv8 model** at startup.

https://github.com/summanbahadur/ai-image-inference-python


Each uploaded image is processed and the service returns detected objects with:

- label
- confidence
- bounding box coordinates

---

# Example Detection Response

Example response returned by the inference service:

```json
{
  "modelVersion": "yolov8n.pt",
  "processingTimeMs": 42,
  "detections": [
    {
      "label": "dog",
      "conf": 0.92,
      "bbox": [120, 45, 320, 410]
    },
    {
      "label": "car",
      "conf": 0.88,
      "bbox": [350, 210, 640, 420]
    }
  ]
}
```


# Example Test Image
<img width="400" height="400" alt="test3" src="https://github.com/user-attachments/assets/7bdf314a-70ce-4d68-845d-96181851359d" />
<img width="400" height="400" alt="image" src="https://github.com/user-attachments/assets/70c39667-457e-4aab-925a-6a5e95fb4ed2" />


# Why This Project Matters

This project demonstrates real-world AI system integration, including:

backend orchestration for ML services

asynchronous job execution

microservice communication

scalable backend design

It reflects how enterprise AI systems are built, where models run behind backend services rather than directly exposed to clients.

# Future Improvements

Support multiple ML models

Add retry mechanism for failed inspections

Introduce message queue (Kafka / RabbitMQ)

Add authentication and authorization

Dockerize both services

Add monitoring and metrics

# Author

Summan Bahadur
Java Backend Developer | AI Systems Integration

