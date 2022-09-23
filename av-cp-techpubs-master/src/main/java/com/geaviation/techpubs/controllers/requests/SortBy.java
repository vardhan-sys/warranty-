package com.geaviation.techpubs.controllers.requests;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SortBy {
    private final List<String> sortSplit;

    public SortBy(String sort) {
        this.sortSplit = Arrays.stream(sort.split("\\|"))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public String field() {
        String field = "";

        List<String> fields = sortSplit.stream()
                .filter(s -> !"asc".equalsIgnoreCase(s) && !"desc".equalsIgnoreCase(s))
                .collect(Collectors.toList());

        if (fields.size() > 0) {
            field = fields.get(0);
        }

        return field;
    }

    public String direction() {
        String direction = "asc";

        for (String s : sortSplit) {
            if ("desc".equalsIgnoreCase(s)) {
                direction = "desc";
                break;
            }
        }

        return direction;
    }

    @Override
    public String toString() {
        return String.join("|", sortSplit);
    }
}
