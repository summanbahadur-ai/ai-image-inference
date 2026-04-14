package com.inspection.dto;

import java.util.List;
import java.util.Map;

public class InferenceResponse {
    private String modelVersion;
    private Long processingTimeMs;
    private List<Map<String, Object>> detections;

    public String getModelVersion() { return modelVersion; }
    public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }

    public Long getProcessingTimeMs() { return processingTimeMs; }
    public void setProcessingTimeMs(Long processingTimeMs) { this.processingTimeMs = processingTimeMs; }

    public List<Map<String, Object>> getDetections() { return detections; }
    public void setDetections(List<Map<String, Object>> detections) { this.detections = detections; }
}
