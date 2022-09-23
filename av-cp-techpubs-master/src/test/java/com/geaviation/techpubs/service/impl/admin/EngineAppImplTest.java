package com.geaviation.techpubs.service.impl.admin;

import com.geaviation.techpubs.data.api.techlib.*;
import com.geaviation.techpubs.data.impl.AwsResourcesService;
import com.geaviation.techpubs.data.impl.ProgramDataSvcImpl;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.techlib.*;
import com.geaviation.techpubs.models.techlib.dto.*;
import com.geaviation.techpubs.models.techlib.response.CompanyMdmEngineModelResponse;
import com.geaviation.techpubs.services.impl.admin.EngineAppImpl;
import com.geaviation.techpubs.services.util.AppConstants;
import com.geaviation.techpubs.services.util.admin.AdminAppUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.collections.Sets;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Component
public class EngineAppImplTest {

    @Mock
    private AdminAppUtil adminAppUtil;

    @Mock
    private ICompanyEngineModelData iCompanyEngineModelData;

    @Mock
    private IEngineModelProgramData iEngineModelProgramData;

    @Mock
    private ICompanyData iCompanyData;

    @Mock
    private IPermissionData iPermissionData;

    @Mock
    private IBookData iBookData;

    @Mock
    private IPublicationTypeData iPublicationTypeData;

    @Mock
    private IPageBlkData iPageBlkData;

    @Mock
    private IBookcaseVersionData iBookcaseVersionDataMock;

    @Mock
    private ICompanyEnginePageblkEnablementData iCompanyEnginePageblkEnablementData;

    @Mock
    private ICompanyEngineTechlvEnablementData iCompanyEngineTechlvEnablementData;

    @Mock
    private ITechnologyLevelData iTechnologyLevelData;

    @Mock
    private AwsResourcesService awsResourcesService;

    @Mock
    private IUserRoleData iUserRoleData;

    @Mock
    private IRoleData iRoleData;

    @Mock
    private ProgramDataSvcImpl programDataSvc;

    @InjectMocks
    private EngineAppImpl iEngineApp;

    CompanyEngineModelEntity companyModel;
    CompanyEngineModelEntity companyModel2;
    CompanyEngineModelEntity companyModel3;

    EngineModelProgramEntity engineModelProgram;
    EngineModelProgramEntity engineModelProgram2;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        companyModel = new CompanyEngineModelEntity();
        companyModel.setIcaoCode("DAL");
        companyModel.setEngineModel("CF34-1");
        companyModel.setCreatedBy("111");

        companyModel2 = new CompanyEngineModelEntity();
        companyModel2.setIcaoCode("DAL");
        companyModel2.setEngineModel("CF34-10");
        companyModel2.setCreatedBy("111");

        companyModel3 = new CompanyEngineModelEntity();
        companyModel3.setIcaoCode("DAL");
        companyModel3.setEngineModel("H80");
        companyModel3.setCreatedBy("111");

        List<CompanyEngineModelEntity> previouslyEnabledModels = new ArrayList<>();
        previouslyEnabledModels.add(companyModel);
        previouslyEnabledModels.add(companyModel2);
        previouslyEnabledModels.add(companyModel3);

        when(iCompanyEngineModelData.findByIcaoCodeIgnoreCase(Mockito.anyString()))
            .thenReturn(previouslyEnabledModels);

        engineModelProgram = new EngineModelProgramEntity();
        engineModelProgram.setBookcaseKey("bookcase1");
        engineModelProgram.setEngineModel("H80");

        engineModelProgram2 = new EngineModelProgramEntity();
        engineModelProgram2.setBookcaseKey("bookcase2");
        engineModelProgram2.setEngineModel("GE90");

        List<EngineModelProgramEntity> engineModelProgramEntities = new ArrayList<>();
        engineModelProgramEntities.add(engineModelProgram);
        engineModelProgramEntities.add(engineModelProgram2);

