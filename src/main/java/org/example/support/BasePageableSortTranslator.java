package org.example.support;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;

import java.util.Map;

public abstract class BasePageableSortTranslator {
    protected abstract Map<String, String> getAllowedSortFields();

    public Pageable translate(Pageable pageable) {
        if (pageable == null || pageable.getSort().isUnsorted()) {
            return pageable;
        }

        Sort translatedSort = Sort.unsorted();
        Map<String, String> allowedFields = getAllowedSortFields();

        for (Sort.Order order : pageable.getSort()) {
            String property = order.getProperty();

            if (allowedFields.containsKey(property)) {
                String jpqlExpression = allowedFields.get(property);
                translatedSort = translatedSort.and(JpaSort.unsafe(order.getDirection(), jpqlExpression));
            }
        }

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), translatedSort);
    }
}
