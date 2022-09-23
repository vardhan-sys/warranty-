package com.geaviation.techpubs.controllers.impl.admin;

import com.geaviation.techpubs.data.util.SecurityEscape;
import com.geaviation.techpubs.models.techlib.response.PermissionListResponse;
import com.geaviation.techpubs.models.techlib.response.RolePolicyAttributeListResponse;
import com.geaviation.techpubs.services.api.admin.IAuthorizationApp;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.geaviation.techpubs.services.util.AppConstants.SM_SSOID;

@RestController
@RefreshScope
@RequestMapping("/admin/authorization")
public class AuthorizationController {

    @Value("${techpubs.services.sqlInjection}")
    private boolean sqlInjection;

    @Autowired
    private IAuthorizationApp iAuthorizationApp;

    /**
     * Gets the list of user permissions by SSO (from header). Can also filter on specific resource.
     *
     * @param resource the resource to filter on
     * @return The list of user permissions
     */
    @GetMapping(value = "/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PermissionListResponse> getCurrentUserPermissions(
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "resource", value = "eg. companies") @RequestParam(value = "resource", required = false) String resource) {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            resource = SecurityEscape.cleanString(resource);
        }

        return ResponseEntity.ok(new PermissionListResponse(iAuthorizationApp.getUserPermissions(ssoId, resource)));
    }

    /**
     * Gets the list of user roles by SSO (from header) with its policy and attributes.
     *
     * @return The list of user roles.
     */
    @GetMapping(value = "/roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RolePolicyAttributeListResponse> getCurrentUserRoles(@RequestHeader(SM_SSOID) String ssoId,
    		@ApiParam(name = "product", value = "eg. avsystems") @RequestParam(value = "product", required = false) String product) {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
        }

        return ResponseEntity.ok(new RolePolicyAttributeListResponse(iAuthorizationApp.getUserRolePolicyAndAttributes(ssoId,product)));
    }

}
