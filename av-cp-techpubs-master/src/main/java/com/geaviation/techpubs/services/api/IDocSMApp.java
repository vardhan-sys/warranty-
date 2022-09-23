package com.geaviation.techpubs.services.api;

import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.DocumentDataTableModel;
import com.geaviation.techpubs.models.DocumentModel;
import java.util.Map;

public interface IDocSMApp extends IDocSubSystemApp {

    /**
     * Gets the download SM.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param model the model
     * @param doctype the doctype
     * @param queryParams the query params
     * @return the download SM
     * @throws TechpubsException the techpubs exceptions
     */
    DocumentDataTableModel getDownloadSM(String ssoId, String portalId, String model,
        String doctype,
        Map<String, String> queryParams) throws TechpubsException;

    /**
     * Gets the associated documents SM.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param category the category
     * @param queryParams the query params
     * @return the associated documents SM
     * @throws TechpubsException the techpubs exceptions
     */
    DocumentModel getAssociatedDocumentsSM(String ssoId, String portalId, String category,
        Map<String, String> queryParams) throws TechpubsException;

    /**
     * Gets the SM resource init.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param category the category
     * @param queryParams the query params
     * @return the SM resource init
     * @throws TechpubsException the techpubs exceptions
     */
    String getSMResourceInit(String ssoId, String portalId, String category,
        Map<String, String> queryParams)
        throws TechpubsException;

}
