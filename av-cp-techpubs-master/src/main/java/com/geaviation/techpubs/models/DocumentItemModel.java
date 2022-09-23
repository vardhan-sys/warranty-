package com.geaviation.techpubs.models;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlRootElement
@XmlSeeAlso({DocumentItemCMMModel.class, DocumentItemAOWModel.class, DocumentItemLLModel.class,
    DocumentItemAssociatedCMMModel.class, DocumentItemWSPGModel.class, DocumentItemFHModel.class,
    DocumentItemSMModel.class, DocumentItemVIModel.class, DocumentItemLLModel.class})
public class DocumentItemModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private String type;
    private String title;
    private String resourceUri;
    private String groupName;
    private ProgramItemModel programItem;
    private String fileSize;

    private static final String MULTI_BROWSER = "Y";

    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }

    public String getGroupName() {
        return this.groupName == null ? "" : this.groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getTitle() {
        return (this.title != null ? this.title : "");
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    @XmlTransient
    @JsonIgnore
    public ProgramItemModel getProgramItem() {
        return this.programItem;
    }

    public String getProgramtitle() {
        return (this.programItem != null ? this.programItem.getTitle() : null);
    }

    public String getProgram() {
        return (this.programItem != null ? this.programItem.getProgramDocnbr() : null);
    }

    public void setProgramItem(ProgramItemModel programItem) {
        this.programItem = programItem;
    }

    public String getProgramdocnbr() {
        return this.getProgram();
    }

    public String getMultibrowser() {
        return MULTI_BROWSER;
    }

}
