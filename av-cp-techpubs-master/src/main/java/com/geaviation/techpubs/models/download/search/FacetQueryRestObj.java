package com.geaviation.techpubs.models.download.search;

import java.io.Serializable;
import java.util.List;

/**
 * Holds values pertaining to the query string to Elastic and contains methods for formatting that
 * data.
 *
 * Rest object for client requests or responses.
 * This should stay a POJO, put any logic in Model layer or somewhere else
 */
public class FacetQueryRestObj implements Serializable {
    private static final long serialVersionUID = 4060404845381641405L;
    private String name;
    private List<String> values;

    public FacetQueryRestObj(String name, List<String> values){
        this.name = name;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }


}