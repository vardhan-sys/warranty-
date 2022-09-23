package com.geaviation.techpubs.data.impl;

import com.geaviation.dss.service.common.exception.TechnicalException;
import com.geaviation.techpubs.data.api.IDocVIData;
import com.geaviation.techpubs.data.util.DataConstants;
import com.geaviation.techpubs.data.util.DataUtil;
import com.geaviation.techpubs.data.util.log.LogExecutionTime;
import com.geaviation.techpubs.models.DocumentItemModel;
import com.geaviation.techpubs.models.DocumentItemVIModel;
import com.geaviation.techpubs.models.SubSystem;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class DocVIDataImpl extends AbstractDocMongoSystemData<DocumentItemVIModel> implements
    IDocVIData {

    private static final Logger log = LogManager.getLogger(DocVIDataImpl.class);
    private static final String RESOURCE_URI = "/techpubs/techdocs/";
    private static final String RESOURCE_URI_TR = "/techpubs/techdocs/vi/tr/";
    private static final String DOCTYPE_VALUE = "VI";

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
    protected DocumentItemVIModel getDocumentItemModel() {
        return new DocumentItemVIModel();
    }

    @Override
    public SubSystem getSubSystem() {
        return SubSystem.VI;
    }

    @Override
    @LogExecutionTime
    public DocumentItemModel getDocument(List<String> modelList, List<String> tokenList,
        String fileId) {
        DocumentItemVIModel documentItemVI = null;
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
                documentItemVI = new DocumentItemVIModel();
                documentItemVI.setId(fileId);
                documentItemVI.setTitle(doc.getString(DataConstants.DOCUMENTS_TITLE));
                documentItemVI.setDocType(doc.getString(DataConstants.DOCUMENTS_DOCTYPE));
                documentItemVI.setResourceUri(RESOURCE_URI + fileId);
                documentItemVI.setModel(doc.getString(DataConstants.DOCUMENTS_FAMILY));
                documentItemVI
                    .setReleaseDate(
                        DataUtil.formatDateTime(doc.getDate(DataConstants.DOCUMENTS_CREATIONDATE)));
                documentItemVI.setContentType(artifact.getMetadata().getString(DataConstants.TYPE));
            }
        } catch (Exception e) {
            log.error(DataConstants.LOGGER_VIDOC, e);
            throw new TechnicalException(DataConstants.LOGGER_VIDOC, e);
        }

        return documentItemVI;
    }

    @Override
    public String getReleaseDate(DocumentItemModel documentItem) {
        return ((DocumentItemVIModel) documentItem).getReleaseDate();
    }

    @Override
    protected void setDocumentSubsystem(Document document, List<String> modelList,
        DocumentItemVIModel documentItemModel) {

        ObjectId fileRef = (ObjectId) document.get(DataConstants.DOCUMENTS_FILEID);
        documentItemModel.setId(fileRef.toString());
        documentItemModel.setTitle(document.getString(DataConstants.DOCUMENTS_TITLE));
        documentItemModel.setDocType(document.getString(DataConstants.DOCUMENTS_DOCTYPE));
        documentItemModel.setResourceUri(
            RESOURCE_URI + fileRef.toString() + DataConstants.TYPE_PARAM + getSubSystem().toString()
                .toLowerCase());
        documentItemModel.setModel(document.getString(DataConstants.DOCUMENTS_FAMILY));
        documentItemModel
            .setReleaseDate(
                DataUtil.formatDateTime(document.getDate(DataConstants.DOCUMENTS_CREATIONDATE)));

    }

    @Override
    protected MongoIterable<Document> getCollectionResult(MongoDatabase mongoDb,
        List<String> modelList,
        List<String> tokenList, Map<String, String> queryParams) {
        Document match = new Document(DataConstants.DOCUMENTS_SUBSYSTEM, getSubsystemValue())
            .append(DataConstants.OR, Arrays.asList(
                new Document(DataConstants.DOCUMENTS_DELETED,
                    new Document(DataConstants.EXISTS, false)),
                new Document(DataConstants.DOCUMENTS_DELETED,
                    new Document(DataConstants.NE, DataConstants.Y))))
            .append(DataConstants.DOCUMENTS_ACLS, new Document(DataConstants.IN, tokenList));
        applyDoctypeQueryParams(match, queryParams);
        applyDateQueryParams(match, queryParams);
        return mongoDb.getCollection(DataConstants.COLLECTION_DOCUMENTS).find(match);

    }

    @Override
    @LogExecutionTime
    public List<DocumentItemModel> getVIDocumentTypes(List<String> modelList, List<String> tokenList) {
        List<DocumentItemModel> documentItemList = new ArrayList<>();
        MongoDatabase mongoDb = mongoClient.getDatabase(DataConstants.DB_TECHPUBS);

        try {
            Document match = new Document(DataConstants.DOCUMENTS_SUBSYSTEM, getSubsystemValue())
                .append(DataConstants.OR, Arrays.asList(
                    new Document(DataConstants.DOCUMENTS_DELETED,
                        new Document(DataConstants.EXISTS, false)),
                    new Document(DataConstants.DOCUMENTS_DELETED,
                        new Document(DataConstants.NE, DataConstants.Y))))
                .append(DataConstants.DOCUMENTS_ACLS, new Document(DataConstants.IN, tokenList))
                .append(DataConstants.DOCUMENTS_MODELS, new Document(DataConstants.IN, modelList));
            for (Document doc : mongoDb.getCollection(DataConstants.COLLECTION_DOCUMENTS)
                .aggregate(Arrays.asList(
                    new Document(DataConstants.MATCH, match),
                    new Document(DataConstants.GROUP, new Document(DataConstants.DOCUMENTS_ID,
                        new Document(DataConstants.DOCUMENTS_DOCTYPE,
                            DataConstants.DOCUMENTS_DOCTYPE_VAL)))))) {
                DocumentItemModel documentItem = new DocumentItemModel();
                Document docId = doc.get(DataConstants.DOCUMENTS_ID, Document.class);
                String docType = docId.getString(DataConstants.DOCUMENTS_DOCTYPE);
                documentItem.setType(getSubSystem().toString());
                documentItem.setId(docType);
                documentItem.setTitle(docType);
                documentItemList.add(documentItem);
            }
        } catch (Exception e) {
            log.error(DataConstants.LOGGER_VIDOC_TYPES, e);
            throw new TechnicalException(DataConstants.LOGGER_VIDOC_TYPES, e);
        }

        return documentItemList;
    }
}
