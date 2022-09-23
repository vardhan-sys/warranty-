package com.geaviation.techpubs.services.util.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geaviation.techpubs.data.util.log.LogExecutionTime;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.cwcadmin.response.UserDetailsResponse;
import com.geaviation.techpubs.models.response.PortalAdminCompanyResponse;
import com.geaviation.techpubs.models.response.PortalUserResponse;
import com.geaviation.techpubs.services.util.AppConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Component
public class AdminAppUtil {

    @Value("${ASSET.URL}")
    private String assetUrl;

    @Value("${PORTAL.ADMIN.URL}")
    private String portalAdminUrl;

    private static final Logger log = LogManager.getLogger(AdminAppUtil.class);

    private static final String FIVESECONDSTIMEOUT = "5000";

    /**
     * This will retrieve the engine model hierarchy of the models owned by the given icao code.
     * @param sso to user who is requesting the engine model hierarchy
     * @param company the company name to grab owned engine models
     * @return StringBuilder of the JSON Response from the asset Service
     * @throws TechpubsException thrown if something went wrong with the rest service call
     */
    @LogExecutionTime
    public StringBuilder getCompanyEngineFamilyModels(String sso, String company) throws TechpubsException {
        String url = assetUrl + "/ae/engine-hierarchy/" + company;

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        return restServiceGETCall(queryParams, sso, url);
    }

    @LogExecutionTime
    public ResponseEntity<UserDetailsResponse> getPortalAdminUserDetails(List<String> ssoIds) {
        String url = portalAdminUrl + "/getUsersBySso";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, List<String>> userRequest = new HashMap<>();
        userRequest.put("ssoIds", ssoIds);
        HttpEntity<Map<String, List<String>>> entity = new HttpEntity<>(userRequest, headers);
        return restTemplate.exchange(url, HttpMethod.POST, entity, UserDetailsResponse.class);
    }

    /**
     * This will call the rest service with the given params and header information.
     * @param queryParms the query params to be passed to the rest service
     * @param headerAndUrl the header and url of the rest service request
     * @return StringBuilder of the JSON response from the rest service
     * @throws TechpubsException thrown if an Exception happened during the rest service call
     */
    @LogExecutionTime
    private StringBuilder restServiceGETCall(MultiValueMap<String, String> queryParms,
        String... headerAndUrl) throws TechpubsException {
        RestTemplate restTemplate = new RestTemplate();
        StringBuilder resultString;
        HttpHeaders headers = new HttpHeaders();

        try {
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.set("sm_ssoid", headerAndUrl[0]);
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(headerAndUrl[1]);
            if (queryParms != null && !queryParms.isEmpty()) {
                builder.queryParams(queryParms);
            }
            log.debug("URL-" + builder.toUriString());
                SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate.getRequestFactory();
            rf.setConnectTimeout(Integer.parseInt(FIVESECONDSTIMEOUT));
            resultString = new StringBuilder(restTemplate
                                                 .exchange(builder.buildAndExpand().toUri(), HttpMethod.GET, entity,
                                                     String.class).getBody());
        } catch (Exception e) {
            log.error(e);
            throw new TechpubsException(TechpubsException.TechpubsAppError.REST_SERVICE_FAILED);
        }
        return resultString;
    }


    /**
     * Parse the JSON string builder to grab the Family / Model relationships.
     * @param result the JSON we want to parse
     * @return Map of Families to Engine Models list parsed from the JSON
     */
    @LogExecutionTime
    public Map<String, List<String>> parseMdmCompanyEngineModelResponse(StringBuilder result) {
        Map<String, List<String>> familyModelRelationship = new TreeMap<>();
        JSONObject resultJsonObj = new JSONObject(result.toString());
        Iterator<?> jsonHeaderKey = resultJsonObj.keys();

        while (jsonHeaderKey.hasNext()) {
            String jsonHeader = (String) jsonHeaderKey.next();
            if (jsonHeader.equalsIgnoreCase(AppConstants.ENGINE_FAMILIES)) {

                JSONObject engineObj = resultJsonObj.getJSONObject(jsonHeader);
                Iterator<?> engineObjKeys = engineObj.keys();

                //engine obj keys = each family
                while (engineObjKeys.hasNext()) {
                    String engineFamily = (String) engineObjKeys.next();
                    Iterator<?> modelObjKeys = ((JSONObject) engineObj.get(engineFamily)).keys();

                    List<String> models = new ArrayList<>();

                    //model obj keys = each model under family
                    while (modelObjKeys.hasNext()) {
                        models.add((String) modelObjKeys.next());
                    }
                    familyModelRelationship.put(engineFamily, models);
                }
            }
        }

        return familyModelRelationship;
    }

}
