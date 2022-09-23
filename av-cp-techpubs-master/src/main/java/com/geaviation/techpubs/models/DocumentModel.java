package com.geaviation.techpubs.models;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement(name = "documentresponse")
public class DocumentModel extends Response {

    private static final long serialVersionUID = 1218919241981840176L;
    private List<DocumentItemModel> documentItemList;

    //added for DVDDownloadList service
    private List<DVDInfoResponse> dvdList;

    @XmlElementWrapper(name = "objects")
    @XmlElement(name = "document")
    @JsonProperty("objects")
    public List<DocumentItemModel> getDocumentItemList() {
        return documentItemList;
    }

    public void setDocumentItemList(List<DocumentItemModel> documentItemList) {
        this.documentItemList = documentItemList;
    }

    //added for DVDDownloadList service
    public List<DVDInfoResponse> getDvdList() {
        return dvdList;
    }

    public void setDvdList(List<DVDInfoResponse> dvdList) {
        this.dvdList = dvdList;
    }
}
