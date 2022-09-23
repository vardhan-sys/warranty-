package com.geaviation.techpubs.services.impl;

import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.download.TechpubsPartDetailsExcelEntry;
import com.geaviation.techpubs.models.download.TechpubsPartDetailsExcelExporter;
import com.geaviation.techpubs.models.download.search.PsvcSearchRequestRestObj;
import com.geaviation.techpubs.models.download.search.PsvcSearchRequester;
import com.geaviation.techpubs.models.download.search.SortField;
import com.geaviation.techpubs.data.util.SecurityEscape;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;

@Component
public class TechpubsPartDetailsExcelApp {

    @Value("${PSVC.SEARCH.URL}")
    private String searchUrl;

    @Value("${techpubs.services.sqlInjection}")
    private boolean sqlInjection;

    private static final Logger log = LogManager.getLogger(TechpubsPartDetailsExcelApp.class);

    public File createPartDetailsExcelFile(JSONArray materialsParts) throws IOException {
        if (materialsParts == null || materialsParts.length() == 0) {
            return null;
        }
        // Build list of entries
        ArrayList<TechpubsPartDetailsExcelEntry> excelEntries = new ArrayList<>();
        for (int i = 0; i < materialsParts.length(); i++) {
            JSONObject part = materialsParts.getJSONObject(i);
            Object csnObj = part.get("csn");
            String csn = null;
            if (!csnObj.equals(JSONObject.NULL)) {
                csn = csnObj.toString();
            }
            TechpubsPartDetailsExcelEntry entry = new TechpubsPartDetailsExcelEntry(
                    part.getString("partNumber"), part.getString("keyword"),
                    part.getString("quantity"), csn);
            excelEntries.add(entry);
        }

        // create excel file
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        File excelFile = new File("./" + currentDateTime + "PartDetailsExport.xlsx");
        FileOutputStream fileOutputStream = new FileOutputStream(excelFile);

        // creates exporter and exports document information into File
        TechpubsPartDetailsExcelExporter exporter = new TechpubsPartDetailsExcelExporter(excelEntries);
        exporter.excelExport(fileOutputStream);

        fileOutputStream.close();

        return excelFile;
    }

    private boolean searchResultIdMatchesDocumentId(String documentId, Map<String, Object> searchResult) {
        String searchResultId = (String) searchResult.get("id");
        if (sqlInjection) {
            searchResultId = SecurityEscape.cleanString(searchResultId);
        }
        return searchResultId.equals(documentId);
    }

    public List<Map<String, Object>> getPsvcSearchResultsForFilepathUrl(String fullUrl, String ssoId, String portalId) throws TechpubsException {
        PsvcSearchRequestRestObj psvcSearchRequestRestObj = new PsvcSearchRequestRestObj();
        psvcSearchRequestRestObj.setModule("documents");
        // in Elasticsearch, we can use the full filepath of the document to find the specific doc
        // Full filepath is just the URL of this endpoint with /file instead of /part-details, so doing string replacement
        // Also removing /services from beginning (checking both \\/services and /services)
        String id = fullUrl.replace("/part-details", "/file").replace("\\/services", "")
                .replace("/services", "");
        psvcSearchRequestRestObj.setSearchText(id);
        psvcSearchRequestRestObj.setFacetQueries(new ArrayList<>());
        psvcSearchRequestRestObj.setSortField(new SortField("title", SortField.PSVC_ORDER.ASC));
        psvcSearchRequestRestObj.setStart("0");
        psvcSearchRequestRestObj.setLimit("20");
        List<Map<String, Object>> results = new PsvcSearchRequester().requestResults(psvcSearchRequestRestObj, ssoId, portalId, searchUrl);
        // Filter results - we want only the (hopefully single) result with the correct document id
        return results.stream().filter(r -> searchResultIdMatchesDocumentId(id, r)).collect(Collectors.toList());
    }
}
