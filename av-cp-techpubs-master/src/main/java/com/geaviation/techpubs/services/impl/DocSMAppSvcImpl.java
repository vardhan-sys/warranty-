package com.geaviation.techpubs.services.impl;

import com.geaviation.techpubs.data.api.IDocSMData;
import com.geaviation.techpubs.data.api.IDocSubSystemData;
import com.geaviation.techpubs.data.util.log.LogExecutionTime;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.AssociatedDocumentModel;
import com.geaviation.techpubs.models.DocumentDataTableModel;
import com.geaviation.techpubs.models.DocumentInfoModel;
import com.geaviation.techpubs.models.DocumentItemAssociatedSMModel;
import com.geaviation.techpubs.models.DocumentItemModel;
import com.geaviation.techpubs.models.DocumentModel;
import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.services.api.IDocSMApp;
import com.geaviation.techpubs.services.util.AppConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
public class DocSMAppSvcImpl extends AbstractDocSubSystemAppImpl implements IDocSMApp {

    private static final String FILE_NAME = "_Supplementary_Manuals_";
    private static final Logger LOGGER = LogManager.getLogger(DocSMAppSvcImpl.class);

    @Override
    public SubSystem getSubSystem() {
        return SubSystem.SM;
    }

    @Override
    String setFileName() {
        return FILE_NAME;
    }

    @Override
    protected String setSubSystemResource(DocumentInfoModel documentInfo,
        DocumentItemModel documentItem,
        Map<String, String> queryParams) {
        documentInfo.setTitle(((DocumentItemAssociatedSMModel) documentItem).getTitle());
        String resourceURI = ((DocumentItemAssociatedSMModel) documentItem).getResourceUri();
        String contentType = ((DocumentItemAssociatedSMModel) documentItem).getContentType();
        String docUri = ((DocumentItemAssociatedSMModel) documentItem).getDocumentsUri();
        if (queryParams != null && queryParams.containsKey(AppConstants.MODELS)) {
            docUri = docUri
                .replaceFirst("\\?models=[^&]*", "?models=" + queryParams.get(AppConstants.MODELS));
        }
        documentInfo.setResourceUri(
            resourceURI + AppConstants.TYPE_PARAM + getSubSystem().toString().toLowerCase());
        documentInfo.setDocumentsUri(docUri);
        return iResourceData
            .prepareWrappedResource(AppConstants.SERVICES + resourceURI + AppConstants.BIN
                    + AppConstants.TYPE_PARAM + getSubSystem().toString().toLowerCase(), contentType,
                documentInfo);

    }

