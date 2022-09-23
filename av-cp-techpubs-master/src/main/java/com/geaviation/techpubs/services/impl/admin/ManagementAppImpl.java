package com.geaviation.techpubs.services.impl.admin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geaviation.techpubs.data.api.cwcadmin.ICwcadminUsrData;
import com.geaviation.techpubs.data.api.techlib.IEngineModelData;
import com.geaviation.techpubs.data.api.techlib.IPermissionData;
import com.geaviation.techpubs.data.api.techlib.IRoleData;
import com.geaviation.techpubs.data.api.techlib.IUserData;
import com.geaviation.techpubs.data.api.techlib.IUserRoleData;
import com.geaviation.techpubs.data.impl.AwsResourcesService;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.cwcadmin.dto.UserDto;
import com.geaviation.techpubs.models.techlib.EngineModelEntity;
import com.geaviation.techpubs.models.techlib.RoleEntity;
import com.geaviation.techpubs.models.techlib.UserEntity;
import com.geaviation.techpubs.models.techlib.UserRoleAttributes;
import com.geaviation.techpubs.models.techlib.UserRoleEntity;
import com.geaviation.techpubs.models.techlib.dto.AddUserRoleAttributesDto;
import com.geaviation.techpubs.models.techlib.dto.ProductRolesDto;
import com.geaviation.techpubs.models.techlib.dto.RolePermissionsDto;
import com.geaviation.techpubs.models.techlib.dto.UpdateUserRoleAttributesDto;
import com.geaviation.techpubs.models.techlib.dto.UserRoleAttributesDto;
import com.geaviation.techpubs.models.techlib.dto.UserRolesDto;
import com.geaviation.techpubs.services.api.admin.IManagementApp;
import com.geaviation.techpubs.services.excel.ExcelMaker;
import com.geaviation.techpubs.services.excel.exception.ExcelException;
import com.geaviation.techpubs.services.excel.model.ExcelSheet;
import com.geaviation.techpubs.services.excel.model.FileWithBytes;
import com.geaviation.techpubs.services.util.AppConstants;
import com.geaviation.techpubs.services.util.StringUtils;
import com.geaviation.techpubs.services.util.admin.AdminAppUtil;
import com.geaviation.techpubs.services.util.admin.EngineModelTableUpdater;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@Service
@RefreshScope
public class ManagementAppImpl implements IManagementApp {

    @Autowired
    private IUserData iUserData;

    @Autowired
    private ICwcadminUsrData iCwcadminUsrData;

    @Autowired
    private IRoleData iRoleData;

    @Autowired
    private IUserRoleData iUserRoleData;
    
    @Autowired
    private IPermissionData iPermissionData;

    @Autowired
    private AdminAppUtil adminAppUtil;
    
    @Autowired
    private IEngineModelData iEngineModelData;

    @Autowired
    private AwsResourcesService awsResourcesService;
    
    @Autowired
    private EngineModelTableUpdater engModelUpdater;
    
    @Value("${AUDIT.TRAIL.ENABLED}")
    private boolean auditTrailEnabled;
    
    @Value("${techpubs.services.reachMvp2}")
    private boolean reachMvp2Active;

    private final int pageSize = 50;

    private static final Logger log = LogManager.getLogger(ManagementAppImpl.class);

    @Override
    public Page<UserRolesDto> getUsers(int page, String sortBy, String searchTerm, String product) throws TechpubsException {
        String sortBySplit[] = sortBy.split("\\|");
        if (sortBySplit.length != 2) {
            throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }

        String column = sortBy.split("\\|")[0];
        if (!column.equals("sso") && !column.equals("firstName") && !column.equals("lastName")) {
            throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }

        String order = sortBy.split("\\|")[1];

        Pageable pageRequest;
        if (order != null && order.equalsIgnoreCase("asc")) {
            pageRequest = PageRequest.of(page, pageSize, Sort.by(column).ascending());
        } else if (order != null && order.equalsIgnoreCase("desc")) {
            pageRequest = PageRequest.of(page, pageSize, Sort.by(column).descending());
        } else {
            pageRequest = PageRequest.of(page, pageSize, Sort.by(column));
        }
        List<UserRoleEntity> userRoles = null;
        if(!ObjectUtils.isEmpty(product)) {
        	userRoles = iUserRoleData.findUsersByProduct(product);
        } else {
        	 userRoles = iUserRoleData.findAll();
        }
       
        Map<String, List<String>> userRoleMap = userRoles.stream().collect(
                Collectors.groupingBy(UserRoleEntity::getSso, Collectors.mapping(UserRoleEntity::getRole, Collectors.toList())));

        Page<UserRolesDto> users;
        if (!StringUtils.isEmpty(searchTerm)) {
            users = iCwcadminUsrData.searchDocAdminUsers(userRoleMap.keySet(), "%"+searchTerm+"%","%"+searchTerm+"%","%"+searchTerm+"%", pageRequest);
        } else {
            users = iCwcadminUsrData.findDocAdminUsers(userRoleMap.keySet(), pageRequest);
        }

        for (UserRolesDto user : users) {
            user.setRoles(userRoleMap.get(user.getSso()));
        }

        return users;
    }

