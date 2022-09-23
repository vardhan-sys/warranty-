package com.geaviation.techpubs.models.download.search;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonSetter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Data structure for holding requests from the UI
 *
 * Rest object for client requests or responses.
 * This should stay a POJO, put any logic in Model layer or somewhere else
 */
public class PsvcSearchRequestRestObj implements Serializable{
    private static final long serialVersionUID = -5991924227638785782L;

    /* The following properties are documented in the individual getter/setter methods */

    @NotNull
    private String module = "documents";
    @NotNull
    private String searchText;
    private List<FacetQueryRestObj> facetQueryRestObjs;
    private transient SortField sortField = new SortField("title", SortField.PSVC_ORDER.ASC);
    @Min(0)
    private int start = 0;
    @Min(0)
    private int saStart = 0;
    @Max(3000)
    @Min(1)
    private int limit = 100;
    private int sEcho = 0; // this field is a requirement of a particular UI implementation.

    public PsvcSearchRequestRestObj(){}

    public PsvcSearchRequestRestObj(String searchText, List<FacetQueryRestObj> facetQueryRestObjs) {
        this.searchText = searchText;
        this.facetQueryRestObjs = facetQueryRestObjs;
    }

    /**
     * @return the text that the user is searching for
     */
    public String getSearchText() {
        return searchText;
    }

    /**
     * Set the text that the user is searching for
     *
     * @param searchText
     */
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    /**
     * @return the name of the module that should be searched
     */
    public String getModule() {
        return module;
    }

    /**
     * Set the name of the module that should be searched
     *
     * @param module
     */
    public void setModule(String module) {
        this.module = module;
    }

    /**
     * @return the 0-indexed GE search result number that the results should start with
     */
    public int getStart() {
        return start;
    }

    /**
     * Set the 0-indexed GE search result number that the results should start with.
     *
     * @param start
     */
    public void setStart(String start) {
        this.start = Integer.parseInt(start);
    }

    /**
     * @return the 0-indexed Safran search result number that the results should start with
     */
    public int getSaStart() {
        return saStart;
    }

    /**
     * Set the 0-indexed Safran search result number that the results should start with
     *
     * @param saStart
     */
    public void setSaStart(String saStart) {
        this.saStart = Integer.parseInt(saStart);
    }

    /**
     * @return the total number of search results being requested
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Set the total number of search results that should be returned
     *
     * @param limit
     */
    public void setLimit(String limit) {
        this.limit = Integer.parseInt(limit);
    }

    /**
     * Set the information about the Facet query or queries that the user wishes to execute as a
     * {@link FacetQueryRestObj} object
     *
     * @param facetQueries
     */
    public void setFacetQueries(List<FacetQueryRestObj> facetQueries) {
        this.facetQueryRestObjs = facetQueries;
    }

    /**
     * @return the information about the Facet query or queries that the user wishes to execute as a
     * {@link FacetQueryRestObj} object
     */
    public List<FacetQueryRestObj> getFacetQueries() {
        return facetQueryRestObjs;
    }

    /**
     * @return the information about how the user wishes the results to be sorted as a {@link SortField}
     */
    public SortField getSortField() {
        return sortField;
    }

    /**
     * Set the information about how the user wishes the results to be sorted as a {@link SortField}
     *
     * @param sortField
     */
    public void setSortField(SortField sortField) {
        this.sortField = sortField;
    }

    public int getSEcho() {
        return sEcho;
    }

    @JsonSetter("sEcho") // jackson needs explicit mapping if second letter of prop is capital
    public void setSEcho(String sEcho) {
        if (sEcho != null) {
            this.sEcho = Integer.parseInt(sEcho);
        }
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("{ \"facetQueries\": [");
        for (int i=0; i < facetQueryRestObjs.size(); i++) {
                builder.append("{\"name\": \""+facetQueryRestObjs.get(i).getName() + "\", \"values\": [");
                for(int x=0; x < facetQueryRestObjs.get(i).getValues().size(); x++) {
                    if (x == facetQueryRestObjs.get(i).getValues().size() -1) {
                        builder.append("\"" + facetQueryRestObjs.get(i).getValues().get(x) + "\"]}\n");
                    } else {
                        builder.append("\"" + facetQueryRestObjs.get(i).getValues().get(x) + "\",");
                    }
                }
                if (i != facetQueryRestObjs.size() - 1) {
                    builder.append(",");
                }
        }
        builder.append("],");
        builder.append("\"limit\": " + limit + ",");
        builder.append("\"module\": \"documents\",");
        builder.append("\"start\": " + start + ",");
        builder.append("\"searchText\": \"" + searchText + "\",");
        builder.append("\"sortField\": {\"field\":\"" + sortField.getField() + "\"," +
                "\"order\": \"" + sortField.getOrder() + "\"}}");

        return builder.toString();
    }

    public String toEscapedString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{ \"facetQueries\": [");
        for (int i=0; i < facetQueryRestObjs.size(); i++) {
            builder.append("{\"name\": \""+facetQueryRestObjs.get(i).getName() + "\", \"values\": [");
            for(int x=0; x < facetQueryRestObjs.get(i).getValues().size(); x++) {
                if (x == facetQueryRestObjs.get(i).getValues().size() -1) {
                    builder.append("\"" + facetQueryRestObjs.get(i).getValues().get(x) + "\"]}\n");
                } else {
                    builder.append("\"" + facetQueryRestObjs.get(i).getValues().get(x) + "\",");
                }
            }
            if (i != facetQueryRestObjs.size() - 1) {
                builder.append(",");
            }
        }
        builder.append("],");
        builder.append("\"limit\": " + limit + ",");
        builder.append("\"module\": \"documents\",");
        builder.append("\"start\": " + start + ",");
        // If searchText contains inner quotes, they need to be double escaped
        String escapedSearchText = searchText;
        if (searchText.startsWith("\"") && searchText.endsWith("\"")) {
            escapedSearchText = searchText.replace("\"", "\\\"");
        }
        builder.append("\"searchText\": \"" + escapedSearchText + "\",");
        builder.append("\"sortField\": {\"field\":\"" + sortField.getField() + "\"," +
                "\"order\": \"" + sortField.getOrder() + "\"}}");

        return builder.toString();
    }
}

