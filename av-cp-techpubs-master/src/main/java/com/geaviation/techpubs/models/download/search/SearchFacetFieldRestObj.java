package com.geaviation.techpubs.models.download.search;

import java.io.Serializable;

/**
 * Stores data on a generic facet.
 *
 * Rest object for client requests or responses.
 * This should stay a POJO, put any logic in Model layer or somewhere else
 */
public class SearchFacetFieldRestObj implements Serializable {
    private static final long serialVersionUID = -8657914587746668750L;
    private String field;
    private String name;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

