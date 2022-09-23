package com.geaviation.techpubs.data.impl;

import com.geaviation.techpubs.data.api.IProgramData;
import com.geaviation.techpubs.data.api.techlib.IBookData;
import com.geaviation.techpubs.data.api.techlib.IBookcaseVersionData;
import com.geaviation.techpubs.models.BookDAO;
import com.geaviation.techpubs.models.BookcaseContentDAO;
import com.geaviation.techpubs.models.TocModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class BookBookcaseService {

    @Autowired
    private IProgramData programDataSvc;

    @Autowired
    private IBookData bookData;

    @Autowired
    private IBookcaseVersionData iBookcaseVersionData;

    public List<BookcaseContentDAO> getBooksByBookcaseKey(List<String> bookcaseKeyList) {
       List<BookcaseContentDAO> bookList = new ArrayList<>();

       for (String bookcaseKey : bookcaseKeyList) {
           bookList.addAll(getBooksByBookcaseKey(bookcaseKey));
       }

       return bookList;
    }

    public List<BookDAO> getBooksByBookcaseKey(String bookcaseKey) {
        //retrieve the online version of the bookcase
        String version = iBookcaseVersionData.findOnlineBookcaseVersion(bookcaseKey);

        List<BookDAO> bookcaseContentDAOList = bookData.findByBookcaseKeyAndVersion(bookcaseKey, version);

        if (!CollectionUtils.isEmpty(bookcaseContentDAOList))
            bookcaseContentDAOList.addAll(getBooksFromTPSDB(bookcaseKey, bookcaseContentDAOList.get(0).getBookcasetitle()));

        return bookcaseContentDAOList;
    }

    private List<BookDAO> getBooksFromTPSDB(String bookcaseKey, String bookcaseTitle) {
        List<BookDAO> booksFromTPS = new ArrayList<>();

        for (TocModel tocModel : programDataSvc.getTocsByProgram(bookcaseKey)) {
            BookDAO bookFromTPS = new BookDAO();
            bookFromTPS.setTitle(tocModel.getTitle());
            bookFromTPS.setBookcaseKey(bookcaseKey);
            bookFromTPS.setBookcasetitle(bookcaseTitle);

            booksFromTPS.add(bookFromTPS);
        }

        return booksFromTPS;
    }
}
