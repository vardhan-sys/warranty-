package com.geaviation.techpubs.services.util.admin;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.geaviation.techpubs.data.api.techlib.ISystemDocumentData;
import com.geaviation.techpubs.data.api.techlib.ISystemDocumentSiteLookupData;
import com.geaviation.techpubs.data.api.techlib.ISystemDocumentTypeLookupData;
import com.geaviation.techpubs.data.factories.SqsClientFactory;
import com.geaviation.techpubs.data.impl.AwsResourcesService;
import com.geaviation.techpubs.data.util.DataConstants;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.techlib.SystemDocumentEntity;
import com.geaviation.techpubs.models.techlib.SystemDocumentSiteLookupEntity;
import com.geaviation.techpubs.models.techlib.SystemDocumentTypeLookupEntity;
import com.geaviation.techpubs.services.util.AppConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RefreshScope
public class AvSystemAuditLogService {

    private static final Logger log = LogManager.getLogger(AwsResourcesService.class);
    private static final String ASE_SQS_MESSAGE = "Caught an AmazonServiceException. The message made it to Amazon SQS, but was rejected with an error response for some reason.";
    public static final String APP_ID = "appId";
    public static final String AWSERROR =
            "Caught an AmazonClientException. The client encountered a serious internal problem while "
                    +
                    "trying to communicate with Amazon SQS, such as not being able to access the network.";
    public static final String ERROR_MESSAGE = "Error Message: ";

    @Value("${SQS.QUEUE.URL}")
    private String sqsQueueUrl;

    @Autowired
    private ISystemDocumentSiteLookupData iSystemDocumentSiteLookupData;

    @Autowired
    private ISystemDocumentTypeLookupData iSystemDocumentTypeLookupData;

    @Autowired
    private ISystemDocumentData iSystemDocumentData;


    @Autowired
    private SqsClientFactory sqsClientFactory;


    /**
     * Utility method that finds values of provided IDs from systemDocumentLookup table
     * @return
     */
    public SystemDocumentTypeLookupEntity getDocTypeValue(String docId) throws TechpubsException {
        Optional<SystemDocumentEntity> sysDocOptional = iSystemDocumentData.findById(UUID.fromString(docId));
        if (sysDocOptional.isPresent()) {
            SystemDocumentEntity docEntity = sysDocOptional.get();
            Optional<SystemDocumentTypeLookupEntity> docTypeEntity = iSystemDocumentTypeLookupData.findById(docEntity.getSystemDocumentTypeLookupEntity().getId());
            if (docTypeEntity.isPresent()) {
                return docTypeEntity.get();
            } else {
                log.error("No document type found for id: " + docId);
                throw new TechpubsException(TechpubsException.TechpubsAppError.DATA_NOT_FOUND);
            }

        } else {
            log.error("No system document found for id " + docId);
            throw new TechpubsException(TechpubsException.TechpubsAppError.DATA_NOT_FOUND);
        }
    }

    /**
     * Utility method that finds values of provided IDs from systemDocumentSiteLookup table
     * @return
     */
    public SystemDocumentSiteLookupEntity getDocSiteValue(String docId) throws TechpubsException {
        Optional<SystemDocumentEntity> sysDocOptional = iSystemDocumentData.findById(UUID.fromString(docId));
        if (sysDocOptional.isPresent()) {
            SystemDocumentEntity docEntity = sysDocOptional.get();
            Optional<SystemDocumentSiteLookupEntity> docSiteEntity = iSystemDocumentSiteLookupData.findById(docEntity.getSystemDocumentSiteLookupEntity().getId());
            if (docSiteEntity.isPresent()) {
                return docSiteEntity.get();
            } else {
                log.error("No document type found for id: " + docId);
                throw new TechpubsException(TechpubsException.TechpubsAppError.DATA_NOT_FOUND);
            }

        } else {
            log.error("No system document found for id " + docId);
            throw new TechpubsException(TechpubsException.TechpubsAppError.DATA_NOT_FOUND);
        }
    }

    /**
     * Writes attributes to the Audit Trail SQS queue to be picked up by the Audit Writer. This is meant to capture
     * AvSystem document uploads activity.
     *
     * @param ssoId - The sso of the user uploading the document
     * @param docId - The unique ID of the documentUploaded
     * @param action - The status being set
     */
    public void writeAvSystemEditUploadAuditLog(String ssoId, String docName, String docId, String action, String documentRevision) throws TechpubsException {
        SystemDocumentTypeLookupEntity documentTypeLookupEntity = getDocTypeValue(docId);
        SystemDocumentSiteLookupEntity documentSiteLookupEntity = getDocSiteValue(docId);

        try {
            AmazonSQS sqsClient = sqsClientFactory.getSqsClient();
            Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
            messageAttributes.put(DataConstants.MODIFIED_BY, new MessageAttributeValue()
                    .withDataType(DataConstants.STRING).withStringValue(ssoId));
            messageAttributes.put(
                    APP_ID, new MessageAttributeValue().withDataType(DataConstants.STRING)
                            .withStringValue(DataConstants.APP_ID));

            JSONObject json = new JSONObject();
            json.put(DataConstants.CATEGORY, AppConstants.AVSYSTEMS_UPLOADER);
            json.put(DataConstants.ACTION, action);
            json.put("documentType", documentTypeLookupEntity.getValue());
            json.put("documentSite", documentSiteLookupEntity.getValue());
            json.put("docId", docId);
            json.put("document", docName);
            json.put("revisionNumber", documentRevision);

            String messageBody = json.toString();

            SendMessageRequest sendMessageRequest = new SendMessageRequest();
            sendMessageRequest.withMessageBody(messageBody);
            sendMessageRequest.withQueueUrl(sqsQueueUrl);
            sendMessageRequest.withMessageAttributes(messageAttributes);
            sqsClient.sendMessage(sendMessageRequest);
            log.info(
                    "Wrote to Uploader Audit log: " + ssoId + ". Document ID: " + docId + ".");
        } catch (AmazonServiceException ase) {
            logAWSException(ase, ASE_SQS_MESSAGE);

        } catch (AmazonClientException ace) {
            log.error(
                    AWSERROR);
            log.error(ERROR_MESSAGE + ace.getMessage());
        } catch (TechpubsException techpubsEx) {
            log.error("Could not get SQS Client: " + techpubsEx.getMessage());
        }
    }


    /**
     * Helper to log the AWS errors we could encounter
     *
     * @param ase The error message
     * @param userMessage A helpful user message for the specific error encountered
     */
    private void logAWSException(AmazonServiceException ase, String userMessage) {
        log.error(userMessage);
        log.error("Error Message:    " + ase.getMessage());
        log.error("HTTP Status Code: " + ase.getStatusCode());
        log.error("AWS Error Code:   " + ase.getErrorCode());
        log.error("Error Type:       " + ase.getErrorType());
        log.error("Request ID:       " + ase.getRequestId());
    }

}
