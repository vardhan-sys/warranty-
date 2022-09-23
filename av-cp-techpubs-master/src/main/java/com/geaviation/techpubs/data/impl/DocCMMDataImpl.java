package com.geaviation.techpubs.data.impl;

import com.geaviation.dss.service.common.exception.TechnicalException;
import com.geaviation.techpubs.data.api.IDocMongoData;
import com.geaviation.techpubs.data.util.DataConstants;
import com.geaviation.techpubs.data.util.DataUtil;
import com.geaviation.techpubs.data.util.log.LogExecutionTime;
import com.geaviation.techpubs.models.DocumentItemAssociatedCMMModel;
import com.geaviation.techpubs.models.DocumentItemCMMModel;
import com.geaviation.techpubs.models.DocumentItemModel;
import com.geaviation.techpubs.models.SubSystem;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

@Component
public class DocCMMDataImpl extends AbstractDocMongoSystemData<DocumentItemCMMModel> implements
    IDocMongoData {

    private static final String RESOURCE_URI = "/techpubs/techdocs/";
    private static final String RESOURCE_URI_TR = "/techpubs/techdocs/cmms/tr/";
    private static final String DOCTYPE_VALUE = "CMM";

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
    protected DocumentItemCMMModel getDocumentItemModel() {
        return new DocumentItemCMMModel();
    }

    @Override
    public SubSystem getSubSystem() {
        return SubSystem.CMM;
    }

    @Override
    protected void setDocumentSubsystem(Document doc, List<String> modelList,
        DocumentItemCMMModel documentItemModel) {
        String docnbr = doc.getString(DataConstants.DOCUMENTS_DOCNBR);
        documentItemModel.setPublication(docnbr);

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
    public List<DocumentItemModel> getAssociatedDocuments(List<String> modelList,
        List<String> tokenList, String fileId,
        Boolean includeParts) {
        List<DocumentItemModel> documentItemList = new ArrayList<>();

        // Get CMM publication
        DocumentItemAssociatedCMMModel documentItem = getDocument(modelList, tokenList, fileId);

        if (documentItem != null) {
            documentItemList.add(documentItem);
            // Get Associated TRs
            documentItemList.addAll(getTRs(modelList, tokenList, documentItem.getParentId()));
            // Get Associated Parts
            if (includeParts && documentItem.getParts() != null) {
                // derive associated parts document
                DocumentItemAssociatedCMMModel documentItemAssociatedCMM = new DocumentItemAssociatedCMMModel();
                documentItemAssociatedCMM.setId(DataConstants.DOCUMENTS_PARTS);
                documentItemAssociatedCMM.setPublication(documentItem.getPublication());
                documentItemAssociatedCMM
                    .setResourceUri(RESOURCE_URI + DataConstants.RESOURCE_CMM + fileId
                        + DataConstants.RESOURCE_DOCUMENTS_CMM_PARTS);
                documentItemAssociatedCMM
                    .setDocumentUri(
                        RESOURCE_URI + fileId + DataConstants.RESOURCE_DOCUMENTS_ASSOCIATED
                            + DataConstants.TYPE_PARAM + getSubSystem().toString().toLowerCase());
                documentItemAssociatedCMM
                    .setTitle(DataConstants.PART_NUMBER_TEXT + documentItem.getPublication());
                documentItemAssociatedCMM.setModel(documentItem.getModel());
                documentItemAssociatedCMM.setReleaseDate(documentItem.getReleaseDate());
                documentItemAssociatedCMM.setFileType(DataConstants.FIELD_TYPE_HTML);
                documentItemList.add(documentItemAssociatedCMM);
            }
        }
        return documentItemList;
    }

    @Override
    @LogExecutionTime
    public DocumentItemAssociatedCMMModel getDocument(List<String> modelList,
        List<String> tokenList, String fileId) {

        DocumentItemAssociatedCMMModel documentItemAssociatedCMM = null;

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

                documentItemAssociatedCMM = new DocumentItemAssociatedCMMModel();
                documentItemAssociatedCMM.setId(doc.get(DataConstants.DOCUMENTS_FILEID).toString());
                String cmmId;
                if (doc.containsKey(DataConstants.DOCUMENTS_PARENTID)) {
                    cmmId = mongoDb.getCollection(DataConstants.COLLECTION_DOCUMENTS)
                        .find(new Document(DataConstants.DOCUMENTS_ID,
                            doc.getObjectId(DataConstants.DOCUMENTS_PARENTID)))
                        .projection(new Document(DataConstants.DOCUMENTS_FILEID, true)).first()
                        .getObjectId(DataConstants.DOCUMENTS_FILEID).toString();
                    documentItemAssociatedCMM.setParentId(cmmId);
                } else {
                    cmmId = fileId;
                    documentItemAssociatedCMM
                        .setParentId(doc.get(DataConstants.DOCUMENTS_ID).toString());
                }
                documentItemAssociatedCMM
                    .setPublication(doc.getString(DataConstants.DOCUMENTS_DOCNBR));
                documentItemAssociatedCMM.setModel(createDisplayModelList(modelList,
                    castList(doc.get(DataConstants.DOCUMENTS_MODELS), String.class)));
                documentItemAssociatedCMM
                    .setDocumentUri(
                        RESOURCE_URI + cmmId + DataConstants.RESOURCE_DOCUMENTS_ASSOCIATED
                            + DataConstants.TYPE_PARAM + getSubSystem().toString().toLowerCase());
                documentItemAssociatedCMM.setResourceUri(RESOURCE_URI + fileId);

                Date relDate = doc.getDate(DataConstants.DOCUMENTS_RELEASEDATE);
                if (relDate == null) {
                    relDate = doc.getDate(DataConstants.DOCUMENTS_CREATIONDATE);
                }
                documentItemAssociatedCMM.setReleaseDate(DataUtil.formatDateTime(relDate));

                // Parts
                List<Document> partsArray = castList(doc.get(DataConstants.DOCUMENTS_PARTS),
                    Document.class);
                setParts(documentItemAssociatedCMM, partsArray);

                String title = doc.getString(DataConstants.DOCUMENTS_TITLE);

                GridFSFile artifact = GridFSBuckets
                    .create(mongoDb, DataConstants.COLLECTION_ARTIFACTS)
                    .find(new Document(DataConstants.ARTIFACTS_ID, id)).first();
                documentItemAssociatedCMM
                    .setTitle(title == null || title.isEmpty() ? artifact.getFilename() : title);
                documentItemAssociatedCMM
                    .setFileType(
                        artifact.getMetadata().getString(DataConstants.ARTIFACTS_METADATA_TYPE));
                documentItemAssociatedCMM.setFileName(artifact.getFilename());
            }
        } catch (Exception e) {
            log.error("Exception in getCMMDocument", e);
            throw new TechnicalException("getCMMDocument", e);
        }

        return documentItemAssociatedCMM;
    }

    @Override
    public String getReleaseDate(DocumentItemModel documentItem) {

        return ((DocumentItemAssociatedCMMModel) documentItem).getReleaseDate();

    }

    private void setParts(DocumentItemAssociatedCMMModel documentItemAssociatedCMM,
        List<Document> partsArray) {
        if (partsArray != null) {
            List<String[]> parts = new ArrayList<>();
            for (Document part : partsArray) {
                String[] partToAdd = new String[2];
                partToAdd[0] = part.getString(DataConstants.DOCUMENTS_PARTS_GEPARTNBR);
                partToAdd[1] = part.getString(DataConstants.DOCUMENTS_PARTS_VINPARTNBR);
                parts.add(partToAdd);
            }
            documentItemAssociatedCMM.setParts(parts);
        }
    }

    private List<DocumentItemAssociatedCMMModel> getTRs(List<String> modelList,
        List<String> tokenList,
        String parentId) {
        MongoDatabase mongoDb = mongoClient.getDatabase(DataConstants.DB_TECHPUBS);
        List<DocumentItemAssociatedCMMModel> documentItemList = new ArrayList<>();

        try {
            Document match = new Document(DataConstants.DOCUMENTS_SUBSYSTEM, getSubsystemValue())
                .append(DataConstants.OR, Arrays.asList(
                    new Document(DataConstants.DOCUMENTS_DELETED,
                        new Document(DataConstants.EXISTS, false)),
                    new Document(DataConstants.DOCUMENTS_DELETED,
                        new Document(DataConstants.NE, DataConstants.Y))))
                .append(DataConstants.DOCUMENTS_PARENTID, new ObjectId(parentId))
                .append(DataConstants.DOCTYPE, DataConstants.TR)
                .append(DataConstants.DOCUMENTS_MODELS, new Document(DataConstants.IN, modelList))
                .append(DataConstants.DOCUMENTS_ACLS, new Document(DataConstants.IN, tokenList));

            for (Document doc : mongoDb.getCollection(DataConstants.COLLECTION_DOCUMENTS)
                .find(match)
                .projection(new Document(DataConstants.DOCUMENTS_ID, false)
                    .append(DataConstants.DOCUMENTS_DOCNBR, true)
                    .append(DataConstants.DOCUMENTS_CMMPUBNBR, true)
                    .append(DataConstants.DOCUMENTS_MODELS, true)
                    .append(DataConstants.DOCUMENTS_RELEASEDATE, true)
                    .append(DataConstants.DOCUMENTS_TRREASON, true)
                    .append(DataConstants.DOCUMENTS_FILEID, true)
                    .append(DataConstants.DOCUMENTS_TITLE, true)
                    .append(DataConstants.DOCUMENTS_GROUPNAME, true)
                    .append(DataConstants.DOCTYPE, true))) {
                ObjectId fileRef = (ObjectId) doc.get(DataConstants.DOCUMENTS_FILEID);
                GridFSFile artifact = GridFSBuckets
                    .create(mongoDb, DataConstants.COLLECTION_ARTIFACTS)
                    .find(new Document(DataConstants.DOCUMENTS_ID, fileRef)).first();
                String fileName = artifact.getFilename();
                DocumentItemAssociatedCMMModel documentItemAssociatedCMM = new DocumentItemAssociatedCMMModel();
                documentItemAssociatedCMM.setId(doc.get(DataConstants.DOCUMENTS_FILEID).toString());
                documentItemAssociatedCMM
                    .setPublication(doc.getString(DataConstants.DOCUMENTS_CMMPUBNBR));
                documentItemAssociatedCMM
                    .setResourceUri(
                        RESOURCE_URI_TR + doc.get(DataConstants.DOCUMENTS_FILEID).toString());
                StringBuilder sb = new StringBuilder();
                sb.append(doc.getString(DataConstants.DOCTYPE)).append(DataConstants.EMPTY_SPACE)
                    .append(doc.getString(DataConstants.DOCUMENTS_DOCNBR))
                    .append(DataConstants.HYPHEN)
                    .append(
                        DataUtil.formatDateTime(doc.getDate(DataConstants.DOCUMENTS_RELEASEDATE)));
                sb.append(DataConstants.HYPHEN).append(doc.getString(DataConstants.DOCUMENTS_TITLE))
                    .append(DataConstants.HYPHEN)
                    .append(doc.getString(DataConstants.DOCUMENTS_TRREASON));
                documentItemAssociatedCMM.setTitle(sb.toString());
                documentItemAssociatedCMM
                    .setFileType(artifact.getMetadata().getString(DataConstants.TYPE));
                documentItemAssociatedCMM.setFileName(fileName);
                documentItemAssociatedCMM
                    .setGroupName(doc.getString(DataConstants.DOCUMENTS_GROUPNAME));
                documentItemAssociatedCMM.setModel(createDisplayModelList(modelList,
                    castList(doc.get(DataConstants.DOCUMENTS_MODELS), String.class)));
                Date relDate = doc.getDate(DataConstants.DOCUMENTS_RELEASEDATE);
                if (relDate == null) {
                    relDate = doc.getDate(DataConstants.DOCUMENTS_CREATIONDATE);
                }
                documentItemAssociatedCMM.setReleaseDate(DataUtil.formatDateTime(relDate));
                documentItemList.add(documentItemAssociatedCMM);
            }
        } catch (Exception e) {
            log.error(DataConstants.LOGGER_GETCMMTR, e);
            throw new TechnicalException(DataConstants.LOGGER_GETCMMTR, e);
        }

        return documentItemList;
    }

}
