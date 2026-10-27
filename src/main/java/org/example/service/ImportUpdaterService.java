package org.example.service;

import lombok.AllArgsConstructor;
import org.example.model.ImportStatus;
import org.example.repository.ImportStatusRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ImportUpdaterService {
    private static final String IMPORT_BOOK_SQL = "insert into book(title, category, available, author_id) values (?,?,?,?)";
    private static final String STATUS_PROCESSED_UPDATE = "update import_status set processed = processed + ? where id = ?";
    private final ImportStatusRepository importStatusRepository;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void updateToProcessing(int id) {
        ImportStatus importStatus = importStatusRepository.findById(id).orElseThrow();
        importStatus.setStartDate(LocalDateTime.now());
        importStatus.setStatus(ImportStatus.Status.PROCESSING);
        importStatusRepository.saveAndFlush(importStatus);
    }

    @Transactional
    public void updateToFailed(int id, String errorMessage) {
        ImportStatus importStatus = importStatusRepository.findById(id).orElseThrow();
        importStatus.setStatus(ImportStatus.Status.FAILED);
        importStatus.setFinishDate(LocalDateTime.now());
        importStatus.setFailedReason(errorMessage);
        importStatusRepository.saveAndFlush(importStatus);
    }

    @Transactional
    public void updateToSuccess(int id) {
        ImportStatus importStatus = importStatusRepository.findById(id).orElseThrow();
        importStatus.setStatus(ImportStatus.Status.SUCCESS);
        importStatus.setFinishDate(LocalDateTime.now());
        importStatusRepository.saveAndFlush(importStatus);
    }

    @Transactional
    public void saveBooksBatch(List<String[]> batch, int id) {

        jdbcTemplate.batchUpdate(IMPORT_BOOK_SQL, batch, batch.size(), (ps, item) -> {
            ps.setString(1, item[0]);
            ps.setString(2, item[1]);
            ps.setBoolean(3, true);
            ps.setString(4, item[2]);

        });
        updateProgress(id, batch.size());
    }

    public void updateProgress(int id, int processedItems) {
        jdbcTemplate.update(STATUS_PROCESSED_UPDATE, processedItems, id);
    }
}
