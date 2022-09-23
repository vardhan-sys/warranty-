package com.geaviation.techpubs.models.download.search;

/**
 * Object for storing data pertaining to what the SortField is called and to what data field it
 * corresponds to
 */
public class SortField {
    private String field;
    private PSVC_ORDER order;

    public enum PSVC_ORDER {
        DESC("desc"),
        ASC("asc");

        private String name;

        PSVC_ORDER(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public PSVC_ORDER reverse() {
            return (this == ASC) ? DESC : ASC;
        }
    }

    public SortField(){}
    public SortField(String fieldName, PSVC_ORDER sortOrder){
        this.field = fieldName;
        this.order = sortOrder;
    }

    public boolean matchKeywordedName(String fieldToCompare) {
        return field.equals(fieldToCompare + ".keyword");
    }

    public boolean matchName(String fieldToCompare) {
        return field.equals(fieldToCompare);
    }

    /**
     * Getters and setters
     */
    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public PSVC_ORDER getOrder() {
        return order;
    }

    public void setPsvcOrder(PSVC_ORDER order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "SortField [field=" + field + ", order=" + order + "]";
    }
}
