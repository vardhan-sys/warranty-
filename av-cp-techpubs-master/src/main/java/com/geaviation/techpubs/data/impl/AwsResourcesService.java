package com.geaviation.techpubs.data.impl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.services.cloudfront.CloudFrontCookieSigner;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.geaviation.techpubs.data.client.ApiGatewayClient;
import com.geaviation.techpubs.data.client.ApiGatewayException;
import com.geaviation.techpubs.data.client.ApiGatewayRequest;
import com.geaviation.techpubs.data.factories.AmazonS3ClientFactory;
import com.geaviation.techpubs.data.factories.ApiGatewayClientFactory;
import com.geaviation.techpubs.data.factories.SecretsManagerClientFactory;
import com.geaviation.techpubs.data.factories.SqsClientFactory;
import com.geaviation.techpubs.data.util.DataConstants;
import com.geaviation.techpubs.data.util.DataUtil;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.services.util.AppConstants;
import com.geaviation.techpubs.services.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RefreshScope
public class AwsResourcesService {

    private static final Logger log = LogManager.getLogger(AwsResourcesService.class);
    private static final String ASE_SQS_MESSAGE = "Caught an AmazonServiceException. The message made it to Amazon SQS, but was rejected with an error response for some reason.";
    public static final String APP_ID = "appId";
    public static final String AWSERROR =
        "Caught an AmazonClientException. The client encountered a serious internal problem while "
            +
            "trying to communicate with Amazon SQS, such as not being able to access the network.";
    public static final String ERROR_MESSAGE = "Error Message: ";

    @Value("${spring.profiles.active}")
    private String environment;

    @Value("${techpubs.services.UseNewSecretName}")
    private boolean useNewSecretName;

    @Value("${CLOUDFRONT.SECRET.NAME}")
    private String cloudFrontSecret;

    @Value("${SQS.QUEUE.URL}")
    private String sqsQueueUrl;

    @Autowired
    private SecretsManagerClientFactory secretsManagerClientFactory;

    @Autowired
    private SqsClientFactory sqsClientFactory;

    @Autowired
    private ApiGatewayClientFactory apiGatewayClientFactory;

    @Autowired
    private AmazonS3ClientFactory amazonS3ClientFactory;

    /**
     * Method generates a javax response builder that includes three CloudFront singed cookies.
     * These cookies are used to secure the Tech Pubs CloudFront distribution.
     *
     * @param portalId - Users portal id to identify which portal to create cookie for.
     * @param program - Engine program used to created the resource policy path in cookie.
     * @return Response.ResponseBuilder - Response builder containing three CLoudFront cookies that
     * will get returned to users browser.
     * @throws TechpubsException - If errors occur, throw exception up to the web layer to handle
     */
    public ResponseEntity generateCloudFrontCookieResponse(String portalId, String program) throws TechpubsException {
        CloudFrontCookieSigner.CookiesForCustomPolicy cloudFrontCookies = generateCloudFrontCookies(
            portalId, program);

        String hostDomain = DataUtil.getPortalDomain(portalId, environment);

        String policyKey = cloudFrontCookies.getPolicy().getKey();
        String policyValue = cloudFrontCookies.getPolicy().getValue();
        String signatureKey = cloudFrontCookies.getSignature().getKey();
        String signatureValue = cloudFrontCookies.getSignature().getValue();
        String keyPairIdKey = cloudFrontCookies.getKeyPairId().getKey();
        String keyPairIdValue = cloudFrontCookies.getKeyPairId().getValue();

        ResponseCookie cloudfrontPolicyCookie = ResponseCookie.from(policyKey, policyValue)
                .secure(true)
                .path("/")
                .maxAge(-1)
                .domain(hostDomain)
                .build();
        ResponseCookie cloudfrontSignatureCookie = ResponseCookie.from(signatureKey, signatureValue)
                .secure(true)
                .path("/")
                .maxAge(-1)
                .domain(hostDomain)
                .build();
        ResponseCookie cloudfrontKeyPairIdCookie = ResponseCookie.from(keyPairIdKey, keyPairIdValue)
                .secure(true)
                .path("/")
                .maxAge(-1)
                .domain(hostDomain)
                .build();

        HttpHeaders responseHeaders = new HttpHeaders();

        responseHeaders.add(HttpHeaders.SET_COOKIE, cloudfrontPolicyCookie.toString());
        responseHeaders.add(HttpHeaders.SET_COOKIE, cloudfrontSignatureCookie.toString());
        responseHeaders.add(HttpHeaders.SET_COOKIE, cloudfrontKeyPairIdCookie.toString());

        return ResponseEntity.ok()
                .headers(responseHeaders).build();

    }

