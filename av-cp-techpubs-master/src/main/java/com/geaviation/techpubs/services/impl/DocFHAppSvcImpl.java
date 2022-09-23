package com.geaviation.techpubs.services.impl;

import com.geaviation.techpubs.data.api.IDocFHData;
import com.geaviation.techpubs.data.api.IDocSubSystemData;
import com.geaviation.techpubs.data.util.log.LogExecutionTime;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.AssociatedDocumentModel;
import com.geaviation.techpubs.models.DocumentInfoModel;
import com.geaviation.techpubs.models.DocumentItemAssociatedFHModel;
import com.geaviation.techpubs.models.DocumentItemModel;
import com.geaviation.techpubs.models.DocumentModel;
import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.services.api.IDocFHApp;
import com.geaviation.techpubs.services.util.AppConstants;
import java.util.ArrayList;
import java.util.HashMap;
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
public class DocFHAppSvcImpl extends AbstractDocSubSystemAppImpl implements IDocFHApp {

    private static final Logger log = LogManager.getLogger(DocFHAppSvcImpl.class);

    private static final String FILE_NAME = "_Fleet_Highlites_";

    @Override
    public SubSystem getSubSystem() {
        return SubSystem.FH;
    }

    @Override
    String setFileName() {
        return FILE_NAME;
    }

    @Override
    protected void setSubSystemAssociatedDocuments(String ssoId, String portalId, String fileId,
        Map<String, String> queryParams, List<DocumentItemModel> documentItemList,
        AssociatedDocumentModel associatedDocumentModel) throws TechpubsException {

        if (!documentItemList.isEmpty()) {
            associatedDocumentModel
                .setTitle(((DocumentItemAssociatedFHModel) documentItemList.get(0)).getModel()
                    + " - " + ((DocumentItemAssociatedFHModel) documentItemList.get(0)).getYearNum()
                    + " - "
                    + ((DocumentItemAssociatedFHModel) documentItemList.get(0))
                    .getMonthQuarterDisplay());
        }

        // Append filter parameters to resource uri
        if (!documentItemList.isEmpty()) {
            String filterParams = getFHFilterParams(queryParams);
            if (filterParams != null && !filterParams.isEmpty()) {
                for (DocumentItemModel doc : documentItemList) {
                    if (!(doc.getResourceUri().contains(AppConstants.SCT)
                        || (doc.getResourceUri().contains(AppConstants.ART)))) {
                        doc.setResourceUri(doc.getResourceUri() + AppConstants.TYPE_PARAM
                            + getSubSystem().toString().toLowerCase() + filterParams);
                    }
                }
            } else {
                for (DocumentItemModel doc : documentItemList) {
                    if (!(doc.getResourceUri().contains(AppConstants.SCT)
                        || (doc.getResourceUri().contains(AppConstants.ART)))) {
                        doc.setResourceUri(doc.getResourceUri() + AppConstants.TYPE_PARAM
                            + getSubSystem().toString().toLowerCase() + filterParams);
                    }
                }
            }

        }
    }

    private String getFHFilterParams(Map<String, String> queryParams) {
        StringBuilder sbFilterQuery = new StringBuilder();
        sbFilterQuery.append((queryParams.get(AppConstants.FAMS) != null
            ? AppConstants.EQ_FAMS + queryParams.get(AppConstants.FAMS) : ""));
        sbFilterQuery.append((queryParams.get(AppConstants.MODS) != null
            ? AppConstants.EQ_MODS + queryParams.get(AppConstants.MODS) : ""));
        sbFilterQuery.append((queryParams.get(AppConstants.TAILS) != null
            ? AppConstants.EQ_TAILS + queryParams.get(AppConstants.TAILS) : ""));
        sbFilterQuery.append((queryParams.get(AppConstants.AIRCRAFTS) != null
            ? AppConstants.EQ_AIRCRAFTS + queryParams.get(AppConstants.AIRCRAFTS) : ""));
        sbFilterQuery.append((queryParams.get(AppConstants.ESNS) != null
            ? AppConstants.EQ_ESNS + queryParams.get(AppConstants.ESNS) : ""));

        if (sbFilterQuery.length() > 0) {
            sbFilterQuery.setCharAt(0, '&');
        }

        return sbFilterQuery.toString();
    }

