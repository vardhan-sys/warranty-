package com.geaviation.techpubs.services.impl;

import com.geaviation.techpubs.data.api.IDocMongoData;
import com.geaviation.techpubs.data.api.IDocSubSystemData;
import com.geaviation.techpubs.data.util.log.LogExecutionTime;
import com.geaviation.techpubs.exceptions.PdfPrintException;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.AssociatedDocumentModel;
import com.geaviation.techpubs.models.DocumentInfoModel;
import com.geaviation.techpubs.models.DocumentItemAssociatedCMMModel;
import com.geaviation.techpubs.models.DocumentItemCMMModel;
import com.geaviation.techpubs.models.DocumentItemModel;
import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.services.api.IDocCMMApp;
import com.geaviation.techpubs.services.util.AppConstants;
import com.geaviation.techpubs.services.util.PDFPrintApp;
import com.geaviation.techpubs.services.util.PDFConverter;
import com.geaviation.techpubs.services.util.TechpubsAppUtil;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@ConfigurationProperties
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
@Component
public class DocCMMAppSvcImpl extends AbstractDocSubSystemAppImpl implements IDocCMMApp {

    private static final String FILE_NAME = "_CMM_";
    private static final Logger log = LogManager.getLogger(DocCMMAppSvcImpl.class);

    @Value("${techpubs.services.US538636}")
    private boolean US538636;

    @Autowired
    private PDFPrintApp pdfConverter;

    @Autowired
    private PDFConverter newPdfConverter;

    @Override
    public SubSystem getSubSystem() {
        return SubSystem.CMM;
    }

    @Override
    protected void setSubSystemAssociatedDocuments(String ssoId, String portalId, String fileId,
        Map<String, String> queryParams, List<DocumentItemModel> documentItemList,
        AssociatedDocumentModel associatedDocumentModel) throws TechpubsException {
        if (!documentItemList.isEmpty()) {
            associatedDocumentModel
                .setTitle(((DocumentItemCMMModel) documentItemList.get(0)).getPublication() + " "
                    + documentItemList.get(0).getTitle());
            for (DocumentItemModel doc : documentItemList) {
                if (!doc.getResourceUri().contains(AppConstants.CMMS)) {
                    doc.setResourceUri(
                        doc.getResourceUri() + AppConstants.TYPE_PARAM + getSubSystem().toString()
                            .toLowerCase());
                }
            }
        }
    }

    @Override
    protected List<Map<String, String>> getSubSystemDownloadCSV(String ssoId, String portalId,
        List<String> modelList,
        List<String> tokenList, IDocSubSystemData iDocSubSystemData, String model, String family,
        List<DocumentItemModel> docItemList, String docType, Map<String, String> queryParams) {

        List<Map<String, String>> dataList = new ArrayList<>();
        for (DocumentItemModel modelDoc : docItemList) {
            List<DocumentItemModel> assocDocs = ((IDocMongoData) iDocSubSystemData)
                .getAssociatedDocuments(modelList,
                    tokenList, modelDoc.getId(), false);
            for (DocumentItemModel doc : assocDocs) {
                DocumentItemCMMModel dam = (DocumentItemCMMModel) doc;
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put(AppConstants.TITLE, dam.getTitle());
                dataMap.put(AppConstants.RELEASE_DATE, dam.getReleaseDate());
                dataMap.put(AppConstants.MODEL, model);
                dataList.add(dataMap);
            }
        }
        return dataList;
    }

    protected String setFileName() {
        return FILE_NAME;

    }

    @Override
    protected boolean getIncludeParts() {
        return true;
    }

    @Override
    protected void getSubSytemDownloadResource(String ssoId, String portalId, ZipOutputStream zos,
        List<String> modelList, List<String> tokenList, IDocSubSystemData iDocSubSystemData,
        String fileId)
        throws TechpubsException, IOException {
        DocumentItemModel cmmDoc = ((IDocMongoData) iDocSubSystemData)
            .getDocument(modelList, tokenList, fileId);
        List<DocumentItemModel> assocDocs = ((IDocMongoData) iDocSubSystemData)
            .getAssociatedDocuments(modelList,
                tokenList, cmmDoc.getId(), false);
        for (DocumentItemModel doc : assocDocs) {
            Map<String, Object> artMap = getArtifact(ssoId, portalId, doc.getId());
            zos.putNextEntry(new ZipEntry((String) artMap.get(AppConstants.DOWNLOADNAME)));
            zos.write((byte[]) artMap.get(AppConstants.CONTENT));
            zos.closeEntry();
        }
    }

