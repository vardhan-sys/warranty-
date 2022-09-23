package com.geaviation.techpubs.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import com.geaviation.techpubs.data.api.IProgramData;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.services.api.IProgramApp;
import com.geaviation.techpubs.services.impl.BookcaseApp;
import com.geaviation.techpubs.services.util.AppConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.map.HashedMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class BookcaseAppTest {
    private static final String PORTALID = "portalid";
    private static final String SSO =  "sso";
    private static final String GEK1 = "GEK1";
    private static final String GEK2 = "GEK2";
    private static final String GEK3 = "GEK3";
    private Map<String, String> searchFilterMap;

    @Mock(name="iProgramApp")
    IProgramApp programAppMock;

    @Mock(name="iProgramData")
    IProgramData programDataMock;

    @InjectMocks
    BookcaseApp bookcaseAppSvc;

    private List<String> gekList;

    @Before
    public void setup() throws TechpubsException {
        gekList = new ArrayList<>(Arrays.asList(GEK1, GEK2, GEK3));

        searchFilterMap = new HashedMap();
        searchFilterMap.put(AppConstants.MODEL, "model");

        // Instantiate class we're testing and inject mocks
        this.bookcaseAppSvc = new BookcaseApp();
        MockitoAnnotations.initMocks(this);

        when(programAppMock.getAuthorizedPrograms(isA(String.class), isA(String.class), isA(SubSystem.class))).thenReturn(new ArrayList<>(gekList));
        when(programDataMock.getProgramsByModel(isA(String.class), isA(SubSystem.class))).thenReturn(new ArrayList<>(new ArrayList<>()));
        when(programDataMock.getProgramsByFamily(isA(String.class), isA(SubSystem.class))).thenReturn(new ArrayList<>(new ArrayList<>()));
    }

    @Test
    public void whenGetAuthorizedProgramsThrowsExceptionShouldThrowSameException() throws TechpubsException {
        when(programDataMock.getProgramsByModel(isA(String.class), isA(SubSystem.class))).thenReturn(new ArrayList<>(gekList));
        when(programAppMock.getAuthorizedPrograms(isA(String.class), isA(String.class), isA(SubSystem.class))).thenThrow(new NullPointerException());
        Assertions.assertThrows(NullPointerException.class, () -> bookcaseAppSvc.getAuthorizedBookcaseKeysForRequest(SSO, PORTALID, searchFilterMap));
    }

    @Test
    public void whenGetAuthorizedProgramsReturnsEmtpyListShouldReturnEmptyList() throws TechpubsException {
        when(programAppMock.getAuthorizedPrograms(isA(String.class), isA(String.class), isA(SubSystem.class))).thenReturn(new ArrayList<>());
        List<String> resultList =  bookcaseAppSvc.getAuthorizedBookcaseKeysForRequest(SSO, PORTALID, searchFilterMap);

        assert(resultList.isEmpty());
    }

    @Test
    public void whenGetAuthorizedProgramsReturnsItemsThenShouldReturnListOfOnlyTheItemsThatAreAlsoInListOfBookcaseKeysForTheRequest() throws TechpubsException {
        List<String> authorizedGekList = new ArrayList(Arrays.asList(GEK1, GEK3));
        when(programDataMock.getProgramsByModel(isA(String.class), isA(SubSystem.class))).thenReturn(new ArrayList<>(gekList));
        when(programAppMock.getAuthorizedPrograms(isA(String.class), isA(String.class), isA(SubSystem.class))).thenReturn(authorizedGekList);
        List<String> resultList =  bookcaseAppSvc.getAuthorizedBookcaseKeysForRequest(SSO, PORTALID, searchFilterMap);

        assertEquals(resultList, authorizedGekList);
    }

    @Test
    public void whenGetProgramsByModelThrowsExceptionShouldThrowSameException() {
        when(programDataMock.getProgramsByModel(isA(String.class), isA(SubSystem.class))).thenThrow(new NullPointerException());
        Assertions.assertThrows(NullPointerException.class, () -> bookcaseAppSvc.getAuthorizedBookcaseKeysForRequest(SSO, PORTALID, searchFilterMap));
    }

    @Test
    public void whenGetProgramsByFamilyThrowsExceptionShouldThrowSameException() {
        searchFilterMap.put(AppConstants.MODEL, null);
        searchFilterMap.put(AppConstants.FAMILY, "family");

        when(programDataMock.getProgramsByFamily(isA(String.class), isA(SubSystem.class))).thenThrow(new NullPointerException());
        Assertions.assertThrows(NullPointerException.class, () -> bookcaseAppSvc.getAuthorizedBookcaseKeysForRequest(SSO, PORTALID, searchFilterMap));
    }

    @Test
    public void whenGetProgramsByModelReturnsListWithItemsAndItemsAreAuthorizedShouldReturnAListWithTheSameNumberOfItems() throws TechpubsException {
        when(programDataMock.getProgramsByModel(isA(String.class), isA(SubSystem.class))).thenReturn(new ArrayList<>(gekList));
        List<String> resultList =  bookcaseAppSvc.getAuthorizedBookcaseKeysForRequest(SSO, PORTALID, searchFilterMap);

        assertEquals(resultList, gekList);
    }

    @Test
    public void whenGetProgramsByFamilyReturnsListWithItemsAndItemsAreAuthorizedShouldReturnAListWithTheSameNumberOfItems() throws TechpubsException {
        searchFilterMap.put(AppConstants.MODEL, null);
        searchFilterMap.put(AppConstants.FAMILY, "family");
        when(programDataMock.getProgramsByFamily(isA(String.class), isA(SubSystem.class))).thenReturn(new ArrayList<>(gekList));
        List<String> resultList =  bookcaseAppSvc.getAuthorizedBookcaseKeysForRequest(SSO, PORTALID, searchFilterMap);

        assertEquals(resultList, gekList);

    }
}
