package com.geaviation.techpubs.data.impl;

import com.geaviation.techpubs.data.api.IResourceData;
import com.geaviation.techpubs.data.api.techlib.IBookcaseVersionData;
import com.geaviation.techpubs.data.api.techlib.IPageblkLookupData;
import com.geaviation.techpubs.data.html.filter.BrowserSupportFilter;
import com.geaviation.techpubs.data.html.filter.HTMLWriterFilter;
import com.geaviation.techpubs.data.html.filter.UpdateResourceFilter;
import com.geaviation.techpubs.data.html.filter.UpdateTargetAnchorsFilter;
import com.geaviation.techpubs.data.util.DataConstants;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.ProgramItemModel;
import java.util.ArrayList;
import java.util.List;

import com.geaviation.techpubs.models.techlib.dto.PageblkLookupDto;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.cyberneko.html.HTMLConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;

import static com.geaviation.techpubs.data.test.util.TestConstants.testNfsUrl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Component
public class UpdateTargetAnchorsFilterTest {

    private static final String BW_LOW = "?bw=low";
    String GEK112064 = "gek112064";
    String GEK112060 = "gek112060";

    @Mock
    ProgramItemModel programItem;

    @Mock
    IResourceData resourceData;

    @Mock
    IBookcaseVersionData iBookcaseVersionData;

    @Mock
    IPageblkLookupData iPageblkLookupData;

    private static final boolean featureUS342496 = true;
    private List<PageblkLookupDto> pageblkLookupDtos;

    @Before
    public void setup() {
        pageblkLookupDtos = new ArrayList<>();
        PageblkLookupDto pageblkLookupDto = new PageblkLookupDto();
        pageblkLookupDto.setOnlineFilename("72-00-00-01.htm");

        pageblkLookupDtos.add(pageblkLookupDto);
    }


    @Test
    public void testUpdateToHtmLink () throws IOException {
        when(programItem.getProgramDocnbr()).thenReturn(GEK112064);
        when(programItem.getProgramOnlineVersion()).thenReturn("2.3");
        when(programItem.getProgramOnlineVersionLocation()).thenReturn(testNfsUrl +  GEK112060 + "/2.3");
        when(iBookcaseVersionData.findOnlineBookcaseVersion(anyString())).thenReturn("1.0");
        when(iPageblkLookupData.lookupPageblkByTarget(anyString(), anyString(), anyString(), anyString())).thenReturn(pageblkLookupDtos);

        File testHtmDoc = new File(testNfsUrl + "testFiles/testData/testUpdateToHtmLink.htm");
        File expectedDoc = new File(testNfsUrl + "testFiles/expected/testUpdateToHtmLinkExpected.htm");
        if (featureUS342496) {
            expectedDoc = new File(testNfsUrl + "testFiles/expected/testUpdateToHtmLinkExpectedUS342496.htm");
        }

        // read the expected contents
        byte[] expectedByteArray  = FileUtils.readFileToByteArray(expectedDoc);
        String expectedContents = new String(expectedByteArray);


        //setup file reading to html that will use the UpdateTargetAnchorsFilter
        HTMLConfiguration parser = new HTMLConfiguration();
        FileInputStream fileInputStream = new FileInputStream(testHtmDoc);
        BufferedInputStream inStream = new BufferedInputStream(fileInputStream);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        XMLDocumentFilter[] filters = new XMLDocumentFilter[4];
        filters[0] = new UpdateTargetAnchorsFilter(DataConstants.SERVICE_URL, programItem, GEK112064,
                testHtmDoc.getName(), BW_LOW, new ResourceDataImpl(), iBookcaseVersionData, iPageblkLookupData, null);
        filters[1] = new UpdateResourceFilter(DataConstants.SERVICE_URL, programItem, GEK112064);
        filters[2] = new BrowserSupportFilter();
        filters[3] = new HTMLWriterFilter(baos, DataConstants.UTF_8);
        parser.setProperty("http://cyberneko.org/html/properties/filters", filters);
        XMLInputSource xis = new XMLInputSource(null, null, null, inStream, null);
        parser.parse(xis);
        baos.close();
        String stringDocument = baos.toString();

        //assertions
        assertEquals(expectedContents,stringDocument);
    }

