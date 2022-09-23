package com.geaviation.techpubs.services.impl;

import com.geaviation.techpubs.data.api.IDocLLData;
import com.geaviation.techpubs.data.api.IDocSubSystemData;
import com.geaviation.techpubs.data.util.log.LogExecutionTime;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.AssociatedDocumentModel;
import com.geaviation.techpubs.models.DocumentDataTableModel;
import com.geaviation.techpubs.models.DocumentInfoModel;
import com.geaviation.techpubs.models.DocumentItemAssociatedLLModel;
import com.geaviation.techpubs.models.DocumentItemModel;
import com.geaviation.techpubs.models.DocumentModel;
import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.services.api.IDocLLApp;
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
public class DocLLAppSvcImpl extends AbstractDocSubSystemAppImpl implements IDocLLApp {

    private static final String FILE_NAME = "_Lessor_Library_";

    private static final Logger log = LogManager.getLogger(DocLLAppSvcImpl.class);

    @Override
    public SubSystem getSubSystem() {
        return SubSystem.LL;
    }

    @Override
    protected List<String> getSubSytemEntitlements(String currentOrg, String portalId,
        SubSystem subSystem,
        List<String> modelList) {

        return iEntitlementData.getEntitlements(currentOrg, portalId, subSystem.toString(), null);
    }

    @Override
    String setFileName() {
        return FILE_NAME;
    }

    @Override
    protected String setSubSystemResource(DocumentInfoModel documentInfo,
        DocumentItemModel documentItem,
        Map<String, String> queryParams) {
        setLLTitle((DocumentItemAssociatedLLModel) documentItem);
        documentInfo.setTitle(((DocumentItemAssociatedLLModel) documentItem).getTitle());
        String resourceURI = ((DocumentItemAssociatedLLModel) documentItem).getResourceUri();
        String contentType = ((DocumentItemAssociatedLLModel) documentItem).getContentType();
        String documentsURI = ((DocumentItemAssociatedLLModel) documentItem).getDocumentsUri();
        documentInfo.setResourceUri(
            resourceURI + AppConstants.TYPE_PARAM + getSubSystem().toString().toLowerCase());
        documentInfo.setDocumentsUri(documentsURI);
        return iResourceData
            .prepareWrappedResource(AppConstants.SERVICES + resourceURI + AppConstants.BIN
                    + AppConstants.TYPE_PARAM + getSubSystem().toString().toLowerCase(), contentType,
                documentInfo);
    }

    private void setLLTitle(DocumentItemAssociatedLLModel documentItemAssociatedLL) {
        StringBuilder sb = new StringBuilder();
        String groupName = documentItemAssociatedLL.getGroupName();

        if (groupName != null && !groupName.isEmpty()) {
            sb.append(groupName).append(" - ");
        }

        switch (documentItemAssociatedLL.getFileCategoryTypeId()) {
            case 2: // Conference Presentations
                sb.append(documentItemAssociatedLL.getConferenceLocation()).append(" - ");
                sb.append(documentItemAssociatedLL.getUploadMonthName()).append(" - ");
                sb.append(documentItemAssociatedLL.getUploadYearNumber()).append(" - ");
                break;
            case 3: // Updates
                sb.append(documentItemAssociatedLL.getUploadMonthName()).append(" - ");
                sb.append(documentItemAssociatedLL.getUploadYearNumber()).append(" - ");
                break;
            case 4: // Reference Materials
                break;
            default:
                documentItemAssociatedLL.setTitle(documentItemAssociatedLL.getFileTitle());
                break;
        }
        sb.append(documentItemAssociatedLL.getFileTitle());
        documentItemAssociatedLL.setTitle(sb.toString());
    }

