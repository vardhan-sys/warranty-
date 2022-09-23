package com.geaviation.techpubs.service.impl;

import com.geaviation.dss.service.common.exception.TechnicalException;
import com.geaviation.techpubs.data.impl.BookcaseTOCData;
import com.geaviation.techpubs.models.BookcaseTocDAO;
import com.geaviation.techpubs.models.BookcaseTocModel;
import com.geaviation.techpubs.services.impl.BookcaseTOCApp;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

public class BookcaseTOCAppTest {
  @InjectMocks
  BookcaseTOCApp bookcaseTOCApp;

  @Mock
  BookcaseTOCData bookcaseTOCData;

  @Before
  public void setup() {
    // Instantiate class we're testing and inject mocks
    this.bookcaseTOCApp = new BookcaseTOCApp();
    MockitoAnnotations.initMocks(this);
    
  }

//  @Test
//  public void whenAnExceptionIsThrownThenShouldReturnANewBookcaseTOCModelWithSuccessFalse() {
//    when(bookcaseTOCData.getBookData(isA(String.class), isA(String.class), isNull()))
//            .thenThrow(new TechnicalException(new Exception()));
//
//    BookcaseTocModel result = bookcaseTOCApp.getBookcaseTOC("someBookcase", "", Boolean.FALSE, null);
//    assert(!result.isSuccess());
//  }

  @Test
  public void whenNoDataIsRetrievedThenShouldReturnBookcaseTOCModelWithSuccessTrue() {
    when(bookcaseTOCData.getBookData(isA(String.class), isA(String.class), isNull()))
            .thenReturn(new ArrayList<BookcaseTocDAO>());

    BookcaseTocModel result = bookcaseTOCApp.getBookcaseTOC("someBookcase", "", Boolean.FALSE, null);
    assert(result.isSuccess());
  }

  @Test
  public void whenDataIsRetrievedThenShouldReturnBookcaseTOCModelWithSuccessTrue() {
    when(bookcaseTOCData.getBookData(isA(String.class), isA(String.class), isNull()))
            .thenReturn(new ArrayList<BookcaseTocDAO>(Arrays.asList(new BookcaseTocDAO())));

    BookcaseTocModel result = bookcaseTOCApp.getBookcaseTOC("someBookcase", "", Boolean.FALSE, null);
    assert(result.isSuccess());
  }

  @Test
  public void whenDataIsRetrievedThenShouldReturnBookcaseTOCModelWithBookcaseTOCModelThatHasTheData() {
    List<BookcaseTocDAO> dataList =  new ArrayList<BookcaseTocDAO>(Arrays.asList(new BookcaseTocDAO()));
    when(bookcaseTOCData.getBookData(isA(String.class), isA(String.class), isNull()))
            .thenReturn(dataList);

    BookcaseTocModel result = bookcaseTOCApp.getBookcaseTOC("someBookcase", "", Boolean.FALSE, null);
    assertEquals(result.getBookcaseTOCItemList(), dataList);
  }
}
