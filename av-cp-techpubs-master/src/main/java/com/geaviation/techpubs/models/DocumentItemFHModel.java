package com.geaviation.techpubs.models;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlType(name = "fh")
public class DocumentItemFHModel extends DocumentItemModel {

    private static final long serialVersionUID = 1L;
    private String program;
    private String programtitle;
    private String year;
    private String monthQuarter;
    private String monthQuarterDisplay;

    public DocumentItemFHModel() {
        super.setType("FH");
    }

    @Override
    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    @XmlTransient
    @JsonIgnore
    @Override
    public String getProgramdocnbr() {
        return "";
    }

    @Override
    public String getProgramtitle() {
        return programtitle;
    }

    public void setProgramtitle(String programtitle) {
        this.programtitle = programtitle;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @XmlTransient
    @JsonIgnore
    public String getMonthQuarter() {
        return monthQuarter;
    }

    public void setMonthQuarter(String monthQuarter) {
        this.monthQuarter = monthQuarter;
    }

    public String getMonthQuarterDisplay() {
        return monthQuarterDisplay;
    }

    public void setMonthQuarterDisplay(String monthQuarterDisplay) {
        this.monthQuarterDisplay = monthQuarterDisplay;
    }
}
