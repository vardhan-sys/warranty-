package com.geaviation.techpubs.service.impl.admin;

import com.geaviation.techpubs.data.api.techlib.IBookcaseData;
import com.geaviation.techpubs.data.api.techlib.IBookcaseVersionData;
import com.geaviation.techpubs.data.api.techlib.IPermissionData;
import com.geaviation.techpubs.data.util.SearchLoaderUtil;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.techlib.UserRoleAttributes;
import com.geaviation.techpubs.models.techlib.dto.BookcaseEngineModelsDto;
import com.geaviation.techpubs.models.techlib.dto.BookcaseVersionDto;
import com.geaviation.techpubs.models.techlib.dto.BookcaseVersionUpdateDto;
import com.geaviation.techpubs.models.techlib.dto.BookcaseWithOnlineVersionDto;
import com.geaviation.techpubs.models.techlib.dto.PublisherBookcaseVersionStatusDto;
import com.geaviation.techpubs.models.techlib.response.PublisherBookcaseVersionsResponse;
import com.geaviation.techpubs.models.techlib.response.PublisherSummaryResponse;
import com.geaviation.techpubs.services.excel.exception.ExcelException;
import com.geaviation.techpubs.services.excel.model.FileWithBytes;
import com.geaviation.techpubs.services.impl.admin.PublisherAppImpl;

