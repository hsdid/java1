package org.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class ImportStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private LocalDateTime submitDate;
    private LocalDateTime startDate;
    private LocalDateTime finishDate;
    private Status status;
    private String failedReason;
    private int processed;
    private String fileName;

    public ImportStatus(String fileName) {
        this.submitDate = LocalDateTime.now();
        this.status = Status.NEW;
        this.fileName = fileName;
    }

    public enum Status {
        NEW, PROCESSING, SUCCESS, FAILED
    }
}