    @Override
    @LogExecutionTime
    public DocumentDataTableModel getDownloadSM(String ssoId, String portalId, String model,
        String doctype,
        Map<String, String> queryParams) throws TechpubsException {

        queryParams.put(AppConstants.MODELS, model);
        List<DocumentItemModel> docItemList = getAssociatedDocumentsSM(ssoId, portalId, doctype,
            queryParams)
            .getDocumentItemList();

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
    public DocumentModel getAssociatedDocumentsSM(String ssoId, String portalId, String category,
        Map<String, String> queryParams) throws TechpubsException {

        if (ssoId == null || ssoId.isEmpty() || portalId == null || portalId.isEmpty()) {
            LOGGER.error(AppConstants.GET_ASSOCIATED_SM + " ("
                + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorCode() + ") - "
                + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg() + " ("
                + AppConstants.SSO_ID + "="
                + ssoId + ", " + AppConstants.PORTAL_ID + "=" + portalId + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        List<String> modelList = getSelectiveModelListForRequest(ssoId, portalId, queryParams);
        List<String> tokenList = getEntitlements(ssoId, portalId, getSubSystem(), modelList);

        // get Associated Documents for the given SM program and category type
        List<DocumentItemModel> documentItemList = new ArrayList<>();
        List<DocumentItemModel> documentItemAssociatedList = null;
        List<String[]> sortParams = null;

        // Get all Documents, update title and sort.
        documentItemAssociatedList = new ArrayList<>();
        IDocSubSystemData iDocSubSystemData = docDataRegServices
            .getSubSystemService(getSubSystem());
        for (DocumentItemAssociatedSMModel documentItemAssociatedSM : ((IDocSMData) iDocSubSystemData)
            .getAssociatedDocumentsSM(modelList, tokenList, category, queryParams)) {
            documentItemAssociatedList.add(documentItemAssociatedSM);
        }
        sortParams = new ArrayList<>();
        sortParams.add(new String[]{AppConstants.GROUP_NAME, AppConstants.ASC});
        sortParams.add(new String[]{AppConstants.RELEASE_DATE_VALUE, AppConstants.DESC});
        sortParams.add(new String[]{AppConstants.TITLE, AppConstants.ASC});
        techpubsAppUtil.sortDocumentItems(documentItemAssociatedList, sortParams);
        documentItemList.addAll(documentItemAssociatedList);

        AssociatedDocumentModel associatedDocumentModel = new AssociatedDocumentModel();
        associatedDocumentModel.setType(getSubSystem().toString());
        associatedDocumentModel.setTitle(modelList.toString() + " " + category);
        associatedDocumentModel.setDocumentItemList(documentItemList);
        associatedDocumentModel.setSuccess(true);

        return associatedDocumentModel;
    }

    private List<String> getSelectiveModelListForRequest(String ssoId, String portalId,
        Map<String, String> queryParams)
        throws TechpubsException {

        Set<String> modelSet = new HashSet<>();
        modelSet.addAll(getModelList(ssoId, portalId, null, null));
        if (queryParams != null && queryParams.containsKey(AppConstants.MODELS)) {
            List<String> inclModels = Arrays
                .asList(queryParams.get(AppConstants.MODELS).split(","));
            for (Iterator<String> modIt = modelSet.iterator(); modIt.hasNext(); ) {
                String model = modIt.next();
                if (!inclModels.contains(model)) {
                    modIt.remove();
                }
            }
        }
        return new ArrayList<>(modelSet);
    }

    @Override
    @LogExecutionTime
    public String getSMResourceInit(String ssoId, String portalId, String category,
        Map<String, String> queryParams)
        throws TechpubsException {

        List<DocumentItemModel> documentItemList = getAssociatedDocumentsSM(ssoId, portalId,
            category, queryParams)
            .getDocumentItemList();

        // Deny Access if no associated documents found (should not happen)
        if (documentItemList.isEmpty()) {
            LOGGER.error(AppConstants.GET_SM_RESOURCE_INIT
                + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorCode() + ") - "
                + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg() + " (category="
                + category + ") "
                + " (" + AppConstants.SSO_ID + "=" + ssoId + ", " + AppConstants.PORTAL_ID + "="
                + portalId + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        DocumentItemAssociatedSMModel documentItemAssociatedSM = (DocumentItemAssociatedSMModel) documentItemList
            .get(0);

        return getResource(ssoId, portalId, null, documentItemAssociatedSM.getId(), queryParams);
    }

    @Override
    protected HashMap<String, String> getSubSystemDownloadCSVFieldMap(
        HashMap<String, String> csvFieldMap) {

        csvFieldMap.put(AppConstants.DOWNLOAD_CSV_DOCTYPE, AppConstants.DOWNLOAD_CSV_DOCTYPE_VAL);
        return csvFieldMap;
    }

    @Override
    protected List<Map<String, String>> getSubSystemDownloadCSV(String ssoId, String portalId,
        List<String> modelList,
        List<String> tokenList, IDocSubSystemData iDocSubSystemData, String model, String family,
        List<DocumentItemModel> modelDoc, String docType, Map<String, String> queryParams) {
        queryParams.put(AppConstants.MODELS, model);
        List<Map<String, String>> dataList = new ArrayList<>();
        try {
            List<DocumentItemModel> docItemSMList = getAssociatedDocumentsSM(ssoId, portalId,
                docType, queryParams)
                .getDocumentItemList();
            for (DocumentItemModel smDoc : docItemSMList) {
                DocumentItemAssociatedSMModel dam = (DocumentItemAssociatedSMModel) smDoc;
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put(AppConstants.MODEL, model);
                dataMap.put(AppConstants.TITLE, dam.getTitle());
                dataMap.put(AppConstants.DOC_TYPE, docType);
                dataMap.put(AppConstants.RELEASE_DATE, dam.getReleaseDate());
                dataList.add(dataMap);
            }
        } catch (TechpubsException e) {
            LOGGER.error(
                AppConstants.DOWNLOAD_CSV_LOGGER + TechpubsException.TechpubsAppError.INTERNAL_ERROR
                    .getErrorCode()
                    + ") - " + TechpubsException.TechpubsAppError.INTERNAL_ERROR.getErrorMsg()
                    + " ("
                    + AppConstants.MODEL + "=" + model + ")" + " (" + AppConstants.FAMILY + "="
                    + family + ")",
                e);
        }
        return dataList;

    }

}