    /**
     * Method generates signed CloudFront cookies.  Cookies contain policy resource path, private
     * key, key pair id, expires date, active date and ip range.  Method gets sensitive data from
     * Secrets Manager.
     *
     * @param portalId - portal id (CWC or Honda) used to generate CloudFront policy Resource Path
     * in the signed cookies.
     * @param program - engine program (e.g. gek112060) used to generate CloudFront policy Resource
     * Path in the signed cookies.
     * @return CloudFrontCookieSigner.CookiesForCustomPolicy - AWS CloudFront object that contains
     * the three cookies required to access AWS CloudFront.
     * @throws TechpubsException If errors occur, throw exception up to the web layer to handle
     */
    private CloudFrontCookieSigner.CookiesForCustomPolicy  generateCloudFrontCookies(String portalId, String program) throws TechpubsException {
        String keyPairId;
        PrivateKey privateKey;
        try {
            JSONObject cloudFrontSecrets = getCloudFrontSecrets();
            keyPairId = cloudFrontSecrets.get("keyPairId").toString();
            privateKey = DataUtil
                .readPrivateKeyFromString(cloudFrontSecrets.get("privateKey").toString());
        } catch (JSONException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("generateCloudFrontCookies - failed to read private key. " + e);
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
        }

        Date activeFrom = DataUtil.getCurrentDateInGmt();
        Date expiresOn = DataUtil.getCurrentDatePlusThirtyMinutesInGmt();
        String policyResourcePath = DataUtil
            .getPortalDistributionResourcePath(portalId, environment, program);
        String ipRange = null;

        return CloudFrontCookieSigner
            .getCookiesForCustomPolicy(policyResourcePath, privateKey, keyPairId, expiresOn,
                activeFrom, ipRange);
    }

    public S3Object getS3Object(String bucketName, String objKey) throws TechpubsException {

        AmazonS3 amazonS3Client = amazonS3ClientFactory.getS3Client();
        S3Object s3Object = null;

        try {

            s3Object = amazonS3Client.getObject(new GetObjectRequest(bucketName, objKey));

        } catch (AmazonServiceException e) {
            log.error(e.getMessage());
            throw new TechpubsException(TechpubsException.TechpubsAppError.RESOURCE_NOT_FOUND);
        } catch (SdkClientException e) {
            log.error(e.getMessage());
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
        }

        return s3Object;

    }

    public File getS3File(String bucketName, String objKey) throws TechpubsException {

        AmazonS3 amazonS3Client = amazonS3ClientFactory.getS3Client();
        S3Object s3Object = null;

        try {
            s3Object = amazonS3Client.getObject(new GetObjectRequest(bucketName, objKey));
            InputStream inputStream = s3Object.getObjectContent();
            File file = new File(inputStream.toString());
        } catch (AmazonServiceException e) {
            log.error(e.getMessage());
            throw new TechpubsException(TechpubsException.TechpubsAppError.RESOURCE_NOT_FOUND);
        } catch (SdkClientException e) {
            log.error(e.getMessage());
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
        }
        return new File(objKey);
    }


