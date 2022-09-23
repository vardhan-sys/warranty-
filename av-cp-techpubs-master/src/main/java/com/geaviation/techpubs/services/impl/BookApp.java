package com.geaviation.techpubs.services.impl;

import com.geaviation.techpubs.data.impl.BookBookcaseService;
import com.geaviation.techpubs.models.BookcaseContentDAO;
import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.services.api.IBookcaseContentApp;
import com.geaviation.techpubs.services.util.AppConstants;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BookApp implements IBookcaseContentApp {
    @Autowired
    private BookBookcaseService bookBookcaseService;

    @Value("${bookcase.spm.ge}")
    private String geSpmBookcaseKey;

    @Value("${bookcase.spm.honda}")
    private String hondaSpmBookcaseKey;

    @Override
    public SubSystem getSubSystem() {
        return SubSystem.BOOK;
    }

    @Override
    public List<BookcaseContentDAO> getBookcaseItems(String portalId, List<String> bookcaseKeys, String bookcaseItemType) {
        List<BookcaseContentDAO> bookList = bookBookcaseService.getBooksByBookcaseKey(bookcaseKeys);

        if (!bookList.isEmpty()) {
            if (!bookcaseKeys.contains(geSpmBookcaseKey))
                bookList.addAll(bookBookcaseService.getBooksByBookcaseKey(geSpmBookcaseKey));

            if (portalId.equalsIgnoreCase(AppConstants.GEHONDA) && !bookcaseKeys.contains(hondaSpmBookcaseKey))
                bookList.addAll(bookBookcaseService.getBooksByBookcaseKey(hondaSpmBookcaseKey));
        }

        return bookList;
    }
}
