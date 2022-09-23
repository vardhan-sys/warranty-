package com.geaviation.techpubs.model.download;

import com.geaviation.techpubs.models.download.OverlayDownloadFile;
import com.geaviation.techpubs.models.download.OverlayDownloadRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class OverlayDownloadRequestTest {

    private OverlayDownloadRequest request;
    private String filesStringSingleFile = "sbs:test_file.zip";
    private String filesStringMultifile = "sbs:test_file1.zip|sbs:test_file2.zip|sbs:test_file3.zip";

    @Before
    public void setup() {
        request = new OverlayDownloadRequest();
    }

    @Test
    public void requestWithZeroFilesHasFileCountOfZero() {
        Assert.assertEquals(0, request.fileCount());
        Assert.assertEquals(0, request.getFiles().size());
    }

    @Test
    public void requestWithSingleFileHasFileCountOfOne() {
        request.setFiles(filesStringSingleFile);

        Assert.assertEquals(1, request.fileCount());
        Assert.assertEquals(1, request.getFiles().size());
    }

    @Test
    public void requestWithSingleFileHasFilesWithSingleOverlayDownloadFileObject() {
        request.setFiles(filesStringSingleFile);

        List<OverlayDownloadFile> files = request.getFiles();
        OverlayDownloadFile file = files.get(0);

        Assert.assertNotNull(file);
        Assert.assertEquals("sbs", file.getManual());
        Assert.assertEquals("test_file.zip", file.getName());
    }

    @Test
    public void requestWithSingleFileReturnsFilesAsStringWithSingleFile() {
        request.setFiles(filesStringSingleFile);

        Assert.assertEquals(filesStringSingleFile, request.getFilesAsString());
    }

    @Test
    public void requestWithMultiFileHasFileCountOfOne() {
        request.setFiles(filesStringMultifile);

        Assert.assertEquals(3, request.fileCount());
        Assert.assertEquals(3, request.getFiles().size());
    }

    @Test
    public void requestWithMultiFileHasFilesWithMultipleOverlayDownloadFileObjects() {
        request.setFiles(filesStringMultifile);

        List<OverlayDownloadFile> files = request.getFiles();
        OverlayDownloadFile file1 = files.get(0);
        OverlayDownloadFile file2 = files.get(1);
        OverlayDownloadFile file3 = files.get(2);

        Assert.assertNotNull(file1);
        Assert.assertEquals("sbs", file1.getManual());
        Assert.assertEquals("test_file1.zip", file1.getName());

        Assert.assertNotNull(file2);
        Assert.assertEquals("sbs", file2.getManual());
        Assert.assertEquals("test_file2.zip", file2.getName());

        Assert.assertNotNull(file3);
        Assert.assertEquals("sbs", file3.getManual());
        Assert.assertEquals("test_file3.zip", file3.getName());
    }

    @Test
    public void requestWithMultiFileReturnsFilesAsStringWithSingleFile() {
        request.setFiles(filesStringMultifile);

        Assert.assertEquals(filesStringMultifile, request.getFilesAsString());
    }
}
