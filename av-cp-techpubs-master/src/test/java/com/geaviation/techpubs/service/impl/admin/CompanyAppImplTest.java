package com.geaviation.techpubs.service.impl.admin;

import com.geaviation.techpubs.data.api.cwcadmin.ICwcadminOrgData;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.cwcadmin.dto.CompanyListDto;
import com.geaviation.techpubs.services.excel.exception.ExcelException;
import com.geaviation.techpubs.services.excel.model.FileWithBytes;
import com.geaviation.techpubs.services.impl.admin.CompanyAppImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class CompanyAppImplTest {

    @Mock
    ICwcadminOrgData mockICwcadminOrgData;

    @InjectMocks
    CompanyAppImpl companyAppImpl;

    private static final String COMPANY = "COMPANY";
    private static final String ICAO_CODE = "ICAO_CODE";
    private static final String DUNS_NUM = "DUNS_NUM";
    private static final int PAGE = 0;
    private static final String SORT_COMPANY_BY_DESC = "company|desc";
    private static final String SORT_COMPANY_BY_ASC = "company|asc";
    private static final String INVALID_SORT_BY_LENGTH = "sort";
    private static final String INVALID_SORT_BY_COLUMN = "sort|by";
    private static final String SEARCH_EMPTY = "";
    private static final String SEARCH = "SEARCH";

    private Page<CompanyListDto> companyListDtoPage;
    private List<CompanyListDto> companyListDtoList;
    private CompanyListDto companyListDto;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        companyListDto = new CompanyListDto(COMPANY, ICAO_CODE, DUNS_NUM);
    }

    @Test
    public void getCompaniesGivenPageAndSortByDescReturnListOfCompanies() throws TechpubsException {
        companyListDtoList = new ArrayList<>();
        companyListDtoList.add(companyListDto);
        companyListDtoPage = new PageImpl<>(companyListDtoList);
        when(mockICwcadminOrgData.findPortalAdminCompanies(any(Pageable.class))).thenReturn(companyListDtoPage);

        Page<CompanyListDto> getCompaniesResponse = companyAppImpl.getCompanies(PAGE,
            SORT_COMPANY_BY_DESC, SEARCH_EMPTY);

        assertFalse(getCompaniesResponse.getContent().isEmpty());
        assertTrue(getCompaniesResponse.getContent().get(0).getCompany().contains(COMPANY));
    }

    @Test
    public void getCompaniesGivenPageAndSortByAscReturnListOfCompanies() throws TechpubsException {
        companyListDtoList = new ArrayList<>();
        companyListDtoList.add(companyListDto);
        companyListDtoPage = new PageImpl<>(companyListDtoList);
        when(mockICwcadminOrgData.findPortalAdminCompanies(any(Pageable.class))).thenReturn(companyListDtoPage);

        Page<CompanyListDto> getCompaniesResponse = companyAppImpl.getCompanies(PAGE,
            SORT_COMPANY_BY_ASC, SEARCH_EMPTY);

        assertFalse(getCompaniesResponse.getContent().isEmpty());
        assertTrue(getCompaniesResponse.getContent().get(0).getCompany().contains(COMPANY));
    }

    @Test
    public void getCompaniesReturnGivenPageAndSortByDescAndSearchReturnListOfCompanies() throws TechpubsException {
        companyListDtoList = new ArrayList<>();
        companyListDtoList.add(companyListDto);
        companyListDtoPage = new PageImpl<>(companyListDtoList);
        when(mockICwcadminOrgData.searchPortalAdminCompanies(anyString(), anyString(), anyString(),
                any(Pageable.class))).thenReturn(companyListDtoPage);

        Page<CompanyListDto> getCompaniesResponse = companyAppImpl.getCompanies(PAGE,
            SORT_COMPANY_BY_DESC, SEARCH);

        assertFalse(getCompaniesResponse.getContent().isEmpty());
        assertTrue(getCompaniesResponse.getContent().get(0).getCompany().contains(COMPANY));
    }

    @Test
    public void getCompaniesReturnGivenPageAndSortByAscAndSearchReturnListOfCompanies() throws TechpubsException {
        companyListDtoList = new ArrayList<>();
        companyListDtoList.add(companyListDto);
        companyListDtoPage = new PageImpl<>(companyListDtoList);
        when(mockICwcadminOrgData.searchPortalAdminCompanies(anyString(), anyString(), anyString(),
            any(Pageable.class))).thenReturn(companyListDtoPage);

        Page<CompanyListDto> getCompaniesResponse = companyAppImpl.getCompanies(PAGE,
            SORT_COMPANY_BY_ASC, SEARCH);

        assertFalse(getCompaniesResponse.getContent().isEmpty());
        assertTrue(getCompaniesResponse.getContent().get(0).getCompany().contains(COMPANY));
    }

    @Test(expected = TechpubsException.class)
    public void getCompaniesGivenPageAndInvalidSortByLengthThrowsException() throws TechpubsException {
        companyAppImpl.getCompanies(PAGE, INVALID_SORT_BY_LENGTH, SEARCH_EMPTY);
    }

    @Test(expected = TechpubsException.class)
    public void getCompaniesGivenPageAndInvalidSortByColumnThrowsException() throws TechpubsException {
        companyAppImpl.getCompanies(PAGE, INVALID_SORT_BY_COLUMN, SEARCH_EMPTY);
    }

    @Test
    public void downloadCompaniesReturnsCompaniesSummaryExcelFile() throws ExcelException {
        companyListDtoList = new ArrayList<>();
        companyListDtoList.add(companyListDto);
        when(mockICwcadminOrgData.findPortalAdminCompanies()).thenReturn(companyListDtoList);

        FileWithBytes fileWithBytes = companyAppImpl.downloadCompanies();

        assertNotNull(fileWithBytes.getContents());
        assertEquals("companies_summary.xlsx", fileWithBytes.getFileName());
    }
}
