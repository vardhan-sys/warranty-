package com.geaviation.techpubs.data.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableUtilTest {
    private static final int page = 0;
    private static final int size = 10;
    private static final String validField = "field";
    private static final String emptyField = "";
    private static final String ascDir = "asc";
    private static final String descDir = "desc";
    private static final String invalidDir = "";

    @Test
    public void whenCreatingPageableWithSortableFieldAndAscDirection_shouldResultInPageableObjWithSortingInAscendingOrder() {
        Pageable pageable = PageableUtil.create(page, size, validField, ascDir);

        assertEquals(page, pageable.getPageNumber());
        assertEquals(size, pageable.getPageSize());

        Sort sort = pageable.getSort();
        assertTrue(sort.isSorted());
        assertNotNull(sort.getOrderFor("field"));
        assertTrue(sort.getOrderFor("field").isAscending());
    }

    @Test
    public void whenCreatingPageableWithSortableFieldAndDescDirection_shouldResultInPageableObjWithSortingInDescendingOrder() {
        Pageable pageable = PageableUtil.create(page, size, validField, descDir);

        assertEquals(page, pageable.getPageNumber());
        assertEquals(size, pageable.getPageSize());

        Sort sort = pageable.getSort();
        assertTrue(sort.isSorted());
        assertNotNull(sort.getOrderFor("field"));
        assertTrue(sort.getOrderFor("field").isDescending());
    }

    @Test
    public void whenCreatingPageableWithDirectionAndEmptyField_shouldResultInUnsortedPageableObject() {
        Pageable pageable = PageableUtil.create(page, size, emptyField, ascDir);

        assertEquals(page, pageable.getPageNumber());
        assertEquals(size, pageable.getPageSize());

        Sort sort = pageable.getSort();
        assertTrue(sort.isUnsorted());
    }

    @Test
    public void whenCreatingPageableWithFieldAndEmptyDirection_shouldResultInUnsortedPageableObject() {
        Pageable pageable = PageableUtil.create(page, size, validField, invalidDir);

        assertEquals(page, pageable.getPageNumber());
        assertEquals(size, pageable.getPageSize());

        Sort sort = pageable.getSort();
        assertTrue(sort.isUnsorted());
    }

}
