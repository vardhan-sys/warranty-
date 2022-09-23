package com.geaviation.techpubs.models;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlRootElement(name = "downloadresponse")
public class DocumentDownloadModel extends Response {

    private static final long serialVersionUID = 1L;
    private String zipFilename;
    private byte[] zipFileByteArray;

    @XmlTransient
    @JsonIgnore
    public String getZipFilename() {
        return zipFilename;
    }

    public void setZipFilename(String zipFilename) {
        this.zipFilename = zipFilename;
    }

    @XmlTransient
    @JsonIgnore
    public byte[] getZipFileByteArray() {
        return zipFileByteArray;
    }

    public void setZipFileByteArray(byte[] zipFileByteArray) {
        this.zipFileByteArray = zipFileByteArray;
    }
}
