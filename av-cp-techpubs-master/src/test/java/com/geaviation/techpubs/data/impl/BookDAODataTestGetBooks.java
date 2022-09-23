package com.geaviation.techpubs.data.impl;

import com.geaviation.dss.service.common.exception.TechnicalException;
import com.geaviation.techpubs.data.api.IProgramData;
import com.geaviation.techpubs.data.api.techlib.IBookData;
import com.geaviation.techpubs.data.api.techlib.IBookcaseVersionData;
import com.geaviation.techpubs.models.BookDAO;
import com.geaviation.techpubs.models.BookcaseContentDAO;
import com.geaviation.techpubs.models.TocModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;


public class BookDAODataTestGetBooks {
    public static final String BOOKCASE_NAME = "gek112161";

    @Mock(name="programDataSvc")
    IProgramData programDataMock;

    @Mock
    IBookcaseVersionData iBookcaseVersionDataMock;

    @Mock
    IBookData iBookDataMock;

    @InjectMocks
    BookBookcaseService bookBookcaseServiceImpl;

    BookDAO bookItem1;
    BookDAO bookItem2;
    BookDAO bookItem3;

    List<BookDAO> mockDBReturnList;

    @Before
    public void setUp()  {

        // Instantiate class we're testing and inject mocks
        this.bookBookcaseServiceImpl = new BookBookcaseService();
        MockitoAnnotations.initMocks(this);

        bookItem1 = new BookDAO();
        bookItem1.setKey("BookDAO 1");
        bookItem1.setTitle("BookDAO 1 Title");
        bookItem1.setRevisiondate("20190101");
        bookItem1.setRevisionNumber("BookDAO 1 Revision Num");
        bookItem1.setBookcaseKey("BookDAO 1 Bookcase Key");
        bookItem1.setBookcasetitle("BookDAO 1 Bookcase Title");

        bookItem2 = new BookDAO();

        bookItem3 = new BookDAO();
        bookItem3.setKey("BookDAO 3");
        bookItem3.setTitle("BookDAO 3 Title");
        bookItem3.setRevisiondate("20190303");
        bookItem3.setRevisionNumber("BookDAO 3 Revision Num");
        bookItem3.setBookcaseKey("BookDAO 3 Bookcase Key");
        bookItem3.setBookcasetitle("BookDAO 3 Bookcase Title");

        mockDBReturnList = new ArrayList<>(Arrays.asList(bookItem1, bookItem2, bookItem3));

        when(iBookDataMock.findByBookcaseKeyAndVersion(isA(String.class), isA(String.class))).thenReturn(mockDBReturnList);
        when(iBookcaseVersionDataMock.findOnlineBookcaseVersion(isA(String.class))).thenReturn("");
        when(programDataMock.getTocsByProgram(isA(String.class))).thenReturn(new ArrayList<>());
    }

    @Test
    public void whenDBReturnsEmptyListThenShouldReturnEmtpyBookList(){
        when(iBookDataMock.findByBookcaseKeyAndVersion(isA(String.class), isA(String.class))).thenReturn(new ArrayList<>());
        List<BookcaseContentDAO> resultList =  bookBookcaseServiceImpl.getBooksByBookcaseKey(new ArrayList<>(Arrays.asList(BOOKCASE_NAME)));
      assert (resultList.isEmpty());
    }

    @Test
    public void whenDBReturnsListOfItemsThenShouldReturnListWithSameNumberOfBookItems(){
        List<BookcaseContentDAO> resultList =  bookBookcaseServiceImpl.getBooksByBookcaseKey(new ArrayList<>(Arrays.asList(BOOKCASE_NAME)));
        assert (resultList.size() == 3);
    }

    @Test
    public void whenGetOnlineVersionThrowsExceptionThenShouldThrowTechnicalException(){
        when(iBookcaseVersionDataMock.findOnlineBookcaseVersion(isA(String.class))).thenThrow(TechnicalException.class);
        Assertions.assertThrows(TechnicalException.class, () -> bookBookcaseServiceImpl.getBooksByBookcaseKey(new ArrayList<>(Arrays.asList(BOOKCASE_NAME))));
    }

