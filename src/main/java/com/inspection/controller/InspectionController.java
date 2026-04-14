package com.inspection.controller;

import com.inspection.entity.Inspection;
import com.inspection.service.InspectionService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/inspections")
public class InspectionController {

    private final InspectionService service;

    public InspectionController(InspectionService service) {
        this.service = service;
    }

    @PostMapping(value = "/async", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Inspection createAsync(@RequestPart("image") MultipartFile image) throws IOException {
        return service.createInspectionAsync(image);
    }

    @PostMapping(value = "/sync", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Inspection createSync(@RequestPart("image") MultipartFile image) throws IOException {
        return service.createInspectionSync(image);
    }

    @GetMapping
    public List<Inspection> list() {
        return service.listInspections();
    }

    @GetMapping("/{id}")
    public Inspection get(@PathVariable Long id) {
        return service.getInspection(id);
    }

    @GetMapping("/health")
    public String Health() {
        return " OK";

    }

}
