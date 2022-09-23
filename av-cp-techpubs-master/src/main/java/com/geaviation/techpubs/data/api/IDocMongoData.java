package com.geaviation.techpubs.data.api;

import com.geaviation.techpubs.models.DocumentItemModel;
import java.util.List;
import java.util.Map;

/**
 * The Interface IDocMongoSystemData.
 */
public interface IDocMongoData extends IDocSubSystemData {

    /**
     * Gets the documents.
     *
     * @param modelList the model list
     * @param tokenList the token list
     * @param queryParams the query params
     * @return the documents
     */
    List<DocumentItemModel> getDocuments(List<String> modelList, List<String> tokenList,
        Map<String, String> queryParams);

    /**
     * Gets the associated documents.
     *
     * @param modelList the model list
     * @param tokenList the token list
     * @param fileId the file id
     * @param includeParts the include parts
     * @return the associated documents
     */
    List<DocumentItemModel> getAssociatedDocuments(List<String> modelList, List<String> tokenList,
        String fileId,
        Boolean includeParts);

    /**
     * Gets the document.
     *
     * @param modelList the model list
     * @param tokenList the token list
     * @param fileId the file id
     * @return the document
     */
    DocumentItemModel getDocument(List<String> modelList, List<String> tokenList, String fileId);

    /**
     * Gets the release date.
     *
     * @param documentItem the document item
     * @return the release date
     */
    String getReleaseDate(DocumentItemModel documentItem);

}
