package com.inspection.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class InferenceWorker {

    private final InferenceRunner runner;

    public InferenceWorker(InferenceRunner runner) {
        this.runner = runner;
    }

    @Async("inferenceExecutor")
    public void runInferenceAsync(Long inspectionId) {
        try {
            runner.runInferenceSync(inspectionId);
        } catch (Exception ignored) {
            // status is set to FAILED inside runner
        }
    }
}
