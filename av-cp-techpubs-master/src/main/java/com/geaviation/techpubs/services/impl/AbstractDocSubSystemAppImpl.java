package com.geaviation.techpubs.services.impl;

import com.geaviation.csv.export.app.api.ICSVExportApp;
import com.geaviation.dss.service.common.exception.TechnicalException;
import com.geaviation.techpubs.data.api.IDocMongoData;
import com.geaviation.techpubs.data.api.IDocSubSystemData;
import com.geaviation.techpubs.data.api.IEntitlementData;
import com.geaviation.techpubs.data.api.IResourceData;
import com.geaviation.techpubs.data.impl.DocDataRegServices;
import com.geaviation.techpubs.data.util.log.LogExecutionTime;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.AssociatedDocumentModel;
import com.geaviation.techpubs.models.DocumentDataTableModel;
import com.geaviation.techpubs.models.DocumentDownloadModel;
import com.geaviation.techpubs.models.DocumentInfoModel;
import com.geaviation.techpubs.models.DocumentItemModel;
import com.geaviation.techpubs.models.DocumentModel;
import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.services.api.IDocSubSystemApp;
import com.geaviation.techpubs.services.util.AppConstants;
import com.geaviation.techpubs.services.util.StringUtils;
import com.geaviation.techpubs.services.util.TechpubsAppUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Configuration
@ConfigurationProperties
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
@Component
public abstract class AbstractDocSubSystemAppImpl implements IDocSubSystemApp {

    private static final Logger log = LogManager.getLogger(AbstractDocSubSystemAppImpl.class);

    @Autowired
    protected TechpubsAppUtil techpubsAppUtil;

    @Autowired
    protected IEntitlementData iEntitlementData;

    @Autowired
    protected IResourceData iResourceData;

    @Autowired
    protected DocDataRegServices docDataRegServices;

    @Autowired
    protected ICSVExportApp iCSVExportApp;


    @Value("${PDF.HTMLDIRECTURL}")
    protected String directHtmlURL;

    @Value("${PDF.HTMLURL}")
    protected String htmlURL;

    @Value("${FLAG.FEATURE.US471731}")
    boolean flag_US471731;

    protected static final String LESSOR_LIB = "LESSOR-LIBRARY";

    protected boolean getLessorModelList() {
        return false;
    }

    protected boolean getIncludeParts() {
        return false;
    }

    abstract String setFileName();

    @LogExecutionTime
    public List<DocumentItemModel> getDocuments(String ssoId, String portalId,
        Map<String, String> searchFilter,
        Map<String, String> queryParams) throws TechpubsException {

        String family = searchFilter.get(AppConstants.FAMILY);
        String model = searchFilter.get(AppConstants.MODEL);
        String aircraft = searchFilter.get(AppConstants.AIRCRAFT);
        String tail = searchFilter.get(AppConstants.TAIL);
        List<String> esnList = new ArrayList<>();
        if (TechpubsAppUtil.isNotNullandEmpty(searchFilter.get(AppConstants.ESN))) {
            esnList = Arrays.asList(searchFilter.get(AppConstants.ESN).split("\\|"));
        }
        List<String> modelList = getModelListForRequest(ssoId, portalId, family, model, aircraft,
            tail, esnList,
            getLessorModelList());
        List<String> tokenList = getEntitlements(ssoId, portalId, getSubSystem(), modelList);

        IDocSubSystemData iDocSubSystemData = docDataRegServices
            .getSubSystemService(getSubSystem());
        List<DocumentItemModel> docList = ((IDocMongoData) iDocSubSystemData)
            .getDocuments(modelList, tokenList,
                queryParams);
        techpubsAppUtil.sortDocumentItems(docList, queryParams);
        return docList;
    }

