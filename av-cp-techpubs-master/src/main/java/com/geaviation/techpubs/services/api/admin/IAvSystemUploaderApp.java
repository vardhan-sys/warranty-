package com.geaviation.techpubs.services.api.admin;

import com.geaviation.techpubs.controllers.requests.SortBy;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.techlib.SystemDocumentEntity;
import com.geaviation.techpubs.models.techlib.SystemDocumentSiteLookupEntity;
import com.geaviation.techpubs.models.techlib.SystemDocumentTypeLookupEntity;
import com.geaviation.techpubs.models.techlib.dto.AddSystemDocumentsDto;
import com.geaviation.techpubs.models.techlib.dto.SystemDocumentByIdDTO;
import com.geaviation.techpubs.models.techlib.dto.SystemDocumentDTO;
import com.geaviation.techpubs.models.techlib.dto.SystemDocumentExcelDownloadDTO;
import com.geaviation.techpubs.services.excel.exception.ExcelException;
import com.geaviation.techpubs.services.excel.model.FileWithBytes;
import org.springframework.data.domain.Page;

import java.io.ByteArrayOutputStream;
import java.util.List;

public interface IAvSystemUploaderApp {

    List<SystemDocumentTypeLookupEntity> getSystemDocumentType();

    List<SystemDocumentSiteLookupEntity> getSystemDocumentSite();

    String addSystemDocuments(AddSystemDocumentsDto addSystemDocumentsDto) throws TechpubsException;

    List<SystemDocumentExcelDownloadDTO> getSystemDocumentListForExcelDownload() throws TechpubsException;

    SystemDocumentByIdDTO getSystemDocumentById(String id) throws TechpubsException;

    byte[] getSystemDocumentDownload(SystemDocumentEntity systemDocument) throws TechpubsException;

    SystemDocumentEntity getSystemDocumentEntityById(String id) throws TechpubsException;

    FileWithBytes downloadSystemDocuments() throws ExcelException, TechpubsException;

    Boolean systemDocumentExists(String documentNumber, String documentSiteId, String documentTypeId) throws TechpubsException;

    void deleteSystemDocument(String id) throws TechpubsException;

    Page<SystemDocumentDTO> getSystemDocumentsByDocumentTypeAndPartNumber(
            String partNumber, String documentType, int page, int size, SortBy sortBy) throws TechpubsException;

    void updateSystemDocument(AddSystemDocumentsDto addSystemDocumentsDto, String id) throws TechpubsException;

    void hardDeleteSystemDocument(String id) throws TechpubsException;

}
