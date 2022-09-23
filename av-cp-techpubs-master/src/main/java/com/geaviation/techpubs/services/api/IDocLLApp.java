package com.geaviation.techpubs.services.api;

import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.DocumentDataTableModel;
import com.geaviation.techpubs.models.DocumentModel;
import java.io.File;
import java.util.Map;

public interface IDocLLApp extends IDocSubSystemApp {

    /**
     * Gets the documents.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param queryParams the query params
     * @return DocumentDataTableModel the documents
     * @throws TechpubsException the techpubs exceptions
     */

    DocumentDataTableModel getDownloadLL(String ssoId, String portalId, String category,
        Map<String, String> queryParams) throws TechpubsException;

    /**
     * Gets the associated documents LL.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param category the category
     * @param queryParams the query params
     * @return the associated documents LL
     * @throws TechpubsException the techpubs exceptions
     */
    DocumentModel getAssociatedDocumentsLL(String ssoId, String portalId, String category,
        Map<String, String> queryParams) throws TechpubsException;

    /**
     * Gets the LL resource init.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param category the category
     * @param queryParams the query params
     * @return the LL resource init
     * @throws TechpubsException the techpubs exceptions
     */
    String getLLResourceInit(String ssoId, String portalId, String category,
        Map<String, String> queryParams)
        throws TechpubsException;

    /**
     * Gets the download LLCSV.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param category the category
     * @param queryParams the query params
     * @return the download LLCSV
     * @throws TechpubsException the techpubs exceptions
     */
    File getDownloadLLCSV(String ssoId, String portalId, String category,
        Map<String, String> queryParams)
        throws TechpubsException;

}
