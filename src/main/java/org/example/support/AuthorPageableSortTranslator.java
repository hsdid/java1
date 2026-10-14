package org.example.support;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuthorPageableSortTranslator extends BasePageableSortTranslator {
    @Override
    protected Map<String, String> getAllowedSortFields() {
        return Map.of(
                "id", "a.id",
                "firstName", "a.firstName",
                "lastName", "a.lastName",
                "birthYear", "a.birthYear",
                "deathYear", "a.deathYear",
                "numberOfBooks", "size(a.books)"
        );
    }
}
