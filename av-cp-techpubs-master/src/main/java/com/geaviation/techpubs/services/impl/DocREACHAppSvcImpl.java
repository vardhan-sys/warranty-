package com.geaviation.techpubs.services.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.geaviation.techpubs.config.S3Config;
import com.geaviation.techpubs.data.api.IDocMongoData;
import com.geaviation.techpubs.data.api.IDocSubSystemData;
import com.geaviation.techpubs.data.api.IDocTDData;
import com.geaviation.techpubs.data.api.IProgramData;
import com.geaviation.techpubs.data.api.techlib.enginedoc.IEngineDocumentData;
import com.geaviation.techpubs.data.factories.AmazonS3ClientFactory;
import com.geaviation.techpubs.data.util.DataConstants;
import com.geaviation.techpubs.data.util.log.LogExecutionTime;
import com.geaviation.techpubs.exceptions.PdfPrintException;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.*;
import com.geaviation.techpubs.models.techlib.enginedoc.EngineDocumentEntity;
import com.geaviation.techpubs.services.api.IDocCMMApp;
import com.geaviation.techpubs.services.api.IDocREACHApp;
import com.geaviation.techpubs.services.api.IProgramApp;
import com.geaviation.techpubs.services.util.AppConstants;
import com.geaviation.techpubs.services.util.PDFConverter;
import com.geaviation.techpubs.services.util.PDFPrintApp;
import com.geaviation.techpubs.services.util.TechpubsAppUtil;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
@Component
public class DocREACHAppSvcImpl extends AbstractDocSubSystemAppImpl implements IDocREACHApp {


    private static final Logger log = LogManager.getLogger(DocREACHAppSvcImpl.class);

    private static final String REACH_ENGINE_DOCUMENT_TYPE = "REACH";

    @Autowired
    private IProgramApp iProgramApp;


    @Autowired
    private IProgramData iProgramData;

    @Autowired
    private IEngineDocumentData iEngineDocumentData;

    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    private S3Config s3Config;

    @Autowired
    private AmazonS3ClientFactory amazonS3ClientFactory;

    @Value("${engineDocs.bucketName}")
    private String engineDocsBucketName;

    @Override
    public SubSystem getSubSystem() {
        return SubSystem.REACH;
    }

    @Override
    public Map<String, Object> getArtifact(String ssoId, String portalId, String fileId) throws TechpubsException {

        Optional<EngineDocumentEntity> engineDocOptional = iEngineDocumentData.findById(UUID.fromString(fileId));
        if (!engineDocOptional.isPresent()) {
            throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }
        EngineDocumentEntity engineDocument = engineDocOptional.get();

        List<String> documentModels = engineDocument.getEngineModelEntity().stream().map(model -> model.getModel()).collect(Collectors.toList());
        List<String> userAccessModels = getModelListForRequest(ssoId, portalId, null, null, null, null, null, false);
        // At least one of the documentModels must be in the userAccessModels list
        boolean hasAccess = false;
        for (String docModel : documentModels) {
            if (userAccessModels.contains(docModel)) {
                hasAccess = true;
                break;
            }
        }
        if (!hasAccess) {
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        byte[] fileContents = getDocumentFromS3(engineDocument);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, Object> artifact = new HashMap<>();
        artifact.put("content", fileContents);
        artifact.put(AppConstants.FILENAME, engineDocument.getFileName());
        artifact.put(AppConstants.DOWNLOADNAME, engineDocument.getFileName() + "_" + simpleDateFormat.format(engineDocument.getLastUpdatedDate()));
        Map<String, String> metadata = new HashMap<>();
        metadata.put(DataConstants.DOCUMENTS_SUBSYSTEM, "REACH");
        metadata.put(DataConstants.TYPE, "application/pdf");
        artifact.put(DataConstants.METADATA, metadata);

        return artifact;
    }

    @Override
    @LogExecutionTime
    public List<DocumentItemModel> getDocuments(String ssoId, String portalId,
        Map<String, String> searchFilter,
        Map<String, String> queryParams) throws TechpubsException {

        List<DocumentItemModel> docItemList = new ArrayList<>();

        String family = searchFilter.get(AppConstants.FAMILY);
        String model = searchFilter.get(AppConstants.MODEL);
        String aircraft = searchFilter.get(AppConstants.AIRCRAFT);
        String tail = searchFilter.get(AppConstants.TAIL);
        List<String> esnList = new ArrayList<>();
        if (TechpubsAppUtil.isNotNullandEmpty(searchFilter.get(AppConstants.ESN))) {
            esnList = Arrays.asList(searchFilter.get(AppConstants.ESN).split("\\|"));
        }

        log.debug("Input ESN list size-" + esnList.size());

        List<String> modelList = getModelListForRequest(ssoId, portalId, family, model, aircraft, tail, esnList, false);

        // Get documents by engine model list && engineDocumentType = REACH
        List<DocumentItemModel> documents = new ArrayList<>();
        Optional<List<EngineDocumentEntity>> reachEngineDocumentsOptional = iEngineDocumentData.findByDocumentTypeAndEngineModelAndDeleted(REACH_ENGINE_DOCUMENT_TYPE, modelList, false);
        if (reachEngineDocumentsOptional.isPresent()) {
            List<EngineDocumentEntity> reachEngineDocuments = reachEngineDocumentsOptional.get();
            for (EngineDocumentEntity doc : reachEngineDocuments) {
                DocumentItemREACHModel reachModel = new DocumentItemREACHModel();
                reachModel.setId(doc.getId().toString());
                reachModel.setTitle(doc.getDocumentTitle());
                reachModel.setResourceUri("/techpubs/techdocs/" + doc.getId().toString() + "/pdf?type=reach");
                reachModel.setModel(doc.getEngineModelEntity().stream().map(m -> m.getModel()).collect(Collectors.joining(", ")));
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                reachModel.setRevisionDate(simpleDateFormat.format(doc.getIssueDate()));
                documents.add(reachModel);
            }
        }
        return documents;
    }

    @Override
    String setSubSystemResource(DocumentInfoModel documentInfo, DocumentItemModel documentItem,
        Map<String, String> queryParams) {
        return null;
    }

    @Override
    String setFileName() {
        return null;
    }

    private byte[] getDocumentFromS3(EngineDocumentEntity engineDocument) throws TechpubsException {
        try {
            AmazonS3 amazonS3Client = amazonS3ClientFactory.getS3Client();
            S3Object s3Object = amazonS3Client.getObject(new GetObjectRequest(engineDocsBucketName, getS3FilePath(engineDocument)));
            byte[] file = IOUtils.toByteArray(s3Object.getObjectContent());
            s3Object.close();
            return file;
        } catch (IOException | AmazonServiceException e) {
            log.error("Error downloading file for doc: " + engineDocument.getId(), e);
            throw new TechpubsException(TechpubsException.TechpubsAppError.DOWNLOADING_FILE_FROM_S3_ERROR);
        }
    }

    private String getS3FilePath(EngineDocumentEntity engineDocument) {
        return engineDocument.getEngineDocumentTypeLookupEntity().getValue() + '/'
                + engineDocument.getId() + '/' + engineDocument.getFileName();
    }

}
