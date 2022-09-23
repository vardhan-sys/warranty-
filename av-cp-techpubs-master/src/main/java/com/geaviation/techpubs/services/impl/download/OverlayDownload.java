package com.geaviation.techpubs.services.impl.download;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.geaviation.techpubs.data.api.IProgramData;
import com.geaviation.techpubs.data.factories.AmazonS3ClientFactory;
import com.geaviation.techpubs.data.impl.AwsS3Service;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.ProgramItemModel;
import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.models.download.OverlayDownloadFile;
import com.geaviation.techpubs.models.download.OverlayDownloadRequest;
import com.geaviation.techpubs.services.api.IProgramApp;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class OverlayDownload {

    private static final Logger logger = LogManager.getLogger(OverlayDownload.class);

    private static final String CLOSE = ")";
    private static final String CLOSE_DASH = ") - ";
    private static final String COMMA = ",";
    private static final String DOWNLOAD_TYPE = ",downloadType=";
    private static final String DVD = "dvd";
    private static final String DVD_INFO_FILENAME = "info.txt";
    private static final String FILENAMES = ",filenames=";
    private static final String GEAE = "geae";
    private static final String OVERLAY_DOWNLOAD_GET_RESOURCE = "OverlayDownload.downloadResources - ";
    private static final String PORTAL_ID = ",portalId=";
    private static final String PROGRAM_EQ = " (program=";
    private static final String SB = "sb";
    private static final String SOURCE = "source";
    private static final String SSO_ID = "(ssoId=";
    private static final String TYPE5 = ",type =";
    private static final String ZIP = "zip";

    @Autowired
    private AwsS3Service s3Service;

    @Autowired
    private IProgramApp programApp;

    @Autowired
    private IProgramData programData;

    @Autowired
    private AmazonS3ClientFactory clientFactory;

    @Value("${OVERLAY.DOWNLOAD.THREADS}")
    private Integer downloadThreads;

    public StreamingResponseBody downloadResources(OverlayDownloadRequest request) throws TechpubsException {

        ProgramItemModel programItem = null;

        try {
            programItem = programData.getProgramItem(request.getProgram(), SubSystem.TD);
        } catch (DocumentException | IOException e) {
            logger.error(e);
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
        }

        AmazonS3 s3Client = clientFactory.getAsyncS3Client();
        StreamingResponseBody output;

        if (SOURCE.equalsIgnoreCase(request.getDownloadType()) && request.fileCount() == 1) {
            output = singleFileDownload(s3Client, programItem, request);
        } else {
            output = multipleFileDownload(s3Client, programItem, request);
        }

        return output;
    }

    private StreamingResponseBody singleFileDownload(AmazonS3 s3Client, ProgramItemModel programItem, OverlayDownloadRequest request) throws TechpubsException {
        OverlayDownloadFile file = request.getFiles().get(0);
        S3Object s3Object = s3Service.getS3Object(s3Client, programItem, file.getManual(), file.getName());
        return singleFileOutput(s3Object);
    }

    private StreamingResponseBody multipleFileDownload(AmazonS3 s3Client, ProgramItemModel programItem, OverlayDownloadRequest request) throws TechpubsException {
        ExecutorService executors = Executors.newFixedThreadPool(downloadThreads);
        List<CompletableFuture<S3Object>> downloadFutures = downloadFutures(s3Client, programItem, request.getFiles(), executors);
        StreamingResponseBody output = multiFileOutput(downloadFutures, programItem, request);
        executors.shutdown();
        return output;
    }

    private List<CompletableFuture<S3Object>> downloadFutures(AmazonS3 s3Client, ProgramItemModel programItem, List<OverlayDownloadFile> files, ExecutorService executors) throws TechpubsException {
        List<CompletableFuture<S3Object>> s3Objects = new ArrayList<>();

        for(OverlayDownloadFile file : files) {
            if (StringUtils.isEmpty(file.getManual()) || StringUtils.isEmpty(file.getName())) {
                logger.error(OVERLAY_DOWNLOAD_GET_RESOURCE + "Invalid manual:filename" + PROGRAM_EQ + programItem.getProgramDocnbr()
                        + ",filename=" + file + CLOSE);
                throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
            }
            CompletableFuture<S3Object> future = CompletableFuture.supplyAsync(() -> {
                S3Object s3Object = null;

                try {
                    s3Object = s3Service.getS3Object(s3Client, programItem, file.getManual(), file.getName());
                } catch (TechpubsException e) {
                    throw new CompletionException(e);
                }

                return s3Object;
            }, executors);

            s3Objects.add(future);
        }

        return s3Objects;
    }

    private StreamingResponseBody singleFileOutput(S3Object s3Object) throws TechpubsException {
        return response -> {
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            IOUtils.copy(inputStream, response);
            inputStream.close();
            s3Object.close();
            response.flush();
        };

    }

    private StreamingResponseBody multiFileOutput(List<CompletableFuture<S3Object>> downloadFutures, ProgramItemModel programItem, OverlayDownloadRequest request) throws TechpubsException {
        return response -> {
            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response);
                 ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream)) {
                for (CompletableFuture<S3Object> objectFuture : downloadFutures) {
                    S3Object s3Object = null;
                    try {
                        s3Object = objectFuture.join();

                        S3ObjectInputStream inputStream = s3Object.getObjectContent();
                        String[] s3Key = s3Object.getKey().split("/");
                        String filename = s3Key[s3Key.length - 1];

                        zipOutputStream.putNextEntry(new ZipEntry(filename));
                        IOUtils.copy(inputStream, zipOutputStream);
                        zipOutputStream.closeEntry();

                        inputStream.close();
                        s3Object.close();
                    } catch (CompletionException e) {
                        logger.error(e);
                    }
                }

                if (DVD.equalsIgnoreCase(request.getDownloadType())) {
                    String infoText = programItem.getDvdInfoTxt() + COMMA;
                    if (!SB.equalsIgnoreCase(request.getType())) {
                        infoText += programItem.getDvdVersion();
                    }

                    try (InputStream inputStream = new ByteArrayInputStream(infoText.getBytes())) {
                        zipOutputStream.putNextEntry(new ZipEntry(DVD_INFO_FILENAME));
                        IOUtils.copy(inputStream, zipOutputStream);
                        zipOutputStream.closeEntry();
                    }
                }

                zipOutputStream.flush();
            }
        };
    }

    public String zipFileName(String ssoId, OverlayDownloadRequest request) {
        if (SOURCE.equalsIgnoreCase(request.getDownloadType()) && request.fileCount() == 1) {
            List<OverlayDownloadFile> files = request.getFiles();
            OverlayDownloadFile file = files.get(0);
            return file.getName();
        } else {
            Instant moment = Instant.now();
            String extension = (DVD.equals(request.getDownloadType()) ? GEAE : ZIP);
            return String.format("%s_%s_%s_%d.%s", ssoId, request.getProgram(), request.getDownloadType(), moment.getEpochSecond(), extension);
        }
    }
}
