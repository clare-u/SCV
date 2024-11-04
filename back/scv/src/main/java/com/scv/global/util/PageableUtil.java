package com.scv.global.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class PageableUtil {

    public Pageable createPageable(int page, int size, String orderBy, String direction) {
        if (!orderBy.isEmpty()) {
            Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(orderBy).descending() : Sort.by(orderBy).ascending();
            return PageRequest.of(page, size, sort);
        } else {
            return PageRequest.of(page, size);
        }
    }
}
