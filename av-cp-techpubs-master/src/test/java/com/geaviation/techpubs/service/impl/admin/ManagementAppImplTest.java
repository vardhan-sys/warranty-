package com.geaviation.techpubs.service.impl.admin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geaviation.techpubs.data.api.techlib.*;
import com.geaviation.techpubs.models.techlib.EngineModelEntity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.geaviation.techpubs.data.api.cwcadmin.ICwcadminUsrData;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.cwcadmin.dto.UserDto;
import com.geaviation.techpubs.models.techlib.RoleEntity;
import com.geaviation.techpubs.models.techlib.UserRoleAttributes;
import com.geaviation.techpubs.models.techlib.UserRoleEntity;
import com.geaviation.techpubs.models.techlib.dto.AddUserRoleAttributesDto;
import com.geaviation.techpubs.models.techlib.dto.UpdateUserRoleAttributesDto;
import com.geaviation.techpubs.models.techlib.dto.UserRoleAttributesDto;
import com.geaviation.techpubs.models.techlib.dto.UserRolesDto;
import com.geaviation.techpubs.services.excel.exception.ExcelException;
import com.geaviation.techpubs.services.excel.model.FileWithBytes;
import com.geaviation.techpubs.services.impl.admin.ManagementAppImpl;
import com.geaviation.techpubs.services.util.admin.AdminAppUtil;
import com.google.common.collect.ImmutableMap;

public class ManagementAppImplTest {

    @Mock
    ICwcadminUsrData mockICwcadminUsrData;

    @Mock
    IRoleData iRoleDataMock;

    @Mock
    IUserRoleData mockIUserRoleData;

    @Mock
    IUserData mockIUserData;

    @Mock
    AdminAppUtil adminAppUtil;

    @Mock
    private IPermissionData iPermissionDataMock;

    @Mock
    private IEngineModelData iEngineModelDataMock;

    @InjectMocks
    ManagementAppImpl managementAppImpl;
    
    private static final String SSO_ID = "SSO";
    private static final String SSO_ID2 = "SSO2";
    private static final String USER_SSO_ID = "USER_SSO_ID";
    private static final String ROLE = "ROLE";
    private static final String ROLE2 = "ROLE2";
    private static final int PAGE = 0;
    private static final String SORT_SSO_BY_DESC = "sso|desc";
    private static final String SORT_SSO_BY_ASC = "sso|asc";
    private static final String SORT_SSO_BY_NONE = "sso|none";
    private static final String INVALID_SORT_BY_LENGTH = "sort";
    private static final String INVALID_SORT_BY_COLUMN = "sort|by";
    private static final String SEARCH_EMPTY = "";
    private static final String SEARCH = "SEARCH";
    private static final String ENGINE_FAMILY = "ENGINE_FAMILY";
    private static final String PRODUCT_EMPTY = "";
    private static final String PRODUCT = "product";
    private static final String RESOURCE = "resource";

    private List<String> engineModels;
    private List<String> airFrames;
    private List<String> docTypes;
    private List<UserRoleEntity> userRoleEntityList;
    private List<UserRolesDto> userRolesDtoList;
    private List<UserDto> userDtoList;
    private Page<UserRolesDto> userRolesDtoPage;
    private Page<UserDto> userDtoPage;
    private List<String> userSsoIdList;
    private List<String> userRoleList;
    private List<UserRoleAttributesDto> userRoleAttributesDtoList;
    private UserRoleEntity userRoleEntity;
    private UserRoleAttributes userRoleAttributes;
    private UserRolesDto userRolesDto;
    private UserDto userDto;
    private RoleEntity roleEntity;
    private AddUserRoleAttributesDto addUserRoleAttributesDto;
    private UserRoleAttributesDto userRoleAttributesDto;
    private UpdateUserRoleAttributesDto updateUserRoleAttributesDto;
    private EngineModelEntity engineModelEntity1;
    private EngineModelEntity engineModelEntity2;
    private List <EngineModelEntity> engineModelEntityList;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        engineModels = new ArrayList<>(Arrays.asList("engine_model1", "engine_model2"));
        airFrames = new ArrayList<>(Arrays.asList("air_frame1", "air_frame2"));
        docTypes = new ArrayList<>(Arrays.asList("doc_type1", "doc_type2"));

