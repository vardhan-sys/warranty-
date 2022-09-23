package com.geaviation.techpubs.data.factories;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.geaviation.techpubs.exceptions.TechpubsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AmazonS3ClientFactory {

  @Value("${spring.profiles.active}")
  private String environment;

  @Autowired
  private ClientConfigurationFactory configurationFactory;

  /**
   * Factory to open AWS Secrets Manager client.  With this client users can interact with Secrets
   * Manager.  If you are running locally the client will be configured with proxy.  Otherwise it
   * will use the default client.
   *
   * @return AWSSecretsManager AWS Secrets Manger client
   * @throws TechpubsException Throws tech pubs exceptions if sso and/or sso password environment
   * variables are not set on users local machine.
   */
  public AmazonS3 getS3Client() throws TechpubsException {

    if ("local".equals(environment)) {

      System.setProperty("com.amazonaws.sdk.disableCertChecking", "true");

      return AmazonS3ClientBuilder.standard()
          .withRegion(Regions.US_EAST_1)
          .withClientConfiguration(configurationFactory.getClientConfigurations())
          .withCredentials(new DefaultAWSCredentialsProviderChain())
          .build();
    } else {
      return AmazonS3ClientBuilder.defaultClient();
    }
  }

  /**
   * Factory to get S3 client with a configured number of max connections set. To be used in conjunction with
   * asynchronous calls to S3.
   *
   * @return AmazonS3 Configured S3 client
   */
  public AmazonS3 getAsyncS3Client() {
    AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard()
            .withRegion(Regions.US_EAST_1);

    if ("local".equals(environment)) {
      System.setProperty("com.amazonaws.sdk.disableCertChecking", "true");
      builder.withClientConfiguration(configurationFactory.getLocalAsyncClientConfiguration())
              .withCredentials(new DefaultAWSCredentialsProviderChain());
    } else {
      builder.withClientConfiguration(configurationFactory.getRemoteAsyncClientConfiguration());
    }

    return builder.build();
  }
}