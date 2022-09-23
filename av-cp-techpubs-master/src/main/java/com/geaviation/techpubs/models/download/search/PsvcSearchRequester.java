package com.geaviation.techpubs.models.download.search;

import com.geaviation.techpubs.exceptions.TechpubsException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PsvcSearchRequester {

    private static final Logger log = LogManager.getLogger(PsvcSearchRequester.class);

    public List<Map<String, Object>> requestResults(PsvcSearchRequestRestObj searchRequest, String sso, String portalId
    , String baseURL) throws TechpubsException{

        ArrayList searchResults = new ArrayList<Map<String,Object>>();
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String searchRequestString;
            searchRequestString = searchRequest.toEscapedString();
            StringEntity request = new StringEntity(searchRequestString, "application/json", "UTF-8");
            HttpPost httppost = new HttpPost(baseURL + "/document/results");
            httppost.addHeader("sm_ssoid", sso);
            httppost.addHeader("portal_id", portalId);
            httppost.setEntity(request);

            HttpResponse response = client.execute(httppost);
            if (response.getStatusLine().getStatusCode() != 200) {
                log.error("Got error response from psvc-search: " + response);
                throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR, "Unable to get search results");
            }
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                try (InputStream instream = entity.getContent()) {
                    String jsonString = IOUtils.toString(instream, StandardCharsets.UTF_8);
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONArray documentList = jsonObject.getJSONArray("documentList");
                    for (int i=0; i<documentList.length(); i++){
                        HashMap<String,Object> result =(HashMap<String, Object>) assembleHashMap(documentList, i);
                        searchResults.add(result);
                    }
                }
            }

        } catch (IOException e) {
            log.error(e);
        }


        return searchResults;
    }

    public Map<String,Object> assembleHashMap(JSONArray documentList, int i) {
        JSONObject document = documentList.getJSONObject(i);
        Iterator<?> keys = document.keys();
        HashMap<String, Object> result = new HashMap<>();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            Object value;
            if ("engineFamilies".equals(key) || "engineModels".equals(key)) {
                JSONArray jsArray = document.getJSONArray(key);
                ArrayList<String> arrayList = new ArrayList<>();

                for (int x = 0; x < jsArray.length(); x++) {
                    arrayList.add(jsArray.getString(x));
                }
                value = arrayList;
            } else {
                value = document.get(key);
            }
            result.put(key, value);
        }
        return result;
    }
}
