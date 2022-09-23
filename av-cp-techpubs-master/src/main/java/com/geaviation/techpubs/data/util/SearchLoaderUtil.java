package com.geaviation.techpubs.data.util;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.geaviation.techpubs.config.AwsConfig;
import com.geaviation.techpubs.config.S3Config;
import com.geaviation.techpubs.data.model.payload.AvsystemSearchLambdaEvent;
import com.geaviation.techpubs.data.model.payload.PageblkLoaderEvent;
import com.geaviation.techpubs.models.S3PutEvent;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class SearchLoaderUtil {

  @Value("${aws.isLocal:false}")
  private boolean isLocal;

  @Value("${SearchIndexConverterLambda}")
  private String searchIndexConverter;

  @Value("${PageBlkUpdateIndexLambda}")
  private String pageBlkUpdateIndex;

  @Value("${aws.lambda.avsystemSearchLoader}")
  private String avsystemIndexLambda;

  @Autowired
  private S3Config s3Config;

  private static final Logger log = LogManager.getLogger(SearchLoaderUtil.class);

  /**
   * Invokes av-cp-techpubs-invoke-search lambda function to index bookcase version in Elastic Search
   * @param bookcase Bookcase to indexed
   * @param version Version of bookcase to index
   * @return The result of invoking AWS Lambda function
   */
  public InvokeResult invokeSearchLoader(String bookcase, String version) {
    InvokeResult res = null;

    AWSLambda client = buildClient();

    try {
      String key = bookcase + "/" + version + "/xml/toc.xml";
      S3PutEvent event = new S3PutEvent(s3Config.getS3Bucket().getBucketName(), key);

      //Setup an InvokeRequest
      InvokeRequest request = new InvokeRequest()
              .withFunctionName(searchIndexConverter)
              .withPayload(event.toString())
              .withInvocationType(InvocationType.Event);

      //Invoke the Lambda function
      res = client.invoke(request);
      int value = res.getStatusCode();
      log.info("Lambda invocation status code: {}", value);

    } catch (Exception e) {
      log.error(e.getMessage());
    }

    return res;
  }

  /**
   * Invokes av-cp-techpubs-update-search-pageblk-index lambda function to update pageblk version in Elastic Search
   * @param bookcase Bookcase to indexed
   * @param version Version of bookcase to index
   * @param book Book to indexed
   * @param bookType Book type of bookcase to index
   * @param bookType File name type to index
   * @return The result of invoking AWS Lambda function
   */
  public InvokeResult invokePageblkUpdateLoader(String bookcase, String book, String bookType, String version, String fileName) {
    InvokeResult res = null;

    AWSLambda client = buildClient();

    PageblkLoaderEvent pageblkLoaderEvent = new PageblkLoaderEvent();
    pageblkLoaderEvent.setBookcase(bookcase);
    pageblkLoaderEvent.setBook(book);
    pageblkLoaderEvent.setBookType(bookType);
    pageblkLoaderEvent.setVersion(version);
    pageblkLoaderEvent.setFileName(fileName);

    try {
      //Setup an InvokeRequest
      InvokeRequest request = new InvokeRequest()
              .withFunctionName(pageBlkUpdateIndex)
              .withPayload(pageblkLoaderEvent.toString())
              .withInvocationType(InvocationType.Event);

      //Invoke the Lambda function
      res = client.invoke(request);
      int value = res.getStatusCode();
      log.info("Lambda invocation status code: {}", value);

    } catch (Exception e) {
      log.error(e.getMessage());
    }

    return res;
  }

  /**
   *
   * @param id
   * @return
   */
  public InvokeResult invokeAvsystemSearchLoader(String id) {
    InvokeResult res = null;

    AWSLambda client = buildClient();
    AvsystemSearchLambdaEvent event = new AvsystemSearchLambdaEvent();
    event.setId(id);

    try {
      Gson gson = new Gson();
      String eventJson = gson.toJson(event);
      //Setup an InvokeRequest
      InvokeRequest request = new InvokeRequest()
              .withFunctionName(avsystemIndexLambda)
              .withPayload(eventJson)
              .withInvocationType(InvocationType.RequestResponse);

      res = client.invoke(request);

      if (res.getFunctionError() != null) {
        log.error("An error occurred while indexing document {}", id);
      } else {
        log.info("Indexed document {}", id);
      }

    } catch (Exception e) {
      log.error(e.getMessage());
    }

    return res;
  }

  private AWSLambda buildClient() {
    AWSLambdaClientBuilder clientBuilder = AWSLambdaClientBuilder
            .standard()
            .withRegion(Regions.US_EAST_1);

    if (isLocal) {
      clientBuilder = clientBuilder.withClientConfiguration(AwsConfig.getLocalClientConfiguration());
    }

    return clientBuilder.build();
  }

}
