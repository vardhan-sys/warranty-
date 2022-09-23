package com.geaviation.techpubs.data.factories;

import com.geaviation.techpubs.data.client.ApiGatewayClient;
import com.geaviation.techpubs.data.client.ApiGatewayClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApiGatewayClientFactory {

    @Value("${AUDIT.TRAIL.APIGW.API_ID}")
    private String auditTrailApiId;

    @Value("${APIGW.VPC_ENDPOINT_ID}")
    private String apigwVpcEndpointId;

    @Value("${AUDIT.TRAIL.APIGW.STAGE}")
    private String auditTrailApiStage;

    public ApiGatewayClient getApiGatewayClient() {

        String apiGatewayEndpoint = "https://" + auditTrailApiId + "-" + apigwVpcEndpointId + ".execute-api.us-east-1.amazonaws.com/" + auditTrailApiStage;

        return ApiGatewayClientBuilder.standard()
                .withEndpoint(apiGatewayEndpoint)
                .build();
    }
}