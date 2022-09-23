package com.geaviation.techpubs.data.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableUtil {

    private PageableUtil() { }

    public static Pageable create(int page, int size, String field, String direction) {
        Pageable pageable = null;
        boolean canSort = StringUtils.isNotBlank(field);

        if (canSort && "desc".equalsIgnoreCase(direction)) {
            pageable = PageRequest.of(page, size, Sort.by(field).descending());
        } else if (canSort && "asc".equalsIgnoreCase(direction)) {
            pageable = PageRequest.of(page, size, Sort.by(field).ascending());
        } else {
            pageable = PageRequest.of(page, size);
        }

        return pageable;
    }
}
