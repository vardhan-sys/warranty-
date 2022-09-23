package com.geaviation.techpubs.models.techlib.response;

import com.geaviation.techpubs.models.techlib.dto.BookcaseWithOnlineVersionDto;

import java.util.List;

public class PublisherSummaryResponse {

    private List<BookcaseWithOnlineVersionDto> bookcases;

    public PublisherSummaryResponse() { }

    public PublisherSummaryResponse(List<BookcaseWithOnlineVersionDto> bookcases) {
        this.bookcases = bookcases;
    }

    public List<BookcaseWithOnlineVersionDto> getBookcases() { return bookcases; }

    public void setBookcases(List<BookcaseWithOnlineVersionDto> bookcases) { this.bookcases = bookcases; }
}
