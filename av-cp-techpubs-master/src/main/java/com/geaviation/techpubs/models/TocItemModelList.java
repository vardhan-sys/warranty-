package com.geaviation.techpubs.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "contentresponse")
public class TocItemModelList extends Response {

    private static final long serialVersionUID = 1218919241981840176L;
    private List<TocItemModel> tocItemList;

    public String getProgramdocnbr() {
        return isNotNullandEmpty(tocItemList) ? tocItemList.get(0).getProgramdocnbr() : null;
    }

    public String getProgramtitle() {
        return isNotNullandEmpty(tocItemList) ? tocItemList.get(0).getProgramtitle() : null;
    }

    public String getManualdocnbr() {
        return isNotNullandEmpty(tocItemList) ? tocItemList.get(0).getManualdocnbr() : null;
    }

    public String getManualtitle() {
        return isNotNullandEmpty(tocItemList) ? tocItemList.get(0).getManualtitle() : null;
    }

    public String getManualrevdate() {
        return isNotNullandEmpty(tocItemList) ? tocItemList.get(0).getManualrevdate() : null;
    }

    @XmlElementWrapper(name = "objects")
    @XmlElement(name = "toc")
    @JsonProperty("objects")
    public List<TocItemModel> getTocItemList() {
        return tocItemList;
    }

    public void setTocItemList(List<TocItemModel> tocItemList) {
        this.tocItemList = tocItemList;
    }

    public boolean isNotNullandEmpty(List<TocItemModel> tocItemList2) {
        boolean isValid = false;
        if (tocItemList2 != null && !tocItemList2.isEmpty()) {
            isValid = true;
        }
        return isValid;
    }
}
