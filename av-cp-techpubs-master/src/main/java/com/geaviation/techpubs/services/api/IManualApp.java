package com.geaviation.techpubs.services.api;

import com.geaviation.techpubs.data.util.log.LogExecutionTime;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.DocumentModel;
import com.geaviation.techpubs.models.ManualItemModel;
import com.geaviation.techpubs.models.ManualModel;
import com.geaviation.techpubs.models.ResourceMetaDataModel;
import java.util.List;
import java.util.Map;

/**
 * The Interface IManualApp.
 */
public interface IManualApp {

    /**
     * Gets the manuals.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param queryParams the query params
     * @return the manuals
     * @throws TechpubsException the techpubs exceptions
     */
    @Deprecated
    ManualModel getManuals(String ssoId, String portalId, Map<String, String> queryParams)
        throws TechpubsException;

    /**
     * Gets the HTML resource TD.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param program the program
     * @param manual the manual
     * @param filename the filename
     * @return the HTML resource TD
     * @throws TechpubsException the techpubs exceptions
     */
    byte[] getHTMLResourceTD(String ssoId, String portalId, String program, String manual,
       String filename, String bandwidth, boolean multibrowserDocumentRequired) throws TechpubsException;

    byte[] getHTMLResourceTD(String ssoId, String portalId, String program, String version ,
        String manual, String filename, String bandwidth, boolean multibrowserDocumentRequired)
        throws TechpubsException;

    byte[] getHTMLResourceTDByTargetIndex(String ssoId, String portalId, String program,
        String version, String manual, String target, String bandwidth,
        boolean multibrowserDocumentRequired);

    @LogExecutionTime
    ResourceMetaDataModel getResourceName(String ssoId, String portalId, String program,
        String manual, String target,
        String filename, Map<String, String> queryParams) throws TechpubsException;

    /**
     * Gets the program banner TD.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param program the program
     * @return the program banner TD
     * @throws TechpubsException the techpubs exceptions
     */
    byte[] getProgramBannerTD(String ssoId, String portalId, String program)
        throws TechpubsException;

    /**
     * Gets the HTML resource summary TD.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param program the program
     * @param manual the manual
     * @param filename the filename
     * @return the HTML resource summary TD
     * @throws TechpubsException the techpubs exceptions
     */
    String getHTMLResourceSummaryTD(String ssoId, String portalId, String program, String manual,
        String filename) throws TechpubsException;

    String getHTMLResourceSummaryTD(String ssoId, String portalId, String program, String version,
        String manual, String filename) throws TechpubsException;

    /**
     * Gets the binary resource TD.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param program the program
     * @param manual the manual
     * @param res the res
     * @return the binary resource TD
     * @throws TechpubsException the techpubs exceptions
     */
    byte[] getBinaryResourceTD(String ssoId, String portalId, String program, String version, String manual, String res) throws TechpubsException;

    /**
     * Gets the binary resource TD.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param program the program
     * @param manual the manual
     * @param res the res
     * @param onlineFileName the onlineFileName
     * @return the binary resource TD
     * @throws TechpubsException the techpubs exceptions
     */
    byte[] getSMMBinaryResourceTD(String ssoId, String portalId, String program, String version,
        String manual, String res, String onlineFileName) throws TechpubsException;

    /**
     * Gets the prints the HTML resource summary TD.
     *
     * @param baseURI the base URI
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param program the program
     * @param manual the manual
     * @param filename the filename
     * @return the prints the HTML resource summary TD
     * @throws TechpubsException the techpubs exceptions
     */
    byte[] getPrintHTMLResourceSummaryTD(String baseURI, String ssoId, String portalId,
        String program, String manual,
        String filename) throws TechpubsException;

    /**
     * Gets the prints the HTML resource TD.
     *
     * @param baseURI the base URI
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param program the program
     * @param manual the manual
     * @param filename the filename
     * @return the prints the HTML resource TD
     * @throws TechpubsException the techpubs exceptions
     */
    byte[] getPrintHTMLResourceTD(String baseURI, String ssoId, String portalId, String program,
        String version, String manual, String filename) throws TechpubsException;

    /**
     * Gets the download manuals.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param program the program
     * @param downloadtype the downloadtype
     * @param type the type
     * @param queryParams the query params
     * @return the download manuals
     * @throws TechpubsException the techpubs exceptions
     */
    List<ManualItemModel> getDownloadManuals(String ssoId, String portalId, String program,
        String downloadtype,
        String type, Map<String, String> queryParams) throws TechpubsException;

    /**
     * Gets the documents by parent toc id TD.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param program the program
     * @param manual the manual
     * @param parentnodeid the parentnodeid
     * @param queryParams the query params
     * @return the documents by parent toc id TD
     * @throws TechpubsException the techpubs exceptions
     */
    //Remove this while clearing featureFlagUS448283
    @Deprecated
    DocumentModel getDocumentsByParentTocIdTD(String ssoId, String portalId, String program,
        String manual,
        String parentnodeid, Map<String, String> queryParams) throws TechpubsException;

    /**
     * Gets the sb filename for a given version and sb value
     *
     * @param program the program that is used to find the current version
     * @param sbnbr the sb number that is used to find the sb file
     * @return the sb file
     */
    byte[] getSbResource(String ssoId, String portalId,
        String manual, String program, String sbnbr, String bandwidth,
        boolean multiBrowserSupportRequired) throws TechpubsException;


    byte[] getSbResource(String ssoId, String portalId, String manual, String program,
        String version, String sbnbr, String bandwidth, boolean multiBrowserSupportRequired)
        throws TechpubsException;
}
