package com.geaviation.techpubs.services.impl;

import com.geaviation.techpubs.data.api.IDocSubSystemData;
import com.geaviation.techpubs.models.DocumentInfoModel;
import com.geaviation.techpubs.models.DocumentItemModel;
import com.geaviation.techpubs.models.DocumentItemWSPGModel;
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
public class DocWSPGAppSvcImpl extends AbstractDocSubSystemAppImpl implements IDocSubSystemApp {

    private static final String FILE_NAME = "_Workscope_Planning_Guide_";

    @Override
    public SubSystem getSubSystem() {
        return SubSystem.WSPG;
    }

    @Override
    String setFileName() {
        return FILE_NAME;
    }

    @Override
    protected String setSubSystemResource(DocumentInfoModel documentInfo,
        DocumentItemModel documentItem,
        Map<String, String> queryParams) {
        documentInfo.setTitle(((DocumentItemWSPGModel) documentItem).getTitle());
        String resourceURI = ((DocumentItemWSPGModel) documentItem).getResourceUri();
        String contentType = ((DocumentItemWSPGModel) documentItem).getContentType();
        documentInfo.setResourceUri(
            resourceURI + AppConstants.TYPE_PARAM + getSubSystem().toString().toLowerCase());
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
        // Build the CSV data.
        for (DocumentItemModel wspgDoc : docItemList) {
            DocumentItemWSPGModel dam = (DocumentItemWSPGModel) wspgDoc;
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put(AppConstants.MODEL, model);
            dataMap.put(AppConstants.TITLE, dam.getTitle());
            dataMap.put(AppConstants.RELEASE_DATE, dam.getReleaseDate());
            dataList.add(dataMap);
        }
        return dataList;
    }
}
