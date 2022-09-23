package com.geaviation.techpubs.services.api.admin;

import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.cwcadmin.dto.UserDto;
import com.geaviation.techpubs.models.techlib.RoleEntity;
import com.geaviation.techpubs.models.techlib.dto.AddUserRoleAttributesDto;
import com.geaviation.techpubs.models.techlib.dto.ProductRolesDto;
import com.geaviation.techpubs.models.techlib.dto.UpdateUserRoleAttributesDto;
import com.geaviation.techpubs.models.techlib.dto.UserRolesDto;
import com.geaviation.techpubs.services.excel.exception.ExcelException;
import com.geaviation.techpubs.services.excel.model.FileWithBytes;
import com.google.common.collect.ImmutableMap;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IManagementApp {

    Page<UserRolesDto> getUsers(int page, String sortBy, String searchTerm, String product) throws TechpubsException;
    UserDto getUser(String userSsoId);
    List<RoleEntity> getRoles();
    Page<UserDto> searchPortalsUsers(int page, String sortBy, String searchTerm) throws TechpubsException;
    FileWithBytes downloadUsers(String product) throws ExcelException;
    FileWithBytes downloadUserPermissions(String userSsoId, String product) throws ExcelException;
    ImmutableMap<Object, Object> getMdmEngineModels(String ssoId) throws TechpubsException;
    void addUserRoles(String ssoId, AddUserRoleAttributesDto addUserRoleAttributesDto, String product) throws TechpubsException;
    void updateUserRole(String ssoId, String userSsoId, String role, UpdateUserRoleAttributesDto updateUserRoleAttributesDto, String product) throws TechpubsException;
    void deleteUserRole(String ssoId, String userSsoId, String role, String product) throws TechpubsException;
    List<ProductRolesDto> getAllAvailableRolesForProduct(String product);
    ImmutableMap<Object, Object> getEntitledEngineModels(String ssoId, String product, String resource) throws TechpubsException;
}
