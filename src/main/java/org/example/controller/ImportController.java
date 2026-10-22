package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.model.ImportStatus;
import org.example.service.ImportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/import")
@RequiredArgsConstructor
public class ImportController {

    private final ImportService importService;

    // todo zmiana na dto
    @PostMapping
    public ResponseEntity<ImportStatus> importBooks(@RequestPart("books") MultipartFile file) throws IOException {
        ImportStatus actualImport = importService.startImport(file.getName());
        importService.importBooks(file.getInputStream(), actualImport.getId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(actualImport);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImportStatus> importBooks(@PathVariable int id) {
        ImportStatus actualImport = importService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(actualImport);
    }



}
