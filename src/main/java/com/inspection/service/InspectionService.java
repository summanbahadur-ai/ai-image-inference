package com.inspection.service;

import com.inspection.config.StorageProperties;
import com.inspection.entity.Inspection;
import com.inspection.entity.InspectionStatus;
import com.inspection.repository.InspectionRepository;
import com.inspection.web.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class InspectionService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png");

    private final InspectionRepository repo;
    private final StorageProperties storageProperties;
    private final InferenceWorker worker;
    private final InferenceRunner runner;

    public InspectionService(
            InspectionRepository repo,
            StorageProperties storageProperties,
            InferenceWorker worker,
            InferenceRunner runner
    ) {
        this.repo = repo;
        this.storageProperties = storageProperties;
        this.worker = worker;
        this.runner = runner;
    }

    public Inspection createInspectionAsync(MultipartFile image) throws IOException {
        Inspection ins = saveUpload(image);          // PENDING
        worker.runInferenceAsync(ins.getId());       // background: PROCESSING -> DONE/FAILED
        return ins;
    }

    /** Debug/admin: blocks until inference completes and returns updated entity. */
    public Inspection createInspectionSync(MultipartFile image) throws IOException {
        Inspection ins = saveUpload(image);          // PENDING
        runner.runInferenceSync(ins.getId());        // do inference now (blocking)
        return getInspection(ins.getId());           // fetch updated state (DONE/FAILED)
    }


    public List<Inspection> listInspections() {
        return repo.findAll();
    }

    public Inspection getInspection(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Inspection not found: " + id));
    }

    private Inspection saveUpload(MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Image is required");
        }

        String contentType = image.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Only PNG and JPEG images are allowed");
        }

        String ext = contentType.equals("image/png") ? "png" : "jpg";
        String relativePath = "images/" + UUID.randomUUID() + "." + ext;

        Path root = Paths.get(storageProperties.getRoot());
        Path absoluteTarget = root.resolve(relativePath);

        Files.createDirectories(absoluteTarget.getParent());

        try (InputStream in = image.getInputStream()) {
            Files.copy(in, absoluteTarget, StandardCopyOption.REPLACE_EXISTING);
        }

        Inspection ins = new Inspection();
        ins.setImagePath(relativePath);
        ins.setStatus(InspectionStatus.PENDING);

        return repo.save(ins);
    }

}
