package com.geaviation.techpubs.models;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement(name = "contentresponse")
public class TocNodeModel extends Response {

    private static final long serialVersionUID = 1L;
    private List<TocItemNodeModel> tocItemNodeList;

    @XmlElementWrapper(name = "objects")
    @XmlElement(name = "node")
    @JsonProperty("objects")
    public List<TocItemNodeModel> getTocItemNodeList() {
        return tocItemNodeList;
    }

    public void setTocItemNodeList(List<TocItemNodeModel> tocItemNodeList) {
        this.tocItemNodeList = tocItemNodeList;
    }
}