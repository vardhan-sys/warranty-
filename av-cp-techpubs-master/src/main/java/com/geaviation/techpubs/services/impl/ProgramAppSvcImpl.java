package com.geaviation.techpubs.services.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.geaviation.csv.export.app.api.ICSVExportApp;
import com.geaviation.techpubs.config.S3Config;
import com.geaviation.techpubs.data.api.IDocumentData;
import com.geaviation.techpubs.data.api.IProgramData;
import com.geaviation.techpubs.data.api.IResourceData;
import com.geaviation.techpubs.data.api.techlib.IBookcaseVersionData;
import com.geaviation.techpubs.data.api.techlib.IPageBlkData;
import com.geaviation.techpubs.data.factories.AmazonS3ClientFactory;
import com.geaviation.techpubs.data.util.DataConstants;
import com.geaviation.techpubs.data.util.DataUtil;
import com.geaviation.techpubs.data.util.log.LogExecutionTime;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.*;
import com.geaviation.techpubs.models.techlib.BookcaseVersionEntity;
import com.geaviation.techpubs.models.techlib.dto.OfflineDVDInfoDto;
import com.geaviation.techpubs.services.api.IProgramApp;
import com.geaviation.techpubs.services.api.logic.IPageblkEnablement;
import com.geaviation.techpubs.services.util.AppConstants;
import com.geaviation.techpubs.services.util.DynamicComparator;
import com.geaviation.techpubs.services.util.StringUtils;
import com.geaviation.techpubs.services.util.TechpubsAppUtil;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.*;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
@RefreshScope
public class ProgramAppSvcImpl implements IProgramApp {

    private static final String SSO_ID2 = " (ssoId=";

    private static final String STR = ":";

    private static final String REVISION = "revision";

    private static final String RELDATETO = "reldateto";

    private static final String RELDATEFROM2 = " (reldatefrom=";

    private static final String YYYY_MM_DD = "yyyy-MM-dd";

    private static final String REVENDDATE = "revenddate";

    private static final String RELENDDATE = "relenddate";

    private static final String RELSTARTDATE = "relstartdate";

    private static final String REVSTARTDATE = "revstartdate";

    private static final String RELDATEFROM = "reldatefrom";

    private static final String Y = "Y";

    private static final String ACTIVEIND = "activeind";

    private static final String SBTYPE = "sbtype";

    private static final String ALL = "all";

    private static final String DVD = "dvd";

    private static final String GET_DOWNLOAD_RESOURCE_TD = "getDownloadResourceTD - ";

    private static final String FILENAMES = ",filenames=";

    private static final String TYPE5 = ",type =";

    private static final String DOCNBR2 = "./*[@docnbr='";

    private static final String COLUMN = ", column=";

    private static final String IC = "ic";

    private static final String PUBCWCDATE = "pubcwcdate";

    private static final String CWC_ADDED_DATE = "cwc-added-date";

    private static final String REVDATE = "revdate";

    private static final String FILENAME = "filename";

    private static final String SOURCEFILENAME = "sourcefilename";

    private static final String DOCNBR = "docnbr";

    private static final String MANUAL_DOCNBR = "manualDocnbr";

    private static final String TR_NBR = "TR Nbr";

    private static final String ID = "id";

    private static final String TYPE4 = "Type";

    private static final String TR = "tr";

    private static final String DOC_NBR = "Doc Nbr";

    private static final String DISPLAY_MANUAL = "displayManual";

    private static final String MANUAL_TYPE2 = "Manual Type";

    private static final String MANUAL_TYPE = "manualType";

    private static final String ATA_NBR = "ATA Nbr";

    private static final String ATANUM = "atanum";

    private static final String REV_DATE = "Rev Date";

    private static final String REVISION_DATE = "revisionDate";

    private static final String REV_NBR = "Rev Nbr";

    private static final String REVNBR = "revnbr";

    private static final String LINK = "link";

    private static final String MY_GEA_LINK = "myGEA Link";

    private static final String SB_NBR = "SB Nbr";

    private static final String FULL_SB_NBR = "fullSBNbr";

    private static final String ISSUE_DATE = "Issue Date";

    private static final String RELEASE_DATE = "releaseDate";

    private static final String CATEGORY2 = "category";

    private static final String CATEGORY = "Category";

    private static final String ENGINE_MODEL = "Engine Model";

    private static final String PROGRAMTITLE = "programtitle";

    private static final String TYPE3 = "type";

    private static final String TITLE2 = "Title";

    private static final String SB = "sb";

    private static final String ORG_GROUPNAME = "org.groupname";

    private static final String TITLE = "title";

    private static final String CLOSE_DASH = ") - ";

    private static final String CLOSE = ")";

    private static final String INVALID_REQUEST_DVD = "getDownloadDocuments (Invalid Request (DVD:MANUAL is invalid)) (";

    private static final String LR = "lr";

    private static final String DOWNLOADTYPE2 = ",downloadtype=";

    private static final String MANUAL = "manual";

    private static final String SOURCE = "source";

    private static final String TYPE2 = ",type=";

    private static final String DOWNLOAD_TYPE = ",download type=";

    private static final String GET_DOWNLOAD_DOCUMENTS = "getDownloadDocuments (";

    private static final String GET_CONTENT_BY_MANUAL_TD = "getContentByManualTD (";

    private static final String GET_CONTENT_BY_DOC_FILE = "getContentByDocFile (";

    private static final String GET_CONTENT_BY_PROGRAM_TD = "getContentByProgramTD (";

    private static final String GET_CONTENT_BY_TOC_NODE_ID = "getContentByTocNodeId (";

    private static final String FILE_EQ = ",file=";
    private static final String PORTAL_ID = ",portalId=";
    private static final String SSO_ID = "(ssoId=";
    private static final String PARENTNODEID = ",parentnodeid=";
    private static final String MANUAL_EQ = ",manual=";
    private static final String PROGRAM_EQ = " (program=";

    private static final String GEAE = "geae";
    private static final String SLASH = "/";
    private static final String DVD_FOLDER = "/dvd/";
    private static final String CURRENT_ORG = "currentorg";
    private static final String TYPE = "offlineviewer";

    private static final DecimalFormat format = new DecimalFormat("#.0");

    private static final Logger log = LogManager.getLogger(ProgramAppSvcImpl.class);

    @Value("${techpubs.services.downloadOverlayReviewer}")
    private boolean downloadOverlayFeatureFlag;

    @Autowired
    private IProgramData iProgramData;

    @Autowired
    private AmazonS3ClientFactory amazonS3ClientFactory;

    @Autowired
    private TechpubsAppUtil techpubsAppUtil;

    @Autowired
    private ICSVExportApp iCSVExportApp;

    @Autowired
    private IResourceData iResourceData;

    @Autowired
    private IDocumentData iDocumentData;

    @Autowired
    private IPageBlkData iPageBlkData;

    @Autowired
    private IPageblkEnablement iPageblkEnablement;

    @Autowired
    private IBookcaseVersionData iBookcaseVersionData;

    @Autowired
    private S3Config s3Config;

    /**
     * Retrieve Programs (SB,TR,IC,CMM,FH,TP)
     *
     * @param ssoId - User Id
     * @param portalId - Portal Id (CWC/GEHONDA)
     * @param queryParams - Map of HTTP query parameters
     * @return ProgramModel - List of programs
     */
    @Override
    @LogExecutionTime
    public ProgramModel getPrograms(String ssoId, String portalId, Map<String, String> queryParams)
        throws TechpubsException {
        return getPrograms(ssoId, portalId, null, null, null, queryParams);
    }

    /**
     * Retrieve Technical Publications for the specified MDM family (SB,TR,IC,CMM,FH,TP)
     *
     * @param ssoId - User Id
     * @param portalId - Portal Id (CWC/GEHONDA)
     * @param family - MDM Family
     * @param queryParams - Map of HTTP query parameters
     * @return ProgramModel - List of programs
     */
    @Override
    @LogExecutionTime
    public ProgramModel getPrograms(String ssoId, String portalId, String family,
        Map<String, String> queryParams)
        throws TechpubsException {
        return getPrograms(ssoId, portalId, family, null, null, null, null, queryParams);
    }

    /**
     * Retrieve Technical Publications for the specified MDM family, model (SB,TR,IC,CMM,FH,TP)
     *
     * @param ssoId - User Id
     * @param portalId - Portal Id (CWC/GEHONDA)
     * @param family - MDM Family
     * @param model - MDM Model
     * @param queryParams - Map of HTTP query parameters
     * @return ProgramModel - List of programs
     */
    @Override
    @LogExecutionTime
    public ProgramModel getPrograms(String ssoId, String portalId, String family, String model,
        Map<String, String> queryParams) throws TechpubsException {
        return getPrograms(ssoId, portalId, family, model, null, null, null, queryParams);
    }

    /**
     * Retrieve Technical Publications for the specified MDM family, model, aircraft
     * (SB,TR,IC,CMM,FH,TP)
     *
     * @param ssoId - User Id
     * @param portalId - Portal Id (CWC/GEHONDA)
     * @param family - MDM Family
     * @param model - MDM Model
     * @param aircraft - MDM Aircraft
     * @param queryParams - Map of HTTP query parameters
     * @return ProgramModel - List of programs
     */
    @Override
    @LogExecutionTime
    public ProgramModel getPrograms(String ssoId, String portalId, String family, String model,
        String aircraft,
        Map<String, String> queryParams) throws TechpubsException {
        return getPrograms(ssoId, portalId, family, model, aircraft, null, null, queryParams);
    }

