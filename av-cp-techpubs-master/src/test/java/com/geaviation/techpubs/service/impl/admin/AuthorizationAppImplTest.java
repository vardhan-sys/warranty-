package com.geaviation.techpubs.service.impl.admin;

import com.geaviation.techpubs.data.api.techlib.IPermissionData;
import com.geaviation.techpubs.data.api.techlib.IUserRoleData;
import com.geaviation.techpubs.models.techlib.dto.UserPermissionsDto;
import com.geaviation.techpubs.models.techlib.dto.UserRolePolicyAttributesDto;
import com.geaviation.techpubs.services.impl.admin.AuthorizationAppImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class AuthorizationAppImplTest {

    @Mock
    private IPermissionData mockIPermissionData;

    @Mock
    private IUserRoleData mockIUserRoleData;

    @InjectMocks
    private AuthorizationAppImpl authorizationApp;

    private static final String SSO_ID = "SSO_ID";
    private static final String RESOURCE_WITH_ACCESS = "RESOURCE_WITH_ACCESS";
    private static final String RESOURCE_WITHOUT_ACCESS = "RESOURCE_WITHOUT_ACCESS";
    private static final String RESOURCE = "RESOURCE";
    private static final String RESOURCE_EMPTY = "";
    private static final String ACTION = "ACTION";
    private static final String ROLE = "ROLE";
    private static final String TYPE = "TYPE";
    private static final String PRODUCT_EMPTY = "";

    private List<UserRolePolicyAttributesDto> rolePolicyAttributesDtoList;
    private UserRolePolicyAttributesDto rolePolicyAttributesDto;
    private List<UserPermissionsDto> userPermissionsDtoList;
    private UserPermissionsDto userPermissionsDto;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        rolePolicyAttributesDto = new UserRolePolicyAttributesDto(ROLE, new Object(), new Object());
        userPermissionsDto = new UserPermissionsDto(RESOURCE, ACTION, TYPE);
    }

    @Test
    public void getUserPermissionsGivenSsoAndEmptyResourceReturnsPermissions() {
        userPermissionsDtoList = new ArrayList<>();
        userPermissionsDtoList.add(userPermissionsDto);
        when(mockIPermissionData.findUserPermissions(SSO_ID)).thenReturn(userPermissionsDtoList);
        authorizationApp.getUserPermissions(SSO_ID, RESOURCE_EMPTY);
        assertFalse(userPermissionsDtoList.isEmpty());
    }

    @Test
    public void getUserPermissionsGivenSsoAndNotEmptyResourceReturnsPermissions() {
        userPermissionsDtoList = new ArrayList<>();
        userPermissionsDtoList.add(userPermissionsDto);
        when(mockIPermissionData.findUserPermissionsByResource(SSO_ID, RESOURCE)).thenReturn(userPermissionsDtoList);
        authorizationApp.getUserPermissions(SSO_ID, RESOURCE);
        assertFalse(userPermissionsDtoList.isEmpty());
    }

    @Test
    public void getUserPermissionsGivenSsoAndEmptyResourceReturnsNoPermissions() {
        userPermissionsDtoList = new ArrayList<>();
        when(mockIPermissionData.findUserPermissions(SSO_ID)).thenReturn(userPermissionsDtoList);
        authorizationApp.getUserPermissions(SSO_ID, RESOURCE_EMPTY);
        assertTrue(userPermissionsDtoList.isEmpty());
    }

    @Test
    public void getUserPermissionsGivenSsoAndNotEmptyResourceReturnsNoPermissions() {
        userPermissionsDtoList = new ArrayList<>();
        when(mockIPermissionData.findUserPermissionsByResource(SSO_ID, RESOURCE)).thenReturn(userPermissionsDtoList);
        authorizationApp.getUserPermissions(SSO_ID, RESOURCE);
        assertTrue(userPermissionsDtoList.isEmpty());
    }

    @Test
    public void getUserRolePolicyAndAttributesGivenSsoReturnsRolePolicyAttributesList() {
        rolePolicyAttributesDtoList = new ArrayList<>();
        rolePolicyAttributesDtoList.add(rolePolicyAttributesDto);
        when(mockIUserRoleData.findUserRolesWithPolicyAndAttributesBySso(SSO_ID)).thenReturn(rolePolicyAttributesDtoList);
        List<UserRolePolicyAttributesDto> userRolePolicyAndAttributes = authorizationApp.getUserRolePolicyAndAttributes(SSO_ID,PRODUCT_EMPTY);
        assertFalse(userRolePolicyAndAttributes.isEmpty());
    }

    @Test
    public void getUserRolePolicyAndAttributesGivenSsoReturnsEmptyRolePolicyAttributesList() {
        rolePolicyAttributesDtoList = new ArrayList<>();
        when(mockIUserRoleData.findUserRolesWithPolicyAndAttributesBySso(SSO_ID)).thenReturn(rolePolicyAttributesDtoList);
        List<UserRolePolicyAttributesDto> userRolePolicyAndAttributes = authorizationApp.getUserRolePolicyAndAttributes(SSO_ID,PRODUCT_EMPTY);
        assertTrue(userRolePolicyAndAttributes.isEmpty());
    }

    @Test
    public void checkIfUserHasPermissionGivenSsoAndResourceAndActionReturnsTrue() {
        when(mockIPermissionData.userPermissionExists(SSO_ID, RESOURCE_WITH_ACCESS, ACTION)).thenReturn(true);
        boolean hasPermission = authorizationApp.checkIfUserHasPermission(SSO_ID, RESOURCE_WITH_ACCESS, ACTION);
        assertTrue(hasPermission);
    }

    @Test
    public void checkIfUserHasPermissionGivenSsoAndResourceAndActionReturnsFalse() {
        when(mockIPermissionData.userPermissionExists(SSO_ID, RESOURCE_WITHOUT_ACCESS, ACTION)).thenReturn(false);
        boolean hasPermission = authorizationApp.checkIfUserHasPermission(SSO_ID, RESOURCE_WITHOUT_ACCESS, ACTION);
        assertFalse(hasPermission);
    }

}