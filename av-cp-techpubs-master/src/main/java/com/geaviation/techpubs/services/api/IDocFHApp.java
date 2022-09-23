package com.geaviation.techpubs.services.api;

import com.geaviation.techpubs.exceptions.TechpubsException;
import java.util.Map;

public interface IDocFHApp extends IDocSubSystemApp {

    /**
     * Gets the resource.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param overallFileId the overall File Id
     * @param fileId the file Id
     * @throws TechpubsException the techpubs exceptions
     */
    String getFHResourceSCT(String ssoId, String portalId, String overallFileId, String fileId,
        Map<String, String> queryParams) throws TechpubsException;

    /**
     * Gets the resource.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param id the Id
     * @param sectid the sect id
     * @throws TechpubsException the techpubs exceptions
     */
    Map<String, Object> getFHArtifactSCT(String ssoId, String portalId, String id, String sectid)
        throws TechpubsException;

    /**
     * Gets the resource.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param overallFileId the overall File Id
     * @param fileId the file Id
     * @throws TechpubsException the techpubs exceptions
     */
    String getFHResourceART(String ssoId, String portalId, String overallFileId, String fileId,
        Map<String, String> queryParams) throws TechpubsException;

    /**
     * Gets the resource.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param id the Id
     * @param sectid the sect id
     * @throws TechpubsException the techpubs exceptions
     */
    Map<String, Object> getFHArtifactART(String ssoId, String portalId, String id, String sectid)
        throws TechpubsException;

}
