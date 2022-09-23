package com.geaviation.techpubs.service.impl;

import com.geaviation.techpubs.models.download.OverlayDownloadFile;
import com.geaviation.techpubs.models.download.OverlayDownloadRequest;
import com.geaviation.techpubs.services.impl.download.OverlayDownload;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class OverlayDownloadTest {
    public static final String SSO_ID = "123456789";
    public static final String PORTAL_ID = "cwc";
    public static final String PROGRAM = "gek12345";
    public static final String DOWNLOAD_TYPE_SOURCE = "source";
    public static final String DOWNLOAD_TYPE_DVD = "dvd";
    public static final String TYPE = "sbs";
    public static final String MANUAL = "sbs";
    public static final String FILE_1 = "file1.zip";
    public static final String FILE_2 = "file2.zip";

    private OverlayDownloadRequest requestMultipleFiles;
    private OverlayDownloadRequest requestMultipleFilesDvd;
    private OverlayDownloadRequest requestSingleFile;
    private OverlayDownloadRequest requestSingleFileDvd;
    private final Instant defaultTime = Instant.now();

    @InjectMocks
    private OverlayDownload overlayDownload;

    @Before
    public void setup() {
        List<OverlayDownloadFile> singleFile = new ArrayList<>();
        singleFile.add(new OverlayDownloadFile(MANUAL, FILE_1));

        requestSingleFile = new OverlayDownloadRequest(PROGRAM, DOWNLOAD_TYPE_SOURCE, TYPE, singleFile);
        requestSingleFileDvd = new OverlayDownloadRequest(PROGRAM, DOWNLOAD_TYPE_DVD, TYPE, singleFile);

        List<OverlayDownloadFile> multipleFiles = new ArrayList<>();
        multipleFiles.add(new OverlayDownloadFile(MANUAL, FILE_1));
        multipleFiles.add(new OverlayDownloadFile(MANUAL, FILE_2));

        requestMultipleFiles = new OverlayDownloadRequest(PROGRAM, DOWNLOAD_TYPE_SOURCE, TYPE, multipleFiles);
        requestMultipleFilesDvd = new OverlayDownloadRequest(PROGRAM, DOWNLOAD_TYPE_DVD, TYPE, multipleFiles);
    }

    @Test
    public void zipFileNameRequestWithSingleFile() {
        String fileName = overlayDownload.zipFileName(SSO_ID, requestSingleFile);

        Assert.assertEquals(FILE_1, fileName);
    }

    @Test
    public void zipFileNameRequestWithSingleFileDvd() {
        try (MockedStatic<Instant> mock = Mockito.mockStatic(Instant.class)) {
            mock.when(Instant::now).thenReturn(defaultTime);

            String fileName = overlayDownload.zipFileName(SSO_ID, requestSingleFileDvd);

            String testFile = String.format("123456789_gek12345_dvd_%s.geae", defaultTime.getEpochSecond());
            Assert.assertEquals(testFile, fileName);
        }
    }

    @Test
    public void zipFileNameRequestWithMultipleFiles() {
        try (MockedStatic<Instant> mock = Mockito.mockStatic(Instant.class)) {
            mock.when(Instant::now).thenReturn(defaultTime);

            String fileName = overlayDownload.zipFileName(SSO_ID, requestMultipleFiles);

            String testFile = String.format("123456789_gek12345_source_%s.zip", defaultTime.getEpochSecond());
            Assert.assertEquals(testFile, fileName);

            fileName = overlayDownload.zipFileName(SSO_ID, requestMultipleFilesDvd);
            testFile = String.format("123456789_gek12345_dvd_%s.geae", defaultTime.getEpochSecond());
            Assert.assertEquals(testFile, fileName);
        }
    }
}
