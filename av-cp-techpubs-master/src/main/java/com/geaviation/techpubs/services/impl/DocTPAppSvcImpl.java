package com.geaviation.techpubs.services.impl;

import com.geaviation.techpubs.data.api.IDocSubSystemData;
import com.geaviation.techpubs.data.api.IDocTPData;
import com.geaviation.techpubs.data.util.log.LogExecutionTime;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.AssociatedDocumentModel;
import com.geaviation.techpubs.models.DocumentDataTableModel;
import com.geaviation.techpubs.models.DocumentInfoModel;
import com.geaviation.techpubs.models.DocumentItemAssociatedTPModel;
import com.geaviation.techpubs.models.DocumentItemModel;
import com.geaviation.techpubs.models.DocumentModel;
import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.services.api.IDocTPApp;
import com.geaviation.techpubs.services.util.AppConstants;
import com.geaviation.techpubs.services.util.StringUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
@Component
public class DocTPAppSvcImpl extends AbstractDocSubSystemAppImpl implements IDocTPApp {

    private static final String FILE_NAME = "_Technical_Presentations_";

    private static final Logger log = LogManager.getLogger(DocTPAppSvcImpl.class);

    @Override
    public SubSystem getSubSystem() {
        return SubSystem.TP;
    }

    @Override
    protected boolean getLessorModelList() {
        return true;
    }

    @Override
    String setFileName() {
        return FILE_NAME;
    }

    @Override
    protected String setSubSystemResource(DocumentInfoModel documentInfo,
        DocumentItemModel documentItem,
        Map<String, String> queryParams) {

        setTPTitle((DocumentItemAssociatedTPModel) documentItem);
        documentInfo.setTitle(((DocumentItemAssociatedTPModel) documentItem).getTitle());
        String resourceURI = ((DocumentItemAssociatedTPModel) documentItem).getResourceUri();
        String contentType = ((DocumentItemAssociatedTPModel) documentItem).getContentType();
        String documentsURI = ((DocumentItemAssociatedTPModel) documentItem).getDocumentsUri();
        documentInfo.setResourceUri(
            resourceURI + AppConstants.TYPE_PARAM + getSubSystem().toString().toLowerCase());
        documentInfo.setDocumentsUri(documentsURI);
        return iResourceData
            .prepareWrappedResource(AppConstants.SERVICES + resourceURI + AppConstants.BIN
                    + AppConstants.TYPE_PARAM + getSubSystem().toString().toLowerCase(), contentType,
                documentInfo);

    }

    private void setTPTitle(DocumentItemAssociatedTPModel documentItemAssociatedTP) {
        StringBuilder sb;

        switch (documentItemAssociatedTP.getFileCategoryTypeId()) {
            case 2: // Conference Presentations
                sb = new StringBuilder();
                setTPTitleConfSwitch(documentItemAssociatedTP, sb);
                break;
            case 3: // Updates
                sb = new StringBuilder();
                setTPTitleUpdateSwitch(documentItemAssociatedTP, sb);
                break;
            case 4: // Reference Materials
                sb = new StringBuilder();
                setTPTitleRefSwitch(documentItemAssociatedTP, sb);
                break;
            default:
                documentItemAssociatedTP.setTitle(documentItemAssociatedTP.getFileTitle());
                break;
        }
    }

    private void setTPTitleConfSwitch(DocumentItemAssociatedTPModel documentItemAssociatedTP,
        StringBuilder sb) {
        String groupName;
        sb.append(documentItemAssociatedTP.getConferenceLocation()).append(" - ");
        groupName = documentItemAssociatedTP.getGroupName();
        if (groupName != null && !groupName.isEmpty()) {
            sb.append(groupName).append(" - ");
        }
        sb.append(documentItemAssociatedTP.getUploadMonthName()).append(" - ");
        sb.append(documentItemAssociatedTP.getUploadYearNumber()).append(" - ");
        sb.append(documentItemAssociatedTP.getFileTitle());
        documentItemAssociatedTP.setTitle(sb.toString());
    }

    private void setTPTitleUpdateSwitch(DocumentItemAssociatedTPModel documentItemAssociatedTP,
        StringBuilder sb) {
        String groupName;
        groupName = documentItemAssociatedTP.getGroupName();
        if (groupName != null && !groupName.isEmpty()) {
            sb.append(groupName).append(" - ");
        }
        sb.append(documentItemAssociatedTP.getUploadMonthName()).append(" - ");
        sb.append(documentItemAssociatedTP.getUploadYearNumber()).append(" - ");
        sb.append(documentItemAssociatedTP.getFileTitle());
        documentItemAssociatedTP.setTitle(sb.toString());
    }

