package com.geaviation.techpubs.service.impl.admin;

import com.geaviation.techpubs.data.impl.AwsResourcesService;
import com.geaviation.techpubs.data.model.dto.AuditLogDto;
import com.geaviation.techpubs.data.model.response.AuditLogResponse;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.services.excel.exception.ExcelException;
import com.geaviation.techpubs.services.excel.model.FileWithBytes;
import com.geaviation.techpubs.services.impl.admin.AuditAppImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AuditAppImplTest {

    @Mock
    AwsResourcesService mockAwsResourcesService;

    @InjectMocks
    AuditAppImpl auditAppImpl;

    private static final String FROM_DATE = "FROM_DATE";
    private static final String TO_DATE = "TO_DATE";
    private static final String COMPANY_CATEGORY = "companies";
    private static final String ADMIN_MANAGEMENT_CATEGORY = "admin-management";

    private static final String APIGW_RESPONSES = "src/test/resources/responses/apigw/";

    private AuditLogDto auditLogDto;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        auditLogDto = new AuditLogDto();
        auditLogDto.setFromDate(FROM_DATE);
        auditLogDto.setToDate(TO_DATE);
    }

    @Test
    public void getCompanyAuditLogsGivenDefaultAuditLogDtoReturnAuditLogResponse() throws TechpubsException, IOException {
        String response = new String (Files.readAllBytes(Paths.get(APIGW_RESPONSES + "getCompanyAuditLogs.json")));

        when(mockAwsResourcesService.getAuditLogs(any())).thenReturn(response);

        auditLogDto.setCategory(COMPANY_CATEGORY);
        AuditLogResponse auditLogResponse = auditAppImpl.getAuditLogs(auditLogDto);
        assertEquals(5, auditLogResponse.getCount());
    }

    @Test
    public void getAdminManagementAuditLogsGivenDefaultAuditLogDtoReturnAuditLogResponse() throws TechpubsException, IOException {
        String response = new String (Files.readAllBytes(Paths.get(APIGW_RESPONSES + "getAdminManagementAuditLogs.json")));

        when(mockAwsResourcesService.getAuditLogs(any())).thenReturn(response);

        auditLogDto.setCategory(ADMIN_MANAGEMENT_CATEGORY);
        AuditLogResponse auditLogResponse = auditAppImpl.getAuditLogs(auditLogDto);
        assertEquals(5, auditLogResponse.getCount());
    }

    @Test
    public void downloadCompanyAuditLogsReturnsAuditLogsExcelFile() throws TechpubsException, ExcelException, IOException {
        String response = new String (Files.readAllBytes(Paths.get(APIGW_RESPONSES + "getCompanyAuditLogs.json")));

        when(mockAwsResourcesService.getAuditLogs(any())).thenReturn(response);

        auditLogDto.setCategory(COMPANY_CATEGORY);
        FileWithBytes fileWithBytes = auditAppImpl.downloadAuditLogs(auditLogDto);

        assertNotNull(fileWithBytes.getContents());
        assertEquals("docadmin_company_audit.xlsx", fileWithBytes.getFileName());
    }

    @Test
    public void downloadAdminManagementAuditLogsReturnsAuditLogsExcelFile() throws TechpubsException, ExcelException, IOException {
        String response = new String (Files.readAllBytes(Paths.get(APIGW_RESPONSES + "getAdminManagementAuditLogs.json")));

        when(mockAwsResourcesService.getAuditLogs(any())).thenReturn(response);

        auditLogDto.setCategory(ADMIN_MANAGEMENT_CATEGORY);
        FileWithBytes fileWithBytes = auditAppImpl.downloadAuditLogs(auditLogDto);

        assertNotNull(fileWithBytes.getContents());
        assertEquals("docadmin_admin_audit.xlsx", fileWithBytes.getFileName());
    }
}
