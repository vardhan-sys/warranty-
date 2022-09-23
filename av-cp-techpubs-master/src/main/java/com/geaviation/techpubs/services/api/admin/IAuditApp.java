package com.geaviation.techpubs.services.api.admin;

import com.geaviation.techpubs.data.model.dto.AuditLogDto;
import com.geaviation.techpubs.data.model.response.AuditLogResponse;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.services.excel.exception.ExcelException;
import com.geaviation.techpubs.services.excel.model.FileWithBytes;

public interface IAuditApp {

    AuditLogResponse getAuditLogs(AuditLogDto auditLogDto) throws TechpubsException;
    FileWithBytes downloadAuditLogs(AuditLogDto auditLogDto) throws ExcelException, TechpubsException;
}
