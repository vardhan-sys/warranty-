package com.geaviation.techpubs.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import com.geaviation.techpubs.data.impl.BookBookcaseService;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.BookDAO;
import com.geaviation.techpubs.models.BookcaseContentDAO;
import com.geaviation.techpubs.services.impl.BookApp;
import com.geaviation.techpubs.services.util.AppConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

public class BookDAOAppSvcImplTest {
    private static  final String PORTALID = "portal_id";
    private static  final String BOOKCASE_ITEM_TYPE = "BOOK";
    private List<String> bookcaseKeys = new ArrayList<>(Arrays.asList("gek112060", "gek112059"));

    @Mock(name="iTOCDataSvcImpl")
    BookBookcaseService bookBookcaseService;

    @InjectMocks
    BookApp bookApp;

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


        // Instantiate class we're testing and inject mocks
        this.bookApp = new BookApp();
        MockitoAnnotations.initMocks(this);

        ReflectionTestUtils.setField(bookApp, "geSpmBookcaseKey", "gek108792");
        ReflectionTestUtils.setField(bookApp, "hondaSpmBookcaseKey", "gek119360");



        when(bookBookcaseService.getBooksByBookcaseKey("gek108792")).thenReturn(new ArrayList(Arrays.asList(geSpmBook)));
        when(bookBookcaseService.getBooksByBookcaseKey("gek119360")).thenReturn(new ArrayList(Arrays.asList(hondaSpmBook)));
        when(bookBookcaseService.getBooksByBookcaseKey(isA(List.class))).thenReturn(new ArrayList<>(Arrays.asList(bookcase1Book1RevDate1, bookcase1Book2RevDate1)));
    }

    @Test
    public void whenGetBooksByBookcaseReturnsEmptyListShouldReturnABooklistObjectWithNoBooks() throws TechpubsException {
        when(bookBookcaseService.getBooksByBookcaseKey(isA(List.class))).thenReturn(new ArrayList<>());
        List <BookcaseContentDAO> result = bookApp
            .getBookcaseItems(PORTALID, bookcaseKeys, BOOKCASE_ITEM_TYPE );
        assert (result.isEmpty());
    }


   @Test
    public void whenGetBooksByBookcaseThrowsExceptionShouldRethrowTheException(){
       when(bookBookcaseService.getBooksByBookcaseKey(isA(List.class))).thenThrow(new NullPointerException());
       Assertions.assertThrows(NullPointerException.class, () -> bookApp
           .getBookcaseItems(PORTALID, bookcaseKeys, BOOKCASE_ITEM_TYPE ));
   }

   @Test
    public void whenGetBooksByBookcaseReturnsListShouldReturnListWithSameNumberOfItemsPlusTheSPMBooks(){
       List <BookcaseContentDAO> result = bookApp
           .getBookcaseItems(PORTALID, bookcaseKeys, BOOKCASE_ITEM_TYPE );
       assertEquals (result.size(), 3);
   }


    @Test
    public void whenGetBooksByBookcaseReturnsBooksThenResultListShouldIncludeGeSPMBook(){
        bookList = new ArrayList(Arrays.asList(bookcase1Book1RevDate1, bookcase1Book2RevDate1));
        when(bookBookcaseService.getBooksByBookcaseKey(isA(List.class))).thenReturn (bookList);

        List<BookcaseContentDAO> result = bookApp
            .getBookcaseItems(PORTALID, bookcaseKeys, BOOKCASE_ITEM_TYPE);

        assert(result.contains(geSpmBook));
    }

    @Test
    public void whenGetBooksByBookcaseReturnsBooksAndPortalIdIsNotHondaThenResultListShouldNotIncludeHondaSPMBook(){
        bookList = new ArrayList(Arrays.asList(bookcase1Book1RevDate1, bookcase1Book2RevDate1));
        when(bookBookcaseService.getBooksByBookcaseKey(isA(List.class))).thenReturn (bookList);

        List<BookcaseContentDAO> result = bookApp
            .getBookcaseItems(PORTALID, bookcaseKeys, BOOKCASE_ITEM_TYPE);

        assert(!result.contains(hondaSpmBook));
    }

    @Test
    public void whenGetBooksByBookcaseKeyReturnsListWithItemAndPortalIdIsHondaResultShouldIncludeHondaSpmBooks() {
        bookList = new ArrayList(Arrays.asList(bookcase1Book1RevDate1, bookcase1Book2RevDate1));
        when(bookBookcaseService.getBooksByBookcaseKey(isA(List.class))).thenReturn (bookList);

        List<BookcaseContentDAO> result = bookApp
            .getBookcaseItems(AppConstants.GEHONDA, bookcaseKeys, BOOKCASE_ITEM_TYPE);

        assert(result.contains(hondaSpmBook));
    }



    @Test
    public void whenGetBooksByBookcaseKeyReturnsListWithItemAndPortalIdIsHondaResultShouldIncludeGeSpmBooks() {
        bookList = new ArrayList(Arrays.asList(bookcase1Book1RevDate1, bookcase1Book2RevDate1));
        when(bookBookcaseService.getBooksByBookcaseKey(isA(List.class))).thenReturn (bookList);

        List<BookcaseContentDAO> result = bookApp
            .getBookcaseItems(AppConstants.GEHONDA, bookcaseKeys, BOOKCASE_ITEM_TYPE);

        assert(result.contains(geSpmBook));
    }






}