import java.sql.Timestamp;
import java.util.*;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class PublisherAppImplTest {

    @Mock
    private IBookcaseData mockIBookcaseData;

    @Mock
    private IBookcaseVersionData mockIBookcaseVersionData;

    @Mock
    private IPermissionData mockIPermissionData;

    @Mock
    private SearchLoaderUtil mockSearchLoaderUtil;

    @InjectMocks
    PublisherAppImpl publisherAppImpl;

    private static final String BOOKCASE_TITLE = "BOOKCASE_TITLE";
    private static final String BOOKCASE_KEY = "BOOKCASE_KEY";
    private static final String ENGINE_FAMILY = "ENGINE_FAMILY";
    private static final String ONLINE_VERSION = "ONLINE_VERSION";
    private static final String BOOKCASE_VERSION = "BOOKCASE_VERSION";
    private static final String BOOKCASE_VERSION_STATUS = "BOOKCASE_VERSION_STATUS";
    private static final String BOOKCASE_RELEASE_DATE_STRING = "2020-01-01 00:00:00";
    private static final Timestamp BOOKCASE_RELEASE_DATE = Timestamp.valueOf(BOOKCASE_RELEASE_DATE_STRING);
    private static final String SORT_BOOKCASE_KEY_BY_DESC = "bookcaseKey|desc";
    private static final String SORT_BOOKCASE_KEY_BY_ASC = "bookcaseKey|asc";
    private static final String SORT_BOOKCASE_TITLE_BY_DESC = "bookcaseTitle|desc";
    private static final String SORT_BOOKCASE_TITLE_BY_ASC = "bookcaseTitle|asc";
    private static final String SORT_ENGINE_FAMILY_BY_DESC = "engineFamily|desc";
    private static final String SORT_ENGINE_FAMILY_BY_ASC = "engineFamily|asc";
    private static final String SORT_ONLINE_VERSION_BY_DESC = "onlineVersion|desc";
    private static final String SORT_ONLINE_VERSION_BY_ASC = "onlineVersion|asc";
    private static final String INVALID_SORT_BY_LENGTH = "sortBy";
    private static final String INVALID_SORT_BY_COLUMN = "column";
    private static final String SEARCH_EMPTY = "";
    private static final String SEARCH = "SEARCH";
    private static final String SSOID = "SSOID";
    private static final String BOOKCASE_ID = "BOOKCASE_ID";
    private static final String TEST_VERSION_1 = "1.0";
    private static final String TEST_VERSION_2 = "2.0";
    private static final BookcaseVersionUpdateDto BOOKCASE_VERSION_UPDATE_DTOBOOKCASE_VERSION_UPDATE_DTO = new BookcaseVersionUpdateDto();
    private static final String OFFLINE = "offline";
    private static final String ONLINE = "online";

    private BookcaseWithOnlineVersionDto bookcaseWithOnlineVersionDto;
    private PublisherBookcaseVersionStatusDto publisherBookcaseVersionStatusDto;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        bookcaseWithOnlineVersionDto = new BookcaseWithOnlineVersionDto(BOOKCASE_TITLE, BOOKCASE_KEY, ENGINE_FAMILY, ONLINE_VERSION);
        publisherBookcaseVersionStatusDto = new PublisherBookcaseVersionStatusDto(BOOKCASE_VERSION, BOOKCASE_VERSION_STATUS, BOOKCASE_RELEASE_DATE);

        BookcaseVersionDto version1 = new BookcaseVersionDto(TEST_VERSION_1, ONLINE, BOOKCASE_RELEASE_DATE_STRING);
        BookcaseVersionDto version2 = new BookcaseVersionDto(TEST_VERSION_2, OFFLINE);
        List<BookcaseVersionDto> versionsList = new LinkedList<>();
        versionsList.add(version1);
        versionsList.add(version2);
        BOOKCASE_VERSION_UPDATE_DTOBOOKCASE_VERSION_UPDATE_DTO.setBookcaseVersions(versionsList);

        UserRoleAttributes userRoleAttributes = new UserRoleAttributes();
        List<String> engineList = new ArrayList<>();
        engineList.add("all");
        userRoleAttributes.setEngineModels(engineList);
        List<UserRoleAttributes> attributesList = Arrays.asList(userRoleAttributes);
        when(mockIPermissionData.findUserRoleAttributes(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(attributesList);
    }

    @Test
    public void getPublisherSummaryGivenSortByBookcaseKeyDescReturnListOfVersions() throws TechpubsException {
        List<BookcaseWithOnlineVersionDto> bookcaseWithOnlineVersionDtoList = new ArrayList<>();
        bookcaseWithOnlineVersionDtoList.add(bookcaseWithOnlineVersionDto);

        when(mockIBookcaseData.findBookcasesWithOnlineVersion(anyString(), anyString(), anyString())).thenReturn(bookcaseWithOnlineVersionDtoList);

        PublisherSummaryResponse getPublisherSummaryResponse = publisherAppImpl.getPublisherSummary(SSOID, SORT_BOOKCASE_KEY_BY_DESC, SEARCH_EMPTY);

        assertFalse(getPublisherSummaryResponse.getBookcases().isEmpty());
        assertTrue(getPublisherSummaryResponse.getBookcases().get(0).getBookcaseKey().contains(BOOKCASE_KEY));
    }

    @Test
    public void getPublisherSummaryGivenSortByBookcaseKeyAscReturnListOfVersions() throws TechpubsException {
        List<BookcaseWithOnlineVersionDto> bookcaseWithOnlineVersionDtoList = new ArrayList<>();
        bookcaseWithOnlineVersionDtoList.add(bookcaseWithOnlineVersionDto);

        when(mockIBookcaseData.findBookcasesWithOnlineVersion(anyString(), anyString(), anyString())).thenReturn(bookcaseWithOnlineVersionDtoList);


        PublisherSummaryResponse getPublisherSummaryResponse = publisherAppImpl.getPublisherSummary(SSOID, SORT_BOOKCASE_KEY_BY_ASC, SEARCH_EMPTY);

        assertFalse(getPublisherSummaryResponse.getBookcases().isEmpty());
        assertTrue(getPublisherSummaryResponse.getBookcases().get(0).getBookcaseKey().contains(BOOKCASE_KEY));
    }

    @Test
    public void getPublisherSummaryGivenSortByBookcaseTitleDescReturnListOfVersions() throws TechpubsException {
        List<BookcaseWithOnlineVersionDto> bookcaseWithOnlineVersionDtoList = new ArrayList<>();
        bookcaseWithOnlineVersionDtoList.add(bookcaseWithOnlineVersionDto);

        when(mockIBookcaseData.findBookcasesWithOnlineVersion(anyString(), anyString(), anyString())).thenReturn(bookcaseWithOnlineVersionDtoList);

        PublisherSummaryResponse getPublisherSummaryResponse = publisherAppImpl.getPublisherSummary(SSOID, SORT_BOOKCASE_TITLE_BY_DESC, SEARCH_EMPTY);

        assertFalse(getPublisherSummaryResponse.getBookcases().isEmpty());
        assertTrue(getPublisherSummaryResponse.getBookcases().get(0).getBookcaseTitle().contains(BOOKCASE_TITLE));
    }

    @Test
    public void getPublisherSummaryGivenSortByBookcaseTitleAscReturnListOfVersions() throws TechpubsException {
        List<BookcaseWithOnlineVersionDto> bookcaseWithOnlineVersionDtoList = new ArrayList<>();
        bookcaseWithOnlineVersionDtoList.add(bookcaseWithOnlineVersionDto);

        when(mockIBookcaseData.findBookcasesWithOnlineVersion(anyString(), anyString(), anyString())).thenReturn(bookcaseWithOnlineVersionDtoList);

        PublisherSummaryResponse getPublisherSummaryResponse = publisherAppImpl.getPublisherSummary(SSOID, SORT_BOOKCASE_TITLE_BY_ASC, SEARCH_EMPTY);

        assertFalse(getPublisherSummaryResponse.getBookcases().isEmpty());
        assertTrue(getPublisherSummaryResponse.getBookcases().get(0).getBookcaseTitle().contains(BOOKCASE_TITLE));
    }

    @Test
    public void getPublisherSummaryGivenSortByEngineFamilyDescReturnListOfVersions() throws TechpubsException {
        List<BookcaseWithOnlineVersionDto> bookcaseWithOnlineVersionDtoList = new ArrayList<>();
        bookcaseWithOnlineVersionDtoList.add(bookcaseWithOnlineVersionDto);

        when(mockIBookcaseData.findBookcasesWithOnlineVersion(anyString(), anyString(), anyString())).thenReturn(bookcaseWithOnlineVersionDtoList);

        PublisherSummaryResponse getPublisherSummaryResponse = publisherAppImpl.getPublisherSummary(SSOID, SORT_ENGINE_FAMILY_BY_DESC, SEARCH_EMPTY);

        assertFalse(getPublisherSummaryResponse.getBookcases().isEmpty());
        assertTrue(getPublisherSummaryResponse.getBookcases().get(0).getEngineFamily().contains(ENGINE_FAMILY));
    }

    @Test
    public void getPublisherSummaryGivenSortByEngineFamilyAscReturnListOfVersions() throws TechpubsException {
        List<BookcaseWithOnlineVersionDto> bookcaseWithOnlineVersionDtoList = new ArrayList<>();
        bookcaseWithOnlineVersionDtoList.add(bookcaseWithOnlineVersionDto);

        when(mockIBookcaseData.findBookcasesWithOnlineVersion(anyString(), anyString(), anyString())).thenReturn(bookcaseWithOnlineVersionDtoList);

        PublisherSummaryResponse getPublisherSummaryResponse = publisherAppImpl.getPublisherSummary(SSOID, SORT_ENGINE_FAMILY_BY_ASC, SEARCH_EMPTY);

        assertFalse(getPublisherSummaryResponse.getBookcases().isEmpty());
        assertTrue(getPublisherSummaryResponse.getBookcases().get(0).getEngineFamily().contains(ENGINE_FAMILY));
    }

    @Test
    public void getPublisherSummaryGivenSortByOnlineVersionDescReturnListOfVersions() throws TechpubsException {
        List<BookcaseWithOnlineVersionDto> bookcaseWithOnlineVersionDtoList = new ArrayList<>();
        bookcaseWithOnlineVersionDtoList.add(bookcaseWithOnlineVersionDto);

        when(mockIBookcaseData.findBookcasesWithOnlineVersion(anyString(), anyString(), anyString())).thenReturn(bookcaseWithOnlineVersionDtoList);

        PublisherSummaryResponse getPublisherSummaryResponse = publisherAppImpl.getPublisherSummary(SSOID, SORT_ONLINE_VERSION_BY_DESC, SEARCH_EMPTY);

        assertFalse(getPublisherSummaryResponse.getBookcases().isEmpty());
        assertTrue(getPublisherSummaryResponse.getBookcases().get(0).getOnlineVersion().contains(ONLINE_VERSION));
    }

    @Test
    public void getPublisherSummaryGivenSortByOnlineVersionAscReturnListOfVersions() throws TechpubsException {
        List<BookcaseWithOnlineVersionDto> bookcaseWithOnlineVersionDtoList = new ArrayList<>();
        bookcaseWithOnlineVersionDtoList.add(bookcaseWithOnlineVersionDto);

        when(mockIBookcaseData.findBookcasesWithOnlineVersion(anyString(), anyString(), anyString())).thenReturn(bookcaseWithOnlineVersionDtoList);

        PublisherSummaryResponse getPublisherSummaryResponse = publisherAppImpl.getPublisherSummary(SSOID, SORT_ONLINE_VERSION_BY_ASC, SEARCH_EMPTY);

        assertFalse(getPublisherSummaryResponse.getBookcases().isEmpty());
        assertTrue(getPublisherSummaryResponse.getBookcases().get(0).getOnlineVersion().contains(ONLINE_VERSION));
    }


    @Test(expected = TechpubsException.class)
    public void getPublisherSummaryGivenPageAndInvalidSortByLengthThrowsException() throws TechpubsException {
        publisherAppImpl.getPublisherSummary(SSOID, INVALID_SORT_BY_LENGTH, SEARCH_EMPTY);
    }

    @Test(expected = TechpubsException.class)
    public void getPublisherSummaryGivenPageAndInvalidSortByColumnThrowsException() throws TechpubsException {
        publisherAppImpl.getPublisherSummary(SSOID, INVALID_SORT_BY_COLUMN, SEARCH_EMPTY);
    }

    @Test
    public void downloadBookcasesReturnsPublisherSummaryExcelFileSearchEmpty() throws ExcelException {
        List<BookcaseWithOnlineVersionDto> bookcaseWithOnlineVersionDtoList = new ArrayList<>();
        bookcaseWithOnlineVersionDtoList.add(bookcaseWithOnlineVersionDto);

        when(mockIBookcaseData.findBookcasesWithOnlineVersion(any(), any(), anyString())).thenReturn(bookcaseWithOnlineVersionDtoList);

        FileWithBytes fileWithBytes = publisherAppImpl.downloadPublisherSummary(SSOID, SEARCH_EMPTY);

        assertNotNull(fileWithBytes.getContents());
        assertEquals("bookcases_with_online_versions.xlsx", fileWithBytes.getFileName());
    }

    @Test
    public void downloadBookcasesReturnsPublisherSummaryExcelFileWithSearch() throws ExcelException {
        List<BookcaseWithOnlineVersionDto> bookcaseWithOnlineVersionDtoList = new ArrayList<>();
        bookcaseWithOnlineVersionDtoList.add(bookcaseWithOnlineVersionDto);

        when(mockIBookcaseData.findBookcasesWithOnlineVersion(any(), any(), anyString())).thenReturn(bookcaseWithOnlineVersionDtoList);

        FileWithBytes fileWithBytes = publisherAppImpl.downloadPublisherSummary(SSOID, SEARCH);

        assertNotNull(fileWithBytes.getContents());
        assertEquals("bookcases_with_online_versions.xlsx", fileWithBytes.getFileName());
    }

    @Test
    public void getBookcaseVersionsReturnsPage() {
        List<PublisherBookcaseVersionStatusDto> publisherBookcaseVersionStatusDtoList = new ArrayList<>();
        publisherBookcaseVersionStatusDtoList.add(publisherBookcaseVersionStatusDto);

        when(mockIBookcaseData.getBookcaseVersions(anyString())).thenReturn(publisherBookcaseVersionStatusDtoList);

        PublisherBookcaseVersionsResponse getPublisherSummaryResponse = publisherAppImpl.getBookcaseVersions(BOOKCASE_KEY);

        assertFalse(getPublisherSummaryResponse.getBookcaseVersions().isEmpty());
        assertTrue(getPublisherSummaryResponse.getBookcaseVersions().get(0).getBookcaseVersionStatus().contains(BOOKCASE_VERSION_STATUS));
    }

    @Test
    public void getBookcaseEngineModelsTest() {
        // borderline useless test. Pretty much checks that publisherAppImpl calls getBookcaseEngineModels in IBookcaseData
        String m1 = "model1";
        String m2 = "model2";
        String m3 = "model3";
        List<String> engineModels = Arrays.asList(m1, m2, m3);
        when(mockIBookcaseData.getBookcaseEngineModels(anyString())).thenReturn(engineModels);

        BookcaseEngineModelsDto bookcaseEngineModels = publisherAppImpl.getBookcaseEngineModels("anyString");

        Assertions.assertThat(bookcaseEngineModels.getEngineModels()).contains(m1);
        Assertions.assertThat(bookcaseEngineModels.getEngineModels()).contains(m2);
        Assertions.assertThat(bookcaseEngineModels.getEngineModels()).contains(m3);
    }

    @Test
    public void updateBookcaseVersionStatuses() throws TechpubsException {
        UUID bookcaseKey = UUID.randomUUID();
        when(mockIBookcaseData.getBookcaseVersionId(anyString(), anyString())).thenReturn(bookcaseKey);
        when(mockIBookcaseVersionData.updateBookcaseVersionStatus(anyString(), any(UUID.class))).thenReturn(1);
        when(mockIBookcaseVersionData.updateBookcaseVersionStatusAndReleaseDate(anyString(), any(Timestamp.class), any(UUID.class))).thenReturn(1);
        when(mockSearchLoaderUtil.invokeSearchLoader(anyString(), anyString())).thenReturn(null);
        boolean updateBookcaseVersionStatusResponse = publisherAppImpl.updateBookcaseVersionsStatus(SSOID, BOOKCASE_ID,
            BOOKCASE_VERSION_UPDATE_DTOBOOKCASE_VERSION_UPDATE_DTO);
        assertTrue(updateBookcaseVersionStatusResponse);
    }

    @Test(expected = TechpubsException.class)
    public void updateBookcaseVersionStatusesInvalid() throws TechpubsException {
        UUID bookcaseKey = UUID.randomUUID();
        when(mockIBookcaseData.getBookcaseVersionId(anyString(), anyString())).thenReturn(bookcaseKey);
        BookcaseVersionDto badVersion = new BookcaseVersionDto("-3", "authorSignedEdition");
        BOOKCASE_VERSION_UPDATE_DTOBOOKCASE_VERSION_UPDATE_DTO.getBookcaseVersions().add(badVersion);
        boolean updateBookcaseVersionStatusResponse = publisherAppImpl.updateBookcaseVersionsStatus(SSOID, BOOKCASE_ID,
            BOOKCASE_VERSION_UPDATE_DTOBOOKCASE_VERSION_UPDATE_DTO);
        assertFalse(updateBookcaseVersionStatusResponse);
    }
}