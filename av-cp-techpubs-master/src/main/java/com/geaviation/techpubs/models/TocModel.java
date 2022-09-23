package com.geaviation.techpubs.models;

import java.util.ArrayList;
import java.util.List;

public class TocModel {

    private String programDocnbr;
    private String manualDocnbr;
    private String title;
    private List<TocDocModel> tocDocList;

    public String getProgramDocnbr() {
        return programDocnbr;
    }

    public void setProgramDocnbr(String programDocnbr) {
        this.programDocnbr = programDocnbr;
    }

    public String getManualDocnbr() {
        return manualDocnbr;
    }

    public void setManualDocNbr(String manualDocnbr) {
        this.manualDocnbr = manualDocnbr;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<TocDocModel> getTocDocList() {
        if (this.tocDocList == null) {
            this.tocDocList = new ArrayList<>();
        }
        return tocDocList;
    }

    public void setTocDocList(List<TocDocModel> tocDocList) {
        this.tocDocList = tocDocList;
    }
}