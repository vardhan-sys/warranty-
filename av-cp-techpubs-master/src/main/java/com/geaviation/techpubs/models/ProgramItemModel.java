package com.geaviation.techpubs.models;

import static com.geaviation.techpubs.services.util.AppConstants.PROGRAM;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.lucene.search.IndexSearcher;
import org.dom4j.Element;

@XmlRootElement
public class ProgramItemModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String programDocnbr;
    private List<String> parentProgramList;
    private String title;
    private SubSystem subSystem;
    // Fields for TD Subsystem
    private String dvdVersion;
    private String dvdSbModel;
    private String dvdInfoTxt;
    private String programOnlineVersion;
    private String repositoryBase;
    private String lrProgramDocnbr;
    private boolean licensedProgram = false;
    private transient Element tocRoot;
    private transient IndexSearcher targetSearcher;
    private transient Map<String, Map<String, String>> downloadTypeMap;

    public ProgramItemModel() {
        this("");
    }

    public ProgramItemModel(String programDocnbr) {
        this.programDocnbr = programDocnbr;
    }

    @XmlElement(name = "programDocnbr")
    public String getProgramDocnbr() {
        return this.programDocnbr;
    }

    public List<String> getParentProgramList() {
        return this.parentProgramList;
    }

    public void setParentProgramList(List<String> parentProgramList) {
        this.parentProgramList = parentProgramList;
    }

    public String getTitle() {
        return (this.title != null ? this.title : this.programDocnbr);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SubSystem getSubSystem() {
        return this.subSystem;
    }

    public void setSubSystem(SubSystem subSystem) {
        this.subSystem = subSystem;
    }

    public String getDvdVersion() {
        return this.dvdVersion;
    }

    public void setDvdVersion(String dvdVersion) {
        this.dvdVersion = dvdVersion;
    }

    @XmlTransient
    @JsonIgnore
    public String getDvdSbModel() {
        return this.dvdSbModel;
    }

    public void setDvdSbModel(String dvdSbModel) {
        this.dvdSbModel = dvdSbModel;
    }

    @XmlTransient
    @JsonIgnore
    public String getDvdInfoTxt() {
        return (this.dvdInfoTxt == null ? getDvdSbModel() : this.dvdInfoTxt);
    }

    public void setDvdInfoTxt(String dvdInfoTxt) {
        this.dvdInfoTxt = dvdInfoTxt;
    }

    public String getProgramOnlineVersion() {
        return this.programOnlineVersion;
    }

    public void setProgramOnlineVersion(String programOnlineVersion) {
        this.programOnlineVersion = programOnlineVersion;
    }

    public void setRepositoryBase(String repositoryBase) {
        this.repositoryBase = repositoryBase;
    }

    @XmlTransient
    @JsonIgnore
    public String getLrProgramDocnbr() {
        return this.lrProgramDocnbr;
    }

    public void setLrProgramDocnbr(String lrProgramDocnbr) {
        this.lrProgramDocnbr = lrProgramDocnbr;
    }

    @XmlTransient
    @JsonIgnore
    public String getProgramLocation() {
        return (new File(this.repositoryBase, this.programDocnbr)).getAbsolutePath();
    }

    @XmlTransient
    @JsonIgnore
    public String getProgramOnlineVersionLocation() {
        return (new File(getProgramLocation(), this.programOnlineVersion)).getAbsolutePath();
    }

    @XmlTransient
    @JsonIgnore
    @Deprecated
    public String getProgramOnlineTargetLocation() {
        File targetDir = new File(new File(
            new File(new File(new File(this.repositoryBase, "targetshadow"), this.programDocnbr),
                "targetlive"),
            this.programOnlineVersion), "targetindex");
        return targetDir.getAbsolutePath();
    }

    @XmlTransient
    @JsonIgnore
    @Deprecated
    public String getProgramProgramLocation() {
        return (new File(getProgramLocation(), PROGRAM)).getAbsolutePath();
    }

    public boolean getLicensedProgram() {
        return licensedProgram;
    }

    public void setLicensedProgram(boolean licensedProgram) {
        this.licensedProgram = licensedProgram;
    }

    @XmlTransient
    @JsonIgnore
    public Element getTocRoot() {
        return this.tocRoot;
    }

    public void setTocRoot(Element tocRoot) {
        this.tocRoot = tocRoot;
    }

    @XmlTransient
    @JsonIgnore
    public IndexSearcher getTargetSearcher() {
        return this.targetSearcher;
    }

    public void setTargetSearcher(IndexSearcher targetSearcher) {
        this.targetSearcher = targetSearcher;
    }

    @XmlTransient
    @JsonIgnore
    public Map<String, Map<String, String>> getDownloadTypeMap() {
        return downloadTypeMap;
    }

    public void setDownloadTypeMap(Map<String, Map<String, String>> downloadTypeMap) {
        this.downloadTypeMap = downloadTypeMap;
    }

    @Override
    public boolean equals(Object other) {
        boolean result = false;
        if (other instanceof ProgramItemModel) {
            ProgramItemModel rhs = (ProgramItemModel) other;
            if (other == this) {
                result = true;
            } else {
                result = (this.getProgramDocnbr() == rhs.getProgramDocnbr()
                    || (this.getProgramDocnbr() != null && this.getProgramDocnbr()
                    .equals(rhs.getProgramDocnbr())));
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        return ((this.getProgramDocnbr() == null) ? 0 : this.getProgramDocnbr().hashCode());
    }
}
