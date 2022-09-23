package com.geaviation.techpubs.services.impl;

import com.geaviation.techpubs.data.api.IDocSubSystemData;
import com.geaviation.techpubs.data.api.IDocVIData;
import com.geaviation.techpubs.data.util.log.LogExecutionTime;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.DocumentDataTableModel;
import com.geaviation.techpubs.models.DocumentInfoModel;
import com.geaviation.techpubs.models.DocumentItemModel;
import com.geaviation.techpubs.models.DocumentItemVIModel;
import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.services.api.IDocVIApp;
import com.geaviation.techpubs.services.util.AppConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
@Component
public class DocVIAppSvcImpl extends AbstractDocSubSystemAppImpl implements IDocVIApp {

    private static final String FILE_NAME = "_Vendor_Information_";

    @Override
    public SubSystem getSubSystem() {
        return SubSystem.VI;
    }

    @Override
    String setFileName() {
        return FILE_NAME;
    }

    @Override
    protected String setSubSystemResource(DocumentInfoModel documentInfo,
        DocumentItemModel documentItem,
        Map<String, String> queryParams) {
        documentInfo.setTitle(((DocumentItemVIModel) documentItem).getTitle());
        String resourceURI = ((DocumentItemVIModel) documentItem).getResourceUri();
        String contentType = ((DocumentItemVIModel) documentItem).getContentType();
        documentInfo.setResourceUri(
            resourceURI + AppConstants.TYPE_PARAM + getSubSystem().toString().toLowerCase());
        return iResourceData
            .prepareWrappedResource(AppConstants.SERVICES + resourceURI + AppConstants.BIN
                    + AppConstants.TYPE_PARAM + getSubSystem().toString().toLowerCase(), contentType,
                documentInfo);

    }

    @Override
    protected HashMap<String, String> getSubSystemDownloadCSVFieldMap(
        HashMap<String, String> csvFieldMap) {
        csvFieldMap.put(AppConstants.DOWNLOAD_CSV_FAMILY, AppConstants.DOWNLOAD_CSV_FAMILY_VAL);
        csvFieldMap.put(AppConstants.DOWNLOAD_CSV_DOCTYPE, AppConstants.DOWNLOAD_CSV_DOCTYPE_VAL);
        return csvFieldMap;
    }

    @Override
    protected List<Map<String, String>> getSubSystemDownloadCSV(String ssoId, String portalId,
        List<String> modelList,
        List<String> tokenList, IDocSubSystemData iDocSubSystemData, String model, String family,
        List<DocumentItemModel> docItemList, String docType, Map<String, String> queryParams) {

        List<Map<String, String>> dataList = new ArrayList<>();
        for (DocumentItemModel viDoc : docItemList) {
            DocumentItemVIModel dam = (DocumentItemVIModel) viDoc;
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put(AppConstants.TITLE, dam.getTitle());
            dataMap.put(AppConstants.RELEASE_DATE, dam.getReleaseDate());
            dataMap.put(AppConstants.FAMILY, family);
            dataMap.put(AppConstants.DOC_TYPE, dam.getDocType());
            dataList.add(dataMap);

        }
        return dataList;
    }

    @Override
    @LogExecutionTime
    public DocumentDataTableModel getDownloadVIDocTypes(String ssoId, String portalId,
        String family,
        Map<String, String> queryParams) throws TechpubsException {
        List<String> modelList = getModelListForRequest(ssoId, portalId, family, null, null, null,
            null,
            getLessorModelList());
        List<String> tokenList = getEntitlements(ssoId, portalId, getSubSystem(), modelList);
        IDocSubSystemData iDocSubSystemData = docDataRegServices
            .getSubSystemService(getSubSystem());
        List<DocumentItemModel> docItemList = ((IDocVIData) iDocSubSystemData)
            .getVIDocumentTypes(modelList, tokenList);
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

}
