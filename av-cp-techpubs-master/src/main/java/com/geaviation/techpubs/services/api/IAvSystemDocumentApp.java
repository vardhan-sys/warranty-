package com.geaviation.techpubs.services.api;

import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.services.excel.exception.ExcelException;
import com.geaviation.techpubs.services.excel.model.FileWithBytes;

import org.springframework.core.io.InputStreamResource;

import java.util.Map;

public interface IAvSystemDocumentApp {

    InputStreamResource getDocumentFromS3(String ssoId, String portalId, String docType, String docSite,
                                          String docNumber, String fileName) throws TechpubsException;
    
    FileWithBytes getAvSystemExcelDocument(String ssoId, String portalId, String payload) throws TechpubsException,ExcelException;

    Map<String, Object> getDocumentFromS3ById(String ssoId, String portalId, String docId) throws TechpubsException;
}