    @Test
    public void testUpdateToPdfLink() throws IOException {
        when(programItem.getProgramDocnbr()).thenReturn("gek112080");
        when(programItem.getProgramOnlineVersion()).thenReturn("10.1");
        when(programItem.getProgramOnlineVersionLocation()).thenReturn(testNfsUrl + "gek112080/10.1");
        when(iBookcaseVersionData.findOnlineBookcaseVersion(anyString())).thenReturn("1.0");

        File testHtmDoc = new File(testNfsUrl + "gek112080/10.1/doc/gek112771/cf34_10e-ppbm-intro.htm");
        File expectedDoc = new File(testNfsUrl + "testFiles/expected/testUpdateToPdfLinkExpected.htm");

        if (featureUS342496) {
            expectedDoc = new File(testNfsUrl + "testFiles/expected/testUpdateToPdfLinkExpectedUS342496.htm");
        }
        // read the expected contents
        byte[] expectedByteArray  = FileUtils.readFileToByteArray(expectedDoc);
        String expectedContents = new String(expectedByteArray);


        //setup file reading to html that will use the UpdateTargetAnchorsFilter
        HTMLConfiguration parser = new HTMLConfiguration();
        FileInputStream fileInputStream = new FileInputStream(testHtmDoc);
        BufferedInputStream inStream = new BufferedInputStream(fileInputStream);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        XMLDocumentFilter[] filters = new XMLDocumentFilter[4];
        filters[0] = new UpdateTargetAnchorsFilter(DataConstants.SERVICE_URL, programItem, "gek112771",
                testHtmDoc.getName(), BW_LOW, new ResourceDataImpl(), iBookcaseVersionData, iPageblkLookupData, null);
        filters[1] = new UpdateResourceFilter(DataConstants.SERVICE_URL, programItem, "gek112771");
        filters[2] = new BrowserSupportFilter();
        filters[3] = new HTMLWriterFilter(baos, DataConstants.UTF_8);
        parser.setProperty("http://cyberneko.org/html/properties/filters", filters);
        XMLInputSource xis = new XMLInputSource(null, null, null, inStream, null);
        parser.parse(xis);
        baos.close();
        String stringDocument = baos.toString();

        //assertions
        assertEquals(expectedContents,stringDocument);
    }

