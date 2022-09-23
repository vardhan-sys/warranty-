package com.geaviation.techpubs.controllers.requests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SortByTest {

    @Test
    public void whenSortValueHasValidFieldAndDirectionIsAsc_shouldReturnProvidedFieldAndDirection() {
        SortBy sortBy = new SortBy("airframe|asc");

        assertEquals("airframe", sortBy.field());
        assertEquals("asc", sortBy.direction());
    }

    @Test
    public void whenSortValueHasValidFieldAndDirectionIsDesc_shouldReturnProvidedFieldAndDirection() {
        SortBy sortBy = new SortBy("airframe|desc");

        assertEquals("airframe", sortBy.field());
        assertEquals("desc", sortBy.direction());
    }

    @Test
    public void whenSortValueOnlyHasFieldNoSeparatorNoDirection_shouldReturnFieldAndAscAsDefaultDirection() {
        SortBy sortBy = new SortBy("airframe");

        assertEquals("airframe", sortBy.field());
        assertEquals("asc", sortBy.direction());
    }

    @Test
    public void whenSortValueOnlyHasFieldWithSeparatorNoDirection_shouldReturnFieldAndAscAsDefaultDirection() {
        SortBy sortBy = new SortBy("airframe|");

        assertEquals("airframe", sortBy.field());
        assertEquals("asc", sortBy.direction());
    }

    @Test
    public void whenSortValueHasValidFieldAndDirectionIsAscValuesHaveWhitespaceCharacters_shouldReturnTrimmedValues() {
        SortBy sortBy = new SortBy(" airframe | asc ");

        assertEquals("airframe", sortBy.field());
        assertEquals("asc", sortBy.direction());
    }

    @Test
    public void whenNoFieldAndNoDirectionButHasDelimiter_shouldReturnEmptyFieldAndAscAsDefaultDirection() {
        SortBy sortBy = new SortBy("|");

        assertEquals("", sortBy.field());
        assertEquals("asc", sortBy.direction());
    }

    @Test
    public void whenNoFieldWithDelimiterAndAscDirection_shouldReturnEmptyFieldAndAscDirection() {
        SortBy sortBy = new SortBy("|asc");

        assertEquals("", sortBy.field());
        assertEquals("asc", sortBy.direction());
    }

    @Test
    public void whenNoFieldWithDelimiterAndDescDirection_shouldReturnEmptyFieldAndDescDirection() {
        SortBy sortBy = new SortBy("|desc");

        assertEquals("", sortBy.field());
        assertEquals("desc", sortBy.direction());
    }

    @Test
    public void whenEmptyString_shouldGetEmptyFieldAndAscAsDefaultDirection() {
        SortBy sortBy = new SortBy("");

        assertEquals("", sortBy.field());
        assertEquals("asc", sortBy.direction());
    }
}
