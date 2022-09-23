package com.geaviation.techpubs.services.impl.admin;

import static com.geaviation.techpubs.services.util.AppConstants.ADMIN_MANAGEMENT_TAB;
import static com.geaviation.techpubs.services.util.AppConstants.AV_SYSTEMS_PREFIX;
import static com.geaviation.techpubs.services.util.AppConstants.COMPANIES_TAB;
import static com.geaviation.techpubs.services.util.AppConstants.PUBLISHER_TAB;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geaviation.techpubs.data.impl.AwsResourcesService;
import com.geaviation.techpubs.data.model.dto.AuditLogDto;
import com.geaviation.techpubs.data.model.response.AdminManagementLog;
import com.geaviation.techpubs.data.model.response.AuditLog;
import com.geaviation.techpubs.data.model.response.AuditLogResponse;
import com.geaviation.techpubs.data.model.response.CompanyLog;
import com.geaviation.techpubs.data.model.response.PublisherLog;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.services.api.admin.IAuditApp;
import com.geaviation.techpubs.services.excel.ExcelMaker;
import com.geaviation.techpubs.services.excel.exception.ExcelException;
import com.geaviation.techpubs.services.excel.model.ExcelSheet;
import com.geaviation.techpubs.services.excel.model.FileWithBytes;

@Component
public class AuditAppImpl implements IAuditApp {

    @Autowired
    private AwsResourcesService awsResourcesService;

    private static final Logger log = LogManager.getLogger(AuditAppImpl.class);
    
    public AuditLogResponse getAuditLogs(AuditLogDto auditLogDto) throws TechpubsException {
    	
        JSONObject body = createJsonBody(auditLogDto);

        String response = awsResourcesService.getAuditLogs(body);

        if (response == null) {
            log.error("Received a null response from API Gateway in getAuditLogs()");
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
        }

        QueryResult queryResult = getQueryResult(response);
        int count = queryResult.getCount();
        List<Map<String, AttributeValue>> items = queryResult.getItems();
        Map<String, String> lastEvaluatedKey = formatLastEvaluatedKey(queryResult.getLastEvaluatedKey());

        String category = auditLogDto.getCategory();

        List<? extends AuditLog> logs = null;
        if (ADMIN_MANAGEMENT_TAB.equals(category) || (AV_SYSTEMS_PREFIX + ADMIN_MANAGEMENT_TAB).equals(category)) {
            logs = getAdminManagementLogs(items);
        } else if (COMPANIES_TAB.equals(category) || (AV_SYSTEMS_PREFIX + COMPANIES_TAB).equals(category)) {
            logs = getCompanyLogs(items);
        } else if (PUBLISHER_TAB.equals(category)) {
            logs = getPublisherLogs(items);
        }

        return new AuditLogResponse(count, lastEvaluatedKey, logs);
    }

    private JSONObject createJsonBody(AuditLogDto auditLogDto) {
        JSONObject body = new JSONObject();
        body.put("fromDate", auditLogDto.getFromDate());
        body.put("toDate", auditLogDto.getToDate());

        String category = auditLogDto.getCategory();
        body.put("category", category);

        if (auditLogDto.getCategorySearchTerm() != null) {
            if (ADMIN_MANAGEMENT_TAB.equals(category) || (AV_SYSTEMS_PREFIX + ADMIN_MANAGEMENT_TAB).equals(category)) {
                body.put("categorySearchColumn", "user");
            } else if (COMPANIES_TAB.equals(category) || (AV_SYSTEMS_PREFIX + COMPANIES_TAB).equals(category)) {
                body.put("categorySearchColumn", "company");
            } else if (PUBLISHER_TAB.equals(category)) {
                body.put("categorySearchColumn", "bookcaseKey");
            }

            body.put("categorySearchTerm", auditLogDto.getCategorySearchTerm());
        }

        if (auditLogDto.getAction() != null) {
            body.put("action", auditLogDto.getAction());
        }

        if (auditLogDto.getSsoSearchTerm() != null) {
            body.put("ssoSearchTerm", auditLogDto.getSsoSearchTerm());
        }

        // Only add if paging
        if (auditLogDto.getLastEvaluatedKey() != null) {
            body.put("lastEvaluatedKey", auditLogDto.getLastEvaluatedKey());
        }

        return body;
    }

    private Map<String, String> formatLastEvaluatedKey(Map<String, AttributeValue> lastEvaluatedKeyAttributes) {
        Map<String, String> lastEvaluatedKey = null;
        if (lastEvaluatedKeyAttributes != null) {
            lastEvaluatedKey = new HashMap<>();
            for (Map.Entry<String, AttributeValue> entry : lastEvaluatedKeyAttributes.entrySet()) {
                lastEvaluatedKey.put(entry.getKey(), entry.getValue().getS());
            }
        }

        return lastEvaluatedKey;
    }

