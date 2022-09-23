package com.geaviation.techpubs.services.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.geaviation.techpubs.data.api.techlib.ISystemDocumentData;
import com.geaviation.techpubs.data.factories.AmazonS3ClientFactory;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.techlib.SystemDocumentEntity;
import com.geaviation.techpubs.models.techlib.dto.AvSystemExcelDownloadDTO;
import com.geaviation.techpubs.services.api.IAvSystemDocumentApp;
import com.geaviation.techpubs.services.api.ISearchAppCaller;
import com.geaviation.techpubs.services.excel.ExcelMaker;
import com.geaviation.techpubs.services.excel.exception.ExcelException;
import com.geaviation.techpubs.services.excel.model.ExcelSheet;
import com.geaviation.techpubs.services.excel.model.FileWithBytes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.*;

@Component
public class AvSystemDocumentAppImpl implements IAvSystemDocumentApp {

    private static final Logger log = LogManager.getLogger(AvSystemDocumentAppImpl.class);

    @Value("${avsystems3.admin.uploader.bucketName}")
    private String avSystemsBucketName;

    @Autowired
    private AmazonS3ClientFactory amazonS3ClientFactory;

    @Autowired
    private ISystemDocumentData iSystemDocumentData;

    @Autowired
    private UserService userService;

    @Autowired
    private SalesforceSvcImpl salesforceSvc;

    @Autowired
    ISearchAppCaller searchAppCaller;

    @Override
    public InputStreamResource getDocumentFromS3(String ssoId, String portalId,String docType, String docSite,
                                                 String docNumber, String fileName) throws TechpubsException {

        // NOTE: we have to "un-escape" the & symbol, so it will match the value in the database and in S3
        docSite = docSite.replaceAll("&amp;", "&");

        // Lookup document by type/site/number & check if filename is correct
        SystemDocumentEntity systemDocumentEntity = getSystemDocument(docType, docSite, docNumber);


        String filePath = authzS3PdfDownload(ssoId, portalId, String.valueOf(systemDocumentEntity.getId()));
        if (!systemDocumentEntity.getFileName().equals(fileName)) {
            log.error("Provided file name: " + fileName + " for doc id: " + systemDocumentEntity.getId()
                    + " does not match true file name: " + systemDocumentEntity.getFileName());
            throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }

        String icaoCode = userService.getIcaoCode(ssoId);
        // If icaoCode is GEAE, we can just return the document
        if (!icaoCode.equalsIgnoreCase("GEAE")) {
            // Only checking Tier 1/2 entitlements for now - not customer specific or paid
            boolean entitlementExists = salesforceSvc.entitlementExistsForAirframeandDocType(icaoCode, docType, systemDocumentEntity.getAirframes());
            if (!entitlementExists) {
                throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
            }
        }

        String fullFilePath = docType + "/" + docSite + "/" + docNumber + "/" + fileName;
        InputStream fileStream = getFileFromS3(fullFilePath);
        return new InputStreamResource(fileStream);
    }

    private InputStream getFileFromS3(String fullFilePath) throws TechpubsException {
        try {
            AmazonS3 amazonS3Client = amazonS3ClientFactory.getS3Client();
            S3Object s3Object = amazonS3Client.getObject(new GetObjectRequest(avSystemsBucketName, fullFilePath));
            final InputStream fileStream = s3Object.getObjectContent().getDelegateStream();
            return fileStream;
        } catch (AmazonServiceException e) {
            log.error("Error getting file from s3: " + fullFilePath, e);
            throw new TechpubsException(TechpubsException.TechpubsAppError.DOWNLOADING_FILE_FROM_S3_ERROR);
        }
    }

    private SystemDocumentEntity getSystemDocument(String docType, String docSite, String docNumber)
            throws TechpubsException {
        Optional<SystemDocumentEntity> systemDocumentEntityOptional = iSystemDocumentData
                .findByDocNumberSiteValueTypeValueAndDeleted(docNumber, docSite, docType, false);
        if (systemDocumentEntityOptional.isPresent()) {
            return systemDocumentEntityOptional.get();
        } else {
            log.error("No system document found for type: " + docType + ", site: " + docSite
                    + " and number: " + docNumber);
            throw new TechpubsException(TechpubsException.TechpubsAppError.DATA_NOT_FOUND);
        }
    }

