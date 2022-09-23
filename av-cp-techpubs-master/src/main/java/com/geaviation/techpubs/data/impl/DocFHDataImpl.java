package com.geaviation.techpubs.data.impl;

import com.geaviation.dss.service.common.exception.TechnicalException;
import com.geaviation.techpubs.data.api.IDocFHData;
import com.geaviation.techpubs.data.util.DataConstants;
import com.geaviation.techpubs.data.util.DataUtil;
import com.geaviation.techpubs.data.util.DynamicComparator;
import com.geaviation.techpubs.data.util.log.LogExecutionTime;
import com.geaviation.techpubs.models.DocumentItemAssociatedFHModel;
import com.geaviation.techpubs.models.DocumentItemFHModel;
import com.geaviation.techpubs.models.DocumentItemModel;
import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.services.impl.DocFHAppSvcImpl;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

@Component
public class DocFHDataImpl extends AbstractDocMongoSystemData<DocumentItemFHModel> implements
    IDocFHData {

    private static final String RESOURCE_URI = "/techpubs/techdocs/";
    private static final String RESOURCE_URI_TR = "/techpubs/techdocs/fhs/tr/";
    private static final String DOCTYPE_VALUE = "Overall";

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
    protected DocumentItemFHModel getDocumentItemModel() {
        return new DocumentItemFHModel();
    }

    @Override
    public SubSystem getSubSystem() {
        return SubSystem.FH;
    }

    @Override
    protected void setDocumentSubsystem(Document doc, List<String> modelList,
        DocumentItemFHModel documentItemModel) {

        documentItemModel.setProgram(
            createDisplayModelList(modelList,
                castList(doc.get(DataConstants.DOCUMENTS_MODELS), String.class)));
        documentItemModel.setProgramtitle(
            createDisplayModelList(modelList,
                castList(doc.get(DataConstants.DOCUMENTS_MODELS), String.class)));

        Integer month = doc.getInteger(DataConstants.DOCUMENTS_UPLOADMONTH);
        Integer quarter = doc.getInteger(DataConstants.DOCUMENTS_UPLOADQUARTER);
        String mq;
        String mqFmt;
        if (month != null) {
            mq = DataConstants.M;
            mqFmt = (month < 10 ? "0" : "") + month;
        } else {
            mq = DataConstants.Q;
            mqFmt = mq + (quarter < 10 ? "0" : "") + quarter;
        }

        documentItemModel.setYear(doc.getInteger(DataConstants.DOCUMENTS_UPLOADYEAR).toString());
        documentItemModel
            .setResourceUri(RESOURCE_URI + ((ObjectId) doc.get(DataConstants.DOCUMENTS_FILEID))
                + DataConstants.TYPE_PARAM + getSubSystem().toString().toLowerCase());
        documentItemModel.setMonthQuarter(mq);
        documentItemModel.setMonthQuarterDisplay(mqFmt);
    }

    @Override
    public String getReleaseDate(DocumentItemModel documentItem) {

        return ((DocumentItemAssociatedFHModel) documentItem).getReleaseDate();

    }

    @Override
    @LogExecutionTime
    public List<DocumentItemModel> getAssociatedDocuments(List<String> modelList,
        List<String> tokenList, String fileId,
        Boolean includeParts) {

        DocumentItemAssociatedFHModel documentItemAssociatedFH = getDocument(modelList, tokenList,
            fileId);

        // get Associated Documents for the given FH
        List<DocumentItemModel> documentItemList = new ArrayList<>();

        // Get Single Upload Document
        documentItemList.add(documentItemAssociatedFH);

        // Get and Sort Associated Section Documents
        List<DocumentItemModel> documentItemFHSectionList = new ArrayList<>();
        documentItemFHSectionList
            .addAll(getFHSectionDocuments(modelList, tokenList, documentItemAssociatedFH));
        if (documentItemFHSectionList.size() > 1) {
            List<String[]> sortParams = new ArrayList<>();
            sortParams.add(new String[]{DataConstants.DISPLAYORDER, DataConstants.ASC});
            sortDocumentItems((List<DocumentItemModel>) documentItemFHSectionList, sortParams);
        }
        documentItemList.addAll(documentItemFHSectionList);

        // Get and Sort Associated Article Documents
        List<DocumentItemModel> documentItemFHArticleList = new ArrayList<>();
        documentItemFHArticleList
            .addAll(getFHArticleDocuments(modelList, tokenList, documentItemAssociatedFH));
        if (documentItemFHArticleList.size() > 1) {
            List<String[]> sortParams = new ArrayList<>();
            sortParams.add(new String[]{DataConstants.DISPLAYORDER, DataConstants.ASC});
            sortDocumentItems((List<DocumentItemModel>) documentItemFHArticleList, sortParams);
        }
        documentItemList.addAll(documentItemFHArticleList);

        return documentItemList;
    }

    @Override
    @LogExecutionTime
    public DocumentItemAssociatedFHModel getDocument(List<String> modelList, List<String> tokenList,
        String fileId) {
        DocumentItemAssociatedFHModel documentItemAssociatedFH = null;
        try {
            ObjectId id = new ObjectId(fileId);

            MongoDatabase mongoDb = mongoClient.getDatabase(DataConstants.DB_TECHPUBS);

            Document doc = mongoDb.getCollection(DataConstants.COLLECTION_DOCUMENTS)
                .find(new Document(DataConstants.DOCUMENTS_FILEID, id)
                    .append(DataConstants.DOCUMENTS_MODELS,
                        new Document(DataConstants.IN, modelList)).append(
                        DataConstants.DOCUMENTS_ACLS, new Document(DataConstants.IN, tokenList)))
                .first();

            if (doc != null && !doc.isEmpty()) {
                GridFSFile artifact = GridFSBuckets
                    .create(mongoDb, DataConstants.COLLECTION_ARTIFACTS)
                    .find(new Document(DataConstants.DOCUMENTS_ID, id)).first();
                String fileName = artifact.getFilename();
                Integer month = doc.getInteger(DataConstants.DOCUMENTS_UPLOADMONTH);
                Integer quarter = doc.getInteger(DataConstants.DOCUMENTS_UPLOADQUARTER);
                String mq;
                String mqFmt;
                Integer mqVal;
                if (month != null) {
                    mqVal = month;
                    mq = DataConstants.M;
                    mqFmt = (month < 10 ? "0" : "") + month;
                } else {
                    mqVal = quarter;
                    mq = DataConstants.Q;
                    mqFmt = mq + (quarter < 10 ? "0" : "") + quarter;
                }
                documentItemAssociatedFH = new DocumentItemAssociatedFHModel();
                documentItemAssociatedFH.setId(fileId);
                documentItemAssociatedFH.setSeqId(1);
                documentItemAssociatedFH.setModel(createDisplayModelList(modelList,
                    castList(doc.get(DataConstants.DOCUMENTS_MODELS), String.class)));
                documentItemAssociatedFH.setSection(DataConstants.FHS);
                documentItemAssociatedFH.setMonthQuarter(mq);
                documentItemAssociatedFH.setMonthQuarterDisplay(mqFmt);
                documentItemAssociatedFH.setMnthQtrNumber(mqVal);
                documentItemAssociatedFH
                    .setYearNum(doc.getInteger(DataConstants.DOCUMENTS_UPLOADYEAR));
                documentItemAssociatedFH
                    .setReleaseDate(
                        DataUtil.formatDateTime(doc.getDate(DataConstants.DOCUMENTS_CREATIONDATE)));
                documentItemAssociatedFH.setFileName(fileName);
                documentItemAssociatedFH
                    .setContentType(artifact.getMetadata().getString(DataConstants.TYPE));
                documentItemAssociatedFH.setResourceUri(RESOURCE_URI + fileId); // Only
                // valid
                // for
                // Overall
                // doc.
                documentItemAssociatedFH
                    .setDocumentsUri(RESOURCE_URI + fileId + DataConstants.ASSOCIATED
                        + DataConstants.TYPE_PARAM + getSubSystem().toString()
                        .toLowerCase()); // Only
                // valid
                // for
                // Overall
                // doc.
                documentItemAssociatedFH.setTitle(doc.getString(DataConstants.DOCUMENTS_TITLE));
                documentItemAssociatedFH.setFileType(getFileType(fileName));

            }
        } catch (Exception e) {
            log.error(DataConstants.LOGGER_GETFHDOC, e);
            throw new TechnicalException(DataConstants.LOGGER_GETFHDOC, e);
        }

        return documentItemAssociatedFH;
    }

    public List<DocumentItemAssociatedFHModel> getFHSectionDocuments(List<String> modelList,
        List<String> tokenList,
        DocumentItemAssociatedFHModel documentItemAssociatedFH) {
        MongoDatabase mongoDb = mongoClient.getDatabase(DataConstants.DB_TECHPUBS);
        List<DocumentItemAssociatedFHModel> documentItemList = new ArrayList<>();
        try {
            for (Document doc : mongoDb
                .getCollection(
                    DataConstants.COLLECTION_DOCUMENTS)
                .aggregate(Arrays.asList(
                    new Document(DataConstants.MATCH,
                        new Document(DataConstants.DOCUMENTS_SUBSYSTEM, getSubsystemValue())
                            .append(DataConstants.AND,
                                Arrays.asList(
                                    new Document(DataConstants.OR, Arrays.asList(
                                        new Document(DataConstants.DOCUMENTS_DELETED,
                                            new Document(DataConstants.EXISTS, false)),
                                        new Document(
                                            DataConstants.DOCUMENTS_DELETED,
                                            new Document(DataConstants.NE,
                                                DataConstants.Y)))),
                                    new Document(
                                        DataConstants.OR,
                                        Arrays.asList(
                                            new Document(
                                                DataConstants.M
                                                    .equals(documentItemAssociatedFH
                                                        .getMonthQuarter())
                                                    ? DataConstants.DOCUMENTS_UPLOADMONTH
                                                    : DataConstants.DOCUMENTS_UPLOADQUARTER,
                                                documentItemAssociatedFH
                                                    .getMnthQtrNumber()),
                                            new Document(
                                                DataConstants.DOCUMENTS_DOCTYPE,
                                                DataConstants.NEWSLETTER))),
                                    new Document(DataConstants.DOCUMENTS_MODELS,
                                        new Document(DataConstants.IN, modelList)),
                                    new Document(DataConstants.DOCUMENTS_MODELS,
                                        new Document(DataConstants.IN,
                                            Arrays.asList(documentItemAssociatedFH
                                                .getModel().split(","))))))
                            .append(DataConstants.DOCUMENTS_UPLOADYEAR,
                                documentItemAssociatedFH.getYearNum())
                            .append(DataConstants.DOCUMENTS_ACLS,
                                new Document(DataConstants.IN, tokenList))
                            .append(DataConstants.DOCUMENTS_DOCTYPE, new Document(DataConstants.IN,
                                Arrays.asList(DataConstants.NEWSLETTER, DataConstants.OPERATION,
                                    DataConstants.DOCUMENTS, DataConstants.STATISTICS)))),
                    new Document(DataConstants.PROJECT,
                        new Document(DataConstants.DOCUMENTS_ID, false)
                            .append(DataConstants.DOCUMENTS_FILEID, true)
                            .append(DataConstants.DOCUMENTS_MODELS, true)
                            .append(DataConstants.DOCUMENTS_UPLOADYEAR, true)
                            .append(DataConstants.DOCUMENTS_UPLOADMONTH, true)
                            .append(DataConstants.DOCUMENTS_UPLOADQUARTER, true)
                            .append(DataConstants.DOCUMENTS_CREATIONDATE, true)
                            .append(DataConstants.DOCUMENTS_FAMILY, true)
                            .append(DataConstants.DOCUMENTS_DOCTYPE, true)
                            .append(DataConstants.SORTFIELD,
                                new Document(DataConstants.COND, Arrays.asList(
                                    new Document(DataConstants.EQ,
                                        Arrays.asList(DataConstants.DOCUMENTS_DOCTYPE_VAL,
                                            DataConstants.STATISTICS)),
                                    1,
                                    new Document(DataConstants.COND, Arrays.asList(
                                        new Document(DataConstants.EQ,
                                            Arrays.asList(DataConstants.DOCUMENTS_DOCTYPE_VAL,
                                                DataConstants.OPERATION)),
                                        2,
                                        new Document(DataConstants.COND, Arrays.asList(
                                            new Document(DataConstants.EQ, Arrays.asList(
                                                DataConstants.DOCUMENTS_DOCTYPE_VAL,
                                                DataConstants.DOCUMENTS)),
                                            3,
                                            new Document(DataConstants.COND,
                                                Arrays.asList(
                                                    new Document(DataConstants.EQ,
                                                        Arrays.asList(
                                                            DataConstants.DOCUMENTS_DOCTYPE_VAL,
                                                            DataConstants.NEWSLETTER)),
                                                    4, 99))))))))))))) {
                ObjectId fileRef = (ObjectId) doc.get(DataConstants.DOCUMENTS_FILEID);
                GridFSFile artifact = GridFSBuckets
                    .create(mongoDb, DataConstants.COLLECTION_ARTIFACTS)
                    .find(new Document(DataConstants.DOCUMENTS_ID, fileRef)).first();
                String fileName = artifact.getFilename();
                Integer year = doc.getInteger(DataConstants.DOCUMENTS_UPLOADYEAR);
                Integer month = doc.getInteger(DataConstants.DOCUMENTS_UPLOADMONTH);
                Integer quarter = doc.getInteger(DataConstants.DOCUMENTS_UPLOADQUARTER);
                String mq;
                String mqFmt;
                Integer mqVal;
                if (month != null) {
                    mqVal = month;
                    mq = DataConstants.M;
                    mqFmt = (month < 10 ? "0" : "") + month;
                } else {
                    mqVal = quarter;
                    mq = DataConstants.Q;
                    mqFmt = mq + (quarter < 10 ? "0" : "") + quarter;
                }
                String fam = doc.getString(DataConstants.DOCUMENTS_FAMILY);
                String sn = doc.getString(DataConstants.DOCUMENTS_DOCTYPE);
                DocumentItemAssociatedFHModel documentItemAssociatedFHSection = new DocumentItemAssociatedFHModel();
                documentItemAssociatedFHSection.setId(fileRef.toString());
                documentItemAssociatedFHSection.setSeqId(1);
                documentItemAssociatedFHSection.setModel(createDisplayModelList(modelList,
                    castList(doc.get(DataConstants.DOCUMENTS_MODELS), String.class)));
                documentItemAssociatedFHSection.setSection(sn);
                documentItemAssociatedFHSection.setMonthQuarter(mq);
                documentItemAssociatedFHSection.setMonthQuarterDisplay(mqFmt);
                documentItemAssociatedFHSection.setMnthQtrNumber(mqVal);
                documentItemAssociatedFHSection.setYearNum(year);
                documentItemAssociatedFHSection
                    .setReleaseDate(
                        DataUtil.formatDateTime(doc.getDate(DataConstants.DOCUMENTS_CREATIONDATE)));
                documentItemAssociatedFHSection.setFileName(fileName);
                documentItemAssociatedFHSection.setResourceUri(DataConstants.RESOURCE_URI_FHS
                    + documentItemAssociatedFH.getId() + DataConstants.SCT + fileRef.toString());
                StringBuilder sb = new StringBuilder(fam);
                sb.append(" ").append(sn);
                if (DataConstants.NEWSLETTER.equals(sn)) {
                    sb.append(" ").append(year).append("-").append(mqFmt);
                }
                documentItemAssociatedFHSection.setTitle(sb.toString());
                documentItemAssociatedFHSection.setFileType(getFileType(fileName));
                documentItemAssociatedFHSection
                    .setDisplayOrder(doc.getInteger(DataConstants.SORTFIELD));
                documentItemList.add(documentItemAssociatedFHSection);
            }
        } catch (Exception e) {
            log.error(DataConstants.LOGGER_GETFHSECDOC, e);
            throw new TechnicalException(DataConstants.LOGGER_GETFHSECDOC, e);
        }

        return documentItemList;
    }

    public List<DocumentItemAssociatedFHModel> getFHArticleDocuments(List<String> modelList,
        List<String> tokenList,
        DocumentItemAssociatedFHModel documentItemAssociatedFH) {
        MongoDatabase mongoDb = mongoClient.getDatabase(DataConstants.DB_TECHPUBS);
        List<DocumentItemAssociatedFHModel> documentItemList = new ArrayList<>();
        try {
            for (Document doc : mongoDb.getCollection(DataConstants.COLLECTION_DOCUMENTS)
                .find(new Document(DataConstants.DOCUMENTS_SUBSYSTEM, getSubsystemValue())
                    .append(DataConstants.OR,
                        Arrays.asList(
                            new Document(DataConstants.DOCUMENTS_DELETED,
                                new Document(DataConstants.EXISTS, false)),
                            new Document(DataConstants.DOCUMENTS_DELETED,
                                new Document(DataConstants.NE, DataConstants.Y))))
                    .append(DataConstants.M.equals(documentItemAssociatedFH.getMonthQuarter())
                            ? DataConstants.DOCUMENTS_UPLOADMONTH
                            : DataConstants.DOCUMENTS_UPLOADQUARTER,
                        documentItemAssociatedFH.getMnthQtrNumber())
                    .append(DataConstants.DOCUMENTS_UPLOADYEAR,
                        documentItemAssociatedFH.getYearNum())
                    .append(DataConstants.AND,
                        Arrays.asList(new Document(DataConstants.DOCUMENTS_MODELS,
                                new Document(DataConstants.IN, modelList)),
                            new Document(DataConstants.DOCUMENTS_MODELS,
                                new Document(DataConstants.IN,
                                    Arrays
                                        .asList(documentItemAssociatedFH.getModel().split(","))))))
                    .append(DataConstants.DOCUMENTS_ACLS, new Document(DataConstants.IN, tokenList))
                    .append(DataConstants.DOCUMENTS_DOCTYPE, new Document(DataConstants.IN,
                        Arrays.asList(DataConstants.GENERAL, DataConstants.SHOP,
                            DataConstants.LINE))))) {
                ObjectId fileRef = (ObjectId) doc.get(DataConstants.DOCUMENTS_FILEID);
                GridFSFile artifact = GridFSBuckets
                    .create(mongoDb, DataConstants.COLLECTION_ARTIFACTS)
                    .find(new Document(DataConstants.DOCUMENTS_ID, fileRef)).first();
                String fileName = artifact.getFilename();
                Integer month = doc.getInteger(DataConstants.DOCUMENTS_UPLOADMONTH);
                Integer quarter = doc.getInteger(DataConstants.DOCUMENTS_UPLOADQUARTER);
                String mq;
                String mqFmt;
                Integer mqVal;
                if (month != null) {
                    mqVal = month;
                    mq = DataConstants.M;
                    mqFmt = (month < 10 ? "0" : "") + month;
                } else {
                    mqVal = quarter;
                    mq = DataConstants.Q;
                    mqFmt = mq + (quarter < 10 ? "0" : "") + quarter;
                }
                DocumentItemAssociatedFHModel documentItemAssociatedFHArticle = new DocumentItemAssociatedFHModel();
                documentItemAssociatedFHArticle.setId(fileRef.toString());
                documentItemAssociatedFHArticle.setSeqId(1);
                documentItemAssociatedFHArticle.setModel(createDisplayModelList(modelList,
                    castList(doc.get(DataConstants.DOCUMENTS_MODELS), String.class)));
                documentItemAssociatedFHArticle
                    .setSection(doc.getString(DataConstants.DOCUMENTS_DOCTYPE));
                documentItemAssociatedFHArticle.setMonthQuarter(mq);
                documentItemAssociatedFHArticle.setMonthQuarterDisplay(mqFmt);
                documentItemAssociatedFHArticle.setMnthQtrNumber(mqVal);
                documentItemAssociatedFHArticle
                    .setYearNum(doc.getInteger(DataConstants.DOCUMENTS_UPLOADYEAR));
                documentItemAssociatedFHArticle
                    .setReleaseDate(
                        DataUtil.formatDateTime(doc.getDate(DataConstants.DOCUMENTS_CREATIONDATE)));
                documentItemAssociatedFHArticle.setFileName(fileName);
                documentItemAssociatedFHArticle.setResourceUri(DataConstants.RESOURCE_URI_FHS
                    + documentItemAssociatedFH.getId() + DataConstants.ART + fileRef.toString());
                documentItemAssociatedFHArticle
                    .setTitle(doc.getString(DataConstants.DOCUMENTS_TITLE));
                documentItemAssociatedFHArticle.setFileType(getFileType(fileName));
                documentItemAssociatedFHArticle
                    .setDisplayOrder(doc.getInteger(DataConstants.DOC_SEQ));
                documentItemList.add(documentItemAssociatedFHArticle);
            }
        } catch (Exception e) {
            log.error(DataConstants.LOGGER_GETFHARTDOC, e);
            throw new TechnicalException(DataConstants.LOGGER_GETFHARTDOC, e);
        }

        return documentItemList;
    }

    protected static <T> List<T> castList(Object obj, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        if (obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return result;
    }

    private String getFileType(String filename) {
        String fileType = "";

        if (filename != null) {
            String fileExtension = FilenameUtils.getExtension(filename);
            if ("html".equalsIgnoreCase(fileExtension) || "htm".equalsIgnoreCase(fileExtension)) {
                fileType = "HTML";
            } else {
                fileType = (fileExtension == null ? "" : fileExtension.toUpperCase());
            }
        }

        return fileType;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void sortDocumentItems(List<DocumentItemModel> docItemList,
        List<String[]> sortParamList) {
        // Perform sort if required
        if (docItemList == null || docItemList.isEmpty()) {
            return;
        }

        Class<?> clazz = docItemList.get(0).getClass();
        ComparatorChain comparatorChain = new ComparatorChain();

        for (String[] sortParams : sortParamList) {
            comparatorChain.addComparator(
                new DynamicComparator(clazz, sortParams[0],
                    ("asc".equals(sortParams[1]) ? true : false)));
        }
        if (comparatorChain.size() > 0) {
            Collections.sort(docItemList, comparatorChain);
        }
    }

    @Override
    @LogExecutionTime
    public DocumentItemAssociatedFHModel getFHDocumentSCT(List<String> modelList,
        List<String> tokenList,
        String overallFileId, String fileId) {
        DocumentItemAssociatedFHModel documentItemAssociatedFH = null;
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
                    .find(new Document(DataConstants.DOCUMENTS_ID, id)).first();
                String fileName = artifact.getFilename();
                Integer year = doc.getInteger(DataConstants.DOCUMENTS_UPLOADYEAR);
                Integer month = doc.getInteger(DataConstants.DOCUMENTS_UPLOADMONTH);
                Integer quarter = doc.getInteger(DataConstants.DOCUMENTS_UPLOADQUARTER);
                String mq;
                String mqFmt;
                Integer mqVal;
                if (month != null) {
                    mqVal = month;
                    mq = DataConstants.M;
                    mqFmt = (month < 10 ? "0" : "") + month;
                } else {
                    mqVal = quarter;
                    mq = DataConstants.Q;
                    mqFmt = mq + (quarter < 10 ? "0" : "") + quarter;
                }
                String fam = doc.getString(DataConstants.DOCUMENTS_FAMILY);
                String sn = doc.getString(DataConstants.DOCUMENTS_DOCTYPE);
                documentItemAssociatedFH = new DocumentItemAssociatedFHModel();
                documentItemAssociatedFH.setId(fileId);
                documentItemAssociatedFH.setSeqId(1);
                documentItemAssociatedFH.setModel(createDisplayModelList(modelList,
                    castList(doc.get(DataConstants.DOCUMENTS_MODELS), String.class)));
                documentItemAssociatedFH.setSection(sn);
                documentItemAssociatedFH.setMonthQuarter(mq);
                documentItemAssociatedFH.setMonthQuarterDisplay(mqFmt);
                documentItemAssociatedFH.setMnthQtrNumber(mqVal);
                documentItemAssociatedFH.setYearNum(year);
                documentItemAssociatedFH
                    .setReleaseDate(
                        DataUtil.formatDateTime(doc.getDate(DataConstants.DOCUMENTS_CREATIONDATE)));
                documentItemAssociatedFH.setFileName(fileName);
                documentItemAssociatedFH
                    .setContentType(artifact.getMetadata().getString(DataConstants.TYPE));
                documentItemAssociatedFH
                    .setResourceUri(
                        DataConstants.RESOURCE_URI_FHS + overallFileId + DataConstants.SCT
                            + fileId);
                documentItemAssociatedFH
                    .setDocumentsUri(
                        RESOURCE_URI + overallFileId + DataConstants.RESOURCE_DOCUMENTS_ASSOCIATED
                            + DataConstants.TYPE_PARAM + getSubSystem().toString().toLowerCase());
                StringBuilder sb = new StringBuilder(fam);
                sb.append(DataConstants.EMPTY_SPACE).append(sn);
                if (DataConstants.NEWSLETTER.equals(sn)) {
                    sb.append(DataConstants.EMPTY_SPACE).append(year).append(DataConstants.HYPHEN)
                        .append(mqFmt);
                }
                documentItemAssociatedFH.setTitle(sb.toString());
                documentItemAssociatedFH.setFileType(getFileType(fileName));
            }
        } catch (Exception e) {
            log.error(DataConstants.LOGGER_GETFHDOCSCT, e);
            throw new TechnicalException(DataConstants.LOGGER_GETFHDOCSCT, e);
        }

        return documentItemAssociatedFH;
    }

    @Override
    @LogExecutionTime
    public DocumentItemAssociatedFHModel getFHDocumentART(List<String> modelList,
        List<String> tokenList,
        String overallFileId, String fileId) {
        DocumentItemAssociatedFHModel documentItemAssociatedFH = null;
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
                    .find(new Document(DataConstants.DOCUMENTS_ID, id)).first();
                String fileName = artifact.getFilename();
                Integer year = doc.getInteger(DataConstants.DOCUMENTS_UPLOADYEAR);
                Integer month = doc.getInteger(DataConstants.DOCUMENTS_UPLOADMONTH);
                Integer quarter = doc.getInteger(DataConstants.DOCUMENTS_UPLOADQUARTER);
                String mq;
                String mqFmt;
                Integer mqVal;
                if (month != null) {
                    mqVal = month;
                    mq = DataConstants.M;
                    mqFmt = (month < 10 ? "0" : "") + month;
                } else {
                    mqVal = quarter;
                    mq = DataConstants.Q;
                    mqFmt = mq + (quarter < 10 ? "0" : "") + quarter;
                }
                String fam = doc.getString(DataConstants.DOCUMENTS_FAMILY);
                String sn = doc.getString(DataConstants.DOCUMENTS_DOCTYPE);
                documentItemAssociatedFH = new DocumentItemAssociatedFHModel();
                documentItemAssociatedFH.setId(fileId);
                documentItemAssociatedFH.setSeqId(1);
                documentItemAssociatedFH.setModel(createDisplayModelList(modelList,
                    castList(doc.get(DataConstants.DOCUMENTS_MODELS), String.class)));
                documentItemAssociatedFH.setSection(sn);
                documentItemAssociatedFH.setMonthQuarter(mq);
                documentItemAssociatedFH.setMonthQuarterDisplay(mqFmt);
                documentItemAssociatedFH.setMnthQtrNumber(mqVal);
                documentItemAssociatedFH.setYearNum(year);
                documentItemAssociatedFH
                    .setReleaseDate(
                        DataUtil.formatDateTime(doc.getDate(DataConstants.DOCUMENTS_CREATIONDATE)));
                documentItemAssociatedFH.setFileName(fileName);
                documentItemAssociatedFH
                    .setContentType(artifact.getMetadata().getString(DataConstants.TYPE));
                documentItemAssociatedFH
                    .setResourceUri(
                        DataConstants.RESOURCE_URI_FHS + overallFileId + DataConstants.ART
                            + fileId);
                documentItemAssociatedFH
                    .setDocumentsUri(
                        RESOURCE_URI + overallFileId + DataConstants.RESOURCE_DOCUMENTS_ASSOCIATED
                            + DataConstants.TYPE_PARAM + getSubSystem().toString().toLowerCase());
                documentItemAssociatedFH
                    .setTitle(
                        fam + " " + sn + " - " + doc.getString(DataConstants.DOCUMENTS_TITLE));
                documentItemAssociatedFH.setFileType(getFileType(fileName));
                documentItemAssociatedFH.setEngineFamily(fam);
            }
        } catch (Exception e) {
            log.error(DataConstants.LOGGER_GETFHDOCART, e);
            throw new TechnicalException(DataConstants.LOGGER_GETFHDOCART, e);
        }

        return documentItemAssociatedFH;
    }
}
