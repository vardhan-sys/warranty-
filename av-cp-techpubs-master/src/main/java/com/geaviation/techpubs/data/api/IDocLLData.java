package com.geaviation.techpubs.data.api;

import com.geaviation.techpubs.models.DocumentItemAssociatedLLModel;
import java.util.List;
import java.util.Map;

public interface IDocLLData extends IDocMongoData {

    /**
     * Return the related current conference documents for a Lessor Library
     *
     * @param queryParams - query parameters
     * @param tokenList - customer entitlement tokens
     * @return List<DocumentItemAssociatedLLModel> - List of LL conference documents
     */
    List<DocumentItemAssociatedLLModel> getAssociatedDocumentsLLConfPres(List<String> tokenList,
        Map<String, String> queryParams);

    /**
     * Return the related current update documents for a Lessor Library
     *
     * @param queryParams - query parameters
     * @param tokenList - customer entitlement tokens
     * @return List<DocumentItemAssociatedLLModel> - List of LL current update documents
     */
    List<DocumentItemAssociatedLLModel> getAssociatedDocumentsLLUpdates(List<String> tokenList,
        Map<String, String> queryParams);

    /**
     * Return the related reference material documents for a Lessor Library
     *
     * @param queryParams - query parameters
     * @param tokenList - customer entitlement tokens
     * @return List<DocumentItemAssociatedLLModel> - List of LL reference material documents
     */
    List<DocumentItemAssociatedLLModel> getAssociatedDocumentsLLRefMaterial(List<String> tokenList,
        Map<String, String> queryParams);

}
