package com.geaviation.techpubs.services.api;

import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.dom4j.DocumentException;

/**
 * The Interface IProgramApp.
 */
public interface IProgramApp {

    /**
     * Gets the programs.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param queryParams the query params
     * @return the programs
     * @throws TechpubsException the techpubs exceptions
     */
    ProgramModel getPrograms(String ssoId, String portalId, Map<String, String> queryParams)
        throws TechpubsException;

    /**
     * Gets the programs.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param family the family
     * @param queryParams the query params
     * @return the programs
     * @throws TechpubsException the techpubs exceptions
     */
    ProgramModel getPrograms(String ssoId, String portalId, String family,
        Map<String, String> queryParams)
        throws TechpubsException;

    /**
     * Gets the programs.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param family the family
     * @param model the model
     * @param queryParams the query params
     * @return the programs
     * @throws TechpubsException the techpubs exceptions
     */
    ProgramModel getPrograms(String ssoId, String portalId, String family, String model,
        Map<String, String> queryParams) throws TechpubsException;

    /**
     * Gets the programs.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param family the family
     * @param model the model
     * @param aircraft the aircraft
     * @param queryParams the query params
     * @return the programs
     * @throws TechpubsException the techpubs exceptions
     */
    ProgramModel getPrograms(String ssoId, String portalId, String family, String model,
        String aircraft,
        Map<String, String> queryParams) throws TechpubsException;

    /**
     * Gets the programs.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param family the family
     * @param model the model
     * @param aircraft the aircraft
     * @param tail the tail
     * @param queryParams the query params
     * @return the programs
     * @throws TechpubsException the techpubs exceptions
     */
    ProgramModel getPrograms(String ssoId, String portalId, String family, String model,
        String aircraft, String tail,
        Map<String, String> queryParams) throws TechpubsException;

    /**
     * Gets the programs.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param family the family
     * @param model the model
     * @param aircraft the aircraft
     * @param tail the tail
     * @param esnList the esn list
     * @param queryParams the query params
     * @return the programs
     * @throws TechpubsException the techpubs exceptions
     */
    ProgramModel getPrograms(String ssoId, String portalId, String family, String model,
        String aircraft, String tail,
        List<String> esnList, Map<String, String> queryParams) throws TechpubsException;

    /**
     * Gets the authorized programs.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param subSystem the sub system
     * @return the authorized programs
     * @throws TechpubsException the techpubs exceptions
     */
    List<String> getAuthorizedPrograms(String ssoId, String portalId, SubSystem subSystem)
        throws TechpubsException;

    /**
     * Gets the content by program TD.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param program the program
     * @param queryParams the query params
     * @return the content by program TD
     * @throws TechpubsException the techpubs exceptions
     */
    //Remove this while clearing featureFlagUS448283
    @Deprecated
    TocNodeModel getContentByProgramTD(String ssoId, String portalId, String program,
        Map<String, String> queryParams)
        throws TechpubsException;

    /**
     * Gets the content by toc node id.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param program the program
     * @param manual the manual
     * @param parentnodeid the parentnodeid
     * @param queryParams the query params
     * @return the content by toc node id
     * @throws TechpubsException the techpubs exceptions
     */
    //Remove this while clearing featureFlagUS448283
    @Deprecated
    TocNodeModel getContentByTocNodeId(String ssoId, String portalId, String program, String manual,
        String parentnodeid, Map<String, String> queryParams) throws TechpubsException;

    /**
     * Gets the content by doc file.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param program the program
     * @param manual the manual
     * @param file the file
     * @param queryParams the query params
     * @return the content by doc file
     * @throws TechpubsException the techpubs exceptions
     *
     * Remove with method when US29570 feature flag is removed
     */
    //Remove this while clearing featureFlagUS448283
    @Deprecated
    TocNodeModel getContentByDocFile(String ssoId, String portalId, String program, String manual,
        String file, Map<String, String> queryParams) throws TechpubsException;

    /**
     * Gets the content by doc file.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param program the program
     * @param manual the manual
     * @param file the file
     * @param queryParams the query params
     * @return the content by doc file
     * @throws TechpubsException the techpubs exceptions
     */
    //Remove this while clearing featureFlagUS448283
    @Deprecated
    TocNodeModel getContentByDocFile(String ssoId, String portalId, String program, String version,
        String manual, String file, Map<String, String> queryParams) throws TechpubsException;

    /**
     * Gets the content by manual TD.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param program the program
     * @param manual the manual
     * @param queryParams the query params
     * @return the content by manual TD
     * @throws TechpubsException the techpubs exceptions
     */
    //Remove this while clearing featureFlagUS448283
    @Deprecated
    TocItemModelList getContentByManualTD(String ssoId, String portalId, String program,
        String version, String manual, Map<String, String> queryParams) throws TechpubsException;

    /**
     * Gets the program item list for request.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param family the family
     * @param model the model
     * @param aircraft the aircraft
     * @param tail the tail
     * @param esnList the esn list
     * @param subSystem the sub system
     * @param authorizedProgramsList the authorized programs list
     * @return the program item list for request
     * @throws TechpubsException the techpubs exceptions
     */
    List<ProgramItemModel> getProgramItemListForRequest(String ssoId, String portalId,
        String family, String model,
        String aircraft, String tail, List<String> esnList, SubSystem subSystem,
        List<String> authorizedProgramsList)
        throws TechpubsException, IOException, DocumentException;

    /**
     * Gets the download documents.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param program the program
     * @param downloadtype the downloadtype
     * @param type the type
     * @param queryParams the query params
     * @return the download documents
     * @throws TechpubsException the techpubs exceptions
     */
    DocumentDataTableModel getDownloadDocuments(String ssoId, String portalId, String program,
        String downloadtype,
        String type, Map<String, String> queryParams) throws TechpubsException, InterruptedException;

    /**
     * Gets the download documents CSV.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param program the program
     * @param downloadtype the downloadtype
     * @param type the type
     * @param queryParams the query params
     * @return the download documents CSV
     * @throws TechpubsException the techpubs exceptions
     */
    File getDownloadDocumentsCSV(String ssoId, String portalId, String program, String downloadtype,
        String type,
        Map<String, String> queryParams) throws TechpubsException;

    /**
     * Gets the download resource TD.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param program the program
     * @param downloadtype the downloadtype
     * @param type the type
     * @param files the files
     * @return the download resource TD
     * @throws TechpubsException the techpubs exceptions
     */
    DocumentDownloadModel getDownloadResourceTD(String ssoId, String portalId, String program,
        String downloadtype,
        String type, String files) throws TechpubsException;

    /**
     * Gets the download document names.
     *
     * @param ssoId the sso id
     * @param portalId the portal id
     * @param program the program
     * @param queryParams the query params
     * @return the download document names
     * @throws TechpubsException the techpubs exceptions
     */
    DocumentDataTableModel getDVDFileList(String ssoId, String portalId, String program,
        Map<String, String> queryParams) throws TechpubsException, IOException;


}