    @Override
    public UserDto getUser(String userSsoId) { 
    
    	return iCwcadminUsrData.findDocAdminUser(userSsoId); 
    }

    @Override
    public List<RoleEntity> getRoles() {
        return iRoleData.findAll();
    }

    @Override
    public Page<UserDto> searchPortalsUsers(int page, String sortBy, String searchTerm) throws TechpubsException {

        String[] sortBySplit = sortBy.split("\\|");
        if (sortBySplit.length != 2) {
            throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }

        String column = sortBy.split("\\|")[0];
        if (!"sso".equals(column) && !"firstName".equals(column) && !"lastName".equals(column)) {
            throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }

        String order = sortBy.split("\\|")[1];

        Pageable pageRequest;
        if (order != null && "desc".equalsIgnoreCase(order)) {
            pageRequest = PageRequest.of(page, pageSize, Sort.by(column).ascending());
        } else if (order != null && "asc".equalsIgnoreCase(order)) {
            pageRequest = PageRequest.of(page, pageSize, Sort.by(column).descending());
        } else {
            pageRequest = PageRequest.of(page, pageSize, Sort.by(column));
        }

        if (!StringUtils.isEmpty(searchTerm)) {
            pageRequest = PageRequest.of(page, pageSize, Sort.by("sso").ascending());
            return iCwcadminUsrData.searchUsersWithDocAdminRole("%"+searchTerm+"%","%"+searchTerm+"%","%"+searchTerm+"%", pageRequest);
        } else {
            return iCwcadminUsrData.findAllUsersWithDocAdminRole(pageRequest);
        }
    }

    @Override
    public ImmutableMap<Object, Object> getMdmEngineModels(String ssoId) throws TechpubsException {
        // Grab all engine family -> models with GEA org
        StringBuilder response = adminAppUtil.getCompanyEngineFamilyModels(ssoId, AppConstants.GEA_ORG);
        Map<String, List<String>> engineFamilyMap = adminAppUtil.parseMdmCompanyEngineModelResponse(response);
        ImmutableMap<Object, Object> mdmEngineModelResponse;

        mdmEngineModelResponse = ImmutableMap.builder()
                    .putAll(engineFamilyMap)
                    .put("SPM", ImmutableList.of("SPM","Honda-SPM"))
                    .build();

        return mdmEngineModelResponse;
    }

    @Override
    public void addUserRoles(String ssoId, AddUserRoleAttributesDto addUserRoleAttributesDto, String product) throws TechpubsException {
        List<String> userSsoIds = addUserRoleAttributesDto.getSsoIds();
        List<String> roles = addUserRoleAttributesDto.getRoles();
        UserRoleAttributes attributes = addUserRoleAttributesDto.getAttributes();
        String category = AppConstants.ADMIN_MANAGEMENT_TAB;
        
        if(AppConstants.AV_SYSTEMS.equalsIgnoreCase(product)) {
    		category = AppConstants.AV_SYSTEMS_PREFIX + AppConstants.ADMIN_MANAGEMENT_TAB;
    	}
        
        //verify available roles for passed product
        if(!ObjectUtils.isEmpty(product)) {
        	List<ProductRolesDto> availableRolesForPassedProduct = iRoleData.findRolesByProduct(product);
        	List<String> availableRoles = availableRolesForPassedProduct.stream().map(role -> role.getRole()).collect(Collectors.toList());
        	boolean isRoleAvailable = roles.stream().allMatch(role -> availableRoles.contains(role));
        	if(!isRoleAvailable)
        		throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }

        try {
            for (String userSsoId : userSsoIds) {
                saveUserBySSOId(ssoId, roles, attributes, category, userSsoId);
            }
            
        } catch (Exception e) {
            log.error("Error adding new user roles: " + e);
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
        }
    }