    private void setTPTitleRefSwitch(DocumentItemAssociatedTPModel documentItemAssociatedTP,
        StringBuilder sb) {
        String groupName;
        groupName = documentItemAssociatedTP.getGroupName();
        if (groupName != null && !groupName.isEmpty()) {
            sb.append(groupName).append(" - ");
        }
        sb.append(documentItemAssociatedTP.getFileTitle());
        documentItemAssociatedTP.setTitle(sb.toString());
    }

    @Override
    @LogExecutionTime
    public File getDownloadTPCSV(String ssoId, String portalId, String model, String category,
        Map<String, String> queryParams) throws TechpubsException {

        List<DocumentItemModel> docItemList = getAssociatedDocumentsTP(ssoId, portalId, model,
            category, queryParams)
            .getDocumentItemList();
        techpubsAppUtil.sortDocumentItems(docItemList, queryParams);

        List<Map<String, String>> dataList = new ArrayList<>();
        HashMap<String, String> csvFieldMap = new LinkedHashMap<>();
        csvFieldMap.put(AppConstants.MODEL, AppConstants.DOWNLOAD_CSV_MODEL);
        csvFieldMap.put(AppConstants.TITLE, AppConstants.DOWNLOAD_CSV_TITLE);
        csvFieldMap.put(AppConstants.RELEASE_DATE, AppConstants.DOWNLOAD_CSV_RELEASE_DATE);

        // Build the CSV data.
        for (DocumentItemModel tpDoc : docItemList) {
            DocumentItemAssociatedTPModel dam = (DocumentItemAssociatedTPModel) tpDoc;
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put(AppConstants.MODEL, model);
            dataMap.put(AppConstants.TITLE, dam.getTitle());
            dataMap.put(AppConstants.RELEASE_DATE, dam.getReleaseDate());
            dataList.add(dataMap);
        }

        File csvFile = null;
        try {
            csvFile = iCSVExportApp
                .csvExport(ssoId, AppConstants.DOCUMENT_DOWNLOAD_EXPORT, dataList,
                    new ArrayList<String>(), csvFieldMap);
        } catch (Exception e) {
            log.error(
                "getDownloadTPCSV (CSV Error) (" + TechpubsException.TechpubsAppError.INTERNAL_ERROR
                    .getErrorCode()
                    + ") - " + TechpubsException.TechpubsAppError.INTERNAL_ERROR.getErrorMsg()
                    + " ("
                    + AppConstants.MODEL + "=" + model + "," + AppConstants.CATEGORY + "="
                    + category + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR, e);
        }
        return csvFile;
    }

