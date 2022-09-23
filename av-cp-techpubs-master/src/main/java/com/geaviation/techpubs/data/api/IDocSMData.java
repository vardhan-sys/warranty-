package com.geaviation.techpubs.data.api;

import com.geaviation.techpubs.models.DocumentItemAssociatedSMModel;
import java.util.List;
import java.util.Map;

/**
 * The Interface IDocSMData.
 */
public interface IDocSMData extends IDocMongoData {

    /**
     * Gets the associated documents SM.
     *
     * @param modelList the model list
     * @param tokenList the token list
     * @param category the category
     * @param queryParams the query params
     * @return the associated documents SM
     */
    List<DocumentItemAssociatedSMModel> getAssociatedDocumentsSM(List<String> modelList,
        List<String> tokenList,
        String category, Map<String, String> queryParams);
}
