package com.geaviation.techpubs.data.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.geaviation.techpubs.config.S3Config;
import com.geaviation.techpubs.data.s3.Util;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.ProgramItemModel;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AwsS3Service {
    private static final Logger logger = LogManager.getLogger(AwsS3Service.class);

    @Autowired
    private S3Config s3Config;

    public S3Object getS3Object(AmazonS3 s3Client, String bucketName, String objKey) throws TechpubsException {
        S3Object s3Object = null;

        try {
            s3Object = s3Client.getObject(new GetObjectRequest(bucketName, objKey));
            String fileSize = FileUtils.byteCountToDisplaySize(s3Object.getObjectMetadata().getContentLength());
            logger.info("File: {}, Size: {}", s3Object.getKey(), fileSize);
        } catch (AmazonServiceException e) {
            logger.error(e.getMessage());
            throw new TechpubsException(TechpubsException.TechpubsAppError.RESOURCE_NOT_FOUND, "S3 Key: " + objKey);
        } catch (SdkClientException e) {
            logger.error(e.getMessage());
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
        }

        return s3Object;
    }

    public S3Object getS3Object(AmazonS3 s3Client, ProgramItemModel programItem, String manual, String filename) throws TechpubsException {
        String bucketName = s3Config.getS3Bucket().getBucketName();
        String s3ObjKey = Util.createVersionFolderS3ObjKey(programItem, manual, filename);

        if (!s3Client.doesObjectExist(bucketName, s3ObjKey)) {
            s3ObjKey = Util.createProgramFolderS3ObjKey(programItem, manual, filename);
        }

        return getS3Object(s3Client, bucketName, s3ObjKey);
    }
}
