package com.geaviation.techpubs.models;

import static com.geaviation.techpubs.services.util.AppConstants.PROGRAM;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement(name = "programresponse")
public class ProgramModel extends Response {

    private static final long serialVersionUID = 1218919241981840176L;
    private List<ProgramItemModel> programItemList;

    @XmlElementWrapper(name = "objects")
    @XmlElement(name = PROGRAM)
    @JsonProperty("objects")
    public List<ProgramItemModel> getProgramItemList() {
        return programItemList;
    }

    public void setProgramItemList(List<ProgramItemModel> programItemList) {
        this.programItemList = programItemList;
    }
}
