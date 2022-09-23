package com.geaviation.techpubs.controllers.impl.admin;

import static com.geaviation.techpubs.services.util.AppConstants.SM_SSOID;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.geaviation.techpubs.controllers.util.Constants;
import com.geaviation.techpubs.data.util.SecurityEscape;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.cwcadmin.dto.UserDto;
import com.geaviation.techpubs.models.techlib.dto.AddUserRoleAttributesDto;
import com.geaviation.techpubs.models.techlib.dto.UpdateUserRoleAttributesDto;
import com.geaviation.techpubs.models.techlib.dto.UserRolesDto;
import com.geaviation.techpubs.models.techlib.response.ProductRoleListResponse;
import com.geaviation.techpubs.models.techlib.response.RoleListResponse;
import com.geaviation.techpubs.models.techlib.response.RolePolicyAttributeListResponse;
import com.geaviation.techpubs.services.api.admin.IAuthorizationApp;
import com.geaviation.techpubs.services.api.admin.IManagementApp;
import com.geaviation.techpubs.services.excel.exception.ExcelException;
import com.geaviation.techpubs.services.excel.model.FileWithBytes;
import com.geaviation.techpubs.services.impl.admin.AuthServiceImpl;
import com.geaviation.techpubs.services.util.StringUtils;
import com.google.common.collect.ImmutableMap;

import io.swagger.annotations.ApiParam;

@RestController
@RefreshScope
@RequestMapping("/admin/admin-management")
public class AdminManagementController {

	private static final String RESOURCE_ADMIN_MANAGEMENT = "admin-management";

	private static final String PRODUCT_ENGINEMANUALS = "enginemanuals";

	private static final Logger log = LogManager.getLogger(AdminManagementController.class);

    @Value("${techpubs.services.sqlInjection}")
    private boolean sqlInjection;
    
    @Value("${techpubs.services.reachMvp2}")
    private boolean reachMvp2Active;

    @Autowired
    private IAuthorizationApp iAuthorizationApp;

    @Autowired
    private IManagementApp iManagementApp;

    @Autowired
    AuthServiceImpl authServiceImpl;

