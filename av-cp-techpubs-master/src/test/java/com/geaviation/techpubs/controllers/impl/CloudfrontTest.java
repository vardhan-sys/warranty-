package com.geaviation.techpubs.controllers.impl;

import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.services.api.IProgramApp;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(Enclosed.class)
public class CloudfrontTest {

    public static final String PORTAL = "CWC";
    public static final String SSOID = "212719881";
    private static final String SM_SSOID = "sm_ssoid";
    private static final String PORTAL_ID = "portal_id";

    @Mock
    private IProgramApp iProgramAppMock;


    @InjectMocks
    private DocumentDownloadControllerImpl documentDownloadController;

    @Before
    public void setUp() {
        // Instantiate class we're testing and inject mocks
        this.documentDownloadController = new DocumentDownloadControllerImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getCloudFrontCookiesReturn500IfUserIsNotAuthorized() throws TechpubsException {
        List<String> programs = new ArrayList<>();
        when(iProgramAppMock.getAuthorizedPrograms(any(), any(), any())).thenReturn(programs);

        ResponseEntity response = documentDownloadController.getCloudFrontCookies("gek112060","CWC","gek108745",null);

        assertEquals(response.getStatusCodeValue(), 500);
    }
}