        userRoleAttributes = new UserRoleAttributes(engineModels, airFrames, docTypes);
        userRoleEntity = new UserRoleEntity(SSO_ID, ROLE, userRoleAttributes, null, null, null, null);
        userRolesDto = new UserRolesDto(SSO_ID, null, null, null, null);
        userDto = new UserDto(SSO_ID, null, null, null, null);
        roleEntity = new RoleEntity();
        roleEntity.setName(ROLE);

        userSsoIdList = new ArrayList<>(Arrays.asList(SSO_ID, SSO_ID2));
        userRoleList = new ArrayList<>(Arrays.asList(ROLE, ROLE2));
        addUserRoleAttributesDto = new AddUserRoleAttributesDto(userSsoIdList, userRoleList, userRoleAttributes);
        updateUserRoleAttributesDto = new UpdateUserRoleAttributesDto(ROLE, ROLE2, userRoleAttributes);
        userRoleAttributesDto = new UserRoleAttributesDto(ROLE, userRoleAttributes);

        engineModelEntity1 = new EngineModelEntity();
        engineModelEntity1.setFamily("family1");
        engineModelEntity1.setModel("model1");
        engineModelEntity2 = new EngineModelEntity();
        engineModelEntity2.setFamily("family2");
        engineModelEntity2.setModel("model2");
        engineModelEntityList = new ArrayList<>();
        engineModelEntityList.add(engineModelEntity1);
        engineModelEntityList.add(engineModelEntity2);
    }

    @Test
    public void getUsersGivenPageAndSortBySsoDescReturnListOfUsers() throws TechpubsException {
        userRoleEntityList = new ArrayList<>();
        userRoleEntityList.add(userRoleEntity);

        userRolesDtoList = new ArrayList<>();
        userRolesDtoList.add(userRolesDto);

        userRolesDtoPage = new PageImpl<>(userRolesDtoList);

        when(mockIUserRoleData.findAll()).thenReturn(userRoleEntityList);
        when(mockICwcadminUsrData.findDocAdminUsers(anySet(), any(Pageable.class))).thenReturn(userRolesDtoPage);

        Page<UserRolesDto> getUsersResponse = managementAppImpl.getUsers(PAGE, SORT_SSO_BY_DESC, SEARCH_EMPTY,PRODUCT_EMPTY);

        assertFalse(getUsersResponse.getContent().isEmpty());
        assertTrue(getUsersResponse.getContent().get(0).getRoles().contains(ROLE));
    }

    @Test
    public void getUsersGivenPageAndSortBySsoAscReturnListOfUsers() throws TechpubsException {
        userRoleEntityList = new ArrayList<>();
        userRoleEntityList.add(userRoleEntity);

        userRolesDtoList = new ArrayList<>();
        userRolesDtoList.add(userRolesDto);

        userRolesDtoPage = new PageImpl<>(userRolesDtoList);

        when(mockIUserRoleData.findAll()).thenReturn(userRoleEntityList);
        when(mockICwcadminUsrData.findDocAdminUsers(anySet(), any(Pageable.class))).thenReturn(userRolesDtoPage);

        Page<UserRolesDto> getUsersResponse = managementAppImpl.getUsers(PAGE, SORT_SSO_BY_ASC, SEARCH_EMPTY,PRODUCT_EMPTY);

        assertFalse(getUsersResponse.isEmpty());
        assertTrue(getUsersResponse.getContent().get(0).getRoles().contains(ROLE));
    }

    @Test
    public void getUsersGivenPageAndSortBySsoNoneReturnListOfUsers() throws TechpubsException {
        userRoleEntityList = new ArrayList<>();
        userRoleEntityList.add(userRoleEntity);

        userRolesDtoList = new ArrayList<>();
        userRolesDtoList.add(userRolesDto);

        userRolesDtoPage = new PageImpl<>(userRolesDtoList);

        when(mockIUserRoleData.findAll()).thenReturn(userRoleEntityList);
        when(mockICwcadminUsrData.findDocAdminUsers(anySet(), any(Pageable.class))).thenReturn(userRolesDtoPage);

        Page<UserRolesDto> getUsersResponse = managementAppImpl.getUsers(PAGE, SORT_SSO_BY_NONE, SEARCH_EMPTY,PRODUCT_EMPTY);

        assertFalse(getUsersResponse.isEmpty());
        assertTrue(getUsersResponse.getContent().get(0).getRoles().contains(ROLE));
    }

    @Test
    public void getUsersGivenPageAndSortByAndSearchTermReturnListOfUsers() throws TechpubsException {
        userRoleEntityList = new ArrayList<>();
        userRoleEntityList.add(userRoleEntity);

        userRolesDtoList = new ArrayList<>();
        userRolesDtoList.add(userRolesDto);

        userRolesDtoPage = new PageImpl<>(userRolesDtoList);

        when(mockIUserRoleData.findAll()).thenReturn(userRoleEntityList);
        when(mockICwcadminUsrData.searchDocAdminUsers(anySet(), anyString(), anyString(), anyString(),
                any(Pageable.class))).thenReturn(userRolesDtoPage);

        Page<UserRolesDto> getUsersResponse = managementAppImpl.getUsers(PAGE, SORT_SSO_BY_DESC, SEARCH,PRODUCT_EMPTY);

        assertFalse(getUsersResponse.isEmpty());
        assertTrue(getUsersResponse.getContent().get(0).getRoles().contains(ROLE));
    }

    @Test(expected = TechpubsException.class)
    public void getUsersGivenPageAndInvalidSortByLengthAndSearchTermThrowsException() throws TechpubsException {
        managementAppImpl.getUsers(PAGE, INVALID_SORT_BY_LENGTH, SEARCH,PRODUCT_EMPTY);
    }

    @Test(expected = TechpubsException.class)
    public void getUsersGivenPageAndInvalidSortByColumnAndSearchTermThrowsException() throws TechpubsException {
        managementAppImpl.getUsers(PAGE, INVALID_SORT_BY_COLUMN, SEARCH,PRODUCT_EMPTY);
    }

    @Test
    public void getUserGivenSsoReturnUser() {
        when(mockICwcadminUsrData.findDocAdminUser(anyString())).thenReturn(userDto);
        UserDto user = managementAppImpl.getUser(USER_SSO_ID);
        assertEquals(SSO_ID, user.getSsoId());
    }

    @Test
    public void getRolesReturnListOfRoles() {
        when(iRoleDataMock.findAll()).thenReturn(new ArrayList<>(Arrays.asList(roleEntity)));

        List<RoleEntity> roleList = managementAppImpl.getRoles();

        assertFalse(roleList.isEmpty());
        assertEquals(ROLE, roleList.get(0).getName());
    }

    @Test
    public void searchPortalsUsersGivenPageAndSortBySsoDescReturnListOfUsers() throws TechpubsException {
        userDtoList = new ArrayList<>();
        userDtoList.add(userDto);
        userDtoPage = new PageImpl<>(userDtoList);
        when(mockICwcadminUsrData.findAllUsersWithDocAdminRole(any(Pageable.class))).thenReturn(userDtoPage);

        Page<UserDto> searchPortalsUsersResponse = managementAppImpl.searchPortalsUsers(PAGE, SORT_SSO_BY_DESC, SEARCH_EMPTY);

        assertFalse(searchPortalsUsersResponse.getContent().isEmpty());
        assertEquals(SSO_ID, searchPortalsUsersResponse.getContent().get(0).getSsoId());
    }

    @Test
    public void searchPortalsUsersGivenPageAndSortBySsoAscReturnListOfUsers() throws TechpubsException {
        userDtoList = new ArrayList<>();
        userDtoList.add(userDto);
        userDtoPage = new PageImpl<>(userDtoList);
        when(mockICwcadminUsrData.findAllUsersWithDocAdminRole(any(Pageable.class))).thenReturn(userDtoPage);

        Page<UserDto> searchPortalsUsersResponse = managementAppImpl.searchPortalsUsers(PAGE, SORT_SSO_BY_ASC, SEARCH_EMPTY);

        assertFalse(searchPortalsUsersResponse.getContent().isEmpty());
        assertEquals(SSO_ID, searchPortalsUsersResponse.getContent().get(0).getSsoId());
    }

    @Test
    public void searchPortalsUsersGivenPageAndSortBySsoNoneReturnListOfUsers() throws TechpubsException {
        userDtoList = new ArrayList<>();
        userDtoList.add(userDto);
        userDtoPage = new PageImpl<>(userDtoList);
        when(mockICwcadminUsrData.findAllUsersWithDocAdminRole(any(Pageable.class))).thenReturn(userDtoPage);

        Page<UserDto> searchPortalsUsersResponse = managementAppImpl.searchPortalsUsers(PAGE, SORT_SSO_BY_NONE, SEARCH_EMPTY);

        assertFalse(searchPortalsUsersResponse.getContent().isEmpty());
        assertEquals(SSO_ID, searchPortalsUsersResponse.getContent().get(0).getSsoId());
    }

    @Test
    public void searchPortalsUsersGivenPageAndSortByAndSearchTermReturnListOfUsers() throws TechpubsException {
        userDtoList = new ArrayList<>();
        userDtoList.add(userDto);
        userDtoPage = new PageImpl<>(userDtoList);
        when(mockICwcadminUsrData.searchUsersWithDocAdminRole(anyString(), anyString(), anyString(),
                any(Pageable.class))).thenReturn(userDtoPage);

        Page<UserDto> searchPortalsUsersResponse = managementAppImpl.searchPortalsUsers(PAGE, SORT_SSO_BY_DESC, SEARCH);

        assertFalse(searchPortalsUsersResponse.getContent().isEmpty());
        assertEquals(SSO_ID, searchPortalsUsersResponse.getContent().get(0).getSsoId());
    }

    @Test(expected = TechpubsException.class)
    public void searchPortalsUsersGivenPageAndInvalidSortByLengthAndSearchTermThrowsException() throws TechpubsException {
        managementAppImpl.searchPortalsUsers(PAGE, INVALID_SORT_BY_LENGTH, SEARCH);
    }

    @Test(expected = TechpubsException.class)
    public void searchPortalsUsersGivenPageAndInvalidSortByColumnAndSearchTermThrowsException() throws TechpubsException {
        managementAppImpl.searchPortalsUsers(PAGE, INVALID_SORT_BY_COLUMN, SEARCH);
    }

    @Test
    public void getMdmEngineModelsGivenSsoReturnMdmEngineModelResponse() throws TechpubsException {
        when(adminAppUtil.getCompanyEngineFamilyModels(anyString(), anyString())).thenReturn(new StringBuilder());

        Map<String, List<String>> engineFamilyMap = new HashMap<>();
        engineFamilyMap.put(ENGINE_FAMILY, engineModels);
        when(adminAppUtil.parseMdmCompanyEngineModelResponse(any())).thenReturn(engineFamilyMap);

        ImmutableMap<Object, Object> mdmEngineModelResponse = managementAppImpl.getMdmEngineModels(SSO_ID);

        assertFalse(mdmEngineModelResponse.isEmpty());
        assertTrue(mdmEngineModelResponse.size() == 2);

    }

    @Test
    public void addUserRolesGivenSsoAndAddUserRoleAttributesDtoVerifySave4Times() throws TechpubsException {
        when(mockIUserData.existsById(SSO_ID)).thenReturn(true);
        when(mockIUserData.existsById(SSO_ID2)).thenReturn(false);

        userRoleAttributesDto = new UserRoleAttributesDto(ROLE, null);
        userRoleAttributesDtoList = new ArrayList<>();
        userRoleAttributesDtoList.add(userRoleAttributesDto);

        when(mockIUserRoleData.findUserRoleAttributesBySso(anyString())).thenReturn(userRoleAttributesDtoList);
        when(mockIUserRoleData.findUserRoleEntityBySsoAndRole(anyString(), anyString())).thenReturn(userRoleEntity);
        when(mockIUserRoleData.save(any(UserRoleEntity.class))).thenReturn(userRoleEntity);

        managementAppImpl.addUserRoles(SSO_ID, addUserRoleAttributesDto,PRODUCT_EMPTY);

        verify(mockIUserRoleData, times(4)).save(any(UserRoleEntity.class));
    }

    @Test
    public void updateUserRoleGivenSsoAndUserSsoAndRoleAndUpdateUserRoleAttributesDtoVerifySave() throws TechpubsException {
        when(mockIUserRoleData.findUserRoleEntityBySsoAndRole(anyString(), anyString())).thenReturn(userRoleEntity);
        when(mockIUserRoleData.save(any(UserRoleEntity.class))).thenReturn(userRoleEntity);

        managementAppImpl.updateUserRole(SSO_ID, USER_SSO_ID, ROLE, updateUserRoleAttributesDto,PRODUCT_EMPTY);

        verify(mockIUserRoleData).save(any(UserRoleEntity.class));
    }

    @Test
    public void deleteUserRoleGivenSsoAndUserSsoAndRoleVerifyUserRoleIsDeleted() throws TechpubsException {
        doNothing().when(mockIUserRoleData).deleteBySsoAndRole(anyString(), anyString());

        managementAppImpl.deleteUserRole(SSO_ID, USER_SSO_ID, ROLE, PRODUCT_EMPTY);

        verify(mockIUserRoleData, times(1)).deleteBySsoAndRole(anyString(), anyString());
    }

    @Test
    public void downloadUsersReturnUserSummaryExcelFile() throws ExcelException {
        userRoleEntityList = new ArrayList<>();
        userRoleEntityList.add(userRoleEntity);
        when(mockIUserRoleData.findAll()).thenReturn(userRoleEntityList);

        userRolesDtoList = new ArrayList<>();
        userRolesDtoList.add(userRolesDto);
        when(mockICwcadminUsrData.findDocAdminUsers(anySet())).thenReturn(userRolesDtoList);

        FileWithBytes fileWithBytes = managementAppImpl.downloadUsers(PRODUCT_EMPTY);

        assertNotNull(fileWithBytes.getContents());
        assertEquals("users_summary.xlsx", fileWithBytes.getFileName());
    }

    @Test
    public void downloadUserPermissionsReturnUserPermissionsExcelFile() throws ExcelException {
        userRoleAttributesDtoList = new ArrayList<>();
        userRoleAttributesDtoList.add(userRoleAttributesDto);
        when(mockIUserRoleData.findUserRoleAttributesBySso(anyString())).thenReturn(userRoleAttributesDtoList);

        FileWithBytes fileWithBytes = managementAppImpl.downloadUserPermissions(USER_SSO_ID,PRODUCT_EMPTY);

        assertNotNull(fileWithBytes.getContents());
        assertEquals("permissions.xlsx", fileWithBytes.getFileName());
    }

    @Test
    public void getEntitledEngineModelsValidRequest() throws TechpubsException {
        UserRoleAttributesDto userRoleAttributesDto = new UserRoleAttributesDto();
        userRoleAttributesDto.setRole("provisioner");
        userRoleAttributesDto.setAttributes(userRoleAttributes);
        List<UserRoleAttributesDto> userRoleAttributesMapList = new ArrayList<>();
        userRoleAttributesMapList.add(userRoleAttributesDto);
        when(iPermissionDataMock.findUserRoleAttributesBySSOAndResourceAndProduct(isA(String.class),
                isA(String.class), isA(String.class))).thenReturn(userRoleAttributesMapList);
        when(iEngineModelDataMock.findByModelIn(isA(List.class))).thenReturn(engineModelEntityList);
        ImmutableMap<Object, Object> response = managementAppImpl.getEntitledEngineModels(SSO_ID, PRODUCT, RESOURCE);
        assertEquals("{family2=[model2], family1=[model1]}", String.valueOf(response));
    }

    @Test
    public void getEntitledEngineModelsSuperuserRole() throws TechpubsException {
        UserRoleAttributesDto userRoleAttributesDto = new UserRoleAttributesDto();
        userRoleAttributesDto.setRole("superuser");
        userRoleAttributesDto.setAttributes(userRoleAttributes);
        List<UserRoleAttributesDto> userRoleAttributesMapList = new ArrayList<>();
        userRoleAttributesMapList.add(userRoleAttributesDto);
        when(iPermissionDataMock.findUserRoleAttributesBySSOAndResourceAndProduct(isA(String.class),
                isA(String.class), isA(String.class))).thenReturn(userRoleAttributesMapList);
        ImmutableMap<Object, Object> response = managementAppImpl.getEntitledEngineModels(SSO_ID, PRODUCT, RESOURCE);
        assertEquals("{SPM=[SPM, Honda-SPM]}", String.valueOf(response));
    }
}
