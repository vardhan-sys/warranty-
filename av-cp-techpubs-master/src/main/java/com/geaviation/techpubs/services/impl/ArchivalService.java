package com.geaviation.techpubs.services.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.geaviation.techpubs.config.ArchivalS3Config;
import com.geaviation.techpubs.data.api.techlib.IArchivalCompanyRepo;
import com.geaviation.techpubs.data.api.techlib.IArchivalRepo;
import com.geaviation.techpubs.data.api.techlib.IArchvialDocumentData;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.ArchivalPdfS3DataDAO;
import com.geaviation.techpubs.models.response.ArchivalDocument;
import com.geaviation.techpubs.models.response.ArchivalEntitlement;
import com.geaviation.techpubs.models.techlib.ArchivalCompanyEntity;
import com.geaviation.techpubs.models.techlib.ArchivalDocumentsEntity;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ArchivalService {

    private static final Logger logger = LogManager.getLogger(ArchivalService.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private final IArchivalRepo archivalRepo;
    private final IArchivalCompanyRepo archivalCompanyRepo;
    private final IArchvialDocumentData iArchvialDocumentData;
    private final ArchivalS3Config archivalS3Config;

    @Autowired
    public ArchivalService(IArchivalRepo archivalRepo, IArchivalCompanyRepo archivalCompanyRepo, IArchvialDocumentData iArchvialDocumentData,
                           ArchivalS3Config archivalS3Config) {
        this.archivalRepo = archivalRepo;
        this.archivalCompanyRepo = archivalCompanyRepo;
        this.iArchvialDocumentData = iArchvialDocumentData;
        this.archivalS3Config = archivalS3Config;
        mapper.registerModule(new JavaTimeModule());

    }

    /**
     * Get a list of document entities based on an ICAO code
     *
     * @param icaoCode ICAO code to get archived documents
     * @return
     */
    public ArchivalEntitlement getDocuments(String icaoCode) {
        List<ArchivalDocumentsEntity> entityList = null;

        if (isGeae(icaoCode)) {
            entityList = archivalRepo.findAllDocuments();
        } else {
            entityList = archivalRepo.findByIcaoCode(icaoCode);
        }

        return convertToDto(icaoCode, entityList);
    }

    /**
     * Determine whether or not a company has access to archival documents
     *
     * @param icaoCode
     * @return
     */
    public Boolean hasAccess(String icaoCode) {
        ArchivalCompanyEntity company = archivalCompanyRepo.findByIcaoCode(icaoCode);
        boolean hasAccess;

        if (isGeae(icaoCode)) {
            hasAccess = true;
        } else {
            hasAccess = company != null;
        }

        return hasAccess;
    }

    /**
     * Retrieves a PDF from our archival s3 bucket given a specific model, type, and filename
     *
     * @param model
     * @param type
     * @param fileName
     * @return
     */
    public byte[] getPdf(String model, String type, String fileName) throws TechpubsException, IOException {
        List<ArchivalPdfS3DataDAO> pdf = iArchvialDocumentData.findArchiveDocument(model,type,fileName);
        if(pdf.size() > 1) {
            logger.info("multiple of the same entry were found. Using the first option.");
        }
        return getS3File(pdf.get(0));
    }

    private byte[] getS3File(ArchivalPdfS3DataDAO pdf) throws TechpubsException, IOException {
        S3Object s3Object = null;
        AmazonS3 s3Client = archivalS3Config.ArchivalS3Config();
        String objKey = pdf.getS3Path() + "/" + pdf.getFileName();

        try {
            s3Object = s3Client.getObject(archivalS3Config.getS3Bucket().getBucketName(), objKey);
            String fileSize = FileUtils.byteCountToDisplaySize(s3Object.getObjectMetadata().getContentLength());
            logger.info("File: {}, Size: {}", s3Object.getKey(), fileSize);
        } catch (AmazonServiceException e) {
            logger.error(e.getMessage());
            throw new TechpubsException(TechpubsException.TechpubsAppError.RESOURCE_NOT_FOUND, "S3 Key: " + objKey);
        } catch (SdkClientException e) {
            logger.error(e.getMessage());
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
        }

        byte[] file = IOUtils.toByteArray(s3Object.getObjectContent());
        s3Object.close();
        return file;
    }


    private boolean isGeae(String icaoCode) {
        return "GEAE".equalsIgnoreCase(icaoCode);
    }

    private ArchivalEntitlement convertToDto(String icaoCode, List<ArchivalDocumentsEntity> entities) {
        ArchivalEntitlement entitlement = new ArchivalEntitlement(icaoCode);
        List<ArchivalDocument> documents = mapper.convertValue(entities, new TypeReference<List<ArchivalDocument>>(){});
        entitlement.setDocuments(documents);
        return entitlement;
    }
}
