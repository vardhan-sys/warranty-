package com.geaviation.techpubs.models.download;

/**
 * entry to be used for the export of parts data for service bulletin in excel format
 */
public class TechpubsPartDetailsExcelEntry {
    private String partNumber;
    private String partDescription;
    private String quantity;
    private String csn;

    public TechpubsPartDetailsExcelEntry(String partNumber, String partDescription, String quantity, String csn) {
        this.partNumber = partNumber;
        this.partDescription = partDescription;
        this.quantity = quantity;
        this.csn = csn;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getPartDescription() {
        return partDescription;
    }

    public void setPartDescription(String partDescription) {
        this.partDescription = partDescription;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getCsn() { return csn; }

    public void setCsn(String csn) { this.csn = csn; }
}