    @Override
    protected String setSubSystemResource(DocumentInfoModel documentInfo,
        DocumentItemModel documentItem,
        Map<String, String> queryParams) {
        String filterParams = getFHFilterParams(queryParams);
        documentInfo.setTitle(((DocumentItemAssociatedFHModel) documentItem).getTitle());
        String resourceURI = ((DocumentItemAssociatedFHModel) documentItem).getResourceUri();
        String contentType = ((DocumentItemAssociatedFHModel) documentItem).getContentType();
        String documentsURI = ((DocumentItemAssociatedFHModel) documentItem).getDocumentsUri();
        documentInfo.setResourceUri(
            resourceURI + AppConstants.TYPE_PARAM + getSubSystem().toString().toLowerCase()
                + filterParams);
        documentInfo.setDocumentsUri(documentsURI + filterParams);
        return iResourceData
            .prepareWrappedResource(AppConstants.SERVICES + resourceURI + AppConstants.PDF
                    + AppConstants.TYPE_PARAM + getSubSystem().toString().toLowerCase(), contentType,
                documentInfo);

    }

    @Override
    protected HashMap<String, String> getSubSystemDownloadCSVFieldMap(
        HashMap<String, String> csvFieldMap) {

        csvFieldMap.put(AppConstants.SECTION, AppConstants.CAPS_SECTION);
        return csvFieldMap;
    }

    @Override
    protected List<Map<String, String>> getSubSystemDownloadCSV(String ssoId, String portalId,
        List<String> modelList,
        List<String> tokenList, IDocSubSystemData iDocSubSystemData, String model, String family,
        List<DocumentItemModel> overallFHList, String docType, Map<String, String> queryParams) {

        List<Map<String, String>> dataList = new ArrayList<>();
        // Now get all associated FH docs: Overall (again), Sections, Articles.
        Map<String, String> par = new HashMap<>();
        par.put(AppConstants.MODS, model);
        for (DocumentItemModel overallDoc : overallFHList) {
            String fileId = overallDoc.getId();
            DocumentModel docModel;
            try {
                docModel = getAssociatedDocuments(ssoId, portalId, fileId, par);
                for (DocumentItemModel dim : docModel.getDocumentItemList()) {
                    DocumentItemAssociatedFHModel dam = (DocumentItemAssociatedFHModel) dim;
                    Map<String, String> dataMap = new HashMap<>();
                    dataMap.put(AppConstants.MODEL, dam.getModel());
                    dataMap.put(AppConstants.SECTION, dam.getSection());
                    dataMap.put(AppConstants.TITLE, dam.getTitle());
                    dataMap.put(AppConstants.RELEASE_DATE, dam.getReleaseDate());
                    dataList.add(dataMap);
                }
            } catch (TechpubsException e) {
                log.error(AppConstants.DOWNLOAD_SUBSYSTEM_CSV
                    + TechpubsException.TechpubsAppError.INTERNAL_ERROR.getErrorCode() + ") - "
                    + TechpubsException.TechpubsAppError.INTERNAL_ERROR.getErrorMsg() + " ("
                    + AppConstants.MODEL
                    + "=" + model + ")", e);
            }

        }
        return dataList;

    }

    @Override
    protected List<DocumentItemModel> setSubSystemDownload(String ssoId, String portalId,
        String model,
        List<DocumentItemModel> docItemList, Map<String, String> queryParams) {
        Map<String, String> par = new HashMap<>();
        List<DocumentItemModel> overallFHList = new ArrayList<>();
        try {
            techpubsAppUtil.sortDocumentItems(docItemList, queryParams);
            par.put(AppConstants.MODS, model);
            for (DocumentItemModel overallDoc : docItemList) {
                String fileId = overallDoc.getId();
                DocumentModel docModel;

                docModel = getAssociatedDocuments(ssoId, portalId, fileId, par);

                overallFHList.addAll(docModel.getDocumentItemList());
            }
            int resultSize = overallFHList.size();
            if (resultSize > 0) {
                // Append filter parameters to resource uri
                String filterParams = getFHFilterParams(queryParams);
                if (filterParams != null && !filterParams.isEmpty()) {
                    for (DocumentItemModel doc : overallFHList) {
                        doc.setResourceUri(doc.getResourceUri() + filterParams);
                    }
                }
            }
        } catch (TechpubsException e) {
            log.error(
                AppConstants.DOWNLOAD_SUBSYSTEM + TechpubsException.TechpubsAppError.INTERNAL_ERROR
                    .getErrorCode()
                    + ") - " + TechpubsException.TechpubsAppError.INTERNAL_ERROR.getErrorMsg()
                    + " ("
                    + AppConstants.MODEL + "=" + model + ")",
                e);
        }
        return overallFHList;

    }

