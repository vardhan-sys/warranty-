package com.geaviation.techpubs.services.api.admin;

import com.geaviation.techpubs.controllers.requests.SortBy;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.techlib.dto.*;
import com.geaviation.techpubs.models.techlib.enginedoc.EngineDocumentEntity;
import com.geaviation.techpubs.services.excel.exception.ExcelException;
import com.geaviation.techpubs.services.excel.model.FileWithBytes;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;


public interface IEngineDocAdminApp {

    List<String> getEngineDocumentTypes();

    EngineDocumentEntity addEngineDocuments(EngineDocumentAddReachDTO engineDocumentReachDTO, String ssoId, String documentType) throws TechpubsException;

    Page<EngineDocumentDTO> getEngineDocuments(String documentType, List<String> engineModelList, String searchTerm, int page, int size, SortBy sortBy) throws TechpubsException;

    EngineDocumentEntity deleteEngineDocument(String id) throws TechpubsException;

    Map<String, Object> getFileInputStreamFromS3(String id) throws TechpubsException;

    FileWithBytes downloadEngineDocuments() throws ExcelException, TechpubsException;

    List<EngineDocumentExcelDownloadDTO> getEngineDocumentListForExcelDownload() throws TechpubsException;

    EngineDocumentEntity hardDeleteEngineDocument(String id) throws TechpubsException;

    EngineDocumentByIdReachDTO getEngineDocumentById(String id) throws TechpubsException;
    
    public EngineDocumentEntity updateEngineDocument(EngineDocumentEditReachDTO engineDocumentEditReachDTO, String id, String ssoId) throws TechpubsException;

}