package com.geaviation.techpubs.data.api;

import com.geaviation.techpubs.models.DocumentItemAssociatedFHModel;
import java.util.List;

public interface IDocFHData extends IDocMongoData {

    /**
     * Gets the FH SCT document.
     *
     * @param modelList the model list
     * @param tokenList the token list
     * @param fileId the file id
     * @param overallFileId the over all File Id
     * @return DocumentItemAssociatedFHModel the FH document
     */
    DocumentItemAssociatedFHModel getFHDocumentSCT(List<String> modelList, List<String> tokenList,
        String overallFileId,
        String fileId);

    /**
     * Gets the FH ART document.
     *
     * @param modelList the model list
     * @param tokenList the token list
     * @param fileId the file id
     * @param overallFileId the over all File Id
     * @return DocumentItemAssociatedFHModel the FH document
     */
    DocumentItemAssociatedFHModel getFHDocumentART(List<String> modelList, List<String> tokenList,
        String overallFileId,
        String fileId);

}
