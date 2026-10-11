package org.example.repository;

import org.example.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    Long id(Long id);

    @Query("select a from Author a left join fetch a.books")
    List<Author> findAllWithBooks();
}
