package com.geaviation.techpubs.services.impl.admin;

import com.geaviation.techpubs.data.api.techlib.IBookcaseData;
import com.geaviation.techpubs.data.api.techlib.IBookcaseVersionData;
import com.geaviation.techpubs.data.api.techlib.IEngineModelProgramData;
import com.geaviation.techpubs.data.api.techlib.IPermissionData;
import com.geaviation.techpubs.data.impl.AwsResourcesService;
import com.geaviation.techpubs.data.util.SearchLoaderUtil;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.techlib.EngineModelProgramEntity;
import com.geaviation.techpubs.models.techlib.UserRoleAttributes;
import com.geaviation.techpubs.models.techlib.dto.*;
import com.geaviation.techpubs.models.techlib.response.PublisherBookcaseVersionsResponse;
import com.geaviation.techpubs.models.techlib.response.PublisherSummaryResponse;
import com.geaviation.techpubs.services.api.admin.IPublisherApp;
import com.geaviation.techpubs.services.excel.ExcelMaker;
import com.geaviation.techpubs.services.excel.exception.ExcelException;
import com.geaviation.techpubs.services.excel.model.ExcelSheet;
import com.geaviation.techpubs.services.excel.model.FileWithBytes;
import com.geaviation.techpubs.services.util.AppConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.geaviation.techpubs.exceptions.TechpubsException.TechpubsAppError.INVALID_PARAMETER;

@Service
public class PublisherAppImpl implements IPublisherApp {

    @Autowired
    private IBookcaseData iBookcaseData;

    @Autowired
    private IBookcaseVersionData iBookcaseVersionData;

    @Autowired
    private IPermissionData iPermissionData;

    @Autowired
    private IEngineModelProgramData iEngineModelProgramData;

    @Autowired
    private AwsResourcesService awsResourcesService;

    @Autowired
    private SearchLoaderUtil searchLoaderUtil;

    @Value("${AUDIT.TRAIL.ENABLED}")
    private boolean auditTrailEnabled;

    private static final Logger log = LogManager.getLogger(PublisherAppImpl.class);

    /**
     * Filter out bookcases if the user does not have access to view them.
     * @param ssoId the user to check
     * @param sortBy column and direction to sort list (ex. bookcaseKey|asc)
     * @param searchTerm the term to search on bookcases
     * @return list of bookcases the user has access to.
     */
    public PublisherSummaryResponse getPublisherSummary(String ssoId, String sortBy, String searchTerm) throws TechpubsException {
        String[] sortBySplit = sortBy.split("\\|");
        if (sortBySplit.length != 2) {
            throw new TechpubsException(INVALID_PARAMETER);
        }

        String column = sortBy.split("\\|")[0];
        if (!"bookcaseTitle".equals(column) && !"bookcaseKey".equals(column) && !"engineFamily".equals(column) && !"onlineVersion".equals(column)) {
            throw new TechpubsException(INVALID_PARAMETER);
        }

        String order = sortBy.split("\\|")[1];
        if (!"asc".equalsIgnoreCase(order) && !"desc".equalsIgnoreCase(order)) {
            order = "";
        }

        List<BookcaseWithOnlineVersionDto> onlineBookcases = iBookcaseData.findBookcasesWithOnlineVersion(column, order, searchTerm);
        List<BookcaseWithOnlineVersionDto> filteredBookcases = filterBookcasesBasedOnUserAccess(ssoId, onlineBookcases);

        return new PublisherSummaryResponse(filteredBookcases);
    }

    /**
     * Filter out bookcases if the user does not have access to view them.
     * @param ssoId the user to check
     * @param onlineBookcases list of all onlineBookcases to filter
     * @return list of bookcases the user has access to.
     */
    private List<BookcaseWithOnlineVersionDto> filterBookcasesBasedOnUserAccess(String ssoId, List<BookcaseWithOnlineVersionDto> onlineBookcases) {
        List<UserRoleAttributes> userRoleAttributes = iPermissionData.findUserRoleAttributes(ssoId, AppConstants.PUBLISHER_TAB, AppConstants.VIEW_ACTION);
        List<String> userEngineModels = userRoleAttributes.stream().map(UserRoleAttributes::getEngineModels).flatMap(List::stream).collect(Collectors.toList());

        boolean userAllAccess = userEngineModels.stream().anyMatch("all"::equalsIgnoreCase);

        if (userAllAccess) {
            return onlineBookcases;
        } else {
            List<EngineModelProgramEntity> engineModelPrograms = iEngineModelProgramData.findByEngineModelList(userEngineModels);
            return onlineBookcases.stream()
                    .filter(bookcase -> engineModelPrograms.stream()
                            .anyMatch(engineModelProgram -> bookcase.getBookcaseKey()
                                    .equalsIgnoreCase(engineModelProgram.getBookcaseKey())
                            ))
                    .collect(Collectors.toList());
        }
    }