    @Override
    public FileWithBytes getAvSystemExcelDocument(String ssoId, String portalId, String payload)
            throws TechpubsException, ExcelException {
        List<AvSystemExcelDownloadDTO> avSystemExcelDownloadDTOList = getAvSystemExcelDownloadDTOList(ssoId, portalId, payload);

        ExcelSheet excelSheet;
        try {
            excelSheet = ExcelMaker.buildExcelSheet(avSystemExcelDownloadDTOList);
            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                ExcelMaker.excelSheetToFile(excelSheet, os);
                return new FileWithBytes(os.toByteArray(), excelSheet.getFilename());
            } catch (IOException e) {
                throw new ExcelException("Could not write the excel file.", e);
            }
        } catch (ExcelException e1) {
            throw new ExcelException("Could not write the excel file.", e1);
        }

    }

    private List<AvSystemExcelDownloadDTO> getAvSystemExcelDownloadDTOList(String ssoId, String portalId, String payload) throws TechpubsException {
        String jsonString = searchAppCaller.callSearchEndpoint(ssoId, portalId, payload);
        List<AvSystemExcelDownloadDTO> avSystemExcelDownloadDTOList = new ArrayList<AvSystemExcelDownloadDTO>();
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONObject hits = jsonObject.getJSONObject("hits");
        if (null == hits || null == hits.getJSONArray("hits")) {
            return avSystemExcelDownloadDTOList;
        }
        JSONArray hitsArray = hits.getJSONArray("hits");
        for (int i = 0; i < hitsArray.length(); i++) {
            JSONObject source = hitsArray.getJSONObject(i).getJSONObject("_source");
            String documentTitle = getStringKeyIfExists(source, "document_title", "");
            String documentSite = getStringKeyIfExists(source, "site", "");
            String documentNumber = getStringKeyIfExists(source, "document_number", "");
            String documentType = getStringKeyIfExists(source, "document_type", "");
            String revisionNumber = getStringKeyIfExists(source, "revision_number", "");
            String revisionDate = getStringKeyIfExists(source, "revision_date", "");
            String publishDate = getStringKeyIfExists(source, "publish_date", "");

            StringJoiner sb = new StringJoiner(",");
            JSONArray airFramesArray = getJsonArrayKeyIfExists(source, "airframes");
            for (int j = 0; j < airFramesArray.length(); j++) {
                sb.add(airFramesArray.getString(j));
            }
            String airFrames = sb.toString();
            StringJoiner sj = new StringJoiner(",");
            JSONArray partNumbersArray = getJsonArrayKeyIfExists(source, "part_numbers");
            for (int j = 0; j < partNumbersArray.length(); j++) {
                sj.add(partNumbersArray.getString(j));
            }
            String partNumbers = sj.toString();
            AvSystemExcelDownloadDTO avSystemExcelDownloadDTO = new AvSystemExcelDownloadDTO(documentTitle, documentSite, documentType,
                    documentNumber, partNumbers, airFrames, revisionDate,
                    revisionNumber, publishDate);
            avSystemExcelDownloadDTOList.add(avSystemExcelDownloadDTO);

        }
        return avSystemExcelDownloadDTOList;
    }

    private String getStringKeyIfExists(JSONObject object, String key, String defaultValue) {
        return object.has(key) ? object.getString(key) : defaultValue;
    }

    private JSONArray getJsonArrayKeyIfExists(JSONObject object, String key) {
        return object.has(key) ? object.getJSONArray(key) : new JSONArray();
    }

    //call the search controller to authorize the S3 download
    private String authzS3PdfDownload(String ssoId, String portalId, String docId) throws TechpubsException {

        Map<String, Object> map = new HashMap<>();
        map.put("status", false);
        JSONObject payloadJson = new JSONObject();
        payloadJson.put("index", "av-cp-techpubs-avsystem");
        payloadJson.put(
                "body", new JSONObject()
                        .put("_source", new JSONObject()
                                .put("exclude", new JSONArray()
                                        .put("content")
                                )
                        )
                        .put("docvalue_fields", new JSONArray()
                                .put(new JSONObject()
                                        .put("field", "revision_date")
                                        .put("format", "date")
                                )
                        )
                        .put("query", new JSONObject()
                                .put("bool", new JSONObject()
                                        .put("must", new JSONArray())
                                        .put("filter", new JSONArray()
                                                .put(new JSONObject()
                                                        .put("term", new JSONObject()
                                                                .put("document_id.keyword", docId)
                                                        )
                                                )
                                        )
                                        .put("should", new JSONArray())
                                        .put("must_not", new JSONArray())
                                )
                        )
        );
        String jsonString = searchAppCaller.callSearchEndpoint(ssoId, portalId, payloadJson.toString());
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONObject hits = jsonObject.getJSONObject("hits");
        if (null != hits && null != hits.getJSONArray("hits")) {
            JSONArray hitsArray = hits.getJSONArray("hits");
            if (hitsArray.length() ==1 ) {
                JSONObject source = hitsArray.getJSONObject(0).getJSONObject("_source");
                if (docId.equals(source.getString("document_id")))
                    return source.getString("s3_key");
            }
        }
        throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
    }

    @Override
    public Map<String, Object> getDocumentFromS3ById(String ssoId, String portalId, String docId) throws TechpubsException {

        Map<String, Object> map = new HashMap<>();
        String filePath = authzS3PdfDownload(ssoId, portalId, docId);

        InputStream fileStream = getFileFromS3(filePath);
        map.put("inputstream", new InputStreamResource(fileStream));
        map.put("filename", Paths.get("filePath").getFileName().toString());

        return map;
    }
}

