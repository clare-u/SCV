package com.scv.global.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class PageableUtil {

    public Pageable createPageable(int page, int size, String orderBy, String direction) {
        if (orderBy != null && !orderBy.isEmpty()) {
            Sort.Direction sortDirection = getSortDirection(direction);
            Sort sort = Sort.by(sortDirection, orderBy);
            return PageRequest.of(page, size, sort);
        }
        return PageRequest.of(page, size);
    }

    private Sort.Direction getSortDirection(String direction) {
        if (direction != null && direction.equalsIgnoreCase("asc")) {
            return Sort.Direction.ASC;
        }
        return Sort.Direction.DESC;  // 기본값은 DESC
    }
}