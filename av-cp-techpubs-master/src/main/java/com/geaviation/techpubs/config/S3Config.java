package com.geaviation.techpubs.config;


import com.amazonaws.services.s3.AmazonS3;
import com.geaviation.techpubs.data.factories.AmazonS3ClientFactory;
import com.geaviation.techpubs.exceptions.TechpubsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "s3")
public class S3Config {

  private S3Bucket s3Bucket;

  @Autowired
  private AmazonS3ClientFactory clientFactory;

  @Bean
  @Primary
  public AmazonS3 s3Client() throws TechpubsException {
    return clientFactory.getS3Client();
  }

  /**
   * Solo API - Online links from ESM to IPC return gibberish
   *
   * @see <a href="US366238">https://rally1.rallydev.com/#/342957100028/detail/userstory/370612240212</a>
   */
  public static class S3Bucket {
    // Flags should ALWAYS be disabled by default and set to true in properties.
    // This protects the developer from forgetting to set the feature flag property,
    // causing the service not to start.
    private String bucketName = "";

    public String getBucketName() { return this.bucketName; }
    public void setBucketName(String name) { this.bucketName = name; }
  }

  public S3Bucket getS3Bucket() { return s3Bucket; }
  public void setS3Bucket(S3Bucket s3Bucket) { this.s3Bucket = s3Bucket; }

}