    @LogExecutionTime
    public DocumentModel getAssociatedDocuments(String ssoId, String portalId, String fileId,
        Map<String, String> queryParams) throws TechpubsException {

        String strFamily = queryParams.get(AppConstants.FAMS);
        String strModel = queryParams.get(AppConstants.MODS);
        String strAirCraft = queryParams.get(AppConstants.AIRCRAFTS);
        String strTails = queryParams.get(AppConstants.TAILS);
        List<String> strEsns = (queryParams.get(AppConstants.ESNS) != null ? new ArrayList<String>(
            new HashSet<String>(Arrays.asList(queryParams.get(AppConstants.ESNS).split("\\|"))))
            : null);

        List<String> modelList = getModelListForRequest(ssoId, portalId, strFamily, strModel,
            strAirCraft, strTails,
            strEsns, getLessorModelList());
        List<String> tokenList = getEntitlements(ssoId, portalId, getSubSystem(), modelList);
        // get Associated Documents for the given type
        List<DocumentItemModel> documentItemList = new ArrayList<>();
        IDocSubSystemData iDocSubSystemData = docDataRegServices
            .getSubSystemService(getSubSystem());
        documentItemList.addAll(
            ((IDocMongoData) iDocSubSystemData).getAssociatedDocuments(modelList, tokenList, fileId,
                getIncludeParts()));
        AssociatedDocumentModel associatedDocumentModel = new AssociatedDocumentModel();
        setSubSystemAssociatedDocuments(ssoId, portalId, fileId, queryParams, documentItemList,
            associatedDocumentModel);
        associatedDocumentModel.setType(getSubSystem().toString());
        associatedDocumentModel.setDocumentItemList(documentItemList);
        associatedDocumentModel.setSuccess(true);
        return associatedDocumentModel;
    }

    protected void setSubSystemAssociatedDocuments(String ssoId, String portalId, String fileId,
        Map<String, String> queryParams, List<DocumentItemModel> documentItemList,
        AssociatedDocumentModel associatedDocumentModel) throws TechpubsException {

    }

