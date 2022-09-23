package com.geaviation.techpubs.models.techlib.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.geaviation.techpubs.models.techlib.AirframeLookupEntity;

import javax.xml.bind.annotation.XmlElement;
import java.util.Date;
import java.util.Set;
import java.util.UUID;


public class AvSystemsNotificationDto {
    private String airframe;
    private String docuNumber;
    private String revision;
    private Date revisionDate;
    private String documentDescription;
    private String systemDocumentId;
    private String fileName;

    /**
     *  Place Holder constructor so that code will not break.
     * @param airframe
     * @param docuNumber
     * @param documentDescription
     * @param revision
     * @param fileName
     * @param systemDocumentId
     *
     */
     public AvSystemsNotificationDto(UUID systemDocumentId, String airframe, String docuNumber, String documentDescription, Date revisionDate, String revision, String fileName){
       this.systemDocumentId = systemDocumentId.toString();
       this.airframe = airframe;
       this.docuNumber = docuNumber;
       this.documentDescription = documentDescription;
       this.revisionDate = revisionDate;
       this.revision = revision;
       this.fileName = fileName;

    }


    @XmlElement(name = "airframe")
    @JsonProperty("airframe")
    public String getAirframe() {
        return airframe;
    }

    public void setAirframe(String airframe) {
        this.airframe = airframe;
    }

    @XmlElement(name = "docuNumber")
    @JsonProperty("docuNumber")
    public String getDocuNumber() {
        return docuNumber;
    }
    public void setDocuNumber(String docuNumber) {
        this.docuNumber = docuNumber;
    }

    @XmlElement(name = "revision")
    @JsonProperty("revision")
    public String getRevision() {
        return revision;
    }
    public void setRevision(String revision) {
        this.revision = revision;
    }

    @XmlElement(name = "revisionDate")
    @JsonProperty("revisionDate")
    @JsonFormat(pattern="dd-MMM-yy")
    public Date getRevisionDate() {
        return revisionDate;
    }
    public void setRevisionDate(Date revisionDate) {
        this.revisionDate = revisionDate;
    }

    @XmlElement(name = "fileName")
    @JsonProperty("fileName")
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @XmlElement(name = "documentDescription")
    @JsonProperty("documentDescription")
    public String getDocumentDescription() { return documentDescription; }
    public void setDocumentDescription(String documentDescription) { this.documentDescription = documentDescription; }

    @XmlElement(name = "systemDocumentId")
    @JsonProperty("systemDocumentId")
    public String getSystemDocumentId() { return systemDocumentId; }
    public void setSystemDocumentId(String systemDocumentId) { this.systemDocumentId = systemDocumentId; }
}
