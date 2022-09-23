package com.geaviation.techpubs.data.impl;

import com.geaviation.techpubs.data.api.IEntitlementData;
import com.geaviation.techpubs.data.util.DataConstants;
import com.geaviation.techpubs.data.util.DataUtil;
import com.geaviation.techpubs.data.util.log.LogExecutionTime;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EntitlementsDataImpl implements IEntitlementData {

    @Autowired
    private MongoClient mongoClient;

    private static final Logger log = LogManager.getLogger(EntitlementsDataImpl.class);

    @Override
    @LogExecutionTime
    public List<String> getEntitlements(String company, String portalId, String subSystem,
        List<String> modelList) {
        List<String> entitlementList = new ArrayList<>();
        if (DataUtil.isNotNullandEmpty(company) && DataUtil.isNotNullandEmpty(subSystem)
            && DataUtil.isNotNullandEmpty(portalId)) {
            MongoDatabase mongoDb = mongoClient.getDatabase(DataConstants.DB_TECHPUBS);
            MongoCollection<Document> entitlementsCollection = mongoDb
                .getCollection(DataConstants.COLLECTION_ENTITLEMENT);
            Date now = new Date();
            String portalVal = portalId.toUpperCase();

            for (Document doc : entitlementsCollection.aggregate(Arrays.asList(
                new Document(DataConstants.MATCH,
                    new Document(DataConstants.ENTITLEMENTS_ID, company.toLowerCase())),
                new Document(DataConstants.UNWIND, "$entitlements"),
                new Document(DataConstants.MATCH,
                    new Document(DataConstants.ENTITLEMENTS_SUBSYSTEM, subSystem)
                        .append(DataConstants.ENTITLEMENTS_PORTALID, portalVal)
                        .append(DataConstants.ENTITLEMENTS_STARTDATE,
                            new Document(DataConstants.LTE, now))
                        .append(DataConstants.AND,
                            Arrays.asList(new Document(DataConstants.OR, Arrays.asList(
                                new Document(DataConstants.ENTITLEMENTS_TOKENTYPE,
                                    DataConstants.DOCTYPE).append(
                                    DataConstants.ENTITLEMENTS_TOKEN,
                                    new Document(DataConstants.EXISTS, true)),
                                new Document(DataConstants.ENTITLEMENTS_TOKENTYPE,
                                    DataConstants.DOCUMENT))),
                                new Document(DataConstants.OR,
                                    Arrays.asList(
                                        new Document(DataConstants.ENTITLEMENTS_ENDDATE,
                                            new Document(DataConstants.EXISTS, false)),
                                        new Document(DataConstants.ENTITLEMENTS_ENDDATE,
                                            new Document(DataConstants.GTE, now)))))))))) {
                Document entDoc = (Document) doc.get(DataConstants.ENTITLEMENTS);
                String tokentype = entDoc.getString(DataConstants.TOKENTYPE);
                String token = entDoc.getString(DataConstants.TOKEN);
                if (token != null) {
                    entitlementList = checkToken(modelList, entitlementList, entDoc, tokentype,
                        token);
                }
            }
        }

        return entitlementList;
    }

    @SuppressWarnings("unchecked")
    private List<String> checkToken(List<String> modelList, List<String> entitlementList,
        Document entDoc,
        String tokentype, String token) {

        if (DataConstants.DOCTYPE.equals(tokentype) && (modelList == null || modelList.isEmpty())) {
            entitlementList.add(token);
        } else if (DataConstants.DOCTYPE.equals(tokentype)) {
            List<String> tier2models = (List<String>) entDoc.get("tier2models");
            for (String engine : modelList) {
                if (tier2models == null || !tier2models.contains(engine)) {
                    entitlementList.add(token + ":" + engine);
                }
            }

        } else if (DataConstants.DOCUMENT.equals(tokentype)) {
            entitlementList.add(token);
        }
        return entitlementList;
    }

    @Deprecated
    @Override
    @SuppressWarnings("unchecked")
    @LogExecutionTime
    //Remove this  during US478605 feature flag cleanup
    public List<String> getAdminEntitlements(String ssoId) {
        MongoDatabase mongoDb = mongoClient.getDatabase(DataConstants.DB_TECHPUBS);
        MongoCollection<Document> entitlementsCollection = mongoDb
            .getCollection(DataConstants.COLLECTION_ENTITLEMENT);
        List<String> entitlementList = new ArrayList<>();
        for (Document doc : entitlementsCollection.aggregate(Arrays.asList(
            new Document(DataConstants.MATCH, new Document(DataConstants.ENTITLEMENTS_ID, ssoId)),
            new Document(DataConstants.UNWIND, DataConstants.ENTITLEMENT),
            new Document(DataConstants.MATCH,
                new Document(DataConstants.ENTITLEMENTS_PORTALID, DataConstants.CAP_ADMIN)
                    .append(DataConstants.ENTITLEMENTS_SUBSYSTEM, DataConstants.CAP_ADMIN)
                    .append(DataConstants.ENTITLEMENTS_TOKENTYPE, DataConstants.SMALL_ADMIN)),
            new Document(DataConstants.PROJECT,
                new Document(DataConstants.ENTITLEMENTS_TOKEN, true)),
            new Document(DataConstants.GROUP, new Document(DataConstants.ARTIFACTS_ID,
                new Document(DataConstants.ID, DataConstants.DOCUMENTS_ID_VAL))
                .append(DataConstants.TOKENS,
                    new Document(DataConstants.ADD_TO_SET,
                        DataConstants.SYM_ENTITLEMENTS_TOKEN)))))) {
            entitlementList.addAll((List<String>) doc.get(DataConstants.TOKENS));
        }
        return entitlementList;
    }
}
