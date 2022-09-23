package com.geaviation.techpubs.models.reviewer;

public class PublishPageblkRequest {

    private String key;
    private String publicationTypeCode;
    private boolean emailNotification;
    private String resourceUri;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPublicationTypeCode() {
        return publicationTypeCode;
    }

    public void setPublicationTypeCode(String publicationTypeCode) {
        this.publicationTypeCode = publicationTypeCode;
    }

    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }

    public boolean isEmailNotification() {
        return emailNotification;
    }

    public void setEmailNotification(boolean emailNotification) {
        this.emailNotification = emailNotification;
    }
}