	private void saveUserBySSOId(String ssoId, List<String> roles, UserRoleAttributes attributes, String category,
			String userSsoId) throws TechpubsException {
		if (iUserData.existsById(userSsoId)) {
		    
			if (reachMvp2Active && !attributes.getEngineModels().isEmpty() && !attributes.getEngineModels().contains("all")) {
				// Validate and add engine models to engine_model table
				engModelUpdater.validateEngineModels(attributes.getEngineModels(), userSsoId);
			}
			
			// Adds new attributes to existing roles
			addUserRolesAndAttributes(ssoId, userSsoId, roles, attributes);
		   
			if (auditTrailEnabled) {

		        awsResourcesService.writeAdminManagementAuditLog(
		                ssoId, category, userSsoId, roles, attributes.getEngineModels(),
		                attributes.getAirFrames(), attributes.getDocTypes(), AppConstants.ADD_PERMISSION_ACTION);
		    }
		} else {
		    saveNewUser(ssoId, userSsoId);
		    saveNewUserRolesAndAttributes(ssoId, userSsoId, roles, attributes);
		    if (auditTrailEnabled) {
		        awsResourcesService.writeAdminManagementAuditLog(
		                ssoId, category, userSsoId, roles, attributes.getEngineModels(),
		                attributes.getAirFrames(), attributes.getDocTypes(), AppConstants.ADD_USER_ACTION);
		    }
		}
	}

    private void saveNewUser(String ssoId, String userSsoId) {
        Timestamp ts = new Timestamp(new Date().getTime());
        iUserData.save(new UserEntity(userSsoId, ssoId, ts, ssoId, ts));
    }

    private void saveNewUserRolesAndAttributes(String ssoId, String userSsoId, List<String> roles, UserRoleAttributes attributes) throws TechpubsException {
        Timestamp ts = new Timestamp(new Date().getTime());
        try {
        	
        	for (String role : roles) {
                iUserRoleData.save(new UserRoleEntity(userSsoId, role, attributes, ssoId, ts, ssoId, ts));
            }
            
        } catch (Exception e) {
            log.error("Error saving new users: " + e);
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
        }
    }

    private void addUserRolesAndAttributes(String ssoId, String userSsoId, List<String> roles, UserRoleAttributes attributes) throws TechpubsException {
        // Get existing roles and update
        Timestamp ts = new Timestamp(new Date().getTime());
        List<UserRoleAttributesDto> userRoleAttributesList = iUserRoleData.findUserRoleAttributesBySso(userSsoId);
        List<String> existingRoles = userRoleAttributesList.stream().map(UserRoleAttributesDto::getRole).collect(Collectors.toList());
        for (String role : roles) {
            if (existingRoles.contains(role)) {
                // Update user role attributes
                UserRoleEntity userRole = iUserRoleData.findUserRoleEntityBySsoAndRole(userSsoId, role);
                UserRoleAttributes oldAttributes = userRole.getAttributes();
                UserRoleAttributes newAttributes = new UserRoleAttributes();

                Set<String> engineModels = new LinkedHashSet<>(oldAttributes.getEngineModels());
                engineModels.addAll(attributes.getEngineModels());
                newAttributes.setEngineModels(new ArrayList<>(engineModels));

                Set<String> airFrames = new LinkedHashSet<>(oldAttributes.getAirFrames());
                airFrames.addAll(attributes.getAirFrames());
                newAttributes.setAirFrames(new ArrayList<>(airFrames));

                Set<String> docTypes = new LinkedHashSet<>(oldAttributes.getDocTypes());
                docTypes.addAll(attributes.getDocTypes());
                newAttributes.setDocTypes(new ArrayList<>(docTypes));

                userRole.setAttributes(newAttributes);
                userRole.setLastUpdatedBy(ssoId);
                userRole.setLastUpdatedDate(ts);
                try {
                    iUserRoleData.save(userRole);
                } catch (Exception e) {
                    log.error("Error saving new roles to Users: " + e);
                    throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
                }
            } else {
                // Create new user role for existing user
                List<String> roleList = new ArrayList<>();
                roleList.add(role);
                saveNewUserRolesAndAttributes(ssoId, userSsoId, roleList, attributes);
            }
        }
    }