    @LogExecutionTime
    public DocumentDownloadModel getDownloadResource(String ssoId, String portalId, String files)
        throws TechpubsException {

        if (TechpubsAppUtil.isNullOrEmpty(ssoId) || TechpubsAppUtil.isNullOrEmpty(portalId)) {
            log.error(AppConstants.GET_DOWNLOAD_RESOURCE + " ("
                + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorCode() + ") - "
                + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg() + " ("
                + AppConstants.FILE_ID
                + "=" + files + ") (" + AppConstants.SSO_ID + "=" + ssoId + ", "
                + AppConstants.PORTAL_ID + "="
                + portalId + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        List<String> fileList = (files != null
            ? new ArrayList<String>(new HashSet<String>(Arrays.asList(files.split("\\|"))))
            : new ArrayList<String>());

        DocumentDownloadModel documentDownload = new DocumentDownloadModel();
        documentDownload.setZipFilename(
            ssoId + setFileName() + StringUtils.getFormattedTimestamp("yyyyMMddhhmmssSSS")
                + AppConstants.ZIP);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(baos);
            List<String> modelList = getModelListForRequest(ssoId, portalId, null, null, null, null,
                null,
                getLessorModelList());
            List<String> tokenList = getEntitlements(ssoId, portalId, getSubSystem(), modelList);
            IDocSubSystemData iDocSubSystemData = docDataRegServices
                .getSubSystemService(getSubSystem());

            if (flag_US471731) {
                Map<String, Integer> filenames = new HashMap<>();
                for (String fileId : fileList) {
                    Map<String, Object> artMap = getArtifact(ssoId, portalId, fileId);
                    String originalFilename = (String) artMap.get(AppConstants.DOWNLOADNAME);

                    String downloadFilename = originalFilename;
                    if (filenames.containsKey(originalFilename)) {
                        Integer count = filenames.get(originalFilename);
                        String baseName = FilenameUtils.getBaseName(originalFilename);
                        String extension = FilenameUtils.getExtension(originalFilename);
                        downloadFilename = String.format("%s (%d).%s", baseName, count, extension);
                        filenames.put(originalFilename, count + 1);
                    } else {
                        filenames.put(originalFilename, 1);
                    }

                    zos.putNextEntry(new ZipEntry(downloadFilename));
                    zos.write((byte[]) artMap.get(AppConstants.CONTENT));
                    zos.closeEntry();
                }
            } else {
                for (String fileId : fileList) {
                    getSubSytemDownloadResource(ssoId, portalId, zos, modelList, tokenList, iDocSubSystemData, fileId);
                }
            }

            zos.close();
            documentDownload.setZipFileByteArray(baos.toByteArray());
            baos.close();

        } catch (IOException e) {
            log.error(
                AppConstants.GET_DOWNLOAD_RESOURCE + e + " (" + AppConstants.FILE_ID + "=" + files
                    + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
        }

        return documentDownload;
    }

    @SuppressWarnings("unused")
    protected void getSubSytemDownloadResource(String ssoId, String portalId, ZipOutputStream zos,
        List<String> modelList, List<String> tokenList, IDocSubSystemData iDocSubSystemData,
        String fileId)
        throws TechpubsException, IOException {

        Map<String, Object> artMap = getArtifact(ssoId, portalId, fileId);
        zos.putNextEntry(new ZipEntry((String) artMap.get(AppConstants.DOWNLOADNAME)));
        zos.write((byte[]) artMap.get(AppConstants.CONTENT));
        zos.closeEntry();
    }

    @Override
    @LogExecutionTime
    public DocumentDataTableModel getDocuments(String ssoId, String portalId, Map<String, String> queryParams) throws TechpubsException {

        Map<String, String> searchFilter = TechpubsAppUtil.getFilterFields(queryParams);
        if (!TechpubsAppUtil.isNotNullandEmpty(ssoId) || !TechpubsAppUtil
            .isNotNullandEmpty(portalId)) {
            log.error(AppConstants.ERROR_MESSAGE);
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        TechpubsAppUtil.validateDatatableParameters(queryParams);
        List<DocumentItemModel> docItemList = null;

        docItemList = getDocuments(ssoId, portalId, searchFilter, queryParams);

        int resultSize = docItemList.size();

        if (resultSize > 1) {
            techpubsAppUtil.sortDocumentItems(docItemList, queryParams);
        }
        int iDisplayLength = Integer.parseInt(queryParams.get(AppConstants.IDISPLAYLENGTH));
        int iDisplayStart = Integer.parseInt(queryParams.get(AppConstants.IDISPLAYSTART));
        String sEcho = queryParams.get(AppConstants.SECHO);
        DocumentDataTableModel documentDataTable = new DocumentDataTableModel();
        documentDataTable.setIDisplayLength(iDisplayLength);
        documentDataTable.setIDisplayStart(iDisplayStart);
        documentDataTable.setITotalDisplayRecords(docItemList.size());
        documentDataTable.setITotalRecords(docItemList.size());
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
     * Returns list of engine models and authorization list for a request
     *
     * @param ssoId - User Id
     * @param portalId - Portal Id (CWC/GEHONDA)
     * @param family - MDM Family
     * @param model - MDM Model
     * @param aircraft - MDM Aircraft
     * @param tail - MDM Tail
     * @param esnList - List of MDM ESNs (Serial #)
     * @param withLessor - returns true or false
     * @return List - List of engine models
     * @throws TechpubsException the techpubs exceptions
     */
    protected List<String> getModelListForRequest(String ssoId, String portalId, String family,
        String model,
        String aircraft, String tail, List<String> esnList, boolean withLessor)
        throws TechpubsException {
        boolean tempLessor = withLessor;
        Set<String> modelSet = new HashSet<>();
        if (!withLessor && LESSOR_LIB.equalsIgnoreCase(model)) {
            tempLessor = true; // Only include LESSOR if that is the model
            // selected.
        }
        if (TechpubsAppUtil.isNotNullandEmpty(model) || TechpubsAppUtil.isNotNullandEmpty(family)) {
            modelSet.addAll(getModelList(ssoId, portalId, family, model));
        } else if (TechpubsAppUtil.isCollectionNotEmpty(esnList)) {
            for (String esn : esnList) {
                modelSet.addAll(getModelList(ssoId, portalId, family, model, aircraft, tail, esn));
            }
        } else if (TechpubsAppUtil.isNotNullandEmpty(aircraft) || TechpubsAppUtil
            .isNotNullandEmpty(tail)) {
            modelSet.addAll(getModelList(ssoId, portalId, family, model, aircraft, tail, null));
        } else {
            // 'ALL/ALL' Selected - Return all MDM engine models...
            modelSet.addAll(getModelList(ssoId, portalId, null, null));
        }
        if (tempLessor) {
            modelSet.add(LESSOR_LIB);
        }

        return new ArrayList<>(modelSet);
    }

    /**
     * Use MDM to get engine families/models user has access to. Use Navigation L1 service to
     * retrieve engine family/model mapping.
     *
     * @param ssoId - User Id
     * @param portalId - Portal Id (CWC/GEHONDA)
     * @param family - engine family
     * @param model - engine model
     * @return List - List of MDM models
     * @throws TechpubsException - the techpubs exceptions
     */
    protected List<String> getModelList(String ssoId, String portalId, String family, String model)
        throws TechpubsException {

        Set<String> modelSet = new HashSet<>();

        StringBuilder result = techpubsAppUtil.getNavigationl1(ssoId, portalId);
        JSONObject resultJsonObj = new JSONObject(result.toString());
        Iterator<?> keys = resultJsonObj.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            if (key.equalsIgnoreCase(AppConstants.ENGINE)) {
                JSONObject engineObj = resultJsonObj.getJSONObject(key);
                Iterator<?> engineFamiles = engineObj.keys();
                while (engineFamiles.hasNext()) {
                    String engineFamily = (String) engineFamiles.next();
                    JSONArray engineModels = engineObj.getJSONArray(engineFamily);
                    setModelSet(family, model, modelSet, engineFamily, engineModels);
                }
            }
        }
        log.debug("modelset size-" + modelSet.size());
        return new ArrayList<>(modelSet);
    }

    private void setModelSet(String family, String model, Set<String> modelSet, String engineFamily,
        JSONArray engineModels) {
        if (TechpubsAppUtil.isNotNullandEmpty(model)) {
            for (int i = 0; i < engineModels.length(); i++) {
                if (model.equalsIgnoreCase(engineModels.get(i).toString())) {
                    modelSet.add(engineModels.get(i).toString());
                }
            }
        } else if (TechpubsAppUtil.isNotNullandEmpty(family)) {
            if (family.equalsIgnoreCase(engineFamily)) {
                for (int i = 0; i < engineModels.length(); i++) {
                    modelSet.add(engineModels.get(i).toString());
                }
            }
        } else {
            for (int i = 0; i < engineModels.length(); i++) {
                modelSet.add(engineModels.get(i).toString());
            }
        }
    }

    /**
     * Use MDM to derive model(s) from aircraft/tail/esn Use Navigatopn L2 service to derive
     * model(s) from aircraft/tail/esn
     *
     * @param ssoId - User Id
     * @param portalId - Portal Id (CWC/GEHONDA)
     * @param family - MDM Family
     * @param model - MDM Model
     * @param aircraft - MDM Aircraft
     * @param tail - MDM Tail
     * @param esn - MDM Esn - List of MDM models
     * @throws TechpubsException - the techpubs exceptions
     */
    protected List<String> getModelList(String ssoId, String portalId, String family, String model,
        String aircraft,
        String tail, String esn) throws TechpubsException {
        Set<String> modelSet = new HashSet<>();

        int i = 0;
        int displayLength = 2500;
        int displayRecords = 0;
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.set(AppConstants.SECHO, Integer.toString(AppConstants.INTZERO));
        queryParams.set(AppConstants.IDISPLAYLENGTH, Integer.toString(displayLength));
        queryParams.set(AppConstants.MDATAPROP + AppConstants.INTZERO, AppConstants.FAMILY);
        queryParams.set(AppConstants.SSEARCH + AppConstants.INTZERO, family == null ? "" : family);
        queryParams.set(AppConstants.MDATAPROP + AppConstants.INTONE, AppConstants.MODEL);
        queryParams.set(AppConstants.SSEARCH + AppConstants.INTONE, model == null ? "" : model);
        queryParams.set(AppConstants.MDATAPROP + AppConstants.INTTWO, AppConstants.TYPE);
        queryParams
            .set(AppConstants.SSEARCH + AppConstants.INTTWO, aircraft == null ? "" : aircraft);
        queryParams.set(AppConstants.MDATAPROP + AppConstants.INTTHREE, AppConstants.TAIL);
        queryParams.set(AppConstants.SSEARCH + AppConstants.INTTHREE, tail == null ? "" : tail);
        queryParams.set(AppConstants.MDATAPROP + AppConstants.INTFOUR, AppConstants.ESN);
        queryParams.set(AppConstants.SSEARCH + AppConstants.INTFOUR, esn == null ? "" : esn.trim());
        do {
            queryParams.set(AppConstants.IDISPLAYSTART, Integer.toString(i * displayLength));

            StringBuilder result = techpubsAppUtil.getNavigationl2(queryParams, ssoId, portalId);
            JSONObject resultJsonObj = new JSONObject(result.toString());
            JSONArray objectArray = resultJsonObj.getJSONArray(AppConstants.OBJECTS);
            for (int j = 0; j < objectArray.length(); j++) {
                JSONObject modelObj = objectArray.getJSONObject(j);
                modelSet.add(modelObj.getString(AppConstants.MODEL));
            }

            displayRecords = resultJsonObj.getInt(AppConstants.ITOTALDISPLAYRECORDS);
            i++;
        } while ((i * displayLength) < displayRecords);
        return new ArrayList<>(modelSet);
    }

    /**
     * Return a list of entitlement tokens based on SSO id, portal id and Subsystem
     *
     * @param ssoId - User Id
     * @param portalId - Portal Id (CWC/GEHONDA)
     * @param subSystem - Subsystem
     * @param modelList - Model List
     * @return List - List of entitlement tokens
     */
    protected List<String> getEntitlements(String ssoId, String portalId, SubSystem subSystem,
        List<String> modelList) {
        String currentOrg;
        try {
            currentOrg = techpubsAppUtil.getCurrentOperator(ssoId, portalId);
            log.debug("org from portal service-" + currentOrg);
        } catch (Exception e) {
            log.error("Exception occured while calling portal service : " + e);
            throw new TechnicalException(e.getMessage(), e.getCause());
        }
        return getSubSytemEntitlements(currentOrg, portalId, subSystem, modelList);
    }

    protected List<String> getSubSytemEntitlements(String currentOrg, String portalId,
        SubSystem subSystem,
        List<String> modelList) {

        return iEntitlementData
            .getEntitlements(currentOrg, portalId, subSystem.toString(), modelList);
    }

    @Override
    @LogExecutionTime
    public Map<String, Object> getArtifact(String ssoId, String portalId, String fileId)
        throws TechpubsException {

        if (TechpubsAppUtil.isNullOrEmpty(ssoId) || TechpubsAppUtil.isNullOrEmpty(portalId)) {
            log.error(AppConstants.SUBSYSTEM_ARTIFACT_LOGGER
                + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorCode() + ") - "
                + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg() + " ("
                + AppConstants.FILE_ID
                + "=" + fileId + ") " + "(" + AppConstants.SSO_ID + "=" + ssoId + ","
                + AppConstants.PORTAL_ID + "="
                + portalId + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }
        List<String> modelList = getModelListForRequest(ssoId, portalId, null, null, null, null,
            null,
            getLessorModelList());
        List<String> tokenList = getEntitlements(ssoId, portalId, getSubSystem(), modelList);
        IDocSubSystemData iDocSubSystemData = docDataRegServices
            .getSubSystemService(getSubSystem());
        DocumentItemModel documentItem = ((IDocMongoData) iDocSubSystemData)
            .getDocument(modelList, tokenList, fileId);
        if (documentItem == null) {
            log.error(AppConstants.SUBSYSTEM_ARTIFACT_LOGGER
                + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorCode() + ") - "
                + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg() + " ("
                + AppConstants.FILE_ID
                + "=" + fileId + ")" + "(" + AppConstants.SSO_ID + "=" + ssoId + ","
                + AppConstants.PORTAL_ID + "="
                + portalId + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }
        Map<String, Object> art = iResourceData.getArtifact(fileId);
        if (getSubSystem().toString().equalsIgnoreCase(AppConstants.FH)
            || getSubSystem().toString().equalsIgnoreCase(AppConstants.VI)) {
            art.put(AppConstants.DOWNLOADNAME, art.get(AppConstants.FILENAME));
        } else {
            art.put(AppConstants.DOWNLOADNAME,
                techpubsAppUtil.constructDownloadFilename((String) art.get(AppConstants.FILENAME),
                    ((IDocMongoData) iDocSubSystemData).getReleaseDate(documentItem)));
        }
        return art;

    }

    @Override
    @LogExecutionTime
    public File getDownloadCSV(String ssoId, String portalId, String model, String family,
        String docType,
        Map<String, String> queryParams) throws TechpubsException {

        List<String> modelList = getModelListForRequest(ssoId, portalId, family, model, null, null,
            null,
            getLessorModelList());
        List<String> tokenList = getEntitlements(ssoId, portalId, getSubSystem(), modelList);
        queryParams.put(AppConstants.DOC_TYPE, docType);
        IDocSubSystemData iDocSubSystemData = docDataRegServices
            .getSubSystemService(getSubSystem());
        List<DocumentItemModel> docList = ((IDocMongoData) iDocSubSystemData)
            .getDocuments(modelList, tokenList,
                queryParams);
        techpubsAppUtil.sortDocumentItems(docList, queryParams);
        HashMap<String, String> csvFieldMap = new LinkedHashMap<>();
        if (model != null) {
            csvFieldMap.put(AppConstants.MODEL, AppConstants.DOWNLOAD_CSV_MODEL);
        }
        csvFieldMap.put(AppConstants.TITLE, AppConstants.DOWNLOAD_CSV_TITLE);
        csvFieldMap.put(AppConstants.RELEASE_DATE, AppConstants.DOWNLOAD_CSV_RELEASE_DATE);

        csvFieldMap = getSubSystemDownloadCSVFieldMap(csvFieldMap);

        List<Map<String, String>> dataList = getSubSystemDownloadCSV(ssoId, portalId, modelList,
            tokenList,
            iDocSubSystemData, model, family, docList, docType, queryParams);
        File csvFile = null;
        try {
            csvFile = iCSVExportApp
                .csvExport(ssoId, AppConstants.DOCUMENT_DOWNLOAD_EXPORT, dataList,
                    new ArrayList<String>(), csvFieldMap);
        } catch (Exception e) {
            log.error(
                AppConstants.DOWNLOAD_CSV_LOGGER + TechpubsException.TechpubsAppError.INTERNAL_ERROR
                    .getErrorCode()
                    + ") - " + TechpubsException.TechpubsAppError.INTERNAL_ERROR.getErrorMsg()
                    + " ("
                    + AppConstants.MODEL + "=" + model + ")" + " (" + AppConstants.FAMILY + "="
                    + family + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR, e);
        }
        return csvFile;
    }

    @SuppressWarnings("unused")
    protected List<Map<String, String>> getSubSystemDownloadCSV(String ssoId, String portalId,
        List<String> modelList,
        List<String> tokenList, IDocSubSystemData iDocSubSystemData, String model, String family,
        List<DocumentItemModel> modelDoc, String docType, Map<String, String> queryParams) {
        return new ArrayList<>();

    }

    @Override
    @LogExecutionTime
    public DocumentDataTableModel getDownload(String ssoId, String portalId, String model,
        String family,
        String docType, Map<String, String> queryParams) throws TechpubsException {

        Map<String, String> searchFilter = new HashMap<>();
        if (model != null) {
            searchFilter.put(AppConstants.MODEL, model);
        }
        if (family != null) {
            searchFilter.put(AppConstants.FAMILY, family);
        }
        queryParams.put(AppConstants.DOC_TYPE, docType);
        List<DocumentItemModel> docItemList = getDocuments(ssoId, portalId, searchFilter,
            queryParams);
        docItemList = setSubSystemDownload(ssoId, portalId, model, docItemList, queryParams);

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

    @SuppressWarnings("unused")
    protected List<DocumentItemModel> setSubSystemDownload(String ssoId, String portalId,
        String model,
        List<DocumentItemModel> docItemList, Map<String, String> queryParams) {
        return docItemList;

    }

    @Override
    @LogExecutionTime
    public String getResource(String ssoId, String portalId, String model, String fileId,
        Map<String, String> queryParams)
        throws TechpubsException {

        if (TechpubsAppUtil.isNullOrEmpty(ssoId) || TechpubsAppUtil.isNullOrEmpty(portalId)) {
            log.error(
                AppConstants.GET_RESOURCE + " (" + TechpubsException.TechpubsAppError.NOT_AUTHORIZED
                    .getErrorCode()
                    + ") - " + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg()
                    + " ("
                    + AppConstants.FILE_ID + "=" + fileId + ")" + AppConstants.SSO_ID + "=" + ssoId
                    + ","
                    + AppConstants.PORTAL_ID + "=" + portalId + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        List<String> modelList = getModelListForRequest(ssoId, portalId, null, model, null, null,
            null,
            getLessorModelList());
        List<String> tokenList = getEntitlements(ssoId, portalId, getSubSystem(), modelList);
        IDocSubSystemData iDocSubSystemData = docDataRegServices
            .getSubSystemService(getSubSystem());
        DocumentItemModel documentItem = ((IDocMongoData) iDocSubSystemData)
            .getDocument(modelList, tokenList, fileId);

        if (documentItem == null) {
            log.error(
                AppConstants.GET_RESOURCE + " (" + TechpubsException.TechpubsAppError.NOT_AUTHORIZED
                    .getErrorCode()
                    + ") - " + TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg()
                    + " ("
                    + AppConstants.FILE_ID + "=" + fileId + ")" + AppConstants.SSO_ID + "=" + ssoId
                    + ","
                    + AppConstants.PORTAL_ID + "=" + portalId + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }
        DocumentInfoModel documentInfo = new DocumentInfoModel();
        documentInfo.setType(getSubSystem().toString());
        return setSubSystemResource(documentInfo, documentItem, queryParams);

    }

    abstract String setSubSystemResource(DocumentInfoModel documentInfo,
        DocumentItemModel documentItem,
        Map<String, String> queryParams);

    protected HashMap<String, String> getSubSystemDownloadCSVFieldMap(
        HashMap<String, String> csvFieldMap) {
        return csvFieldMap;
    }

}