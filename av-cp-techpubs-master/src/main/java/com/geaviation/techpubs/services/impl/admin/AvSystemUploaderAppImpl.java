package com.geaviation.techpubs.services.impl.admin;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.geaviation.techpubs.config.S3Config;
import com.geaviation.techpubs.controllers.requests.SortBy;
import com.geaviation.techpubs.data.api.techlib.IPartNumbersAffectedData;
import com.geaviation.techpubs.data.api.techlib.IAirframeLookupData;
import com.geaviation.techpubs.data.api.techlib.ISalesforceCompanyData;
import com.geaviation.techpubs.data.api.techlib.ISystemDocumentData;
import com.geaviation.techpubs.data.api.techlib.ISystemDocumentSiteLookupData;
import com.geaviation.techpubs.data.api.techlib.ISystemDocumentTypeLookupData;
import com.geaviation.techpubs.data.factories.AmazonS3ClientFactory;
import com.geaviation.techpubs.data.impl.AwsResourcesService;
import com.geaviation.techpubs.data.mapper.SystemDocumentMapper;
import com.geaviation.techpubs.data.util.PageableUtil;
import com.geaviation.techpubs.data.util.log.LogExecutionTime;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.techlib.AirframeLookupEntity;
import com.geaviation.techpubs.models.techlib.PartNumbersAffectedEntity;
import com.geaviation.techpubs.models.techlib.SalesforceCompanyLookupEntity;
import com.geaviation.techpubs.models.techlib.SystemDocumentEntity;
import com.geaviation.techpubs.models.techlib.SystemDocumentSiteLookupEntity;
import com.geaviation.techpubs.models.techlib.SystemDocumentTypeLookupEntity;
import com.geaviation.techpubs.models.techlib.dto.*;
import com.geaviation.techpubs.services.api.admin.IAvSystemUploaderApp;
import com.geaviation.techpubs.services.excel.ExcelMaker;
import com.geaviation.techpubs.services.excel.exception.ExcelException;
import com.geaviation.techpubs.services.excel.model.ExcelSheet;
import com.geaviation.techpubs.services.excel.model.FileWithBytes;
import com.geaviation.techpubs.services.util.TechpubsAppUtil;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AvSystemUploaderAppImpl implements IAvSystemUploaderApp {

    private static final Logger LOGGER = LogManager.getLogger(AvSystemUploaderAppImpl.class);

    @Autowired
    private AwsResourcesService awsResourcesService;

    @Autowired
    private ISystemDocumentTypeLookupData iSystemDocumentTypeLookupData;

    @Autowired
    private ISystemDocumentSiteLookupData iSystemDocumentSiteLookupData;

    @Autowired
    private ISystemDocumentData iSystemDocumentData;

    @Autowired
    private IAirframeLookupData iAirframeLookupData;

    @Autowired
    private ISalesforceCompanyData iSalesforceCompanyData;

    @Autowired
    private IPartNumbersAffectedData iPartNumbersAffectedData;

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${avsystems3.admin.uploader.bucketName}")
    private String avSystemsBucketName;

    @Autowired
    private S3Config s3Config;

    @Autowired
    private AmazonS3ClientFactory amazonS3ClientFactory;


    @Override
    public List<SystemDocumentTypeLookupEntity> getSystemDocumentType() {
        return iSystemDocumentTypeLookupData.findAll();
    }

    @Override
    public List<SystemDocumentSiteLookupEntity> getSystemDocumentSite() {
        return iSystemDocumentSiteLookupData.findAll();
    }

    /**
     * getSystemDocumentsByDocumentTypeAndPartNumber: Get a paginated list of system documents with optional part number and document type filters
     *
     * @param partNumber part number to filter on - String (optional, can be null)
     * @param documentTypeId ID of documentType to filter on - String (optional, can be null)
     * @param page number of page
     * @param size size of page
     * @param sortBy the field to sort by - fields specified in src/main/java/com/geaviation/techpubs/data/mapper/SystemDocumentMapper.java
     *
     * @return Page of SystemDocumentDTO objects
     */
    @Override
    public Page<SystemDocumentDTO> getSystemDocumentsByDocumentTypeAndPartNumber(
            String partNumber, String documentTypeId, int page, int size, SortBy sortBy) throws TechpubsException {

        String mappedField = SystemDocumentMapper.sortMapper(sortBy.field());
        String direction = sortBy.direction();

        Pageable pageable = PageableUtil.create(page, size, mappedField, direction);

        Page<SystemDocumentDTO> documents;
        if (partNumber != null && !partNumber.isEmpty()) {
            if (documentTypeId != null && !documentTypeId.isEmpty()) {
                documents = iSystemDocumentData
                        .findByDocumentTypeAndPartNumberAndDeleted(
                                UUID.fromString(documentTypeId), partNumber, false, pageable);
            } else {
                documents = iSystemDocumentData
                        .findByPartNumberAndDeleted(partNumber, false, pageable);
            }
        } else if (documentTypeId != null && !documentTypeId.isEmpty()) {
            documents = iSystemDocumentData
                    .findByDocumentTypeAndDeleted(UUID.fromString(documentTypeId), false, pageable);
        } else {
            documents = iSystemDocumentData.findByDeletedPaginated(false, pageable);
        }
        return documents;
    }

    private void deleteFileFromS3Bucket(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            LOGGER.error("No filename provided");
            return;
        }
        try {
            LOGGER.info("Deleting File from s3 due to Exception occured while saving data to source");
            AmazonS3 amazonS3Client = amazonS3ClientFactory.getS3Client();
            amazonS3Client.deleteObject(new DeleteObjectRequest(avSystemsBucketName, fileName));
        } catch (AmazonServiceException | TechpubsException ex) {
            LOGGER.error("error [" + ex.getMessage() + "] occurred while removing [" + fileName + "] ");
        }
    }

    @Override
    public List<SystemDocumentExcelDownloadDTO> getSystemDocumentListForExcelDownload() throws TechpubsException {
        Optional<List<SystemDocumentEntity>> systemDocumentsOptional = iSystemDocumentData.findByDeleted(false);
        List<SystemDocumentExcelDownloadDTO> systemDocumentExcelDownloadDTOList = new ArrayList<>();

        try {
            if (systemDocumentsOptional.isPresent()) {
                List<SystemDocumentEntity> systemDocuments = systemDocumentsOptional.get();
                for (SystemDocumentEntity systemDocument : systemDocuments) {
                    SystemDocumentExcelDownloadDTO excelDownloadDTO = new SystemDocumentExcelDownloadDTO();
                    excelDownloadDTO.setDocumentTitle(systemDocument.getDocumentDescription());
                    excelDownloadDTO.setDocumentSite(systemDocument.getSystemDocumentSiteLookupEntity().getValue());
                    excelDownloadDTO.setDocumentType(systemDocument.getSystemDocumentTypeLookupEntity().getValue());
                    excelDownloadDTO.setDocumentNumber(systemDocument.getDocumentNumber());
                    excelDownloadDTO.setDocumentRevision(systemDocument.getRevision());
                    excelDownloadDTO.setCompanySpecific(systemDocument.getCompanySpecific());
                    excelDownloadDTO.setDocumentRevisionDate(systemDocument.getRevisionDate());
                    excelDownloadDTO.setDocumentDistributionDate(systemDocument.getDistributionDate());
                    excelDownloadDTO.setPowerDocument(systemDocument.getPowerDocument());

                    Set<PartNumbersAffectedEntity> partsAffectedList = systemDocument.getPartNumbersAffectedEntity();
                    String partsAffected = partsAffectedList.stream().map(PartNumbersAffectedEntity::getPartNumber).collect(Collectors.joining(", "));
                    excelDownloadDTO.setPartsAffected(partsAffected);

                    Set<AirframeLookupEntity> aircraftList = systemDocument.getAirframes();
                    String aircraft = aircraftList.stream().map(AirframeLookupEntity::getAirframe).collect(Collectors.joining(", "));
                    excelDownloadDTO.setAircraftPlatforms(aircraft);

                    if (systemDocument.getCompanySpecific()) {
                        Set<SalesforceCompanyLookupEntity> specificCustomersList = systemDocument.getSpecificCompanies();
                        String specificCustomers = specificCustomersList.stream().map(c -> c.getCompanyName()).collect(Collectors.joining(", "));
                        excelDownloadDTO.setSpecificCustomers(specificCustomers);
                    }

                    systemDocumentExcelDownloadDTOList.add(excelDownloadDTO);
                }
            } else {
                LOGGER.info("System document contains empty list");
                throw new TechpubsException(TechpubsException.TechpubsAppError.DATA_NOT_FOUND);
            }
        } catch (Exception e) {
            LOGGER.error("Error while getting the system document data " + e);
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
        }

        return systemDocumentExcelDownloadDTOList;
    }

    @Override
    @Transactional(rollbackFor = TechpubsException.class)
    @LogExecutionTime
    public String addSystemDocuments(AddSystemDocumentsDto addSystemDocumentsDto) throws TechpubsException {

        // Check if combo of site/type/docnbr already exists
        if (systemDocumentExists(addSystemDocumentsDto.getDocumentType(), addSystemDocumentsDto.getDocumentNumber(), addSystemDocumentsDto.getDocumentSite())) {
            LOGGER.error("Unable to create document. A document already exists with site " +
                    addSystemDocumentsDto.getDocumentSite() + ", type " + addSystemDocumentsDto.getDocumentType() +
                    " and document number " + addSystemDocumentsDto.getDocumentNumber());
            throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_DOCUMENT_IDENTIFIERS);
        }

        SystemDocumentEntity systemDocumentEntity = new SystemDocumentEntity();

        // Set file name and path
        String filepath = getFilePathForUpload(addSystemDocumentsDto.getDocumentUploadFile().getOriginalFilename(), addSystemDocumentsDto);
        systemDocumentEntity.setFileName(addSystemDocumentsDto.getDocumentUploadFile().getOriginalFilename());
        systemDocumentEntity.setS3FilePath(filepath);

        // Metadata
        addOrUpdateSystemDocumentMetadata(addSystemDocumentsDto, systemDocumentEntity);

        // File upload
        uploadFile(addSystemDocumentsDto.getDocumentUploadFile(), addSystemDocumentsDto);
        systemDocumentEntity = iSystemDocumentData.save(systemDocumentEntity);

        return systemDocumentEntity.getId().toString();
    }

    @Override
    @Transactional(rollbackFor = TechpubsException.class)
    public void updateSystemDocument(AddSystemDocumentsDto addSystemDocumentsDto, String id) throws TechpubsException {
        Optional<SystemDocumentEntity> systemDocumentOptional = iSystemDocumentData.findById(UUID.fromString(id));
        if(systemDocumentOptional.isPresent()){
            // Check if combo of site/type/docnbr already exists, and isn't the doc we're modifying
            Optional<SystemDocumentEntity> systemDocumentOptionalByData = iSystemDocumentData
                    .findByDocumentNumberSiteAndType(
                            UUID.fromString(addSystemDocumentsDto.getDocumentType()),
                            addSystemDocumentsDto.getDocumentNumber(), UUID.fromString(addSystemDocumentsDto.getDocumentSite()));
            if (systemDocumentOptionalByData.isPresent() && !systemDocumentOptionalByData.get().getId().toString().equals(id)) {
                LOGGER.error("Unable to modify document. A document already exists with site " +
                        addSystemDocumentsDto.getDocumentSite() + ", type " + addSystemDocumentsDto.getDocumentType() +
                        " and document number " + addSystemDocumentsDto.getDocumentNumber());
                throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_DOCUMENT_IDENTIFIERS);
            }

            SystemDocumentEntity systemDocument = systemDocumentOptional.get();
            String oldFilePath = systemDocument.getS3FilePath();

            // Metadata
            addOrUpdateSystemDocumentMetadata(addSystemDocumentsDto, systemDocument);

            // File Upload + set filename & filepath
            uploadFileForUpdateSystemDocument(addSystemDocumentsDto, systemDocument, oldFilePath);
            iSystemDocumentData.save(systemDocument);
        } else {
            LOGGER.error("Unable to update document. ID " + id + " not found.");
            throw new TechpubsException(TechpubsException.TechpubsAppError.DATA_NOT_FOUND);
        }
    }

    @Override
    public SystemDocumentByIdDTO getSystemDocumentById(String id) throws TechpubsException {

        Optional<SystemDocumentEntity> systemDocumentOptional = iSystemDocumentData.findById(UUID.fromString(id));
        SystemDocumentByIdDTO systemDocumentByIdDTO = new SystemDocumentByIdDTO();

        try {
            if (systemDocumentOptional.isPresent()) {
                SystemDocumentEntity systemDocument = systemDocumentOptional.get();

                systemDocumentByIdDTO.setSystemDocumentId(systemDocument.getId().toString());
                systemDocumentByIdDTO.setDocumentType(systemDocument.getSystemDocumentTypeLookupEntity().getValue());
                systemDocumentByIdDTO.setDocumentTypeID(systemDocument.getSystemDocumentTypeLookupEntity().getId().toString());
                systemDocumentByIdDTO.setDocumentSiteID(systemDocument.getSystemDocumentSiteLookupEntity().getId().toString());
                systemDocumentByIdDTO.setDocumentNumber(systemDocument.getDocumentNumber());
                systemDocumentByIdDTO.setDocumentSite(systemDocument.getSystemDocumentSiteLookupEntity().getValue());
                systemDocumentByIdDTO.setDocumentTitle(systemDocument.getDocumentDescription());
                systemDocumentByIdDTO.setDocumentRevision(systemDocument.getRevision());
                systemDocumentByIdDTO.setDocumentDistributionDate(systemDocument.getDistributionDate());
                systemDocumentByIdDTO.setDocumentRevisionDate(systemDocument.getRevisionDate());
                systemDocumentByIdDTO.setEmailNotification(systemDocument.getEmailNotification());
                systemDocumentByIdDTO.setCompanySpecific(systemDocument.getCompanySpecific());
                systemDocumentByIdDTO.setFileName(systemDocument.getFileName());
                systemDocumentByIdDTO.setPowerDocument(systemDocument.getPowerDocument());



                Set<AirframeLookupEntity> airCraftList = systemDocument.getAirframes();
                List<AirframeDto> airCraftListData = new ArrayList<>();
                for(AirframeLookupEntity aircraft : airCraftList){
                    AirframeDto airframe = new AirframeDto();
                    airframe.setId(aircraft.getId());
                    airframe.setAirframe(aircraft.getAirframe());
                    airCraftListData.add(airframe);
                }
                systemDocumentByIdDTO.setAircraftPlatforms(airCraftListData);

                Set<PartNumbersAffectedEntity> partAffectedList = systemDocument.getPartNumbersAffectedEntity();
                List<String> partNumberListData = new ArrayList<String>();
                for(PartNumbersAffectedEntity partAffected:partAffectedList){

                    partNumberListData.add(partAffected.getPartNumber());

                }
                systemDocumentByIdDTO.setPartsAffected(partNumberListData);


                Set<SalesforceCompanyLookupEntity> specificCustomersList = systemDocument.getSpecificCompanies();
                List<SalesforceCompanyDto> specificCustomerListData = new ArrayList<>();
                for(SalesforceCompanyLookupEntity specificCustomer : specificCustomersList){
                    SalesforceCompanyDto salesforceCompanyDto = new SalesforceCompanyDto(specificCustomer.getId(), specificCustomer.getCompanyName());
                    specificCustomerListData.add(salesforceCompanyDto);
                }
                systemDocumentByIdDTO.setSpecificCustomers(specificCustomerListData);

                Set<SalesforceCompanyLookupEntity> companyPaidSubscriptionList = systemDocument.getCompanyPaidSubscription();
                List<SalesforceCompanyDto> companyPaidSubscriptionData = new ArrayList<>();
                for(SalesforceCompanyLookupEntity companyPaidSubscription : companyPaidSubscriptionList){
                    SalesforceCompanyDto salesforceCompanyDto = new SalesforceCompanyDto(companyPaidSubscription.getId(), companyPaidSubscription.getCompanyName());
                    companyPaidSubscriptionData.add(salesforceCompanyDto);
                }
                systemDocumentByIdDTO.setCompanyPaidSubscription(companyPaidSubscriptionData);


            } else {
                LOGGER.info("System document not found with the given Id " +id);
                throw new TechpubsException(TechpubsException.TechpubsAppError.DATA_NOT_FOUND);
            }

        } catch (Exception e) {
            LOGGER.error("Error while getting the system document data by ID " + e);
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
        }

        return systemDocumentByIdDTO;
    }

    @Override
    public Boolean systemDocumentExists(String documentTypeId, String documentNumber, String documentSiteId) throws TechpubsException {
        // NOTE: intentionally including deleted documents in this query
        // Prefer that users un-delete a document instead of creating a new one with the same identifiers
        Optional<SystemDocumentEntity> systemDocumentOptional = iSystemDocumentData
                .findByDocumentNumberSiteAndType(
                        UUID.fromString(documentTypeId),
                        documentNumber, UUID.fromString(documentSiteId));

        return systemDocumentOptional.isPresent();
    }

    @Override
    public SystemDocumentEntity getSystemDocumentEntityById(String id) throws TechpubsException {
        Optional<SystemDocumentEntity> systemDocumentOptional = iSystemDocumentData.findById(UUID.fromString(id));
        if (systemDocumentOptional.isPresent()) {
            return systemDocumentOptional.get();
        } else {
            LOGGER.error("No system document found for id " + id);
            throw new TechpubsException(TechpubsException.TechpubsAppError.DATA_NOT_FOUND);
        }
    }

    @Override
    public byte[] getSystemDocumentDownload(SystemDocumentEntity systemDocument) throws TechpubsException{
        try {
            AmazonS3 amazonS3Client = amazonS3ClientFactory.getS3Client();
            S3Object s3Object = amazonS3Client.getObject(new GetObjectRequest(avSystemsBucketName, systemDocument.getS3FilePath()));
            byte[] file = IOUtils.toByteArray(s3Object.getObjectContent());
            s3Object.close();
            return file;
        } catch (IOException | AmazonServiceException e) {
            LOGGER.error("Error downloading file " + systemDocument.getS3FilePath(), e);
            throw new TechpubsException(TechpubsException.TechpubsAppError.DOWNLOADING_FILE_FROM_S3_ERROR);
        }
    }

    public FileWithBytes downloadSystemDocuments() throws ExcelException, TechpubsException {
        List<SystemDocumentExcelDownloadDTO> documents = getSystemDocumentListForExcelDownload();

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
    public void deleteSystemDocument(String id) throws TechpubsException {
        LOGGER.info("Delete system document "+ id);
        Optional<SystemDocumentEntity> systemDocumentOptional = iSystemDocumentData.findById(UUID.fromString(id));
        if(systemDocumentOptional.isPresent()){
            try {
                SystemDocumentEntity systemDocument = systemDocumentOptional.get();
                systemDocument.setDeleted(true);
                iSystemDocumentData.save(systemDocument);
            }catch (Exception e){
                LOGGER.error("Exception occurred deleting document id " + id, e);
                throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
            }
        }else{
            LOGGER.info("Unable to find system document with id " + id);
            throw new TechpubsException(TechpubsException.TechpubsAppError.DATA_NOT_FOUND);
        }
    }

    private void addOrUpdateSystemDocumentMetadata(AddSystemDocumentsDto addSystemDocumentsDto, SystemDocumentEntity systemDocumentEntity) throws TechpubsException {
        try {
            systemDocumentEntity.setDocumentDescription(addSystemDocumentsDto.getDocumentTitle());

            SystemDocumentSiteLookupEntity systemDocumentSiteLookupEntity = new SystemDocumentSiteLookupEntity();
            systemDocumentSiteLookupEntity.setId(UUID.fromString(addSystemDocumentsDto.getDocumentSite()));
            systemDocumentEntity.setSystemDocumentSiteLookupEntity(systemDocumentSiteLookupEntity);

            SystemDocumentTypeLookupEntity systemDocumentTypeLookupEntity = new SystemDocumentTypeLookupEntity();
            systemDocumentTypeLookupEntity.setId(UUID.fromString(addSystemDocumentsDto.getDocumentType()));
            systemDocumentEntity.setSystemDocumentTypeLookupEntity(systemDocumentTypeLookupEntity);

            systemDocumentEntity.setDocumentNumber(addSystemDocumentsDto.getDocumentNumber());

            systemDocumentEntity.setPartNumbers(addSystemDocumentsDto.getPartsAffected());

            systemDocumentEntity.setEmailNotification(addSystemDocumentsDto.getEmailNotification());

            systemDocumentEntity.setCompanySpecific(addSystemDocumentsDto.getCompanySpecific());

            systemDocumentEntity.setRevision(addSystemDocumentsDto.getDocumentRevision());

	        systemDocumentEntity.setPowerDocument(addSystemDocumentsDto.getPowerDocument());

            Date revisionDate = new SimpleDateFormat("yyyy-MM-dd").parse(addSystemDocumentsDto.getDocumentRevisionDate());
            systemDocumentEntity.setRevisionDate(revisionDate);

            Date distributionDate = new SimpleDateFormat("yyyy-MM-dd").parse(addSystemDocumentsDto.getDocumentDistributionDate());
            systemDocumentEntity.setDistributionDate(distributionDate);

            Set<AirframeLookupEntity> airframes = getAirFrameListForSystemDocument(addSystemDocumentsDto);
            systemDocumentEntity.setAirframes(airframes);
            Set<SalesforceCompanyLookupEntity> specificCompanies = null;
            if (addSystemDocumentsDto.getCompanySpecific()) {
                specificCompanies = saveCompanySystemDocument(addSystemDocumentsDto.getSpecificCustomers());
            }
            systemDocumentEntity.setSpecificCompanies(specificCompanies);


            Set<SalesforceCompanyLookupEntity> paidSubscriptions = saveCompanySystemDocument(addSystemDocumentsDto.getPaidSubscriptions());
            systemDocumentEntity.setCompanyPaidSubscription(paidSubscriptions);

        } catch (ParseException p) {
            LOGGER.error("Error occurred due to parsing the date" + p);
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
        } catch (Exception ex) {
            LOGGER.error("Error occurred due to internal issue " + ex);
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
        }
    }

    private void uploadFileForUpdateSystemDocument(AddSystemDocumentsDto addSystemDocumentsDto,
            SystemDocumentEntity systemDocument, String oldFilePath) throws TechpubsException {

        // If a new file is provided, we need to upload it and delete the old one
        try {
            if (addSystemDocumentsDto.getDocumentUploadFile() != null) {
                String newFilePath = uploadFile(addSystemDocumentsDto.getDocumentUploadFile(), addSystemDocumentsDto);
                // NOTE: old file path & new file path might differ in filename only, or the site/type/docnumber might be different as well
                if (!newFilePath.equals(oldFilePath)) {
                    // delete old file
                    deleteFileFromS3Bucket(oldFilePath);
                    // update entity filename & filepath
                    systemDocument.setFileName(addSystemDocumentsDto.getDocumentUploadFile().getOriginalFilename());
                    systemDocument.setS3FilePath(newFilePath);
                }
            } else {
                // If combination of doc site + doc type + doc number has changed (this makes up the filepath),
                // but there's no new file, we need to move the existing file
                String newFilePath = getFilePathForUpload(systemDocument.getFileName(), addSystemDocumentsDto);
                if (!newFilePath.equals(oldFilePath)) {
                    // Move file
                    AmazonS3 amazonS3Client = amazonS3ClientFactory.getS3Client();
                    amazonS3Client.copyObject(new CopyObjectRequest(avSystemsBucketName, oldFilePath, avSystemsBucketName, newFilePath));
                    deleteFileFromS3Bucket(oldFilePath);
                    // Update entity & filepath
                    systemDocument.setS3FilePath(newFilePath);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error updating document file: " + e);
            throw new TechpubsException(TechpubsException.TechpubsAppError.UPLOADING_FILE_TO_S3_ERROR);
        }
    }


    private String uploadFile(MultipartFile documentUploadFile, AddSystemDocumentsDto addSystemDocumentsDto) throws TechpubsException {
        if (documentUploadFile == null) {
            LOGGER.error("No file provided for upload");
            throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }
        LOGGER.info("File upload in progress.{}", documentUploadFile.getOriginalFilename());
        try {
            final File file = TechpubsAppUtil.convertMultiPartFileToFile(documentUploadFile);
            String uniqueFileName = getFilePathForUpload(file.getName(), addSystemDocumentsDto);

            AmazonS3 amazonS3Client = amazonS3ClientFactory.getS3Client();

            LOGGER.info("Uploading file with name " + uniqueFileName);
            final PutObjectRequest putObjectRequest = new PutObjectRequest(avSystemsBucketName, uniqueFileName, file);
            amazonS3Client.putObject(putObjectRequest);

            LOGGER.info("File upload is completed.");
            file.delete();    // To remove the file locally created in the project folder.

            return uniqueFileName;
        } catch (Exception ex) {
            LOGGER.error("Error while uploading file: " + ex);
            throw new TechpubsException(TechpubsException.TechpubsAppError.UPLOADING_FILE_TO_S3_ERROR);
        }
    }

    private String getFilePathForUpload(String filename, AddSystemDocumentsDto addSystemDocumentsDto) throws TechpubsException {
        if (filename == null || filename.isEmpty()) {
            LOGGER.error("No filename provided");
            throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }
        Optional<SystemDocumentTypeLookupEntity> sysTypeOptional = iSystemDocumentTypeLookupData.findById(UUID.fromString(addSystemDocumentsDto.getDocumentType()));
        Optional<SystemDocumentSiteLookupEntity> sysSiteOptional = iSystemDocumentSiteLookupData.findById(UUID.fromString(addSystemDocumentsDto.getDocumentSite()));
        if (sysTypeOptional.isPresent() && sysSiteOptional.isPresent()) {
            SystemDocumentTypeLookupEntity sysType = sysTypeOptional.get();
            SystemDocumentSiteLookupEntity sysSite = sysSiteOptional.get();
            return sysType.getValue() + "/" + sysSite.getValue()
                    + "/" + addSystemDocumentsDto.getDocumentNumber() + "/" + filename;
        } else {
            throw new TechpubsException(TechpubsException.TechpubsAppError.UPLOADING_FILE_TO_S3_ERROR);
        }
    }

    private Set<SalesforceCompanyLookupEntity> saveCompanySystemDocument(List<String> companyIds) throws TechpubsException {
      
        if (companyIds != null && !companyIds.isEmpty()) {
            Set<SalesforceCompanyLookupEntity> companyLookupEntities = new HashSet<>();
            for (String specificCustomerId : companyIds) {
                Optional<SalesforceCompanyLookupEntity> companyEntityOptional = iSalesforceCompanyData.findById(UUID.fromString(specificCustomerId));
                if (companyEntityOptional.isPresent()) {
                    companyLookupEntities.add(companyEntityOptional.get());
                } else {
                    LOGGER.error("No company found for id " + specificCustomerId);
                    throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
                }
            }
            return companyLookupEntities;
        }

        return null;
    }


    private Set<AirframeLookupEntity> getAirFrameListForSystemDocument(AddSystemDocumentsDto addSystemDocumentsDto) throws TechpubsException {

        Set<AirframeLookupEntity> airframes = new HashSet<>();
        List<String> airframeIds = addSystemDocumentsDto.getAircraftPlatforms();
        if (airframeIds == null || airframeIds.isEmpty()) {
            LOGGER.error("No airframe list provided");
            throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }

        for (String airframeId : airframeIds) {
            Optional<AirframeLookupEntity> airframeOptional = iAirframeLookupData.findById(UUID.fromString(airframeId));
            if (airframeOptional.isPresent()) {
                airframes.add(airframeOptional.get());
            } else {
                LOGGER.error("No airframe found for id " + airframeId);
                throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
            }
        }

        return airframes;
    }

    @Override
    @Transactional(rollbackFor = TechpubsException.class)
    @LogExecutionTime
    public void hardDeleteSystemDocument(String id) throws TechpubsException {
        LOGGER.info("Delete system document " + id);
        Optional<SystemDocumentEntity> systemDocumentOptional = iSystemDocumentData.findById(UUID.fromString(id));
        if (systemDocumentOptional.isPresent()) {
            SystemDocumentEntity systemDocument = systemDocumentOptional.get();
            // Delete file from s3
            deleteFileFromS3(systemDocument.getS3FilePath());
            // Delete Metadata from DB
            iSystemDocumentData.delete(systemDocument);
        } else {
            LOGGER.info("Unable to find system document with id " + id);
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
            amazonS3Client.deleteObject(new DeleteObjectRequest(avSystemsBucketName, filepath));
        } catch (AmazonServiceException ex) {
            LOGGER.error("error [" + ex.getMessage() + "] occurred while removing [" + filepath + "]");
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR, "Error deleting file from s3");
        }
    }


}
