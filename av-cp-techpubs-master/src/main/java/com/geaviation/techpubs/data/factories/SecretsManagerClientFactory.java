package com.geaviation.techpubs.data.factories;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.geaviation.techpubs.exceptions.TechpubsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SecretsManagerClientFactory {

    @Value("${spring.profiles.active}")
    private String environment;

    @Autowired
    private ClientConfigurationFactory clientConfigurationFactory;

    /**
     * Factory to open AWS Secrets Manager client.  With this client users can interact with Secrets
     * Manager.  If you are running locally the client will be configured with proxy.  Otherwise it
     * will use the default client.
     *
     * @return AWSSecretsManager AWS Secrets Manger client
     * @throws TechpubsException Throws tech pubs exceptions if sso and/or sso password environment
     * variables are not set on users local machine.
     */
    public AWSSecretsManager getSecretsManagerClient() throws TechpubsException {

        if ("local".equals(environment)) {
            return AWSSecretsManagerClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .withClientConfiguration(clientConfigurationFactory.getClientConfigurations())
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();
        } else {
            return AWSSecretsManagerClientBuilder.defaultClient();
        }
    }
}
