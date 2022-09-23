package com.geaviation.techpubs.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
public class TocItemModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private ProgramItemModel programItem;
    private String manualdocnbr;
    private String manualtitle;
    private String manualrevdate;
    private String title;
    private String toctitle;
    private String nodeid;

    @XmlTransient
    @JsonIgnore
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

    public String getManualdocnbr() {
        return this.manualdocnbr;
    }

    public void setManualdocnbr(String manualdocnbr) {
        this.manualdocnbr = manualdocnbr;
    }

    public String getManualtitle() {
        return this.manualtitle;
    }

    public void setManualtitle(String manualtitle) {
        this.manualtitle = manualtitle;
    }

    public String getManualrevdate() {
        return this.manualrevdate;
    }

    public void setManualrevdate(String manualrevdate) {
        this.manualrevdate = manualrevdate;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getToctitle() {
        return this.toctitle;
    }

    public void setToctitle(String toctitle) {
        this.toctitle = toctitle;
    }

    public String getNodeid() {
        return this.nodeid;
    }

    public void setId(String nodeid) {
        this.nodeid = nodeid;
    }
}
