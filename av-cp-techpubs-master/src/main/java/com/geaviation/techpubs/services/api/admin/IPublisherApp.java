package com.geaviation.techpubs.services.api.admin;

import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.techlib.dto.BookcaseEngineModelsDto;
import com.geaviation.techpubs.models.techlib.dto.BookcaseVersionUpdateDto;
import com.geaviation.techpubs.models.techlib.response.PublisherBookcaseVersionsResponse;
import com.geaviation.techpubs.models.techlib.response.PublisherSummaryResponse;
import com.geaviation.techpubs.services.excel.exception.ExcelException;
import com.geaviation.techpubs.services.excel.model.FileWithBytes;

public interface IPublisherApp {

    PublisherSummaryResponse getPublisherSummary(String ssoId, String sortBy, String searchTerm) throws TechpubsException;

    FileWithBytes downloadPublisherSummary(String ssoId, String searchTerm) throws ExcelException;

    BookcaseEngineModelsDto getBookcaseEngineModels(String bookcaseKey);

    PublisherBookcaseVersionsResponse getBookcaseVersions(String bookcase);

    boolean updateBookcaseVersionsStatus(String ssoId, String bookcaseKey, BookcaseVersionUpdateDto bookcaseVersionUpdateDTO) throws TechpubsException;
}