    /**
     * Method retrieves AWS CloudFront ssh key from Secrets Manger.
     *
     * @return JSONObject that contains the AWS CloudFront ssh key pair id and private key value.
     * @throws TechpubsException If errors occur, throw exceptions up to the web layer to handle
     */
    private JSONObject getCloudFrontSecrets() throws TechpubsException {


        AWSSecretsManager client = secretsManagerClientFactory.getSecretsManagerClient();
        String secretName;
        if(useNewSecretName) {
            secretName = cloudFrontSecret;
        } else{
            if(environment.equals("dev") || environment.equals("qa")) {
                secretName = "cp-cloudfront-keys-nonprod";
            } else {
                secretName = "cp-cloudfront-keys-prod";
            }
        }

        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
            .withSecretId(secretName);

        GetSecretValueResult getSecretValueResult = null;
        try {
            getSecretValueResult = client.getSecretValue(getSecretValueRequest);
        } catch (Exception e) {
            log.error("Failed to retrieve secret from secrets manager. " + e);
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
        }

        // Decrypts secret using the associated KMS CMK.
        String secretString = null;
        if (getSecretValueResult.getSecretString() != null) {
            secretString = getSecretValueResult.getSecretString();
        } else {
            log.error("Secret value returned null");
            throw new TechpubsException(TechpubsException.TechpubsAppError.RESOURCE_NOT_FOUND);
        }

        return new JSONObject(secretString);
    }

    /**
     * Writes attributes to the Audit Trail SQS queue to be picked up by the Audit Writer lambda function
     * which will then write a record to DynamoDB.
     *
     * @param ssoId - The sso of the user modifying the value
     * @param category - The specific resource that is changing. Used for filtering.
     * @param userSsoId - The user being modified.
     * @param roles - The list of roles being added, updated, or deleted.
     * @param engineModels - The list of engine models being added, updated, or deleted.
     * @param airFrames - The list of air frames being added, updated, or deleted.
     * @param documentTypes - The list of document types being added, updated, or deleted.
     * @param action - The change action
     * @throws TechpubsException - If errors occur, throw exception up to the web layer to handle
     */
    public void writeAdminManagementAuditLog(String ssoId, String category, String userSsoId, List<String> roles,
    List<String> engineModels, List<String> airFrames, List<String> documentTypes, String action) throws TechpubsException {
        try {
            AmazonSQS sqsClient = sqsClientFactory.getSqsClient();
            Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
            messageAttributes.put(DataConstants.MODIFIED_BY, new MessageAttributeValue().withDataType(DataConstants.STRING).withStringValue(ssoId));
            messageAttributes.put(
                APP_ID, new MessageAttributeValue().withDataType(DataConstants.STRING)
                    .withStringValue(DataConstants.APP_ID));

            JSONObject json = new JSONObject();
            json.put(DataConstants.CATEGORY, category);
            json.put("user", userSsoId);
            json.put(DataConstants.ACTION, action);
            if (roles != null && !roles.isEmpty()) {
                json.put("roles", StringUtils.listToString(roles));
            }
            if (engineModels != null && !engineModels.isEmpty()) {
                json.put("engineModels", StringUtils.listToString(engineModels));
            }
            if (airFrames != null && !airFrames.isEmpty()) {
                json.put("airFrames", StringUtils.listToString(airFrames));
            }
            if (documentTypes != null && !documentTypes.isEmpty()) {
                json.put("documentTypes", StringUtils.listToString(documentTypes));
            }

            String messageBody = json.toString();

            SendMessageRequest sendMessageRequest = new SendMessageRequest();
            sendMessageRequest.withMessageBody(messageBody);
            sendMessageRequest.withQueueUrl(sqsQueueUrl);
            sendMessageRequest.withMessageAttributes(messageAttributes);
            sqsClient.sendMessage(sendMessageRequest);
        } catch (AmazonServiceException ase) {
            logAWSException(ase, ASE_SQS_MESSAGE);
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
        } catch (AmazonClientException ace) {
            log.error(
                AWSERROR);
            log.error(ERROR_MESSAGE + ace.getMessage());
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
        }
    }