    @Test
    public void whenGetTocsByProgramThrowsExceptionThenShouldThrowTechnicalException(){
        when(programDataMock.getTocsByProgram(isA(String.class))).thenThrow(TechnicalException.class);
        Assertions.assertThrows(TechnicalException.class, () -> bookBookcaseServiceImpl.getBooksByBookcaseKey(new ArrayList<>(Arrays.asList(BOOKCASE_NAME))));
    }

    /**********************************************************************************************
     ************************************** POJO Mapping Tests ************************************
     **********************************************************************************************/

    @Test
    public void whenBookcaseKeyIsNullThenBooksBookcaseKeyShouldBeNull() {
        List<BookcaseContentDAO> resultList =  bookBookcaseServiceImpl.getBooksByBookcaseKey(new ArrayList<>(Arrays.asList(BOOKCASE_NAME)));
        assert (resultList.get(1).getBookcaseKey() == null);
    }

    @Test
    public void whenBookcaseKeyAttributeIsNotNullThenBooksbbookcaseKeyShouldBeEqualToBookcasekeyAttributeValue() {
        List<BookcaseContentDAO> resultList =  bookBookcaseServiceImpl.getBooksByBookcaseKey(new ArrayList<>(Arrays.asList(BOOKCASE_NAME)));
        assertEquals(resultList.get(0).getBookcaseKey(), (bookItem1.getBookcaseKey()));
    }

    @Test
    public void whenBookKeyIsNullThenBooksBookKeyShouldBeNull() {
        List<BookcaseContentDAO> resultList =  bookBookcaseServiceImpl.getBooksByBookcaseKey(new ArrayList<>(Arrays.asList(BOOKCASE_NAME)));
        assert (((BookDAO)resultList.get(1)).getKey() == null);
    }

    @Test
    public void whenBookKeyAttributeIsNotNullThenBooksKeyShouldBeEqualToBookKeyAttributeValue() {
        List<BookcaseContentDAO> resultList =  bookBookcaseServiceImpl.getBooksByBookcaseKey(new ArrayList<>(Arrays.asList(BOOKCASE_NAME)));
        assert (((BookDAO)resultList.get(0)).getKey().equals(bookItem1.getKey()));
    }

    @Test
    public void whenBookTitleIsNullThenBooksTitleShouldBeNull() {
        List<BookcaseContentDAO> resultList =  bookBookcaseServiceImpl.getBooksByBookcaseKey(new ArrayList<>(Arrays.asList(BOOKCASE_NAME)));
        assert (resultList.get(1).getTitle() == null);
    }

    @Test
    public void whenBookTitleAttributeIsNotNullThenBooksTitleShouldBeEqualToBookTitleAttributeValue() {
        List<BookcaseContentDAO> resultList =  bookBookcaseServiceImpl.getBooksByBookcaseKey(new ArrayList<>(Arrays.asList(BOOKCASE_NAME)));
        assert (resultList.get(0).getTitle().equals(bookItem1.getTitle()));
    }

    @Test
    public void whenBookRevisionDateIsNullThenBooksRevisionDateShouldBeNull() {
        List<BookcaseContentDAO> resultList =  bookBookcaseServiceImpl.getBooksByBookcaseKey(new ArrayList<>(Arrays.asList(BOOKCASE_NAME)));
        assertEquals(resultList.get(1).getRevisiondate(), "");
    }

    @Test
    public void whenBookRevisionDateAttributeIsNotNullThenBooksRevisionDateShouldBeEqualToBookRevisionDateAttributeValue() {
        List<BookcaseContentDAO> resultList =  bookBookcaseServiceImpl.getBooksByBookcaseKey(new ArrayList<>(Arrays.asList(BOOKCASE_NAME)));
        assertEquals (resultList.get(0).getRevisiondate(), "2019-01-01");
    }

    @Test
    public void whenBookRevisionNumIsNullThenBooksRevisionNumShouldBeNull() {
        List<BookcaseContentDAO> resultList =  bookBookcaseServiceImpl.getBooksByBookcaseKey(new ArrayList<>(Arrays.asList(BOOKCASE_NAME)));
        assert (((BookDAO)resultList.get(1)).getRevisionNumber() == null);
    }

