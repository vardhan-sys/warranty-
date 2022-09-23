package com.geaviation.techpubs.services.util;

import com.geaviation.techpubs.data.util.log.LogExecutionTime;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.*;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MultivaluedMap;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

@Component
public class TechpubsAppUtil {

    @Value("${PORTAL.URL}")
    private String portalBaseUrl;

    @Value("${NAVIGATION.URL}")
    private String navigationUrl;

    private static final Logger log = LogManager.getLogger(TechpubsAppUtil.class);
    private static final String EMPTY_STRING = "";
    private static final String FIVESECONDSTIMEOUT = "5000";


    /**
     * Returns File from MultipartFile object
     *
     * @param multipartFile - MultipartFile
     * @return java.io.File
     */
    public static java.io.File convertMultiPartFileToFile(MultipartFile multipartFile) throws TechpubsException {
        final java.io.File file = new File(multipartFile.getOriginalFilename());
        try (final FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(multipartFile.getBytes());
        } catch (final IOException ex) {
            throw new TechpubsException(TechpubsException.TechpubsAppError.CONVERT_MULTIPART_TO_FILE_ERROR);
        }
        return file;
    }

    /**
     * Returns true if the given string is not null and not empty
     *
     * @param strData - String data
     * @return boolean - returns true or false
     */
    public static boolean isNotNullandEmpty(final String strData) {
        boolean isValid = false;
        if (strData != null && !EMPTY_STRING.equals(strData.trim())) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * Returns true if the given string is not null and not empty
     *
     * @param strData - String data
     * @return boolean - returns true or false
     */
    public static boolean isNullOrEmpty(final String strData) {
        boolean isValid = false;
        if (null == strData || strData.trim().equals(EMPTY_STRING)) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * This method will check whether the passed collection is not null and not empty
     *
     * @param list - collection
     * @return boolean - returns true or false
     */
    public static boolean isCollectionNotEmpty(final Collection<? extends Object> list) {
        return list != null && !list.isEmpty();
    }

    /**
     * Return Subsystem based on document type
     *
     * @param type - Document Type
     * @return Subsystem - SubSystem (TD,CMM,FH,TP)
     */
    public static SubSystem getSubSystem(String type) {
        SubSystem subSystem;

        if ("ic".equalsIgnoreCase(type) || "tr".equalsIgnoreCase(type) || "sb"
            .equalsIgnoreCase(type)) {
            subSystem = SubSystem.TD;
        } else {
            try {
                subSystem = SubSystem.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException | NullPointerException e) {
                log.error("Exception in getSubSystem : " + e);
                subSystem = SubSystem.INVALID;
            }
        }
        return subSystem;
    }

    public String getCurrentOperator(String sso, String portalId) throws TechpubsException {

        String url = portalBaseUrl + "user/currentorg/{sso}";
        Map<String, String> uriParams = new HashMap<>();
        uriParams.put("sso", sso);
        return restServiceGETCall(uriParams, null, sso, portalId, url).toString();
    }

    public String getCurrentIcaoCode(String sso, String portalId) throws TechpubsException {

        String url = portalBaseUrl + "user/orgid/{sso}";
        Map<String, String> uriParams = new HashMap<>();
        uriParams.put("sso", sso);
        return restServiceGETCall(uriParams, null, sso, portalId, url).toString();
    }

    @LogExecutionTime
    public List<Property> getCompanyAttributesForSSO(String sso, String portalId,
        String strCompanyAtrbtList)
        throws TechpubsException {

        String url = portalBaseUrl + "/org/propsso/{sso}/{prop}";
        Map<String, Object> uriParams = new HashMap<>();
        uriParams.put("sso", sso);
        uriParams.put("prop", strCompanyAtrbtList);
        StringBuilder attributeBuilder = restServiceGETCall(uriParams, null, sso, portalId, url);

        JSONObject resultJsonObj = new JSONObject(attributeBuilder.toString());

        JSONArray propertyJsonArray = resultJsonObj.getJSONArray("property");

        List<Property> attributeList = new ArrayList<>();

        if (propertyJsonArray != null && propertyJsonArray.length() > 0) {
            JSONObject propertyJsonObj = new JSONObject(propertyJsonArray.get(0).toString());

            String propValue = propertyJsonObj.getString("propValue");
            String propName = propertyJsonObj.getString("propName");

            attributeList.add(new Property(propName, propValue));
        }

        return attributeList;
    }


    public StringBuilder getNavigationl1(String sso, String portalId) throws TechpubsException {

        String url = navigationUrl + "/l1";
        return restServiceGETCall(null, null, sso, portalId, url);

    }

    public StringBuilder getNavigationl2(MultiValueMap<String, String> queryParams, String sso,
        String portalId)
        throws TechpubsException {

        String url = navigationUrl + "/l2";
        return restServiceGETCall(null, queryParams, sso, portalId, url);

    }

    @LogExecutionTime
    public StringBuilder restServiceGETCall(Map<String, ?> uriParams,
        MultiValueMap<String, String> queryParms,
        String... headerAndUrl) throws TechpubsException {
        RestTemplate restTemplate = new RestTemplate();
        StringBuilder resultString;
        HttpHeaders headers = new HttpHeaders();
        try {
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.set("sm_ssoid", headerAndUrl[0]);
            headers.set("portal_id", headerAndUrl[1]);
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(headerAndUrl[2]);
            if (queryParms != null && !queryParms.isEmpty()) {
                builder.queryParams(queryParms);
            }
            log.debug("URL-" + builder.toUriString());
            SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate
                .getRequestFactory();
            rf.setConnectTimeout(Integer.parseInt(FIVESECONDSTIMEOUT));
            if (uriParams == null || uriParams.isEmpty()) {
                resultString = new StringBuilder(restTemplate
                    .exchange(builder.buildAndExpand().toUri(), HttpMethod.GET, entity,
                        String.class).getBody());
            } else {
                resultString = new StringBuilder(restTemplate
                    .exchange(builder.buildAndExpand(uriParams).toUri(), HttpMethod.GET, entity,
                        String.class)
                    .getBody());
            }
        } catch (Exception e) {
            log.error(e);
            throw new TechpubsException(TechpubsException.TechpubsAppError.REST_SERVICE_FAILED);
        }
        return resultString;
    }


    @LogExecutionTime
    public List<Property> getProperty(String sso, String portalId, String strCompanyAtrbtList)
        throws TechpubsException {
        Org org = null;
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.set("sm_ssoid", sso);
            headers.set("portal_id", portalId);
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
            String url = portalBaseUrl + "org/propsso/{sso}/{prop}";
            Map<String, String> uriParams = new HashMap<>();
            uriParams.put("sso", sso);
            uriParams.put("prop", strCompanyAtrbtList);
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
            SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate
                .getRequestFactory();
            rf.setConnectTimeout(Integer.parseInt(FIVESECONDSTIMEOUT));
            org = restTemplate
                .exchange(builder.buildAndExpand(uriParams).toUri(), HttpMethod.GET, entity,
                    Org.class).getBody();

        } catch (Exception e) {
            log.error("Error in getProperty()" + e);
            throw new TechpubsException(TechpubsException.TechpubsAppError.REST_SERVICE_FAILED);
        }

        return org.getProperties();
    }


    public static Map<String, String> getFilterFields(Map<String, String> queryParams) {
        Map<String, String> filterMap = new HashMap<>();
        int totalColumns = getQueryParamIntFromForm(queryParams, "iColumns");
        for (int i = 0; i < totalColumns; i++) {
            String dataProp = queryParams.get(AppConstants.MDATAPROP + i);
            String sSearch = queryParams.get(AppConstants.SSEARCH + i);
            String sSearchEnd = queryParams.get(AppConstants.SSEARCHEND + i);

            if (sSearch != null && dataProp != null) {
                filterMap.put(dataProp, sSearch);
            }
            if (sSearchEnd != null && dataProp != null) {
                filterMap.put(dataProp + "End", sSearchEnd);
            }
        }

        return filterMap;
    }

    public static int getQueryParamIntFromForm(Map<String, String> queryParams, String param) {
        int defaultInt = 0;
        try {
            defaultInt = Integer.parseInt(queryParams.get(param));
        } catch (NumberFormatException e) {
            log.error(e);
        }
        return defaultInt;
    }

    public Map<String, String> getQueryParams(HttpServletRequest request) {
        Map<String, String> queryParams = new HashMap<>();
        Map<String, String[]> queryParams1 = request.getParameterMap();

        for (Entry<String, String[]> entry : queryParams1.entrySet()) {
            queryParams.put(entry.getKey(), entry.getValue()[0]);
        }
        return queryParams;
    }

    public String constructDownloadFilename(String origFilename, String relDate) {
        String newName;
        int pos = origFilename.lastIndexOf('.');
        if (pos < 0) {
            newName = origFilename + "_" + relDate;
        } else {
            newName = origFilename.substring(0, pos) + "_" + relDate + origFilename.substring(pos);
        }
        return newName;
    }

    public static String getFormattedTimestamp(String fmt) {
        return new SimpleDateFormat(fmt).format(new Date());
    }

    public static Map<String, String> convertMultiToRegularMap(MultivaluedMap<String, String> m) {
        Map<String, String> map = new HashMap<>();
        if (m == null) {
            return map;
        }
        for (Entry<String, List<String>> entry : m.entrySet()) {
            StringBuilder sb = new StringBuilder();
            for (String s : entry.getValue()) {
                if (sb.length() > 0) {
                    sb.append(',');
                }
                sb.append(s);
            }
            map.put(entry.getKey(), sb.toString());
        }
        return map;
    }

    public Map<String, Object> convertObjectToMap(Object object) {
        Map<String, Object> result = new HashMap<>();
        if (object instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) (object);
            for (Entry<?, ?> entry : map.entrySet()) {
                String key = entry.getKey().toString();
                Object value = entry.getValue();
                result.put(key, value);
            }
        }
        return result;
    }

