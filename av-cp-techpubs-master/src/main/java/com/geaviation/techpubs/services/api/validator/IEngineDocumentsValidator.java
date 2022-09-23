package com.geaviation.techpubs.services.api.validator;

import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.techlib.dto.EngineDocumentAddReachDTO;
import com.geaviation.techpubs.models.techlib.dto.EngineDocumentEditReachDTO;

public interface IEngineDocumentsValidator {
     void validateEngineDocuments(String documentType, EngineDocumentAddReachDTO engineDocumentReachDTO) throws TechpubsException;
     void validateUpdateEngineDocuments(String id, EngineDocumentEditReachDTO engineDocumentEditReachDTO) throws TechpubsException;
}
