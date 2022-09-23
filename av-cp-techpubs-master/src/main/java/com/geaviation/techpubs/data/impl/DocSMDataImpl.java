package com.geaviation.techpubs.data.impl;

import com.geaviation.dss.service.common.exception.TechnicalException;
import com.geaviation.techpubs.data.api.IDocSMData;
import com.geaviation.techpubs.data.util.DataConstants;
import com.geaviation.techpubs.data.util.DataUtil;
import com.geaviation.techpubs.data.util.log.LogExecutionTime;
import com.geaviation.techpubs.models.DocumentItemAssociatedSMModel;
import com.geaviation.techpubs.models.DocumentItemModel;
import com.geaviation.techpubs.models.DocumentItemSMModel;
import com.geaviation.techpubs.models.SubSystem;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

@Component
public class DocSMDataImpl extends AbstractDocMongoSystemData<DocumentItemSMModel> implements
    IDocSMData {

    private static final String RESOURCE_URI = "/techpubs/techdocs/";
    private static final String RESOURCE_URI_TR = "/techpubs/techdocs/sm/sct/";
    private static final String DOCTYPE_VALUE = "SM";

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
    protected DocumentItemSMModel getDocumentItemModel() {
        return new DocumentItemSMModel();
    }

    @Override
    public SubSystem getSubSystem() {
        return SubSystem.SM;
    }

    @Override
    protected void setDocumentSubsystem(Document doc, List<String> modelList,
        DocumentItemSMModel documentItemModel) {

        Document docId = doc.get(DataConstants.DOCUMENTS_ID, Document.class);
        String docType = docId.getString(DataConstants.DOCUMENTS_DOCTYPE);
        String modelVal = createDisplayModelList(modelList,
            castList(doc.get(DataConstants.DOCUMENTS_MODELS), String.class));
        documentItemModel.setModel(modelVal);
        documentItemModel.setTitle(docType);
        documentItemModel
            .setResourceUri(RESOURCE_URI_TR + docType + DataConstants.INIT + DataConstants.QUESTION
                + DataConstants.DOCUMENTS_MODELS + DataConstants.EQUAL + modelVal);
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
            .append(DataConstants.DOCUMENTS_MODELS, new Document(DataConstants.IN, modelList))
            .append(DataConstants.DOCUMENTS_ACLS, new Document(DataConstants.IN, tokenList));

        applyDoctypeQueryParams(match, queryParams);
        applyDateQueryParams(match, queryParams);
        return mongoDb.getCollection(DataConstants.COLLECTION_DOCUMENTS).aggregate(Arrays.asList(
            new Document(DataConstants.MATCH, match),
            new Document(DataConstants.UNWIND, DataConstants.DOCUMENTS_MODELS_VAL),
            new Document(DataConstants.MATCH,
                new Document(DataConstants.DOCUMENTS_MODELS,
                    new Document(DataConstants.IN, modelList))),
            new Document(DataConstants.GROUP,
                new Document(DataConstants.DOCUMENTS_ID,
                    new Document(DataConstants.DOCUMENTS_DOCTYPE,
                        DataConstants.DOCUMENTS_DOCTYPE_VAL))
                    .append(DataConstants.DOCUMENTS_MODELS, new Document(DataConstants.ADD_TO_SET,
                        DataConstants.DOCUMENTS_MODELS_VAL)))));

    }

    @Override
    @LogExecutionTime
    public DocumentItemAssociatedSMModel getDocument(List<String> modelList, List<String> tokenList,
        String fileId) {

        DocumentItemAssociatedSMModel documentItemSM = null;
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
                documentItemSM = new DocumentItemAssociatedSMModel();
                documentItemSM.setId(fileId);
                String title = doc.getString(DataConstants.DOCUMENTS_TITLE);
                documentItemSM
                    .setTitle(title == null || title.isEmpty() ? artifact.getFilename() : title);
                documentItemSM.setContentType(
                    artifact.getMetadata().getString(DataConstants.ARTIFACTS_METADATA_TYPE));
                documentItemSM.setGroupName(doc.getString(DataConstants.DOCUMENTS_GROUPNAME));
                documentItemSM.setResourceUri(RESOURCE_URI + fileId);
                documentItemSM.setDocumentsUri(
                    RESOURCE_URI_TR + doc.getString(DataConstants.DOCUMENTS_DOCTYPE)
                        + DataConstants.ASSOCIATED + DataConstants.QUESTION
                        + DataConstants.DOCUMENTS_MODELS
                        + DataConstants.EQUAL + createDisplayModelList(modelList,
                        castList(doc.get(DataConstants.DOCUMENTS_MODELS), String.class)));
            }
        } catch (Exception e) {
            log.error(DataConstants.LOGGER_GETSMDOCUMENTS, e);
            throw new TechnicalException(DataConstants.LOGGER_GETSMDOCUMENTS, e);
        }
        return documentItemSM;
    }

    @Override
    public String getReleaseDate(DocumentItemModel documentItem) {

        return ((DocumentItemAssociatedSMModel) documentItem).getReleaseDate();

    }

    @Override
    @LogExecutionTime
    public List<DocumentItemAssociatedSMModel> getAssociatedDocumentsSM(List<String> modelList,
        List<String> tokenList,
        String category, Map<String, String> queryParams) {

        List<DocumentItemAssociatedSMModel> documentItemList = new ArrayList<>();
        MongoDatabase mongoDb = mongoClient.getDatabase(DataConstants.DB_TECHPUBS);

        try {
            Document match = new Document(DataConstants.DOCUMENTS_SUBSYSTEM, getSubsystemValue())
                .append(DataConstants.OR, Arrays.asList(
                    new Document(DataConstants.DOCUMENTS_DELETED,
                        new Document(DataConstants.EXISTS, false)),
                    new Document(DataConstants.DOCUMENTS_DELETED,
                        new Document(DataConstants.NE, DataConstants.Y))))
                .append(DataConstants.DOCUMENTS_DOCTYPE, category)
                .append(DataConstants.DOCUMENTS_MODELS, new Document(DataConstants.IN, modelList))
                .append(DataConstants.DOCUMENTS_ACLS, new Document(DataConstants.IN, tokenList));

            // Append optional parameters to query, if applicable.
            applyDoctypeQueryParams(match, queryParams);
            applyDateQueryParams(match, queryParams);

            for (Document doc : mongoDb.getCollection(DataConstants.COLLECTION_DOCUMENTS)
                .find(match)
                .projection(new Document(DataConstants.DOCUMENTS_ID, false)
                    .append(DataConstants.DOCUMENTS_CREATIONDATE, true)
                    .append(DataConstants.DOCUMENTS_FILEID, true)
                    .append(DataConstants.DOCUMENTS_TITLE, true)
                    .append(DataConstants.DOCUMENTS_GROUPNAME, true)
                    .append(DataConstants.DOCUMENTS_DOCTYPE, true))) {
                ObjectId fileRef = (ObjectId) doc.get(DataConstants.DOCUMENTS_FILEID);
                GridFSFile artifact = GridFSBuckets
                    .create(mongoDb, DataConstants.COLLECTION_ARTIFACTS)
                    .find(new Document(DataConstants.DOCUMENTS_ID, fileRef)).first();
                DocumentItemAssociatedSMModel documentItemAssociatedSM = new DocumentItemAssociatedSMModel();
                documentItemAssociatedSM.setId(fileRef.toString());
                documentItemAssociatedSM.setTitle(doc.getString(DataConstants.DOCUMENTS_TITLE));
                documentItemAssociatedSM
                    .setContentType(artifact.getMetadata().getString(DataConstants.TYPE));
                documentItemAssociatedSM
                    .setGroupName(doc.getString(DataConstants.DOCUMENTS_GROUPNAME));
                documentItemAssociatedSM
                    .setReleaseDate(
                        DataUtil.formatDateTime(doc.getDate(DataConstants.DOCUMENTS_CREATIONDATE)));
                documentItemAssociatedSM
                    .setResourceUri(RESOURCE_URI + fileRef.toString() + DataConstants.TYPE_PARAM
                        + getSubSystem().toString().toLowerCase());
                documentItemList.add(documentItemAssociatedSM);
            }
        } catch (Exception e) {
            log.error(DataConstants.LOGGER_ASSOCIATED_SM, e);
            throw new TechnicalException(DataConstants.LOGGER_ASSOCIATED_SM, e);
        }

        return documentItemList;
    }

}
