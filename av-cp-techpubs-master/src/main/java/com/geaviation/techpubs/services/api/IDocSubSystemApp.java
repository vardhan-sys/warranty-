package com.geaviation.techpubs.services.api;

import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.DocumentDataTableModel;
import com.geaviation.techpubs.models.DocumentDownloadModel;
import com.geaviation.techpubs.models.DocumentModel;
import com.geaviation.techpubs.models.SubSystem;
import java.io.File;
import java.util.Map;

/**
 * The Interface IDocSubSystemApp.
 */
public interface IDocSubSystemApp {

    /**
     * Gets the documents.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param queryParams the query params
     * @return DocumentDataTableModel the documents
     * @throws TechpubsException the techpubs exceptions
     */
    @Deprecated
    DocumentDataTableModel getDocuments(String ssoId, String portalId, Map<String, String> queryParams)
        throws TechpubsException;

    /**
     * Gets the sub system.
     *
     * @return the sub system
     */
    abstract SubSystem getSubSystem();

    /**
     * Gets the associated documents.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param fileId the file id
     * @return the associated documents
     * @throws TechpubsException the techpubs exceptions
     */
    DocumentModel getAssociatedDocuments(String ssoId, String portalId, String fileId,
        Map<String, String> queryParams)
        throws TechpubsException;

    /**
     * Gets the download resource CMM.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param files the files
     * @return the download resource CMM
     * @throws TechpubsException the techpubs exceptions
     */
    DocumentDownloadModel getDownloadResource(String ssoId, String portalId, String files)
        throws TechpubsException;

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
    File getDownloadCSV(String ssoId, String portalId, String model, String family, String docType,
        Map<String, String> queryParams) throws TechpubsException;

    /**
     * Retrieves the list of downloadable documents for a selected program .
     *
     * @param queryParams the URI params
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param model the model name
     * @return DocumentDataTableModel the File object
     * @throws TechpubsException the techpubs exceptions
     */
    DocumentDataTableModel getDownload(String ssoId, String portalId, String model, String family,
        String docType,
        Map<String, String> queryParams) throws TechpubsException;

    /**
     * Gets the resource.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param publication the publication
     * @return String the resource
     * @throws TechpubsException the techpubs exceptions
     */
    String getResource(String ssoId, String portalId, String model, String publication,
        Map<String, String> queryParams)
        throws TechpubsException;

    /**
     * Gets the print resource.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param fileId the file id
     * @return Map the resource
     * @throws TechpubsException the techpubs exceptions
     */

    Map<String, Object> getArtifact(String ssoId, String portalId, String fileId)
        throws TechpubsException;

}
