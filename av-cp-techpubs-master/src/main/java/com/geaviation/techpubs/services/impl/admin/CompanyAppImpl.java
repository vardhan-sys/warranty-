package com.geaviation.techpubs.services.impl.admin;

import com.geaviation.techpubs.data.api.cwcadmin.ICwcadminOrgData;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.cwcadmin.dto.CompanyListDto;
import com.geaviation.techpubs.services.api.admin.ICompanyApp;
import com.geaviation.techpubs.services.excel.ExcelMaker;
import com.geaviation.techpubs.services.excel.exception.ExcelException;
import com.geaviation.techpubs.services.excel.model.ExcelSheet;
import com.geaviation.techpubs.services.excel.model.FileWithBytes;
import com.geaviation.techpubs.services.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class CompanyAppImpl implements ICompanyApp {

    @Autowired
    private ICwcadminOrgData iCwcadminOrgData;

    private final int pageSize = 50;

    private static final Logger log = LogManager.getLogger(CompanyAppImpl.class);

    public Page<CompanyListDto> getCompanies(int page, String sortBy, String searchTerm) throws TechpubsException {
        String[] sortBySplit = sortBy.split("\\|");
        if (sortBySplit.length != 2) {
            throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }

        String column = sortBy.split("\\|")[0];
        if (!"company".equals(column) && !"icaoCode".equals(column) && !"dunsNum".equals(column)) {
            throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }

        String order = sortBy.split("\\|")[1];

        Pageable pageRequest;
        if (order != null && "asc".equalsIgnoreCase(order)) {
            pageRequest = PageRequest.of(page, pageSize, Sort.by(column).ascending());
        } else if (order != null && "desc".equalsIgnoreCase(order)) {
            pageRequest = PageRequest.of(page, pageSize, Sort.by(column).descending());
        } else {
            pageRequest = PageRequest.of(page, pageSize, Sort.by(column));
        }

        if (!StringUtils.isEmpty(searchTerm)) {
            return iCwcadminOrgData.searchPortalAdminCompanies("%"+searchTerm+"%","%"+searchTerm+"%","%"+searchTerm+"%", pageRequest);
        } else {
            return iCwcadminOrgData.findPortalAdminCompanies(pageRequest);
        }
    }

    public FileWithBytes downloadCompanies() throws ExcelException {
        List<CompanyListDto> companyList = iCwcadminOrgData.findPortalAdminCompanies();

        ExcelSheet excelSheet = ExcelMaker.buildExcelSheet(companyList);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ExcelMaker.excelSheetToFile(excelSheet, os);
            return new FileWithBytes(os.toByteArray(), excelSheet.getFilename());
        } catch (IOException e) {
            throw new ExcelException("Could not write the excel file.", e);
        }
    }
}
