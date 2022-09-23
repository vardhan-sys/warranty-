package com.geaviation.techpubs.data.client;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ApiGatewayClientBuilderTest {

    @Test
    public void whenClientBuilderIsCorrectReturnReturnHappyResponse() {
        AWSCredentialsProvider credentials = new AWSStaticCredentialsProvider(new BasicAWSCredentials("foo", "bar"));

        ApiGatewayClient client = new ApiGatewayClientBuilder()
                .withClientConfiguration(new ClientConfiguration())
                .withCredentials(credentials)
                .withEndpoint("https://foobar.execute-api.us-east-1.amazonaws.com")
                .withRegion(Region.getRegion(Regions.fromName("us-east-1")))
                .build();

        assertEquals("execute-api", client.getServiceNameIntern());
    }

    @Test
    public void whenClientBuilderHasNoEndpointThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ApiGatewayClientBuilder()
                    .withClientConfiguration(new ClientConfiguration())
                    .withRegion(Region.getRegion(Regions.fromName("us-east-1")))
                    .build();
        });
    }

    @Test
    public void whenClientBuilderHasNoRegionReturnDefualt() {
        AWSCredentialsProvider credentials = new AWSStaticCredentialsProvider(new BasicAWSCredentials("foo", "bar"));

        ApiGatewayClient client = new ApiGatewayClientBuilder()
                .withClientConfiguration(new ClientConfiguration())
                .withCredentials(credentials)
                .withEndpoint("https://foobar.execute-api.us-east-1.amazonaws.com")
                .build();

        assertEquals("us-east-1", client.getSigningRegion());
    }
}
