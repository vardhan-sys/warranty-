package com.geaviation.techpubs.services.impl;

import com.geaviation.techpubs.data.api.IManualData;
import com.geaviation.techpubs.data.api.IProgramData;
import com.geaviation.techpubs.data.api.IResourceData;
import com.geaviation.techpubs.data.api.techlib.IBookcaseVersionData;
import com.geaviation.techpubs.data.api.techlib.IPageBlkData;
import com.geaviation.techpubs.data.api.techlib.IPageblkLookupData;
import com.geaviation.techpubs.data.util.log.LogExecutionTime;
import com.geaviation.techpubs.exceptions.PdfPrintException;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.exceptions.TechpubsException.TechpubsAppError;
import com.geaviation.techpubs.models.DocumentItemModel;
import com.geaviation.techpubs.models.DocumentItemTDModel;
import com.geaviation.techpubs.models.DocumentModel;
import com.geaviation.techpubs.models.ManualItemModel;
import com.geaviation.techpubs.models.ManualModel;
import com.geaviation.techpubs.models.ProgramItemModel;
import com.geaviation.techpubs.models.ResourceMetaDataModel;
import com.geaviation.techpubs.models.SbData;
import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.models.techlib.BookcaseVersionEntity;
import com.geaviation.techpubs.models.techlib.dto.PageblkLookupDto;
import com.geaviation.techpubs.services.api.IManualApp;
import com.geaviation.techpubs.services.api.IProgramApp;
import com.geaviation.techpubs.services.api.logic.IPageblkEnablement;
import com.geaviation.techpubs.services.util.AppConstants;
import com.geaviation.techpubs.services.util.PDFConverter;
import com.geaviation.techpubs.services.util.PDFPrintApp;
import com.geaviation.techpubs.services.util.TechpubsAppUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RefreshScope
public class ManualAppSvcImpl implements IManualApp {

    public static final String EXCEPTION_THROWN_IN_GET_HTMLRESOURCE_TD_FOR_SSO_ID = "Exception thrown in getHTMLResourceTD() for ssoId ";
    public static final String COMMA_PROGRAM = ", program ";
    public static final String GET_HTMLRESOURCE_SUMMARY_TD = "getHTMLResourceSummaryTD";
    public static final String PORTAL_ID = ", portalId ";
    public static final String COMMA_MANUAL = ", manual ";
    public static final String GET_BINARY_RESOURCE_TD = "getBinaryResourceTD (";
    public static final String SBNBR = "sbnbr ";

    @Value("${techpubs.services.US538636}")
    private boolean US538636;

    @Autowired
    private TechpubsAppUtil techpubsAppUtil;

    @Autowired
    private IProgramApp iProgramApp;

    @Autowired
    private PDFPrintApp pdfConverter;

    @Autowired
    private PDFConverter newPdfConverter;

    @Autowired
    private IProgramData iProgramData;

    @Autowired
    private IPageblkEnablement iPageblkEnablement;

    @Autowired
    private IManualData iManualData;

    @Autowired
    private IResourceData iResourceData;

    @Autowired
    private IPageBlkData iPageBlkData;

    @Autowired
    private IPageblkLookupData iPageblkLookupData;

    @Value("${PDF.HTMLDIRECTURL}")
    private String directHtmlURL;

    @Value("${PDF.HTMLURL}")
    private String htmlURL;

    @Autowired
    private IBookcaseVersionData iBookcaseVersionData;

    private static final Logger log = LogManager.getLogger(ManualAppSvcImpl.class);

    @Override
    @LogExecutionTime
    public ManualModel getManuals(String ssoId, String portalId, Map<String, String> queryParams)
        throws TechpubsException {

        if (TechpubsAppUtil.isNullOrEmpty(ssoId) || TechpubsAppUtil.isNullOrEmpty(portalId)) {
            log.error(AppConstants.ERROR_MESSAGE);
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }
        Map<String, String> searchFilter = TechpubsAppUtil.getFilterFields(queryParams);
        String family = searchFilter.get(AppConstants.FAMILY);
        String model = searchFilter.get(AppConstants.MODEL);
        String aircraft = searchFilter.get(AppConstants.AIRCRAFT);
        String tail = searchFilter.get(AppConstants.TAIL);
        List<String> esnList = new ArrayList<>();
        if (TechpubsAppUtil.isNotNullandEmpty(searchFilter.get(AppConstants.ESN))) {
            esnList = Arrays.asList(searchFilter.get(AppConstants.ESN).split("\\|"));
        }
        // validate queryparms
        TechpubsAppUtil.validateDatatableParameters(queryParams);

        List<ManualItemModel> manualItemList = new ArrayList<>();

        List<String> authorizedProgramsList = iProgramApp
            .getAuthorizedPrograms(ssoId, portalId, SubSystem.TD);

        if (!authorizedProgramsList.isEmpty()) {
            List<ProgramItemModel> programList = new ArrayList<>();
            try {
                programList = getProgramListForRequest(ssoId, portalId, family,
                    model, aircraft, tail, esnList, authorizedProgramsList);
            } catch (Exception e) {
                log.info(e.getMessage());
            }

            if (TechpubsAppUtil.isCollectionNotEmpty(programList)) {
                for (ProgramItemModel program : programList) {
                    manualItemList.addAll(iManualData.getManualsByProgram(program));
                }

                if (!manualItemList.isEmpty()) {
                    // Add SPM Manuals if other ge manuals are found (and authorized)
                    ProgramItemModel spmProgram = iProgramData.getSpmProgramItem();
                    if (spmProgram != null && authorizedProgramsList.contains(
                            spmProgram.getProgramDocnbr()) && !programList.contains(spmProgram)) {
                        manualItemList.addAll(iManualData.getManualsByProgram(iProgramData.getSpmProgramItem()));
                    }
                }

                if (!manualItemList.isEmpty() && portalId.equalsIgnoreCase(AppConstants.GEHONDA)) {
                    // Add Honda-SPM Manuals if other honda manuals are found (and authorized)
                    ProgramItemModel hondaSpmProgram = iProgramData.getHondaSpmProgramItem();
                    if (hondaSpmProgram != null && authorizedProgramsList.contains(
                            hondaSpmProgram.getProgramDocnbr()) && !programList.contains(hondaSpmProgram)) {
                        manualItemList.addAll(iManualData.getManualsByProgram(iProgramData.getHondaSpmProgramItem()));
                    }
                }

                techpubsAppUtil.sortManualItems(manualItemList, queryParams);
            }
        }

        int resultSize = manualItemList.size();

        int iDisplayLength = Integer.parseInt(queryParams.get(AppConstants.IDISPLAYLENGTH));
        int iDisplayStart = Integer.parseInt(queryParams.get(AppConstants.IDISPLAYSTART));
        String sEcho = queryParams.get(AppConstants.SECHO);
        ManualModel manualModel = new ManualModel();
        manualModel.setIDisplayLength(iDisplayLength);
        manualModel.setIDisplayStart(iDisplayStart);
        manualModel.setITotalDisplayRecords(manualItemList.size());
        manualModel.setITotalRecords(manualItemList.size());
        manualModel.setSEcho(sEcho);

        manualModel.setManualItemList(manualItemList.subList((Math.min(iDisplayStart, resultSize)),
                (Math.min(iDisplayStart + iDisplayLength, resultSize))));
        manualModel.setSuccess(true);

        return manualModel;
    }