    @Override
    protected String setSubSystemResource(DocumentInfoModel documentInfo,
        DocumentItemModel documentItem,
        Map<String, String> queryParams) {
        documentInfo.setTitle(((DocumentItemAssociatedCMMModel) documentItem).getTitle());
        String resourceURI = ((DocumentItemAssociatedCMMModel) documentItem).getResourceUri();
        String contentType = ((DocumentItemAssociatedCMMModel) documentItem).getFileType();
        String documentsURI = ((DocumentItemAssociatedCMMModel) documentItem).getDocumentUri();
        documentInfo.setResourceUri(
            resourceURI + AppConstants.TYPE_PARAM + getSubSystem().toString().toLowerCase());
        documentInfo.setDocumentsUri(documentsURI);
        return iResourceData
            .prepareWrappedResource(AppConstants.SERVICES + resourceURI + AppConstants.PDF
                    + AppConstants.TYPE_PARAM + getSubSystem().toString().toLowerCase(), contentType,
                documentInfo);

    }

    @Override
    @LogExecutionTime
    public String getCMMParts(String ssoId, String portalId, String fileId)
        throws TechpubsException {

        // Ensure user (ssoid) has access to CMM by retrieving CMM publication
        // based on fileId
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
        IDocSubSystemData iDocSubSystemData = docDataRegServices
            .getSubSystemService(getSubSystem());
        List<String> modelList = getModelListForRequest(ssoId, portalId, null, null, null, null,
            null,
            getLessorModelList());
        List<String> tokenList = getEntitlements(ssoId, portalId, getSubSystem(), modelList);
        DocumentItemModel cmmDoc = ((IDocMongoData) iDocSubSystemData)
            .getDocument(modelList, tokenList, fileId);
        return iResourceData.getCMMParts(((DocumentItemAssociatedCMMModel) cmmDoc).getPublication(),
            ((DocumentItemAssociatedCMMModel) cmmDoc).getId(),
            ((DocumentItemAssociatedCMMModel) cmmDoc).getParts());

    }

    @Override
    @LogExecutionTime
    public byte[] getCMMPartsPDF(String baseURI, String ssoId, String portalId, String fileId)
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

        IDocSubSystemData iDocSubSystemData = docDataRegServices
            .getSubSystemService(getSubSystem());
        List<String> modelList = getModelListForRequest(ssoId, portalId, null, null, null, null,
            null,
            getLessorModelList());
        List<String> tokenList = getEntitlements(ssoId, portalId, getSubSystem(), modelList);
        DocumentItemModel documentItem = ((IDocMongoData) iDocSubSystemData)
            .getDocument(modelList, tokenList, fileId);

        byte[] pdfByte = null;

        // Call Print service
        String inputHTML = baseURI + "techdocs/cmms/" + fileId + "/parts";
        String header = "Part Number List for : " + (((DocumentItemAssociatedCMMModel) documentItem)
            .getPublication())
            + "| | | | | | | | |";
        DateFormat dateFormat = new SimpleDateFormat(AppConstants.DATE_FORMAT);
        StringBuilder sbProprietary = new StringBuilder(AppConstants.GE_PROPRIETARY);
        sbProprietary.append(AppConstants.DISCLOSURE);
        sbProprietary.append(AppConstants.EXPRESS);
        sbProprietary.append(AppConstants.TECHNICAL_DATA);
        sbProprietary.append(AppConstants.TRANSFER);
        sbProprietary.append(AppConstants.AUTHORIZATION);
        String footer =
            "Date Printed: " + dateFormat.format(new Date()) + "|" + sbProprietary.toString()
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
            log.error(e);
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
        }

        return pdfByte;
    }

}