    @Override
    @LogExecutionTime
    public DocumentDataTableModel getDownloadLL(String ssoId, String portalId, String category,
        Map<String, String> queryParams) throws TechpubsException {

        // Documents are already sorted.
        List<DocumentItemModel> docItemList = getAssociatedDocumentsLL(ssoId, portalId, category,
            queryParams)
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

    @Override
    @LogExecutionTime
    public DocumentModel getAssociatedDocumentsLL(String ssoId, String portalId, String category,
        Map<String, String> queryParams) throws TechpubsException {

        if (!StringUtils.isInteger(category)) {
            log.error(AppConstants.GET_ASSOCIATEDDOC_LL
                + TechpubsException.TechpubsAppError.INVALID_PARAMETER.getErrorCode() + ") - "
                + TechpubsException.TechpubsAppError.INVALID_PARAMETER.getErrorMsg()
                + AppConstants.EQ_CATEGORY
                + category + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }

        if (ssoId == null || ssoId.isEmpty() || portalId == null || portalId.isEmpty()) {
            log.error(AppConstants.GET_ASSOCIATEDDOC_LL
                + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorCode() + ") - "
                + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg()
                + AppConstants.EQ_CATEGORY
                + category + ") " + AppConstants.EQ_SSO + ssoId + AppConstants.EQ_PORTAL + portalId
                + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        List<String> tokenList = getEntitlements(ssoId, portalId, SubSystem.LL, null);

        // get Associated Documents for the given LL program and category type
        List<DocumentItemModel> documentItemList = new ArrayList<>();
        int categoryType = Integer.parseInt(category);
        switch (categoryType) {
            case 2: // Conference Presentations
                // Get all Conference Documents, update title and sort.
                documentItemList = getAssociatedDocumentsLLConfPresSwitch(documentItemList,
                    tokenList, queryParams);
                break;
            case 3: // Updates
                // Get all Updates Documents, update title and sort.
                documentItemList = getAssociatedDocumentsLLUpdatesSwitch(documentItemList,
                    tokenList, queryParams);
                break;
            case 4: // Reference Materials
                // Get Reference Material Documents, update title and sort.
                documentItemList = getAssociatedDocumentsLLRefMaterialSwitch(documentItemList,
                    tokenList, queryParams);
                break;
            default:
                break;
        }

        AssociatedDocumentModel associatedDocumentModel = new AssociatedDocumentModel();
        associatedDocumentModel.setType(AppConstants.LL);
        StringBuilder sbTitle = new StringBuilder();
        if (!documentItemList.isEmpty()) {
            sbTitle.append(
                ((DocumentItemAssociatedLLModel) documentItemList.get(0)).getFileCategoryName());
        }
        associatedDocumentModel.setTitle(sbTitle.toString());
        associatedDocumentModel.setDocumentItemList(documentItemList);
        associatedDocumentModel.setSuccess(true);

        return associatedDocumentModel;
    }

    public List<DocumentItemModel> getAssociatedDocumentsLLConfPresSwitch(
        List<DocumentItemModel> documentItemList,
        List<String> tokenList, Map<String, String> queryParams) {
        List<DocumentItemModel> documentItemAssociatedList = new ArrayList<>();
        List<String[]> sortParams = null;
        IDocSubSystemData iDocSubSystemData = docDataRegServices.getSubSystemService(SubSystem.LL);
        for (DocumentItemAssociatedLLModel documentItemAssociatedLL : ((IDocLLData) iDocSubSystemData)
            .getAssociatedDocumentsLLConfPres(tokenList, queryParams)) {
            setLLTitle(documentItemAssociatedLL);
            documentItemAssociatedList.add(documentItemAssociatedLL);
        }
        sortParams = new ArrayList<>();
        sortParams.add(new String[]{AppConstants.UPLOADYEARNUMBER, AppConstants.DESC});
        sortParams.add(new String[]{AppConstants.UPLOADMONTHNUMBER, AppConstants.DESC});
        sortParams.add(new String[]{AppConstants.FILE_TITLE, AppConstants.ASC});
        techpubsAppUtil.sortDocumentItems(documentItemAssociatedList, sortParams);
        documentItemList.addAll(documentItemAssociatedList);
        return documentItemList;
    }

    public List<DocumentItemModel> getAssociatedDocumentsLLUpdatesSwitch(
        List<DocumentItemModel> documentItemList,
        List<String> tokenList, Map<String, String> queryParams) {
        List<DocumentItemModel> documentItemAssociatedList = new ArrayList<>();
        List<String[]> sortParams = null;
        IDocSubSystemData iDocSubSystemData = docDataRegServices.getSubSystemService(SubSystem.LL);
        for (DocumentItemAssociatedLLModel documentItemAssociatedLL : ((IDocLLData) iDocSubSystemData)
            .getAssociatedDocumentsLLUpdates(tokenList, queryParams)) {
            setLLTitle(documentItemAssociatedLL);
            documentItemAssociatedList.add(documentItemAssociatedLL);
        }
        sortParams = new ArrayList<>();
        sortParams.add(new String[]{AppConstants.UPLOADYEARNUMBER, AppConstants.DESC});
        sortParams.add(new String[]{AppConstants.UPLOADMONTHNUMBER, AppConstants.DESC});
        sortParams.add(new String[]{AppConstants.FILE_TITLE, AppConstants.ASC});
        techpubsAppUtil.sortDocumentItems(documentItemAssociatedList, sortParams);
        documentItemList.addAll(documentItemAssociatedList);
        return documentItemList;
    }

    public List<DocumentItemModel> getAssociatedDocumentsLLRefMaterialSwitch(
        List<DocumentItemModel> documentItemList,
        List<String> tokenList, Map<String, String> queryParams) {
        List<DocumentItemModel> documentItemAssociatedList = new ArrayList<>();
        List<String[]> sortParams = null;
        IDocSubSystemData iDocSubSystemData = docDataRegServices.getSubSystemService(SubSystem.LL);
        for (DocumentItemAssociatedLLModel documentItemAssociatedLL : ((IDocLLData) iDocSubSystemData)
            .getAssociatedDocumentsLLRefMaterial(tokenList, queryParams)) {
            setLLTitle(documentItemAssociatedLL);
            documentItemAssociatedList.add(documentItemAssociatedLL);
        }
        sortParams = new ArrayList<>();
        sortParams.add(new String[]{AppConstants.TITLE, AppConstants.DESC});
        techpubsAppUtil.sortDocumentItems(documentItemAssociatedList, sortParams);
        documentItemList.addAll(documentItemAssociatedList);
        return documentItemList;
    }

    @Override
    @LogExecutionTime
    public String getLLResourceInit(String ssoId, String portalId, String category,
        Map<String, String> queryParams)
        throws TechpubsException {

        List<DocumentItemModel> documentItemList = getAssociatedDocumentsLL(ssoId, portalId,
            category, null)
            .getDocumentItemList();

        // Deny Access if no associated documents found (should not happen)
        if (documentItemList.isEmpty()) {
            log.error(
                AppConstants.RESOURCEINIT_LL + TechpubsException.TechpubsAppError.NOT_AUTHORIZED
                    .getErrorCode()
                    + ") - " + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg()
                    + AppConstants.EQ_CATEGORY + category + ") " + AppConstants.EQ_SSO + ssoId
                    + AppConstants.EQ_PORTAL
                    + portalId + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        DocumentItemAssociatedLLModel documentItemAssociatedLL = (DocumentItemAssociatedLLModel) documentItemList
            .get(0);

        return getResource(ssoId, portalId, null, documentItemAssociatedLL.getId(), queryParams);
    }

    @Override
    @LogExecutionTime
    public File getDownloadLLCSV(String ssoId, String portalId, String category,
        Map<String, String> queryParams)
        throws TechpubsException {
        List<DocumentItemModel> docItemList = getAssociatedDocumentsLL(ssoId, portalId, category,
            queryParams)
            .getDocumentItemList();
        techpubsAppUtil.sortDocumentItems(docItemList, queryParams);

        List<Map<String, String>> dataList = new ArrayList<>();
        HashMap<String, String> csvFieldMap = new LinkedHashMap<>();
        csvFieldMap.put("title", "Title");
        csvFieldMap.put("releasedate", "Release Date");

        // Build the CSV data.
        for (DocumentItemModel llDoc : docItemList) {
            DocumentItemAssociatedLLModel dam = (DocumentItemAssociatedLLModel) llDoc;
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("title", dam.getTitle());
            dataMap.put("releasedate", dam.getReleaseDate());
            dataList.add(dataMap);
        }

        File csvFile = null;
        try {
            csvFile = iCSVExportApp
                .csvExport(ssoId, "DOCUMENT_DOWNLOAD_EXPORT", dataList, new ArrayList<String>(),
                    csvFieldMap);
        } catch (Exception e) {
            log.error("getDownloadLLCSV (CSV Error) ("
                + TechpubsException.TechpubsAppError.INTERNAL_ERROR.getErrorCode() + ") - "
                + TechpubsException.TechpubsAppError.INTERNAL_ERROR.getErrorMsg() + " (category="
                + category + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR, e);
        }
        return csvFile;
    }

}
