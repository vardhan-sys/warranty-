package com.geaviation.techpubs.services.api;

import com.geaviation.techpubs.models.BookcaseContentDAO;
import com.geaviation.techpubs.models.SubSystem;

import java.util.List;

public interface IBookcaseContentApp {

    List<BookcaseContentDAO> getBookcaseItems(String portalId, List<String> bookcaseKeys, String bookcaseItemType);
    SubSystem getSubSystem();

}
