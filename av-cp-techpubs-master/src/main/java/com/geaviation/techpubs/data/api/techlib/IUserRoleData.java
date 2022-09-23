package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.techlib.UserRoleEntity;
import com.geaviation.techpubs.models.techlib.UserRoleEntityPK;
import com.geaviation.techpubs.models.techlib.dto.UserRoleAttributesDto;
import com.geaviation.techpubs.models.techlib.dto.UserRolePolicyAttributesDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface IUserRoleData extends JpaRepository<UserRoleEntity, UserRoleEntityPK> {

    @Query("SELECT ur.role " +
            "FROM UserRoleEntity ur " +
            "WHERE ur.sso = :sso"
    )
    List<UserRoleEntity> findUserRolesBySso(@Param("sso") String sso);

    @Query("SELECT ur.sso " +
            "FROM UserRoleEntity ur " +
            "WHERE ur.role = :role"
    )
    List<UserRoleEntity> findUsersByRole(@Param("role") String role);

    @Query("SELECT NEW com.geaviation.techpubs.models.techlib.dto.UserRolePolicyAttributesDto(ur.role, r.policy, ur.attributes) " +
            "FROM UserRoleEntity ur " +
            "INNER JOIN RoleEntity r ON r.name = ur.role " +
            "WHERE ur.sso = :sso"
    )
    List<UserRolePolicyAttributesDto> findUserRolesWithPolicyAndAttributesBySso(@Param("sso") String ssoId);
    
    @Query("SELECT NEW com.geaviation.techpubs.models.techlib.dto.UserRoleAttributesDto(ur.role, ur.attributes) " +
            "FROM UserRoleEntity ur " +
            "WHERE ur.sso = :sso"
    )
    List<UserRoleAttributesDto> findUserRoleAttributesBySso(@Param("sso") String ssoId);

    @Query("SELECT ur " +
            "FROM UserRoleEntity ur " +
            "WHERE ur.sso = :sso " +
            "AND ur.role = :role"
    )
    UserRoleEntity findUserRoleEntityBySsoAndRole(@Param("sso") String sso, @Param("role") String role);

    @Query("SELECT ur " +
            "FROM UserRoleEntity ur " +
            "WHERE ur.sso = :sso"
    )
    List<UserRoleEntity> findUserRoleEntityBySso(@Param("sso") String sso);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserRoleEntity ur " +
            "WHERE ur.sso = :sso " +
            "AND ur.role = :role"
    )
    void deleteBySsoAndRole(@Param("sso") String sso, @Param("role") String role);
    
    @Query("SELECT ur " +
    		"FROM UserRoleEntity ur " +
    		"WHERE ur.role in( select rl_p.role from RolePermissionEntity rl_p\r\n" + 
    		"inner join ResourceEntity rs on rs.name = rl_p.resource\r\n" + 
    		"where rs.type ='product' and rs.name= :product)"
    )
    List<UserRoleEntity> findUsersByProduct(@Param("product") String product);

    @Query("SELECT NEW com.geaviation.techpubs.models.techlib.dto.UserRolePolicyAttributesDto(ur.role, r.policy, ur.attributes,r.label) " +
            "FROM UserRoleEntity ur " +
            "INNER JOIN RoleEntity r ON r.name = ur.role " +
            "WHERE ur.sso = :sso and ur.role in (select role from RolePermissionEntity where resource =:product)"
    )
	List<UserRolePolicyAttributesDto> findUserRolesWithPolicyAndAttributesBySsoAndProduct(@Param("sso") String ssoId, @Param("product") String product);

    @Query("SELECT NEW com.geaviation.techpubs.models.techlib.dto.UserRoleAttributesDto(ur.role, ur.attributes) " +
            "FROM UserRoleEntity ur " +
            "WHERE ur.sso = :sso and ur.role in (select role from RolePermissionEntity where resource =:product)"
    )
	List<UserRoleAttributesDto> findUserRoleAttributesBySsoAndProduct(@Param("sso") String ssoId, @Param("product") String product);
}
