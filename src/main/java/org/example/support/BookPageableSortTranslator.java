package org.example.support;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BookPageableSortTranslator extends BasePageableSortTranslator {
    @Override
    protected Map<String, String> getAllowedSortFields() {
        return Map.of(
                "id", "id",
                "title", "title",
                "authorId", "author.id",
                "category", "category",
                "available", "available"
        );
    }
}
