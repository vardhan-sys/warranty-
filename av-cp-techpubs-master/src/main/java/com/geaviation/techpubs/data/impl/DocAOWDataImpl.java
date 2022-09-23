package com.geaviation.techpubs.data.impl;

import com.geaviation.dss.service.common.exception.TechnicalException;
import com.geaviation.techpubs.data.api.IDocMongoData;
import com.geaviation.techpubs.data.util.DataConstants;
import com.geaviation.techpubs.data.util.DataUtil;
import com.geaviation.techpubs.data.util.log.LogExecutionTime;
import com.geaviation.techpubs.models.DocumentItemAOWModel;
import com.geaviation.techpubs.models.DocumentItemAOWRef;
import com.geaviation.techpubs.models.DocumentItemModel;
import com.geaviation.techpubs.models.SubSystem;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

@Component
public class DocAOWDataImpl extends AbstractDocMongoSystemData<DocumentItemAOWModel> implements
    IDocMongoData {

    private static final String RESOURCE_URI = "/techpubs/techdocs/";
    private static final String RESOURCE_URI_TR = "/techpubs/techdocs/aow/tr/";
    private static final String DOCTYPE_VALUE = "AOW";

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
    protected DocumentItemAOWModel getDocumentItemModel() {
        return new DocumentItemAOWModel();
    }

    @Override
    public SubSystem getSubSystem() {
        return SubSystem.AOW;
    }

    @Override
    protected MongoIterable<Document> getCollectionResult(MongoDatabase mongoDb,
        List<String> modelList,
        List<String> tokenList, Map<String, String> queryParams) {

        // Construct default query.
        Document match = new Document(DataConstants.DOCUMENTS_SUBSYSTEM, getSubsystemValue())
            .append(DataConstants.DOCUMENTS_DOCTYPE, getDoctypeValue())
            .append(DataConstants.OR, Arrays.asList(
                new Document(DataConstants.DOCUMENTS_DELETED,
                    new Document(DataConstants.EXISTS, false)),
                new Document(DataConstants.DOCUMENTS_DELETED,
                    new Document(DataConstants.NE, DataConstants.Y))))
            .append(DataConstants.DOCUMENTS_MODELS, new Document(DataConstants.IN, modelList))
            .append(DataConstants.DOCUMENTS_ACLS, new Document(DataConstants.IN, tokenList));

        // Append optional parameters to query, if applicable.
        applyDoctypeQueryParams(match, queryParams);
        applyDateQueryParams(match, queryParams);

        return mongoDb.getCollection(DataConstants.COLLECTION_DOCUMENTS)
            .aggregate(Arrays.asList(new Document(DataConstants.MATCH, match),
                new Document(DataConstants.UNWIND,
                    new Document(DataConstants.PATH, DataConstants.ASSOC_DOCS_VAL)
                        .append(DataConstants.PRESERVE_NULL_AND_EMPTY_ARRAYS, true)),
                new Document(DataConstants.LOOKUP,
                    new Document(DataConstants.FROM, DataConstants.COLLECTION_DOCUMENTS)
                        .append(DataConstants.LOCAL_FIELD, DataConstants.ASSOC_DOCS)
                        .append(DataConstants.FOREIGN_FIELD, DataConstants.DOCUMENTS_ID)
                        .append(DataConstants.AS, DataConstants.REF_DOCS)),
                new Document(DataConstants.PROJECT, new Document(DataConstants.DOCUMENTS_ID, true)
                    .append(DataConstants.DOCUMENTS_DOCNBR, true)
                    .append(DataConstants.DOCUMENTS_MODELS, true)
                    .append(DataConstants.DOCUMENTS_TITLE, true)
                    .append(DataConstants.DOCUMENTS_FILEID, true)
                    .append(DataConstants.DOCUMENTS_REVDATE, true)
                    .append(DataConstants.DOCUMENTS_CREATIONDATE, true)
                    .append(DataConstants.REF_DOCS,
                        new Document(DataConstants.ARRAY_ELEM_AT,
                            Arrays.asList(DataConstants.REF_DOCS_VAL, 0)))),
                new Document(DataConstants.GROUP, new Document("_id",
                    new Document(DataConstants.DOCUMENTS_ID, DataConstants.DOCUMENTS_ID_VAL)
                        .append(DataConstants.DOCUMENTS_DOCNBR, DataConstants.DOCUMENTS_DOCNBR_VAL)
                        .append(DataConstants.DOCUMENTS_MODELS, DataConstants.DOCUMENTS_MODELS_VAL)
                        .append(DataConstants.DOCUMENTS_TITLE, DataConstants.DOCUMENTS_TITLE_VAL)
                        .append(DataConstants.DOCUMENTS_FILEID, DataConstants.DOCUMENTS_FILEID_VAL)
                        .append(DataConstants.DOCUMENTS_REVDATE,
                            DataConstants.DOCUMENTS_REVDATE_VAL)
                        .append(DataConstants.DOCUMENTS_CREATIONDATE,
                            DataConstants.DOCUMENTS_CREATIONDATE_VAL)).append(
                    DataConstants.REF_WIRES,
                    new Document(DataConstants.ADD_TO_SET,
                        new Document(DataConstants.DOCUMENTS_DOCNBR,
                            DataConstants.REF_DOCS_VAL_DOCUMENTS_DOCNBR)
                            .append(DataConstants.DOCUMENTS_FILEID,
                                DataConstants.REF_DOCS_VAL_DOCUMENTS_FILEID)
                            .append(DataConstants.DOCUMENTS_TITLE,
                                DataConstants.REF_DOCS_VAL_DOCUMENTS_TITLE)
                            .append(DataConstants.DOCUMENTS_MODELS,
                                DataConstants.REF_DOCS_VAL_DOCUMENTS_MODELS)
                            .append(DataConstants.DOCUMENTS_REVDATE,
                                DataConstants.REF_DOCS_VAL_DOCUMENTS_REVDATE)
                            .append(DataConstants.DOCUMENTS_CREATIONDATE,
                                DataConstants.REF_DOCS_VAL_DOCUMENTS_CREATIONDATE)))),
                new Document(DataConstants.PROJECT,
                    new Document(DataConstants.DOCUMENTS_ID,
                        DataConstants.DOCUMENTS_ID_VAL_DOCUMENTS_ID)
                        .append(DataConstants.DOCUMENTS_DOCNBR,
                            DataConstants.DOCUMENTS_ID_VAL_DOCUMENTS_DOCNBR)
                        .append(DataConstants.DOCUMENTS_MODELS,
                            DataConstants.DOCUMENTS_ID_VAL_DOCUMENTS_MODELS)
                        .append(DataConstants.DOCUMENTS_TITLE,
                            DataConstants.DOCUMENTS_ID_VAL_DOCUMENTS_TITLE)
                        .append(DataConstants.DOCUMENTS_FILEID,
                            DataConstants.DOCUMENTS_ID_VAL_DOCUMENTS_FILEID)
                        .append(DataConstants.DOCUMENTS_REVDATE,
                            DataConstants.DOCUMENTS_ID_VAL_DOCUMENTS_REVDATE)
                        .append(DataConstants.DOCUMENTS_CREATIONDATE,
                            DataConstants.DOCUMENTS_ID_VAL_DOCUMENTS_CREATIONDATE)
                        .append(DataConstants.REF_WIRES,
                            new Document(DataConstants.COND,
                                Arrays.asList(
                                    new Document(DataConstants.EQ,
                                        Arrays.asList(DataConstants.REF_WIRES_VAL,
                                            Arrays.asList(new Document()))),
                                    null, DataConstants.REF_WIRES_VAL))))));
    }

    @Override
    protected void setDocumentSubsystem(Document doc, List<String> modelList,
        DocumentItemAOWModel documentItemModel) {

        documentItemModel
            .setDocumentsUri(RESOURCE_URI + ((ObjectId) doc.get(DataConstants.DOCUMENTS_FILEID))
                + RESOURCE_ASSOCIATED + DataConstants.TYPE_PARAM + getSubSystem().toString()
                .toLowerCase());
        documentItemModel
            .setResourceUri(RESOURCE_URI + ((ObjectId) doc.get(DataConstants.DOCUMENTS_FILEID))
                + DataConstants.TYPE_PARAM + getSubSystem().toString().toLowerCase());
        documentItemModel.setWireNumber(doc.getString(DataConstants.DOCUMENTS_DOCNBR));

        documentItemModel.setModel(
            createDisplayModelList(modelList,
                castList(doc.get(DataConstants.DOCUMENTS_MODELS), String.class)));

        Date relDate = doc.getDate(DataConstants.DOCUMENTS_REVDATE);
        if (relDate == null) {
            relDate = doc.getDate(DataConstants.DOCUMENTS_CREATIONDATE);
        }
        documentItemModel.setReleaseDate(DataUtil.formatDateTime(relDate));

        List<Document> refWires = castList(doc.get(DataConstants.REF_WIRES), Document.class);
        List<DocumentItemAOWRef> refWireList = null;
        if (refWires != null) {
            refWireList = new ArrayList<>();
            for (Document refDoc : refWires) {
                DocumentItemAOWRef refWire = new DocumentItemAOWRef();
                String refFileId = ((ObjectId) refDoc.get(DataConstants.DOCUMENTS_FILEID))
                    .toString();
                refWire.setId(refFileId);
                refWire.setTitle(refDoc.getString(DataConstants.DOCUMENTS_TITLE));
                refWire.setWireNumber(refDoc.getString(DataConstants.DOCUMENTS_DOCNBR));

                refWire.setModel(createDisplayModelList(modelList,
                    castList(refDoc.get(DataConstants.DOCUMENTS_MODELS), String.class)));

                Date refRelDate = refDoc.getDate(DataConstants.DOCUMENTS_REVDATE);
                if (refRelDate == null) {
                    refRelDate = refDoc.getDate(DataConstants.DOCUMENTS_CREATIONDATE);
                }
                refWire.setReleaseDate(DataUtil.formatDateTime(refRelDate));
                refWire.setResourceUri(
                    RESOURCE_URI + refFileId + DataConstants.TYPE_PARAM + getSubSystem().toString()
                        .toLowerCase());
                refWire.setDocumentsUri(
                    RESOURCE_URI + refFileId + RESOURCE_ASSOCIATED + DataConstants.TYPE_PARAM
                        + getSubSystem().toString().toLowerCase());
                refWireList.add(refWire);
            }
        }

        documentItemModel.setRefWireList(refWireList);
    }

    @Override
    @LogExecutionTime
    public List<DocumentItemModel> getAssociatedDocuments(List<String> modelList,
        List<String> tokenList, String fileId,
        Boolean includeParts) {
        List<DocumentItemModel> documentItemList = new ArrayList<>();

        DocumentItemAOWModel documentItem = getDocument(modelList, tokenList, fileId);
        documentItemList.add(documentItem);

        return documentItemList;
    }

    @Override
    @LogExecutionTime
    public DocumentItemAOWModel getDocument(List<String> modelList, List<String> tokenList,
        String fileId) {

        DocumentItemAOWModel documentItemAOW = null;

        MongoDatabase mongoDb = mongoClient.getDatabase(DataConstants.DB_TECHPUBS);
        try {
            ObjectId id = new ObjectId(fileId);

            // Run query.
            Document doc = mongoDb
                .getCollection(
                    DataConstants.COLLECTION_DOCUMENTS)
                .aggregate(
                    Arrays.asList(
                        new Document(DataConstants.MATCH,
                            new Document(DataConstants.DOCUMENTS_FILEID, id)
                                .append(DataConstants.DOCUMENTS_MODELS,
                                    new Document(DataConstants.IN, modelList))
                                .append(DataConstants.DOCUMENTS_ACLS,
                                    new Document(DataConstants.IN, tokenList))),
                        new Document(DataConstants.UNWIND,
                            new Document(DataConstants.PATH, DataConstants.ASSOC_DOCS_VAL)
                                .append(DataConstants.PRESERVE_NULL_AND_EMPTY_ARRAYS, true)),
                        new Document(DataConstants.LOOKUP,
                            new Document(DataConstants.FROM, DataConstants.DOCUMENT)
                                .append(DataConstants.LOCAL_FIELD, DataConstants.ASSOC_DOCS)
                                .append(DataConstants.FOREIGN_FIELD, DataConstants.DOCUMENTS_ID)
                                .append(DataConstants.AS, DataConstants.REF_DOCS)),
                        new Document(DataConstants.PROJECT,
                            new Document(DataConstants.DOCUMENTS_ID, true)
                                .append(DataConstants.DOCUMENTS_DOCNBR, true)
                                .append(DataConstants.DOCUMENTS_MODELS, true)
                                .append(DataConstants.DOCUMENTS_TITLE, true)
                                .append(DataConstants.DOCUMENTS_FILEID, true)
                                .append(DataConstants.DOCUMENTS_REVDATE, true)
                                .append(DataConstants.DOCUMENTS_CREATIONDATE, true)
                                .append(DataConstants.REF_DOCS,
                                    new Document(DataConstants.ARRAY_ELEM_AT,
                                        Arrays.asList(DataConstants.REF_DOCS_VAL, 0)))),
                        new Document(DataConstants.GROUP, new Document(DataConstants.DOCUMENTS_ID,
                            new Document(DataConstants.DOCUMENTS_ID, DataConstants.DOCUMENTS_ID_VAL)
                                .append(DataConstants.DOCUMENTS_DOCNBR,
                                    DataConstants.DOCUMENTS_DOCNBR_VAL)
                                .append(DataConstants.DOCUMENTS_MODELS,
                                    DataConstants.DOCUMENTS_MODELS_VAL)
                                .append(DataConstants.DOCUMENTS_TITLE,
                                    DataConstants.DOCUMENTS_TITLE_VAL)
                                .append(DataConstants.DOCUMENTS_FILEID,
                                    DataConstants.DOCUMENTS_FILEID_VAL)
                                .append(DataConstants.DOCUMENTS_REVDATE,
                                    DataConstants.DOCUMENTS_REVDATE_VAL)
                                .append(DataConstants.DOCUMENTS_CREATIONDATE,
                                    DataConstants.DOCUMENTS_CREATIONDATE_VAL)).append(
                            DataConstants.REF_WIRES,
                            new Document(DataConstants.ADD_TO_SET,
                                new Document(DataConstants.DOCUMENTS_DOCNBR,
                                    DataConstants.REF_DOCS_VAL_DOCUMENTS_DOCNBR)
                                    .append(DataConstants.DOCUMENTS_FILEID,
                                        DataConstants.REF_DOCS_VAL_DOCUMENTS_FILEID)
                                    .append(DataConstants.DOCUMENTS_TITLE,
                                        DataConstants.REF_DOCS_VAL_DOCUMENTS_TITLE)
                                    .append(DataConstants.DOCUMENTS_MODELS,
                                        DataConstants.REF_DOCS_VAL_DOCUMENTS_MODELS)
                                    .append(DataConstants.DOCUMENTS_REVDATE,
                                        DataConstants.REF_DOCS_VAL_DOCUMENTS_REVDATE)
                                    .append(DataConstants.DOCUMENTS_CREATIONDATE,
                                        DataConstants.REF_DOCS_VAL_DOCUMENTS_CREATIONDATE)))),
                        new Document(DataConstants.PROJECT,
                            new Document(DataConstants.DOCUMENTS_ID,
                                DataConstants.DOCUMENTS_ID_VAL_DOCUMENTS_ID)
                                .append(DataConstants.DOCUMENTS_DOCNBR,
                                    DataConstants.DOCUMENTS_ID_VAL_DOCUMENTS_DOCNBR)
                                .append(DataConstants.DOCUMENTS_MODELS,
                                    DataConstants.DOCUMENTS_ID_VAL_DOCUMENTS_MODELS)
                                .append(DataConstants.DOCUMENTS_TITLE,
                                    DataConstants.DOCUMENTS_ID_VAL_DOCUMENTS_TITLE)
                                .append(DataConstants.DOCUMENTS_FILEID,
                                    DataConstants.DOCUMENTS_ID_VAL_DOCUMENTS_FILEID)
                                .append(DataConstants.DOCUMENTS_REVDATE,
                                    DataConstants.DOCUMENTS_ID_VAL_DOCUMENTS_REVDATE)
                                .append(DataConstants.DOCUMENTS_CREATIONDATE,
                                    DataConstants.DOCUMENTS_ID_VAL_DOCUMENTS_CREATIONDATE)
                                .append(DataConstants.REF_WIRES, new Document(
                                    DataConstants.COND,
                                    Arrays.asList(
                                        new Document(DataConstants.EQ,
                                            Arrays.asList(
                                                DataConstants.REF_WIRES_VAL,
                                                Arrays.asList(
                                                    new Document()))),
                                        null, DataConstants.REF_WIRES_VAL))))))
                .first();
            if (doc != null && !doc.isEmpty()) {
                GridFSFile artifact = GridFSBuckets
                    .create(mongoDb, DataConstants.COLLECTION_ARTIFACTS)
                    .find(new Document(DataConstants.DOCUMENTS_ID, id)).first();
                List<Document> refWires = castList(doc.get(DataConstants.REF_WIRES),
                    Document.class);
                List<DocumentItemAOWRef> refWireList = null;
                if (refWires != null) {
                    refWireList = new ArrayList<>();
                    for (Document refDoc : refWires) {
                        DocumentItemAOWRef refWire = new DocumentItemAOWRef();
                        String refFileId = ((ObjectId) refDoc.get(DataConstants.DOCUMENTS_FILEID))
                            .toString();
                        refWire.setId(refFileId);
                        refWire.setTitle(refDoc.getString(DataConstants.DOCUMENTS_TITLE));
                        refWire.setWireNumber(refDoc.getString(DataConstants.DOCUMENTS_DOCNBR));
                        refWire.setModel(createDisplayModelList(modelList,
                            castList(doc.get(DataConstants.DOCUMENTS_MODELS), String.class)));
                        Date relDate = refDoc.getDate(DataConstants.DOCUMENTS_REVDATE);
                        if (relDate == null) {
                            relDate = refDoc.getDate(DataConstants.DOCUMENTS_CREATIONDATE);
                        }
                        refWire.setReleaseDate(DataUtil.formatDateTime(relDate));
                        refWire.setResourceUri(RESOURCE_URI + refFileId);
                        refWire.setDocumentsUri(RESOURCE_URI + refFileId + DataConstants.ASSOCIATED
                            + DataConstants.TYPE_PARAM + getSubSystem().toString().toLowerCase());
                        refWireList.add(refWire);
                    }
                }
                documentItemAOW = new DocumentItemAOWModel();
                documentItemAOW.setId(fileId);
                documentItemAOW.setTitle(doc.getString(DataConstants.DOCUMENTS_TITLE));
                documentItemAOW.setResourceUri(RESOURCE_URI + fileId);
                documentItemAOW.setWireNumber(doc.getString(DataConstants.DOCUMENTS_DOCNBR));
                documentItemAOW.setModel(createDisplayModelList(modelList,
                    castList(doc.get(DataConstants.DOCUMENTS_MODELS), String.class)));
                Date relDate = doc.getDate(DataConstants.DOCUMENTS_REVDATE);
                if (relDate == null) {
                    relDate = doc.getDate(DataConstants.DOCUMENTS_CREATIONDATE);
                }
                documentItemAOW.setReleaseDate(DataUtil.formatDateTime(relDate));
                documentItemAOW
                    .setContentType(artifact.getMetadata().getString(DataConstants.TYPE));
                documentItemAOW.setRefWireList(refWireList);
                documentItemAOW.setResourceUri(RESOURCE_URI + fileId);
                documentItemAOW.setDocumentsUri(RESOURCE_URI + fileId + DataConstants.ASSOCIATED
                    + DataConstants.TYPE_PARAM + getSubSystem().toString().toLowerCase());
            }
        } catch (Exception e) {
            log.error(DataConstants.LOGGER_GETAOWDOCUMENTS, e);
            throw new TechnicalException(DataConstants.LOGGER_GETAOWDOCUMENTS, e);
        }
        return documentItemAOW;
    }

    @Override
    public String getReleaseDate(DocumentItemModel documentItem) {

        return ((DocumentItemAOWModel) documentItem).getReleaseDate();

    }
}
