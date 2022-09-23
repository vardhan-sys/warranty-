package com.geaviation.techpubs.services.api.admin;

import com.geaviation.techpubs.models.techlib.dto.UserPermissionsDto;
import com.geaviation.techpubs.models.techlib.dto.UserRolePolicyAttributesDto;

import java.util.List;

public interface IAuthorizationApp {

    List<UserPermissionsDto> getUserPermissions(String ssoId, String resource);
    List<UserRolePolicyAttributesDto> getUserRolePolicyAndAttributes(String ssoId, String product);
    boolean checkIfUserHasPermission(String ssoId, String resource, String action);
    boolean checkIfUserHasPermission(String ssoId, String resource, String action, String product);
}
