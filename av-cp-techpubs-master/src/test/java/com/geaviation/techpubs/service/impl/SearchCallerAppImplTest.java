package com.geaviation.techpubs.service.impl;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.exceptions.TechpubsException.TechpubsAppError;
import com.geaviation.techpubs.services.impl.SearchCallerAppImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

public class SearchCallerAppImplTest {

  @InjectMocks
  SearchCallerAppImpl searchCallerApp;

  @Mock
  RestTemplate restTemplateMock;

  @Mock
  HttpClientErrorException httpClientErrorExceptionMock;

  @Mock
  HttpServerErrorException httpServerErrorExceptionMock;

  private static final String SSO_ID_MOCK = "sso-id";
  private static final String PORTAL_ID_MOCK = "portal-id";

  @Before
  public void setup() {
    this.searchCallerApp = new SearchCallerAppImpl();
    MockitoAnnotations.initMocks(this);

    ReflectionTestUtils.setField(searchCallerApp, "searchAPIUrl", "http://testurl.com");
  }

  @Test
  public void callSearchEndpointSuccessfulIfResponseHasABody() throws TechpubsException {

    String searchMock = "payload";

    ResponseEntity<String> responseEntity = new ResponseEntity<>(
        searchMock, HttpStatus.OK);

    when(restTemplateMock.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
        eq(String.class))).thenReturn(responseEntity);

    String actualResponse = searchCallerApp.callSearchEndpoint(SSO_ID_MOCK, PORTAL_ID_MOCK,
        "payload");

    assertNotNull(actualResponse);
  }

  @Test
  public void callSearchEndpointReturnsNullIfResponseHasNoBody() throws TechpubsException {

    String searchMock = null;

    ResponseEntity<String> responseEntity = new ResponseEntity<>(
        searchMock, HttpStatus.OK);

    when(restTemplateMock.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
        eq(String.class))).thenReturn(responseEntity);

    String actualResponse = searchCallerApp.callSearchEndpoint(SSO_ID_MOCK, PORTAL_ID_MOCK,
        "");

    assertNull(actualResponse);
  }


  @Test
  public void callSearchEndpointThrowsInvalidParameterWhenClientError() throws TechpubsException {

    when(restTemplateMock.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
        eq(String.class))).thenThrow(httpClientErrorExceptionMock);

    Throwable e = Assertions.assertThrows(TechpubsException.class,
        () -> searchCallerApp.callSearchEndpoint(SSO_ID_MOCK, PORTAL_ID_MOCK, ""));

    assertEquals(((TechpubsException) e).getTechpubsAppError(), TechpubsAppError.INVALID_PARAMETER);
  }

  @Test
  public void callSearchEndpointThrowsInternalErrorWhenServerErrorOccurs() throws TechpubsException {
    when(restTemplateMock.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
        eq(String.class))).thenThrow(httpServerErrorExceptionMock);

    Throwable e = Assertions.assertThrows(TechpubsException.class,
        () -> searchCallerApp.callSearchEndpoint(SSO_ID_MOCK, PORTAL_ID_MOCK, ""));

    assertEquals(((TechpubsException) e).getTechpubsAppError(), TechpubsAppError.INTERNAL_ERROR);
  }
}