    @Override
    public void updateUserRole(String ssoId, String userSsoId, String role, UpdateUserRoleAttributesDto updateUserRoleAttributesDto, String product) throws TechpubsException {
        
    	Timestamp ts = new Timestamp(new Date().getTime());
        String newRole = updateUserRoleAttributesDto.getNewRole();
        UserRoleAttributes attributes = updateUserRoleAttributesDto.getAttributes();
        String category = AppConstants.ADMIN_MANAGEMENT_TAB;
        
        if(AppConstants.AV_SYSTEMS.equalsIgnoreCase(product)) {
    		category = AppConstants.AV_SYSTEMS_PREFIX + AppConstants.ADMIN_MANAGEMENT_TAB;
    	}
        
    	//verify new role is valid for passed product
        if(!ObjectUtils.isEmpty(product)) {
        	List<ProductRolesDto> availableRolesForPassedProduct = iRoleData.findRolesByProduct(product);
        	List<String> availableRoles = availableRolesForPassedProduct.stream().map(rl -> rl.getRole()).collect(Collectors.toList());
        	if(!availableRoles.contains(newRole))
        		throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }
    	
        UserRoleEntity userRole = iUserRoleData.findUserRoleEntityBySsoAndRole(userSsoId, role);

		try {
			
			if (reachMvp2Active && !attributes.getEngineModels().isEmpty()) {
				// Validate and add engine models to engine_model table
				engModelUpdater.validateEngineModels(attributes.getEngineModels(), userSsoId);
			}

			if (newRole != null && !newRole.isEmpty()) {
				userRole.setRole(newRole);
			}

			userRole.setAttributes(attributes);
			userRole.setLastUpdatedBy(ssoId);
			userRole.setLastUpdatedDate(ts);
			
			iUserRoleData.save(userRole);
			if (auditTrailEnabled) {
				awsResourcesService.writeAdminManagementAuditLog(ssoId, category, userSsoId,
						new ArrayList<>(Arrays.asList(role)), attributes.getEngineModels(), attributes.getAirFrames(),
						attributes.getDocTypes(), AppConstants.UPDATE_USER_ACTION);
			}
		} catch (Exception e) {
            log.error("Error updating user " + userSsoId + " role permissions: " + e);
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
        }
    }

    @Transactional
    @Override
    public void deleteUserRole(String ssoId, String userSsoId, String role, String product) throws TechpubsException {
    	
    	 String category = AppConstants.ADMIN_MANAGEMENT_TAB;
         
         if(AppConstants.AV_SYSTEMS.equalsIgnoreCase(product)) {
     		category = AppConstants.AV_SYSTEMS_PREFIX + AppConstants.ADMIN_MANAGEMENT_TAB;
     	}
    	
    	//verify role is valid for passed product
        if(!ObjectUtils.isEmpty(product)) {
        	List<ProductRolesDto> availableRolesForPassedProduct = iRoleData.findRolesByProduct(product);
        	List<String> availableRoles = availableRolesForPassedProduct.stream().map(rl -> rl.getRole()).collect(Collectors.toList());
        	if(!availableRoles.contains(role))
        		throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }
        try {
            iUserRoleData.deleteBySsoAndRole(userSsoId, role);
            if (auditTrailEnabled) {
                awsResourcesService.writeAdminManagementAuditLog(
                        ssoId, category, userSsoId, new ArrayList<>(Arrays.asList(role)),
                        null, null, null, AppConstants.DELETE_ROLE_ACTION);
            }
        } catch (Exception e) {
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
        }
    }

