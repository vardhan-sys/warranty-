package com.geaviation.techpubs.models.techlib.response;

import com.geaviation.techpubs.models.techlib.dto.PublisherBookcaseVersionStatusDto;

import java.util.List;

public class PublisherBookcaseVersionsResponse {

    private List<PublisherBookcaseVersionStatusDto> bookcaseVersions;

    public PublisherBookcaseVersionsResponse() { }

    public PublisherBookcaseVersionsResponse(List<PublisherBookcaseVersionStatusDto> bookcaseVersions) {
        this.bookcaseVersions = bookcaseVersions;
    }

    public List<PublisherBookcaseVersionStatusDto> getBookcaseVersions() { return bookcaseVersions; }

    public void setBookcaseVersions(List<PublisherBookcaseVersionStatusDto> bookcaseVersions) { this.bookcaseVersions = bookcaseVersions; }
}
