package com.geaviation.techpubs.services.impl.admin;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.geaviation.techpubs.controllers.requests.SortBy;
import com.geaviation.techpubs.data.api.techlib.enginedoc.IEngineCASNumberData;
import com.geaviation.techpubs.data.api.techlib.enginedoc.IEngineDocumentData;
import com.geaviation.techpubs.data.api.techlib.enginedoc.IEngineDocumentTypeLookupData;
import com.geaviation.techpubs.data.api.techlib.enginedoc.IEnginePartNumberLookupData;
import com.geaviation.techpubs.data.enginedoc.mapper.EngineDocumentMapper;
import com.geaviation.techpubs.data.factories.AmazonS3ClientFactory;
import com.geaviation.techpubs.data.util.PageableUtil;
import com.geaviation.techpubs.data.util.log.LogExecutionTime;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.techlib.EngineModelEntity;
import com.geaviation.techpubs.models.techlib.dto.*;
import com.geaviation.techpubs.models.techlib.enginedoc.EngineCASNumberEntity;
import com.geaviation.techpubs.models.techlib.enginedoc.EngineDocumentEntity;
import com.geaviation.techpubs.models.techlib.enginedoc.EngineDocumentTypeLookupEntity;
import com.geaviation.techpubs.models.techlib.enginedoc.EnginePartNumberLookupEntity;
import com.geaviation.techpubs.services.api.admin.IEngineDocAdminApp;
import com.geaviation.techpubs.services.api.validator.IEngineDocumentsValidator;
import com.geaviation.techpubs.services.excel.ExcelMaker;
import com.geaviation.techpubs.services.excel.exception.ExcelException;
import com.geaviation.techpubs.services.excel.model.ExcelSheet;
import com.geaviation.techpubs.services.excel.model.FileWithBytes;
import com.geaviation.techpubs.services.util.TechpubsAppUtil;
import com.geaviation.techpubs.services.util.admin.EngineModelTableUpdater;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RefreshScope
public class EngineDocAdminAppImpl implements IEngineDocAdminApp {

    private static final Logger LOGGER = LogManager.getLogger(EngineDocAdminAppImpl.class);

    @Autowired
    private IEngineDocumentData engineDocumentData;

    @Autowired
    private IEngineDocumentTypeLookupData iEngineDocumentTypeLookupData;

    @Autowired
    private IEnginePartNumberLookupData enginePartNumberLookupData;

    @Autowired
    private AmazonS3ClientFactory amazonS3ClientFactory;

    @Autowired
    private EngineModelTableUpdater engModelUpdater;
    
    @Autowired
    private IEngineCASNumberData iEngineCASNumberData;
    
    @Autowired
    private IEngineDocumentsValidator engineDocValidator;

    @Value("${engineDocs.bucketName}")
    private String engineDocsBucketName;

    @Value("${techpubs.services.reachMvp2}")
    private boolean reachMvp2Active;

    /**
     * getEngineDocuments: Get a paginated list of engine documents
     *
     * @param page   number of page
     * @param size   size of page
     * @param sortBy the field to sort by - fields specified in
     *               src/main/java/com/geaviation/techpubs/data/enginedoc/mapper/EngineDocumentMapper.java
     * @return Page of EngineDocumentDTO objects
     */

