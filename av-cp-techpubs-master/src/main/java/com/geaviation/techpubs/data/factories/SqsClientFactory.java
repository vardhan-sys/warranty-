package com.geaviation.techpubs.data.factories;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.geaviation.techpubs.exceptions.TechpubsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SqsClientFactory {

    @Value("${spring.profiles.active}")
    private String environment;

    @Autowired
    private ClientConfigurationFactory clientConfigurationFactory;

    /**
     * Factory to open AWS SQS client.  With this client users can interact with SQS.
     * If you are running locally the client will be configured with proxy.  Otherwise it
     * will use the default client.
     *
     * @return AmazonSQS AWS SQS client
     * @throws TechpubsException Throws tech pubs exceptions if sso and/or sso password environment
     * variables are not set on users local machine.
     */
    public AmazonSQS getSqsClient() throws TechpubsException {
      // running local connects to us-east-1
      if ("local".equals(environment)) {
        return AmazonSQSClientBuilder.standard()
            .withClientConfiguration(clientConfigurationFactory.getClientConfigurations())
            .withCredentials(new DefaultAWSCredentialsProviderChain())
            .withRegion(Regions.US_EAST_1)
            .build();
      } else {
        return AmazonSQSClientBuilder.defaultClient();
      }
    }
}
