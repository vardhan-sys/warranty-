package com.geaviation.techpubs.data.impl;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.amazonaws.services.secretsmanager.model.InternalServiceErrorException;
import com.geaviation.techpubs.data.client.ApiGatewayClient;
import com.geaviation.techpubs.data.client.ApiGatewayRequest;
import com.geaviation.techpubs.data.factories.ApiGatewayClientFactory;
import com.geaviation.techpubs.data.factories.SecretsManagerClientFactory;
import com.geaviation.techpubs.data.test.util.TestConstants;
import com.geaviation.techpubs.exceptions.TechpubsException;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


public class AwsResourcesServiceTest {

    @Mock
    SecretsManagerClientFactory secretsManagerClientFactoryMock;

    @Mock
    AWSSecretsManager awsSecretsManagerMock;

    @Mock
    ApiGatewayClientFactory apiGatewayClientFactoryMock;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    ApiGatewayClient apiGatewayClientMock;

    @InjectMocks
    private AwsResourcesService awsResourcesService;

    private String defaultPortalID;
    private String defaultProgram;
    private GetSecretValueResult defaultGetSecretValueResult;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException, TechpubsException {
        // Instantiate class we're testing and inject mocks
        this.awsResourcesService = new AwsResourcesService();
        MockitoAnnotations.initMocks(this);

        // Instantiate default input params
        this.defaultPortalID = "CWC";
        this.defaultProgram = "gek112060";

        // Set environment variable
        Field envEnvironmentVariable = AwsResourcesService.class.getDeclaredField("environment");
        envEnvironmentVariable.setAccessible(true);
        envEnvironmentVariable.set(awsResourcesService, "dev");

        // Create response for secrets manager .getSecretValue
        this.defaultGetSecretValueResult = new GetSecretValueResult();
        defaultGetSecretValueResult.setSecretString(TestConstants.TEST_SECRET_STRING);

        // Set expectations for secrets manager and client interactions
        when(secretsManagerClientFactoryMock.getSecretsManagerClient()).thenReturn(awsSecretsManagerMock);
        when(awsSecretsManagerMock.getSecretValue(any())).thenReturn(defaultGetSecretValueResult);

        when(apiGatewayClientFactoryMock.getApiGatewayClient()).thenReturn(apiGatewayClientMock);
    }

    @Test
    public void generateCloudFrontCookieResponseReturnsResponseBuilder() throws TechpubsException {
        ResponseEntity builder = awsResourcesService
            .generateCloudFrontCookieResponse(defaultPortalID, defaultProgram);

        assertNotNull(builder);
    }

    @Test
    public void generateCloudFrontCookieResponseReturnsThreeCookies() throws TechpubsException {
        ResponseEntity response = null;
        response = awsResourcesService.generateCloudFrontCookieResponse(defaultPortalID, defaultProgram);

        assertEquals(3, response.getHeaders().get(HttpHeaders.SET_COOKIE).size());

    }

    @Test
    public void generateCloudFrontCookieResponseReturnsWithCorrectCookieNames() throws TechpubsException {
        ResponseEntity response = awsResourcesService.generateCloudFrontCookieResponse(defaultPortalID, defaultProgram);

        List<String> headerCookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);

