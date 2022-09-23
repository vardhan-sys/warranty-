package com.geaviation.techpubs.data.client;

import com.amazonaws.AmazonWebServiceResponse;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.http.AmazonHttpClient;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.http.apache.client.impl.SdkHttpClient;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.HttpContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;

public class ApiGatewayClientTest {

    private ApiGatewayClient client;
    private SdkHttpClient mockClient;

    private static final String ENDPOINT = "https://foobar.execute-api.us-east-1.amazonaws.com";
    private static final String REGION = "us-east-1";

    @BeforeEach
    public void setUp() throws IOException {
        AWSCredentialsProvider credentials = new AWSStaticCredentialsProvider(new BasicAWSCredentials("foo", "bar"));

        mockClient = Mockito.mock(SdkHttpClient.class);
        HttpResponse resp = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK"));
        BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContent(new ByteArrayInputStream("test payload".getBytes()));
        runMockito(resp, entity);

        ClientConfiguration clientConfig = new ClientConfiguration();

        client = new ApiGatewayClientBuilder()
                .withClientConfiguration(clientConfig)
                .withCredentials(credentials)
                .withEndpoint(ENDPOINT)
                .withRegion(Region.getRegion(Regions.fromName(REGION)))
                .withHttpClient(new AmazonHttpClient(clientConfig, mockClient, null))
                .build();
    }

    private void runMockito(HttpResponse resp, BasicHttpEntity entity) throws IOException {
        resp.setEntity(entity);
        Mockito.doReturn(resp).when(mockClient).execute(any(HttpUriRequest.class), any(HttpContext.class));
    }

    @org.junit.jupiter.api.Test
    public void whenHeadersArePassedReturnCorrect200Response() {
        Map<String, String> headers = getHeaders();
        AmazonWebServiceResponse<ApiGatewayResponse> response = getResponse(headers);

        runResponseChecks(response);
    }

    private Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return headers;
    }

    private void runResponseChecks(AmazonWebServiceResponse<ApiGatewayResponse> response) {
        Assertions.assertEquals("test payload", response.getResult().getBody());
        Assertions.assertEquals(200, response.getResult().getHttpResponse().getStatusCode());
    }

    private AmazonWebServiceResponse<ApiGatewayResponse> getResponse(Map<String, String> headers) {
        return client.execute(getRequest(headers));
    }

    @org.junit.jupiter.api.Test
    public void whenHeadersAndParametersArePassedReturnCorrect200Response() throws IOException {
        Map<String, String> headers = getHeaders();
        Map<String, List<String>> parameters = new HashMap<>();
        parameters.put("key", Collections.singletonList("value"));
        AmazonWebServiceResponse<ApiGatewayResponse> response = client.execute(getRequest(headers).withParameters(parameters));

        runResponseChecks(response);
    }

    private ApiGatewayRequest getRequest(Map<String, String> headers) {
        return getBaseRequest()
                .withHeaders(headers);
    }

    private ApiGatewayRequest getBaseRequest() {
        return new ApiGatewayRequest()
                .withBody(new ByteArrayInputStream("test request".getBytes()))
                .withHttpMethod(HttpMethodName.POST)
                .withResourcePath("/test/orders");
    }

    @org.junit.jupiter.api.Test
    public void testExecuteNoApiKeyNoCreds() {
        client = new ApiGatewayClientBuilder()
                .withEndpoint(ENDPOINT)
                .withRegion(Region.getRegion(Regions.fromName(REGION)))
                .withClientConfiguration(new ClientConfiguration())
                .withHttpClient(new AmazonHttpClient(new ClientConfiguration(), mockClient, null))
                .build();

        AmazonWebServiceResponse<ApiGatewayResponse> response = client.execute(getBaseRequest());

        runResponseChecks(response);
    }

    @Test
    public void testExecuteNon2xxException() throws IOException {
        HttpResponse resp = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 404, "Not found"));
        BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContent(new ByteArrayInputStream("{\"message\" : \"error payload\"}".getBytes()));
        runMockito(resp, entity);

        Map<String, String> headers = getHeaders();

        try {
            getResponse(headers);
            Assertions.fail("Expected exception");
        } catch (ApiGatewayException e) {
            Assertions.assertEquals(404, e.getStatusCode());
            Assertions.assertEquals("{\"message\":\"error payload\"}", e.getErrorMessage());
        }
    }
}