package com.geaviation.techpubs.data.impl;

import com.geaviation.dss.service.common.exception.TechnicalException;
import com.geaviation.techpubs.data.api.IDocTPData;
import com.geaviation.techpubs.data.util.DataConstants;
import com.geaviation.techpubs.data.util.DataUtil;
import com.geaviation.techpubs.data.util.log.LogExecutionTime;
import com.geaviation.techpubs.models.DocumentItemAssociatedTPModel;
import com.geaviation.techpubs.models.DocumentItemModel;
import com.geaviation.techpubs.models.DocumentItemTPModel;
import com.geaviation.techpubs.models.ProgramItemModel;
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
public class DocTPDataImpl extends AbstractDocMongoSystemData<DocumentItemTPModel> implements
    IDocTPData {

    private static final String RESOURCE_URI = "/techpubs/techdocs/tps/pgm/";
    private static final String RESOURCE_URI_TR = "/techpubs/techdocs/tp/tr/";
    private static final String DOCTYPE_VALUE = "TP";

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
    protected DocumentItemTPModel getDocumentItemModel() {
        return new DocumentItemTPModel();
    }

    @Override
    public SubSystem getSubSystem() {
        return SubSystem.TP;
    }

    @Override
    protected void setDocumentSubsystem(Document doc, List<String> modelList,
        DocumentItemTPModel documentItemModel) {

        Document docId = doc.get(DataConstants.DOCUMENTS_ID, Document.class);
        String model = docId.getString(DataConstants.DOCUMENTS_MODELS);
        Integer docTypeId = docId.getInteger(DataConstants.DOCUMENTS_DOCTYPE_ID);
        documentItemModel.setId(model + "|" + docTypeId);
        documentItemModel.setTitle(docId.getString(DataConstants.DOCUMENTS_DOCTYPE));
        documentItemModel.setProgramItem(new ProgramItemModel(model));
        documentItemModel.setResourceUri(
            RESOURCE_URI + model + DataConstants.RESOURCE_SCT + docTypeId + RESOURCE_INIT);

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
            new Document(DataConstants.GROUP, new Document(DataConstants.DOCUMENTS_ID,
                new Document(DataConstants.DOCUMENTS_MODELS, DataConstants.DOCUMENTS_MODELS_VAL)
                    .append(DataConstants.DOCUMENTS_DOCTYPE_ID,
                        DataConstants.DOCUMENTS_DOCTYPEID_VAL)
                    .append(DataConstants.DOCUMENTS_DOCTYPE,
                        DataConstants.DOCUMENTS_DOCTYPE_VAL)))));

    }

    @Override
    @LogExecutionTime
    public DocumentItemModel getDocument(List<String> modelList, List<String> tokenList,
        String fileId) {
        MongoDatabase mongoDb = mongoClient.getDatabase(DataConstants.DB_TECHPUBS);
        DocumentItemAssociatedTPModel documentItemAssociatedTP = null;
        try {
            ObjectId id = new ObjectId(fileId);

            Document doc = mongoDb.getCollection(DataConstants.COLLECTION_DOCUMENTS)
                .find(new Document(DataConstants.DOCUMENTS_FILEID, id)
                    .append(DataConstants.DOCUMENTS_MODELS,
                        new Document(DataConstants.IN, modelList)).append(
                        DataConstants.DOCUMENTS_ACLS, new Document(DataConstants.IN, tokenList)))
                .first();

            if (doc != null && !doc.isEmpty()) {
                List<String> models = castList(doc.get(DataConstants.DOCUMENTS_MODELS),
                    String.class);
                String refModel = null;
                for (String mod : modelList) {
                    if (models.contains(mod)) {
                        // If doc has multiple models, pick any that customer
                        // has access to.
                        refModel = mod;
                        break;
                    }
                }
                GridFSFile artifact = GridFSBuckets
                    .create(mongoDb, DataConstants.COLLECTION_ARTIFACTS)
                    .find(new Document(DataConstants.ARTIFACTS_ID, id)).first();
                documentItemAssociatedTP = new DocumentItemAssociatedTPModel();
                documentItemAssociatedTP.setId(fileId);
                documentItemAssociatedTP.setFileTitle(doc.getString(DataConstants.DOCUMENTS_TITLE));
                documentItemAssociatedTP
                    .setContentType(artifact.getMetadata().getString(DataConstants.TYPE));
                documentItemAssociatedTP
                    .setUploadMonthName(doc.getString(DataConstants.DOCUMENTS_UPLOADMONTH));
                documentItemAssociatedTP
                    .setUploadYearNumber(doc.getInteger(DataConstants.DOCUMENTS_UPLOADYEAR));
                documentItemAssociatedTP
                    .setReleaseDate(
                        DataUtil.formatDateTime(doc.getDate(DataConstants.DOCUMENTS_CREATIONDATE)));
                documentItemAssociatedTP.setFileCategoryName(doc.getString(DataConstants.DOCTYPE));
                documentItemAssociatedTP
                    .setConferenceLocation(doc.getString(DataConstants.DOCUMENTS_REVISION));
                documentItemAssociatedTP
                    .setGroupName(doc.getString(DataConstants.DOCUMENTS_GROUPNAME));
                documentItemAssociatedTP.setPdfFileName(artifact.getFilename());
                documentItemAssociatedTP
                    .setResourceUri(DataConstants.RESOURCE_URI_GENERIC + fileId);
                documentItemAssociatedTP.setDocumentsUri(
                    RESOURCE_URI + refModel + DataConstants.SCT + doc
                        .getInteger(DataConstants.DOCUMENTS_DOCTYPE_ID)
                        + DataConstants.RESOURCE_DOCUMENTS_ASSOCIATED);
                documentItemAssociatedTP.setArchiveInd(doc.getString(DataConstants.STATUS));
                documentItemAssociatedTP
                    .setFileCategoryTypeId(doc.getInteger(DataConstants.DOCUMENTS_DOCTYPE_ID));
            }
        } catch (Exception e) {
            log.error(DataConstants.LOGGER_GETTPDOCS, e);
            throw new TechnicalException(DataConstants.LOGGER_GETTPDOCS, e);
        }

        return documentItemAssociatedTP;
    }

    @Override
    public String getReleaseDate(DocumentItemModel documentItem) {

        return ((DocumentItemAssociatedTPModel) documentItem).getReleaseDate();

    }

    @Override
    @LogExecutionTime
    public List<DocumentItemAssociatedTPModel> getAssociatedDocumentsTPScorecard(
        List<String> modelList,
        List<String> tokenList, Map<String, String> queryParams) {

        List<DocumentItemAssociatedTPModel> documentItemList = new ArrayList<>();
        MongoDatabase mongoDb = mongoClient.getDatabase(DataConstants.DB_TECHPUBS);

        try {
            Document filter = new Document();

            // Append optional parameters to query, if applicable.
            applyDoctypeQueryParams(filter, queryParams);
            applyDateQueryParams(filter, queryParams);

            for (Document doc : mongoDb
                .getCollection(
                    DataConstants.COLLECTION_DOCUMENTS)
                .aggregate(Arrays.asList(
                    new Document(DataConstants.MATCH,
                        new Document(DataConstants.DOCUMENTS_SUBSYSTEM, getSubsystemValue())
                            .append(DataConstants.OR,
                                Arrays.asList(
                                    new Document(DataConstants.DOCUMENTS_DELETED,
                                        new Document(DataConstants.EXISTS, false)),
                                    new Document(DataConstants.DOCUMENTS_DELETED,
                                        new Document(DataConstants.NE, DataConstants.Y))))
                            .append(DataConstants.DOCUMENTS_DOCTYPE,
                                DataConstants.FLEET_RELIABILITY_SCORECARD)
                            .append(DataConstants.DOCUMENTS_MODELS,
                                new Document(DataConstants.IN, modelList))
                            .append(DataConstants.DOCUMENTS_ACLS,
                                new Document(DataConstants.IN, tokenList))),
                    new Document(DataConstants.PROJECT,
                        new Document(DataConstants.DOCUMENTS_ID, false)
                            .append(DataConstants.DOCUMENTS_UPLOADYEAR, true)
                            .append(DataConstants.DOCUMENTS_UPLOADMONTH, true)
                            .append(DataConstants.STATUS, true)
                            .append(DataConstants.DOCUMENTS_FILEID, true)
                            .append(DataConstants.DOCUMENTS_CREATIONDATE, true)
                            .append(DataConstants.DOCUMENTS_TITLE, true)
                            .append(DataConstants.DOCUMENTS_DOCTYPE_ID, true)
                            .append(DataConstants.DOCTYPE, true)
                            .append(DataConstants.SORTFIELD, DataUtil.getYearMonthDocument())),
                    new Document(DataConstants.GROUP,
                        new Document(DataConstants.DOCUMENTS_ID, new Document(
                            DataConstants.SORTFIELD,
                            DataConstants.SORTFIELD_VAL)).append(DataConstants.DOC, new Document(
                            DataConstants.ADD_TO_SET,
                            new Document(DataConstants.DOCUMENTS_FILEID,
                                DataConstants.DOCUMENTS_FILEID_VAL)
                                .append(DataConstants.STATUS, DataConstants.STATUS_VAL)
                                .append(DataConstants.DOCUMENTS_CREATIONDATE,
                                    DataConstants.DOCUMENTS_CREATIONDATE_VAL)
                                .append(DataConstants.DOCUMENTS_TITLE,
                                    DataConstants.DOCUMENTS_TITLE_VAL)
                                .append(DataConstants.DOCUMENTS_DOCTYPE_ID,
                                    DataConstants.DOCUMENTS_DOCTYPEID_VAL)
                                .append(DataConstants.DOCUMENTS_DOCTYPE,
                                    DataConstants.DOCUMENTS_DOCTYPE_VAL)))),
                    new Document(DataConstants.SORT_VAL,
                        new Document(DataConstants.ID_SORTFIELD, -1)),
                    new Document(DataConstants.LIMIT, 1),
                    new Document(DataConstants.UNWIND, DataConstants.DOC_VAL),
                    new Document(DataConstants.PROJECT,
                        new Document(DataConstants.DOCUMENTS_ID, false)
                            .append(DataConstants.SORTFIELD, DataConstants.ID_SORTFIELD_VAL)
                            .append(DataConstants.DOCUMENTS_FILEID, DataConstants.DOC_FILE_ID)
                            .append(DataConstants.STATUS, DataConstants.DOC_STATUS)
                            .append(DataConstants.DOCUMENTS_CREATIONDATE,
                                DataConstants.DOC_CREATIONDATE)
                            .append(DataConstants.DOCUMENTS_TITLE, DataConstants.DOC_TITLE)
                            .append(DataConstants.DOCUMENTS_DOCTYPE_ID,
                                DataConstants.DOC_DOCTYPE_ID)
                            .append(DataConstants.DOCUMENTS_DOCTYPE, DataConstants.DOC_DOCTYPE)),
                    new Document(DataConstants.MATCH, filter) // Optional
                    // search
                    // parameters.
                ))) {
                ObjectId fileRef = (ObjectId) doc.get(DataConstants.DOCUMENTS_FILEID);
                GridFSFile artifact = GridFSBuckets
                    .create(mongoDb, DataConstants.COLLECTION_ARTIFACTS)
                    .find(new Document(DataConstants.DOCUMENTS_ID, fileRef)).first();
                String fileName = artifact.getFilename();
                DocumentItemAssociatedTPModel documentItemAssociatedTP = new DocumentItemAssociatedTPModel();
                documentItemAssociatedTP.setId(fileRef.toString());
                documentItemAssociatedTP
                    .setContentType(artifact.getMetadata().getString(DataConstants.TYPE));
                documentItemAssociatedTP
                    .setResourceUri(DataConstants.RESOURCE_URI_GENERIC + fileRef.toString()
                        + DataConstants.TYPE_PARAM + getSubSystem().toString().toLowerCase());
                documentItemAssociatedTP
                    .setReleaseDate(
                        DataUtil.formatDateTime(doc.getDate(DataConstants.DOCUMENTS_CREATIONDATE)));
                documentItemAssociatedTP.setFileTitle(doc.getString(DataConstants.DOCUMENTS_TITLE));
                documentItemAssociatedTP.setPdfFileName(fileName);
                documentItemAssociatedTP.setFileType(DataUtil.getFileType(fileName));
                documentItemAssociatedTP
                    .setFileCategoryName(doc.getString(DataConstants.DOCUMENTS_DOCTYPE));
                documentItemAssociatedTP.setArchiveInd(doc.getString(DataConstants.STATUS));
                documentItemAssociatedTP
                    .setFileCategoryTypeId(doc.getInteger(DataConstants.DOCUMENTS_DOCTYPE_ID));
                documentItemList.add(documentItemAssociatedTP);
            }
        } catch (Exception e) {
            log.error(DataConstants.LOGGER_GETASSOCIATEDDOCUMENTSTPSCORECARD, e);
            throw new TechnicalException(DataConstants.LOGGER_GETASSOCIATEDDOCUMENTSTPSCORECARD, e);
        }

        return documentItemList;
    }

    @Override
    @LogExecutionTime
    public List<DocumentItemAssociatedTPModel> getAssociatedDocumentsTPConfPres(
        List<String> modelList,
        List<String> tokenList, Map<String, String> queryParams) {

        List<DocumentItemAssociatedTPModel> documentItemList = new ArrayList<>();
        MongoDatabase mongoDb = mongoClient.getDatabase(DataConstants.DB_TECHPUBS);

        try {
            Document match = new Document(DataConstants.DOCUMENTS_SUBSYSTEM, getSubsystemValue())
                .append(DataConstants.OR, Arrays.asList(
                    new Document(DataConstants.DOCUMENTS_DELETED,
                        new Document(DataConstants.EXISTS, false)),
                    new Document(DataConstants.DOCUMENTS_DELETED,
                        new Document(DataConstants.NE, DataConstants.Y))))
                .append(DataConstants.DOCUMENTS_DOCTYPE, DataConstants.CONFERENCE_PRESENTATION)
                .append(DataConstants.DOCUMENTS_MODELS, new Document(DataConstants.IN, modelList))
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
                DocumentItemAssociatedTPModel documentItemAssociatedTP = new DocumentItemAssociatedTPModel();
                documentItemAssociatedTP.setId(fileRef.toString());
                documentItemAssociatedTP
                    .setContentType(
                        artifact.getMetadata().getString(DataConstants.ARTIFACTS_METADATA_TYPE));
                documentItemAssociatedTP
                    .setConferenceLocation(doc.getString(DataConstants.DOCUMENTS_REVISION));
                documentItemAssociatedTP
                    .setGroupName(doc.getString(DataConstants.DOCUMENTS_REVISION));
                documentItemAssociatedTP
                    .setFileCategoryName(doc.getString(DataConstants.DOCUMENTS_DOCTYPE));
                documentItemAssociatedTP
                    .setUploadMonthName(doc.getString(DataConstants.DOCUMENTS_UPLOADMONTH));
                documentItemAssociatedTP
                    .setUploadMonthNumber(
                        DataUtil.monthAsInt(doc.getString(DataConstants.DOCUMENTS_UPLOADMONTH)));
                documentItemAssociatedTP
                    .setUploadYearNumber(doc.getInteger(DataConstants.DOCUMENTS_UPLOADYEAR));
                documentItemAssociatedTP
                    .setReleaseDate(
                        DataUtil.formatDateTime(doc.getDate(DataConstants.DOCUMENTS_CREATIONDATE)));
                documentItemAssociatedTP.setFileTitle(doc.getString(DataConstants.DOCUMENTS_TITLE));
                documentItemAssociatedTP
                    .setResourceUri(DataConstants.RESOURCE_URI_GENERIC + fileRef.toString()
                        + DataConstants.TYPE_PARAM + getSubSystem().toString().toLowerCase());
                documentItemAssociatedTP.setPdfFileName(fileName);
                documentItemAssociatedTP.setFileType(DataUtil.getFileType(fileName));
                documentItemAssociatedTP.setArchiveInd(doc.getString(DataConstants.STATUS));
                documentItemAssociatedTP
                    .setFileCategoryTypeId(doc.getInteger(DataConstants.DOCUMENTS_DOCTYPE_ID));
                documentItemList.add(documentItemAssociatedTP);
            }
        } catch (Exception e) {
            log.error(DataConstants.LOGGER_GETASSOCIATEDDOCUMENTSTPCONFPRES, e);
            throw new TechnicalException(DataConstants.LOGGER_GETASSOCIATEDDOCUMENTSTPCONFPRES, e);
        }

        return documentItemList;
    }

    @Override
    @LogExecutionTime
    public List<DocumentItemAssociatedTPModel> getAssociatedDocumentsTPUpdates(
        List<String> modelList,
        List<String> tokenList, Map<String, String> queryParams) {
        MongoDatabase mongoDb = mongoClient.getDatabase(DataConstants.DB_TECHPUBS);
        List<DocumentItemAssociatedTPModel> documentItemList = new ArrayList<>();

        try {
            Document match = new Document(DataConstants.DOCUMENTS_SUBSYSTEM, getSubsystemValue())
                .append(DataConstants.OR, Arrays.asList(
                    new Document(DataConstants.DOCUMENTS_DELETED,
                        new Document(DataConstants.EXISTS, false)),
                    new Document(DataConstants.DOCUMENTS_DELETED,
                        new Document(DataConstants.NE, DataConstants.Y))))
                .append(DataConstants.DOCUMENTS_DOCTYPE, DataConstants.UPDATES)
                .append(DataConstants.DOCUMENTS_MODELS, new Document(DataConstants.IN, modelList))
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
                DocumentItemAssociatedTPModel documentItemAssociatedTP = new DocumentItemAssociatedTPModel();
                documentItemAssociatedTP.setId(fileRef.toString());
                documentItemAssociatedTP
                    .setContentType(
                        artifact.getMetadata().getString(DataConstants.ARTIFACTS_METADATA_TYPE));
                documentItemAssociatedTP
                    .setGroupName(doc.getString(DataConstants.DOCUMENTS_GROUPNAME));
                documentItemAssociatedTP
                    .setFileCategoryName(doc.getString(DataConstants.DOCUMENTS_DOCTYPE));
                documentItemAssociatedTP
                    .setUploadMonthName(doc.getString(DataConstants.DOCUMENTS_UPLOADMONTH));
                documentItemAssociatedTP
                    .setUploadMonthNumber(
                        DataUtil.monthAsInt(doc.getString(DataConstants.DOCUMENTS_UPLOADMONTH)));
                documentItemAssociatedTP
                    .setUploadYearNumber(doc.getInteger(DataConstants.DOCUMENTS_UPLOADYEAR));
                documentItemAssociatedTP
                    .setReleaseDate(
                        DataUtil.formatDateTime(doc.getDate(DataConstants.DOCUMENTS_CREATIONDATE)));
                documentItemAssociatedTP.setFileTitle(doc.getString(DataConstants.DOCUMENTS_TITLE));
                documentItemAssociatedTP
                    .setResourceUri(DataConstants.RESOURCE_URI_GENERIC + fileRef.toString()
                        + DataConstants.TYPE_PARAM + getSubSystem().toString().toLowerCase());
                documentItemAssociatedTP.setPdfFileName(fileName);
                documentItemAssociatedTP.setFileType(DataUtil.getFileType(fileName));
                documentItemAssociatedTP.setArchiveInd(doc.getString(DataConstants.STATUS));
                documentItemAssociatedTP
                    .setFileCategoryTypeId(doc.getInteger(DataConstants.DOCUMENTS_DOCTYPE_ID));
                documentItemList.add(documentItemAssociatedTP);
            }
        } catch (Exception e) {
            log.error(DataConstants.LOGGER_GETASSOCIATEDDOCUMENTSTPUPDATES, e);
            throw new TechnicalException(DataConstants.LOGGER_GETASSOCIATEDDOCUMENTSTPUPDATES, e);
        }
        return documentItemList;
    }

    @Override
    @LogExecutionTime
    public List<DocumentItemAssociatedTPModel> getAssociatedDocumentsTPRefMaterial(
        List<String> modelList,
        List<String> tokenList, Map<String, String> queryParams) {
        MongoDatabase mongoDb = mongoClient.getDatabase(DataConstants.DB_TECHPUBS);
        List<DocumentItemAssociatedTPModel> documentItemList = new ArrayList<>();

        try {
            Document match = new Document(DataConstants.DOCUMENTS_SUBSYSTEM, getSubsystemValue())
                .append(DataConstants.OR, Arrays.asList(
                    new Document(DataConstants.DOCUMENTS_DELETED,
                        new Document(DataConstants.EXISTS, false)),
                    new Document(DataConstants.DOCUMENTS_DELETED,
                        new Document(DataConstants.NE, DataConstants.Y))))
                .append(DataConstants.DOCUMENTS_DOCTYPE, DataConstants.REFERENCE_MATERIALS)
                .append(DataConstants.DOCUMENTS_MODELS, new Document(DataConstants.IN, modelList))
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
                DocumentItemAssociatedTPModel documentItemAssociatedTP = new DocumentItemAssociatedTPModel();
                documentItemAssociatedTP.setId(fileRef.toString());
                documentItemAssociatedTP
                    .setContentType(
                        artifact.getMetadata().getString(DataConstants.ARTIFACTS_METADATA_TYPE));
                documentItemAssociatedTP
                    .setGroupName(doc.getString(DataConstants.DOCUMENTS_GROUPNAME));
                documentItemAssociatedTP
                    .setFileCategoryName(doc.getString(DataConstants.DOCUMENTS_DOCTYPE));
                documentItemAssociatedTP
                    .setReleaseDate(
                        DataUtil.formatDateTime(doc.getDate(DataConstants.DOCUMENTS_CREATIONDATE)));
                documentItemAssociatedTP.setFileTitle(doc.getString(DataConstants.DOCUMENTS_TITLE));
                documentItemAssociatedTP
                    .setResourceUri(DataConstants.RESOURCE_URI_GENERIC + fileRef.toString()
                        + DataConstants.TYPE_PARAM + getSubSystem().toString().toLowerCase());
                documentItemAssociatedTP.setPdfFileName(fileName);
                documentItemAssociatedTP.setFileType(DataUtil.getFileType(fileName));
                documentItemAssociatedTP.setArchiveInd(doc.getString(DataConstants.STATUS));
                documentItemAssociatedTP
                    .setFileCategoryTypeId(doc.getInteger(DataConstants.DOCUMENTS_DOCTYPE_ID));
                documentItemList.add(documentItemAssociatedTP);
            }
        } catch (Exception e) {
            log.error(DataConstants.LOGGER_GETASSOCIATEDDOCUMENTSTPREFMATERIAL, e);
            throw new TechnicalException(DataConstants.LOGGER_GETASSOCIATEDDOCUMENTSTPREFMATERIAL,
                e);
        }
        return documentItemList;
    }

}
