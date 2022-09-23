package com.geaviation.techpubs.models.download.search;

import java.util.List;

/**
 * entry to be used for the export of data for the techpubs search widget in the form of an excel sheet
 */
public class TechpubsSearchExcelEntry {
    private String title;
    private String type;
    private List<String> engineFamily;
    private List<String> engineModels;
    private String date;
    private String category;
    private String details;
    public TechpubsSearchExcelEntry(String title, String type, List<String> engineFamily,
                                    List<String> engineModels, String date, String category, String details) {
        this.title = title;
        this.type = type;
        this.engineFamily = engineFamily;
        this.engineModels = engineModels;
        this.date = date;
        this.category = category;
        this.details = details;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getEngineFamily() {
        return engineFamily;
    }

    public void setEngineFamily(List<String> engineFamily) {
        this.engineFamily = engineFamily;
    }

    public List<String> getEngineModels() {
        return engineModels;
    }

    public void setEngineModels(List<String> engineModels) {
        this.engineModels = engineModels;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}