    @Override
    public Page<EngineDocumentDTO> getEngineDocuments(String documentType, List<String> engineModelList, String searchTerm, int page, int size, SortBy sortBy) throws TechpubsException {

        String mappedField = EngineDocumentMapper.sortMapper(sortBy.field());
        String direction = sortBy.direction();
        List<EngineDocumentDTO> documentsList = new ArrayList<EngineDocumentDTO>();

        engineModelList = (engineModelList == null || engineModelList.isEmpty()) ? null : engineModelList;
        searchTerm = (searchTerm == null || searchTerm.isEmpty()) ? null : searchTerm.toLowerCase();

        Pageable pageable = PageableUtil.create(page, size, mappedField, direction);
        Page<EngineDocumentEntity> engineDocuments;
        if (reachMvp2Active) {
            engineDocuments = engineDocumentData.findByDocumentTypeAndEngineModelAndDeletedPaginated(documentType, engineModelList, searchTerm, false, pageable);
        } else {
            engineDocuments = engineDocumentData.findByDeleted(false, pageable);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if (engineDocuments.hasContent()) {
            for (EngineDocumentEntity engineDoc : engineDocuments) {
                EngineDocumentDTO engineDocumentDTO = new EngineDocumentDTO();
                engineDocumentDTO.setId(engineDoc.getId().toString());
                engineDocumentDTO.setDocumentTitle(engineDoc.getDocumentTitle());
                engineDocumentDTO.setLastUpdatedDate(simpleDateFormat.format(engineDoc.getLastUpdatedDate()));
                engineDocumentDTO.setIssueDate(simpleDateFormat.format(engineDoc.getIssueDate()));

                List<String> engineModelsList = new ArrayList<String>();
                if (!engineDoc.getEngineModelEntity().isEmpty()) {
                    for (EngineModelEntity engineModel : engineDoc.getEngineModelEntity()) {
                        engineModelsList.add(engineModel.getModel());
                    }
                }
                engineDocumentDTO.setEngineModels(engineModelsList);
                engineDocumentDTO.setDocumentType(engineDoc.getEngineDocumentTypeLookupEntity().getValue());
                documentsList.add(engineDocumentDTO);
            }

        }
        return new PageImpl<>(documentsList, pageable, engineDocuments.getTotalElements());
    }

    /**
     * "Gets the list of all engine document types"
     */
    @Override
    public List<String> getEngineDocumentTypes() {
        return iEngineDocumentTypeLookupData.findAll().stream().map(e -> e.getValue()).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = TechpubsException.class)
    @LogExecutionTime
    public EngineDocumentEntity addEngineDocuments(EngineDocumentAddReachDTO engineDocumentReachDTO, String ssoId, String documentType)
            throws TechpubsException {
    	try {
	    engineDocValidator.validateEngineDocuments(documentType, engineDocumentReachDTO);
	} catch (Exception e) {
	    throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
	}
        MultipartFile multipartFile = engineDocumentReachDTO.getDocumentUploadFile();
        if (multipartFile == null) {
            LOGGER.error("No file provided for upload");
            throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER, "No file provided");
        }
        if (!multipartFile.getContentType().equalsIgnoreCase(MediaType.APPLICATION_PDF_VALUE)) {
            LOGGER.error("Non-pdf file provided: " + multipartFile.getOriginalFilename());
            throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER, "File must be a pdf");
        }

        EngineDocumentEntity engineDocumentEntity = new EngineDocumentEntity();
        addEngineDocumentMetadata(engineDocumentReachDTO, engineDocumentEntity, ssoId, documentType);
        engineDocumentEntity = engineDocumentData.save(engineDocumentEntity);
        uploadFile(multipartFile, engineDocumentEntity);

