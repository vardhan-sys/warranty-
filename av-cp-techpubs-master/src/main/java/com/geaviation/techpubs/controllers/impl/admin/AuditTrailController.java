package com.geaviation.techpubs.controllers.impl.admin;

import com.geaviation.techpubs.controllers.util.Constants;
import com.geaviation.techpubs.data.model.dto.AuditLogDto;
import com.geaviation.techpubs.data.model.response.AuditLogResponse;
import com.geaviation.techpubs.data.util.SecurityEscape;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.services.api.admin.IAuditApp;
import com.geaviation.techpubs.services.excel.exception.ExcelException;
import com.geaviation.techpubs.services.excel.model.FileWithBytes;
import com.geaviation.techpubs.services.impl.admin.AuthServiceImpl;

import io.swagger.annotations.ApiParam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.geaviation.techpubs.services.util.AppConstants.SM_SSOID;

@RestController
@RefreshScope
@RequestMapping("/admin/audit-trail")
public class AuditTrailController {

    private static final Logger log = LogManager.getLogger(AuditTrailController.class);

    @Value("${techpubs.services.sqlInjection}")
    private boolean sqlInjection;

    @Autowired
    private IAuditApp iAuditApp;

    @Autowired
    AuthServiceImpl authServiceImpl;

    /**
     * Given an engine model, get a list of corresponding bookcase keys.
     *
     * @return The list of bookcase keys.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuditLogResponse> getAuditLogs(
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "product", value = "eg. aero") @RequestParam(value = "product") String product,
            @RequestBody AuditLogDto auditLogDto,
            HttpServletRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
        }

        try {
            authServiceImpl.checkResourceAccessForProduct(ssoId, "audit-trail", request, product);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(iAuditApp.getAuditLogs(auditLogDto));
    }

    /**
     * Given an engine model, get a list of corresponding bookcase keys. 
     *
     * @return The list of bookcase keys.
     */
    @PostMapping(value = "/download", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> downloadAuditLogs(
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "product", value = "eg. aero") @RequestParam(value = "product") String product,
            @RequestBody AuditLogDto auditLogDto,
            HttpServletRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
        }

        try {
            authServiceImpl.checkResourceAccessForProduct(ssoId, "audit-trail", request, product);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ResponseEntity<byte[]> response;

        try {
            FileWithBytes fileWithBytes = iAuditApp.downloadAuditLogs(auditLogDto);
            byte[] content = fileWithBytes.getContents();
            String fileName = fileWithBytes.getFileName().split("\\.", 2)[0];
            String fileType = fileWithBytes.getFileName().split("\\.", 2)[1];
            String file = fileName + "_" +
                    new SimpleDateFormat("yyyy_MM_dd_HH.mm.ss").format(new Date())
                    + "." + fileType;

            response = ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, Constants.ATTACHMENT_FILENAME + file + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(content);
        } catch (ExcelException e) {
            log.error("Error building excel file in /audit-trail/download");
            response = ResponseEntity.ok(Constants.NO_DATA_TO_DOWNLOAD.getBytes());
        }

        return response;
    }
}
