package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.techlib.RoleEntity;
import com.geaviation.techpubs.models.techlib.dto.ProductRolesDto;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IRoleData extends JpaRepository<RoleEntity, String> {

	@Query("SELECT NEW com.geaviation.techpubs.models.techlib.dto.ProductRolesDto(rl.name,rl.label,rl.description) " +
            "from RolePermissionEntity rl_p \r\n" + 
            "inner join RoleEntity rl on rl.name = rl_p.role \r\n" + 
            "inner join ResourceEntity rs on rs.name = rl_p.resource\r\n" + 
            "where rs.type ='product' and rs.name= :productName"
    )
	List<ProductRolesDto> findRolesByProduct(@Param("productName") String productName);


}