    @Test
    public void whenBookRevisionNumAttributeIsNotNullThenBooksRevisionNumShouldBeEqualToBookRevisionNumAttributeValue() {
        List<BookcaseContentDAO> resultList =  bookBookcaseServiceImpl.getBooksByBookcaseKey(new ArrayList<>(Arrays.asList(BOOKCASE_NAME)));
        assert (((BookDAO)resultList.get(0)).getRevisionNumber().equals(bookItem1.getRevisionNumber()));
    }

    @Test
    public void whenBookcaseTitleAttributeIsNullThenBooksBookcaseTitleShouldBeNull() {
        List<BookcaseContentDAO> resultList =  bookBookcaseServiceImpl.getBooksByBookcaseKey(new ArrayList<>(Arrays.asList(BOOKCASE_NAME)));
        assert (resultList.get(1).getBookcasetitle() == null);
    }

    @Test
    public void whenBookcaseTitleAttributeIsNotNullThenBooksBookcaseTitleShouldBeEqualToBookcaseTitleAttributeValue() {
        List<BookcaseContentDAO> resultList =  bookBookcaseServiceImpl.getBooksByBookcaseKey(new ArrayList<>(Arrays.asList(BOOKCASE_NAME)));
        assert (resultList.get(0).getBookcasetitle().equals(bookItem1.getBookcasetitle()));
    }

    @Test
    public void whenGetTocsByProgramReturnsListOfItemsThenReturnListShouldContainSameNumberOfBookItems(){
        String tocModel1Title = "tocModel1 Title";
        String tocModel2Title = "tocModel2 Title";

        TocModel tocModel1 = new TocModel();
        tocModel1.setTitle(tocModel1Title);
        TocModel tocModel2 = new TocModel();
        tocModel2.setTitle(tocModel2Title);

        when(programDataMock.getTocsByProgram(isA(String.class))).thenReturn(new ArrayList<>(Arrays.asList(tocModel1, tocModel2)));
        List<BookcaseContentDAO> resultList =  bookBookcaseServiceImpl.getBooksByBookcaseKey(new ArrayList<>(Arrays.asList(BOOKCASE_NAME)));
        assert (resultList.size() == 5);
    }

    @Test
    public void whenGetTocsByProgramReturnsListOfItemsThenReturnListShouldContainTheItemsInTheSameOrder(){
        String tocModel1Title = "tocModel1 Title";
        String tocModel2Title = "tocModel2 Title";

        TocModel tocModel1 = new TocModel();
        tocModel1.setTitle(tocModel1Title);
        TocModel tocModel2 = new TocModel();
        tocModel2.setTitle(tocModel2Title);

        when(programDataMock.getTocsByProgram(isA(String.class))).thenReturn(new ArrayList<>(Arrays.asList(tocModel1, tocModel2)));
        List<BookcaseContentDAO> resultList =  bookBookcaseServiceImpl.getBooksByBookcaseKey(new ArrayList<>(Arrays.asList(BOOKCASE_NAME)));
        assert (resultList.get(3).getTitle().equals(tocModel1Title));
        assert (resultList.get(4).getTitle().equals(tocModel2Title));
    }

    @Test
    public void whenDBReturnsEmtpyListAndGetTocsByProgramReturnsListOfItemsThenReturnListBeEmpty(){
        String tocModel1Title = "tocModel1 Title";
        String tocModel2Title = "tocModel2 Title";

        TocModel tocModel1 = new TocModel();
        tocModel1.setTitle(tocModel1Title);
        TocModel tocModel2 = new TocModel();
        tocModel2.setTitle(tocModel2Title);

        when(iBookDataMock.findByBookcaseKeyAndVersion(isA(String.class), isA(String.class))).thenReturn(new ArrayList<>());
        when(programDataMock.getTocsByProgram(isA(String.class))).thenReturn(new ArrayList<>(Arrays.asList(tocModel1, tocModel2)));
        List<BookcaseContentDAO> resultList =  bookBookcaseServiceImpl.getBooksByBookcaseKey(new ArrayList<>(Arrays.asList(BOOKCASE_NAME)));
        assert (resultList.isEmpty());
    }
}