    @Override
    @LogExecutionTime
    public String getFHResourceSCT(String ssoId, String portalId, String overallFileId,
        String fileId,
        Map<String, String> queryParams) throws TechpubsException {

        if (ssoId == null || ssoId.isEmpty() || portalId == null || portalId.isEmpty()) {
            log.error(
                AppConstants.FH_RESOURCE_SCT + TechpubsException.TechpubsAppError.NOT_AUTHORIZED
                    .getErrorCode()
                    + ") - " + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg()
                    + " ("
                    + AppConstants.OVERALLFILEID + "=" + overallFileId + ", " + AppConstants.FILE_ID
                    + "=" + fileId
                    + ") " + "(" + AppConstants.SSO_ID + "=" + ssoId + "," + AppConstants.PORTAL_ID
                    + "=" + portalId
                    + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        List<String> modelList = getModelListForRequest(ssoId, portalId, null, null, null, null,
            null,
            getLessorModelList());
        List<String> tokenList = getEntitlements(ssoId, portalId, SubSystem.FH, modelList);

        IDocSubSystemData iDocSubSystemData = docDataRegServices
            .getSubSystemService(getSubSystem());

        DocumentItemAssociatedFHModel documentItemAssociatedFH = ((IDocFHData) iDocSubSystemData)
            .getFHDocumentSCT(modelList, tokenList, overallFileId, fileId);

        if (documentItemAssociatedFH == null) {
            // Deny Access
            log.error(
                AppConstants.FH_RESOURCE_SCT + TechpubsException.TechpubsAppError.NOT_AUTHORIZED
                    .getErrorCode()
                    + ") - " + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg()
                    + " ("
                    + AppConstants.OVERALLFILEID + "=" + overallFileId + ", " + AppConstants.FILE_ID
                    + "=" + fileId
                    + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }
        DocumentInfoModel documentInfo = new DocumentInfoModel();
        documentInfo.setType(getSubSystem().toString());
        return setSubSystemResource(documentInfo, documentItemAssociatedFH, queryParams);
    }

    @Override
    @LogExecutionTime
    public Map<String, Object> getFHArtifactSCT(String ssoId, String portalId, String overallFileId,
        String fileId)
        throws TechpubsException {

        if (ssoId == null || ssoId.isEmpty() || portalId == null || portalId.isEmpty()) {
            log.error(
                AppConstants.FH_RESOURCE_SCT + TechpubsException.TechpubsAppError.NOT_AUTHORIZED
                    .getErrorCode()
                    + ") - " + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg()
                    + " ("
                    + AppConstants.OVERALLFILEID + "=" + overallFileId + ", " + AppConstants.FILE_ID
                    + "=" + fileId
                    + ") " + "(" + AppConstants.SSO_ID + "=" + ssoId + "," + AppConstants.PORTAL_ID
                    + "=" + portalId
                    + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        List<String> modelList = getModelListForRequest(ssoId, portalId, null, null, null, null,
            null,
            getLessorModelList());
        List<String> tokenList = getEntitlements(ssoId, portalId, SubSystem.FH, modelList);

        IDocSubSystemData iDocSubSystemData = docDataRegServices
            .getSubSystemService(getSubSystem());

        DocumentItemAssociatedFHModel documentItemAssociatedFH = ((IDocFHData) iDocSubSystemData)
            .getFHDocumentSCT(modelList, tokenList, overallFileId, fileId);

        if (documentItemAssociatedFH == null) {
            // Deny Access
            log.error(
                AppConstants.FH_RESOURCE_SCT + TechpubsException.TechpubsAppError.NOT_AUTHORIZED
                    .getErrorCode()
                    + ") - " + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg()
                    + " ("
                    + AppConstants.OVERALLFILEID + "=" + overallFileId + ", " + AppConstants.FILE_ID
                    + "=" + fileId
                    + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        return iResourceData.getArtifact(fileId);
    }

    @Override
    @LogExecutionTime
    public String getFHResourceART(String ssoId, String portalId, String overallFileId,
        String fileId,
        Map<String, String> queryParams) throws TechpubsException {

        if (ssoId == null || ssoId.isEmpty() || portalId == null || portalId.isEmpty()) {
            log.error(
                AppConstants.FH_RESOURCE_SCT + TechpubsException.TechpubsAppError.NOT_AUTHORIZED
                    .getErrorCode()
                    + ") - " + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg()
                    + " ("
                    + AppConstants.OVERALLFILEID + "=" + overallFileId + ", " + AppConstants.FILE_ID
                    + "=" + fileId
                    + ") " + "(" + AppConstants.SSO_ID + "=" + ssoId + "," + AppConstants.PORTAL_ID
                    + "=" + portalId
                    + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        List<String> modelList = getModelListForRequest(ssoId, portalId, null, null, null, null,
            null,
            getLessorModelList());
        List<String> tokenList = getEntitlements(ssoId, portalId, SubSystem.FH, modelList);

        IDocSubSystemData iDocSubSystemData = docDataRegServices
            .getSubSystemService(getSubSystem());

        DocumentItemAssociatedFHModel documentItemAssociatedFH = ((IDocFHData) iDocSubSystemData)
            .getFHDocumentART(modelList, tokenList, overallFileId, fileId);

        if (documentItemAssociatedFH == null) {
            // Deny Access
            log.error(
                AppConstants.FH_RESOURCE_SCT + TechpubsException.TechpubsAppError.NOT_AUTHORIZED
                    .getErrorCode()
                    + ") - " + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg()
                    + " ("
                    + AppConstants.OVERALLFILEID + "=" + overallFileId + ", " + AppConstants.FILE_ID
                    + "=" + fileId
                    + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }
        DocumentInfoModel documentInfo = new DocumentInfoModel();
        documentInfo.setType(getSubSystem().toString());
        return setSubSystemResource(documentInfo, documentItemAssociatedFH, queryParams);
    }

    @Override
    @LogExecutionTime
    public Map<String, Object> getFHArtifactART(String ssoId, String portalId, String overallFileId,
        String fileId)
        throws TechpubsException {

        if (ssoId == null || ssoId.isEmpty() || portalId == null || portalId.isEmpty()) {
            log.error(
                AppConstants.FH_RESOURCE_SCT + TechpubsException.TechpubsAppError.NOT_AUTHORIZED
                    .getErrorCode()
                    + ") - " + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg()
                    + " ("
                    + AppConstants.OVERALLFILEID + "=" + overallFileId + ", " + AppConstants.FILE_ID
                    + "=" + fileId
                    + ") " + "(" + AppConstants.SSO_ID + "=" + ssoId + "," + AppConstants.PORTAL_ID
                    + "=" + portalId
                    + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        List<String> modelList = getModelListForRequest(ssoId, portalId, null, null, null, null,
            null,
            getLessorModelList());
        List<String> tokenList = getEntitlements(ssoId, portalId, SubSystem.FH, modelList);

        IDocSubSystemData iDocSubSystemData = docDataRegServices
            .getSubSystemService(getSubSystem());

        DocumentItemAssociatedFHModel documentItemAssociatedFH = ((IDocFHData) iDocSubSystemData)
            .getFHDocumentART(modelList, tokenList, overallFileId, fileId);

        if (documentItemAssociatedFH == null) {
            // Deny Access
            log.error(
                AppConstants.FH_RESOURCE_SCT + TechpubsException.TechpubsAppError.NOT_AUTHORIZED
                    .getErrorCode()
                    + ") - " + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg()
                    + " ("
                    + AppConstants.OVERALLFILEID + "=" + overallFileId + ", " + AppConstants.FILE_ID
                    + "=" + fileId
                    + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        return iResourceData.getArtifact(fileId);
    }

}