    private List<ProgramItemModel> getProgramListForRequest(String ssoId, String portalId,
        String family, String model,
        String aircraft, String tail, List<String> esnList, List<String> authorizedProgramsList)
        throws TechpubsException, IOException, DocumentException {
        Set<ProgramItemModel> programSet = new HashSet<>();

        if (TechpubsAppUtil.isNotNullandEmpty(model)) {
            programSet.addAll(
                iProgramData.getProgramItemsByModel(model, SubSystem.TD, authorizedProgramsList));
        } else if (TechpubsAppUtil.isNotNullandEmpty(family)) {
            programSet.addAll(
                iProgramData.getProgramItemsByFamily(family, SubSystem.TD, authorizedProgramsList));
        } else if (TechpubsAppUtil.isCollectionNotEmpty(esnList)) {
            for (String esn : esnList) {
                for (String derivedModel : techpubsAppUtil
                    .getModelList(ssoId, portalId, family, model, aircraft, tail,
                        esn)) {
                    programSet.addAll(
                        iProgramData.getProgramItemsByModel(derivedModel, SubSystem.TD,
                            authorizedProgramsList));
                }
            }
        } else if (TechpubsAppUtil.isNotNullandEmpty(aircraft) || TechpubsAppUtil
            .isNotNullandEmpty(tail)) {
            for (String derivedModel : techpubsAppUtil
                .getModelList(ssoId, portalId, family, model, aircraft, tail,
                    null)) {
                programSet.addAll(
                    iProgramData.getProgramItemsByModel(derivedModel, SubSystem.TD,
                        authorizedProgramsList));
            }
        } else {
            // 'ALL/ALL' Selected - Return all Techpubs 'mapped' programs that
            // have been authorized....
            programSet.addAll(
                iProgramData.getProgramItemsByFamily(null, SubSystem.TD, authorizedProgramsList));
        }

        return new ArrayList<>(programSet);
    }

    @Override
    @LogExecutionTime
    public byte[] getHTMLResourceTDByTargetIndex(String ssoId, String portalId, String program,
        String version, String bookKey, String target, String bandwidth,
        boolean multiBrowserDocumentRequired) {
        ProgramItemModel programItem = iProgramData
            .getProgramItemVersion(new BookcaseVersionEntity(program, version), SubSystem.TD);

        byte[] htmlResource = null;

        List<PageblkLookupDto> entities = iResourceData
            .pageblkTargetLookup(programItem, bookKey, target);

        String filename;

        switch (entities.size()) {
            case 0:
                filename = target+".htm";
                try {
                    htmlResource = getHTMLResourceTD(ssoId, portalId, program, version, bookKey,
                        filename, bandwidth, multiBrowserDocumentRequired);
                } catch (TechpubsException e) {
                    log.debug("No filename found matching target index " + target);
                    htmlResource = iResourceData.getDocumentNotFoundFile();
                }

                break;
            case 1:
                filename = entities.get(0).getOnlineFilename();
                try {
                    htmlResource = getHTMLResourceTD(ssoId, portalId, program, version, bookKey,
                        filename, bandwidth, multiBrowserDocumentRequired);
                } catch (TechpubsException e) {
                    log.info(e.getMessage());
                }
                break;
            default:
                htmlResource = iResourceData.displaySelectDocument(programItem, entities, bandwidth,
                    multiBrowserDocumentRequired).getBytes();
        }

        return htmlResource;
    }

