package com.geaviation.techpubs.services.impl;

import com.geaviation.techpubs.data.impl.BookcaseDataImpl;
import com.geaviation.techpubs.data.impl.BookcaseTOCData;
import com.geaviation.techpubs.models.BookcaseTocModel;
import com.geaviation.techpubs.models.Response;
import com.geaviation.techpubs.models.techlib.dto.BookcaseVersionStatusDTO;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BookcaseTOCApp {

    @Autowired
    private BookcaseTOCData bookcaseTOCData;

    @Autowired
    private BookcaseDataImpl bookcaseDataImpl;

    private static final Logger log = LogManager.getLogger(BookcaseTOCApp.class);

    public BookcaseTocModel getBookcaseTOC(String bookcaseKey, String bookKey, Boolean limit, String version) {
        BookcaseTocModel bookcaseTocModel;
        try {
            bookcaseTocModel = limit ? new BookcaseTocModel(bookcaseTOCData.getBooks(bookcaseKey, version)) :
                    new BookcaseTocModel(bookcaseTOCData.getBookData(bookcaseKey, bookKey, version));
            bookcaseTocModel.setSuccess(true);
        } catch (Exception e){
            bookcaseTocModel = new BookcaseTocModel();
            log.error("Could not retrieve bookcase TOC: ", e);
        }
        return bookcaseTocModel;
    }

    public List<BookcaseVersionStatusDTO> getBookcaseVersionStatuses(String bookcaseKey) {
        return bookcaseDataImpl.getBookcaseVersionStatuses(bookcaseKey);
    }

    public void publishPageblkDocument(String bookcase, String book, String bookType, String version, String fileName,
                                       String pageblkKey, boolean emailNotification) {
        try {
            bookcaseTOCData.publishPageblkDocument(bookcase, book, bookType, version, fileName, pageblkKey, emailNotification);
        } catch (Exception e) {
            log.error(String.format("Could not publish bookcase %s book %s pageBlockKey %s", bookcase, book, pageblkKey), e);
        }
    }
}
