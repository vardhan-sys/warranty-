package com.geaviation.techpubs.services.impl;

import com.geaviation.techpubs.data.api.techlib.IBookcaseVersionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("BookcaseVersionApp")
public class BookcaseVersionApp {

    @Autowired
    private IBookcaseVersionData iBookcaseVersionData;

    /**
     * @param bookcaseKey
     * @return the online version of the bookcase
     */
    public String getOnlineBookcaseVersion(String bookcaseKey) {
        return iBookcaseVersionData.findOnlineBookcaseVersion(bookcaseKey);
    }
}