    public FileWithBytes downloadAuditLogs(AuditLogDto auditLogDto) throws ExcelException, TechpubsException {
        JSONObject body = createJsonBody(auditLogDto);
        String response = awsResourcesService.getAuditLogs(body);

        if (response == null) {
            log.error("Received a null response from API Gateway in downloadAuditLogs()");
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
        }

        QueryResult queryResult = getQueryResult(response);
        List<Map<String, AttributeValue>> items = queryResult.getItems();
        Map<String, String> lastEvaluatedKey = formatLastEvaluatedKey(queryResult.getLastEvaluatedKey());

        String fromDate = auditLogDto.getFromDate();
        String toDate = auditLogDto.getToDate();
        String category = auditLogDto.getCategory();
        String categorySearchTerm = auditLogDto.getCategorySearchTerm();
        String action = auditLogDto.getAction();
        String ssoSearchTerm = auditLogDto.getSsoSearchTerm();

        while (lastEvaluatedKey != null) {
            AuditLogDto newRequest = new AuditLogDto(fromDate, toDate, category,categorySearchTerm, action,
                    ssoSearchTerm, lastEvaluatedKey);
            JSONObject newBody = createJsonBody(newRequest);
            String nextResponse = awsResourcesService.getAuditLogs(newBody);

            queryResult = getQueryResult(nextResponse);
            items.addAll(queryResult.getItems());
            lastEvaluatedKey = formatLastEvaluatedKey(queryResult.getLastEvaluatedKey());
        }

        List<? extends AuditLog> logs = new ArrayList<>();
        if (ADMIN_MANAGEMENT_TAB.equals(category) || (AV_SYSTEMS_PREFIX + ADMIN_MANAGEMENT_TAB).equals(category)) {
            logs = getAdminManagementLogs(items);
        } else if (COMPANIES_TAB.equals(category) || (AV_SYSTEMS_PREFIX + COMPANIES_TAB).equals(category)) {
            logs = getCompanyLogs(items);
        } else if (PUBLISHER_TAB.equals(category)) {
            logs = getPublisherLogs(items);
        }

        return !logs.isEmpty() ? createExcelFile(logs) : null;
    }

    private FileWithBytes createExcelFile(List<?> logs)  throws ExcelException {
        ExcelSheet excelSheet = ExcelMaker.buildExcelSheet(logs);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ExcelMaker.excelSheetToFile(excelSheet, os);
            return new FileWithBytes(os.toByteArray(), excelSheet.getFilename());
        } catch (IOException e) {
            throw new ExcelException("Could not write the excel file.", e);
        }
    }

        ObjectMapper mapper = new ObjectMapper();
        private QueryResult getQueryResult(String response) {
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

        QueryResult queryResult = null;

        try {
            queryResult = mapper.readValue(response, QueryResult.class);
        } catch (IOException e) {
            log.error("Error serializing query result. " + e);
        }

        return queryResult;
    }

    private List<AdminManagementLog> getAdminManagementLogs(List<Map<String, AttributeValue>> items) {
        List<AdminManagementLog> logs = new ArrayList<>();
        for (Map<String, AttributeValue> item : items) {
            AdminManagementLog adminManagementLog = new AdminManagementLog();
            for (Map.Entry<String, AttributeValue> entry : item.entrySet()) {
                try {
                    Field field = AdminManagementLog.class.getDeclaredField(entry.getKey());
                    field.setAccessible(true);
                    field.set(adminManagementLog, entry.getValue().getS());
                    field.setAccessible(false);
                } catch (Exception e) {
                    log.error("Cannot set field " + entry.getKey() + " on AdminManagemenLog class.");
                }
            }
            logs.add(adminManagementLog);
        }
        return logs;
    }

    private List<CompanyLog> getCompanyLogs(List<Map<String, AttributeValue>> items) {
        List<CompanyLog> logs = new ArrayList<>();
        for (Map<String, AttributeValue> item : items) {
            CompanyLog companyLog = new CompanyLog();
            for (Map.Entry<String, AttributeValue> entry : item.entrySet()) {
                try {
                    Field field = CompanyLog.class.getDeclaredField(entry.getKey());
                    field.setAccessible(true);
                    field.set(companyLog, entry.getValue().getS());
                    field.setAccessible(false);
                } catch (Exception e) {
                    log.error("Cannot set field " + entry.getKey() + " on CompanyLog class");
                }
            }
            logs.add(companyLog);
        }
        return logs;
    }

    private List<PublisherLog> getPublisherLogs(List<Map<String, AttributeValue>> items) {
        List<PublisherLog> logs = new ArrayList<>();
        for (Map<String, AttributeValue> item : items) {
            PublisherLog publisherLog = new PublisherLog();
            for (Map.Entry<String, AttributeValue> entry : item.entrySet()) {
                try {
                    Field field = PublisherLog.class.getDeclaredField(entry.getKey());
                    field.setAccessible(true);
                    field.set(publisherLog, entry.getValue().getS());
                    field.setAccessible(false);
                } catch (Exception e) {
                    log.error("Cannot set field " + entry.getKey() + " on PublisherLog class");
                }
            }
            logs.add(publisherLog);
        }
        return logs;
    }
}