    /**
     * Download the publisher summary of bookcases.
     * @param ssoId the user to check
     * @param searchTerm the term to search on bookcases
     * @return excel file containing list of bookcases.
     */
    public FileWithBytes downloadPublisherSummary(String ssoId, String searchTerm) throws ExcelException {

        List<BookcaseWithOnlineVersionDto> onlineBookcases = iBookcaseData.findBookcasesWithOnlineVersion(null, null, searchTerm);
        List<BookcaseWithOnlineVersionDto> filteredBookcases = filterBookcasesBasedOnUserAccess(ssoId, onlineBookcases);

        ExcelSheet excelSheet = ExcelMaker.buildExcelSheet(filteredBookcases);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ExcelMaker.excelSheetToFile(excelSheet, os);
            return new FileWithBytes(os.toByteArray(), excelSheet.getFilename());
        } catch (IOException e) {
            throw new ExcelException("Could not write the excel file.", e);
        }
    }

    public BookcaseEngineModelsDto getBookcaseEngineModels(String bookcaseKey){
        return new BookcaseEngineModelsDto(iBookcaseData.getBookcaseEngineModels(bookcaseKey));
    }

    public PublisherBookcaseVersionsResponse getBookcaseVersions(String bookcaseKey) {
        List<PublisherBookcaseVersionStatusDto> versionList = null;
        // Different query for SPM bookcases
        if ("gek108792".equalsIgnoreCase(bookcaseKey) || "gek119360".equalsIgnoreCase(bookcaseKey)) {
            versionList = iBookcaseData.getSpmBookcaseVersions(bookcaseKey);
        } else {
            versionList = iBookcaseData.getBookcaseVersions(bookcaseKey);
        }
        for (PublisherBookcaseVersionStatusDto versionStatusDto : versionList) {
            String releaseDate = null;
            if (versionStatusDto.getReleaseDate() != null) {
                releaseDate = new SimpleDateFormat("yyyy-MM-dd").format((Timestamp)versionStatusDto.getReleaseDate());
            }
            versionStatusDto.setReleaseDate(releaseDate);
        }
        versionList.sort(
            Comparator.comparing(PublisherBookcaseVersionStatusDto::getBookcaseVersion).reversed());
        return new PublisherBookcaseVersionsResponse(versionList);
    }

    public boolean updateBookcaseVersionsStatus(String ssoId, String bookcaseKey,
        BookcaseVersionUpdateDto bookcaseVersionUpdateDTO) throws TechpubsException {
        verifyUserAccessToBookcase(ssoId, bookcaseKey);
        // Verify that the status is valid so that our DB key and DTO Enum work and if release date is set for online
        verifyValidPublisherStatusesAndReleaseDate(bookcaseVersionUpdateDTO, bookcaseKey);
        bookcaseVersionUpdateDTO.getBookcaseVersions().forEach(bookcaseVersionInfo -> {
            UUID bookcaseVersionId = iBookcaseData
                    .getBookcaseVersionId(bookcaseKey, bookcaseVersionInfo.getVersion());
            if (bookcaseVersionInfo.getStatus().equalsIgnoreCase(AppConstants.ONLINE)) {

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Date date = null;
                try {
                    date = df.parse(bookcaseVersionInfo.getReleaseDate());
                } catch (ParseException pe) {
                    log.error("Could not parse release date. Format should be yyyy-MM-dd.", pe);
                }
                iBookcaseVersionData.updateBookcaseVersionStatusAndReleaseDate(bookcaseVersionInfo.getStatus().toLowerCase(),
                        new Timestamp(date.getTime()), bookcaseVersionId);

                searchLoaderUtil.invokeSearchLoader(bookcaseKey, bookcaseVersionInfo.getVersion());

            } else {
                iBookcaseVersionData.updateBookcaseVersionStatus(bookcaseVersionInfo.getStatus().toLowerCase(),
                        bookcaseVersionId);
            }

            if (auditTrailEnabled) {
                awsResourcesService
                        .writePublisherAuditLog(ssoId, bookcaseKey, bookcaseVersionInfo.getVersion(),
                                bookcaseVersionInfo.getAuditAction(),bookcaseVersionInfo.getReleaseDate());
            }

        });

        return true;
    }

    private void verifyUserAccessToBookcase(String ssoId, String bookcaseKey) throws TechpubsException {
        List<UserRoleAttributes> userRoleAttributes = iPermissionData.findUserRoleAttributes(ssoId, AppConstants.PUBLISHER_TAB, AppConstants.VIEW_ACTION);
        List<String> userEngineModels = userRoleAttributes.stream().map(UserRoleAttributes::getEngineModels).flatMap(List::stream).collect(Collectors.toList());

        boolean userAllAccess = userEngineModels.stream().anyMatch("all"::equalsIgnoreCase);

        if (!userAllAccess) {
            List<String> engineModels = iEngineModelProgramData.findEngineModelsByBookcaseKey(bookcaseKey);
            for (String engineModel : engineModels) {
                if (!userEngineModels.contains(engineModel)) {
                    log.error("User " + ssoId + " does not have access to Bookcase " + bookcaseKey + ".");
                    throw new TechpubsException(INVALID_PARAMETER);
                }
            }
        }
    }

    private void verifyValidPublisherStatusesAndReleaseDate(BookcaseVersionUpdateDto bookcaseVersionUpdateDTO, String bookcaseKey)
        throws TechpubsException {
        for (BookcaseVersionDto bookcase : bookcaseVersionUpdateDTO.getBookcaseVersions()) {
            if (!bookcase.hasValidStatus()) {
              log.error("No records updated. Bookcase " + bookcaseKey + " does not have a valid status.");
                throw new TechpubsException(INVALID_PARAMETER);
            }

            if (bookcase.getStatus().equalsIgnoreCase(AppConstants.ONLINE) && bookcase.getReleaseDate() == null) {
                    log.error("No records updated. Setting a bookcase online requires a release date.");
                    throw new TechpubsException(INVALID_PARAMETER);
            }
        }
    }
}