    @Override
    @LogExecutionTime
    public byte[] getHTMLResourceTD(String ssoId, String portalId, String program, String manual,
        String filename, String bandwidth, boolean multiBrowserDocumentRequired) throws TechpubsException {

        validateUserHasAccessToBookcase(ssoId, portalId, program, manual, filename);
        try {
            ProgramItemModel programItem = iProgramData.getProgramItem(program, SubSystem.TD);
            byte[] htmlResource;
                boolean userIsExplicitlyEnabledToViewFile = getUserIsExplicitlyEnabledToViewFile(ssoId, portalId, program, manual, filename, programItem);
                htmlResource = iResourceData
                    .getHTMLResourceTD(programItem, manual, filename, bandwidth,
                        multiBrowserDocumentRequired, userIsExplicitlyEnabledToViewFile);

            return htmlResource;
        } catch (Exception e){

            log.error(EXCEPTION_THROWN_IN_GET_HTMLRESOURCE_TD_FOR_SSO_ID + ssoId + PORTAL_ID
                + portalId + COMMA_PROGRAM + program + COMMA_MANUAL + manual + ", " + "filename " + filename
                + ", bandwidth " + bandwidth + ", multibrowserDocumentRequired " + multiBrowserDocumentRequired, e );
            throw new TechpubsException(TechpubsAppError.INTERNAL_ERROR, e);

        }
    }

    @Override
    @LogExecutionTime
    public byte[] getHTMLResourceTD(String ssoId, String portalId, String program, String version,
        String manual, String filename, String bandwidth, boolean multiBrowserDocumentRequired)
        throws TechpubsException {
        validateUserHasAccessToBookcase(ssoId, portalId, program, manual, filename);

        if(AppConstants.ONLINE.equals(version)){
            version = iBookcaseVersionData.findOnlineBookcaseVersion(program);
        }

        BookcaseVersionEntity versionedProgram = new BookcaseVersionEntity(program, version);
        try {
            ProgramItemModel programItem = iProgramData
                .getProgramItemVersion(versionedProgram, SubSystem.TD);
            byte[] htmlResource;
            boolean userIsExplicitlyEnabledToViewFile = getUserIsExplicitlyEnabledToViewFile(ssoId,
                portalId, program, manual, filename, programItem);
            htmlResource = iResourceData
                .getHTMLResourceTD(programItem, manual, filename, bandwidth,
                    multiBrowserDocumentRequired, userIsExplicitlyEnabledToViewFile);
            return htmlResource;
        } catch (Exception e) {
            log.error(EXCEPTION_THROWN_IN_GET_HTMLRESOURCE_TD_FOR_SSO_ID + ssoId + PORTAL_ID
                + portalId + COMMA_PROGRAM + program + COMMA_MANUAL + manual + ", " + "filename "
                + filename
                + ", bandwidth " + bandwidth + ", multibrowserDocumentRequired "
                + multiBrowserDocumentRequired, e);
            throw new TechpubsException(TechpubsAppError.INTERNAL_ERROR, e);
        }
    }

