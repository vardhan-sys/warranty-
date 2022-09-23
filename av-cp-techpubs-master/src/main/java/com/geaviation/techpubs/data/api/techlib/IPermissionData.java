package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.techlib.PermissionEntity;
import com.geaviation.techpubs.models.techlib.PermissionEntityPK;
import com.geaviation.techpubs.models.techlib.UserRoleAttributes;
import com.geaviation.techpubs.models.techlib.dto.UserPermissionsDto;
import com.geaviation.techpubs.models.techlib.dto.UserRoleAttributesDto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface IPermissionData extends JpaRepository<PermissionEntity, PermissionEntityPK> {

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END " +
            "FROM UserRoleEntity ur " +
            "INNER JOIN RolePermissionEntity rp ON rp.role = ur.role " +
            "INNER JOIN PermissionEntity p ON p.resource = rp.resource AND p.action = rp.action " +
            "WHERE ur.sso = :sso " +
            "AND p.resource = :resource " +
            "AND p.action = :action"
    )
    boolean userPermissionExists(@Param("sso") String ssoId, @Param("resource") String resource, @Param("action") String action);

    @Query("SELECT ur.attributes " +
               "FROM UserRoleEntity ur " +
               "INNER JOIN RolePermissionEntity rp ON rp.role = ur.role " +
               "INNER JOIN PermissionEntity p ON p.resource = rp.resource AND p.action = rp.action " +
               "WHERE ur.sso = :sso " +
               "AND p.resource = :resource " +
               "AND p.action = :action"
    )
    List<UserRoleAttributes> findUserRoleAttributes(@Param("sso") String ssoId, @Param("resource") String resource, @Param("action") String action);

	@Query("SELECT NEW com.geaviation.techpubs.models.techlib.dto.UserRoleAttributesDto(ur.role, ur.attributes) " 
			+ "FROM UserRoleEntity ur "
			+ "INNER JOIN RolePermissionEntity rp ON rp.role = ur.role "
			+ "INNER JOIN PermissionEntity p ON p.resource = rp.resource AND p.action = rp.action "
			+ "INNER JOIN ResourceEntity re ON p.resource = re.name " + "WHERE ur.sso = :sso "
			+ "AND p.resource = :resource " + "AND p.action = 'edit' "
			+ "AND rp.role in (select role from RolePermissionEntity where resource =:product)")
	
	List<UserRoleAttributesDto> findUserRoleAttributesBySSOAndResourceAndProduct(@Param("sso") String ssoId,
			@Param("resource") String resource, @Param("product") String product);

    @Query("SELECT DISTINCT NEW com.geaviation.techpubs.models.techlib.dto.UserPermissionsDto(p.resource, p.action, re.type) " +
            "FROM UserRoleEntity ur " +
            "INNER JOIN RolePermissionEntity rp ON rp.role = ur.role " +
            "INNER JOIN PermissionEntity p ON p.resource = rp.resource AND p.action = rp.action " +
            "INNER JOIN ResourceEntity re ON p.resource = re.name " +
            "WHERE ur.sso = :sso"
    )
    List<UserPermissionsDto> findUserPermissions(@Param("sso") String ssoId);

    @Query("SELECT NEW com.geaviation.techpubs.models.techlib.dto.UserPermissionsDto(p.resource, p.action, re.type) " +
            "FROM UserRoleEntity ur " +
            "INNER JOIN RolePermissionEntity rp ON rp.role = ur.role " +
            "INNER JOIN PermissionEntity p ON p.resource = rp.resource AND p.action = rp.action " +
            "INNER JOIN ResourceEntity re ON p.resource = re.name " +
            "WHERE ur.sso = :sso " +
            "AND re.name = :resource"
    )
    List<UserPermissionsDto> findUserPermissionsByResource(@Param("sso") String ssoId, @Param("resource") String resource);
    
    @Query("SELECT DISTINCT NEW com.geaviation.techpubs.models.techlib.dto.UserPermissionsDto(p.resource, p.action, re.type,(select resource from RolePermissionEntity \r\n" + 
    		"where resource in (select name from ResourceEntity where type='product') and role = rp.role) as product) " +
            "FROM UserRoleEntity ur " +
            "INNER JOIN RolePermissionEntity rp ON rp.role = ur.role " +
            "INNER JOIN PermissionEntity p ON p.resource = rp.resource AND p.action = rp.action " +
            "INNER JOIN ResourceEntity re ON p.resource = re.name " +
            "WHERE ur.sso = :sso"
    )
    List<UserPermissionsDto> findUserPermissionsNew(@Param("sso") String ssoId);

    @Query("SELECT NEW com.geaviation.techpubs.models.techlib.dto.UserPermissionsDto(p.resource, p.action, re.type,(select resource from RolePermissionEntity \r\n" + 
    		"where resource in (select name from ResourceEntity where type='product') and role = rp.role) as product) " +
    		"FROM UserRoleEntity ur " +
            "INNER JOIN RolePermissionEntity rp ON rp.role = ur.role " +
            "INNER JOIN PermissionEntity p ON p.resource = rp.resource AND p.action = rp.action " +
            "INNER JOIN ResourceEntity re ON p.resource = re.name " +
            "WHERE ur.sso = :sso " +
            "AND re.name = :resource"
    )
    List<UserPermissionsDto> findUserPermissionsByResourceNew(@Param("sso") String ssoId, @Param("resource") String resource);
    
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END " +
            "FROM UserRoleEntity ur " +
            "INNER JOIN RolePermissionEntity rp ON rp.role = ur.role " +
            "INNER JOIN PermissionEntity p ON p.resource = rp.resource AND p.action = rp.action " +
            "WHERE ur.sso = :sso " +
            "AND p.resource = :resource " +
            "AND p.action = :action " +
            "AND rp.role in (select role from RolePermissionEntity where resource =:product)"
    )
    boolean userPermissionExistsForProduct(@Param("sso") String ssoId, @Param("resource") String resource, @Param("action") String action, @Param("product") String product);
}