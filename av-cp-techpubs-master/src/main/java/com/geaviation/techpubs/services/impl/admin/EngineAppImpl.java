package com.geaviation.techpubs.services.impl.admin;


import com.geaviation.techpubs.data.api.techlib.*;
import com.geaviation.techpubs.data.impl.AwsResourcesService;
import com.geaviation.techpubs.data.impl.ProgramDataSvcImpl;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.exceptions.TechpubsException.TechpubsAppError;
import com.geaviation.techpubs.models.techlib.*;
import com.geaviation.techpubs.models.techlib.dto.*;
import com.geaviation.techpubs.models.techlib.response.BookcaseKeyListResponse;
import com.geaviation.techpubs.models.techlib.response.CompanyMdmEngineModelResponse;
import com.geaviation.techpubs.services.api.admin.IEngineApp;
import com.geaviation.techpubs.services.util.AppConstants;
import com.geaviation.techpubs.services.util.StringUtils;
import com.geaviation.techpubs.services.util.admin.AdminAppUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.ListUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EngineAppImpl implements IEngineApp {

    private static final Logger log = LogManager.getLogger(EngineAppImpl.class);

    @Autowired
    private AdminAppUtil adminAppUtil;

    @Autowired
    private ICompanyEngineModelData iCompanyEngineModelData;

    @Autowired
    private ICompanyData iCompanyData;

    @Autowired
    private IEngineModelProgramData iEngineModelProgramData;

    @Autowired
    private IBookData iBookData;

    @Autowired
    private IPageBlkData iPageBlkData;

    @Autowired
    private IBookcaseVersionData iBookCaseVersionData;

    @Autowired
    private IPermissionData iPermissionData;

    @Autowired
    private ICompanyEnginePageblkEnablementData iCompanyEnginePageblkEnablementData;

    @Autowired
    private ICompanyEngineTechlvEnablementData iCompanyEngineTechlvEnablementData;

    @Autowired
    private ITechnologyLevelData iTechnologyLevelData;

    @Autowired
    private AwsResourcesService awsResourcesService;

    @Autowired
    private IUserRoleData iUserRoleData;

    @Autowired
    private IRoleData iRoleData;

    @Value("${AUDIT.TRAIL.ENABLED}")
    private boolean auditTrailEnabled;

    @Override
    public CompanyMdmEngineModelResponse getCompanyMdmEngineModels(String ssoId, String company)
        throws TechpubsException {
        List<UserRoleAttributes> attributesList = getUserRoleAttributes(ssoId, AppConstants.VIEW_ACTION);

        List<String> engineModels = attributesList.stream().map(UserRoleAttributes::getEngineModels).flatMap(List::stream).collect(Collectors.toList());

        boolean userAllAccess = engineModels.stream().anyMatch("all"::equalsIgnoreCase);

        // Grab the engine family -> models with icaoCode
        StringBuilder companyAssetResponse = adminAppUtil.getCompanyEngineFamilyModels(ssoId, company);
        Map<String, List<String>> companyEngines = adminAppUtil.parseMdmCompanyEngineModelResponse(companyAssetResponse);

        // Grab all engine family -> models with GEA org icaoCode
        StringBuilder allAssetResponse = adminAppUtil.getCompanyEngineFamilyModels(ssoId, AppConstants.GEA_ORG);
        Map<String, List<String>> allEngines = adminAppUtil.parseMdmCompanyEngineModelResponse(allAssetResponse);

        CompanyMdmEngineModelResponse companyMdmEngineModelResponse = new CompanyMdmEngineModelResponse();

        // filter out engines based on users access, add boolean flag mapping
        // for each engine that the company has access to
        allEngines.forEach((family, models) -> {
            Map<String, Boolean> modelMap = models.stream()
                                                .filter(e -> userAllAccess || engineModels.stream().anyMatch(e::equalsIgnoreCase))
                                                .collect(Collectors.toMap(e -> e,
                                                    e -> companyEngines.get(family) == null ? Boolean.FALSE : companyEngines.get(family).contains(e)));
            if (!modelMap.isEmpty()) {
                companyMdmEngineModelResponse.addCompanyMdmEngineModels(family, new TreeMap<>(modelMap));
            }
        });

        return companyMdmEngineModelResponse;
    }

    @Override
    public List<CompanyEngineModelEntity> getSavedCompanyEngineModels(String ssoId, String icaoCode) {
        List<UserRoleAttributes> attributesList = getUserRoleAttributes(ssoId, AppConstants.VIEW_ACTION);

        List<String> engineModels = attributesList.stream().map(UserRoleAttributes::getEngineModels).flatMap(List::stream).collect(Collectors.toList());

        boolean userAllAccess = engineModels.stream().anyMatch("all"::equalsIgnoreCase);

        List<CompanyEngineModelEntity> companyEngineModelEntities = iCompanyEngineModelData.findByIcaoCodeIgnoreCase(icaoCode);

        //get saved engines the user has access to and set previously enabled
        companyEngineModelEntities = companyEngineModelEntities.stream()
                                         .filter(e -> userAllAccess ? Boolean.TRUE
                                                          : engineModels.stream()
                                                                .anyMatch(e.getEngineModel()::equalsIgnoreCase))
                                         .peek(e -> e.setPreviouslyEnabled(iCompanyEngineModelData.isPreviouslyEnabled(e.getIcaoCode(), e.getEngineModel())))
                                         .collect(Collectors.toList());

        return companyEngineModelEntities;
    }

    @Override
    public List<BookEntity> getCompanyEngineModelBooks(String ssoId, String icaoCode, String engModel) throws TechpubsException {
        checkUserEnginePermission(Lists.newArrayList(engModel), ssoId, AppConstants.VIEW_ACTION);
        checkCompanyEngineViewable(Lists.newArrayList(engModel), icaoCode);

        // grab all engine model program entities based on engine model
        List<EngineModelProgramEntity> engineModelProgramEntities = iEngineModelProgramData.findByEngineModelIgnoreCase(engModel);

        // for all bookcases/programs associated to the engine model find books associated to them
        // and populate previously enabled field
        return engineModelProgramEntities.parallelStream()
                   .flatMap(e -> iBookData.findByBookcaseKey(e.getBookcaseKey()).parallelStream())
                                     .peek(bookEntity -> bookEntity.setPreviouslyEnabled(iBookData.isPreviouslyEnabled(bookEntity.getId(), icaoCode, engModel)))
                   .collect(Collectors.toList());
    }

    @Override
    public void deleteCompanyEngineModel(String ssoId, String icaoCode, String engineModel) throws TechpubsException {
        checkUserEnginePermission(Lists.newArrayList(engineModel), ssoId, AppConstants.EDIT_ACTION);
        checkCompanyEngineViewable(Lists.newArrayList(engineModel), icaoCode);

        try {
            CompanyEngineModelEntityPK companyEngineModelEntityPK = new CompanyEngineModelEntityPK();
            companyEngineModelEntityPK.setEngineModel(engineModel);
            companyEngineModelEntityPK.setIcaoCode(icaoCode);

            iCompanyEngineModelData.deleteById(companyEngineModelEntityPK);

            if (auditTrailEnabled) {
                Runnable auditRunnable = () -> awsResourcesService.writeCompaniesAuditLog(ssoId, icaoCode, engineModel,
                    null, null, null, null, null, AppConstants.DELETE);
                new Thread(auditRunnable).start();
            }
        }
        catch (IllegalArgumentException argException) {
            log.error(argException);
            throw new TechpubsException(TechpubsAppError.INVALID_PARAMETER);
        }
    }

    @Override
    public SMMDocsDto getCompanyEngineModelSMMDocuments(String ssoId, String icaoCode, String engModel)
        throws TechpubsException {
        checkUserEnginePermission(Lists.newArrayList(engModel), ssoId, AppConstants.VIEW_ACTION);
        checkCompanyEngineViewable(Lists.newArrayList(engModel), icaoCode);

        // grab all engine model program entities based on engine model
        List<EngineModelProgramEntity> engineModelPrograms = iEngineModelProgramData.findByEngineModelIgnoreCase(engModel);

        //retrieve all technology level entities and filter out only tech levels the user has access to
        List<TechnologyLevelEntity> techLevels = iTechnologyLevelData.findByListOfLevels(getUserTechnologyLevels(ssoId));

        SMMDocsDto smmDocsDto = new SMMDocsDto();

        //grab the online version for each bookcase/program
        Map<EngineModelProgramEntity, String> programVersionMap =
            engineModelPrograms.stream()
                .collect(Collectors.toMap(e -> e,
                    e -> {
                        String onlineVersion = iBookCaseVersionData.findOnlineBookcaseVersion(e.getBookcaseKey());
                        return onlineVersion == null ? "" : onlineVersion;
                    }
                ));

        // for all bookcases/programs associated to the engine model (with online version) find the smm documents
        // of type ic and manual (where ic has no parent manual), filter documents based on user's access, populate previously enabled
        programVersionMap.forEach((modelProgram, version) -> {
            if (StringUtils.isEmpty(version)) {
                //no version don't grab page blks for this bookcase/program
                return;
            }
            List<PageBlkDto> pageBlkDtos = iPageBlkData.findSMMDocsByBookcaseKeyAndPublicationTypeAndVersion(modelProgram.getBookcaseKey(),
                AppConstants.DOCUMENT_TYPE_SMM, AppConstants.PUB_TYPE_MANUAL, AppConstants.PUB_TYPE_IC, version);

            pageBlkDtos = pageBlkDtos.parallelStream()
                .filter(pageBlk -> techLevels.stream().anyMatch(level -> pageBlk.getTechnologyLevelId().equals(level.getId())))
                .filter(pageBlk -> {
                    // remove ics where there is a manual page blk for the page blk key and section id
                    if (AppConstants.PUB_TYPE_IC.equalsIgnoreCase(pageBlk.getPublicationType())) {
                        return !iPageBlkData.hasManWithPageBlkKeyAndSectionId(pageBlk.getPageblkKey(), pageBlk.getSectionId(), AppConstants.PUB_TYPE_MANUAL);
                    }
                    return true;
                })
                .peek(pageBlk -> pageBlk.setPreviouslyEnabled(iCompanyEnginePageblkEnablementData.exists(
                    Example.of(new CompanyEnginePageblkEnablementEntity(icaoCode, engModel, pageBlk.getPageblkKey(), pageBlk.getSectionId())))))
                .sorted()
                .collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(pageBlkDtos)) {
                smmDocsDto.addBookcaseSMMDocs(modelProgram.getBookcaseKey(), pageBlkDtos);
            }
        });

        return smmDocsDto;
    }

    @Override
    public List<TechLevelEngineResponse> getCompanyEngineModelTechLevel(String ssoId, String icaoCode, String engModel) throws TechpubsException {
        checkUserEnginePermission(Lists.newArrayList(engModel), ssoId, AppConstants.VIEW_ACTION);
        checkCompanyEngineViewable(Lists.newArrayList(engModel), icaoCode);

        // grab all engine model program entities based on engine model
        List<EngineModelProgramEntity> engineModelPrograms = iEngineModelProgramData.findByEngineModelIgnoreCase(engModel);

        Map<String, List<CompanyEngineTechlvEnablementEntity>>  bookcaseTechLeves = new HashMap<>();

        engineModelPrograms.forEach(e -> bookcaseTechLeves.put(e.getBookcaseKey(),
            iCompanyEngineTechlvEnablementData.findAllByIcaoCodeEngineModelandBookcase(icaoCode, engModel, e.getBookcaseKey())));

        //retrieve all technology levels and filter out only tech levels the user has access to
        List<TechnologyLevelEntity> usersTechLevels = iTechnologyLevelData.findByListOfLevels(getUserTechnologyLevels(ssoId));
        List<TechLevelEngineResponse> response = new ArrayList<>();

        bookcaseTechLeves.forEach((bookcase, techlvEnables) -> {

            TechLevelEngineResponse techLevelResponse = new TechLevelEngineResponse();
            List<TechLevelDto> techLevelDtos = new ArrayList<>();

            usersTechLevels.forEach(techLevel -> {

                Boolean previouslyEnabled = techlvEnables.stream()
                                                .anyMatch(e -> e.getTechnologyLevelId().equals(techLevel.getId()));

                techLevelDtos.add(new TechLevelDto(techLevel.getId(),
                    "Technology Level " + techLevel.getLevel(), previouslyEnabled));

            });
            if (!CollectionUtils.isEmpty(techLevelDtos)) {
                techLevelResponse.setBoocaseKey(bookcase);
                techLevelResponse.setTechLevels(techLevelDtos);
                response.add(techLevelResponse);
            }
        });

        return response;
    }

    @Override
    public void saveCompanyEngineModels(String ssoId, String icaoCode,
        AddCompanyEngineModelDto addCompanyEngineModelDto) throws TechpubsException {

        if (addCompanyEngineModelDto.getEngineModels() == null) {
            log.error("Invalid Add Engine Model Data in body");
            throw new TechpubsException(TechpubsAppError.INVALID_PARAMETER);
        }

        List<String> postedModels = addCompanyEngineModelDto.getEngineModels();
        checkUserEnginePermission(postedModels, ssoId, AppConstants.EDIT_ACTION);

        if (!companyExists(icaoCode)) {
            saveCompany(ssoId, icaoCode);
        }

        List<CompanyEngineModelEntity> persistedEntities = iCompanyEngineModelData.findByIcaoCodeIgnoreCase(icaoCode);
        List<CompanyEngineModelEntity> saveEntities = new ArrayList<>();

        // create new entities for company engine models not in database
        postedModels.forEach(model -> {
            if (persistedEntities.stream().noneMatch(e -> e.getEngineModel().equalsIgnoreCase(model))) {
                CompanyEngineModelEntity companyEngineModel = new CompanyEngineModelEntity();
                companyEngineModel.setIcaoCode(icaoCode);
                companyEngineModel.setEngineModel(model);
                companyEngineModel.setCreatedBy(ssoId);
                saveEntities.add(companyEngineModel);
            }
        });

        iCompanyEngineModelData.saveAll(saveEntities);
    }

    @Override
    public void saveCompanyEngineModelSMMDocuments(String ssoId, String icaoCode,
        AddEngineSMMDocsDto addEngineSMMDocsDto, Boolean enable) throws TechpubsException {

        if (addEngineSMMDocsDto.getEngineSMMDocuments() == null) {
            log.error("Invalid Add SMM Documents Data in body");
            throw new TechpubsException(TechpubsAppError.INVALID_PARAMETER);
        }

        Map<String, List<UUID>> postedEngineSMMDocuments = addEngineSMMDocsDto.getEngineSMMDocuments();
        checkUserEnginePermission(Lists.newArrayList(postedEngineSMMDocuments.keySet()), ssoId, AppConstants.EDIT_ACTION);
        checkCompanyEngineViewable(Lists.newArrayList(postedEngineSMMDocuments.keySet()), icaoCode);

        if (enable) {
            enableSmmDocuments(postedEngineSMMDocuments, icaoCode, ssoId);
        } else {
            disableSmmDocuments(postedEngineSMMDocuments, icaoCode, ssoId);
        }
    }

    @Override
    public void saveCompanyEngineModelTechnologyLevels(String ssoId, String icaoCode,
        AddCompanyEngineTechLevelDto addCompanyEngineTechLevelDto) throws TechpubsException {

        if (addCompanyEngineTechLevelDto.getEngineTechnologyLevels() == null) {
            log.error("Invalid Add Company Engine Tech Level Data in body");
            throw new TechpubsException(TechpubsAppError.INVALID_PARAMETER);
        }

        Map<String, Map<String, List<UUID>>> postedEngineTechLevels = addCompanyEngineTechLevelDto.getEngineTechnologyLevels();
        checkUserEnginePermission(Lists.newArrayList(postedEngineTechLevels.keySet()), ssoId, AppConstants.EDIT_ACTION);
        checkCompanyEngineViewable(Lists.newArrayList(postedEngineTechLevels.keySet()), icaoCode);

        final List<CompanyEngineTechlvEnablementEntity> saveEntities = new ArrayList<>();
        final List<CompanyEngineTechlvEnablementEntity> deleteEntities = new ArrayList<>();

        postedEngineTechLevels.forEach((engineModel, programTechLevel) ->
            programTechLevel.forEach((program, techLevelIds) -> {
                //find all persisted entities by icao / engine model / bookcase/program
                List<CompanyEngineTechlvEnablementEntity> persistedEntities =
                    iCompanyEngineTechlvEnablementData.findAllByIcaoCodeEngineModelandBookcase(icaoCode, engineModel, program);

                techLevelIds.parallelStream().forEach(techLevel -> {
                    CompanyEngineTechlvEnablementEntity exampleEntity =
                        new CompanyEngineTechlvEnablementEntity(icaoCode, engineModel, techLevel, program);

                    //get persisted entity or new entity for each smm document
                    CompanyEngineTechlvEnablementEntity newEntity = persistedEntities.stream()
                                                                        .filter(exampleEntity::equals)
                                                                        .findFirst()
                                                                        .orElse(exampleEntity);
                    //set user fields, and tech level entity
                    if (newEntity.getCreatedBy() == null) {
                        newEntity.setCreatedBy(ssoId);

                        Optional<TechnologyLevelEntity> techLevelEntity = iTechnologyLevelData.findById(techLevel);
                        techLevelEntity.ifPresent(newEntity::setTechLvById);
                    }
                    newEntity.setLastUpdatedBy(ssoId);
                    saveEntities.add(newEntity);
                });

                deleteEntities.addAll(ListUtils.subtract(persistedEntities, saveEntities));
            })
        );

        //save entities and delete all persisted entities we aren't saving now
        iCompanyEngineTechlvEnablementData.saveAll(saveEntities);
        iCompanyEngineTechlvEnablementData.deleteAll(deleteEntities);

        //audit enabled and disabled tech levels
        if (auditTrailEnabled) {
            Runnable auditRunnable = () -> {
                saveEntities.forEach(e -> awsResourcesService.writeCompaniesAuditLog(ssoId, e.getIcaoCode(), e.getEngineModel(),
                    AppConstants.DOCUMENT_TYPE_SMM,
                    AppConstants.TECHNOLOGY_LEVEL + " " + e.getTechLvById().getLevel(),
                    e.getBookcaseKey(), null, null, AppConstants.ENABLE));

                deleteEntities.forEach(e -> awsResourcesService.writeCompaniesAuditLog(ssoId, e.getIcaoCode(), e.getEngineModel(),
                    AppConstants.DOCUMENT_TYPE_SMM,
                    AppConstants.TECHNOLOGY_LEVEL + " " + e.getTechLvById().getLevel(),
                    e.getBookcaseKey(), null, null, AppConstants.DISABLE));
            };
            new Thread(auditRunnable).start();
        }
    }

    /**
     * Disable all SMM Documents passed in and audit.
     *
     * @param postedEngineSMMDocuments The mapping of Engine -> SMM Document Ids to disable.
     * @param icaoCode The company code to disable documents for
     * @param ssoId sso of the admin user performing disable
     */
    private void disableSmmDocuments(Map<String, List<UUID>> postedEngineSMMDocuments, String icaoCode, String ssoId) {
        Map<PageblkEntity, CompanyEnginePageblkEnablementEntity> deleteEntities = new HashMap<>();

        postedEngineSMMDocuments.forEach((engineModel, smmDocumentIds) -> {
             List<PageblkEntity> pageblks = smmDocumentIds.parallelStream()
                                                .map(iPageBlkData::findById)
                                                .filter(Optional::isPresent)
                                                .map(Optional::get)
                                                .collect(Collectors.toList());
            pageblks.forEach(pageblk -> {
                CompanyEnginePageblkEnablementEntityPK enableKey =
                    new CompanyEnginePageblkEnablementEntityPK(icaoCode, engineModel, pageblk.getPageblkKey(),
                        pageblk.getBookSection().getId());

                Optional<CompanyEnginePageblkEnablementEntity> disableEntity = iCompanyEnginePageblkEnablementData.findById(enableKey);

                if (!disableEntity.isPresent()) {
                    log.info("Company page blk enable entity not present for " + icaoCode + " " + engineModel + " "
                                 + pageblk.getPageblkKey() + " " + pageblk.getBookSection().getSectionKey());
                    return;
                }
                deleteEntities.put(pageblk, disableEntity.get());
            });
        });

        iCompanyEnginePageblkEnablementData.deleteAll(deleteEntities.values());

        if (auditTrailEnabled) {
            Runnable auditRunnable = () -> deleteEntities.forEach(
                (pageblk, pageblkEnable) ->
                    awsResourcesService.writeCompaniesAuditLog(ssoId, pageblkEnable.getIcaoCode(),
                        pageblkEnable.getEngineModel(), AppConstants.DOCUMENT_TYPE_SMM, pageblk.getTocTitle(),
                    iPageBlkData.findBookcaseKeyFromPageBlkId(pageblk.getId()), pageblkEnable.getPageblkKey(),
                    iPageBlkData.findBookKeyFromPageBlk(pageblk.getId()), AppConstants.DISABLE));
            new Thread(auditRunnable).start();
        }
    }

    /**
     * Enable all SMM Documents passed in not already persisted and audit.
     *
     * @param postedEngineSMMDocuments The mapping of Engine -> SMM Document Ids to enable.
     * @param icaoCode The company code to enable documents for.
     * @param ssoId sso of the admin user performing enable
     */
    private void enableSmmDocuments(Map<String, List<UUID>> postedEngineSMMDocuments, String icaoCode, String ssoId) {
        Map<PageblkEntity, CompanyEnginePageblkEnablementEntity> saveEntities = new HashMap<>();

        //create a new company page blk enable entity for each engine model and page blk
        postedEngineSMMDocuments.forEach((engineModel, smmDocumentIds) -> {

            List<PageblkEntity> pageblks = smmDocumentIds.parallelStream()
                                               .map(iPageBlkData::findById)
                                               .filter(Optional::isPresent)
                                               .map(Optional::get)
                                               .collect(Collectors.toList());
            pageblks.forEach(pageblk -> {

                CompanyEnginePageblkEnablementEntityPK enableKey =
                    new CompanyEnginePageblkEnablementEntityPK(icaoCode, engineModel, pageblk.getPageblkKey(),
                        pageblk.getBookSection().getId());

                Optional<CompanyEnginePageblkEnablementEntity> enableEntity = iCompanyEnginePageblkEnablementData.findById(enableKey);

                if (enableEntity.isPresent()) {
                    log.info("Company page blk enable entity present for " + icaoCode + " " + engineModel + " "
                                 + pageblk.getPageblkKey() + " " + pageblk.getBookSection().getSectionKey());
                    return;
                }

                //create new entity for company page blk enable
                CompanyEnginePageblkEnablementEntity newEntity = new CompanyEnginePageblkEnablementEntity(
                    icaoCode, engineModel, pageblk.getPageblkKey(), pageblk.getBookSection().getId());

                //set user fields
                newEntity.setCreatedBy(ssoId);
                newEntity.setLastUpdatedBy(ssoId);
                saveEntities.put(pageblk, newEntity);
            });
        });

        iCompanyEnginePageblkEnablementData.saveAll(saveEntities.values());

        if (auditTrailEnabled) {
            Runnable auditRunnable = () -> saveEntities.forEach(
                (pageblk, pageblkEnable) ->
                    awsResourcesService.writeCompaniesAuditLog(ssoId, pageblkEnable.getIcaoCode(),
                        pageblkEnable.getEngineModel(), AppConstants.DOCUMENT_TYPE_SMM, pageblk.getTocTitle(),
                    iPageBlkData.findBookcaseKeyFromPageBlkId(pageblk.getId()), pageblk.getPageblkKey(),
                    iPageBlkData.findBookKeyFromPageBlk(pageblk.getId()), AppConstants.ENABLE));
            new Thread(auditRunnable).start();
        }
    }

    /**
     * Check that the company has the specified engines enabled to view.
     *
     * @param checkEngines the engines to check
     * @param icaoCode the company code to check
     * @throws TechpubsException if the company does not have the engines enabled to view
     */
    private void checkCompanyEngineViewable(List<String> checkEngines, String icaoCode) throws TechpubsException {
        //verify company has engines viewable
        List<CompanyEngineModelEntityPK> modelsNotEnabled = checkEngines.stream()
                .map(engineModel -> new CompanyEngineModelEntityPK(icaoCode, engineModel))
                .filter(compEngModelPK -> !iCompanyEngineModelData.existsById(compEngModelPK))
                .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(modelsNotEnabled)) {
            log.error("Company: " + icaoCode + " does not have models: " + modelsNotEnabled.stream()
                                                                             .map(CompanyEngineModelEntityPK::getEngineModel)
                                                                             .collect(Collectors.toList()) + " enabled");
            throw new TechpubsException(TechpubsAppError.INVALID_PARAMETER);
        }
    }

    /**
     * Check that the user has permission for the action being performed on the specified engines.
     *
     * @param checkEngines the engines to check permissions for
     * @param ssoId the id of the user attempting the action
     * @param action the action the user is performing on the given engines
     * @throws TechpubsException if the user does not have the correct permission
     */
    private void checkUserEnginePermission(List<String> checkEngines, String ssoId, String action) throws TechpubsException {
        List<UserRoleAttributes> attributesList = getUserRoleAttributes(ssoId, action);
        List<String> engineModels = attributesList.parallelStream()
                                        .map(UserRoleAttributes::getEngineModels)
                                        .flatMap(List::stream)
                                        .collect(Collectors.toList());

        boolean userAllAccess = engineModels.stream().anyMatch("all"::equalsIgnoreCase);

        //check if user has access to the posted engines
        if (!userAllAccess) {
            //get all models the user doesn't have access to
            List subtractedModels = ListUtils.subtract(checkEngines, engineModels.stream()
                                                                         .map(String::toUpperCase)
                                                                         .collect(Collectors.toList()));
            if (!subtractedModels.isEmpty()) {
                log.error("User " + ssoId + " does not have access for action: " + action + " on models: " +  subtractedModels);
                throw new TechpubsException(TechpubsAppError.INVALID_PARAMETER);
            }
        }
    }

    /**
     * Retrieve the User's Technology Levels they have access to.
     * @param ssoId the user to check
     * @return list of technology levels the user has access to.
     */
    private List<String> getUserTechnologyLevels(String ssoId) {
        List<UserRoleEntity> userRoleEntities = iUserRoleData.findUserRoleEntityBySso(ssoId);
        List<String> userTechLevels = new ArrayList<>();

        userRoleEntities.forEach(e -> {
            Optional<RoleEntity> optRole = iRoleData.findById(e.getRole());
            optRole.ifPresent(roleEntity -> userTechLevels.addAll(roleEntity.getPolicy().getTechnologyLevels()));
        });

        return userTechLevels;
    }

    /**
     * Retrieve the User's Role Attributes, containing the list of Engines / AirFrames the User has
     * access to.
     * @param ssoid the user to check
     * @return list of engines the user has access to.
     */
    private List<UserRoleAttributes> getUserRoleAttributes(String ssoid, String action) {
        return iPermissionData.findUserRoleAttributes(ssoid, AppConstants.COMPANIES_TAB, action);
    }

    private void saveCompany(String ssoId, String icaoCode) {
        Timestamp ts = new Timestamp(new Date().getTime());
        CompanyEntity company = new CompanyEntity(icaoCode, ssoId, ts, ssoId, ts);
        iCompanyData.save(company);
    }

    private boolean companyExists(String icaoCode) { return iCompanyData.existsById(icaoCode); }

    @Override
    public BookcaseKeyListResponse getBookcaseKeyMappings(String engineModel) {
        List<EngineModelProgramEntity> engineModelPrograms = iEngineModelProgramData.findByEngineModelIgnoreCase(engineModel);
        return new BookcaseKeyListResponse(engineModelPrograms.stream().map(EngineModelProgramEntity::getBookcaseKey).collect(Collectors.toList()));
    }
}
