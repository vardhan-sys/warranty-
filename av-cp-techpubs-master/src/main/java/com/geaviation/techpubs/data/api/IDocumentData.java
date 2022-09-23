package com.geaviation.techpubs.data.api;

import com.geaviation.techpubs.models.DocumentItemModel;
import com.geaviation.techpubs.models.ProgramItemModel;
import java.util.List;
import java.util.Map;

public interface IDocumentData {

    List<DocumentItemModel> getCatalogFileDocuments(ProgramItemModel programItem,
        String downloadtype,
        Map<String, String> criteriaMap);

    List<DocumentItemModel> getCatalogDocuments(ProgramItemModel programItem, String downloadtype,
        Map<String, String> criteriaMap);

}
