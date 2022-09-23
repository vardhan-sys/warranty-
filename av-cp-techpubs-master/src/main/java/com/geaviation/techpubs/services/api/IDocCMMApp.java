package com.geaviation.techpubs.services.api;

import com.geaviation.techpubs.exceptions.TechpubsException;

public interface IDocCMMApp extends IDocSubSystemApp {

    /**
     * Gets the CMM parts.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param fileId the file id
     * @return the CMM parts
     * @throws TechpubsException the techpubs exceptions
     */
    String getCMMParts(String ssoId, String portalId, String fileId) throws TechpubsException;

    /**
     * Gets the CMM parts PDF.
     *
     * @param baseURI the base URI
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param fileId the file id
     * @return the CMM parts PDF
     * @throws TechpubsException the techpubs exceptions
     */
    byte[] getCMMPartsPDF(String baseURI, String ssoId, String portalId, String fileId)
        throws TechpubsException;

}
