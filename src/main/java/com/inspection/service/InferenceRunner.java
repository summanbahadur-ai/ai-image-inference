package com.inspection.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inspection.config.StorageProperties;
import com.inspection.dto.InferenceResponse;
import com.inspection.entity.Inspection;
import com.inspection.entity.InspectionStatus;
import com.inspection.repository.InspectionRepository;
import com.inspection.web.NotFoundException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

@Service
public class InferenceRunner {

    private final InspectionRepository repo;
    private final StorageProperties storageProperties;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public InferenceRunner(
            InspectionRepository repo,
            StorageProperties storageProperties,
            ObjectMapper objectMapper,
            WebClient inferenceWebClient
    ) {
        this.repo = repo;
        this.storageProperties = storageProperties;
        this.objectMapper = objectMapper;
        this.webClient = inferenceWebClient;
    }

    public void runInferenceSync(Long inspectionId) {
        Inspection ins = repo.findById(inspectionId)
                .orElseThrow(() -> new NotFoundException("Inspection not found: " + inspectionId));

        if (ins.getStatus() == InspectionStatus.DONE) return;

        ins.setStatus(InspectionStatus.PROCESSING);
        repo.save(ins);

        Path imgPath = resolveAbsoluteImagePath(ins.getImagePath());

        try {
            InferenceResponse resp = callPythonInfer(imgPath);

            ins.setResultJson(objectMapper.writeValueAsString(resp));
            ins.setModelVersion(resp.getModelVersion());
            ins.setProcessingTimeMs(resp.getProcessingTimeMs());
            ins.setStatus(InspectionStatus.DONE);
            repo.save(ins);

        } catch (Exception ex) {
            ins.setStatus(InspectionStatus.FAILED);
            repo.save(ins);
            throw new RuntimeException(ex);
        }
    }

    private Path resolveAbsoluteImagePath(String relativePath) {
        return Paths.get(storageProperties.getRoot()).resolve(relativePath).normalize();
    }

    private InferenceResponse callPythonInfer(Path imagePath) throws Exception {
        byte[] bytes = Files.readAllBytes(imagePath);

        ByteArrayResource fileResource = new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
                return imagePath.getFileName().toString();
            }
        };

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("image", fileResource).contentType(MediaType.APPLICATION_OCTET_STREAM);

        return webClient.post()
                .uri("/infer")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(InferenceResponse.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }
}