    @Override
    @LogExecutionTime
    public ResourceMetaDataModel getResourceName(String ssoId, String portalId, String program,
        String manual, String target,
        String filename, Map<String, String> queryParams) throws TechpubsException {

        if (TechpubsAppUtil.isNullOrEmpty(ssoId) || TechpubsAppUtil.isNullOrEmpty(portalId)) {
            log.error("getResourceName (" + TechpubsException.TechpubsAppError.NOT_AUTHORIZED
                .getErrorCode()
                + ") - " + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg() + "("
                + AppConstants.PROGRAM + "=" + program + AppConstants.EQ_MANUAL + manual
                + ",target=" + target + ","
                + AppConstants.FILENAME + "=" + filename + ") " + "(" + AppConstants.SSO_ID + "="
                + ssoId
                + AppConstants.PORTAL_ID + "=" + portalId + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        // Ensure user has access to program
        if (!iProgramApp.getAuthorizedPrograms(ssoId, portalId, SubSystem.TD).contains(program)) {
            log.error(
                "getResourceName (" + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE
                    .getErrorCode()
                    + ") - " + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE
                    .getErrorMsg() + "("
                    + AppConstants.PROGRAM + "=" + program + AppConstants.EQ_MANUAL + manual
                    + ",target=" + target + ","
                    + AppConstants.FILENAME + "=" + filename + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE);
        }

        ProgramItemModel programItem = null;
        try {
            programItem = iProgramData.getProgramItem(program, SubSystem.TD);
        } catch (Exception e){
            log.info(e.getMessage());
        }

        return iResourceData
            .getResourceNameTD(programItem, manual, target,
                filename, ("high".equalsIgnoreCase(queryParams.get("bw")) ? "high" : "low"),
                ("Y".equalsIgnoreCase(queryParams.get("mbdr"))));
    }

    @Override
    @LogExecutionTime
    public byte[] getProgramBannerTD(String ssoId, String portalId, String program)
        throws TechpubsException {

        if (TechpubsAppUtil.isNullOrEmpty(ssoId) || TechpubsAppUtil.isNullOrEmpty(portalId)) {
            log.error(
                AppConstants.PROGRAM_BANNER_TD + TechpubsException.TechpubsAppError.NOT_AUTHORIZED
                    .getErrorCode()
                    + ") - " + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg() + "("
                    + AppConstants.PROGRAM + "=" + program + ") " + "(" + AppConstants.SSO_ID + "="
                    + ssoId
                    + AppConstants.PORTAL_ID + "=" + portalId + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        // Ensure user has access to program
        if (!iProgramApp.getAuthorizedPrograms(ssoId, portalId, SubSystem.TD).contains(program)) {
            log.error(AppConstants.PROGRAM_BANNER_TD
                + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE.getErrorCode() + ") - "
                + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE.getErrorMsg() + "("
                + AppConstants.PROGRAM + "=" + program + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE);
        }

        ProgramItemModel programItem = null;

        try {
            programItem = iProgramData.getProgramItem(program, SubSystem.TD);
        } catch (Exception e) {
            log.info(e.getMessage());
        }

        return iResourceData
            .getBinaryResourceTD(programItem, "getd",
                "graphics/gni_image.jpg");
    }

    @Override
    @LogExecutionTime
    public String getHTMLResourceSummaryTD(String ssoId, String portalId, String program,
        String manual, String filename) throws TechpubsException {

        if (TechpubsAppUtil.isNullOrEmpty(ssoId) || TechpubsAppUtil.isNullOrEmpty(portalId)) {
            log.error(
                GET_HTMLRESOURCE_SUMMARY_TD + " (" + TechpubsException.TechpubsAppError.NOT_AUTHORIZED
                    .getErrorCode()
                    + ") - " + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg()
                    + " ("
                    + AppConstants.PROGRAM + "=" + program + AppConstants.EQ_MANUAL + manual
                    + AppConstants.FILENAME_EQ
                    + filename + ") " + "(" + AppConstants.SSO_ID + "=" + ssoId + ","
                    + AppConstants.PORTAL_ID + "="
                    + portalId + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        // Ensure user has access to program
        if (!iProgramApp.getAuthorizedPrograms(ssoId, portalId, SubSystem.TD).contains(program)) {
            log.error(GET_HTMLRESOURCE_SUMMARY_TD + " ("
                + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE.getErrorCode() + ") - "
                + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE.getErrorMsg() + " ("
                + AppConstants.PROGRAM + "=" + program + AppConstants.EQ_MANUAL + manual
                + AppConstants.FILENAME_EQ
                + filename + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE);
        }

        ProgramItemModel programItem = null;

        try {
            programItem = iProgramData.getProgramItem(program, SubSystem.TD);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return iResourceData
            .getHTMLResourceSummaryTD(programItem, manual,
                filename);
    }

    @Override
    @LogExecutionTime
    public String getHTMLResourceSummaryTD(String ssoId, String portalId, String program,
        String version, String manual, String filename) throws TechpubsException {
        if (TechpubsAppUtil.isNullOrEmpty(ssoId) || TechpubsAppUtil.isNullOrEmpty(portalId)) {
            log.error(
                GET_HTMLRESOURCE_SUMMARY_TD + " (" + TechpubsException.TechpubsAppError.NOT_AUTHORIZED
                    .getErrorCode()
                    + ") - " + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg()
                    + " ("
                    + AppConstants.PROGRAM + "=" + program + AppConstants.EQ_MANUAL + manual
                    + AppConstants.FILENAME_EQ
                    + filename + ") " + "(" + AppConstants.SSO_ID + "=" + ssoId + ","
                    + AppConstants.PORTAL_ID + "="
                    + portalId + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }
        // Ensure user has access to program
        if (!iProgramApp.getAuthorizedPrograms(ssoId, portalId, SubSystem.TD).contains(program)) {
            log.error(GET_HTMLRESOURCE_SUMMARY_TD + " ("
                + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE.getErrorCode() + ") - "
                + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE.getErrorMsg() + " ("
                + AppConstants.PROGRAM + "=" + program + AppConstants.EQ_MANUAL + manual
                + AppConstants.FILENAME_EQ
                + filename + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE);
        }
        BookcaseVersionEntity versionedProgram = new BookcaseVersionEntity(program, version);
        return iResourceData.getHTMLResourceSummaryTD(
            iProgramData.getProgramItemVersion(versionedProgram, SubSystem.TD), manual, filename);
    }

    @Override
    @LogExecutionTime
    public byte[] getBinaryResourceTD(String ssoId, String portalId, String program, String version,
        String manual, String res) throws TechpubsException {
        if (TechpubsAppUtil.isNullOrEmpty(ssoId) || TechpubsAppUtil.isNullOrEmpty(portalId)) {
            log.error(GET_BINARY_RESOURCE_TD + TechpubsException.TechpubsAppError.NOT_AUTHORIZED
                .getErrorCode()
                + ") - " + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg() + " ("
                + AppConstants.PROGRAM + "=" + program + AppConstants.EQ_MANUAL + manual + ",res="
                + res + "("
                + AppConstants.SSO_ID + "=" + ssoId + "," + AppConstants.PORTAL_ID + "=" + portalId
                + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }
        // Ensure user has access to program
        if (!iProgramApp.getAuthorizedPrograms(ssoId, portalId, SubSystem.TD).contains(program)) {
            log.error(GET_BINARY_RESOURCE_TD
                + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE.getErrorCode() + ") - "
                + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE.getErrorMsg() + ") ");
        }

        byte[] binaryResource = iResourceData.getBinaryResourceTD(iProgramData
                .getProgramItemVersion(new BookcaseVersionEntity(program, version), SubSystem.TD),
            manual, res);

        if (binaryResource == null) {
            throw new TechpubsException(TechpubsException.TechpubsAppError.RESOURCE_NOT_FOUND);
        }

        return binaryResource;
    }

    @Override
    @LogExecutionTime
    public byte[] getSMMBinaryResourceTD(String ssoId, String portalId, String program, String version, String manual,
        String res, String onlineFileName)
        throws TechpubsException {

        if (TechpubsAppUtil.isNullOrEmpty(ssoId) || TechpubsAppUtil.isNullOrEmpty(portalId)) {
            log.error(GET_BINARY_RESOURCE_TD + TechpubsException.TechpubsAppError.NOT_AUTHORIZED
                .getErrorCode()
                + ") - " + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg() + " ("
                + AppConstants.PROGRAM + "=" + program + AppConstants.EQ_MANUAL + manual + ",res="
                + res + "("
                + AppConstants.SSO_ID + "=" + ssoId + "," + AppConstants.PORTAL_ID + "=" + portalId
                + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        boolean isPageblkEnabled = iPageblkEnablement.isPageblkEnabled(ssoId, portalId, program, manual, onlineFileName);

        byte[] binaryResource = iResourceData.getBinaryResourceTD(iProgramData
                .getProgramItemVersion(new BookcaseVersionEntity(program, version), SubSystem.TD),
            manual, isPageblkEnabled ? manual + "_f/" + res : res);

        if (binaryResource == null) {
            throw new TechpubsException(TechpubsException.TechpubsAppError.RESOURCE_NOT_FOUND);
        }

        return binaryResource;
    }

    @Override
    @LogExecutionTime
    public byte[] getPrintHTMLResourceSummaryTD(String baseURI, String ssoId, String portalId,
        String program,
        String manual, String filename) throws TechpubsException {

        if (TechpubsAppUtil.isNullOrEmpty(ssoId) || TechpubsAppUtil.isNullOrEmpty(portalId)) {
            log.error(AppConstants.PRINT_HTML_RESOURCE_SUMMARY_TD
                + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorCode() + ") - "
                + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg() + " ("
                + AppConstants.PROGRAM
                + "=" + program + AppConstants.EQ_MANUAL + manual + AppConstants.FILENAME_EQ
                + filename + ") "
                + AppConstants.EQ_SSO + ssoId + AppConstants.EQ_PORTAL + portalId + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        // Ensure user has access to program
        if (!iProgramApp.getAuthorizedPrograms(ssoId, portalId, SubSystem.TD).contains(program)) {
            log.error(AppConstants.PRINT_HTML_RESOURCE_SUMMARY_TD
                + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE.getErrorCode() + ") - "
                + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE.getErrorMsg() + " ("
                + AppConstants.PROGRAM + "=" + program + AppConstants.EQ_MANUAL + manual
                + AppConstants.FILENAME_EQ
                + filename + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE);
        }

        ProgramItemModel programItem = null;

        try {
            programItem = iProgramData.getProgramItem(program, SubSystem.TD);
        } catch (Exception e) {
            log.info(e.getMessage());
        }

        // Acquire Document Item Information
        DocumentItemTDModel documentItem = (DocumentItemTDModel) iManualData
            .getDocumentItem(programItem, manual, filename);

        byte[] pdfByte = null;
        if (documentItem != null && documentItem.isPrintable()) {
            if (AppConstants.HTML.equalsIgnoreCase(documentItem.getFileType())) {
                // Call Print service
                String inputHTML =
                    baseURI + AppConstants.PDF_SERVICE + program + AppConstants.MANS + manual
                        + "/summary/" + filename;
                String header =
                    ("SB".equalsIgnoreCase(documentItem.getType()) ? documentItem.getProgramtitle()
                        + " "
                        : "") + documentItem.getManualtitle() + "| | |" + documentItem.getTitle()
                        + "| | | | | |";
                String disc;
                if (AppConstants.GEHONDA.equalsIgnoreCase(portalId)) {
                    disc =
                        "GE Honda AERO ENGINES PROPRIETARY INFORMATION - Not to be used, disclosed to others or "
                            + "reproduced without the express written consent of GE. Technical data "
                            + "is considered ITAR and/or EAR controlled; transfer of this data to a "
                            + "Non-US Person, without USG authorization, is strictly prohibited.";
                } else {
                    disc = "GE PROPRIETARY INFORMATION - Not to be used, disclosed to others or "
                        + "reproduced without the express written consent of GE. Technical data "
                        + "is considered ITAR and/or EAR controlled; transfer of this data to a "
                        + "Non-US Person, without USG authorization, is strictly prohibited.";
                }
                String footer = "|" + disc + "| | | | | | | |";
                try {
                    if (US538636) {
                        pdfByte = newPdfConverter
                                .convertHTMLToPDF(htmlURL, directHtmlURL, inputHTML, ssoId, portalId, header, footer)
                                .toByteArray();
                    } else {
                        pdfConverter.init(ssoId, portalId, header, footer);
                        pdfByte = pdfConverter.convertHTMLToPDF(htmlURL, directHtmlURL, inputHTML).toByteArray();
                    }
                } catch (PdfPrintException e) {
                    log.error(
                        AppConstants.PRINT_HTML_RESOURCE_SUMMARY_TD + e.getErrorCode() + ") - " + e
                            + " ("
                            + AppConstants.PROGRAM + "=" + program + AppConstants.EQ_MANUAL + manual
                            + AppConstants.EQ_FILE + filename + ")");
                    throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
                }
            } else {
                pdfByte = iResourceData
                    .getBinaryResourceTD(documentItem.getProgramItem(), manual, filename);
            }
        } else {
            pdfByte = iResourceData.getPrintNotAvailable();
        }

        return pdfByte;
    }

    @Override
    @LogExecutionTime
    public byte[] getPrintHTMLResourceTD(String baseURI, String ssoId, String portalId,
        String program, String version, String manual, String filename) throws TechpubsException {

        if (TechpubsAppUtil.isNullOrEmpty(ssoId) || TechpubsAppUtil.isNullOrEmpty(portalId)) {
            log.error(AppConstants.PRINT_HTML_RESOURCE_TD
                + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorCode() + ") - "
                + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg() + " ("
                + AppConstants.PROGRAM
                + "=" + program + AppConstants.EQ_MANUAL + manual + ",filename=" + filename + ") "
                + AppConstants.EQ_SSO + ssoId + AppConstants.EQ_PORTAL + portalId + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        // Ensure user has access to program
        if (!iProgramApp.getAuthorizedPrograms(ssoId, portalId, SubSystem.TD).contains(program)) {
            log.error(AppConstants.PRINT_HTML_RESOURCE_TD
                + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE.getErrorCode() + ") - "
                + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE.getErrorMsg() + " ("
                + AppConstants.PROGRAM + "=" + program + AppConstants.EQ_MANUAL + manual
                + AppConstants.EQ_FILE
                + filename + " )");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE);
        }

        DocumentItemTDModel documentItem = (DocumentItemTDModel) iManualData
            .getDocumentItem(iProgramData
                    .getProgramItemVersion(new BookcaseVersionEntity(program, version), SubSystem.TD),
                manual, filename);

        byte[] pdfByte = null;
        if (documentItem != null) {
            if (AppConstants.HTML.equalsIgnoreCase(documentItem.getFileType())) {
                if (documentItem.isPrintable()) {
                    // Always print low bandwidth file, if available.
                    // getMfilename defaults to filename when null
                    // Call Print service
                    String inputHTML =
                        baseURI + AppConstants.PDF_SERVICE + program + "/versions/" + version + AppConstants.MANS + manual
                            + "/file/" + documentItem.getMfilename();
                    String header = (AppConstants.SB.equalsIgnoreCase(documentItem.getType())
                        ? documentItem.getProgramtitle() + " " : "") + documentItem.getManualtitle()
                        + "| | |"
                        + documentItem.getTitle() + "| | | | | |";
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                    StringBuilder sbProprietary = new StringBuilder();
                    if (AppConstants.GEHONDA.equalsIgnoreCase(portalId)) {
                        sbProprietary.append("GE Honda AERO ENGINES PROPRIETARY INFORMATION – ");
                        sbProprietary.append(
                            "Not to be used, disclosed to others or reproduced without the ");
                        sbProprietary.append("express written consent of GE Honda. ");
                        sbProprietary.append(
                            "This technical data is EAR controlled pursuant to 15 CFR Parts 730-774. Transfer ");
                        sbProprietary
                            .append("of this data to a Non-US Person, without the proper ");
                        sbProprietary
                            .append("U.S. Government authorization is strictly prohibited.");
                    } else {
                        sbProprietary.append("GE PROPRIETARY INFORMATION - ");
                        sbProprietary.append(
                            "Not to be used, disclosed to others or reproduced without the ");
                        sbProprietary.append("express written consent of GE. ");
                        sbProprietary
                            .append("Technical data is considered ITAR and/or EAR controlled; ");
                        sbProprietary
                            .append("transfer of this data to a Non-US Person, without USG ");
                        sbProprietary.append("authorization, is strictly prohibited.");
                    }

                    String footer =
                        "Date Printed: " + dateFormat.format(new Date()) + "|" + sbProprietary
                            .toString()
                            + "|@Page| | | | | | |";

                    try {
                        if (US538636) {
                            pdfByte = newPdfConverter
                                    .convertHTMLToPDF(htmlURL, directHtmlURL, inputHTML, ssoId, portalId, header, footer)
                                    .toByteArray();
                        } else {
                            pdfConverter.init(ssoId, portalId, header, footer);
                            pdfByte = pdfConverter.convertHTMLToPDF(htmlURL, directHtmlURL, inputHTML).toByteArray();
                        }
                    } catch (PdfPrintException e) {
                        log.error(
                            AppConstants.PRINT_HTML_RESOURCE_TD + e.getErrorCode() + ") - " + e
                                + AppConstants.EQ_PROGRAM + program + AppConstants.EQ_MANUAL
                                + manual
                                + AppConstants.EQ_FILE + filename + ")");
                        pdfByte = iResourceData.getPrintNotAvailable();
                    }
                } else { // Try to 'print' PDF version
                    try {
                        pdfByte = iResourceData.getBinaryResourceTD(documentItem.getProgramItem(), manual,
                                FilenameUtils.removeExtension(filename) + ".pdf");
                    } catch (TechpubsException e) {
                        log.error(String
                            .format("S3 Resource not found : %s ",
                                e.getMessage()));
                    }
                    if (pdfByte == null) {
                        pdfByte = iResourceData.getPrintNotAvailable();
                    }
                }
            } else {
                pdfByte = iResourceData
                    .getBinaryResourceTD(documentItem.getProgramItem(), manual, filename);
            }
        } else {
            pdfByte = iResourceData.getPrintNotAvailable();
        }

        return pdfByte;
    }

    @Override
    @LogExecutionTime
    public List<ManualItemModel> getDownloadManuals(String ssoId, String portalId, String program,
        String downloadtype,
        String type, Map<String, String> queryParams) throws TechpubsException {

        if (TechpubsAppUtil.isNullOrEmpty(ssoId) || TechpubsAppUtil.isNullOrEmpty(portalId)) {
            log.error("getDownloadManuals (" + TechpubsException.TechpubsAppError.NOT_AUTHORIZED
                .getErrorCode()
                + ") - " + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg()
                + AppConstants.EQ_PROGRAM
                + program + AppConstants.EQ_DOWNLOADTYPE + downloadtype + AppConstants.EQ_TYPE
                + type + ") "
                + AppConstants.EQ_SSO + ssoId + AppConstants.EQ_PORTAL + portalId + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        // Validate request (DVD:MANUAL is invalid)
        if (("dvd".equalsIgnoreCase(downloadtype) && "manual".equalsIgnoreCase(type))) {
            log.error(
                "getDownloadDocuments (Invalid Request (DVD:MANUAL is invalid)) ("
                    + TechpubsException.TechpubsAppError.INTERNAL_ERROR.getErrorCode() + ") - "
                    + TechpubsException.TechpubsAppError.INTERNAL_ERROR.getErrorMsg()
                    + AppConstants.EQ_PROGRAM
                    + program + AppConstants.EQ_DOWNLOADTYPE + downloadtype + AppConstants.EQ_TYPE
                    + type + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
        }

        // Ensure user has access to program
        if (!iProgramApp.getAuthorizedPrograms(ssoId, portalId, SubSystem.TD).contains(program)) {
            log.error("getDownloadManuals ("
                + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE.getErrorCode() + ") - "
                + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE.getErrorMsg()
                + AppConstants.EQ_PROGRAM
                + program + AppConstants.EQ_DOWNLOADTYPE + downloadtype + AppConstants.EQ_TYPE
                + type + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE);
        }

        ProgramItemModel programItem = null;

        try {
            programItem = iProgramData.getProgramItem(program, SubSystem.TD);
        } catch (Exception e) {
            log.info(e.getMessage());
        }

        if (programItem == null) {
            log.error("getDownloadManuals (Invalid Program) ("
                + TechpubsException.TechpubsAppError.INTERNAL_ERROR.getErrorCode() + ") - "
                + TechpubsException.TechpubsAppError.INTERNAL_ERROR.getErrorMsg()
                + AppConstants.EQ_PROGRAM
                + program + AppConstants.EQ_DOWNLOADTYPE + downloadtype + AppConstants.EQ_TYPE
                + type + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
        }

        List<ManualItemModel> manualItemList = new ArrayList<>();

        Map<String, String> downloadTypeMap = programItem.getDownloadTypeMap()
            .get((downloadtype + "|" + type).toLowerCase());

        if (downloadTypeMap != null) {
            for (String manualDocnbr : downloadTypeMap.keySet()) {
                ManualItemModel manualItemModel = new ManualItemModel();
                manualItemModel.setManualdocnbr(manualDocnbr);
                manualItemModel.setTitle(
                        downloadTypeMap.get(manualDocnbr) + ("all".equalsIgnoreCase(manualDocnbr) ? ""
                                : " (" + manualDocnbr.substring(manualDocnbr.indexOf(':') + 1) + ")"));
                manualItemModel.setProgramItem(programItem);
                manualItemModel.setMultibrowser("N");
                manualItemList.add(manualItemModel);
            }
        }

        return manualItemList;
    }

    //Remove this while clearing featureFlagUS448283
    @Deprecated
    @Override
    @LogExecutionTime
    public DocumentModel getDocumentsByParentTocIdTD(String ssoId, String portalId, String program,
        String manual,
        String parentnodeid, Map<String, String> queryParams) throws TechpubsException {

        if (TechpubsAppUtil.isNullOrEmpty(ssoId) || TechpubsAppUtil.isNullOrEmpty(portalId)) {
            log.error("getDocumentsByParentTocIdTD ("
                + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorCode() + ") - "
                + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg()
                + AppConstants.EQ_PROGRAM
                + program + AppConstants.EQ_MANUAL + manual + ",parentnodeid=" + parentnodeid + ") "
                + AppConstants.EQ_SSO + ssoId + AppConstants.EQ_PORTAL + portalId + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        // Ensure user has access to program
        if (!iProgramApp.getAuthorizedPrograms(ssoId, portalId, SubSystem.TD).contains(program)) {
            log.error("getDocumentsByParentTocIdTD ("
                + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE.getErrorCode() + ") - "
                + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE.getErrorMsg()
                + AppConstants.EQ_PROGRAM
                + program + AppConstants.EQ_MANUAL + manual + ",parentnodeid=" + parentnodeid
                + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE);
        }

        DocumentModel documentModel = new DocumentModel();
        List<DocumentItemModel> documentItemList = new ArrayList<>();

        ProgramItemModel programItem = null;

        try {
            programItem = iProgramData.getProgramItem(program, SubSystem.TD);
        } catch (Exception e) {
            log.info(e.getMessage());
        }

        documentItemList.addAll(iManualData
            .getDocumentsByParentTocId(programItem, manual,
                parentnodeid));
        documentModel.setDocumentItemList(documentItemList);
        documentModel.setSuccess(true);

        return documentModel;
    }

    private boolean getUserIsExplicitlyEnabledToViewFile(String ssoId, String portalId, String bookcaseKey, String bookKey,
        String filename, ProgramItemModel programItem) throws TechpubsException {
        String icao = techpubsAppUtil.getCurrentIcaoCode(ssoId, portalId);
        return iPageBlkData
            .pageblkIsEnabledForIcao(icao, filename, bookcaseKey, bookKey,
                programItem.getProgramOnlineVersion());
    }

    private void validateUserHasAccessToBookcase(String ssoId, String portalId, String bookcaseKey, String bookKey,
        String filename)
        throws TechpubsException {
        if (TechpubsAppUtil.isNullOrEmpty(ssoId) || TechpubsAppUtil.isNullOrEmpty(portalId)) {
            log.error("validateUserHasAccessToBookcase (" + TechpubsException.TechpubsAppError.NOT_AUTHORIZED
                .getErrorCode()
                + ") - " + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg() + "("
                + AppConstants.PROGRAM + "=" + bookcaseKey + AppConstants.EQ_MANUAL + bookKey
                + ", "
                + AppConstants.FILENAME + "=" + filename + ") " + "(" + AppConstants.SSO_ID + "="
                + ssoId
                + AppConstants.PORTAL_ID + "=" + portalId + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        // Ensure user has access to program
        if (!iProgramApp.getAuthorizedPrograms(ssoId, portalId, SubSystem.TD).contains(bookcaseKey)) {
            log.error(
                "validateUserHasAccessToBookcase (" + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE
                    .getErrorCode()
                    + ") - " + TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE
                    .getErrorMsg() + "("
                    + AppConstants.PROGRAM + "=" + bookcaseKey + AppConstants.EQ_MANUAL + bookKey
                    + ", " + AppConstants.FILENAME + "=" + filename + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE);
        }
    }

    @Override
    @LogExecutionTime
    public byte[] getSbResource(String ssoId, String portalId,
        String manual, String program, String sbnbr, String bandwidth,
        boolean multiBrowserDocumentRequired) throws TechpubsException {

        List<SbData> sbnbrFileList;
        String sbnbrFile = null;
        byte[] htmlResource = null;
        try {
            ProgramItemModel programItem = iProgramData.getProgramItem(program, SubSystem.TD);
            String findProgram = program + "/";
            if (sbnbr.contains("R")) {
                sbnbr = sbnbr.substring(0, sbnbr.indexOf('R'));
            }
            if (sbnbr.contains("A")) {
                sbnbr = sbnbr.substring(0, sbnbr.indexOf('A')) + sbnbr
                    .substring(sbnbr.indexOf('A') + 1);
            }
            sbnbrFileList = iPageBlkData
                .findOnlineSbFilesFromPageBlk(findProgram, programItem.getProgramOnlineVersion(),
                    sbnbr);
            if (sbnbrFileList != null && !sbnbrFileList.isEmpty()) {
              Collections.sort(sbnbrFileList);
              sbnbrFile = sbnbrFileList.get(0).getOnlineFilename();
              htmlResource = validateUserAndReturnFile(ssoId, portalId, program, manual,
                  sbnbrFile, programItem, bandwidth, multiBrowserDocumentRequired);
            } else {
              log.info("No results found for query findSbOnlineFileFromPageBlk for ssoId " + ssoId + PORTAL_ID
                  + portalId + COMMA_PROGRAM + program + COMMA_MANUAL + manual + ", " + SBNBR + sbnbr + ", Version: " + programItem.getProgramOnlineVersion());
            }
        } catch (Exception e){
            log.error(EXCEPTION_THROWN_IN_GET_HTMLRESOURCE_TD_FOR_SSO_ID + ssoId + PORTAL_ID
                + portalId + COMMA_PROGRAM + program + COMMA_MANUAL + manual + ", " + SBNBR + sbnbr, e );
            throw new TechpubsException(TechpubsAppError.INTERNAL_ERROR, e);
        }

        return htmlResource;
    }

    @Override
    @LogExecutionTime
    public byte[] getSbResource(String ssoId, String portalId, String manual, String program,
        String version, String sbnbr, String bandwidth, boolean multiBrowserDocumentRequired)
        throws TechpubsException {

        List<SbData> sbnbrFileList;
        String sbnbrFile = null;
        byte[] htmlResource = null;
        BookcaseVersionEntity versionedProgram = new BookcaseVersionEntity(program, version);
        try {
            ProgramItemModel programItem = iProgramData
                .getProgramItemVersion(versionedProgram, SubSystem.TD);
            String findProgram = program + "/";
            if (sbnbr.contains("R")) {
                sbnbr = sbnbr.substring(0, sbnbr.indexOf('R'));
            }

            if (sbnbr.contains("A")) {
                sbnbr = sbnbr.substring(0, sbnbr.indexOf('A')) + sbnbr
                    .substring(sbnbr.indexOf('A') + 1);
            }
            sbnbrFileList = iPageBlkData
                .findOnlineSbFilesFromPageBlk(findProgram, programItem.getProgramOnlineVersion(),
                    sbnbr);
            if (sbnbrFileList != null && !sbnbrFileList.isEmpty()) {
                Collections.sort(sbnbrFileList);
                sbnbrFile = sbnbrFileList.get(0).getOnlineFilename();
                htmlResource = validateUserAndReturnFile(ssoId, portalId, program, manual,
                    sbnbrFile, programItem, bandwidth, multiBrowserDocumentRequired);
            } else {
                log.info("No results found for query findSbOnlineFileFromPageBlk for ssoId " + ssoId + PORTAL_ID
                    + portalId + COMMA_PROGRAM + program + COMMA_MANUAL + manual + ", " + SBNBR + sbnbr + ", Version: " + programItem.getProgramOnlineVersion());
            }
        } catch (Exception e){
            log.error(EXCEPTION_THROWN_IN_GET_HTMLRESOURCE_TD_FOR_SSO_ID + ssoId + PORTAL_ID
                + portalId + COMMA_PROGRAM + program + COMMA_MANUAL + manual + ", " + SBNBR + sbnbr, e );
            throw new TechpubsException(TechpubsAppError.INTERNAL_ERROR, e);
        }

        return htmlResource;
    }

    public byte[] validateUserAndReturnFile(String ssoId, String portalId,
        String program, String manual, String sbnbrFile,
        ProgramItemModel programItem, String bandwidth, boolean multiBrowserDocumentRequired)
        throws TechpubsException {

        validateUserHasAccessToBookcase(ssoId, portalId, program, manual, sbnbrFile);

        boolean userIsExplicitlyEnabledToViewFile = getUserIsExplicitlyEnabledToViewFile(ssoId,
            portalId, program, manual, sbnbrFile, programItem);
      byte[] htmlResource = iResourceData
            .getHTMLResourceTD(programItem, manual, sbnbrFile, bandwidth,
                multiBrowserDocumentRequired, userIsExplicitlyEnabledToViewFile);
        return htmlResource;
    }

}
