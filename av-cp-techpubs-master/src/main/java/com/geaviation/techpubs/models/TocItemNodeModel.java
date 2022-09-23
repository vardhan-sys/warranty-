package com.geaviation.techpubs.models;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlRootElement
public class TocItemNodeModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private ProgramItemModel programItem;
    private String type;
    private String doctype;
    private String summaryUri;
    private String revdate;
    private String title;
    private String toctitle;
    private String resourceUri;
    private String children;
    private String multibrowser = "N";

    @XmlTransient
    @JsonIgnore
    public ProgramItemModel getProgramItem() {
        return this.programItem;
    }

    public void setProgramItem(ProgramItemModel programItem) {
        this.programItem = programItem;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDoctype() {
        return this.doctype;
    }

    public void setDoctype(String doctype) {
        this.doctype = doctype;
    }

    public String getSummaryUri() {
        return this.summaryUri;
    }

    public void setSummaryUri(String summaryUri) {
        this.summaryUri = summaryUri;
    }

    public String getRevdate() {
        return this.revdate == null ? "" : this.revdate;
    }

    public void setRevdate(String revdate) {
        this.revdate = revdate;
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

    public String getResourceUri() {
        return this.resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }

    public String getChildren() {
        return this.children;
    }

    public void setChildren(String children) {
        this.children = children;
    }

    public String getMultibrowser() {
        return this.multibrowser;
    }

    public void setMultibrowser(String multibrowser) {
        this.multibrowser = multibrowser;
    }
}
