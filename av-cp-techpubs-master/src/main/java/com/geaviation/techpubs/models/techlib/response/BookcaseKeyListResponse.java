package com.geaviation.techpubs.models.techlib.response;

import java.util.List;

public class BookcaseKeyListResponse {

    List<String> bookcaseKeys;

    public BookcaseKeyListResponse() { }

    public BookcaseKeyListResponse(List<String> bookcaseKeys) { this.bookcaseKeys = bookcaseKeys; }

    public List<String> getBookcaseKeys() { return bookcaseKeys; }

    public void setBookcaseKeys(List<String> bookcaseKeys) { this.bookcaseKeys = bookcaseKeys; }
}
