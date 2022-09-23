package com.geaviation.techpubs.service.impl;

import com.geaviation.techpubs.config.ArchivalS3Config;
import com.geaviation.techpubs.data.api.techlib.IArchivalCompanyRepo;
import com.geaviation.techpubs.data.api.techlib.IArchivalRepo;
import com.geaviation.techpubs.data.api.techlib.IArchvialDocumentData;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.techlib.ArchivalCompanyEntity;
import com.geaviation.techpubs.services.impl.ArchivalService;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ArchivalServiceTest {

    private static final String geae = "GEAE";
    private static final String validCompany = "DEL";
    private static final String invalidCompany = "asdf";

    private ArchivalService archivalService;
    private IArchivalRepo archivalRepo;
    private IArchivalCompanyRepo archivalCompanyRepo;
    private IArchvialDocumentData iArchvialDocumentData;
    private ArchivalS3Config archivalS3Config;

    @Before
    public void setup() {
        archivalCompanyRepo = mock(IArchivalCompanyRepo.class);
        archivalRepo = mock(IArchivalRepo.class);
        archivalService = new ArchivalService(archivalRepo, archivalCompanyRepo, iArchvialDocumentData, archivalS3Config);
    }

    @Test
    public void testThatGeaeUsersHaveAccessToArchivalDocuments() throws TechpubsException {
        ArchivalCompanyEntity archivalCompanyEntity = new ArchivalCompanyEntity();
        archivalCompanyEntity.setId(UUID.randomUUID());
        archivalCompanyEntity.setIcaoCode(geae);

        when(archivalCompanyRepo.findByIcaoCode(any())).thenReturn(archivalCompanyEntity);

        boolean hasAccess = archivalService.hasAccess(geae);
        assertTrue(hasAccess);
    }

    @Test
    public void testThatValidCompanyHasAccessToArchivalDocuments() throws TechpubsException {
        ArchivalCompanyEntity archivalCompanyEntity = new ArchivalCompanyEntity();
        archivalCompanyEntity.setId(UUID.randomUUID());
        archivalCompanyEntity.setIcaoCode(validCompany);

        when(archivalCompanyRepo.findByIcaoCode(any())).thenReturn(archivalCompanyEntity);

        boolean hasAccess = archivalService.hasAccess(validCompany);
        assertTrue(hasAccess);
    }

    @Test
    public void testThatInvalidCompanyDoesNotHaveAccessToArchivalDocuments() throws TechpubsException {
        ArchivalCompanyEntity archivalCompanyEntity = new ArchivalCompanyEntity();
        archivalCompanyEntity.setId(UUID.randomUUID());
        archivalCompanyEntity.setIcaoCode(invalidCompany);

        when(archivalCompanyRepo.findByIcaoCode(any())).thenReturn(null);

        boolean hasAccess = archivalService.hasAccess(invalidCompany);
        assertFalse(hasAccess);
    }

}