    @Override
    @LogExecutionTime
    public DocumentModel getAssociatedDocumentsTP(String ssoId, String portalId, String model,
        String category,
        Map<String, String> queryParams) throws TechpubsException {

        if (!StringUtils.isInteger(category)) {
            log.error(
                "getAssociatedDocumentsTP (" + TechpubsException.TechpubsAppError.INVALID_PARAMETER
                    .getErrorCode()
                    + ") - " + TechpubsException.TechpubsAppError.INVALID_PARAMETER.getErrorMsg()
                    + " ("
                    + AppConstants.MODEL + "=" + model + "," + AppConstants.CATEGORY + "="
                    + category + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }

        if (ssoId == null || ssoId.isEmpty() || portalId == null || portalId.isEmpty()) {
            log.error(
                "getAssociatedDocumentsTP (" + TechpubsException.TechpubsAppError.NOT_AUTHORIZED
                    .getErrorCode()
                    + ") - " + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg()
                    + " ("
                    + AppConstants.MODEL + "=" + model + "," + AppConstants.CATEGORY + "="
                    + category + ") " + "("
                    + AppConstants.SSO_ID + "=" + ssoId + "," + AppConstants.PORTAL_ID + "="
                    + portalId + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        List<String> modelList = getModelListForRequest(ssoId, portalId, null, model, null, null,
            null, false);
        List<String> tokenList = getEntitlements(ssoId, portalId, getSubSystem(), modelList);
        // get Associated Documents for the given TP program and category type
        List<DocumentItemModel> documentItemList = new ArrayList<>();
        int categoryType = Integer.parseInt(category);
        switch (categoryType) {
            case 1: // Fleet Reliability Scorecards - only return 1 document, no
                // sorting needed.
                documentItemList = switchTPScorecard(documentItemList, modelList, tokenList,
                    queryParams);
                break;
            case 2: // Conference Presentations
                // Get all Conference Documents, update title and sort.
                documentItemList = switchTPConfPres(documentItemList, modelList, tokenList,
                    queryParams);
                break;
            case 3: // Updates
                // Get all Updates Documents, update title and sort.
                documentItemList = switchTPUpdates(documentItemList, modelList, tokenList,
                    queryParams);
                break;
            case 4: // Reference Materials
                // Get Reference Material Documents, update title and sort.
                documentItemList = switchTPRefMaterial(documentItemList, modelList, tokenList,
                    queryParams);
                break;
            default:
                break;
        }
        AssociatedDocumentModel associatedDocumentModel = new AssociatedDocumentModel();
        associatedDocumentModel.setType(getSubSystem().toString());
        StringBuilder sbTitle = new StringBuilder(model);
        if (!documentItemList.isEmpty()) {
            sbTitle.append(" ").append(
                ((DocumentItemAssociatedTPModel) documentItemList.get(0)).getFileCategoryName());
        }
        associatedDocumentModel.setTitle(sbTitle.toString());
        associatedDocumentModel.setDocumentItemList(documentItemList);
        associatedDocumentModel.setSuccess(true);

        return associatedDocumentModel;
    }

    private List<DocumentItemModel> switchTPScorecard(List<DocumentItemModel> documentItemList,
        List<String> modelList,
        List<String> tokenList, Map<String, String> queryParams) {
        IDocSubSystemData iDocSubSystemData = docDataRegServices
            .getSubSystemService(getSubSystem());
        for (DocumentItemAssociatedTPModel documentItemAssociatedTP : ((IDocTPData) iDocSubSystemData)
            .getAssociatedDocumentsTPScorecard(modelList, tokenList, queryParams)) {
            setTPTitle(documentItemAssociatedTP);
            documentItemList.add(documentItemAssociatedTP);
        }
        return documentItemList;
    }

    private List<DocumentItemModel> switchTPConfPres(List<DocumentItemModel> documentItemList,
        List<String> modelList,
        List<String> tokenList, Map<String, String> queryParams) {
        List<DocumentItemModel> documentItemAssociatedList = new ArrayList<>();
        List<String[]> sortParams = null;
        IDocSubSystemData iDocSubSystemData = docDataRegServices
            .getSubSystemService(getSubSystem());
        for (DocumentItemAssociatedTPModel documentItemAssociatedTP : ((IDocTPData) iDocSubSystemData)
            .getAssociatedDocumentsTPConfPres(modelList, tokenList, queryParams)) {
            setTPTitle(documentItemAssociatedTP);
            documentItemAssociatedList.add(documentItemAssociatedTP);
        }
        sortParams = new ArrayList<>();
        sortParams.add(new String[]{AppConstants.UPLOADYEARNUMBER, AppConstants.DESC});
        sortParams.add(new String[]{AppConstants.UPLOADMONTHNUMBER, AppConstants.DESC});
        sortParams.add(new String[]{AppConstants.FILE_TITLE, AppConstants.ASC});
        techpubsAppUtil.sortDocumentItems(documentItemAssociatedList, sortParams);
        documentItemList.addAll(documentItemAssociatedList);
        return documentItemList;
    }

    private List<DocumentItemModel> switchTPUpdates(List<DocumentItemModel> documentItemList,
        List<String> modelList,
        List<String> tokenList, Map<String, String> queryParams) {
        List<DocumentItemModel> documentItemAssociatedList = new ArrayList<>();
        List<String[]> sortParams = null;
        IDocSubSystemData iDocSubSystemData = docDataRegServices
            .getSubSystemService(getSubSystem());
        for (DocumentItemAssociatedTPModel documentItemAssociatedTP : ((IDocTPData) iDocSubSystemData)
            .getAssociatedDocumentsTPUpdates(modelList, tokenList, queryParams)) {
            setTPTitle(documentItemAssociatedTP);
            documentItemAssociatedList.add(documentItemAssociatedTP);
        }
        sortParams = new ArrayList<>();
        sortParams.add(new String[]{AppConstants.UPLOADYEARNUMBER, AppConstants.DESC});
        sortParams.add(new String[]{AppConstants.UPLOADMONTHNUMBER, AppConstants.DESC});
        sortParams.add(new String[]{AppConstants.FILE_TITLE, AppConstants.ASC});
        techpubsAppUtil.sortDocumentItems(documentItemAssociatedList, sortParams);
        documentItemList.addAll(documentItemAssociatedList);
        return documentItemList;
    }

    private List<DocumentItemModel> switchTPRefMaterial(List<DocumentItemModel> documentItemList,
        List<String> modelList, List<String> tokenList, Map<String, String> queryParams) {
        List<DocumentItemModel> documentItemAssociatedList = new ArrayList<>();
        List<String[]> sortParams = null;
        IDocSubSystemData iDocSubSystemData = docDataRegServices
            .getSubSystemService(getSubSystem());
        for (DocumentItemAssociatedTPModel documentItemAssociatedTP : ((IDocTPData) iDocSubSystemData)
            .getAssociatedDocumentsTPRefMaterial(modelList, tokenList, queryParams)) {
            setTPTitle(documentItemAssociatedTP);
            documentItemAssociatedList.add(documentItemAssociatedTP);
        }
        sortParams = new ArrayList<>();
        sortParams.add(new String[]{AppConstants.TITLE, AppConstants.DESC});
        techpubsAppUtil.sortDocumentItems(documentItemAssociatedList, sortParams);
        documentItemList.addAll(documentItemAssociatedList);
        return documentItemList;
    }

    @Override
    @LogExecutionTime
    public DocumentDataTableModel getDownloadTP(String ssoId, String portalId, String model,
        String category,
        Map<String, String> queryParams) throws TechpubsException {

        // Documents are already sorted.
        List<DocumentItemModel> docItemList = getAssociatedDocumentsTP(ssoId, portalId, model,
            category, queryParams)
            .getDocumentItemList();
        techpubsAppUtil.sortDocumentItems(docItemList, queryParams);

        int resultSize = docItemList.size();
        int iDisplayLength = Integer.parseInt(queryParams.get(AppConstants.IDISPLAYLENGTH));
        int iDisplayStart = Integer.parseInt(queryParams.get(AppConstants.IDISPLAYSTART));
        String sEcho = queryParams.get(AppConstants.SECHO);
        DocumentDataTableModel documentDataTable = new DocumentDataTableModel();
        documentDataTable.setIDisplayLength(iDisplayLength);
        documentDataTable.setIDisplayStart(iDisplayStart);
        documentDataTable.setITotalDisplayRecords(resultSize);
        documentDataTable.setITotalRecords(resultSize);
        documentDataTable.setSEcho(sEcho);

        documentDataTable
            .setDocumentItemList(
                docItemList.subList((iDisplayStart > resultSize ? resultSize : iDisplayStart),
                    (iDisplayStart + iDisplayLength > resultSize ? resultSize
                        : iDisplayStart + iDisplayLength)));

        documentDataTable.setSuccess(true);

        return documentDataTable;
    }

    /**
     * Return HTML for the first Technical Publication in the associated document list Authorization
     * will be determined in the invoked methods
     *
     * @param ssoId the sso id
     * @param portalId the portal id (CWC/GEHONDA)
     * @param category the category
     * @param queryParams the query params
     */
    @Override
    @LogExecutionTime
    public String getTPResourceInit(String ssoId, String portalId, String model, String category,
        Map<String, String> queryParams) throws TechpubsException {

        List<DocumentItemModel> documentItemList = getAssociatedDocumentsTP(ssoId, portalId, model,
            category, null)
            .getDocumentItemList();

        // Deny Access if no associated documents found (should not happen)
        if (documentItemList.isEmpty()) {
            log.error(
                AppConstants.RESOURCEINIT_TP + TechpubsException.TechpubsAppError.NOT_AUTHORIZED
                    .getErrorCode()
                    + ") - " + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg()
                    + AppConstants.EQ_MODEL
                    + model + AppConstants.COMMA_CATEGORY + category + ") " + AppConstants.EQ_SSO
                    + ssoId
                    + AppConstants.EQ_PORTAL + portalId + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        DocumentItemAssociatedTPModel documentItemAssociatedTP = (DocumentItemAssociatedTPModel) documentItemList
            .get(0);

        return getResource(ssoId, portalId, model, documentItemAssociatedTP.getId(), queryParams);
    }

}