    @Test
    public void testTargetCortonaLink () throws IOException, TechpubsException {

        when(programItem.getProgramDocnbr()).thenReturn(GEK112060);
        when(programItem.getProgramOnlineVersion()).thenReturn("2.3");
        when(programItem.getProgramOnlineVersionLocation()).thenReturn(testNfsUrl + GEK112060 + "/2.3");

        List<Document> documents = new ArrayList<>();
        Document document = new Document();
        document.add(new Field("filename", "PASSPORT20-A-71-00-00-010-941A-D.htm", Store.YES, Index.ANALYZED));
        document.add(new Field("docnbr", GEK112064, Store.YES, Index.ANALYZED));
        documents.add(document);

        when(resourceData.getDocumentsByTargetIndex(any(ProgramItemModel.class), anyString(), anyString())).thenReturn(documents);
        when(resourceData.cortonaCheck(any(ProgramItemModel.class), anyString(), anyString())).thenReturn(true);
        when(iBookcaseVersionData.findOnlineBookcaseVersion(anyString())).thenReturn("1.0");

        File testHtmDoc = new File(testNfsUrl + "testFiles/testData/testCortonaTargetLink.htm");
        File expectedDoc = new File(testNfsUrl + "testFiles/expected/testCortonaTargetLinkExpectedUS342496.htm");

        // read the expected contents
        byte[] expectedByteArray  = FileUtils.readFileToByteArray(expectedDoc);
        String expectedContents = new String(expectedByteArray);

        HTMLConfiguration parser = new HTMLConfiguration();
        FileInputStream fileInputStream = new FileInputStream(testHtmDoc);
        BufferedInputStream inStream = new BufferedInputStream(fileInputStream);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLDocumentFilter[] filters = new XMLDocumentFilter[4];
        filters[0] = new UpdateTargetAnchorsFilter(DataConstants.SERVICE_URL, programItem, GEK112064,
            testHtmDoc.getName(), BW_LOW, resourceData, iBookcaseVersionData, iPageblkLookupData, null);
        filters[1] = new UpdateResourceFilter(DataConstants.SERVICE_URL, programItem, GEK112064);
        filters[2] = new BrowserSupportFilter();
        filters[3] = new HTMLWriterFilter(baos, DataConstants.UTF_8);
        parser.setProperty("http://cyberneko.org/html/properties/filters", filters);
        XMLInputSource xis = new XMLInputSource(null, null, null, inStream, null);
        parser.parse(xis);
        baos.close();
        String stringDocument = baos.toString();

        assertEquals(expectedContents, stringDocument);
    }

    @Test
    public void testTargetCortonaLink_Failure () throws IOException, TechpubsException {

        when(programItem.getProgramDocnbr()).thenReturn(GEK112060);
        when(programItem.getProgramOnlineVersionLocation()).thenReturn(testNfsUrl + GEK112060 + "/2.3");

        List<Document> documents = new ArrayList<Document>();
        Document document = new Document();
        document.add(new Field("filename", "PASSPORT20-A-71-00-00-010-941A-D.htm", Store.YES, Index.ANALYZED));
        document.add(new Field("docnbr", GEK112064, Store.YES, Index.ANALYZED));
        documents.add(document);

        when(resourceData.getDocumentsByTargetIndex(any(ProgramItemModel.class), anyString(), anyString())).thenReturn(documents);
        when(resourceData.cortonaCheck(any(ProgramItemModel.class), anyString(), anyString())).thenReturn(true);
        when(iBookcaseVersionData.findOnlineBookcaseVersion(anyString())).thenReturn("1.0");

        File testHtmDoc = new File(testNfsUrl + "testFiles/testData/testCortonaTargetLink.htm");
        File notExpectedDoc = new File(testNfsUrl + "testFiles/expected/testUpdateToCortonaLinkExpected.htm");

        // read the expected contents
        byte[] notExpectedByteArray  = FileUtils.readFileToByteArray(notExpectedDoc);
        String notExpectedContents = new String(notExpectedByteArray);

        HTMLConfiguration parser = new HTMLConfiguration();
        FileInputStream fileInputStream = new FileInputStream(testHtmDoc);
        BufferedInputStream inStream = new BufferedInputStream(fileInputStream);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLDocumentFilter[] filters = new XMLDocumentFilter[4];
        filters[0] = new UpdateTargetAnchorsFilter(DataConstants.SERVICE_URL, programItem, GEK112064,
            testHtmDoc.getName(), BW_LOW, resourceData, iBookcaseVersionData, iPageblkLookupData, null);
        filters[1] = new UpdateResourceFilter(DataConstants.SERVICE_URL, programItem, GEK112064);
        filters[2] = new BrowserSupportFilter();
        filters[3] = new HTMLWriterFilter(baos, DataConstants.UTF_8);
        parser.setProperty("http://cyberneko.org/html/properties/filters", filters);
        XMLInputSource xis = new XMLInputSource(null, null, null, inStream, null);
        parser.parse(xis);
        baos.close();
        String stringDocument = baos.toString();

        assertNotEquals(notExpectedContents, stringDocument);
    }
}
