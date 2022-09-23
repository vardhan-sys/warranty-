package com.geaviation.techpubs.data.impl;

import com.geaviation.dss.service.common.exception.TechnicalException;
import com.geaviation.techpubs.data.api.IDocLLData;
import com.geaviation.techpubs.data.util.DataConstants;
import com.geaviation.techpubs.data.util.DataUtil;
import com.geaviation.techpubs.data.util.log.LogExecutionTime;
import com.geaviation.techpubs.models.DocumentItemAssociatedLLModel;
import com.geaviation.techpubs.models.DocumentItemLLModel;
import com.geaviation.techpubs.models.DocumentItemModel;
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
public class DocLLDataImpl extends AbstractDocMongoSystemData<DocumentItemLLModel> implements
    IDocLLData {

    private static final String RESOURCE_URI = "/techpubs/techdocs/";
    private static final String DOCTYPE_VALUE = "LL";

    @Override
    protected String getResourceUri() {
        return RESOURCE_URI;
    }

    @Override
    protected String getResourceUritr() {
        return null;
    }

    @Override
    protected String getDoctypeValue() {
        return DOCTYPE_VALUE;
    }

    @Override
    protected DocumentItemLLModel getDocumentItemModel() {
        return new DocumentItemLLModel();
    }

    @Override
    public SubSystem getSubSystem() {
        return SubSystem.LL;
    }

    @Override
    protected void setDocumentSubsystem(Document doc, List<String> modelList,
        DocumentItemLLModel documentItemModel) {

        Document docId = doc.get(DataConstants.DOCUMENTS_ID, Document.class);
        Integer docTypeId = docId.getInteger(DataConstants.DOCUMENTS_DOCTYPE_ID);
        documentItemModel.setId(docTypeId.toString());
        documentItemModel.setTitle(docId.getString(DataConstants.DOCUMENTS_DOCTYPE));
        documentItemModel.setResourceUri(
            DataConstants.RESOURCE_URI_LL + RESOURCE_SCT + docTypeId + DataConstants.INIT);

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
        return mongoDb.getCollection(DataConstants.COLLECTION_DOCUMENTS)
            .aggregate(Arrays.asList(new Document(DataConstants.MATCH, match),
                new Document(DataConstants.GROUP, new Document(DataConstants.DOCUMENTS_ID,
                    new Document(DataConstants.DOCUMENTS_DOCTYPE_ID,
                        DataConstants.DOCUMENTS_DOCTYPEID_VAL)
                        .append(DataConstants.DOCUMENTS_DOCTYPE,
                            DataConstants.DOCUMENTS_DOCTYPE_VAL)))));

    }

    @Override
    @LogExecutionTime
    public DocumentItemAssociatedLLModel getDocument(List<String> modelList, List<String> tokenList,
        String fileId) {

        DocumentItemAssociatedLLModel documentItemAssociatedLL = null;

        MongoDatabase mongoDb = mongoClient.getDatabase(DataConstants.DB_TECHPUBS);

        try {
            ObjectId id = new ObjectId(fileId);

            Document doc = mongoDb.getCollection(DataConstants.COLLECTION_DOCUMENTS)
                .find(new Document(DataConstants.DOCUMENTS_FILEID, id)
                    .append(DataConstants.DOCUMENTS_ACLS,
                        new Document(DataConstants.IN, tokenList)))
                .first();

            if (doc != null && !doc.isEmpty()) {
                GridFSFile artifact = GridFSBuckets
                    .create(mongoDb, DataConstants.COLLECTION_ARTIFACTS)
                    .find(new Document(DataConstants.DOCUMENTS_ID, id)).first();
                documentItemAssociatedLL = new DocumentItemAssociatedLLModel();
                documentItemAssociatedLL.setId(fileId);
                documentItemAssociatedLL.setFileTitle(doc.getString(DataConstants.DOCUMENTS_TITLE));
                documentItemAssociatedLL
                    .setContentType(
                        artifact.getMetadata().getString(DataConstants.ARTIFACTS_METADATA_TYPE));
                documentItemAssociatedLL
                    .setUploadMonthName(doc.getString(DataConstants.DOCUMENTS_UPLOADMONTH));
                documentItemAssociatedLL
                    .setUploadYearNumber(doc.getInteger(DataConstants.DOCUMENTS_UPLOADYEAR));
                documentItemAssociatedLL
                    .setReleaseDate(
                        DataUtil.formatDateTime(doc.getDate(DataConstants.DOCUMENTS_CREATIONDATE)));
                documentItemAssociatedLL
                    .setFileCategoryName(doc.getString(DataConstants.DOCUMENTS_DOCTYPE));
                documentItemAssociatedLL
                    .setConferenceLocation(doc.getString(DataConstants.DOCUMENTS_REVISION));
                documentItemAssociatedLL
                    .setGroupName(doc.getString(DataConstants.DOCUMENTS_GROUPNAME));
                documentItemAssociatedLL.setPdfFileName(artifact.getFilename());
                documentItemAssociatedLL.setResourceUri(RESOURCE_URI + fileId);
                documentItemAssociatedLL
                    .setDocumentsUri(DataConstants.RESOURCE_URI_LL + RESOURCE_SCT
                        + doc.getInteger(DataConstants.DOCUMENTS_DOCTYPE_ID)
                        + DataConstants.ASSOCIATED);
                documentItemAssociatedLL.setArchiveInd(doc.getString(DataConstants.STATUS));
                documentItemAssociatedLL
                    .setFileCategoryTypeId(doc.getInteger(DataConstants.DOCUMENTS_DOCTYPE_ID));
            }
        } catch (Exception e) {
            log.error(DataConstants.LOGGER_GETLLDOC, e);
            throw new TechnicalException(DataConstants.LOGGER_GETLLDOC, e);
        }

        return documentItemAssociatedLL;
    }

    @Override
    public String getReleaseDate(DocumentItemModel documentItem) {
        return ((DocumentItemAssociatedLLModel) documentItem).getReleaseDate();
    }

    @Override
    @LogExecutionTime
    public List<DocumentItemAssociatedLLModel> getAssociatedDocumentsLLConfPres(
        List<String> tokenList,
        Map<String, String> queryParams) {
        MongoDatabase mongoDb = mongoClient.getDatabase(DataConstants.DB_TECHPUBS);
        List<DocumentItemAssociatedLLModel> documentItemList = new ArrayList<>();

        try {
            Document match = new Document(DataConstants.DOCUMENTS_SUBSYSTEM, getSubsystemValue())
                .append(DataConstants.OR, Arrays.asList(
                    new Document(DataConstants.DOCUMENTS_DELETED,
                        new Document(DataConstants.EXISTS, false)),
                    new Document(DataConstants.DOCUMENTS_DELETED,
                        new Document(DataConstants.NE, DataConstants.Y))))
                .append(DataConstants.DOCUMENTS_DOCTYPE, DataConstants.CONFERENCE_PRESENTATIONS)
                .append(DataConstants.DOCUMENTS_ACLS, new Document(DataConstants.IN, tokenList));

            // Append optional parameters to query, if applicable.
            applyDoctypeQueryParams(match, queryParams);
            applyDateQueryParams(match, queryParams);

            for (Document doc : mongoDb.getCollection(DataConstants.COLLECTION_DOCUMENTS)
                .find(match)
                .projection(new Document(DataConstants.DOCUMENTS_ID, false)
                    .append(DataConstants.DOCUMENTS_UPLOADYEAR, true)
                    .append(DataConstants.DOCUMENTS_UPLOADMONTH, true)
                    .append(DataConstants.DOCUMENTS_CREATIONDATE, true)
                    .append(DataConstants.STATUS, true)
                    .append(DataConstants.DOCUMENTS_FILEID, true)
                    .append(DataConstants.DOCUMENTS_TITLE, true)
                    .append(DataConstants.DOCUMENTS_REVISION, true)
                    .append(DataConstants.DOCUMENTS_DOCTYPE_ID, true)
                    .append(DataConstants.DOCUMENTS_DOCTYPE, true))) {
                ObjectId fileRef = (ObjectId) doc.get(DataConstants.DOCUMENTS_FILEID);
                GridFSFile artifact = GridFSBuckets
                    .create(mongoDb, DataConstants.COLLECTION_ARTIFACTS)
                    .find(new Document(DataConstants.DOCUMENTS_ID, fileRef)).first();
                String fileName = artifact.getFilename();
                DocumentItemAssociatedLLModel documentItemAssociatedLL = new DocumentItemAssociatedLLModel();
                documentItemAssociatedLL.setId(fileRef.toString());
                documentItemAssociatedLL
                    .setContentType(artifact.getMetadata().getString(DataConstants.TYPE));
                documentItemAssociatedLL
                    .setConferenceLocation(doc.getString(DataConstants.DOCUMENTS_REVISION));
                documentItemAssociatedLL
                    .setGroupName(doc.getString(DataConstants.DOCUMENTS_REVISION));
                documentItemAssociatedLL
                    .setFileCategoryName(doc.getString(DataConstants.DOCUMENTS_DOCTYPE));
                documentItemAssociatedLL
                    .setUploadMonthName(doc.getString(DataConstants.DOCUMENTS_UPLOADMONTH));
                documentItemAssociatedLL
                    .setUploadMonthNumber(
                        DataUtil.monthAsInt(doc.getString(DataConstants.DOCUMENTS_UPLOADMONTH)));
                documentItemAssociatedLL
                    .setUploadYearNumber(doc.getInteger(DataConstants.DOCUMENTS_UPLOADYEAR));
                documentItemAssociatedLL
                    .setReleaseDate(
                        DataUtil.formatDateTime(doc.getDate(DataConstants.DOCUMENTS_CREATIONDATE)));
                documentItemAssociatedLL.setFileTitle(doc.getString(DataConstants.DOCUMENTS_TITLE));
                documentItemAssociatedLL
                    .setResourceUri(DataConstants.RESOURCE_URI_GENERIC + fileRef.toString()
                        + DataConstants.TYPE_PARAM + getSubSystem().toString().toLowerCase());
                documentItemAssociatedLL.setPdfFileName(fileName);
                documentItemAssociatedLL.setFileType(DataUtil.getFileType(fileName));
                documentItemAssociatedLL.setArchiveInd(doc.getString(DataConstants.STATUS));
                documentItemAssociatedLL
                    .setFileCategoryTypeId(doc.getInteger(DataConstants.DOCUMENTS_DOCTYPE_ID));
                documentItemList.add(documentItemAssociatedLL);
            }
        } catch (Exception e) {
            log.error(DataConstants.LOGGER_GETLLCONFPRES, e);
            throw new TechnicalException(DataConstants.LOGGER_GETLLCONFPRES, e);
        }

        return documentItemList;
    }

    @Override
    @LogExecutionTime
    public List<DocumentItemAssociatedLLModel> getAssociatedDocumentsLLUpdates(
        List<String> tokenList,
        Map<String, String> queryParams) {

        MongoDatabase mongoDb = mongoClient.getDatabase(DataConstants.DB_TECHPUBS);
        List<DocumentItemAssociatedLLModel> documentItemList = new ArrayList<>();

        try {
            Document match = new Document(DataConstants.DOCUMENTS_SUBSYSTEM, getSubsystemValue())
                .append(DataConstants.OR, Arrays.asList(
                    new Document(DataConstants.DOCUMENTS_DELETED,
                        new Document(DataConstants.EXISTS, false)),
                    new Document(DataConstants.DOCUMENTS_DELETED,
                        new Document(DataConstants.NE, DataConstants.Y))))
                .append(DataConstants.DOCUMENTS_DOCTYPE, DataConstants.UPDATES)
                .append(DataConstants.DOCUMENTS_ACLS, new Document(DataConstants.IN, tokenList));

            // Append optional parameters to query, if applicable.
            applyDoctypeQueryParams(match, queryParams);
            applyDateQueryParams(match, queryParams);

            for (Document doc : mongoDb.getCollection(DataConstants.COLLECTION_DOCUMENTS)
                .find(match)
                .projection(new Document(DataConstants.DOCUMENTS_ID, false)
                    .append(DataConstants.DOCUMENTS_UPLOADYEAR, true)
                    .append(DataConstants.DOCUMENTS_UPLOADMONTH, true)
                    .append(DataConstants.DOCUMENTS_CREATIONDATE, true)
                    .append(DataConstants.STATUS, true)
                    .append(DataConstants.DOCUMENTS_FILEID, true)
                    .append(DataConstants.DOCUMENTS_TITLE, true)
                    .append(DataConstants.DOCUMENTS_GROUPNAME, true)
                    .append(DataConstants.DOCUMENTS_DOCTYPE_ID, true)
                    .append(DataConstants.DOCUMENTS_DOCTYPE, true))) {
                ObjectId fileRef = (ObjectId) doc.get(DataConstants.DOCUMENTS_FILEID);
                GridFSFile artifact = GridFSBuckets
                    .create(mongoDb, DataConstants.COLLECTION_ARTIFACTS)
                    .find(new Document(DataConstants.DOCUMENTS_ID, fileRef)).first();
                String fileName = artifact.getFilename();
                DocumentItemAssociatedLLModel documentItemAssociatedLL = new DocumentItemAssociatedLLModel();
                documentItemAssociatedLL.setId(fileRef.toString());
                documentItemAssociatedLL
                    .setContentType(artifact.getMetadata().getString(DataConstants.TYPE));
                documentItemAssociatedLL
                    .setGroupName(doc.getString(DataConstants.DOCUMENTS_GROUPNAME));
                documentItemAssociatedLL
                    .setFileCategoryName(doc.getString(DataConstants.DOCUMENTS_DOCTYPE));
                documentItemAssociatedLL
                    .setUploadMonthName(doc.getString(DataConstants.DOCUMENTS_UPLOADMONTH));
                documentItemAssociatedLL
                    .setUploadMonthNumber(
                        DataUtil.monthAsInt(doc.getString(DataConstants.DOCUMENTS_UPLOADMONTH)));
                documentItemAssociatedLL
                    .setUploadYearNumber(doc.getInteger(DataConstants.DOCUMENTS_UPLOADYEAR));
                documentItemAssociatedLL
                    .setReleaseDate(
                        DataUtil.formatDateTime(doc.getDate(DataConstants.DOCUMENTS_CREATIONDATE)));
                documentItemAssociatedLL.setFileTitle(doc.getString(DataConstants.DOCUMENTS_TITLE));
                documentItemAssociatedLL
                    .setResourceUri(DataConstants.RESOURCE_URI_GENERIC + fileRef.toString()
                        + DataConstants.TYPE_PARAM + getSubSystem().toString().toLowerCase());
                documentItemAssociatedLL.setPdfFileName(fileName);
                documentItemAssociatedLL.setFileType(DataUtil.getFileType(fileName));
                documentItemAssociatedLL.setArchiveInd(doc.getString(DataConstants.STATUS));
                documentItemAssociatedLL
                    .setFileCategoryTypeId(doc.getInteger(DataConstants.DOCUMENTS_DOCTYPE_ID));
                documentItemList.add(documentItemAssociatedLL);
            }
        } catch (Exception e) {
            log.error(DataConstants.LOGGER_GETLLUPDATES, e);
            throw new TechnicalException(DataConstants.LOGGER_GETLLUPDATES, e);
        }

        return documentItemList;
    }

    @Override
    @LogExecutionTime
    public List<DocumentItemAssociatedLLModel> getAssociatedDocumentsLLRefMaterial(
        List<String> tokenList,
        Map<String, String> queryParams) {
        MongoDatabase mongoDb = mongoClient.getDatabase(DataConstants.DB_TECHPUBS);
        List<DocumentItemAssociatedLLModel> documentItemList = new ArrayList<>();

        try {
            Document match = new Document(DataConstants.DOCUMENTS_SUBSYSTEM, getSubsystemValue())
                .append(DataConstants.OR, Arrays.asList(
                    new Document(DataConstants.DOCUMENTS_DELETED,
                        new Document(DataConstants.EXISTS, false)),
                    new Document(DataConstants.DOCUMENTS_DELETED,
                        new Document(DataConstants.NE, DataConstants.Y))))
                .append(DataConstants.DOCUMENTS_DOCTYPE, DataConstants.REFERENCE_MATERIALS)
                .append(DataConstants.DOCUMENTS_ACLS, new Document(DataConstants.IN, tokenList));

            // Append optional parameters to query, if applicable.
            applyDoctypeQueryParams(match, queryParams);
            applyDateQueryParams(match, queryParams);

            for (Document doc : mongoDb.getCollection(DataConstants.COLLECTION_DOCUMENTS)
                .find(match)
                .projection(new Document(DataConstants.DOCUMENTS_ID, false)
                    .append(DataConstants.STATUS, true)
                    .append(DataConstants.DOCUMENTS_FILEID, true)
                    .append(DataConstants.DOCUMENTS_CREATIONDATE, true)
                    .append(DataConstants.DOCUMENTS_TITLE, true)
                    .append(DataConstants.DOCUMENTS_GROUPNAME, true)
                    .append(DataConstants.DOCUMENTS_DOCTYPE_ID, true)
                    .append(DataConstants.DOCUMENTS_DOCTYPE, true))) {
                ObjectId fileRef = (ObjectId) doc.get(DataConstants.DOCUMENTS_FILEID);
                GridFSFile artifact = GridFSBuckets
                    .create(mongoDb, DataConstants.COLLECTION_ARTIFACTS)
                    .find(new Document(DataConstants.DOCUMENTS_ID, fileRef)).first();
                String fileName = artifact.getFilename();
                DocumentItemAssociatedLLModel documentItemAssociatedLL = new DocumentItemAssociatedLLModel();
                documentItemAssociatedLL.setId(fileRef.toString());
                documentItemAssociatedLL
                    .setContentType(artifact.getMetadata().getString(DataConstants.TYPE));
                documentItemAssociatedLL
                    .setGroupName(doc.getString(DataConstants.DOCUMENTS_GROUPNAME));
                documentItemAssociatedLL
                    .setFileCategoryName(doc.getString(DataConstants.DOCUMENTS_DOCTYPE));
                documentItemAssociatedLL
                    .setReleaseDate(
                        DataUtil.formatDateTime(doc.getDate(DataConstants.DOCUMENTS_CREATIONDATE)));
                documentItemAssociatedLL.setFileTitle(doc.getString(DataConstants.DOCUMENTS_TITLE));
                documentItemAssociatedLL
                    .setResourceUri(DataConstants.RESOURCE_URI_GENERIC + fileRef.toString()
                        + DataConstants.TYPE_PARAM + getSubSystem().toString().toLowerCase());
                documentItemAssociatedLL.setPdfFileName(fileName);
                documentItemAssociatedLL.setFileType(DataUtil.getFileType(fileName));
                documentItemAssociatedLL.setArchiveInd(doc.getString(DataConstants.STATUS));
                documentItemAssociatedLL
                    .setFileCategoryTypeId(doc.getInteger(DataConstants.DOCUMENTS_DOCTYPE_ID));
                documentItemList.add(documentItemAssociatedLL);
            }
        } catch (Exception e) {
            log.error(DataConstants.LOGGER_GETLLREFMATERIAL, e);
            throw new TechnicalException(DataConstants.LOGGER_GETLLREFMATERIAL, e);
        }

        return documentItemList;
    }
}