    @Override
    public FileWithBytes downloadUsers(String product) throws ExcelException {
        List<UserRolesDto> users = getDownloadUsers(product);

        ExcelSheet excelSheet = ExcelMaker.buildExcelSheet(users);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ExcelMaker.excelSheetToFile(excelSheet, os);
            return new FileWithBytes(os.toByteArray(), excelSheet.getFilename());
        } catch (IOException e) {
            throw new ExcelException("Could not write the excel file.", e);
        }
    }

    private List<UserRolesDto> getDownloadUsers(String product) {
    	List<UserRoleEntity> userRoles = null;
    	if(!ObjectUtils.isEmpty(product)) {
    		userRoles = iUserRoleData.findUsersByProduct(product);
    	} else {
    		userRoles = iUserRoleData.findAll();
    	}

        Map<String, List<String>> userRoleMap = userRoles.stream().collect(
                Collectors.groupingBy(UserRoleEntity::getSso, Collectors.mapping(UserRoleEntity::getRole, Collectors.toList())));

        List<UserRolesDto> users = iCwcadminUsrData.findDocAdminUsers(userRoleMap.keySet());

        for (UserRolesDto user : users) {
            user.setRoles(userRoleMap.get(user.getSso()));
        }

        return users;
    }

    @Override
    public FileWithBytes downloadUserPermissions(String userSsoId, String product) throws ExcelException {
        List<RolePermissionsDto> userPermissions = getDownloadUserPermissions(userSsoId, product);

        ExcelSheet excelSheet = ExcelMaker.buildExcelSheet(userPermissions);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ExcelMaker.excelSheetToFile(excelSheet, os);
            return new FileWithBytes(os.toByteArray(), excelSheet.getFilename());
        } catch (IOException e) {
            throw new ExcelException("Could not write the excel file.", e);
        }
    }

    private List<RolePermissionsDto> getDownloadUserPermissions(String userSsoId, String product) {
    	
    	List<UserRoleAttributesDto> userRoleAttributesList = null;
    	if(!ObjectUtils.isEmpty(product)) {
    		userRoleAttributesList = iUserRoleData.findUserRoleAttributesBySsoAndProduct(userSsoId, product);
    	} else {
    		userRoleAttributesList = iUserRoleData.findUserRoleAttributesBySso(userSsoId);
    	}

        List<RolePermissionsDto> downloadUserPermissionsList = new ArrayList<>();

        for (UserRoleAttributesDto userRoleAttributesDto : userRoleAttributesList) {
            RolePermissionsDto rolePermissionsDto = new RolePermissionsDto();
            rolePermissionsDto.setRole(userRoleAttributesDto.getRole());
            UserRoleAttributes attributes = (UserRoleAttributes) userRoleAttributesDto.getAttributes();
            rolePermissionsDto.setEngineModels(attributes.getEngineModels());
            rolePermissionsDto.setAirFrames(attributes.getAirFrames());
            rolePermissionsDto.setDocTypes(attributes.getDocTypes());
            downloadUserPermissionsList.add(rolePermissionsDto);
        }

        return downloadUserPermissionsList;
    }
    
    @Override
    public List<ProductRolesDto> getAllAvailableRolesForProduct(String product) {
    	return iRoleData.findRolesByProduct(product);
    }

	@SuppressWarnings("unchecked")
	@Override
	public ImmutableMap<Object, Object> getEntitledEngineModels(String ssoId, String product, String resource) throws TechpubsException {
		
		List<UserRoleAttributesDto> userRoleAttributesMapList = 
				iPermissionData.findUserRoleAttributesBySSOAndResourceAndProduct(ssoId, resource, product);
		
		List<Object> attributesMapObj = userRoleAttributesMapList.stream().map(UserRoleAttributesDto::getAttributes).collect(Collectors.toList());
		
		ObjectMapper mapper = new ObjectMapper();
		Map<String, List<String>> attributesMap = new HashMap<>();
		
		for(Object attribute : attributesMapObj) {
			attributesMap.putAll(mapper.convertValue(attribute, Map.class));
		}
		
		if(userRoleAttributesMapList.stream().anyMatch(ur -> ur.getRole().contains("superuser"))
				|| attributesMap.get("engineModels").contains("all")) {
			return getMdmEngineModels(ssoId);
		}

		List<EngineModelEntity> engineModelList = 
				iEngineModelData.findByModelIn(attributesMap.get("engineModels"));
				
		Set<Pair<String, String>> engineFamilyModelPairs = 
				engineModelList.stream()
				.map(e -> Pair.of(e.getFamily(), e.getModel()))
				.collect(Collectors.toSet());
		
		Map<String, List<String>> engineFamilyMap = engineFamilyModelPairs.stream()
				.collect(Collectors.groupingBy(Pair::getKey, 
						Collectors.mapping(Pair::getValue, 
								Collectors.toList())));
				
		return ImmutableMap.builder().putAll(engineFamilyMap).build();
	}
}
