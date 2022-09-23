package com.geaviation.techpubs.data.api;

import com.geaviation.techpubs.models.DocumentItemAssociatedTPModel;
import java.util.List;
import java.util.Map;

public interface IDocTPData extends IDocMongoData {

    /**
     * Return the related current conference documents for a Technical Publication model and
     * category
     *
     * @param modelList - selected engine model
     * @param tokenList - customer entitlement tokens
     * @return List<DocumentItemAssociatedTPModel> - List of TP conference documents
     */
    List<DocumentItemAssociatedTPModel> getAssociatedDocumentsTPConfPres(List<String> modelList,
        List<String> tokenList,
        Map<String, String> queryParams);

    /**
     * Return the related current conference documents for a Technical Publication model and
     * category
     *
     * @param modelList - selected engine model
     * @param tokenList - customer entitlement tokens
     * @return List<DocumentItemAssociatedTPModel> - List of TP Scorecard documents
     */
    List<DocumentItemAssociatedTPModel> getAssociatedDocumentsTPScorecard(List<String> modelList,
        List<String> tokenList, Map<String, String> queryParams);

    /**
     * Return the related current update documents for a Technical Publication model and category
     *
     * @param modelList - selected engine model
     * @param tokenList - customer entitlement tokens
     * @return List<DocumentItemAssociatedTPModel> - List of TP current update documents
     */
    List<DocumentItemAssociatedTPModel> getAssociatedDocumentsTPUpdates(List<String> modelList,
        List<String> tokenList,
        Map<String, String> queryParams);

    /**
     * Return the related reference material documents for a Technical Publication model and
     * category
     *
     * @param modelList - selected engine model
     * @param tokenList - customer entitlement tokens
     * @return List<DocumentItemAssociatedTPModel> - List of TP reference material documents
     */
    List<DocumentItemAssociatedTPModel> getAssociatedDocumentsTPRefMaterial(List<String> modelList,
        List<String> tokenList, Map<String, String> queryParams);

}