    /**
     * Gets the list of users on the admin-management screen. SSO must have permission
     * to see list of users.
     *
     * @param page       the page number in results (defaults to 0)
     * @param sortBy     sort by column (i.e. firstName, lastName, sso)
     * @param searchTerm the term to search for
     * @return The list of users
     */
    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<UserRolesDto>> getUsers(
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "page", value = "eg. 0") @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @ApiParam(name = "sortBy", value = "eg. sso|desc") @RequestParam(value = "sortBy", defaultValue = "sso|asc", required = false) String sortBy,
            @ApiParam(name = "searchTerm", value = "eg. 212") @RequestParam(value = "searchTerm", required = false) String searchTerm,
            @ApiParam(name = "product", value = "eg. aero") @RequestParam(value = "product") String product,
            HttpServletRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            sortBy = SecurityEscape.cleanString(sortBy);
            searchTerm = SecurityEscape.cleanString(searchTerm);
        }

        try {
            authServiceImpl.checkResourceAccessForProduct(ssoId, RESOURCE_ADMIN_MANAGEMENT, request, product);

        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(iManagementApp.getUsers(page, sortBy, searchTerm,product));
    }

    /**
     * Adds users with their roles and attributes
     *
     * @param addUserRoleAttributesDto the body of the request
     * @return A 200 response if successful.
     */
    @PostMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addUsers(
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "product", value = "eg. avsystems") @RequestParam(value = "product") String product,
            @RequestBody AddUserRoleAttributesDto addUserRoleAttributesDto,
            HttpServletRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
        }

        try {
            authServiceImpl.checkResourceAccessForProduct(ssoId, RESOURCE_ADMIN_MANAGEMENT, request, product);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        iManagementApp.addUserRoles(ssoId, addUserRoleAttributesDto,product);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Downloads a lists of users in Doc-Admin
     *
     * @return Excel file of the list of users
     */
    @GetMapping(value = "/users/download", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<byte[]> downloadUsers(
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "product", value = "eg. avsystems") @RequestParam(value = "product") String product,
            HttpServletRequest request) throws ExcelException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
        }

		try {
            authServiceImpl.checkResourceAccessForProduct(ssoId, RESOURCE_ADMIN_MANAGEMENT, request, product);
		} catch (TechpubsException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

        ResponseEntity<byte[]> response;

        try {
            FileWithBytes fileWithBytes = iManagementApp.downloadUsers(product);
            byte[] content = fileWithBytes.getContents();
            String fileName = fileWithBytes.getFileName().split("\\.", 2)[0];
            String fileType = fileWithBytes.getFileName().split("\\.", 2)[1];
            String file = fileName + "_" +
                    new SimpleDateFormat("yyyy_MM_dd_HH.mm.ss").format(new Date())
                    + "." + fileType;

            response = ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, Constants.ATTACHMENT_FILENAME + file + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(content);
        } catch (ExcelException e) {
            log.error("There is an error downloading user data. No data found!");
            response = ResponseEntity.ok(Constants.NO_DATA_TO_DOWNLOAD.getBytes());
        }
        return response;
    }

    /**
     * Gets the list of users on the admin-management screen. SSO must have permission
     * to see list of users.
     *
     * @return The list of users
     */
    @GetMapping(value = "/users/{userSsoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> getUser(
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "userSsoId", value = "eg. 123456789") @PathVariable("userSsoId") String userSsoId,
            HttpServletRequest request) {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            userSsoId = SecurityEscape.cleanString(userSsoId);
        }

        try {
            // TODO: use checkResourceAccessForProduct here?
            authServiceImpl.checkResourceAccess(ssoId, RESOURCE_ADMIN_MANAGEMENT, request);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(iManagementApp.getUser(userSsoId));
    }

    /**
     * Downloads a lists of users in Doc-Admin
     *
     * @return Excel file of the list of users
     */
    @GetMapping(value = "/users/{userSsoId}/download", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> downloadUserPermissions(
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "userSsoId", value = "eg. 123456789") @PathVariable("userSsoId") String userSsoId,
            @ApiParam(name = "product", value = "eg. avsystems") @RequestParam(value = "product") String product,
            HttpServletRequest request) throws ExcelException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            userSsoId = SecurityEscape.cleanString(userSsoId);
        }

        try {
            authServiceImpl.checkResourceAccessForProduct(ssoId, RESOURCE_ADMIN_MANAGEMENT, request, product);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ResponseEntity<byte[]> response;

        try {
            FileWithBytes fileWithBytes = iManagementApp.downloadUserPermissions(userSsoId,product);
            byte[] content = fileWithBytes.getContents();
            String fileName = fileWithBytes.getFileName().split("\\.", 2)[0];
            String fileType = fileWithBytes.getFileName().split("\\.", 2)[1];
            String file = fileName + "_" + userSsoId + "_" +
                    new SimpleDateFormat("yyyy_MM_dd_HH.mm.ss").format(new Date())
                    + "." + fileType;

            response = ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, Constants.ATTACHMENT_FILENAME + file + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(content);
        } catch (ExcelException e) {
            log.error("There is an error downloading user permission data. No data found!");
            response = ResponseEntity.ok(Constants.NO_DATA_TO_DOWNLOAD.getBytes());
        }
        return response;
    }

    /**
     * Gets the list of user roles by SSO with its policy and attributes.
     *
     * @param userSsoId the sso of the user in question
     * @return The list of user roles.
     */
    @GetMapping(value = "/users/{userSsoId}/roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RolePolicyAttributeListResponse> getUserRoles(
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "userSsoId", value = "eg. 123456789") @PathVariable("userSsoId") String userSsoId,
            @ApiParam(name = "product", value = "eg. avsystems") @RequestParam(value = "product") String product,
            HttpServletRequest request) {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
        }

        try {
            authServiceImpl.checkResourceAccessForProduct(ssoId, RESOURCE_ADMIN_MANAGEMENT, request, product);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(new RolePolicyAttributeListResponse(iAuthorizationApp.getUserRolePolicyAndAttributes(userSsoId,product)));
    }

    /**
     * Updates a single user's role and attributes
     *
     * @param userSsoId                   the sso of the user to update
     * @param role                        the role to update for the specified user in question
     * @param updateUserRoleAttributesDto the body of the request
     * @return A 200 response if successful.
     */
    @PutMapping(value = "/users/{userSsoId}/roles/{role}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateUserRole(
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "userSsoId", value = "eg. 123456789") @PathVariable("userSsoId") String userSsoId,
            @ApiParam(name = "role", value = "eg. provisioner") @PathVariable("role") String role,
            @RequestBody UpdateUserRoleAttributesDto updateUserRoleAttributesDto,
            @ApiParam(name = "product", value = "eg. avsystems") @RequestParam(value = "product") String product,
            HttpServletRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            userSsoId = SecurityEscape.cleanString(userSsoId);
            role = SecurityEscape.cleanString(role);
        }

        try {
            authServiceImpl.checkResourceAccessForProduct(ssoId, RESOURCE_ADMIN_MANAGEMENT, request, product);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        iManagementApp.updateUserRole(ssoId, userSsoId, role, updateUserRoleAttributesDto, product);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Deletes a single user's role and attributes
     *
     * @param userSsoId The sso of the user whose role to delete
     * @param role      The role to delete
     * @return A 200 response if successful.
     */
    @DeleteMapping(value = "/users/{userSsoId}/roles/{role}")
    public ResponseEntity<Void> deleteUserRole(
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "userSsoId", value = "eg. 123456789") @PathVariable("userSsoId") String userSsoId,
            @ApiParam(name = "role", value = "eg. provisioner") @PathVariable("role") String role,
            @ApiParam(name = "product", value = "eg. avsystems") @RequestParam(value = "product") String product,
            HttpServletRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            userSsoId = SecurityEscape.cleanString(userSsoId);
            role = SecurityEscape.cleanString(role);
        }

        try {
            authServiceImpl.checkResourceAccessForProduct(ssoId, RESOURCE_ADMIN_MANAGEMENT, request, product);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        iManagementApp.deleteUserRole(ssoId, userSsoId, role, product);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Gets a list of paginated users in Portal Admin with the doc admin role.
     *
     * @param page       the page number in results (defaults to 0)
     * @param sortBy     sort by column (i.e. firstName, lastName, sso)
     * @param searchTerm the term to search for
     * @return The page of users
     */
    @GetMapping(value = "/portal-users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<UserDto>> getAdminUsers(
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "page", value = "eg. 0") @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @ApiParam(name = "sortBy", value = "eg. sso|desc") @RequestParam(value = "sortBy", defaultValue = "sso|asc", required = false) String sortBy,
            @ApiParam(name = "searchTerm", value = "eg. 212") @RequestParam(value = "searchTerm", required = false) String searchTerm,
            @ApiParam(name = "product", value = "eg. avsystems") @RequestParam(value = "product", required = false) String product,
            HttpServletRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            sortBy = SecurityEscape.cleanString(sortBy);
            searchTerm = SecurityEscape.cleanString(searchTerm);
        }

		try {
            if (StringUtils.isEmpty(product)) {
                authServiceImpl.checkResourceAccess(ssoId, RESOURCE_ADMIN_MANAGEMENT, request);
            } else {
                authServiceImpl.checkResourceAccessForProduct(ssoId, RESOURCE_ADMIN_MANAGEMENT, request, product);
            }
		} catch (TechpubsException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

        return ResponseEntity.ok(iManagementApp.searchPortalsUsers(page, sortBy, searchTerm));
    }

    /**
     * Gets a list of doc admin roles
     *
     * @return The list of roles in doc admin
     */
    @GetMapping(value = "/roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RoleListResponse> getRoles(
            @RequestHeader(SM_SSOID) String ssoId,
            HttpServletRequest request) {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
        }

        try {
            authServiceImpl.checkResourceAccess(ssoId, RESOURCE_ADMIN_MANAGEMENT, request);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }


        return ResponseEntity.ok(new RoleListResponse(iManagementApp.getRoles()));
    }

    /**
     * Gets a list of engine models in MDM
     *
     * @return The list of engine models sorted by engine family
     */
    @GetMapping(value = "/mdm-engine-models", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ImmutableMap<Object, Object>> getMdmEngineModels(
            @RequestHeader(SM_SSOID) String ssoId,
            HttpServletRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
        }

        try {
            authServiceImpl.checkResourceAccessForProduct(ssoId, RESOURCE_ADMIN_MANAGEMENT, request, PRODUCT_ENGINEMANUALS);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(iManagementApp.getMdmEngineModels(ssoId));
    }
    
    /**
     * Gets a list of engine models in MDM
     *
     * @return The list of engine models sorted by engine family
     */
    @GetMapping(value = "/entitled-engine-models", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ImmutableMap<Object, Object>> getEntitledEngineModels(
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "resource", value = "eg. uploader, publisher") @RequestParam(value = "resource") String resource,
            HttpServletRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
        }

        if (!reachMvp2Active) {
        	return ResponseEntity.notFound().build();
        }
        
        try {
            authServiceImpl.checkResourceAccessForProduct(ssoId, resource, request, PRODUCT_ENGINEMANUALS);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(iManagementApp.getEntitledEngineModels(ssoId, PRODUCT_ENGINEMANUALS, resource));
    }

    /**
     * Gets the list of available roles for passed product
     *
     * @return The list of roles with label
     */
    @GetMapping(value = "/product/roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductRoleListResponse> getAllRolesByProducts( @RequestHeader(SM_SSOID) String ssoId,
    		HttpServletRequest request,@ApiParam(name = "product", value = "eg. avsystems") @RequestParam(value = "product") String product) {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
        }
        
        try {
            authServiceImpl.checkResourceAccessForProduct(ssoId, RESOURCE_ADMIN_MANAGEMENT, request, product);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(new ProductRoleListResponse(iManagementApp.getAllAvailableRolesForProduct(product)));
    }
}
