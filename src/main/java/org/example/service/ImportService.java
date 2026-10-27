package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.response.ImportStatusResponse;
import org.example.model.ImportStatus;
import org.example.repository.ImportStatusRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImportService {
    private static final int BATCH_SIZE = 2000;
    private final ImportStatusRepository importStatusRepository;
    private final ImportUpdaterService importUpdaterService;


    public ImportStatusResponse startImport(String fileName) {
        ImportStatus importStatus =  importStatusRepository.saveAndFlush(new ImportStatus(fileName));
        return ImportStatusResponse.map(importStatus);
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    public ImportStatusResponse findById(int id) {
        ImportStatus importStatus = importStatusRepository.findById(id).orElseThrow();

        return ImportStatusResponse.map(importStatus);
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    public List<ImportStatusResponse> findAll() {
        return importStatusRepository.findAll()
                .stream()
                .map(ImportStatusResponse::map)
                .toList();
    }

    @Async("booksImportExecutor")
    public void importBooks(InputStream inputStream, int id) {

        AtomicInteger counter = new AtomicInteger(0);
        AtomicLong start = new AtomicLong(System.currentTimeMillis());
        List<String[]> batch = new ArrayList<>(1000);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            importUpdaterService.updateToProcessing(id);

            reader.lines()
                    .map(line -> line.split(","))
                    .peek(command -> countTime(counter, start, id))
                    .forEach(item -> {
                        validateBookImportItem(item, counter);

                        batch.add(item);

                        if (batch.size() == BATCH_SIZE) {
                            importUpdaterService.saveBooksBatch(batch, id);
                            batch.clear();
                        }
                    });

            if (!batch.isEmpty()) {
                importUpdaterService.saveBooksBatch(batch, id);
                batch.clear();
            }

            importUpdaterService.updateToSuccess(id);
        } catch (Exception e) {
            log.error("Import {} failed", id, e);
            importUpdaterService.updateToFailed(id, e.getMessage());
        }
    }

    private void validateBookImportItem(String[] item, AtomicInteger lineNumber) {
        if (item.length != 3) {
            throw new IllegalArgumentException(
                    "Line " + lineNumber + ": expected 3 columns"
            );
        }

        if (item[0].isBlank()) {
            throw new IllegalArgumentException(
                    "Line " + lineNumber + ": title cannot be blank"
            );
        }

        if (item[1].isBlank()) {
            throw new IllegalArgumentException(
                    "Line " + lineNumber + ": category cannot be blank"
            );
        }

        try {
            long authorId = Long.parseLong(item[2].trim());

            if (authorId <= 0) {
                throw new IllegalArgumentException(
                        "Line " + lineNumber + ": authorId must be positive"
                );
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Line " + lineNumber + ": authorId must be a number"
            );
        }
    }

    private void countTime(AtomicInteger counter, AtomicLong start, int id) {
        if (counter.incrementAndGet() % 10000 == 0) {
            log.info("imported: {} in {} ms", counter, (System.currentTimeMillis() - start.get()));
            start.set(System.currentTimeMillis());
        }
    }


}