    /**
     * Writes attributes to the Audit Trail SQS queue to be picked up by the Audit Writer lambda function
     * which will then write a record to DynamoDB.
     *
     * @param ssoId - The sso of the user modifying the value
     * @param company - The company who is being modified
     * @param engineModel - The engine model who is being modified
     * @param documentType - The type of the document applicable for all docs
     * @param document - The document title, applicable for SMM Docs
     * @param bookcaseKey - The key of the bookcase, applicable for SMM Docs
     * @param pageblkKey - The key of the page blk, applicable for SMM Docs
     * @param bookKey - The key of the book, applicable for all docs
     * @param action - The change action
     */
    public void writeCompaniesAuditLog(String ssoId, String company, String engineModel,
        String documentType, String document, String bookcaseKey, String pageblkKey, String bookKey, String action) {

        try {
            AmazonSQS sqsClient = sqsClientFactory.getSqsClient();
            Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
            messageAttributes.put(DataConstants.MODIFIED_BY, new MessageAttributeValue().withDataType(DataConstants.STRING).withStringValue(ssoId));
            messageAttributes.put(
                APP_ID, new MessageAttributeValue().withDataType(DataConstants.STRING).withStringValue(DataConstants.APP_ID));

            JSONObject json = new JSONObject();
            json.put(DataConstants.CATEGORY, AppConstants.COMPANIES_TAB);
            json.put("company", company);
            json.put("engineModel", engineModel);

            if (!org.apache.commons.lang3.StringUtils.isEmpty(documentType)) {
                json.put("documentType", documentType);
            }
            if (!org.apache.commons.lang3.StringUtils.isEmpty(document)) {
                json.put("document", document);
            }
            if (!org.apache.commons.lang3.StringUtils.isEmpty(bookcaseKey)) {
                json.put("bookcaseKey", bookcaseKey);
            }
            if (!org.apache.commons.lang3.StringUtils.isEmpty(pageblkKey)) {
                json.put("pageblkKey", pageblkKey);
            }
            if (!org.apache.commons.lang3.StringUtils.isEmpty(bookKey)) {
                json.put("bookKey", bookKey);
            }

            json.put(DataConstants.ACTION, action);
            String messageBody = json.toString();

            SendMessageRequest sendMessageRequest = new SendMessageRequest();
            sendMessageRequest.withMessageBody(messageBody);
            sendMessageRequest.withQueueUrl(sqsQueueUrl);
            sendMessageRequest.withMessageAttributes(messageAttributes);
            sqsClient.sendMessage(sendMessageRequest);
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
     * Writes attributes to the Audit Trail SQS queue to be picked up by the Audit Writer lambda function
     * which will then write a record to DynamoDB.
     *
     * @param ssoId - The sso of the user modifying the value
     * @param bookcaseKey - The key of the bookcase
     * @param bookcaseVersion - The version being modified
     * @param action - The status being set
     */
    public void writePublisherAuditLog(String ssoId, String bookcaseKey, String bookcaseVersion, String action, String releaseDate) {
        try {
            AmazonSQS sqsClient = sqsClientFactory.getSqsClient();
            Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
            messageAttributes.put(DataConstants.MODIFIED_BY, new MessageAttributeValue().withDataType(DataConstants.STRING).withStringValue(ssoId));
            messageAttributes.put(
                APP_ID, new MessageAttributeValue().withDataType(DataConstants.STRING).withStringValue(DataConstants.APP_ID));

            JSONObject json = new JSONObject();
            json.put(DataConstants.CATEGORY, AppConstants.PUBLISHER_TAB);
            json.put(DataConstants.ACTION, action);
            json.put("bookcaseKey", bookcaseKey);
            json.put("bookcaseVersion", bookcaseVersion);
            json.put("releaseDate", releaseDate);

            String messageBody = json.toString();

            SendMessageRequest sendMessageRequest = new SendMessageRequest();
            sendMessageRequest.withMessageBody(messageBody);
            sendMessageRequest.withQueueUrl(sqsQueueUrl);
            sendMessageRequest.withMessageAttributes(messageAttributes);
            sqsClient.sendMessage(sendMessageRequest);
            log.info(
                "Wrote to Publisher Audit log: " + ssoId + " bookcase info: " + bookcaseKey + " "
                    + bookcaseVersion + " " + action);
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
     * Writes attributes to the Audit Trail SQS queue to be picked up by the Audit Writer lambda function
     * which will then write a record to DynamoDB.
     *
     * @param ssoId - The sso of the user modifying the value
     * @param bookcaseKey - The key of the bookcase
     * @param bookcaseVersion - The version being modified
     * @param action - The status being set
     */
    public void writeReviewerAuditLog(String ssoId, String bookcaseKey, String bookcaseVersion, String action,
                                      String book, String publicationTypeCode, String key, String fileName,
                                      String resourceUri, String releaseDate, boolean notificationStatus) {
        try {
            AmazonSQS sqsClient = sqsClientFactory.getSqsClient();
            Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
            messageAttributes.put(DataConstants.MODIFIED_BY, new MessageAttributeValue()
                    .withDataType(DataConstants.STRING).withStringValue(ssoId));
            messageAttributes.put(
                    APP_ID, new MessageAttributeValue().withDataType(DataConstants.STRING)
                            .withStringValue(DataConstants.APP_ID));

            JSONObject json = new JSONObject();
            json.put(DataConstants.CATEGORY, AppConstants.PUBLISHER_TAB);
            json.put(DataConstants.ACTION, action);
            json.put("book", book);
            json.put("typeCode", publicationTypeCode);
            json.put("key", key);
            json.put("fileName", fileName);
            json.put("resourceUri", resourceUri);
            json.put("bookcaseKey", bookcaseKey);
            json.put("bookcaseVersion", bookcaseVersion);
            json.put("releaseDate", releaseDate);
            json.put("notificationStatus", Boolean.toString(notificationStatus));

            String messageBody = json.toString();

            SendMessageRequest sendMessageRequest = new SendMessageRequest();
            sendMessageRequest.withMessageBody(messageBody);
            sendMessageRequest.withQueueUrl(sqsQueueUrl);
            sendMessageRequest.withMessageAttributes(messageAttributes);
            sqsClient.sendMessage(sendMessageRequest);
            log.info(
                    "Wrote to Publisher Audit log: " + ssoId + " bookcase info: " + bookcaseKey + " "
                            + bookcaseVersion + " " + action + " ResourceUri: " + resourceUri);
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
     * Writes attributes to the Audit Trail SQS queue to be picked up by the Audit Writer lambda function
     * which will then write a record to DynamoDB.
     *
     * @param body - The body of the request
     */
    public String getAuditLogs(JSONObject body) {
        ApiGatewayClient apiGatewayClient = apiGatewayClientFactory.getApiGatewayClient();

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        Map<String, List<String>> queryParams = new LinkedHashMap<>();

        String response = null;

        try {

            ByteArrayInputStream bodyByteStream = new ByteArrayInputStream(body.toString().getBytes());

            ApiGatewayRequest request = new ApiGatewayRequest()
                    .withBody(bodyByteStream)
                    .withHttpMethod(HttpMethodName.POST)
                    .withHeaders(headers)
                    .withParameters(queryParams)
                    .withResourcePath("/audit-log/doc-admin");

            response = apiGatewayClient.execute(request).getResult().getBody();

        } catch (ApiGatewayException age) {
            logAWSException(age,
                "Caught an ApiGatewayException. The message made it to Amazon API Gateway, but was " +
                        "rejected with an error response for some reason.");
        }

        return response;
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
