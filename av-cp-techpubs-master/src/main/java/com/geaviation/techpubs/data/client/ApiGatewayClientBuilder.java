package com.geaviation.techpubs.data.client;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.http.AmazonHttpClient;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import org.springframework.stereotype.Component;

/**
 * Fluent builder for {@link com.geaviation.techpubs.data.client.ApiGatewayClient}.
 **/
@Component
public class ApiGatewayClientBuilder {

    private String endpoint;
    private Region region;
    private AWSCredentialsProvider credentials;
    private ClientConfiguration clientConfiguration;
    private AmazonHttpClient httpClient;

    /**
     * Static constructor for ApiGatewayClientBuilder
     *
     * @return ApiGatewayClientBuilder
     */
    public static ApiGatewayClientBuilder standard() { return new ApiGatewayClientBuilder(); }

    /**
     * Specify the API Gateway vpc endpoint url.
     *
     * @param endpoint the vpc endpoint url to be used in the request.
     * @return ApiGatewayClientBuilder
     */
    public ApiGatewayClientBuilder withEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    /**
     * (Optional) Specify the region used in the request signing. Uses the us-east-1 region by default.
     *
     * @param region The region
     * @return ApiGatewayClientBuilder
     */
    public ApiGatewayClientBuilder withRegion(Region region) {
        this.region = region;
        return this;
    }

    /**
     * (Optional) Specify the client configuration. Uses the basic configuration by default.
     *
     * @param clientConfiguration The client configuration
     * @return ApiGatewayClientBuilder
     */
    public ApiGatewayClientBuilder withClientConfiguration(ClientConfiguration clientConfiguration) {
        this.clientConfiguration = clientConfiguration;
        return this;
    }

    /**
     * Specify an implementation of {@link AWSCredentialsProvider} to be used when signing IAM auth'd requests.
     *
     * @param credentials The credential provider
     * @return ApiGatewayClientBuilder
     */
    public ApiGatewayClientBuilder withCredentials(AWSCredentialsProvider credentials) {
        this.credentials = credentials;
        return this;
    }

    /**
     * (Optional) Specify the HTTP client to use. Uses the {@link com.amazonaws.http.AmazonHttpClient} by default.
     *
     * @param client The HTTP client to use.
     * @return ApiGatewayClientBuilder
     */
    public ApiGatewayClientBuilder withHttpClient(AmazonHttpClient client) {
        this.httpClient = client;
        return this;
    }

    /**
     * Gets the AWS Credentials Provider.
     *
     * @return AWSCredentialsProvider
     */
    public AWSCredentialsProvider getCredentials() {
        return credentials;
    }

    /**
     * Gets the Amazon HTTP Client
     *
     * @return The HTTP client
     */
    public AmazonHttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * Gets the API Gateway endpoint
     *
     * @return The endpoint
     */
    public String getEndpoint() {
        return endpoint;
    }

    /**
     * Gets the Region used for request signing
     *
     * @return The region
     */
    public Region getRegion() {
        return region;
    }

    /**
     * Gets the Client Configuration
     *
     * @return The client configuration
     */
    public ClientConfiguration getClientConfiguration() {
        return clientConfiguration;
    }

    /**
     * Builds the Api Gateway Client
     *
     * @return The API Gateway Client
     */
    public ApiGatewayClient build() {
        if (region == null || region.getName().isEmpty()) {
            region = Region.getRegion(Regions.fromName("us-east-1"));
        }

        if (clientConfiguration == null) {
            clientConfiguration = new ClientConfiguration();
        }

        if (credentials == null) {
            credentials = new DefaultAWSCredentialsProviderChain();
        }

        if (endpoint == null || endpoint.isEmpty()) {
            throw new IllegalArgumentException("Endpoint cannot be null or empty");
        }

        return new ApiGatewayClient(clientConfiguration, endpoint, region, credentials, httpClient);
    }
}
