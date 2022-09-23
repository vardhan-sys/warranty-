package com.geaviation.techpubs.data.impl;

import com.geaviation.dss.service.common.exception.TechnicalException;
import com.geaviation.techpubs.data.api.IDocMongoData;
import com.geaviation.techpubs.data.util.DataConstants;
import com.geaviation.techpubs.data.util.DataUtil;
import com.geaviation.techpubs.data.util.log.LogExecutionTime;
import com.geaviation.techpubs.models.DocumentItemModel;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractDocMongoSystemData<T extends DocumentItemModel> extends
    AbstractDocSubSystemData
    implements IDocMongoData {

    protected static final Logger log = LogManager.getLogger(AbstractDocMongoSystemData.class);
    protected static final String RESOURCE_ASSOCIATED = "/associated";
    protected static final String RESOURCE_PARTS = "/parts";
    protected static final String RESOURCE_INIT = "/init";
    protected static final String RESOURCE_SCT = "sct/";

    @Autowired
    protected MongoClient mongoClient;

    protected abstract T getDocumentItemModel();

    protected abstract String getResourceUri();

    protected abstract String getResourceUritr();

    protected abstract String getDoctypeValue();

    protected abstract void setDocumentSubsystem(Document document, List<String> modelList,
        T documentItemModel);

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

        return mongoDb.getCollection(DataConstants.COLLECTION_DOCUMENTS).find(match);
    }

    protected String getResourceAssociated() {
        return RESOURCE_ASSOCIATED;
    }

    protected String getResourceParts() {
        return RESOURCE_PARTS;
    }

    @Override
    @LogExecutionTime
    public List<DocumentItemModel> getDocuments(List<String> modelList, List<String> tokenList,
        Map<String, String> queryParams) {

        MongoDatabase mongoDb = mongoClient.getDatabase(DataConstants.DB_TECHPUBS);
        List<DocumentItemModel> documentItemList = new ArrayList<>();

        try {
            for (Document doc : getCollectionResult(mongoDb, modelList, tokenList, queryParams)) {
                ObjectId fileRef = (ObjectId) doc.get(DataConstants.DOCUMENTS_FILEID);
                T documentItemModel = getDocumentItemModel();
                if (fileRef != null) {
                    GridFSFile artifact = GridFSBuckets
                        .create(mongoDb, DataConstants.COLLECTION_ARTIFACTS)
                        .find(new Document(DataConstants.DOCUMENTS_ID, fileRef)).first();
                    documentItemModel.setId(fileRef.toString());

                    String title = doc.getString(DataConstants.DOCUMENTS_TITLE);
                    documentItemModel.setTitle(
                        title == null || title.isEmpty() ? artifact.getFilename() : title);
                    documentItemModel.setResourceUri(getResourceUri() + fileRef.toString());
                }
                setDocumentSubsystem(doc, modelList, documentItemModel);

                documentItemList.add(documentItemModel);
            }
        } catch (Exception e) {
            log.error("Exception in getDocuments", e);
            throw new TechnicalException("getDocuments", e);
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

    protected void applyDoctypeQueryParams(Document match, Map<String, String> queryParams) {

        String doctype = null;
        if (queryParams != null && !queryParams.isEmpty()) {

            doctype = queryParams.get(DataConstants.DOCUMENTS_DOCTYPE);

            if (doctype != null) {
                match.append(DataConstants.DOCUMENTS_DOCTYPE, doctype);
            }

        }
    }

    protected void applyDateQueryParams(Document match, Map<String, String> queryParams) {

        String reldate = null;
        String reldatefrom = null;
        String reldateto = null;

        if (queryParams != null && !queryParams.isEmpty()) {
            try {
                reldate = queryParams.get(DataConstants.QUERY_PARAM_RELDATE);
                reldatefrom = queryParams.get(DataConstants.QUERY_PARAM_RELDATEFROM);
                reldateto = queryParams.get(DataConstants.QUERY_PARAM_RELDATETO);

                Date fromTs = null;
                Date toTs = null;

                if (reldateto != null) {
                    toTs = new Date(DataUtil.parseDateTime(reldateto).getTime() + 86400000L);
                }

                if (reldatefrom != null) {
                    fromTs = DataUtil.parseDateTime(reldatefrom);
                }

                if (reldate != null && reldate.length() == 7) {
                    reldate += "-01";
                    fromTs = DataUtil.parseDateTime(reldate);
                    toTs = DataUtil.parseDateTime(
                        DataUtil.formatDateTime(new Date(fromTs.getTime() + (32L * 86400000L)))
                            .substring(0, 7)
                            + "-01");
                } else if (reldate != null) {
                    fromTs = DataUtil.parseDateTime(reldate);
                    toTs = new Date(fromTs.getTime() + 86400000L);
                }

                setReldateto(match, fromTs, toTs);

            } catch (ParseException pe) {
                log.error(
                    "matchCreationDateRange (reldate=" + reldate + ",reldatefrom=" + reldatefrom
                        + ",reldateto=" + reldateto + ")", pe);
            }
        }
    }

    protected String createDisplayModelList(List<String> modelList, List<String> docModels) {
        HashSet<String> selectedUniqueModels = new HashSet<>();
        for (String docModel : docModels) {
            if (modelList.contains(docModel)) {
                selectedUniqueModels.add(docModel);
            }
        }
        List<String> selectedModels = new ArrayList<>(selectedUniqueModels);
        Collections.sort(selectedModels);
        return Arrays.toString(selectedModels.toArray()).replaceAll("[\\[\\] ]", "");
    }

    private void setReldateto(Document match, Date fromTs, Date toTs) {

        List<Document> first = new ArrayList<>();
        first.add(new Document(DataConstants.DOCUMENTS_RELEASEDATE,
            new Document(DataConstants.EXISTS, true)));
        first.add(new Document(DataConstants.DOCUMENTS_REVDATE,
            new Document(DataConstants.EXISTS, false)));
        if (fromTs != null) {
            first.add(new Document(DataConstants.DOCUMENTS_RELEASEDATE,
                new Document(DataConstants.GTE, fromTs)));
        }
        if (toTs != null) {
            first.add(new Document(DataConstants.DOCUMENTS_RELEASEDATE,
                new Document(DataConstants.LT, toTs)));
        }

        List<Document> second = new ArrayList<>();
        second.add(new Document(DataConstants.DOCUMENTS_RELEASEDATE,
            new Document(DataConstants.EXISTS, false)));
        second.add(new Document(DataConstants.DOCUMENTS_REVDATE,
            new Document(DataConstants.EXISTS, true)));
        if (fromTs != null) {
            second.add(new Document(DataConstants.DOCUMENTS_REVDATE,
                new Document(DataConstants.GTE, fromTs)));
        }
        if (toTs != null) {
            second.add(new Document(DataConstants.DOCUMENTS_REVDATE,
                new Document(DataConstants.LT, toTs)));
        }

        List<Document> third = new ArrayList<>();
        third.add(new Document(DataConstants.DOCUMENTS_RELEASEDATE,
            new Document(DataConstants.EXISTS, false)));
        third.add(new Document(DataConstants.DOCUMENTS_REVDATE,
            new Document(DataConstants.EXISTS, false)));
        if (fromTs != null) {
            third.add(new Document(DataConstants.DOCUMENTS_CREATIONDATE,
                new Document(DataConstants.GTE, fromTs)));
        }
        if (toTs != null) {
            third.add(new Document(DataConstants.DOCUMENTS_CREATIONDATE,
                new Document(DataConstants.LT, toTs)));
        }

        List<Document> filterList = Arrays.asList(new Document(DataConstants.AND, first),
            new Document(DataConstants.AND, second), new Document(DataConstants.AND, third));
        appendDocList(match, filterList);
    }

    @SuppressWarnings("unchecked")
    private static void appendDocList(Document match, List<Document> docList) {
        Object ref = match;
        if (match.containsKey(DataConstants.OR)) {
            if (match.containsKey(DataConstants.AND)) {
                Object tmp = match.get(DataConstants.AND);
                if (tmp instanceof List) {
                    ref = new ArrayList<Document>(castList(tmp, Document.class));
                    match.put(DataConstants.AND, ref);
                } else {
                    ref = tmp;
                }
            } else {
                ArrayList<Document> tmp = new ArrayList<>();
                tmp.add(new Document(DataConstants.OR, match.remove(DataConstants.OR)));
                match.append(DataConstants.AND, tmp);
                ref = tmp;
            }
        }

        if (ref instanceof ArrayList) {
            ((List<Document>) ref).add(new Document(DataConstants.OR, docList));
        } else if (ref instanceof Document) {
            ((Document) ref).append(DataConstants.OR, docList);
        } else {
            log.error("Unexpected object returned from restructMatch method: " + ref.getClass()
                .getName());
        }
    }

    @Override
    public List<DocumentItemModel> getAssociatedDocuments(List<String> modelList,
        List<String> tokenList, String fileId,
        Boolean includeParts) {

        return new ArrayList<>();
    }
}