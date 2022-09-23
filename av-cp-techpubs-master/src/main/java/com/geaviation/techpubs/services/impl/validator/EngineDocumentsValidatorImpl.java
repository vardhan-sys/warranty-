package com.geaviation.techpubs.services.impl.validator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.geaviation.techpubs.services.util.constants.Constants;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.exceptions.TechpubsException.TechpubsAppError;
import com.geaviation.techpubs.models.techlib.dto.EngineDocumentAddReachDTO;
import com.geaviation.techpubs.models.techlib.dto.EngineDocumentEditReachDTO;
import com.geaviation.techpubs.services.api.validator.IEngineDocumentsValidator;
import com.geaviation.techpubs.services.util.StringUtils;

@Component
public class EngineDocumentsValidatorImpl implements IEngineDocumentsValidator {
	 @Override
	 public void validateEngineDocuments(String documentType, EngineDocumentAddReachDTO engineDocumentReachDTO) throws TechpubsException {
			
			List<String> errorList = new ArrayList<>();
			if(engineDocumentReachDTO == null) {
			    errorList.add("engineDocumentReachDTO" + Constants.ERROR_MSG_REQUIRED_AND_INVALID);
			    throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
			}
			if(StringUtils.isEmpty(documentType)) {
				errorList.add("documentType" + Constants.ERROR_MSG_REQUIRED_AND_INVALID);
			}
			if(StringUtils.isEmpty(engineDocumentReachDTO.getDocumentTitle())) {
				errorList.add("documentTitle" + Constants.ERROR_MSG_REQUIRED_AND_INVALID);
			}
			if(StringUtils.isEmpty(engineDocumentReachDTO.getPartName())) {
				errorList.add("partName " + Constants.ERROR_MSG_REQUIRED_AND_INVALID);
			}
			MultipartFile multipartFile = engineDocumentReachDTO.getDocumentUploadFile();
	       		 if (multipartFile == null) {
				errorList.add("documentUploadFile" + Constants.ERROR_MSG_REQUIRED_AND_INVALID);
			}
	       		 List<String> inputEngineModels = engineDocumentReachDTO.getEngineModels();
			if(inputEngineModels == null || inputEngineModels.size()==0) {
				errorList.add("engineModels" + Constants.ERROR_MSG_REQUIRED_AND_INVALID);
			}
			List<String> inputEnginePartNumbers = engineDocumentReachDTO.getPartNumbers();
			if(inputEnginePartNumbers == null || inputEnginePartNumbers.size()==0) {
				errorList.add("partNumbers" + Constants.ERROR_MSG_REQUIRED_AND_INVALID);
			}
			List<String> inputEngineCASNumbers = engineDocumentReachDTO.getCasNumbers();
			if(inputEngineCASNumbers == null || inputEngineCASNumbers.size()==0) {
				errorList.add("casNumbers" + Constants.ERROR_MSG_REQUIRED_AND_INVALID);
			}
			
			throwValidationErrorMsg(errorList);
			
		}

		
		@Override
		public void validateUpdateEngineDocuments(String id, EngineDocumentEditReachDTO engineDocumentEditReachDTO) throws TechpubsException {
			
			List<String> errorList = new ArrayList<>();
			if(engineDocumentEditReachDTO == null) {
			    errorList.add("engineDocumentEditReachDTO" + Constants.ERROR_MSG_REQUIRED_AND_INVALID);
			    throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
			}
			if(!StringUtils.isEmpty(engineDocumentEditReachDTO.getDocumentType())) {
				errorList.add("documentType" + Constants.ERROR_MSG_REQUIRED_AND_INVALID);
			}
			
			throwValidationErrorMsg(errorList);
		}
		
		private static void throwValidationErrorMsg(List<String> errorList) throws TechpubsException {
			
			if (!errorList.isEmpty()) {

				String errorString = String.join(", ", errorList);

				throw new TechpubsException(TechpubsAppError.INVALID_PARAMETER, errorString);
			}
		}
}
