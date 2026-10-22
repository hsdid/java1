package org.example.repository;

import org.example.model.ImportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImportStatusRepository extends JpaRepository<ImportStatus, Integer> {
}
