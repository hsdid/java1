package org.example.repository;

import org.example.dto.response.AuthorResponse;
import org.example.model.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    Long id(Long id);

    @Query("select new org.example.dto.response.AuthorResponse(a.id, a.firstName, a.lastName, size(a.books)) from Author a")
    Page<AuthorResponse> findAllWithBooks(Pageable pageable);
}
