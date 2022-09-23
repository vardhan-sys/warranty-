package com.geaviation.techpubs.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import com.geaviation.dss.service.common.exception.TechnicalException;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.BookDAO;
import com.geaviation.techpubs.models.BookcaseContentDAO;
import com.geaviation.techpubs.models.BookcaseContentModel;
import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.services.api.IBookcaseContentApp;
import com.geaviation.techpubs.services.impl.BookcaseApp;
import com.geaviation.techpubs.services.impl.BookcaseContentAppRegServices;
import com.geaviation.techpubs.services.impl.BookcaseContentModelApp;
import com.geaviation.techpubs.services.util.AppConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.map.HashedMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class BookcaseContentModelAppTest {
    private static final String PORTALID = "portalid";
    private static final String SSO =  "sso";
    private static final Map<String, String> queryParams = new HashedMap();

    @InjectMocks
    BookcaseContentModelApp bookItemModelAppSvcImpl;

    @Mock
    BookcaseApp iBookcaseAppMock;

    @Mock
    IBookcaseContentApp iBookcaseContentAppMock;

    @Mock
    BookcaseContentAppRegServices bookcaseContentAppRegServices;

    List<BookcaseContentDAO> bookList = new ArrayList();
    BookDAO bookcase1Book1RevDate1;
    BookDAO bookcase1Book2RevDate1;
    BookDAO bookcase1Book2RevDate2;
    BookDAO bookcase2Book1RevDate1;
    BookDAO bookcase3Book1RevDate1;
    BookDAO geSpmBook;
    BookDAO hondaSpmBook;


    @Before
    public void setup() throws TechpubsException {
        bookcase1Book1RevDate1 = new BookDAO();
        bookcase1Book1RevDate1.setKey("1");
        bookcase1Book1RevDate1.setRevisiondate("2019-01-01");
        bookcase1Book1RevDate1.setTitle("BookcaseContentDAO 1");
        bookcase1Book1RevDate1.setBookcasetitle("Bookcase 1");

        bookcase1Book2RevDate1 = new BookDAO();
        bookcase1Book2RevDate1.setKey("4");
        bookcase1Book2RevDate1.setRevisiondate("2019-01-01");
        bookcase1Book2RevDate1.setTitle("BookcaseContentDAO 2");
        bookcase1Book2RevDate1.setBookcasetitle("Bookcase 1");

        bookcase1Book2RevDate2 = new BookDAO();
        bookcase1Book2RevDate2.setKey("5");
        bookcase1Book2RevDate2.setRevisiondate("2019-01-02");
        bookcase1Book2RevDate2.setTitle("BookcaseContentDAO 2");
        bookcase1Book2RevDate2.setBookcasetitle("Bookcase 1");

        bookcase2Book1RevDate1 = new BookDAO();
        bookcase2Book1RevDate1.setKey("8");
        bookcase2Book1RevDate1.setRevisiondate("2019-01-01");
        bookcase2Book1RevDate1.setTitle("BookcaseContentDAO 1");
        bookcase2Book1RevDate1.setBookcasetitle("Bookcase 2");

        bookcase3Book1RevDate1 = new BookDAO();
        bookcase3Book1RevDate1.setKey("8");
        bookcase3Book1RevDate1.setRevisiondate("2019-01-01");
        bookcase3Book1RevDate1.setTitle("BookcaseContentDAO 1");
        bookcase3Book1RevDate1.setBookcasetitle("Bookcase 3");

        geSpmBook = new BookDAO();
        geSpmBook.setKey("ge_spm");

        hondaSpmBook = new BookDAO();
        hondaSpmBook.setKey("honda_spm");

        queryParams.put(AppConstants.ICOLUMNS, "8");
        queryParams.put(AppConstants.MDATAPROP+0, "bookcasetitle");
        queryParams.put(AppConstants.MDATAPROP+1, "title");
        queryParams.put(AppConstants.MDATAPROP+2, "revisiondate");
        queryParams.put(AppConstants.IDISPLAYLENGTH, "10");
        queryParams.put(AppConstants.IDISPLAYSTART, "0");
        queryParams.put(AppConstants.ICOLUMNS, "3");
        queryParams.put(AppConstants.SECHO, "4");
        queryParams.put(AppConstants.TYPE, "IC");

        // Instantiate class we're testing and inject mocks
        this.bookItemModelAppSvcImpl = new BookcaseContentModelApp();
        MockitoAnnotations.initMocks(this);

        when(iBookcaseContentAppMock.getBookcaseItems(isA(String.class), isA(List.class), isA(String.class))).thenReturn(new ArrayList<>());
        when(iBookcaseAppMock.getAuthorizedBookcaseKeysForRequest(isA(String.class), isA(String.class), isA(Map.class))).thenReturn(new ArrayList(Arrays.asList("gek1","gek2", "gek3")));
        when(bookcaseContentAppRegServices.getSubSystemService(isA(SubSystem.class))).thenReturn(
            iBookcaseContentAppMock);
    }

    @Test
    public void whenAnExceptionIsThrownThenShouldReturnANewBookcaseContentModelWithSuccessFalse(){
        when(iBookcaseContentAppMock.getBookcaseItems(isA(String.class), isA(List.class), isA(String.class))).thenThrow(new TechnicalException(new Exception()));
        BookcaseContentModel result = bookItemModelAppSvcImpl.getBookcaseItemModel(SSO, PORTALID, new HashedMap(), queryParams);
        assert(!result.isSuccess());
    }

    @Test
    public void whenNoDataIsRetrievedThenShouldReturnBookcaseContentModelWithSuccessTrue(){
        when(iBookcaseContentAppMock.getBookcaseItems(isA(String.class), isA(List.class), isA(String.class))).thenReturn(new ArrayList<>());

        BookcaseContentModel result = bookItemModelAppSvcImpl.getBookcaseItemModel(SSO, PORTALID, new HashedMap(), queryParams);
        assert(result.isSuccess());
    }

    @Test
    public void whenDataIsRetrievedThenShouldReturnBookcaseTOCModelWithSuccessTrue(){
        when(iBookcaseContentAppMock.getBookcaseItems(isA(String.class), isA(List.class), isA(String.class))).thenReturn(new ArrayList(Arrays.asList(bookcase1Book1RevDate1)));

        BookcaseContentModel result = bookItemModelAppSvcImpl.getBookcaseItemModel(SSO, PORTALID, new HashedMap(), queryParams);
        assert(result.isSuccess());
    }

    @Test
    public void whenDataIsRetrievedThenShouldReturnBookcaseContentModelWithTheData(){
        List<BookcaseContentDAO> dataList =  new ArrayList(Arrays.asList(bookcase1Book1RevDate1));
        when(iBookcaseContentAppMock.getBookcaseItems(isA(String.class), isA(List.class), isA(String.class))).thenReturn(new ArrayList(Arrays.asList(bookcase1Book1RevDate1)));

        BookcaseContentModel result = bookItemModelAppSvcImpl.getBookcaseItemModel(SSO, PORTALID, new HashedMap(), queryParams);
        assertEquals(result.getBookcaseContentDAOS(), dataList);
    }

    @Test
    public void whenGetSubSystemServiceReturnsNullThenShouldReturnBookcaseContentModelWithSuccessFalse(){
        when(bookcaseContentAppRegServices.getSubSystemService(isA(SubSystem.class))).thenReturn(null);
        BookcaseContentModel result= bookItemModelAppSvcImpl.getBookcaseItemModel(SSO, PORTALID, new HashedMap(), queryParams);
        assert(!result.isSuccess());
    }


    @Test
    public void whenGetAuthorizedBookcaseKeysForRequestThrowsExceptionShouldThenShouldReturnBookcaseContentModelWithSuccessFalse() throws TechpubsException {
        when(iBookcaseAppMock.getAuthorizedBookcaseKeysForRequest(isA(String.class), isA(String.class), isA(Map.class))).thenThrow(new NullPointerException());
        BookcaseContentModel result= bookItemModelAppSvcImpl.getBookcaseItemModel(SSO, PORTALID, new HashedMap(), queryParams);
        assert(!result.isSuccess());
    }

    @Test
    public void whenGetBookcaseItemAppThrowsExceptionShouldThenShouldReturnBookcaseContentModelWithSuccessFalse() throws TechpubsException {
        when(iBookcaseContentAppMock.getBookcaseItems(isA(String.class), isA(List.class), isA(String.class))).thenThrow(new NullPointerException());
        BookcaseContentModel result= bookItemModelAppSvcImpl.getBookcaseItemModel(SSO, PORTALID, new HashedMap(), queryParams);
        assert(!result.isSuccess());
    }

    @Test
    public void whenGetBookItemsReturnsEmptyListShouldReturnABooklistObjectWithNoBooks() throws TechpubsException {
        when(iBookcaseContentAppMock.getBookcaseItems(isA(String.class), isA(List.class), isA(String.class))).thenReturn(new ArrayList<>());
        BookcaseContentModel result = bookItemModelAppSvcImpl.getBookcaseItemModel(SSO, PORTALID, new HashedMap(), queryParams);
        assert (result.getBookcaseContentDAOS().isEmpty());
    }

        @Test
        public void whenSortParametersAreRecievedThenShouldReturnBooklistWithBooksSortedAccordingToSortParameters() throws TechpubsException {
            Map<String, String> testQueryParams = new HashedMap();
            testQueryParams.put(AppConstants.ISORT_COL + 0, "2");
            testQueryParams.put(AppConstants.SSORTDIR + 0, "desc");

            testQueryParams.put(AppConstants.ISORT_COL + 1, "1");
            testQueryParams.put(AppConstants.SSORTDIR + 1, "desc");

            testQueryParams.put(AppConstants.ISORT_COL + 2, "0");
            testQueryParams.put(AppConstants.SSORTDIR + 2, "asc");

            testQueryParams.put(AppConstants.IDISPLAYLENGTH, "5");
            testQueryParams.put(AppConstants.IDISPLAYSTART, "0");

            testQueryParams.put(AppConstants.ICOLUMNS, "8");
            testQueryParams.put(AppConstants.MDATAPROP+0, "bookcasetitle");
            testQueryParams.put(AppConstants.MDATAPROP+1, "title");
            testQueryParams.put(AppConstants.MDATAPROP+2, "revisiondate");
            testQueryParams.put(AppConstants.IDISPLAYLENGTH, "10");
            testQueryParams.put(AppConstants.IDISPLAYSTART, "0");
            testQueryParams.put(AppConstants.ICOLUMNS, "3");
            testQueryParams.put(AppConstants.SECHO, "4");
            testQueryParams.put(AppConstants.TYPE, "IC");


            bookList = new ArrayList(Arrays.asList(bookcase1Book1RevDate1, bookcase1Book2RevDate1, bookcase2Book1RevDate1, bookcase1Book2RevDate2, bookcase3Book1RevDate1));

            when(iBookcaseContentAppMock.getBookcaseItems(isA(String.class), isA(List.class), isA(String.class))).thenReturn(bookList);

            BookcaseContentModel bookcaseContentModel = bookItemModelAppSvcImpl.getBookcaseItemModel(SSO, PORTALID, new HashedMap(), testQueryParams);

            List<BookcaseContentDAO> resultList = bookcaseContentModel.getBookcaseContentDAOS();

            assertEquals(bookcase3Book1RevDate1.getKey(), ((BookDAO)resultList.get(4)).getKey());
            assertEquals(bookcase2Book1RevDate1.getKey(), ((BookDAO)resultList.get(3)).getKey());
            assertEquals(bookcase1Book1RevDate1.getKey(), ((BookDAO)resultList.get(2)).getKey());

        }

        @Test
        public void shouldReturnBookListWithDisplayLengthEqualToTheDisplayLengthRecieved() throws TechpubsException {
            BookcaseContentModel bookcaseContentModel = bookItemModelAppSvcImpl.getBookcaseItemModel(SSO, PORTALID, new HashedMap(), queryParams);
            assertEquals(Integer.parseInt(queryParams.get(AppConstants.IDISPLAYLENGTH)), bookcaseContentModel
                .getIDisplayLength());
        }


        @Test
        public void shouldReturnBookListWithDisplayStartEqualToTheDisplayStartRecieved() throws TechpubsException {
            BookcaseContentModel bookcaseContentModel = bookItemModelAppSvcImpl.getBookcaseItemModel(SSO, PORTALID, new HashedMap(), queryParams);
            assertEquals(Integer.parseInt(queryParams.get(AppConstants.IDISPLAYSTART)), bookcaseContentModel
                .getIDisplayStart());
        }

        @Test
        public void shouldReturnBookListWithSEchoEqualToTheSEchoRecieved() throws TechpubsException {
            BookcaseContentModel bookcaseContentModel = bookItemModelAppSvcImpl.getBookcaseItemModel(SSO, PORTALID, new HashedMap(), queryParams);
            assertEquals(queryParams.get(AppConstants.SECHO), bookcaseContentModel.getSEcho());
        }

        @Test
        public void shouldReturnBookListWithTotalDisplayRecordsEqualToTheBookResultListSize() throws TechpubsException {
            bookList = new ArrayList(Arrays.asList(bookcase1Book1RevDate1, bookcase1Book2RevDate1, bookcase2Book1RevDate1, bookcase1Book2RevDate2, bookcase3Book1RevDate1));
            when(iBookcaseContentAppMock.getBookcaseItems(isA(String.class), isA(List.class), isA(String.class))).thenReturn(bookList);
            BookcaseContentModel result = bookItemModelAppSvcImpl.getBookcaseItemModel(SSO, PORTALID, new HashedMap(), queryParams);
            assertEquals(result.getITotalDisplayRecords(), bookList.size());
        }

        @Test
        public void shouldReturnBookListWithTotalRecordsEqualToTheBookResultListSize() throws TechpubsException {
            bookList = new ArrayList(Arrays.asList(bookcase1Book1RevDate1, bookcase1Book2RevDate1, bookcase2Book1RevDate1, bookcase1Book2RevDate2, bookcase3Book1RevDate1));
            when(iBookcaseContentAppMock.getBookcaseItems(isA(String.class), isA(List.class), isA(String.class))).thenReturn(bookList);
            BookcaseContentModel result = bookItemModelAppSvcImpl.getBookcaseItemModel(SSO, PORTALID, new HashedMap(), queryParams);
            assertEquals(result.getITotalRecords(), bookList.size());
        }

        @Test
        public void whenDisplayStartIsGreaterThanResultSizeShouldReturnEmptyList() throws TechpubsException {
            queryParams.put(AppConstants.IDISPLAYSTART, "8");
            bookList = new ArrayList(Arrays.asList(bookcase1Book1RevDate1, bookcase1Book2RevDate1, bookcase2Book1RevDate1, bookcase1Book2RevDate2, bookcase3Book1RevDate1));
            when(iBookcaseContentAppMock.getBookcaseItems(isA(String.class), isA(List.class), isA(String.class))).thenReturn(bookList);
            BookcaseContentModel result = bookItemModelAppSvcImpl.getBookcaseItemModel(SSO, PORTALID, new HashedMap(), queryParams);
            assert(result.getBookcaseContentDAOS().isEmpty());
        }

        @Test
        public void whenDisplayStartIsLessThanResultSizeShouldReturnSubListStartingAtDisplayStart() throws TechpubsException {
            queryParams.put(AppConstants.IDISPLAYLENGTH, "5");
            queryParams.put(AppConstants.IDISPLAYSTART, "2");
            bookList = new ArrayList(Arrays.asList(bookcase1Book1RevDate1, bookcase1Book2RevDate1, bookcase2Book1RevDate1, bookcase1Book2RevDate2, bookcase3Book1RevDate1));
            List<BookcaseContentDAO> subList = new ArrayList<>();
            subList.addAll(bookList.subList(Integer.parseInt(queryParams.get(AppConstants.IDISPLAYSTART)), Integer.parseInt(queryParams.get(AppConstants.IDISPLAYLENGTH))));
            when(iBookcaseContentAppMock.getBookcaseItems(isA(String.class), isA(List.class), isA(String.class))).thenReturn(bookList);

            BookcaseContentModel result = bookItemModelAppSvcImpl.getBookcaseItemModel(SSO, PORTALID, new HashedMap(), queryParams);

            assertEquals(result.getBookcaseContentDAOS(), subList);
        }

        @Test
        public void whenDisplayStartPlusDisplayLengthIsGreaterThanResultSizeShouldReturnSubListWithAllItemsAfterTheDisplayStart() throws TechpubsException {
            queryParams.put(AppConstants.IDISPLAYSTART, "3");
            queryParams.put(AppConstants.IDISPLAYLENGTH, "7");
            List mockList = new ArrayList(Arrays.asList(bookcase1Book1RevDate1, bookcase1Book2RevDate1, bookcase2Book1RevDate1, bookcase1Book2RevDate2, bookcase3Book1RevDate1));

            List<BookcaseContentDAO> subList = new ArrayList<>(Arrays.asList(bookcase1Book2RevDate2, bookcase3Book1RevDate1));
            when(iBookcaseContentAppMock.getBookcaseItems(isA(String.class), isA(List.class), isA(String.class))).thenReturn(mockList);

            BookcaseContentModel result = bookItemModelAppSvcImpl.getBookcaseItemModel(SSO, PORTALID, new HashedMap(), queryParams);

            assertEquals(result.getBookcaseContentDAOS(), subList);

        }

        @Test
        public void whenDisplayStartPlusDisplayLengthIsLessThanOrEqualToResultSizeShouldReturnSubListWithAllItemsBetweenTheDisplayStartInclusiveAndLength() throws TechpubsException {
            queryParams.put(AppConstants.IDISPLAYLENGTH, "2");
            queryParams.put(AppConstants.IDISPLAYSTART, "2");

            bookList = new ArrayList(Arrays.asList(bookcase1Book1RevDate1, bookcase1Book2RevDate1, bookcase2Book1RevDate1, bookcase1Book2RevDate2, bookcase3Book1RevDate1));
            List<BookcaseContentDAO> subList = new ArrayList<>();
            subList.addAll(bookList.subList(2, 4));
            when(iBookcaseContentAppMock.getBookcaseItems(isA(String.class), isA(List.class), isA(String.class))).thenReturn(bookList);

            BookcaseContentModel result = bookItemModelAppSvcImpl.getBookcaseItemModel(SSO, PORTALID, new HashedMap(), queryParams);

            assertEquals(result.getBookcaseContentDAOS(), subList);
        }
    }

