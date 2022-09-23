package com.geaviation.techpubs.config;

import com.amazonaws.services.s3.AmazonS3;
import com.geaviation.techpubs.data.factories.AmazonS3ClientFactory;
import com.geaviation.techpubs.exceptions.TechpubsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "archives3")
public class ArchivalS3Config {

    private S3Bucket s3Bucket;

    @Autowired
    private AmazonS3ClientFactory clientFactory;

    @Bean
    public AmazonS3 ArchivalS3Config() throws TechpubsException {
        return clientFactory.getS3Client();
    }

    public static class S3Bucket {
        private String bucketName = "";

        public String getBucketName() { return this.bucketName; }
        public void setBucketName(String name) { this.bucketName = name; }
    }

    public S3Bucket getS3Bucket() { return s3Bucket; }
    public void setS3Bucket(S3Bucket s3Bucket) { this.s3Bucket = s3Bucket; }

}