        when(iEngineModelProgramData.findByEngineModelIgnoreCase(Mockito.anyString()))
            .thenReturn(engineModelProgramEntities);

        when(iCompanyData.existsById(Mockito.anyString())).thenReturn(Boolean.TRUE);

        UserRoleAttributes userRoleAttributes = new UserRoleAttributes();
        List<String> engineList = new ArrayList<>();
        engineList.add("all");
        userRoleAttributes.setEngineModels(engineList);
        List<UserRoleAttributes> attributesList = Arrays.asList(userRoleAttributes);
        when(iPermissionData.findUserRoleAttributes(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(attributesList);
    }

    @Test
    public void getCompanyMdmEngineModels_Test() throws TechpubsException {

        StringBuilder stringBuilder = new StringBuilder();
        when(adminAppUtil.getCompanyEngineFamilyModels(Mockito.anyString(), Mockito.anyString())).thenReturn(stringBuilder);

        Map<String, List<String>> familyModelRelationCompany = new TreeMap<>();
        familyModelRelationCompany.put("CF34", Lists.newArrayList("CF34-10E", "CF34-3"));

        Map<String, List<String>> familyModelRelationAll = new TreeMap<>();
        familyModelRelationAll.put("CF34", Lists.newArrayList("CF34-10E", "CF34-3", "CF34-8C", "CF34-8E", "CF34-9E"));

        doReturn(familyModelRelationCompany, familyModelRelationAll).when(adminAppUtil).parseMdmCompanyEngineModelResponse(Mockito.any());

        CompanyMdmEngineModelResponse companyMdmEngineModelResponse = iEngineApp.getCompanyMdmEngineModels("123", "DAL");
        assertEquals(companyMdmEngineModelResponse.getCompanyMdmEngineModels().keySet().size(), 1);
        assertEquals(companyMdmEngineModelResponse.getCompanyMdmEngineModels().get("CF34").size(), 5);
        assertEquals(companyMdmEngineModelResponse.getCompanyMdmEngineModels().get("CF34").values().stream().filter(e -> e).count(), 2);
        assertEquals(companyMdmEngineModelResponse.getCompanyMdmEngineModels().get("CF34").values().stream().filter(e -> !e).count(), 3);
    }

    @Test
    public void getCompanyMdmEngineModels_RestrictedUserTest() throws TechpubsException {
        UserRoleAttributes userRoleAttributes = new UserRoleAttributes();
        List<String> engineList = Lists.newArrayList("CF34-10E", "CF34-3");
        userRoleAttributes.setEngineModels(engineList);
        List<UserRoleAttributes> attributesList = Arrays.asList(userRoleAttributes);
        when(iPermissionData.findUserRoleAttributes(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(attributesList);

        StringBuilder stringBuilder = new StringBuilder();
        when(adminAppUtil.getCompanyEngineFamilyModels(Mockito.anyString(), Mockito.anyString())).thenReturn(stringBuilder);

        Map<String, List<String>> familyModelRelationCompany = new TreeMap<>();
        familyModelRelationCompany.put("CF34", Lists.newArrayList("CF34-10E", "CF34-3", "CF34-8C"));
        familyModelRelationCompany.put("GE90", Lists.newArrayList("GE90-1"));

        Map<String, List<String>> familyModelRelationAll = new TreeMap<>();
        familyModelRelationAll.put("CF34", Lists.newArrayList("CF34-10E", "CF34-3", "CF34-8C", "CF34-8E", "CF34-9E"));
        familyModelRelationAll.put("GE90", Lists.newArrayList("GE90-1", "GE90-2", "GE90-3"));

        doReturn(familyModelRelationCompany, familyModelRelationAll).when(adminAppUtil).parseMdmCompanyEngineModelResponse(Mockito.any());

        CompanyMdmEngineModelResponse response = iEngineApp.getCompanyMdmEngineModels("123", "DAL");
        assertEquals(response.getCompanyMdmEngineModels().keySet().size(), 1);
        assertEquals(response.getCompanyMdmEngineModels().get("CF34").size(), 2);
        assertEquals(response.getCompanyMdmEngineModels().get("CF34").values().stream().filter(e -> e).count(), 2);
        assertEquals(response.getCompanyMdmEngineModels().get("CF34").values().stream().filter(e -> !e).count(), 0);
    }

    @Test
    public void getSavedCompanyEngineModels_Test() {
        companyModel.setCompanyEnginePageblkEnablementEntities(Sets.newSet(
            new CompanyEnginePageblkEnablementEntity("DAL", "H80", "pageblkKey1", UUID.randomUUID())));
        companyModel2.setCompanyEnginePageblkEnablementEntities(Sets.newSet(
            new CompanyEnginePageblkEnablementEntity("DAL", "H80", "pageblkKey2", UUID.randomUUID())));
        companyModel3.setCompanyEnginePageblkEnablementEntities(Sets.newSet(
            new CompanyEnginePageblkEnablementEntity("DAL", "H80","pageblkKey3", UUID.randomUUID())));

        when(iCompanyEngineModelData.isPreviouslyEnabled(Mockito.anyString(), Mockito.anyString())).thenReturn(Boolean.TRUE);

        List<CompanyEngineModelEntity> response = iEngineApp.getSavedCompanyEngineModels("123", "DAL");
        assertEquals(response.size(), 3);
        assertTrue(response.contains(companyModel));
        assertTrue(companyModel.isPreviouslyEnabled());
        assertTrue(response.contains(companyModel2));
        assertTrue(response.contains(companyModel3));
    }

    @Test(expected = TechpubsException.class)
    public void getCompanyEngineModelBooks_UserAccessFailTest() throws TechpubsException {
        //verify exception thrown without access to engine passed in
        UserRoleAttributes userRoleAttributes = new UserRoleAttributes();
        List<String> engineList = new ArrayList<>();
        engineList.add("GE90");

        userRoleAttributes.setEngineModels(engineList);
        List<UserRoleAttributes> attributesList = Arrays.asList(userRoleAttributes);
        when(iPermissionData.findUserRoleAttributes(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(attributesList);

        iEngineApp.getCompanyEngineModelBooks("123", "DAL", "H80");
        fail("Exception not thrown with user access restriction.");
    }


    @Test(expected = TechpubsException.class)
    public void getCompanyEngineModelTechLevel_RestrictedUserFailTest() throws  TechpubsException {
        UserRoleAttributes userRoleAttributes = new UserRoleAttributes();
        List<String> engineList = new ArrayList<>();
        engineList.add("GE90");
        userRoleAttributes.setEngineModels(engineList);

        List<UserRoleAttributes> attributesList = Arrays.asList(userRoleAttributes);
        when(iPermissionData.findUserRoleAttributes(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(attributesList);

        iEngineApp.getCompanyEngineModelTechLevel("123", "DAL", "H80");
        fail("Exception not thrown with user access restriction.");
    }

    @Test
    public void saveCompanyEngineModels_Test() throws TechpubsException {
        List<String> engineModels = Lists.newArrayList("CF34-1", "CF34-10",
            "CF34-10E", "CF34-3", "CF34-8C", "CF34-8E", "H80");

        AddCompanyEngineModelDto addCompanyEngineModelDto = new AddCompanyEngineModelDto();
        addCompanyEngineModelDto.setEngineModels(engineModels);

        iEngineApp.saveCompanyEngineModels("222", "DAL", addCompanyEngineModelDto);
        verify(iCompanyEngineModelData, times(1)).saveAll(Mockito.anyList());
    }

    @Test
    public void saveCompanyEngineModels_RestrictedUserTest() throws TechpubsException {
        List<String> engineModels = Lists.newArrayList("CF34-1", "H80");

        AddCompanyEngineModelDto addCompanyEngineModelDto = new AddCompanyEngineModelDto();
        addCompanyEngineModelDto.setEngineModels(engineModels);

        UserRoleAttributes userRoleAttributes = new UserRoleAttributes();
        List<String> engineList = Lists.newArrayList("CF34-1", "H80", "CF34-8E");

        when(iCompanyEngineModelData.findByIcaoCodeIgnoreCase(Mockito.any())).thenReturn(new ArrayList<>());

        userRoleAttributes.setEngineModels(engineList);
        List<UserRoleAttributes> attributesList = Arrays.asList(userRoleAttributes);
        when(iPermissionData.findUserRoleAttributes(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(attributesList);

        iEngineApp.saveCompanyEngineModels("222", "DAL", addCompanyEngineModelDto);
        verify(iCompanyEngineModelData, times(1)).saveAll(Mockito.anyList());
    }

    @Test(expected = TechpubsException.class)
    public void saveCompanyEngineModels_RestrictedUserFailTest() throws TechpubsException {
        List<String> engineModels = Lists.newArrayList("CF34-1");

        AddCompanyEngineModelDto addCompanyEngineModelDto = new AddCompanyEngineModelDto();
        addCompanyEngineModelDto.setEngineModels(engineModels);

        UserRoleAttributes userRoleAttributes = new UserRoleAttributes();
        List<String> engineList = new ArrayList<>();
        engineList.add("GE90");

        userRoleAttributes.setEngineModels(engineList);
        List<UserRoleAttributes> attributesList = Arrays.asList(userRoleAttributes);
        when(iPermissionData.findUserRoleAttributes(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(attributesList);

        iEngineApp.saveCompanyEngineModels("222", "DAL", addCompanyEngineModelDto);
        fail("Exception not thrown with user access restriction.");
    }

    @Test(expected = TechpubsException.class)
    public void saveCompanyEngineModels_InvalidDTOTest() throws TechpubsException {
        AddCompanyEngineModelDto addCompanyEngineModelDto = new AddCompanyEngineModelDto();

        iEngineApp.saveCompanyEngineModels("222", "DAL", addCompanyEngineModelDto);
        fail("Exception not thrown with invalid DTO object.");
    }

    @Test
    public void saveCompanyEngineModelSMMDocuments_Test() throws TechpubsException {
        List<UUID> smmDocUUIDs = Lists.newArrayList(UUID.randomUUID(), UUID.randomUUID(),
            UUID.randomUUID());
        Map<String, List<UUID>> engineSMMDocuments = new HashMap<>();
        engineSMMDocuments.put("CF34-1", smmDocUUIDs);

        AddEngineSMMDocsDto addEngineSMMDocsDto = new AddEngineSMMDocsDto();
        addEngineSMMDocsDto.setEngineSMMDocuments(engineSMMDocuments);

        when(iCompanyEngineModelData.existsById(Mockito.any())).thenReturn(Boolean.TRUE);

        BookSectionEntity bookSectionEntity = new BookSectionEntity();
        bookSectionEntity.setSectionKey("sectionKey");

        PageblkEntity pageblkEntity = new PageblkEntity();
        pageblkEntity.setTocTitle("Title 1");
        pageblkEntity.setBookSection(bookSectionEntity);

        Optional<PageblkEntity> pageblkOpt = Optional.of(pageblkEntity);
        when(iPageBlkData.findById(Mockito.any(UUID.class))).thenReturn(pageblkOpt);

        CompanyEnginePageblkEnablementEntity delEntity = new CompanyEnginePageblkEnablementEntity(
            "DAL", "CF34-1", "pageblkKey", UUID.randomUUID());

        when(iCompanyEnginePageblkEnablementData.findAllByIcaoCodeandEngineModel("DAL", "CF34-1"))
            .thenReturn(Lists.newArrayList(delEntity));

        iEngineApp.saveCompanyEngineModelSMMDocuments("123", "DAL", addEngineSMMDocsDto, Boolean.TRUE);
        verify(iCompanyEnginePageblkEnablementData, times(1)).saveAll(Mockito.any());

        iEngineApp.saveCompanyEngineModelSMMDocuments("123", "DAL", addEngineSMMDocsDto, Boolean.FALSE);
        verify(iCompanyEnginePageblkEnablementData, times(1)).deleteAll(Mockito.any());
    }

    @Test(expected = TechpubsException.class)
    public void saveCompanyEngineModelSMMDocuments_InvalidDTOTest() throws TechpubsException {
        AddEngineSMMDocsDto addEngineSMMDocsDto = new AddEngineSMMDocsDto();
        iEngineApp.saveCompanyEngineModelSMMDocuments("123", "DAL", addEngineSMMDocsDto, Boolean.TRUE);
        fail("Exception not thrown with invalid DTO");
    }

    @Test(expected = TechpubsException.class)
    public void saveCompanyEngineModelSMMDocuments_RestrictedUserTest() throws TechpubsException {
        List<UUID> smmDocUUIDs = Lists.newArrayList(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        Map<String, List<UUID>> engineSMMDocuments = new HashMap<>();
        engineSMMDocuments.put("CF34-1", smmDocUUIDs);
        engineSMMDocuments.put("CF34-10", smmDocUUIDs);

        AddEngineSMMDocsDto addEngineSMMDocsDto = new AddEngineSMMDocsDto();
        addEngineSMMDocsDto.setEngineSMMDocuments(engineSMMDocuments);

        UserRoleAttributes userRoleAttributes = new UserRoleAttributes();
        List<String> engineList = Lists.newArrayList("CF34-1", "H80");

        userRoleAttributes.setEngineModels(engineList);
        List<UserRoleAttributes> attributesList = Arrays.asList(userRoleAttributes);
        when(iPermissionData.findUserRoleAttributes(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(attributesList);

        iEngineApp.saveCompanyEngineModelSMMDocuments("123", "DAL", addEngineSMMDocsDto, Boolean.TRUE);
        fail("Exception not thrown for restricted user access");
    }

    @Test(expected = TechpubsException.class)
    public void saveCompanyEngineModelSMMDocuments_FailUserTest() throws TechpubsException {
        List<UUID> smmDocUUIDs = Lists.newArrayList(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        Map<String, List<UUID>> engineSMMDocuments = new HashMap<>();
        engineSMMDocuments.put("CF34-1", smmDocUUIDs);
        engineSMMDocuments.put("CF34-10", smmDocUUIDs);

        AddEngineSMMDocsDto addEngineSMMDocsDto = new AddEngineSMMDocsDto();
        addEngineSMMDocsDto.setEngineSMMDocuments(engineSMMDocuments);

        when(iCompanyEngineModelData.existsById(Mockito.any())).thenReturn(Boolean.FALSE);

        iEngineApp.saveCompanyEngineModelSMMDocuments("123", "DAL", addEngineSMMDocsDto, Boolean.TRUE);
        fail("Exception not thrown for company engine model not being present");
    }

    @Test
    public void saveCompanyEngineModelTechnologyLevels_Test() throws TechpubsException {
        Map<String, List<UUID>> techLevelUUIDs = new HashMap<>();
        techLevelUUIDs.put("program1", Lists.newArrayList(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
        Map<String, Map<String, List<UUID>>> engineTechLevels = new HashMap<>();
        engineTechLevels.put("CF34-1", techLevelUUIDs);

        AddCompanyEngineTechLevelDto addCompanyEngineTechLevelDto = new AddCompanyEngineTechLevelDto();
        addCompanyEngineTechLevelDto.setEngineTechnologyLevels(engineTechLevels);

        when(iCompanyEngineModelData.existsById(Mockito.any())).thenReturn(Boolean.TRUE);

        TechnologyLevelEntity techLevelEntity = new TechnologyLevelEntity();
        techLevelEntity.setLevel("1");
        Optional<TechnologyLevelEntity> techLevelOpt = Optional.of(techLevelEntity);
        when(iTechnologyLevelData.findById(Mockito.any(UUID.class))).thenReturn(techLevelOpt);

        CompanyEngineTechlvEnablementEntity delEntity = new CompanyEngineTechlvEnablementEntity("DAL", "CF34-1", UUID.randomUUID(), "bookcase1");
        delEntity.setTechLvById(techLevelEntity);
        when(iCompanyEngineTechlvEnablementData.findAllByIcaoCodeEngineModelandBookcase("DAL", "CF34-1", "bookcase1"))
            .thenReturn(Lists.newArrayList(delEntity));

        iEngineApp.saveCompanyEngineModelTechnologyLevels("123", "DAL", addCompanyEngineTechLevelDto);
        verify(iCompanyEngineTechlvEnablementData, times(1)).saveAll(Mockito.any());
        verify(iCompanyEngineTechlvEnablementData, times(1)).deleteAll(Mockito.any());
    }

    @Test(expected = TechpubsException.class)
    public void saveCompanyEngineModelTechnologyLevels_InvalidDTOTest() throws TechpubsException {
        AddCompanyEngineTechLevelDto addCompanyEngineTechLevelDto = new AddCompanyEngineTechLevelDto();
        iEngineApp.saveCompanyEngineModelTechnologyLevels("123", "DAL", addCompanyEngineTechLevelDto);
        fail("Exception not thrown with invalid DTO");
    }

    @Test(expected = TechpubsException.class)
    public void saveCompanyEngineModelTechnologyLevels_RestrictedUserTest() throws TechpubsException {
        Map<String, List<UUID>> techLevelUUIDs = new HashMap<>();
        techLevelUUIDs.put("program1", Lists.newArrayList(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
        Map<String, Map<String, List<UUID>>> engineTechLevels = new HashMap<>();
        engineTechLevels.put("CF34-10", techLevelUUIDs);

        AddCompanyEngineTechLevelDto addCompanyEngineTechLevelDto = new AddCompanyEngineTechLevelDto();
        addCompanyEngineTechLevelDto.setEngineTechnologyLevels(engineTechLevels);

        when(iCompanyEngineModelData.existsById(Mockito.any())).thenReturn(Boolean.TRUE);

        UserRoleAttributes userRoleAttributes = new UserRoleAttributes();
        List<String> engineList = Lists.newArrayList("CF34-1", "H80");

        userRoleAttributes.setEngineModels(engineList);
        List<UserRoleAttributes> attributesList = Arrays.asList(userRoleAttributes);
        when(iPermissionData.findUserRoleAttributes(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(attributesList);

        iEngineApp.saveCompanyEngineModelTechnologyLevels("123", "DAL", addCompanyEngineTechLevelDto);
        fail("Exception not thrown for restricted user access");
    }

    @Test(expected = TechpubsException.class)
    public void saveCompanyEngineModelTechnologyLevels_FailUserTest() throws TechpubsException {
        Map<String, List<UUID>> techLevelUUIDs = new HashMap<>();
        techLevelUUIDs.put("program1", Lists.newArrayList(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
        Map<String, Map<String, List<UUID>>> engineTechLevels = new HashMap<>();
        engineTechLevels.put("CF34-10", techLevelUUIDs);
        engineTechLevels.put("CF34-1", techLevelUUIDs);

        AddCompanyEngineTechLevelDto addCompanyEngineTechLevelDto = new AddCompanyEngineTechLevelDto();
        addCompanyEngineTechLevelDto.setEngineTechnologyLevels(engineTechLevels);

        when(iCompanyEngineModelData.existsById(Mockito.any())).thenReturn(Boolean.FALSE);

        iEngineApp.saveCompanyEngineModelTechnologyLevels("123", "DAL", addCompanyEngineTechLevelDto);
        fail("Exception not thrown for company engine model not being present");
    }
}
