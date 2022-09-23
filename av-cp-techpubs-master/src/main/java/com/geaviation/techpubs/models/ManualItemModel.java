package com.geaviation.techpubs.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement
public class ManualItemModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private ProgramItemModel programItem;
    private String manualdocnbr;
    private String title;
    private String revisiondate;
    private String revisionnumber;
    private String multibrowser;
    private String onlineVersion;

    private static final String[] gekList = new String[]{"gek108738", "gek108739", "gek108740",
        "oebs", "sei-580",
        "sei-582", "sei181", "sei182", "sei183", "sei185", "sei197", "sei256", "sei409", "sei444",
        "sei445",
        "sei446", "sei447", "sei448", "sei569", "sei578", "sei584", "sei694", "sei695", "sei696",
        "sei868"};
    // Temporary list for excluding printing
    private static final List<String> EXCLUDEPRINT = new ArrayList<>(Arrays.asList(gekList));

    @JsonIgnore
    @XmlTransient
    public ProgramItemModel getProgramItem() {
        return this.programItem;
    }

    public String getProgramtitle() {
        return (this.programItem != null ? this.programItem.getTitle() : null);
    }

    public String getProgramdocnbr() {
        return (this.programItem != null ? this.programItem.getProgramDocnbr() : null);
    }

    public void setProgramItem(ProgramItemModel programItem) {
        this.programItem = programItem;
    }

    @JsonProperty("manualdocnbr")
    @XmlElement(name = "manualdocnbr")
    public String getManualDocnbr() {
        return this.manualdocnbr;
    }

    public void setManualdocnbr(String manualdocnbr) {
        this.manualdocnbr = manualdocnbr;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRevisiondate() {
        return this.revisiondate;
    }

    public void setRevisiondate(String revisiondate) {
        this.revisiondate = revisiondate;
    }

    public String getRevisionnumber() {
        return this.revisionnumber;
    }

    public void setRevisionnumber(String revisionnumber) {
        this.revisionnumber = revisionnumber;
    }

    public boolean isPrintable() {
        return (!EXCLUDEPRINT.contains(this.manualdocnbr));
    }

    public String getMultibrowser() {
        return this.multibrowser;
    }

    public void setMultibrowser(String multibrowser) {
        this.multibrowser = multibrowser;
    }

    public String getOnlineVersion() {
        return onlineVersion;
    }

    public void setOnlineVersion(String onlineVersion) {
        this.onlineVersion = onlineVersion;
    }
}
