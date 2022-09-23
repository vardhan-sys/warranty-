package com.geaviation.techpubs.data.impl;

import com.geaviation.dss.service.common.exception.TechnicalException;
import com.geaviation.techpubs.data.api.IDocMongoData;
import com.geaviation.techpubs.data.util.DataConstants;
import com.geaviation.techpubs.data.util.DataUtil;
import com.geaviation.techpubs.data.util.log.LogExecutionTime;
import com.geaviation.techpubs.models.DocumentItemModel;
import com.geaviation.techpubs.models.DocumentItemWSPGModel;
import com.geaviation.techpubs.models.SubSystem;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

@Component
public class DocWSPGDataImpl extends AbstractDocMongoSystemData<DocumentItemWSPGModel> implements
    IDocMongoData {

    private static final String RESOURCE_URI = "/techpubs/techdocs/";
    private static final String RESOURCE_URI_TR = "/techpubs/techdocs/wspgs/tr/";
    private static final String DOCTYPE_VALUE = "WSPG";

    @Override
    protected String getResourceUri() {
        return RESOURCE_URI;
    }

    @Override
    protected String getResourceUritr() {
        return RESOURCE_URI_TR;
    }

    @Override
    protected String getDoctypeValue() {
        return DOCTYPE_VALUE;
    }

    @Override
    protected DocumentItemWSPGModel getDocumentItemModel() {
        return new DocumentItemWSPGModel();
    }

    @Override
    public SubSystem getSubSystem() {
        return SubSystem.WSPG;
    }

    @Override
    protected void setDocumentSubsystem(Document doc, List<String> modelList,
        DocumentItemWSPGModel documentItemModel) {

        documentItemModel.setModel(
            createDisplayModelList(modelList,
                castList(doc.get(DataConstants.DOCUMENTS_MODELS), String.class)));
        documentItemModel
            .setResourceUri(RESOURCE_URI + ((ObjectId) doc.get(DataConstants.DOCUMENTS_FILEID))
                + DataConstants.TYPE_PARAM + getSubSystem().toString().toLowerCase());

        Date relDate = doc.getDate(DataConstants.DOCUMENTS_RELEASEDATE);
        if (relDate == null) {
            relDate = doc.getDate(DataConstants.DOCUMENTS_CREATIONDATE);
        }

        documentItemModel.setReleaseDate(DataUtil.formatDateTime(relDate));
    }

    @Override
    @LogExecutionTime
    public DocumentItemWSPGModel getDocument(List<String> modelList, List<String> tokenList,
        String fileId) {

        DocumentItemWSPGModel documentItemWSPG = null;
        MongoDatabase mongoDb = mongoClient.getDatabase(DataConstants.DB_TECHPUBS);

        try {
            ObjectId id = new ObjectId(fileId);
            Document doc = mongoDb.getCollection(DataConstants.COLLECTION_DOCUMENTS)
                .find(new Document(DataConstants.DOCUMENTS_FILEID, id)
                    .append(DataConstants.DOCUMENTS_MODELS,
                        new Document(DataConstants.IN, modelList)).append(
                        DataConstants.DOCUMENTS_ACLS, new Document(DataConstants.IN, tokenList)))
                .first();

            if (doc != null && !doc.isEmpty()) {

                GridFSFile artifact = GridFSBuckets
                    .create(mongoDb, DataConstants.COLLECTION_ARTIFACTS)
                    .find(new Document(DataConstants.ARTIFACTS_ID, id)).first();
                documentItemWSPG = new DocumentItemWSPGModel();
                documentItemWSPG.setId(fileId);
                String title = doc.getString(DataConstants.DOCUMENTS_TITLE);
                documentItemWSPG
                    .setTitle(title == null || title.isEmpty() ? artifact.getFilename() : title);
                documentItemWSPG.setResourceUri(RESOURCE_URI + fileId);
                documentItemWSPG.setModel(createDisplayModelList(modelList,
                    castList(doc.get(DataConstants.DOCUMENTS_MODELS), String.class)));

                Date relDate = doc.getDate(DataConstants.DOCUMENTS_RELEASEDATE);
                if (relDate == null) {
                    relDate = doc.getDate(DataConstants.DOCUMENTS_CREATIONDATE);
                }
                documentItemWSPG.setReleaseDate(DataUtil.formatDateTime(relDate));
                documentItemWSPG
                    .setContentType(
                        artifact.getMetadata().getString(DataConstants.ARTIFACTS_METADATA_TYPE));
            }
        } catch (Exception e) {
            log.error(DataConstants.LOGGER_GETWSPGDOCUMENTS, e);
            throw new TechnicalException(DataConstants.LOGGER_GETWSPGDOCUMENTS, e);
        }

        return documentItemWSPG;
    }

    @Override
    public String getReleaseDate(DocumentItemModel documentItem) {

        return ((DocumentItemWSPGModel) documentItem).getReleaseDate();

    }

    @Override
    @LogExecutionTime
    public List<DocumentItemModel> getAssociatedDocuments(List<String> modelList,
        List<String> tokenList, String fileId,
        Boolean includeParts) {

        List<DocumentItemModel> documentItemList = new ArrayList<>();

        DocumentItemWSPGModel documentItem = getDocument(modelList, tokenList, fileId);

        if (documentItem != null) {
            documentItemList.add(documentItem);
        }
        return documentItemList;
    }

}
