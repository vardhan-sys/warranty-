package com.geaviation.techpubs.models;

import java.util.Collection;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement(name = "contentresponse")
public class BookcaseTocModel extends Response {

    private Collection<BookcaseTocDAO> bookcaseTOCItemList;

    public BookcaseTocModel() {
    }

    public BookcaseTocModel(Collection<BookcaseTocDAO> bookcaseTocDAOS) {
        this.bookcaseTOCItemList = bookcaseTocDAOS;
    }


    @XmlElementWrapper(name = "objects")
    @JsonProperty("objects")
    public Collection<BookcaseTocDAO> getBookcaseTOCItemList() {
        return bookcaseTOCItemList;
    }
}