    /**
     * Retrieve Technical Publications for the specified MDM family, model, aircraft, tail
     * (SB,TR,IC,CMM,FH,TP)
     *
     * @param ssoId - User Id
     * @param portalId - Portal Id (CWC/GEHONDA)
     * @param family - MDM Family
     * @param model - MDM Model
     * @param aircraft - MDM Aircraft
     * @param tail - MDM Tail
     * @param queryParams - Map of HTTP query parameters
     * @return ProgramModel - List of programs
     */
    @Override
    @LogExecutionTime
    public ProgramModel getPrograms(String ssoId, String portalId, String family, String model,
        String aircraft,
        String tail, Map<String, String> queryParams) throws TechpubsException {
        return getPrograms(ssoId, portalId, family, model, aircraft, tail, null, queryParams);
    }

    /**
     * Retrieve Technical Publications for the specified MDM family, model, aircraft, tail, esns
     * (SB,TR,IC,CMM,FH,TP)
     *
     * @param ssoId - User Id
     * @param portalId - Portal Id (CWC/GEHONDA)
     * @param family - MDM Family
     * @param model - MDM Model
     * @param aircraft - MDM Aircraft
     * @param tail - MDM Tail
     * @param esnList - List of MDM ESNs (Serial #)
     * @param queryParams - Map of HTTP query parameters
     * @return ProgramModel - List of programs
     */
    @Override
    @LogExecutionTime
    public ProgramModel getPrograms(String ssoId, String portalId, String family, String model,
        String aircraft,
        String tail, List<String> esnList, Map<String, String> queryParams)
        throws TechpubsException {

        if (ssoId == null || ssoId.isEmpty() || portalId == null || portalId.isEmpty()) {
            log.error(
                "getPrograms (" + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorCode()
                    + CLOSE_DASH
                    + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg() + " (family="
                    + family + ",model="
                    + model + ",aircraft=" + aircraft + ",tail=" + tail + ",esns="
                    + (esnList == null ? null : esnList.toString()) + ") " + SSO_ID2 + ssoId
                    + PORTAL_ID + portalId
                    + CLOSE);
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        // validate SubSystem
        SubSystem subSystem = SubSystem.INVALID;
        try {
            subSystem = SubSystem
                .valueOf((queryParams.get("subsystem") == null ? ""
                    : queryParams.get("subsystem").toUpperCase()));

        } catch (IllegalArgumentException e) {
            log.error(DataConstants.LOGGER_GETTPDOCS, e);
        }

        List<ProgramItemModel> programList = new ArrayList<>();

        List<String> authorizedProgramsList = getAuthorizedPrograms(ssoId, portalId, subSystem);

        if (!authorizedProgramsList.isEmpty()) {

            try {
                programList = getProgramItemListForRequest(ssoId, portalId, family, model, aircraft,
                    tail, esnList,
                    subSystem, authorizedProgramsList);
            } catch (IOException e) {
                log.info(e.getMessage());
            } catch (DocumentException e) {
                log.info(e.getMessage());
            }

            if (subSystem == SubSystem.TD && !programList.isEmpty()) {
                // Add SPM Manuals if other ge manuals are found (and
                // authorized) for TD
                ProgramItemModel spmProgram = iProgramData.getSpmProgramItem();
                if (spmProgram != null && authorizedProgramsList
                    .contains(spmProgram.getProgramDocnbr())
                    && !programList.contains(spmProgram)) {
                    programList.add(spmProgram);
                }
            }

            if (subSystem == SubSystem.TD && !programList.isEmpty() && portalId
                .equalsIgnoreCase(AppConstants.GEHONDA)) {
                // Add Honda-SPM Manuals if other honda manuals are found (and
                // authorized) for TD
                ProgramItemModel hondaSpmProgram = iProgramData.getHondaSpmProgramItem();
                if (hondaSpmProgram != null && authorizedProgramsList
                    .contains(hondaSpmProgram.getProgramDocnbr())
                    && !programList.contains(hondaSpmProgram)) {
                    programList.add(hondaSpmProgram);
                }
            }
        }

        ProgramModel programModel = new ProgramModel();
        sortDocumentItems(programList);
        programModel.setProgramItemList(programList);

        return programModel;
    }

    /**
     * Returns list of internal program for a given subsystem and authorization list for a request
     *
     * @param ssoId - User Id
     * @param portalId - Portal Id (CWC/GEHONDA)
     * @param family - MDM Family
     * @param model - MDM Model
     * @param aircraft - MDM Aircraft
     * @param tail - MDM Tail
     * @param esnList - List of MDM ESNs (Serial #)
     * @param subSystem - Subsystem (TD,CMM,FH,TP)
     * @param authorizedProgramsList - List of authorized programs
     * @return List<ProgramItemModel> - List of programs
     */
    @Override
    @LogExecutionTime
    public List<ProgramItemModel> getProgramItemListForRequest(String ssoId, String portalId,
        String family,
        String model, String aircraft, String tail, List<String> esnList, SubSystem subSystem,
        List<String> authorizedProgramsList)
        throws TechpubsException, IOException, DocumentException {

        Set<ProgramItemModel> programSet = new HashSet<>();

        if (DataUtil.isNotNullandEmpty(model)) {
            programSet.addAll((authorizedProgramsList == null ? iProgramData
                .getProgramItemsByModel(model, subSystem)
                : iProgramData.getProgramItemsByModel(model, subSystem, authorizedProgramsList)));
        } else if (DataUtil.isNotNullandEmpty(family)) {
            programSet.addAll((authorizedProgramsList == null ? iProgramData
                .getProgramItemsByFamily(family, subSystem)
                : iProgramData.getProgramItemsByFamily(family, subSystem, authorizedProgramsList)));
        } else if (esnList != null && !esnList.isEmpty()) {
            processEsn(ssoId, portalId, family, model, aircraft, tail, esnList, subSystem,
                authorizedProgramsList,
                programSet);
        } else if (DataUtil.isNotNullandEmpty(aircraft) || DataUtil.isNotNullandEmpty(tail)) {
            processAircraft(ssoId, portalId, family, model, aircraft, tail, subSystem,
                authorizedProgramsList,
                programSet);
        } else {
            // 'ALL/ALL' Selected - Return all 'mapped' programs....
            programSet.addAll((authorizedProgramsList == null ? iProgramData
                .getProgramItemsByFamily(null, subSystem)
                : iProgramData.getProgramItemsByFamily(null, subSystem, authorizedProgramsList)));
        }

        return new ArrayList<>(programSet);
    }

    private void processAircraft(String ssoId, String portalId, String family, String model,
        String aircraft,
        String tail, SubSystem subSystem, List<String> authorizedProgramsList,
        Set<ProgramItemModel> programSet)
        throws TechpubsException {
        for (String derivedModel : techpubsAppUtil
            .getModelList(ssoId, portalId, family, model, aircraft, tail, null)) {
            programSet.addAll(
                (authorizedProgramsList == null ? iProgramData
                    .getProgramItemsByModel(derivedModel, subSystem)
                    : iProgramData
                        .getProgramItemsByModel(derivedModel, subSystem, authorizedProgramsList)));
        }
    }

    private void processEsn(String ssoId, String portalId, String family, String model,
        String aircraft, String tail,
        List<String> esnList, SubSystem subSystem, List<String> authorizedProgramsList,
        Set<ProgramItemModel> programSet) throws TechpubsException {
        for (String esn : esnList) {
            for (String derivedModel : techpubsAppUtil
                .getModelList(ssoId, portalId, family, model, aircraft, tail,
                    esn)) {
                programSet.addAll((authorizedProgramsList == null
                    ? iProgramData.getProgramItemsByModel(derivedModel, subSystem)
                    : iProgramData
                        .getProgramItemsByModel(derivedModel, subSystem, authorizedProgramsList)));
            }
        }
    }

    /**
     * Sort list of programs based on program title
     *
     * @param programItemList - List of document
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void sortDocumentItems(List<ProgramItemModel> programItemList) {
        // Perform sort if required
        if (programItemList == null || programItemList.isEmpty()) {
            return;
        }

        ComparatorChain comparatorChain = new ComparatorChain();
        comparatorChain.addComparator(new DynamicComparator(ProgramItemModel.class, TITLE, true));

        Collections.sort(programItemList, comparatorChain);
    }

    @Override
    @LogExecutionTime
    public List<String> getAuthorizedPrograms(String ssoId, String portalId, SubSystem subSystem)
        throws TechpubsException {
        List<String> authorizedProgramList = new ArrayList<>();
        // Get the current org for user
        String currentOrg = techpubsAppUtil.getCurrentOperator(ssoId, portalId);

        // Get roles for user
        List<Property> rolePropertyList = techpubsAppUtil
            .getProperty(ssoId, portalId, ORG_GROUPNAME);

        // Derive programs
        if (rolePropertyList != null && rolePropertyList.size() == 1
            && ORG_GROUPNAME.equals(rolePropertyList.get(0).getPropName())) {
            authorizedProgramList.addAll(iProgramData
                .getProgramsByRoles(
                    Arrays.asList(rolePropertyList.get(0).getPropValue().split("~")), subSystem));
        }

        //Add Non LR GENX bookcases only for Reviewer company
        if (!StringUtils.isEmpty(currentOrg) && currentOrg.equalsIgnoreCase("geae tech all pubs review")) {
            authorizedProgramList.add("gek112865");
            authorizedProgramList.add("gek114118");
        }
        return authorizedProgramList;
    }

    /**
     * Retrieve the high level Table of Contents for the specified program
     *
     * @param ssoId - User Id
     * @param portalId - Portal Id (CWC/GEHONDA)
     * @param program - TD Program (e.g. gek108749)
     * @return TocNodeModel - List of Table of Content Entries
     */
    //Remove this while clearing featureFlagUS448283
    @Deprecated
    @Override
    @LogExecutionTime
    public TocNodeModel getContentByProgramTD(String ssoId, String portalId, String program,
        Map<String, String> queryParams) throws TechpubsException {

        if (ssoId == null || ssoId.isEmpty() || portalId == null || portalId.isEmpty()) {
            log.error(
                GET_CONTENT_BY_PROGRAM_TD + TechpubsException.TechpubsAppError.NOT_AUTHORIZED
                    .getErrorCode()
                    + CLOSE_DASH + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg()
                    + PROGRAM_EQ
                    + program + SSO_ID + ssoId + PORTAL_ID + portalId + CLOSE);
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        // Ensure user has access to program
        if (!getAuthorizedPrograms(ssoId, portalId, SubSystem.TD).contains(program)) {
            log.error(
                GET_CONTENT_BY_PROGRAM_TD + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE
                    .getErrorCode()
                    + CLOSE_DASH + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE
                    .getErrorMsg()
                    + PROGRAM_EQ + program + CLOSE);
            throw new TechpubsException(TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE);
        }

        TocNodeModel tocNodeModel = new TocNodeModel();
        List<TocItemNodeModel> tocItemNodeList = new ArrayList<>();
        try {
            tocItemNodeList.addAll(
                iProgramData.getContentByProgram(iProgramData.getProgramItem(program, SubSystem.TD)));
        } catch (DocumentException e) {
            log.info(e.getMessage());
        } catch (IOException e) {
            log.info(e.getMessage());
        }
        tocNodeModel.setTocItemNodeList(tocItemNodeList);
        tocNodeModel.setSuccess(true);

        return tocNodeModel;
    }

    /**
     * Retrieve the Table of Contents subtree for the specified Program/Manual/ParentTocId
     *
     * @param ssoId - User Id
     * @param portalId - Portal Id (CWC/GEHONDA)
     * @param program - TD Program (e.g. gek108749)
     * @param manual - TD Manual (e.g. gek100700)
     * @param parentnodeid - Table of Contents entry
     * @return TocNodeModel - Subtree nodes
     */
    //Remove this while clearing featureFlagUS448283
    @Deprecated
    @Override
    @LogExecutionTime
    public TocNodeModel getContentByTocNodeId(String ssoId, String portalId, String program,
        String manual,
        String parentnodeid, Map<String, String> queryParams) throws TechpubsException {

        if (ssoId == null || ssoId.isEmpty() || portalId == null || portalId.isEmpty()) {
            log.error(
                GET_CONTENT_BY_TOC_NODE_ID + TechpubsException.TechpubsAppError.NOT_AUTHORIZED
                    .getErrorCode()
                    + CLOSE_DASH + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg()
                    + PROGRAM_EQ
                    + program + MANUAL_EQ + manual + PARENTNODEID + parentnodeid + ") " + SSO_ID
                    + ssoId + PORTAL_ID
                    + portalId + CLOSE);
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        // Ensure user has access to program
        if (!getAuthorizedPrograms(ssoId, portalId, SubSystem.TD).contains(program)) {
            log.error(
                GET_CONTENT_BY_TOC_NODE_ID
                    + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE.getErrorCode()
                    + CLOSE_DASH + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE
                    .getErrorMsg()
                    + PROGRAM_EQ + program + MANUAL_EQ + manual + PARENTNODEID + parentnodeid
                    + CLOSE);
            throw new TechpubsException(TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE);
        }

        TocNodeModel tocNodeModel = new TocNodeModel();
        List<TocItemNodeModel> tocItemNodeList = new ArrayList<>();
        try {
            tocItemNodeList.addAll(
                iProgramData.getContentByTocNodeId(iProgramData.getProgramItem(program, SubSystem.TD),
                    manual, parentnodeid));
        } catch (DocumentException e) {
            log.info(e.getMessage());
        } catch (IOException e) {
            log.info(e.getMessage());
        }
        tocNodeModel.setTocItemNodeList(tocItemNodeList);
        tocNodeModel.setSuccess(true);

        return tocNodeModel;
    }

    /**
     * Retrieve the Table of Contents subtree for the specified Program/Manual/ParentTocId
     *
     * @param ssoId - User Id
     * @param portalId - Portal Id (CWC/GEHONDA)
     * @param program - TD Program (e.g. gek108749)
     * @param manual - TD Manual (e.g. gek100700)
     * @param file - File name of the document for which we need to get the TOC Path for
     * @return TocNodeModel - Subtree nodes
     */
    //Remove this while clearing featureFlagUS448283
    @Deprecated
    @Override
    @LogExecutionTime
    public TocNodeModel getContentByDocFile(String ssoId, String portalId, String program,
        String version, String manual, String file, Map<String, String> queryParams) throws TechpubsException {

        if (ssoId == null || ssoId.isEmpty() || portalId == null || portalId.isEmpty()) {
            log.error(GET_CONTENT_BY_DOC_FILE + TechpubsException.TechpubsAppError.NOT_AUTHORIZED
                .getErrorCode()
                + CLOSE_DASH + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg()
                + PROGRAM_EQ
                + program + MANUAL_EQ + manual + FILE_EQ + file + CLOSE + SSO_ID + ssoId + PORTAL_ID
                + portalId
                + CLOSE);
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        // Ensure user has access to program
        if (!getAuthorizedPrograms(ssoId, portalId, SubSystem.TD).contains(program)) {
            log.error(
                GET_CONTENT_BY_DOC_FILE + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE
                    .getErrorCode()
                    + CLOSE_DASH + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE
                    .getErrorMsg()
                    + PROGRAM_EQ + program + MANUAL_EQ + manual + FILE_EQ + file + CLOSE);
            throw new TechpubsException(TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE);
        }

        BookcaseVersionEntity versionedProgram = new BookcaseVersionEntity();
        versionedProgram.setTitle(program);
        versionedProgram.setBookcaseVersion(version);

        TocNodeModel tocNodeModel = new TocNodeModel();
        List<TocItemNodeModel> tocItemNodeList = new ArrayList<>();
        tocItemNodeList.addAll(
            iProgramData.getContentByDocFile(
                    iProgramData.getProgramItemVersion(versionedProgram, SubSystem.TD), manual, file));
        tocNodeModel.setTocItemNodeList(tocItemNodeList);
        tocNodeModel.setSuccess(true);

        return tocNodeModel;
    }

    /**
     * Retrieve the Table of Contents subtree for the specified Program/Manual/ParentTocId
     *
     * @param ssoId - User Id
     * @param portalId - Portal Id (CWC/GEHONDA)
     * @param program - TD Program (e.g. gek108749)
     * @param manual - TD Manual (e.g. gek100700)
     * @param file - File name of the document for which we need to get the TOC Path for
     * @return TocNodeModel - Subtree nodes
     *
     *
     *
     * Remove with method when US448283 feature flag is removed
     */
    //Remove this while clearing featureFlagUS448283
    @Deprecated
    @Override
    @LogExecutionTime
    public TocNodeModel getContentByDocFile(String ssoId, String portalId, String program,
        String manual, String file, Map<String, String> queryParams) throws TechpubsException {

        if (ssoId == null || ssoId.isEmpty() || portalId == null || portalId.isEmpty()) {
            log.error(GET_CONTENT_BY_DOC_FILE + TechpubsException.TechpubsAppError.NOT_AUTHORIZED
                .getErrorCode()
                + CLOSE_DASH + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg()
                + PROGRAM_EQ
                + program + MANUAL_EQ + manual + FILE_EQ + file + CLOSE + SSO_ID + ssoId + PORTAL_ID
                + portalId
                + CLOSE);
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        // Ensure user has access to program
        if (!getAuthorizedPrograms(ssoId, portalId, SubSystem.TD).contains(program)) {
            log.error(
                GET_CONTENT_BY_DOC_FILE + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE
                    .getErrorCode()
                    + CLOSE_DASH + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE
                    .getErrorMsg()
                    + PROGRAM_EQ + program + MANUAL_EQ + manual + FILE_EQ + file + CLOSE);
            throw new TechpubsException(TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE);
        }

        TocNodeModel tocNodeModel = new TocNodeModel();
        List<TocItemNodeModel> tocItemNodeList = new ArrayList<>();
        try {
            tocItemNodeList.addAll(
                iProgramData.getContentByDocFile(
                    iProgramData.getProgramItem(program, SubSystem.TD), manual, file));
        } catch (DocumentException e) {
            log.info(e.getMessage());
        } catch (IOException e) {
            log.info(e.getMessage());
        }
        tocNodeModel.setTocItemNodeList(tocItemNodeList);
        tocNodeModel.setSuccess(true);

        return tocNodeModel;
    }

    /**
     * Retrieve the high level Table of Contents for the specified manual
     *
     * @param ssoId - User Id
     * @param portalId - Portal Id (CWC/GEHONDA)
     * @param program - TD Program (e.g. gek108749)
     * @param manual - TD Manual (e.g. gek100700)
     * @return TocModel - List of Table of Content Entries
     */
    //Remove this while clearing featureFlagUS448283
    @Deprecated
    @Override
    @LogExecutionTime
    public TocItemModelList getContentByManualTD(String ssoId, String portalId, String program,
        String version, String manual, Map<String, String> queryParams) throws TechpubsException {
        if (ssoId == null || ssoId.isEmpty() || portalId == null || portalId.isEmpty()) {
            log.error(
                GET_CONTENT_BY_MANUAL_TD + TechpubsException.TechpubsAppError.NOT_AUTHORIZED
                    .getErrorCode()
                    + CLOSE_DASH + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg()
                    + PROGRAM_EQ
                    + program + MANUAL_EQ + manual + CLOSE + SSO_ID + ssoId + PORTAL_ID + portalId
                    + CLOSE);
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        // Ensure user has access to program
        if (!getAuthorizedPrograms(ssoId, portalId, SubSystem.TD).contains(program)) {
            log.error(
                GET_CONTENT_BY_MANUAL_TD + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE
                    .getErrorCode()
                    + CLOSE_DASH + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE
                    .getErrorMsg()
                    + PROGRAM_EQ + program + MANUAL_EQ + manual + CLOSE);
            throw new TechpubsException(TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE);
        }

        TocItemModelList tocModel = new TocItemModelList();
        List<TocItemModel> tocItemList = new ArrayList<>();
        tocItemList.addAll(iProgramData.getContentByManual(iProgramData
                .getProgramItemVersion(new BookcaseVersionEntity(program, version), SubSystem.TD),
            manual));
        tocModel.setTocItemList(tocItemList);
        tocModel.setSuccess(true);

        return tocModel;
    }

    private List<S3ObjectSummary> getS3Objects(String s3Folder ){
        AmazonS3 amazonS3Client = null;
        try {
            amazonS3Client = amazonS3ClientFactory.getS3Client();
        } catch (TechpubsException techpubsException) {
            techpubsException.printStackTrace();
        }
        String bucket = s3Config.getS3Bucket().getBucketName();
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
            .withBucketName(bucket)
            .withPrefix(s3Folder);



        ObjectListing objects = null;
        final List<S3ObjectSummary> objectSummaries = new ArrayList<>();
        log.info("Getting S3 Files");
        try {
            objects = amazonS3Client.listObjects(listObjectsRequest);
            objectSummaries.addAll(objects.getObjectSummaries());

            while (objects.isTruncated()) {
                log.info("Getting more S3 Files.");
                objects = amazonS3Client.listNextBatchOfObjects(objects);
                objectSummaries.addAll(objects.getObjectSummaries());
            }
        } catch (Exception ex) {
            log.error("Error getting S3 objects. " + ex.getMessage());
        }

        if (objectSummaries.size() == 0) {
            log.error("ERR_FOLDER_DOES_NOT_EXIST - " + s3Folder);
        }

        return objectSummaries;

    }

    @Override
    @LogExecutionTime
    public DocumentDataTableModel getDownloadDocuments(String ssoId, String portalId,
        String program,
        String downloadtype, String type, Map<String, String> queryParams)
        throws TechpubsException, InterruptedException {

        if (ssoId == null || ssoId.isEmpty() || portalId == null || portalId.isEmpty()) {
            log.error(GET_DOWNLOAD_DOCUMENTS + TechpubsException.TechpubsAppError.NOT_AUTHORIZED
                .getErrorCode()
                + CLOSE_DASH + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg()
                + PROGRAM_EQ
                + program + DOWNLOAD_TYPE + downloadtype + TYPE2 + type + SSO_ID2 + ssoId
                + PORTAL_ID + portalId
                + CLOSE);
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        // Validate request (DVD:MANUAL)
        if ((DVD.equalsIgnoreCase(downloadtype) && MANUAL.equalsIgnoreCase(type))) {
            log.error(INVALID_REQUEST_DVD + TechpubsException.TechpubsAppError.INTERNAL_ERROR
                .getErrorCode()
                + CLOSE_DASH + TechpubsException.TechpubsAppError.INTERNAL_ERROR.getErrorMsg()
                + PROGRAM_EQ
                + program + DOWNLOADTYPE2 + downloadtype + TYPE2 + type + CLOSE);
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
        }

        // Ensure user has access to program
        if (!getAuthorizedPrograms(ssoId, portalId, SubSystem.TD).contains(program)) {
            log.error(
                GET_DOWNLOAD_DOCUMENTS + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE
                    .getErrorCode()
                    + CLOSE_DASH + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE
                    .getErrorMsg()
                    + PROGRAM_EQ + program + DOWNLOAD_TYPE + downloadtype + TYPE2 + type + CLOSE);
            throw new TechpubsException(TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE);
        }

        // validate queryparms
        techpubsAppUtil.validateDatatableParameters(queryParams);

        ProgramItemModel programItem = null;
        try {
            programItem = iProgramData.getProgramItem(program, SubSystem.TD);
        } catch (DocumentException e) {
            log.info(e.getMessage());
        } catch (IOException e) {
            log.info(e.getMessage());
        }

        if (programItem == null) {
            log.error("getDownloadDocuments (Invalid Program) ("
                + TechpubsException.TechpubsAppError.INTERNAL_ERROR.getErrorCode() + CLOSE_DASH
                + TechpubsException.TechpubsAppError.INTERNAL_ERROR.getErrorMsg() + PROGRAM_EQ
                + program
                + DOWNLOADTYPE2 + downloadtype + TYPE2 + type + CLOSE);
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
        }

       Map<String, List<DocumentItemModel>> bookKeyToDocumentMap = getDownloadDocumentsDataByBookKey(programItem, downloadtype,
            type, queryParams);

        List<DocumentItemModel> enabledDocs = iPageblkEnablement.getEnabledFilesForOnlineFilename(ssoId, portalId, program,
            programItem, bookKeyToDocumentMap, type);

        //Logic starts for filtering out documents which are not published yet
        if(downloadOverlayFeatureFlag){
            List<PageblkDetailsDAO> pageblkUnapprovedList;
            List<String> approvedPageblkList = new ArrayList<>();
            List<DocumentItemModel> finalReturnedList = new ArrayList<>();

            log.info("Size of enabledDocs returned from TPS: " , enabledDocs.size());

            //Get all published document for bookcase key and type
            if (IC.equalsIgnoreCase(type) || TR.equalsIgnoreCase(type)) {
                pageblkUnapprovedList = iPageBlkData.findGroupByPageBlksByBookcaseAndTypeAndApprovedForPublish(program, type, false,
                        programItem.getProgramOnlineVersion());
            } else {
                pageblkUnapprovedList = iPageBlkData.findSbPageBlksByBookcaseAndTypeAndApprovedForPublish(program, type, false);
            }
            log.info("Number of pageblks returned which are not approved yet: " , pageblkUnapprovedList.size());

            if("sb".equalsIgnoreCase(type)) {
                //Return all approved SB pageblk keys for any of the versions
                approvedPageblkList = iPageBlkData.findPageBlksKeysForSbType(program);
            }

            if((CollectionUtils.isNotEmpty(pageblkUnapprovedList))){
                for(PageblkDetailsDAO unapprovedPageblk : pageblkUnapprovedList){
                    List<String> finalApprovedPageblkList = approvedPageblkList;
                    enabledDocs.forEach(documentItemModel -> {
                        DocumentItemTDModel documentModel = (DocumentItemTDModel) documentItemModel;

                        if("sb".equalsIgnoreCase(documentModel.getType())){
                            DocumentItemSBCatalogModel documentItemSBCatalogModel = (DocumentItemSBCatalogModel) documentModel;
                                if(!finalApprovedPageblkList.contains(unapprovedPageblk.getKey())) {
                                    if(!StringUtils.isEmpty(unapprovedPageblk.getFileName())) {
                                        if (unapprovedPageblk.getFileName().contains(documentItemSBCatalogModel.getCatalogkey()))
                                            finalReturnedList.add(documentModel);
                                    }
                                }

                        }

                        if("tr".equalsIgnoreCase(documentModel.getType())){
                            DocumentItemTRCatalogModel documentItemTRCatalogModel = (DocumentItemTRCatalogModel) documentModel;
                            if(!StringUtils.isEmpty(unapprovedPageblk.getFileName())) {
                                if (unapprovedPageblk.getFileName().contains(documentItemTRCatalogModel.getCatalogkey()))
                                    finalReturnedList.add(documentModel);
                            }
                        }

                        if("ic".equalsIgnoreCase(documentModel.getType())){
                            DocumentItemICCatalogModel documentItemICCatalogModel = (DocumentItemICCatalogModel) documentModel;
                            if(!StringUtils.isEmpty(unapprovedPageblk.getFileName())) {
                                if (unapprovedPageblk.getFileName().contains(documentItemICCatalogModel.getCatalogkey()))
                                    finalReturnedList.add(documentModel);
                            }
                        }

                    });
                    finalReturnedList.stream().forEach(o -> enabledDocs.remove(o));
                }
            }
        }
        //Logic ends for filtering out documents which are not published yet

        int resultSize = enabledDocs.size();
        if (resultSize > 1) {
            techpubsAppUtil.sortDocumentItems(enabledDocs, queryParams);
        }

        int iDisplayLength = Integer.parseInt(queryParams.get("iDisplayLength"));
        int iDisplayStart = Integer.parseInt(queryParams.get("iDisplayStart"));
        String sEcho = queryParams.get("sEcho");
        DocumentDataTableModel documentDataTable = new DocumentDataTableModel();
        documentDataTable.setIDisplayLength(iDisplayLength);
        documentDataTable.setIDisplayStart(iDisplayStart);
        documentDataTable.setITotalDisplayRecords(resultSize);
        documentDataTable.setITotalRecords(resultSize);
        documentDataTable.setSEcho(sEcho);

        List<DocumentItemModel> paginatedResponse = enabledDocs.subList((iDisplayStart > resultSize ? resultSize : iDisplayStart),
            (iDisplayStart + iDisplayLength > resultSize ? resultSize
                : iDisplayStart + iDisplayLength));

        documentDataTable.setSuccess(true);

        String s3keyPrefix = programItem.getProgramDocnbr() + "/program/doc/" ;

        paginatedResponse.stream().parallel().forEach(f -> {
            DocumentItemTDModel documentModel = (DocumentItemTDModel) f;
            String newS3Key = s3keyPrefix;
            switch (documentModel.getType()) {
                case "manual":
                    DocumentItemSourceCatalogModel documentItemSourceCatalogModel = (DocumentItemSourceCatalogModel) f;
                    newS3Key = s3keyPrefix + documentItemSourceCatalogModel.getManualDocnbr() + "/"
                        + documentItemSourceCatalogModel.getSourcefilename();
                    break;
                case "SB":
                    DocumentItemSBCatalogModel documentItemSBCatalogModel = (DocumentItemSBCatalogModel) f;
                    newS3Key =
                        s3keyPrefix + "sbs/" + documentItemSBCatalogModel.getSourcefilename();
                    break;
                case "IC":
                    DocumentItemICCatalogModel documentItemICCatalogModel = (DocumentItemICCatalogModel) f;
                    newS3Key = s3keyPrefix + documentItemICCatalogModel.getManualDocnbr() + "/"
                        + documentItemICCatalogModel.getSourcefilename();
                    break;
                case "TR":
                    DocumentItemTRCatalogModel documentItemTRCatalogModel = (DocumentItemTRCatalogModel) f;
                    newS3Key = s3keyPrefix + documentItemTRCatalogModel.getManualDocnbr() + "/"
                        + documentItemTRCatalogModel.getSourcefilename();
            }

            f.setFileSize(this.getFileSize(newS3Key));
        });


        documentDataTable.setDocumentItemList(paginatedResponse);

        return documentDataTable;
    }

    @Override
    @LogExecutionTime
    public File getDownloadDocumentsCSV(String ssoId, String portalId, String program,
        String downloadtype, String type,
        Map<String, String> queryParams) throws TechpubsException {

        if (ssoId == null || ssoId.isEmpty() || portalId == null || portalId.isEmpty()) {
            log.error(
                "getDownloadDocumentsCSV (" + TechpubsException.TechpubsAppError.NOT_AUTHORIZED
                    .getErrorCode()
                    + CLOSE_DASH + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg()
                    + PROGRAM_EQ
                    + program + DOWNLOAD_TYPE + downloadtype + TYPE2 + type + SSO_ID2 + ssoId
                    + PORTAL_ID + portalId
                    + CLOSE);
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        // Validate request (DVD:MANUAL is invalid)
        if (DVD.equalsIgnoreCase(downloadtype) && MANUAL.equalsIgnoreCase(type))
        {
            log.error(
                "getDownloadDocumentsCSV (Invalid Request (DVD:MANUAL and SOURCE:LR are invalid)) ("
                    + TechpubsException.TechpubsAppError.INTERNAL_ERROR.getErrorCode() + CLOSE_DASH
                    + TechpubsException.TechpubsAppError.INTERNAL_ERROR.getErrorMsg() + PROGRAM_EQ
                    + program
                    + DOWNLOADTYPE2 + downloadtype + TYPE2 + type + CLOSE);
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
        }

        // Ensure user has access to program
        if (!getAuthorizedPrograms(ssoId, portalId, SubSystem.TD).contains(program)) {
            log.error("getDownloadDocumentsCSV ("
                + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE.getErrorCode()
                + CLOSE_DASH
                + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE.getErrorMsg()
                + PROGRAM_EQ + program
                + DOWNLOAD_TYPE + downloadtype + TYPE2 + type + CLOSE);
            throw new TechpubsException(TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE);
        }

        ProgramItemModel programItem = null;

        try {
            programItem = iProgramData.getProgramItem(program, SubSystem.TD);
        } catch (DocumentException e) {
            log.info(e.getMessage());
        } catch (IOException e) {
            log.info(e.getMessage());
        }

        if (programItem == null) {
            log.error("getDownloadDocumentsCSV (Invalid Program) ("
                + TechpubsException.TechpubsAppError.INTERNAL_ERROR.getErrorCode() + CLOSE_DASH
                + TechpubsException.TechpubsAppError.INTERNAL_ERROR.getErrorMsg() + PROGRAM_EQ
                + program
                + DOWNLOADTYPE2 + downloadtype + TYPE2 + type + CLOSE);
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
        }

        String linkUrlBase = (queryParams.get("url") != null ? queryParams.get("url") : "");

        Map<String, List<DocumentItemModel>> bookKeyToDocumentMap = getDownloadDocumentsDataByBookKey(programItem, downloadtype,
            type, queryParams);

        List<DocumentItemModel> docItemList = iPageblkEnablement.getEnabledFilesForOnlineFilename(ssoId, portalId, program, programItem, bookKeyToDocumentMap, type);

        if(downloadOverlayFeatureFlag){
            List<PageblkDetailsDAO> unapprovedPageblkList ;
            List<DocumentItemModel> finalReturnedList = new ArrayList<>();
            List<String> approvedPageblkList = new ArrayList<>();
            log.info("Size of enabledDocs returned from TPS: " , docItemList.size());

            //Get all published document for bookcase key and type
            if (IC.equalsIgnoreCase(type) || TR.equalsIgnoreCase(type)) {
                unapprovedPageblkList = iPageBlkData.findGroupByPageBlksByBookcaseAndTypeAndApprovedForPublish(program, type, false,
                        programItem.getProgramOnlineVersion());
            } else {
                unapprovedPageblkList = iPageBlkData.findSbPageBlksByBookcaseAndTypeAndApprovedForPublish(program, type, false);
            }

            log.info("Number of pageblks returned which are not approved yet: " , unapprovedPageblkList.size());

            if("sb".equalsIgnoreCase(type)) {
                //Return all approved SB pageblk keys for any of the versions
                approvedPageblkList = iPageBlkData.findPageBlksKeysForSbType(program);
            }
            if((CollectionUtils.isNotEmpty(unapprovedPageblkList))){
                for(PageblkDetailsDAO unapprovedPageblk : unapprovedPageblkList){
                    List<String> finalApprovedPageblkList = approvedPageblkList;
                    docItemList.forEach(documentItemModel -> {
                        DocumentItemTDModel documentModel = (DocumentItemTDModel) documentItemModel;

                        if("sb".equalsIgnoreCase(documentModel.getType())) {
                            DocumentItemSBCatalogModel documentItemSBCatalogModel = (DocumentItemSBCatalogModel) documentModel;
                            if(!finalApprovedPageblkList.contains(unapprovedPageblk.getKey())) {
                                if (unapprovedPageblk.getFileName().contains(documentItemSBCatalogModel.getCatalogkey()))
                                    finalReturnedList.add(documentModel);
                            }
                        }

                        if("tr".equalsIgnoreCase(documentModel.getType())){
                            DocumentItemTRCatalogModel documentItemTRCatalogModel = (DocumentItemTRCatalogModel) documentModel;
                            if(unapprovedPageblk.getFileName().contains(documentItemTRCatalogModel.getCatalogkey()))
                                finalReturnedList.add(documentModel);
                        }

                        if("ic".equalsIgnoreCase(documentModel.getType())){
                            DocumentItemICCatalogModel documentItemICCatalogModel = (DocumentItemICCatalogModel) documentModel;
                            if(unapprovedPageblk.getFileName().contains(documentItemICCatalogModel.getCatalogkey()))
                                finalReturnedList.add(documentModel);
                        }

                    });
                    finalReturnedList.stream().forEach(o -> docItemList.remove(o));
                }
            }
        }

        List<Map<String, String>> dataList = new ArrayList<>();
        HashMap<String, String> csvFieldMap = new LinkedHashMap<>();
        Map<String, String> csvComputedFieldMap = new LinkedHashMap<>();

        csvFieldMap = setCsvFieldMap(type, csvFieldMap);
        csvComputedFieldMap = setCsvComputedFieldMap(type, csvComputedFieldMap);

        if (CollectionUtils.isNotEmpty(docItemList)) {
            Class<?> clazz = docItemList.get(0).getClass();
            for (DocumentItemModel documentItem : docItemList) {
                Map<String, String> dataMap = new LinkedHashMap<>();
                for (String fieldName : csvFieldMap.keySet()) {
                    String methodName =
                        "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    try {
                        Method method = clazz.getMethod(methodName, (Class[]) null);
                        if (method != null) {
                            String value = ((String) method.invoke(documentItem, (Object[]) null));
                            dataMap.put(fieldName, (value == null ? "" : value));
                        }
                    } catch (Exception e) { // Method does not exist
                        log.error(
                            "getDownloadDocumentsCSV (Invalid Export Column) - " + e.getMessage()
                                + PROGRAM_EQ
                                + program + DOWNLOADTYPE2 + downloadtype + TYPE2 + type + COLUMN
                                + fieldName + CLOSE,
                            e);
                        throw new TechpubsException(
                            TechpubsException.TechpubsAppError.INTERNAL_ERROR);
                    }
                }
                // Add additional/compound fields to map
                if (SB.equalsIgnoreCase(type)) {
                    DocumentItemSBCatalogModel documentItemSBCatalog = (DocumentItemSBCatalogModel) documentItem;
                    dataMap.put(FULL_SB_NBR,
                        documentItem.getId() + " R" + documentItemSBCatalog.getVersion());
                    dataMap.put(LINK, linkUrlBase + documentItem.getResourceUri());
                } else if (IC.equalsIgnoreCase(type)) {
                    DocumentItemICCatalogModel documentItemICCatalog = (DocumentItemICCatalogModel) documentItem;
                    String manualType = "";
                    Element manualElement = (Element) programItem.getTocRoot()
                        .selectSingleNode(DOCNBR2 + documentItemICCatalog.getManualDocnbr() + "']");
                    if (manualElement != null) {
                        manualType = (manualElement.attributeValue(TYPE3) != null
                            ? manualElement.attributeValue(TYPE3).split("-")[0] : "").toUpperCase();
                    }
                    dataMap.put(ATANUM, "ATA-" + documentItemICCatalog.getAtanum());
                    dataMap.put(MANUAL_TYPE, manualType);
                    dataMap
                        .put(DISPLAY_MANUAL, documentItemICCatalog.getManualDocnbr().toUpperCase());
                    dataMap.put(LINK, linkUrlBase + documentItem.getResourceUri());
                } else if (TR.equalsIgnoreCase(type)) {
                    DocumentItemTRCatalogModel documentItemTRCatalog = (DocumentItemTRCatalogModel) documentItem;
                    String manualType = "";
                    Element manualElement = (Element) programItem.getTocRoot()
                        .selectSingleNode(DOCNBR2 + documentItemTRCatalog.getManualDocnbr() + "']");
                    if (manualElement != null) {
                        manualType = (manualElement.attributeValue(TYPE3) != null
                            ? manualElement.attributeValue(TYPE3).split("-")[0] : "").toUpperCase();
                    }
                    dataMap.put(ATANUM, "ATA-" + documentItemTRCatalog.getAtanum());
                    dataMap.put(MANUAL_TYPE, manualType);
                    dataMap
                        .put(DISPLAY_MANUAL, documentItemTRCatalog.getManualDocnbr().toUpperCase());
                    dataMap.put(LINK, linkUrlBase + documentItem.getResourceUri());
                } else if (MANUAL.equalsIgnoreCase(type)) {
                    DocumentItemSourceCatalogModel documentItemSourceCatalog = (DocumentItemSourceCatalogModel) documentItem;
                    if (dataMap.get(TITLE) != null && dataMap.get(TITLE)
                        .equals(dataMap.get(SOURCEFILENAME))) {
                        // Use manual title
                        dataMap.put(TITLE, documentItemSourceCatalog.getManualtitle());
                    }
                } else if (LR.equalsIgnoreCase(type)) {
                    DocumentItemLRCatalogModel documentItemLRCatalog = (DocumentItemLRCatalogModel) documentItem;
                    dataMap
                        .put(DISPLAY_MANUAL, documentItemLRCatalog.getManualDocnbr().toUpperCase());
                }
                dataList.add(dataMap);
            }
            csvFieldMap.putAll(csvComputedFieldMap); // Add computed fields to
            // map
        } else {
            Map<String, String> dataMap = new HashMap<>();
            for (String fieldName : csvFieldMap.keySet()) {
                dataMap.put(fieldName, "");
            }
            dataList.add(dataMap);
        }

        File csvFile = null;

        try {
            csvFile = iCSVExportApp
                .csvExport(ssoId, "DOCUMENT_DOWNLOAD_EXPORT", dataList, new ArrayList<String>(),
                    csvFieldMap);
        } catch (Exception e) {
            log.error("getDownloadDocumentsCSV (CSV Error) ("
                + TechpubsException.TechpubsAppError.INTERNAL_ERROR.getErrorCode() + CLOSE_DASH
                + TechpubsException.TechpubsAppError.INTERNAL_ERROR.getErrorMsg() + PROGRAM_EQ
                + program
                + DOWNLOADTYPE2 + downloadtype + TYPE2 + type + CLOSE);
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR, e);
        }

        return csvFile;
    }

    private Map<String, String> setCsvComputedFieldMap(String type,
        Map<String, String> csvComputedFieldMap) {
        if (SB.equalsIgnoreCase(type)) {
            csvComputedFieldMap.put(FULL_SB_NBR, SB_NBR);
            csvComputedFieldMap.put(LINK, MY_GEA_LINK);
        } else if (IC.equalsIgnoreCase(type) || TR.equalsIgnoreCase(type)) {
            csvComputedFieldMap.put(ATANUM, ATA_NBR);
            csvComputedFieldMap.put(MANUAL_TYPE, MANUAL_TYPE2);
            csvComputedFieldMap.put(DISPLAY_MANUAL, DOC_NBR);
            csvComputedFieldMap.put(LINK, MY_GEA_LINK);
        } else if (LR.equalsIgnoreCase(type)) {
            csvComputedFieldMap.put(DISPLAY_MANUAL, DOCNBR);
        }
        return csvComputedFieldMap;
    }

    private HashMap<String, String> setCsvFieldMap(String type,
        HashMap<String, String> csvFieldMap) {
        if (SB.equalsIgnoreCase(type)) {
            csvFieldMap.put(TITLE, TITLE2);
            csvFieldMap.put(TYPE3, TYPE4);
            csvFieldMap.put(PROGRAMTITLE, ENGINE_MODEL);
            csvFieldMap.put(CATEGORY2, CATEGORY);
            csvFieldMap.put(RELEASE_DATE, ISSUE_DATE);
        } else if (IC.equalsIgnoreCase(type)) {
            csvFieldMap.put(TYPE3, TYPE4);
            csvFieldMap.put(PROGRAMTITLE, ENGINE_MODEL);
            csvFieldMap.put(RELEASE_DATE, ISSUE_DATE);
            csvFieldMap.put(REVNBR, REV_NBR);
            csvFieldMap.put(REVISION_DATE, REV_DATE);
            csvFieldMap.put(TITLE, TITLE2);
        } else if (TR.equalsIgnoreCase(type)) {
            csvFieldMap.put(TYPE3, TYPE4);
            csvFieldMap.put(PROGRAMTITLE, ENGINE_MODEL);
            csvFieldMap.put(ID, TR_NBR);
            csvFieldMap.put(RELEASE_DATE, ISSUE_DATE);
            csvFieldMap.put(TITLE, TITLE2);
        } else if (MANUAL.equalsIgnoreCase(type)) {
            csvFieldMap.put(TITLE, TITLE);
            csvFieldMap.put(MANUAL_DOCNBR, DOCNBR);
            csvFieldMap.put(SOURCEFILENAME, FILENAME);
            csvFieldMap.put(REVISION_DATE, REVDATE);
            csvFieldMap.put(PUBCWCDATE, CWC_ADDED_DATE);
        } else if (LR.equalsIgnoreCase(type)) {
            csvFieldMap.put(TITLE, TITLE);
            csvFieldMap.put(SOURCEFILENAME, FILENAME);
            csvFieldMap.put(REVISION_DATE, REVDATE);
            csvFieldMap.put(PUBCWCDATE, CWC_ADDED_DATE);
        }
        return csvFieldMap;
    }


    private Map<String, List<DocumentItemModel>>  getDownloadDocumentsDataByBookKey(ProgramItemModel programItem,
        String downloadtype, String type, Map<String, String> queryParams) throws TechpubsException {


        Map<String, String> commonCriteriaMap = setupCommonCriteriaMap(downloadtype, type,
            queryParams);

        List<String> bookKeys = getRequestedBooksForSearch(programItem, downloadtype, type, queryParams);

        Map<String, List<DocumentItemModel>> bookKeyToDocumentMap = new HashedMap();
        for (String bookKey : bookKeys){
            Map<String, String> criteriaMap = new HashMap<>();
            criteriaMap = setCriteriaMap(type, queryParams, programItem, bookKey, criteriaMap,
                commonCriteriaMap);

            List<DocumentItemModel> tpsDocuments = new ArrayList<>();
            tpsDocuments = setDocItemList(tpsDocuments, downloadtype, type, criteriaMap, programItem,
                queryParams);

            if(tpsDocuments.size() > 0)
                bookKeyToDocumentMap.put(bookKey, tpsDocuments);
        }

        return bookKeyToDocumentMap;
    }

    @Override
    @LogExecutionTime
    public DocumentDownloadModel getDownloadResourceTD(String ssoId, String portalId,
        String program,
        String downloadtype, String type, String files) throws TechpubsException {

        if (ssoId == null || ssoId.isEmpty() || portalId == null || portalId.isEmpty()) {
            log.error(
                GET_DOWNLOAD_RESOURCE_TD + TechpubsException.TechpubsAppError.NOT_AUTHORIZED
                    .getErrorCode()
                    + CLOSE_DASH + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg()
                    + PROGRAM_EQ
                    + program + DOWNLOADTYPE2 + downloadtype + TYPE5 + type + FILENAMES + files
                    + " " + SSO_ID + ssoId
                    + PORTAL_ID + portalId + CLOSE);
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        // Ensure user has access to program
        if (!getAuthorizedPrograms(ssoId, portalId, SubSystem.TD).contains(program)) {
            log.error(
                GET_DOWNLOAD_RESOURCE_TD + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE
                    .getErrorCode()
                    + CLOSE_DASH + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE
                    .getErrorMsg()
                    + PROGRAM_EQ + program + DOWNLOADTYPE2 + downloadtype + TYPE5 + type + FILENAMES
                    + files
                    + " " + SSO_ID + ssoId + PORTAL_ID + portalId + CLOSE);
            throw new TechpubsException(TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE);
        }

        ProgramItemModel programItem = null;

        try {
            programItem = iProgramData.getProgramItem(program, SubSystem.TD);
        } catch (DocumentException e) {
            log.info(e.getMessage());
        } catch (IOException e) {
            log.info(e.getMessage());
        }

        List<String> fileList = (files != null
            ? new ArrayList<String>(new HashSet<String>(Arrays.asList(files.split("\\|"))))
            : new ArrayList<String>());

        DocumentDownloadModel documentDownload = new DocumentDownloadModel();

        if (SOURCE.equalsIgnoreCase(downloadtype) && fileList.size() == 1) {
            String[] fileParts = fileList.get(0).split(STR);
            if (fileParts.length != 2) {
                log.error(
                    GET_DOWNLOAD_RESOURCE_TD + "Invalid manual:filename" + PROGRAM_EQ + program
                        + ",filename="
                        + fileList.get(0) + CLOSE);
                throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
            }
            //fileParts[1] should be the file name the user is trying to download
            documentDownload.setZipFilename(fileParts[1]);
            documentDownload.
                setZipFileByteArray(iResourceData.
                    getBinaryResourceTDs3(programItem, fileParts[0], fileParts[1]));




        } else {
            StringBuilder zipFilenameSB = new StringBuilder(ssoId).append("_")
                .append(programItem.getProgramDocnbr())
                .append("_").append(downloadtype).append("_").append(System.currentTimeMillis())
                .append(".")
                .append((DVD.equals(downloadtype) ? "geae" : "zip"));
            documentDownload.setZipFilename(zipFilenameSB.toString());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zos = null;
            zos = new ZipOutputStream(baos);
            try {
                zos = new ZipOutputStream(baos);

                for (String filename : fileList) {
                    String[] fileParts = filename.split(STR);
                    if (fileParts.length != 2) {
                        log.error(
                            GET_DOWNLOAD_RESOURCE_TD + "Invalid manual:filename" + PROGRAM_EQ
                                + program
                                + ",filename=" + filename + CLOSE);
                        throw new TechpubsException(
                            TechpubsException.TechpubsAppError.INTERNAL_ERROR);
                    }
                    //fileParts[1] should be the file name the user is trying to download
                    zos.putNextEntry(new ZipEntry(fileParts[1]));
                    zos.write(iResourceData.getBinaryResourceTDs3(programItem, fileParts[0], fileParts[1]));
                    zos.closeEntry();
                }

                if (DVD.equalsIgnoreCase(downloadtype)) {
                    // Add info text file to zip file for DVD updates
                    ByteArrayOutputStream baosInfo = new ByteArrayOutputStream();
                    BufferedWriter bwInfo = new BufferedWriter(new OutputStreamWriter(baosInfo));
                    bwInfo.write(programItem.getDvdInfoTxt() + ",");
                    if (!SB.equalsIgnoreCase(type)) {
                        bwInfo.write(programItem.getDvdVersion());
                    }
                    bwInfo.close();

                    zos.putNextEntry(new ZipEntry("info.txt"));
                    zos.write(baosInfo.toByteArray());
                    zos.closeEntry();
                }

                zos.close();

                documentDownload.setZipFileByteArray(baos.toByteArray());

                baos.close();
            } catch (IOException e) {
                log.error(
                    GET_DOWNLOAD_RESOURCE_TD + e.getMessage() + PROGRAM_EQ + program + FILENAMES
                        + files
                        + CLOSE + e);
                throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
            }
        }

        return documentDownload;
    }

    private List<String> getRequestedBooksForSearch(ProgramItemModel programItem,
        String downloadtype, String type, Map<String, String> queryParams){
        // If manual criterion is 'all', determine list of manual document
        // numbers
        List<String> manualList = new ArrayList<>();
        if (ALL.equalsIgnoreCase(queryParams.get(MANUAL))) {
            Map<String, String> downloadManualMap = programItem.getDownloadTypeMap()
                .get((downloadtype + "|" + type).toLowerCase());

            if (downloadManualMap != null) {
                for (String manualDocnbr : downloadManualMap.keySet()) {
                    if (!ALL.equalsIgnoreCase(manualDocnbr)) {
                        manualList.add(manualDocnbr);
                    }
                }
            }
        } else if (queryParams.get(MANUAL) != null) {
            manualList.add(queryParams.get(MANUAL));
        }

        return manualList;
    }

    private Map<String, String> setCriteriaMap(String type, Map<String, String> queryParams,
        ProgramItemModel programItem, String manualDocnbr, Map<String, String> criteriaMap,
        Map<String, String> commonCriteriaMap) {
        criteriaMap.putAll(commonCriteriaMap); // Add common criteria
        // manual Document which begin with "doc:" are of catalog type 'doc'
        criteriaMap.put("catalogtype", (manualDocnbr.startsWith("doc:") ? "doc" : type));
        criteriaMap.put("manualdocnbr", manualDocnbr.substring(manualDocnbr.indexOf(STR) + 1));
        // Revision: Current/Previous not applicable for sb
        if (!SB.equalsIgnoreCase(type) && queryParams.get(REVISION) != null
            && !ALL.equalsIgnoreCase(queryParams.get(REVISION))) {
            // Get current manual revision
            Element manualElement = (Element) programItem.getTocRoot()
                .selectSingleNode(DOCNBR2 + manualDocnbr + "']");
            if (manualElement != null && manualElement.attributeValue(REVNBR) != null
                && StringUtils.isInteger(manualElement.attributeValue(REVNBR))) {
                // IC revision numbers are always 1 greater
                int offset = ((IC.equalsIgnoreCase(type) || TR.equalsIgnoreCase(type) )? 1 : 0);
                if ("previous".equalsIgnoreCase(queryParams.get(REVISION))) {
                    if(IC.equalsIgnoreCase(type) || TR.equalsIgnoreCase(type)) {
                        criteriaMap.put("previousrevnbrlow",
                            String.valueOf(
                                Integer.valueOf(manualElement.attributeValue(REVNBR)) - 1));

                    }
                        criteriaMap.put("previousrevnbr",
                            String.valueOf(
                                Integer.valueOf(manualElement.attributeValue(REVNBR)) + offset - 1));

                } else if ("current".equalsIgnoreCase(queryParams.get(REVISION))) {
                    criteriaMap.put("currentrevnbr",
                        String.valueOf(
                            Integer.valueOf(manualElement.attributeValue(REVNBR)) + offset));
                }
            }
        }
        return criteriaMap;
    }

    private List<DocumentItemModel> setDocItemList(List<DocumentItemModel> docItemList,
        String downloadtype,
        String type, Map<String, String> criteriaMap, ProgramItemModel programItem,
        Map<String, String> queryParams) {
        if (SOURCE.equalsIgnoreCase(downloadtype) && MANUAL.equalsIgnoreCase(type)
            && !"doc".equals(criteriaMap.get("catalogtype"))) {
            docItemList.addAll(
                iDocumentData.getCatalogFileDocuments(programItem, downloadtype, criteriaMap));
        } else {
            List<DocumentItemModel> catalogDocumentList = iDocumentData
                .getCatalogDocuments(programItem, downloadtype,
                    criteriaMap);

            if (SB.equalsIgnoreCase(type) && "current"
                .equalsIgnoreCase(queryParams.get(REVISION))) {
                // filter out past revisions. This assumes list is sorted by
                // sbnbr (id)
                String previousSbnbr = null;
                String currentSbnbr = null;
                Iterator<DocumentItemModel> catalogDocumentIter = catalogDocumentList.iterator();
                while (catalogDocumentIter.hasNext()) {
                    currentSbnbr = catalogDocumentIter.next().getId();
                    if (currentSbnbr != null && currentSbnbr.equals(previousSbnbr)) {
                        catalogDocumentIter.remove();
                    } else {
                        previousSbnbr = currentSbnbr;
                    }
                }
            }

            docItemList.addAll(catalogDocumentList);
        }
        return docItemList;
    }

    private Map<String, String> setupCommonCriteriaMap(String downloadtype, String type,
        Map<String, String> queryParams) throws TechpubsException {
        // Setup the common criteria
        Map<String, String> commonCriteriaMap = new HashMap<>();

        if (SB.equalsIgnoreCase(type)) {
            commonCriteriaMap.put(CATEGORY2, queryParams.get(CATEGORY2));
            if (!ALL.equalsIgnoreCase(queryParams.get(SBTYPE))) {
                commonCriteriaMap.put(SBTYPE, queryParams.get(SBTYPE));
            }
        } else if (!LR.equalsIgnoreCase(type) && (IC.equalsIgnoreCase(type) || TR.equalsIgnoreCase(type) || SOURCE
            .equalsIgnoreCase(downloadtype))) {
            commonCriteriaMap.put(ACTIVEIND, Y); // IC/TR/Digital Exchange
            // are always active
        }
        // Determine release start and end dates
        String startdateKey = (SB.equalsIgnoreCase(type) ? REVSTARTDATE : RELSTARTDATE);
        String enddateKey = (SB.equalsIgnoreCase(type) ? REVENDDATE : RELENDDATE);
        if (queryParams.get(RELDATEFROM) != null) {
            if (!StringUtils.isDate(queryParams.get(RELDATEFROM), YYYY_MM_DD)) {
                log.error(
                    GET_DOWNLOAD_DOCUMENTS + TechpubsException.TechpubsAppError.INVALID_PARAMETER
                        .getErrorCode()
                        + CLOSE_DASH + TechpubsException.TechpubsAppError.INVALID_PARAMETER
                        .getErrorMsg()
                        + RELDATEFROM2 + queryParams.get(RELDATEFROM) + CLOSE);
                throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
            }
            commonCriteriaMap.put(startdateKey, queryParams.get(RELDATEFROM));
        }
        if (queryParams.get(RELDATETO) != null) {
            if (!StringUtils.isDate(queryParams.get(RELDATETO), YYYY_MM_DD)) {
                log.error("getDownloadDocumentsData ("
                    + TechpubsException.TechpubsAppError.INVALID_PARAMETER.getErrorCode()
                    + CLOSE_DASH
                    + TechpubsException.TechpubsAppError.INVALID_PARAMETER.getErrorMsg()
                    + " (reldateto="
                    + queryParams.get(RELDATETO) + CLOSE);
                throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
            }
            commonCriteriaMap.put(enddateKey, queryParams.get(RELDATETO));
        }
        return commonCriteriaMap;
    }

    @Override
    @LogExecutionTime
    public DocumentDataTableModel getDVDFileList(String ssoId, String portalId, String program,
        Map<String, String> queryParams) throws TechpubsException, IOException {
        final String methodName = "getDVDFileList";
        if (ssoId == null || ssoId.isEmpty() || portalId == null || portalId.isEmpty()) {
            log
                .error(methodName + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorCode()
                    + CLOSE_DASH + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg()
                    + PROGRAM_EQ
                    + program + SSO_ID2 + ssoId + PORTAL_ID + portalId
                    + CLOSE);
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        // To check authorization
        if (!getAuthorizedPrograms(ssoId, portalId, SubSystem.TD).contains(program)) {
            log.error(
                methodName + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE.getErrorCode()
                    + CLOSE_DASH + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE
                    .getErrorMsg()
                    + PROGRAM_EQ + program + CLOSE);
            throw new TechpubsException(TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE);
        }

        // validate queryparms
        techpubsAppUtil.validateDatatableParameters(queryParams);

        String revision = queryParams.get(REVISION);
        String userCurrentOrg = queryParams.get(CURRENT_ORG);

        //To set list of allowed Revision types for geae user
        Set<String> revisionNames = setGeaeRevision();

        //Validation - Customer should have access only to Current Revision type
        if (!(userCurrentOrg.equalsIgnoreCase(GEAE)) && !(revision.equalsIgnoreCase("Current"))) {
            log.error(methodName + " (Invalid Revision type for Customer) ("
                + TechpubsException.TechpubsAppError.INVALID_PARAMETER.getErrorCode() + CLOSE_DASH
                + TechpubsException.TechpubsAppError.INVALID_PARAMETER.getErrorMsg() + " Revision "
                + revision
                + CLOSE);
            throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }

        //Validation - to check Revision type for geae user
        if (userCurrentOrg.equalsIgnoreCase(GEAE) && !(revisionNames.contains(revision))) {
            log.error(methodName + " (Invalid Revision type for GEAE user) ("
                + TechpubsException.TechpubsAppError.INVALID_PARAMETER.getErrorCode() + CLOSE_DASH
                + TechpubsException.TechpubsAppError.INVALID_PARAMETER.getErrorMsg() + " Revision "
                + revision
                + CLOSE);
            throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);

        }

        List<OfflineDVDInfoDto> fileList;
        switch (revision) {
            case AppConstants.CURRENT:
                fileList = iBookcaseVersionData
                    .findOfflineDVDDownloadsByVersionStatus(program, AppConstants.ONLINE);
                break;
            case AppConstants.REVIEW_COPY:
                fileList = iBookcaseVersionData
                    .findOfflineDVDDownloadsByVersionStatus(program, AppConstants.OFFLINE);
                break;
            case AppConstants.PREVIOUS:
                fileList = iBookcaseVersionData
                    .findOfflineDVDDownloadsByVersionStatus(program, AppConstants.SUSPENDED);
                break;
            default:
                fileList = iBookcaseVersionData.findAllOfflineDVDDownloads(program);
                break;
        }

        int resultSize = fileList.size();

        //check whether the files in the list is available in the NAS path, if not those filenames should not be returned
        List<String> fileNameList = new ArrayList<>();
        List<String> finalList = new ArrayList<>();
        HashMap<String, String> sn = new HashMap<>();
        List<DVDInfoResponse> finalDVDList = new ArrayList<>();
        String dvdPath = SLASH + program + DVD_FOLDER;

        if (resultSize > 0) {
            AmazonS3 amazonS3Client = amazonS3ClientFactory.getS3Client();
            String bucket = s3Config.getS3Bucket().getBucketName();
            String s3Folder = program + "/dvd";

            ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                .withBucketName(bucket)
                .withPrefix(s3Folder);

            ObjectListing objects = null;
            List<S3ObjectSummary> objectSummaries = null;
            log.info("Getting S3 Files");
            try {
                objects = amazonS3Client.listObjects(listObjectsRequest);
                objectSummaries = objects.getObjectSummaries();

                while (objects.isTruncated()) {
                    log.info("Getting more S3 Files.");
                    objects = amazonS3Client.listNextBatchOfObjects(objects);
                    objectSummaries.addAll(objects.getObjectSummaries());
                }
            } catch (Exception ex) {
                log.error("Error getting S3 objects. " + ex.getMessage());
            }

            if (objectSummaries.size() == 0) {
                log.error("ERR_FOLDER_DOES_NOT_EXIST - " + s3Folder);
            }

            for (OfflineDVDInfoDto offlineDVDInfoDto : fileList) {
                String offlineZipFilename =
                    offlineDVDInfoDto.getOfflineFilename() + "_" + offlineDVDInfoDto
                        .getVersion() + ".zip";

                Optional<S3ObjectSummary> objectSummary = objectSummaries.stream()
                    .filter(f -> f.getKey().toLowerCase()
                        .endsWith(offlineZipFilename.toLowerCase()))
                    .findFirst();

                if (objectSummary.isPresent()) {
                    finalList.add(offlineZipFilename);
                    String fileSize = Long.toString(objectSummary.get().getSize());
                    sn.put(offlineZipFilename, fileSize);
                }
            }
        }

        DVDInfoResponse dvdInfoResponse;

        for (OfflineDVDInfoDto offlineDVDInfoDto : fileList) {
            String offlineZipFilename =
                offlineDVDInfoDto.getOfflineFilename() + "_" + offlineDVDInfoDto.getVersion()
                    + ".zip";
            if (finalList.contains(offlineZipFilename)) {
                dvdInfoResponse = new DVDInfoResponse(
                    offlineZipFilename,
                    offlineDVDInfoDto.getReleaseDate().toString().substring(0, 10),
                    program,
                    sn.get(offlineZipFilename),
                    TYPE
                );
                finalDVDList.add(dvdInfoResponse);
            }

        }

        int finalResulSize = finalDVDList.size();

        if (finalResulSize > 1) {
            techpubsAppUtil.sortDvdItems(finalDVDList, queryParams);
        }

        int iDisplayLength = Integer.parseInt(queryParams.get("iDisplayLength"));
        int iDisplayStart = Integer.parseInt(queryParams.get("iDisplayStart"));
        String sEcho = queryParams.get("sEcho");
        DocumentDataTableModel dvdDataTable = new DocumentDataTableModel();
        dvdDataTable.setIDisplayLength(iDisplayLength);
        dvdDataTable.setIDisplayStart(iDisplayStart);
        dvdDataTable.setITotalDisplayRecords(finalResulSize);
        dvdDataTable.setITotalRecords(finalResulSize);
        dvdDataTable.setSEcho(sEcho);
        dvdDataTable.setDvdList(
            finalDVDList.subList((iDisplayStart > finalResulSize ? finalResulSize : iDisplayStart),
                (iDisplayStart + iDisplayLength > finalResulSize ? finalResulSize
                    : iDisplayStart + iDisplayLength)));
        dvdDataTable.setSuccess(true);

        return dvdDataTable;
    }

    private Set<String> setGeaeRevision() {
        Set<String> geaeRevision = new HashSet<String>();
        geaeRevision.add(AppConstants.CURRENT);
        geaeRevision.add(AppConstants.PREVIOUS);
        geaeRevision.add(AppConstants.REVIEW_COPY);
        geaeRevision.add("All");
        return geaeRevision;
    }

    @Deprecated
    private HashMap<String, String> getFinalFilesDetails(File[] filesList,
        List<String> fileNameList, String path) {
        String fileSize;
        HashMap<String, String> sn = new HashMap<String, String>();
        for (File filename : filesList) {

            for (int i = 0; i < fileNameList.size(); i++) {
                if (filename.getName().equals(fileNameList.get(i))) {
                    File file = new File(path + filename.getName());
                    //fileSize = FileUtils.byteCountToDisplaySize(file.length());

                    //US215915 - To show file size in decimal
                    fileSize = getFileSize((float) file.length());

                    sn.put(filename.getName(), fileSize);
                }

            }

        }
        return sn;
    }

    public String getFileSize(Float length) {

        if (length > FileUtils.ONE_GB) {
            return format.format(length / FileUtils.ONE_GB) + " GB";
        } else if (length > FileUtils.ONE_MB) {
            return format.format(length / FileUtils.ONE_MB) + " MB";
        } else if (length > FileUtils.ONE_KB) {
            return format.format(length / FileUtils.ONE_KB) + " KB";
        }
        return format.format(length) + " B";

    }

    private String getFileSize(String s3Key){
        AmazonS3 amazonS3Client = null;
        String bucket = s3Config.getS3Bucket().getBucketName();
        ObjectMetadata metadata = null;
        try {
            amazonS3Client = amazonS3ClientFactory.getS3Client();
            GetObjectMetadataRequest objectMetadataRequest = new GetObjectMetadataRequest(bucket, s3Key);

            metadata  = amazonS3Client.getObjectMetadata(objectMetadataRequest);
        } catch (Exception ex){
            log.error("Error finding file size. S3 Key:" + s3Key + " " + ex.getMessage() );
            return "";
        }

        return String.valueOf(metadata.getContentLength());
    }

}


