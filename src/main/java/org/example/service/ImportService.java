package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.ImportStatus;
import org.example.repository.ImportStatusRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImportService {
    private static final String IMPORT_BOOK_SQL = "insert into book(title, category, available, author_id) values (?,?,?,?)";
    private final JdbcTemplate jdbcTemplate;
    private final ImportStatusRepository importStatusRepository;


    public ImportStatus startImport(String fileName) {
        return importStatusRepository.saveAndFlush(new ImportStatus(fileName));
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    public ImportStatus findById(int id) {
        return importStatusRepository.findById(id).orElseThrow();
    }

    @Transactional
    @Async("booksImportExecutor")
    public void importBooks(InputStream inputStream, int id) {

        updateToProcessing(id);
        AtomicInteger counter = new AtomicInteger(0);
        AtomicLong start = new AtomicLong(System.currentTimeMillis());

        new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .parallel()
                .map(line -> line.split(","))
                .peek(command -> countTime(counter, start))
                .forEach(this::createBook);

        // todo
        // updateToSuccess()
        // updateToFailed()
        // updateProgress()
    }

    private void updateToProcessing(int id) {
        ImportStatus importStatus = importStatusRepository.findById(id).orElseThrow();
        importStatus.setStartDate(LocalDateTime.now());
        importStatus.setStatus(ImportStatus.Status.PROCESSING);
        importStatusRepository.saveAndFlush(importStatus);
    }

    private void createBook(String[] args) {
        jdbcTemplate.update(IMPORT_BOOK_SQL, args[0], args[1], true, args[2]);
    }

    private void countTime(AtomicInteger counter, AtomicLong start) {
        if (counter.incrementAndGet() % 10000 == 0) {
            log.info("imported: {} in {} ms", counter, (System.currentTimeMillis() - start.get()));
            start.set(System.currentTimeMillis());
        }
    }


}
