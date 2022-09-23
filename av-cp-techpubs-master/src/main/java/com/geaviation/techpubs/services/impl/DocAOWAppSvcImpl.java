package com.geaviation.techpubs.services.impl;

import com.geaviation.techpubs.data.api.IDocSubSystemData;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.AssociatedDocumentModel;
import com.geaviation.techpubs.models.DocumentInfoModel;
import com.geaviation.techpubs.models.DocumentItemAOWModel;
import com.geaviation.techpubs.models.DocumentItemAOWRef;
import com.geaviation.techpubs.models.DocumentItemModel;
import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.services.api.IDocSubSystemApp;
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
public class DocAOWAppSvcImpl extends AbstractDocSubSystemAppImpl implements IDocSubSystemApp {

    private static final String FILE_NAME = "_All_Ops_Wires_";

    @Override
    public SubSystem getSubSystem() {
        return SubSystem.AOW;
    }

    @Override
    protected void setSubSystemAssociatedDocuments(String ssoId, String portalId, String fileId,
        Map<String, String> queryParams, List<DocumentItemModel> documentItemList,
        AssociatedDocumentModel associatedDocumentModel) throws TechpubsException {
        if (!documentItemList.isEmpty()) {
            associatedDocumentModel.setTitle(documentItemList.get(0).getTitle());
            for (DocumentItemModel doc : documentItemList) {
                doc.setResourceUri(
                    doc.getResourceUri() + AppConstants.TYPE_PARAM + getSubSystem().toString()
                        .toLowerCase());
            }
        }

    }

    @Override
    String setFileName() {
        return FILE_NAME;
    }

    @Override
    protected String setSubSystemResource(DocumentInfoModel documentInfo,
        DocumentItemModel documentItem,
        Map<String, String> queryParams) {
        documentInfo.setTitle(((DocumentItemAOWModel) documentItem).getTitle());
        String resourceURI = ((DocumentItemAOWModel) documentItem).getResourceUri();
        String contentType = ((DocumentItemAOWModel) documentItem).getContentType();
        String documentsURI = ((DocumentItemAOWModel) documentItem).getDocumentsUri();
        documentInfo.setResourceUri(
            resourceURI + AppConstants.TYPE_PARAM + getSubSystem().toString().toLowerCase());
        documentInfo.setDocumentsUri(documentsURI);
        return iResourceData
            .prepareWrappedResource(AppConstants.SERVICES + resourceURI + AppConstants.BIN
                    + AppConstants.TYPE_PARAM + getSubSystem().toString().toLowerCase(), contentType,
                documentInfo);

    }

    @Override
    protected List<Map<String, String>> getSubSystemDownloadCSV(String ssoId, String portalId,
        List<String> modelList,
        List<String> tokenList, IDocSubSystemData iDocSubSystemData, String model, String family,
        List<DocumentItemModel> docItemList, String docType, Map<String, String> queryParams) {

        List<Map<String, String>> dataList = new ArrayList<>();

        for (DocumentItemModel doc : docItemList) {
            DocumentItemAOWModel dam = (DocumentItemAOWModel) doc;
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put(AppConstants.DOWNLOAD_CSV_WIRE_NUM, dam.getWireNumber());
            dataMap.put(AppConstants.MODEL, model);
            dataMap.put(AppConstants.TITLE, dam.getTitle());
            List<DocumentItemAOWRef> refList = dam.getRefWireList();
            if (refList == null || refList.isEmpty()) {
                dataMap.put(AppConstants.DOWNLOAD_CSV_REF_WIRE, AppConstants.EMPTY_STRING);
            } else {
                List<String> refWire = new ArrayList<>();
                for (DocumentItemAOWRef ref : refList) {
                    refWire.add(ref.getWireNumber());
                }
                dataMap.put(AppConstants.DOWNLOAD_CSV_REF_WIRE, refWire.toString());
            }
            dataMap.put(AppConstants.RELEASE_DATE, dam.getReleaseDate());
            dataList.add(dataMap);
        }
        return dataList;
    }

    @Override
    protected HashMap<String, String> getSubSystemDownloadCSVFieldMap(
        HashMap<String, String> csvFieldMap) {
        csvFieldMap.put(AppConstants.DOWNLOAD_CSV_WIRE_NUM, AppConstants.DOWNLOAD_CSV_WIRE_NUM_VAL);
        csvFieldMap.put(AppConstants.DOWNLOAD_CSV_REF_WIRE, AppConstants.DOWNLOAD_CSV_REF_WIRE_VAL);
        return csvFieldMap;
    }

}