    /**
     * Validate getDocuments query parameters
     *
     * @param queryParams - Map of HTTP query parameters
     */
    public static void validateDatatableParameters(Map<String, String> queryParams)
        throws TechpubsException {
        if (checkParam(queryParams, AppConstants.IDISPLAYLENGTH, 1)) {
            log.error(AppConstants.VALIDATE_DATATABLE
                + TechpubsException.TechpubsAppError.INVALID_PARAMETER.getErrorCode() + ") - "
                + TechpubsException.TechpubsAppError.INVALID_PARAMETER.getErrorMsg() + " ("
                + AppConstants.IDISPLAYLENGTH + "=" + queryParams.get(AppConstants.IDISPLAYLENGTH)
                + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }

        if (checkParam(queryParams, AppConstants.IDISPLAYSTART, 0)) {
            log.error(AppConstants.VALIDATE_DATATABLE
                + TechpubsException.TechpubsAppError.INVALID_PARAMETER.getErrorCode() + ") - "
                + TechpubsException.TechpubsAppError.INVALID_PARAMETER.getErrorMsg() + " ("
                + AppConstants.IDISPLAYSTART + "=" + queryParams.get(AppConstants.IDISPLAYSTART)
                + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }


        if (checkParam(queryParams, AppConstants.ICOLUMNS, 1)) {
            log.error(AppConstants.VALIDATE_DATATABLE
                + TechpubsException.TechpubsAppError.INVALID_PARAMETER.getErrorCode() + ") - "
                + TechpubsException.TechpubsAppError.INVALID_PARAMETER.getErrorMsg() + " ("
                + AppConstants.ICOLUMNS
                + "=" + queryParams.get(AppConstants.ICOLUMNS) + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }
        if (TechpubsAppUtil.isNullOrEmpty(queryParams.get(AppConstants.SECHO))) {
            log.error(AppConstants.VALIDATE_DATATABLE
                + TechpubsException.TechpubsAppError.INVALID_PARAMETER.getErrorCode() + ") - "
                + TechpubsException.TechpubsAppError.INVALID_PARAMETER.getErrorMsg() + " ("
                + AppConstants.SECHO
                + "=" + queryParams.get(AppConstants.SECHO) + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }

    }

    /**
     * Check param.
     *
     * @param queryParams the query params
     * @param strParam the str param
     * @param intParam the int param
     * @return boolean returns true or false
     */
    private static boolean checkParam(Map<String, String> queryParams, String strParam, int intParam) {
        return queryParams.get(strParam) == null || !StringUtils
            .isInteger(queryParams.get(strParam))
            || TechpubsAppUtil.getQueryParamIntFromForm(queryParams, strParam) < intParam;
    }

    public List<String> getModelList(String ssoId, String portalId, String family, String model,
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

            StringBuilder result = getNavigationl2(queryParams, ssoId, portalId);
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

    public void sortDocumentItems(List<DocumentItemModel> docItemList,
        Map<String, String> queryParams) {
        sortDocumentItems(docItemList, getSortParams(queryParams));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void sortDocumentItems(List<DocumentItemModel> docItemList,
        List<String[]> sortParamList) {
        if (!TechpubsAppUtil.isCollectionNotEmpty(docItemList)) {
            return;
        }

        Class<?> clazz = docItemList.get(0).getClass();
        ComparatorChain comparatorChain = new ComparatorChain();

        for (String[] sortParams : sortParamList) {
            comparatorChain.addComparator(new DynamicComparator(clazz, sortParams[0],
                (AppConstants.ASC.equals(sortParams[1]) ? true : false)));
        }
        if (comparatorChain.size() > 0) {
            Collections.sort(docItemList, comparatorChain);
        }
    }

    public static void sortBooks(List<BookcaseContentDAO> bookcaseContentDAOS,
                                Map<String, String> queryParams) {
        sortBooks(bookcaseContentDAOS, getSortParams(queryParams));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void sortBooks(List<BookcaseContentDAO> bookcaseContentDAOS,
                                List<String[]> sortParamList) {
        if (!TechpubsAppUtil.isCollectionNotEmpty(bookcaseContentDAOS)) {
            return;
        }

        Class<?> clazz = bookcaseContentDAOS.get(0).getClass().getSuperclass();
        ComparatorChain comparatorChain = new ComparatorChain();

        for (String[] sortParams : sortParamList) {
            comparatorChain.addComparator(new DynamicComparator(clazz, sortParams[0],
                    (AppConstants.ASC.equals(sortParams[1]) ? true : false)));
        }
        if (comparatorChain.size() > 0) {
            Collections.sort(bookcaseContentDAOS, comparatorChain);
        }
    }

    public void sortManualItems(List<ManualItemModel> manualItemList,
        Map<String, String> queryParams) {
        sortManualItems(manualItemList, getSortParams(queryParams));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void sortManualItems(List<ManualItemModel> manualItemList,
        List<String[]> sortParamList) {
        if (!TechpubsAppUtil.isCollectionNotEmpty(manualItemList)) {
            return;
        }

        Class<?> clazz = manualItemList.get(0).getClass();
        ComparatorChain comparatorChain = new ComparatorChain();

        for (String[] sortParams : sortParamList) {
            comparatorChain.addComparator(new DynamicComparator(clazz, sortParams[0],
                (AppConstants.ASC.equals(sortParams[1]) ? true : false)));
        }
        if (comparatorChain.size() > 0) {
            Collections.sort(manualItemList, comparatorChain);
        }
    }

    private static List<String[]> getSortParams(Map<String, String> queryParams) {
        List<String[]> sortParamsList = new ArrayList<>();

        for (int i = 0;
            i < TechpubsAppUtil.getQueryParamIntFromForm(queryParams, AppConstants.ICOLUMNS); i++) {
            if (queryParams.get(AppConstants.ISORT_COL + i) != null) {
                String sortColumn = queryParams
                    .get(AppConstants.MDATAPROP + queryParams.get(AppConstants.ISORT_COL + i));
                if (sortColumn != null) {
                    sortParamsList.add(new String[]{sortColumn,
                        (AppConstants.DESC
                            .equalsIgnoreCase(queryParams.get(AppConstants.SSORTDIR + i))
                            ? AppConstants.DESC : AppConstants.ASC)});
                }
            }
        }

        return sortParamsList;
    }

    public void sortDvdItems(List<DVDInfoResponse> finalDvdList, Map<String, String> queryParams) {
        sortDvdItems(finalDvdList, getSortParams(queryParams));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void sortDvdItems(List<DVDInfoResponse> finalDvdList, List<String[]> sortParamList) {
        if (!TechpubsAppUtil.isCollectionNotEmpty(finalDvdList)) {
            return;
        }

        Class<?> clazz = finalDvdList.get(0).getClass();
        ComparatorChain comparatorChain = new ComparatorChain();

        for (String[] sortParams : sortParamList) {
            comparatorChain.addComparator(new DynamicComparator(clazz, sortParams[0],
                (AppConstants.ASC.equals(sortParams[1]) ? true : false)));
        }
        if (comparatorChain.size() > 0) {
            Collections.sort(finalDvdList, comparatorChain);
        }
    }

    public CacheControl getCacheControl() {
        CacheControl cc = new CacheControl();
        cc.setMaxAge(3600);
        cc.setNoTransform(false);
        return cc;
    }
}