        return engineDocumentEntity;
    }

    private void uploadFile(MultipartFile multipartFile, EngineDocumentEntity engineDocument) throws TechpubsException {
        try {

            final File file = TechpubsAppUtil.convertMultiPartFileToFile(multipartFile);
            String filePath = getS3FilePath(engineDocument);
            AmazonS3 amazonS3Client = amazonS3ClientFactory.getS3Client();
            final PutObjectRequest putObjectRequest = new PutObjectRequest(engineDocsBucketName, filePath, file);
            amazonS3Client.putObject(putObjectRequest);

            file.delete();    // To remove the file locally created in the project folder.
        } catch (Exception ex) {
            LOGGER.error("Error while uploading file: " + ex);
            throw new TechpubsException(TechpubsException.TechpubsAppError.UPLOADING_FILE_TO_S3_ERROR);
        }
    }

    private void addEngineDocumentMetadata(EngineDocumentAddReachDTO engineDocumentAddReachDTO, EngineDocumentEntity engineDocumentEntity, String ssoId, String documentType) throws TechpubsException {
        try {
            Optional<EngineDocumentTypeLookupEntity> engineDocumentTypeOptional = iEngineDocumentTypeLookupData.findByValue(documentType);
            if (!engineDocumentTypeOptional.isPresent()) {
                throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
            }
            engineDocumentEntity.setEngineDocumentTypeLookupEntity(engineDocumentTypeOptional.get());

            engineDocumentEntity.setDocumentTitle(engineDocumentAddReachDTO.getDocumentTitle());

            engineDocumentEntity.setPartName(engineDocumentAddReachDTO.getPartName());

            engineDocumentEntity.setDeleted(false);

            engineDocumentEntity.setFileName(engineDocumentAddReachDTO.getDocumentUploadFile().getOriginalFilename());

            engineDocumentEntity.setEmailNotification(engineDocumentAddReachDTO.getEmailNotification());

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            engineDocumentEntity.setIssueDate(formatter.parse(engineDocumentAddReachDTO.getIssueDate()));

            Set<EngineModelEntity> engineModels = engModelUpdater.validateEngineModels(engineDocumentAddReachDTO.getEngineModels(), ssoId);
            engineDocumentEntity.setEngineModelEntity(engineModels);

            Set<EnginePartNumberLookupEntity> partNumbers = getPartNumbersListForEngineDocument(engineDocumentAddReachDTO);
            engineDocumentEntity.setEnginePartNumbers(partNumbers);

            engineDocumentEntity.setEngineCasNumberEntity(engineDocumentAddReachDTO.getCasNumbers());

        } catch (TechpubsException ex) {
            LOGGER.error("Error occurred due to internal issue " + ex);
            throw ex;
        } catch (ParseException ex) {
            LOGGER.error("Error parsing date: " + engineDocumentAddReachDTO.getIssueDate());
            throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER, "Error parsing issue date");
        }
    }

    private Set<EnginePartNumberLookupEntity> getPartNumbersListForEngineDocument(EngineDocumentAddReachDTO engineDocumentReachdto) throws TechpubsException {
        Set<EnginePartNumberLookupEntity> enginePartNumbersSet = new HashSet<>();
        List<String> partNumbers = engineDocumentReachdto.getPartNumbers();
        if (partNumbers == null || partNumbers.isEmpty()) {
            return enginePartNumbersSet;
        }
        for (String partNumber : partNumbers) {
            Optional<EnginePartNumberLookupEntity> partNumberOptional = enginePartNumberLookupData.findByValue(partNumber);
            EnginePartNumberLookupEntity enginePartNumberLookupEntity;
            if (!partNumberOptional.isPresent()) {
                enginePartNumberLookupEntity = new EnginePartNumberLookupEntity();
                enginePartNumberLookupEntity.setValue(partNumber);
                enginePartNumberLookupEntity.setLastUpdatedBy("DOC_ADMIN");
                enginePartNumberLookupEntity = enginePartNumberLookupData.save(enginePartNumberLookupEntity);
            } else {
                enginePartNumberLookupEntity = partNumberOptional.get();
            }
            enginePartNumbersSet.add(enginePartNumberLookupEntity);
        }
        return enginePartNumbersSet;
    }

    @Override
    @Transactional(rollbackFor = TechpubsException.class)
    @LogExecutionTime
    public EngineDocumentEntity deleteEngineDocument(String id) throws TechpubsException {
        LOGGER.info("Delete engine document " + id);
        Optional<EngineDocumentEntity> engineDocumentOptional = engineDocumentData.findById(UUID.fromString(id));
        if (engineDocumentOptional.isPresent()) {
            try {
                EngineDocumentEntity engineDocument = engineDocumentOptional.get();
                engineDocument.setDeleted(true);
                engineDocument = engineDocumentData.save(engineDocument);
                return engineDocument;
            } catch (Exception e) {
                LOGGER.error("Exception occurred deleting document id " + id + "-" + e);
                throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
            }
        } else {
            LOGGER.info("Unable to find engine document with id " + id);
            throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }
    }

    @Override
    public Map<String, Object> getFileInputStreamFromS3(String id) throws TechpubsException {

        Optional<EngineDocumentEntity> engineDocumentEntityOptional = engineDocumentData.findById(UUID.fromString(id));
        if (engineDocumentEntityOptional.isPresent()) {

            EngineDocumentEntity engineDocument = engineDocumentEntityOptional.get();
            //AWS client connect to read file from bucket
            try {
                AmazonS3 amazonS3Client = amazonS3ClientFactory.getS3Client();
                S3Object s3Object = amazonS3Client.getObject(new GetObjectRequest(engineDocsBucketName, getS3FilePath(engineDocument)));
                final InputStream fileInS3 = s3Object.getObjectContent().getDelegateStream();
                Map<String, Object> map = new HashMap<>();
                map.put("stream", fileInS3);
                map.put("filename", engineDocument.getFileName());
                return map;

            } catch (AmazonServiceException e) {
                LOGGER.error("Error downloading file for doc: " + engineDocument.getId(), e);
                throw new TechpubsException(TechpubsException.TechpubsAppError.DOWNLOADING_FILE_FROM_S3_ERROR);
            }
        } else throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);

    }

    //get the filename from the Engine Document Entity

    private String getS3FilePath(EngineDocumentEntity engineDocument) {
        return engineDocument.getEngineDocumentTypeLookupEntity().getValue() + '/' + engineDocument.getId() + '/' + engineDocument.getFileName();
    }

    @Override
    public List<EngineDocumentExcelDownloadDTO> getEngineDocumentListForExcelDownload() throws TechpubsException {
        Optional<List<EngineDocumentEntity>> engineDocumentsOptional = engineDocumentData.findByDeleted(false);
        List<EngineDocumentExcelDownloadDTO> engineDocumentExcelDownloadDTOList = new ArrayList<>();
        if (engineDocumentsOptional.isPresent()) {
            try {
                List<EngineDocumentEntity> engineDocuments = engineDocumentsOptional.get();
                for (EngineDocumentEntity engineDocument : engineDocuments) {
                    EngineDocumentExcelDownloadDTO excelDownloadDTO = new EngineDocumentExcelDownloadDTO();
                    excelDownloadDTO.setDocumentTitle(engineDocument.getDocumentTitle());
                    excelDownloadDTO.setDocumentType(engineDocument.getEngineDocumentTypeLookupEntity().getValue());
                    excelDownloadDTO.setPartName(engineDocument.getPartName());
                    excelDownloadDTO.setEmailNotification(engineDocument.getEmailNotification());
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    excelDownloadDTO.setCreatedDate(formatter.format(engineDocument.getCreatedDate()));
                    excelDownloadDTO.setLastUpdatedDate(formatter.format(engineDocument.getLastUpdatedDate()));
                    excelDownloadDTO.setIssueDate(formatter.format(engineDocument.getIssueDate()));

                    Set<EnginePartNumberLookupEntity> partsNumbersSet = engineDocument.getEnginePartNumbers();
                    String partNumbers = partsNumbersSet.stream().map(EnginePartNumberLookupEntity::getValue)
                            .collect(Collectors.joining(", "));
                    excelDownloadDTO.setPartNumbers(partNumbers);

                    Set<EngineModelEntity> engineModelsSet = engineDocument.getEngineModelEntity();
                    String engineModels = engineModelsSet.stream().map(EngineModelEntity::getModel)
                            .collect(Collectors.joining(", "));
                    excelDownloadDTO.setEngineModels(engineModels);

                    Set<EngineCASNumberEntity> casNumbersSet = engineDocument.getEngineCASNumberEntity();
                    String casNumbers = casNumbersSet.stream().map(EngineCASNumberEntity::getCasNumber)
                            .collect(Collectors.joining(", "));
                    excelDownloadDTO.setCasNumbers(casNumbers);

                    engineDocumentExcelDownloadDTOList.add(excelDownloadDTO);
                }
            } catch (Exception e) {
                LOGGER.error("Error while getting the engine document data " + e);
                throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
            }

        } else {
            LOGGER.info("Engine document contains empty list");
            throw new TechpubsException(TechpubsException.TechpubsAppError.DATA_NOT_FOUND);
        }

        return engineDocumentExcelDownloadDTOList;
    }

    public FileWithBytes downloadEngineDocuments() throws ExcelException, TechpubsException {
        List<EngineDocumentExcelDownloadDTO> documents = getEngineDocumentListForExcelDownload();

        if (documents.isEmpty()) {
            return null;
        }

        ExcelSheet excelSheet = ExcelMaker.buildExcelSheet(documents);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ExcelMaker.excelSheetToFile(excelSheet, os);
            return new FileWithBytes(os.toByteArray(), excelSheet.getFilename());
        } catch (IOException e) {
            throw new ExcelException("Could not write the excel file.", e);
        }
    }

    @Override
    @Transactional(rollbackFor = TechpubsException.class)
    @LogExecutionTime
    public EngineDocumentEntity hardDeleteEngineDocument(String id) throws TechpubsException {
        LOGGER.info("Delete engine document " + id);
        Optional<EngineDocumentEntity> engineDocumentOptional = engineDocumentData.findById(UUID.fromString(id));
        if (engineDocumentOptional.isPresent()) {
            EngineDocumentEntity engineDocument = engineDocumentOptional.get();
            // Delete file from s3
            String filepath = getS3FilePath(engineDocument);
            deleteFileFromS3(filepath);
            // Delete Metadata from DB
            engineDocumentData.delete(engineDocument);
            // Return deleted doc
            return engineDocument;
        } else {
            LOGGER.info("Unable to find engine document with id " + id);
            throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }
    }

    private void deleteFileFromS3(String filepath) throws TechpubsException {
        if (filepath == null || filepath.isEmpty()) {
            LOGGER.error("No filename provided");
            return;
        }
        try {
            AmazonS3 amazonS3Client = amazonS3ClientFactory.getS3Client();
            amazonS3Client.deleteObject(new DeleteObjectRequest(engineDocsBucketName, filepath));
        } catch (AmazonServiceException ex) {
            LOGGER.error("error [" + ex.getMessage() + "] occurred while removing [" + filepath + "]");
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR, "Error deleting file from s3");
        }
    }

    @Override
    @LogExecutionTime
    public EngineDocumentByIdReachDTO getEngineDocumentById(String id) throws TechpubsException {

        Optional<EngineDocumentEntity> engineDocumentEntityOptional = engineDocumentData.findById(UUID.fromString(id));

        EngineDocumentByIdReachDTO engineDocumentByIdReachDTO = new EngineDocumentByIdReachDTO();

        try {
            if (engineDocumentEntityOptional.isPresent()) {
                EngineDocumentEntity engineDocumentEntity = engineDocumentEntityOptional.get();

                engineDocumentByIdReachDTO.setDocumentId(engineDocumentEntity.getId().toString());
                engineDocumentByIdReachDTO.setDocumentType(engineDocumentEntity.getEngineDocumentTypeLookupEntity().getValue());

                engineDocumentByIdReachDTO.setDocumentTitle(engineDocumentEntity.getDocumentTitle());
                engineDocumentByIdReachDTO.setPartName(engineDocumentEntity.getPartName());
                engineDocumentByIdReachDTO.setEmailNotification(engineDocumentEntity.getEmailNotification());
                engineDocumentByIdReachDTO.setIssueDate(engineDocumentEntity.getIssueDate());
                engineDocumentByIdReachDTO.setFileName(engineDocumentEntity.getFileName());

                //Engine Models
                Set<EngineModelEntity> engineModelsSet = engineDocumentEntity.getEngineModelEntity();
                List<String> engineModels = engineModelsSet.stream().map(EngineModelEntity::getModel)
                        .collect(Collectors.toList());
                engineDocumentByIdReachDTO.setEngineModels(engineModels);

                //Engine CAS Num
                Set<EngineCASNumberEntity> casNumbersSet = engineDocumentEntity.getEngineCASNumberEntity();
                List<String> casNumbers = casNumbersSet.stream().map(EngineCASNumberEntity::getCasNumber)
                        .collect(Collectors.toList());
                engineDocumentByIdReachDTO.setCasNumbers(casNumbers);


                //Engine Part Numbers
                Set<EnginePartNumberLookupEntity> partsNumbersSet = engineDocumentEntity.getEnginePartNumbers();
                List<String> partNumbers = partsNumbersSet.stream().map(EnginePartNumberLookupEntity::getValue)
                        .collect(Collectors.toList());
                engineDocumentByIdReachDTO.setPartNumbers(partNumbers);


            } else {
                LOGGER.info("Engine document not found with the given Id " + id);
                throw new TechpubsException(TechpubsException.TechpubsAppError.DATA_NOT_FOUND);
            }

        } catch (Exception e) {
            LOGGER.error("Error while getting the enginedocument data by ID " + e);
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
        }

        return engineDocumentByIdReachDTO;
    }

    @Override
    @Transactional(rollbackFor = TechpubsException.class)
    public EngineDocumentEntity updateEngineDocument(EngineDocumentEditReachDTO engineDocumentEditReachDTO, String id,
			String ssoId) throws TechpubsException {

    	try {
    	    engineDocValidator.validateUpdateEngineDocuments(id, engineDocumentEditReachDTO);
        } catch (Exception e) {
            throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }

        Optional<EngineDocumentEntity> engineDocumentOptional = engineDocumentData.findById(UUID.fromString(id));
        if (engineDocumentOptional.isPresent()) {
            EngineDocumentEntity engineDocument = engineDocumentOptional.get();
            String oldS3filePath = engineDocument.getEngineDocumentTypeLookupEntity().getValue() + "/"
                + engineDocument.getId().toString() + "/" + engineDocument.getFileName();

            // Metadata
            updateEngineDocumentMetadata(engineDocumentEditReachDTO, engineDocument, id, ssoId);
            String newFilePath = getS3FilePath(engineDocument);
            if(!oldS3filePath.equals(newFilePath)) {
                engineDocumentData.save(engineDocument);
                uploadFile(engineDocumentEditReachDTO, engineDocument, oldS3filePath);
            }
            return engineDocument;

        } else {
            LOGGER.error("Unable to update document. ID " + id + " not found.");
            throw new TechpubsException(TechpubsException.TechpubsAppError.DATA_NOT_FOUND);
        }
    }

    private void uploadFile(EngineDocumentEditReachDTO engineDocumentEditReachDTO,
	    EngineDocumentEntity engineDocument, String oldFilePath) throws TechpubsException {

	// If a new file is provided, we need to upload it and delete the old one
	try {
	    final File file = TechpubsAppUtil.convertMultiPartFileToFile(engineDocumentEditReachDTO.getDocumentUploadFile());
            String filePath = getS3FilePath(engineDocument);
            AmazonS3 amazonS3Client = amazonS3ClientFactory.getS3Client();
            final PutObjectRequest putObjectRequest = new PutObjectRequest(engineDocsBucketName, filePath, file);
            amazonS3Client.putObject(putObjectRequest);
            file.delete();
	        if (!filePath.equals(oldFilePath)) {
		    // delete old file
		    deleteFileFromS3(oldFilePath);
		}
			
	} catch (Exception e) {
	    LOGGER.error("Error updating document file: " + e);
	    throw new TechpubsException(TechpubsException.TechpubsAppError.UPLOADING_FILE_TO_S3_ERROR);
	}
    }

    private void updateEngineDocumentMetadata(EngineDocumentEditReachDTO engineDocumentEditReachDTO,
	    EngineDocumentEntity engineDocumentEntity, String id, String ssoId) throws TechpubsException {
	try {
			engineDocumentEntity.setDocumentTitle(engineDocumentEditReachDTO.getDocumentTitle());

			engineDocumentEntity.setPartName(engineDocumentEditReachDTO.getPartName());

			engineDocumentEntity.setDeleted(false);

            if (engineDocumentEditReachDTO.getDocumentUploadFile() != null) {
                engineDocumentEntity.setFileName(engineDocumentEditReachDTO.getDocumentUploadFile().getOriginalFilename());
            }

			engineDocumentEntity.setEmailNotification(engineDocumentEditReachDTO.getEmailNotification());
				
			engineDocumentEntity.setLastEmailSentDate(null);

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			engineDocumentEntity.setIssueDate(formatter.parse(engineDocumentEditReachDTO.getIssueDate()));

			Set<EngineModelEntity> engineModels = engModelUpdater.validateEngineModels(engineDocumentEditReachDTO.getEngineModels(),
			    ssoId);
			engineDocumentEntity.setEngineModelEntity(engineModels);

			Set<EnginePartNumberLookupEntity> partNumbers = getPartNumbersListForEngineDocument(engineDocumentEditReachDTO);
			engineDocumentEntity.setEnginePartNumbers(partNumbers);

			engineDocumentEntity.setEngineCasNumberEntity(engineDocumentEditReachDTO.getCasNumbers());
			
		} catch (TechpubsException ex) {
			LOGGER.error("Error occurred due to internal issue " + ex);
			throw ex;
		} catch (ParseException ex) {
			LOGGER.error("Error parsing date: " + engineDocumentEditReachDTO.getIssueDate());
			throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER,
					"Error parsing issue date");
		}
	}
	
	private Set<EnginePartNumberLookupEntity> getPartNumbersListForEngineDocument(EngineDocumentEditReachDTO engineDocumentEditReachDTO) throws TechpubsException {
        Set<EnginePartNumberLookupEntity> enginePartNumbersSet = new HashSet<>();
        List<String> partNumbers = engineDocumentEditReachDTO.getPartNumbers();
        if (partNumbers == null || partNumbers.isEmpty()) {
            return enginePartNumbersSet;
        }
        for (String partNumber : partNumbers) {
             Optional<EnginePartNumberLookupEntity> partNumberOptional = enginePartNumberLookupData.findByValue(partNumber);
             EnginePartNumberLookupEntity enginePartNumberLookupEntity;
             if (!partNumberOptional.isPresent()) {
                enginePartNumberLookupEntity = new EnginePartNumberLookupEntity();
                enginePartNumberLookupEntity.setValue(partNumber);
                enginePartNumberLookupEntity.setLastUpdatedBy("DOC_ADMIN");
                enginePartNumberLookupEntity = enginePartNumberLookupData.save(enginePartNumberLookupEntity);
             } else {
            	 enginePartNumberLookupEntity = partNumberOptional.get();
             }
             enginePartNumbersSet.add(enginePartNumberLookupEntity);
        }
        return enginePartNumbersSet;
    }
}



