package com.geaviation.techpubs.services.impl.admin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.geaviation.techpubs.data.api.techlib.IEngineModelProgramData;
import com.geaviation.techpubs.data.api.techlib.IPermissionData;
import com.geaviation.techpubs.data.api.techlib.IUserRoleData;
import com.geaviation.techpubs.models.techlib.UserRoleAttributes;
import com.geaviation.techpubs.models.techlib.dto.UserPermissionsDto;
import com.geaviation.techpubs.models.techlib.dto.UserRolePolicyAttributesDto;
import com.geaviation.techpubs.services.api.admin.IAuthorizationApp;
import com.geaviation.techpubs.services.util.AppConstants;
import com.geaviation.techpubs.services.util.StringUtils;

@Service("AuthorizationApp")
@RefreshScope
public class AuthorizationAppImpl implements IAuthorizationApp {

    @Autowired
    private IPermissionData iPermissionData;

    @Autowired
    private IUserRoleData iUserRoleData;

    @Autowired
    private IEngineModelProgramData iEngineModelProgramData;

	@Override
	public List<UserPermissionsDto> getUserPermissions(String ssoId, String resource) {
        if (StringUtils.isEmpty(resource)) {
            return iPermissionData.findUserPermissionsNew(ssoId);
        } else {
            return iPermissionData.findUserPermissionsByResourceNew(ssoId, resource);
        }
	}

	@Override
	public List<UserRolePolicyAttributesDto> getUserRolePolicyAndAttributes(String ssoId, String product) {
        if (!ObjectUtils.isEmpty(product)) {
            return iUserRoleData.findUserRolesWithPolicyAndAttributesBySsoAndProduct(ssoId, product);
        } else
            return iUserRoleData.findUserRolesWithPolicyAndAttributesBySso(ssoId);
	}

    @Override
    public boolean checkIfUserHasPermission(String ssoId, String resource, String action) {
        return iPermissionData.userPermissionExists(ssoId, resource, action);
    }
    
    @Override
    public boolean checkIfUserHasPermission(String ssoId, String resource, String action, String product) {
        return iPermissionData.userPermissionExistsForProduct(ssoId, resource, action,product);
    }

    public boolean checkIfUserHasBookcaseAccess(String ssoId, String resource, String bookcaseKey) {
        List<UserRoleAttributes> userRoleAttributes = iPermissionData.findUserRoleAttributes(ssoId, resource, AppConstants.VIEW_ACTION);
        List<String> userEngineModels = userRoleAttributes.stream().map(UserRoleAttributes::getEngineModels).flatMap(List::stream).collect(Collectors.toList());

        boolean userAllAccess = userEngineModels.stream().anyMatch("all"::equalsIgnoreCase);

        boolean hasBookcaseAccess = false;

        if (!userAllAccess) {
            List<String> engineModels = iEngineModelProgramData.findEngineModelsByBookcaseKey(bookcaseKey);
            for (String engineModel : engineModels) {
                if (userEngineModels.contains(engineModel)) {
                    hasBookcaseAccess = true;
                    break;
                }
            }
        } else {
            hasBookcaseAccess = true;
        }

        return hasBookcaseAccess;
    }
}
