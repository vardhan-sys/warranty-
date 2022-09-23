package com.geaviation.techpubs.services.api.admin;

import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.cwcadmin.dto.CompanyListDto;
import com.geaviation.techpubs.services.excel.exception.ExcelException;
import com.geaviation.techpubs.services.excel.model.FileWithBytes;
import org.springframework.data.domain.Page;

public interface ICompanyApp {

     Page<CompanyListDto> getCompanies(int page, String sortBy, String searchTerm) throws TechpubsException;
     FileWithBytes downloadCompanies() throws ExcelException;
}
