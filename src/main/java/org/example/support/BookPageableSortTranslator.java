package org.example.support;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BookPageableSortTranslator extends BasePageableSortTranslator {
    @Override
    protected Map<String, String> getAllowedSortFields() {
        return Map.of(
                "id", "b.id",
                "title", "b.title",
                "authorId", "b.author.id",
                "category", "b.category",
                "available", "b.available"
        );
    }
}
