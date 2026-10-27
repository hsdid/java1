package org.example.dto.response;

import org.example.model.ImportStatus;

import java.time.LocalDateTime;

public record ImportStatusResponse(
        int id,
        LocalDateTime submitDate,
        LocalDateTime startDate,
        LocalDateTime finishDate,
        ImportStatus.Status status,
        String failedReason,
        int processed
        ) {
    public static ImportStatusResponse map(ImportStatus importStatus) {
        return new ImportStatusResponse(
                importStatus.getId(),
                importStatus.getSubmitDate(),
                importStatus.getStartDate(),
                importStatus.getFinishDate(),
                importStatus.getStatus(),
                importStatus.getFailedReason(),
                importStatus.getProcessed()
        );
    }
}