        assertTrue(headerCookies.get(0).contains("CloudFront-Policy"));
        assertTrue(headerCookies.get(1).contains("CloudFront-Signature"));
        assertTrue(headerCookies.get(2).contains("CloudFront-Key-Pair-Id"));
    };


    @Test
    public void generateCloudFrontCookieResponseReturnsCookiesWithCorrectDomainMyGEA()
        throws TechpubsException {
        String portalID = "CWC";
        ResponseEntity response = null;

        response = awsResourcesService.generateCloudFrontCookieResponse(portalID, defaultProgram);
        List<String> headerCookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);

        List headerList =  response.getHeaders().getValuesAsList("Set-Cookie");

        headerList.forEach(cookie -> {
            assertTrue(cookie.toString().contains("dev.my.geaviation.com"));
        });
    }

    @Test
    public void generateCloudFrontCookieResponseReturnsCookiesWithCorrectDomainHonda()
        throws TechpubsException {
        String portalID = "GEHonda";
        ResponseEntity response = null;

        response = awsResourcesService.generateCloudFrontCookieResponse(portalID, defaultProgram);
        List<String> headerCookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);

        List headerList =  response.getHeaders().getValuesAsList("Set-Cookie");

        headerList.forEach(cookie -> {
            assertTrue(cookie.toString().contains("dev.my.gehonda.com"));
        });
    }

    @Test
    public void ensureCloudFrontCookiesConfigurations() throws TechpubsException {
        ResponseEntity response = null;

        response = awsResourcesService.generateCloudFrontCookieResponse(defaultPortalID, defaultProgram);
        List headerList =  response.getHeaders().getValuesAsList("Set-Cookie");

        headerList.forEach(cookie -> {
            assertTrue(cookie.toString().contains("Path=/"));
            assertTrue(cookie.toString().contains("Secure"));
        });

    }

    @Test(expected = TechpubsException.class)
    public void throwTechPubsExceptionIfFailToRetrieveSecretStringFromClient()
        throws TechpubsException {
        when(awsSecretsManagerMock.getSecretValue(any()))
            .thenThrow(new InternalServiceErrorException("Error retrieving secret."));

        awsResourcesService.generateCloudFrontCookieResponse(defaultPortalID, defaultProgram);
    }

    @Test(expected = TechpubsException.class)
    public void throwTechPubsExceptionIfSecretStringIsNull() throws TechpubsException {
        GetSecretValueResult getSecretValueResult = new GetSecretValueResult();
        getSecretValueResult.setSecretString(null);

        when(awsSecretsManagerMock.getSecretValue(any())).thenReturn(getSecretValueResult);

        awsResourcesService.generateCloudFrontCookieResponse(defaultPortalID, defaultProgram);
    }

    @Test(expected = TechpubsException.class)
    public void throwTechPubsExceptionIfErrorParsingSecureString() throws TechpubsException {
        GetSecretValueResult getSecretValueResult = new GetSecretValueResult();
        getSecretValueResult.setSecretString("{\"Bad\": \"json}");

        when(awsSecretsManagerMock.getSecretValue(any())).thenReturn(getSecretValueResult);

        awsResourcesService.generateCloudFrontCookieResponse(defaultPortalID, defaultProgram);
    }

    @Test(expected = TechpubsException.class)
    public void throwTechPubsExceptionIfErrorParsingSecureStringInvalidKey()
        throws TechpubsException {
        GetSecretValueResult getSecretValueResult = new GetSecretValueResult();
        getSecretValueResult.setSecretString(
            "{\"keyPairId\": \"keyPairIdValue\", \"private-Key\": \"privateKeyValue\"}");

        when(awsSecretsManagerMock.getSecretValue(any())).thenReturn(getSecretValueResult);

        awsResourcesService.generateCloudFrontCookieResponse(defaultPortalID, defaultProgram);
    }

    @Test(expected = TechpubsException.class)
    public void throwTechPubsExceptionIfErrorParsingPemKey() throws TechpubsException {
        GetSecretValueResult getSecretValueResult = new GetSecretValueResult();
        getSecretValueResult.setSecretString(
            "{\"keyPairId\": \"keyPairIdValue\", \"privateKey\": \"not a valid pem key value\"}");

        when(awsSecretsManagerMock.getSecretValue(any())).thenReturn(getSecretValueResult);

        awsResourcesService.generateCloudFrontCookieResponse(defaultPortalID, defaultProgram);
    }

    @Test
    public void whenGetAuditLogsHasValidJsonBodyReturnApiGatewayResponse() throws JSONException {
        JSONObject body = new JSONObject();
        body.put("key", "value");

        when(apiGatewayClientMock.execute(any(ApiGatewayRequest.class)).getResult().getBody()).thenReturn("response");

        String response = awsResourcesService.getAuditLogs(body);
        assertEquals("response", response);
    }
}