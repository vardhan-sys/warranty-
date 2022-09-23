package com.geaviation.techpubs.services.api;

import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.DocumentDataTableModel;
import com.geaviation.techpubs.models.DocumentModel;
import java.io.File;
import java.util.Map;

public interface IDocTPApp extends IDocSubSystemApp {

    /**
     * Gets the associated documents.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param model the model
     * @param queryParams the query params
     * @return the associated documents
     * @throws TechpubsException the techpubs exceptions
     */
    DocumentModel getAssociatedDocumentsTP(String ssoId, String portalId, String model,
        String category,
        Map<String, String> queryParams) throws TechpubsException;

    /**
     * Gets the download CSV.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param model the model
     * @param queryParams the query params
     * @return the download CSV
     * @throws TechpubsException the techpubs exceptions
     */
    File getDownloadTPCSV(String ssoId, String portalId, String model, String category,
        Map<String, String> queryParams)
        throws TechpubsException;

    /**
     * Gets the documents.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param queryParams the query params
     * @return DocumentDataTableModel the documents
     * @throws TechpubsException the techpubs exceptions
     */

    DocumentDataTableModel getDownloadTP(String ssoId, String portalId, String model,
        String category,
        Map<String, String> queryParams) throws TechpubsException;

    /**
     * Gets the TP resource init.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param category the category
     * @param queryParams the query params
     * @return the TP resource init
     * @throws TechpubsException the techpubs exceptions
     */

    String getTPResourceInit(String ssoId, String portalId, String model, String category,
        Map<String, String> queryParams) throws TechpubsException;

}
