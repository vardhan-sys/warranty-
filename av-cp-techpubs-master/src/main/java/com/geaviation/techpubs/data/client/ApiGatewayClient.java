package com.geaviation.techpubs.data.client;

import com.amazonaws.*;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.http.*;
import com.amazonaws.internal.auth.DefaultSignerProvider;
import com.amazonaws.protocol.json.JsonOperationMetadata;
import com.amazonaws.protocol.json.SdkStructuredPlainJsonFactory;
import com.amazonaws.regions.Region;
import com.amazonaws.transform.JsonErrorUnmarshaller;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiGatewayClient extends AmazonWebServiceClient {
    private static final String API_GATEWAY_SERVICE_NAME = "execute-api";

    private final JsonResponseHandler<ApiGatewayResponse> responseHandler;
    private final HttpResponseHandler<AmazonServiceException> errorResponseHandler;
    private final AWSCredentialsProvider credentials;
    private final String endpoint;
    private final AWS4Signer signer;

    ApiGatewayClient(ClientConfiguration clientConfiguration, String endpoint, Region region,
                     AWSCredentialsProvider credentials, AmazonHttpClient httpClient) {
        super(clientConfiguration);
        this.endpoint = endpoint;
        this.credentials = credentials;
        this.signer = new AWS4Signer();
        this.signer.setServiceName(API_GATEWAY_SERVICE_NAME);
        this.signer.setRegionName(region.getName());

        final JsonOperationMetadata metadata = new JsonOperationMetadata().withHasStreamingSuccessResponse(false).withPayloadJson(false);
        final Unmarshaller<ApiGatewayResponse, JsonUnmarshallerContext> responseUnmarshaller = in -> new ApiGatewayResponse(in.getHttpResponse());
        this.responseHandler = SdkStructuredPlainJsonFactory.SDK_JSON_FACTORY.createResponseHandler(metadata, responseUnmarshaller);
        JsonErrorUnmarshaller defaultErrorUnmarshaller = new JsonErrorUnmarshaller(ApiGatewayException.class, null) {
            @Override
            public AmazonServiceException unmarshall(JsonNode jsonContent) {
                return new ApiGatewayException(jsonContent.toString());
            }
        };
        this.errorResponseHandler = SdkStructuredPlainJsonFactory.SDK_JSON_FACTORY.createErrorResponseHandler(
                Collections.singletonList(defaultErrorUnmarshaller), null);

        if (httpClient != null) {
            super.client = httpClient;
        }
    }

    public AmazonWebServiceResponse<ApiGatewayResponse> execute(ApiGatewayRequest request) {
        return execute(request.getHttpMethod(), request.getResourcePath(), request.getHeaders(), request.getParameters(), request.getBody());
    }

    private AmazonWebServiceResponse<ApiGatewayResponse> execute(
            HttpMethodName method, String resourcePath, Map<String, String> headers, Map<String, List<String>> parameters, InputStream content) {
        final ExecutionContext executionContext = buildExecutionContext();

        DefaultRequest request = new DefaultRequest(API_GATEWAY_SERVICE_NAME);
        request.setHttpMethod(method);
        request.setContent(content);
        request.setEndpoint(URI.create(this.endpoint));
        request.setResourcePath(resourcePath);
        request.setHeaders(buildRequestHeaders(headers));
        if (parameters != null) {
            request.setParameters(parameters);
        }
        return this.client.requestExecutionBuilder()
                .request(request)
                .executionContext(executionContext)
                .errorResponseHandler(errorResponseHandler)
                .execute(responseHandler).getAwsResponse();
    }

    private ExecutionContext buildExecutionContext() {
        final ExecutionContext executionContext = ExecutionContext.builder().withSignerProvider(
                new DefaultSignerProvider(this, signer)).build();
        executionContext.setCredentialsProvider(credentials);
        return executionContext;
    }

    private Map<String, String> buildRequestHeaders(Map<String, String> headers) {
        if (headers == null) {
            return new HashMap<>();
        } else {
            return headers;
        }
    }

    @Override
    protected String getServiceNameIntern() {
        return API_GATEWAY_SERVICE_NAME;
    }

    @Override
    protected String getSigningRegion() { return signer.getRegionName(); }
}