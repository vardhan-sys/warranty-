package com.geaviation.techpubs.models;

public class S3PutEvent {
    private String bucket;
    private String objectKey;

    public S3PutEvent(String bucket, String objectKey) {
        this.bucket = bucket;
        this.objectKey = objectKey;
    }

    @Override
    public String toString() {
        return "{\n" +
                "  \"Records\": [\n" +
                "    {\n" +
                "      \"eventVersion\": \"2.0\",\n" +
                "      \"eventSource\": \"aws:s3\",\n" +
                "      \"awsRegion\": \"{region}\",\n" +
                "      \"eventTime\": \"1970-01-01T00:00:00Z\",\n" +
                "      \"eventName\": \"ObjectCreated:Put\",\n" +
                "      \"userIdentity\": {\n" +
                "        \"principalId\": \"EXAMPLE\"\n" +
                "      },\n" +
                "      \"requestParameters\": {\n" +
                "        \"sourceIPAddress\": \"127.0.0.1\"\n" +
                "      },\n" +
                "      \"responseElements\": {\n" +
                "        \"x-amz-request-id\": \"EXAMPLE123456789\",\n" +
                "        \"x-amz-id-2\": \"EXAMPLE123/5678abcdefghijklambdaisawesome/mnopqrstuvwxyzABCDEFGH\"\n" +
                "      },\n" +
                "      \"s3\": {\n" +
                "        \"s3SchemaVersion\": \"1.0\",\n" +
                "        \"configurationId\": \"testConfigRule\",\n" +
                "        \"bucket\": {\n" +
                "          \"name\":  \"" + this.bucket + "\",\n" +
                "          \"ownerIdentity\": {\n" +
                "            \"principalId\": \"EXAMPLE\"\n" +
                "          },\n" +
                "          \"arn\": \"arn:{partition}:s3:::mybucket\"\n" +
                "        },\n" +
                "        \"object\": {\n" +
                "          \"key\":  \"" + this.objectKey + "\",\n" +
                "          \"size\": 1024,\n" +
                "          \"eTag\": \"0123456789abcdef0123456789abcdef\",\n" +
                "          \"sequencer\": \"0A1B2C3D4E5F678901\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }
}
