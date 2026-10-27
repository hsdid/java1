package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.response.ImportStatusResponse;
import org.example.service.ImportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/import")
@RequiredArgsConstructor
public class ImportController {

    private final ImportService importService;

    @PostMapping
    public ResponseEntity<ImportStatusResponse> importBooks(@RequestPart("books") MultipartFile file) throws IOException {
        ImportStatusResponse actualImport = importService.startImport(file.getName());
        importService.importBooks(file.getInputStream(), actualImport.id());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(actualImport);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImportStatusResponse> getImportStatus(@PathVariable int id) {
        ImportStatusResponse actualImport = importService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(actualImport);
    }

    @GetMapping()
    public ResponseEntity<List<ImportStatusResponse>> getAllImportStatus() {
        List<ImportStatusResponse> list = importService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

}
