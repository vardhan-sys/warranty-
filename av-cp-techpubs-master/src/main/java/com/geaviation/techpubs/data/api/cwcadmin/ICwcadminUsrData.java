package com.geaviation.techpubs.data.api.cwcadmin;

import com.geaviation.techpubs.models.cwcadmin.CwcadminUsrEntity;
import com.geaviation.techpubs.models.cwcadmin.dto.UserDto;
import com.geaviation.techpubs.models.techlib.dto.UserRolesDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ICwcadminUsrData extends PagingAndSortingRepository<CwcadminUsrEntity, Integer> {

    @Query("SELECT NEW com.geaviation.techpubs.models.cwcadmin.dto.UserDto(usr.sso, usr.firstName, usr.lastName, pav.prtyAtrbtVal, usr.primaryEmail) " +
            "FROM CwcadminUsrEntity usr " +
            "JOIN CwcadminAuthUserGrpEntity usr_grp on usr.sso = usr_grp.ssoId " +
            "JOIN CwcadminAuthGroupsEntity grp ON grp.groupId = usr_grp.groupId " +
            "JOIN CwcadminPrtyAtrbtValEntity pav ON pav.prtyId = usr.userId " +
            "WHERE grp.groupName = 'documents_admin' " +
            "AND pav.atrbtId = 35"
    )
    Page<UserDto> findAllUsersWithDocAdminRole(Pageable pageRequest);

    @Query("SELECT NEW com.geaviation.techpubs.models.cwcadmin.dto.UserDto(usr.sso, usr.firstName, usr.lastName, pav.prtyAtrbtVal, usr.primaryEmail) " +
            "FROM CwcadminUsrEntity usr " +
            "JOIN CwcadminAuthUserGrpEntity usr_grp on usr.sso = usr_grp.ssoId " +
            "JOIN CwcadminAuthGroupsEntity grp ON grp.groupId = usr_grp.groupId " +
            "JOIN CwcadminPrtyAtrbtValEntity pav ON pav.prtyId = usr.userId " +
            "WHERE grp.groupName = 'documents_admin' " +
            "AND pav.atrbtId = 35 " +
            "AND (usr.sso LIKE :ssoTerm " +
            "OR usr.firstName LIKE :firstTerm " +
            "OR usr.lastName LIKE :lastTerm)"
    )
    Page<UserDto> searchUsersWithDocAdminRole(@Param("ssoTerm") String ssoTerm, @Param("firstTerm") String firstTerm, @Param("lastTerm") String lastTerm, Pageable pageRequest);

    @Query("SELECT NEW com.geaviation.techpubs.models.techlib.dto.UserRolesDto(usr.sso, usr.firstName, usr.lastName, pav.prtyAtrbtVal, usr.primaryEmail) " +
            "FROM CwcadminUsrEntity usr " +
            "JOIN CwcadminPrtyAtrbtValEntity pav ON pav.prtyId = usr.userId " +
            "WHERE pav.atrbtId = 35 " +
            "AND usr.sso IN (:users)"
    )
    Page<UserRolesDto> findDocAdminUsers(@Param("users") Set<String> users, Pageable pageRequest);

    @Query("SELECT NEW com.geaviation.techpubs.models.techlib.dto.UserRolesDto(usr.sso, usr.firstName, usr.lastName, pav.prtyAtrbtVal, usr.primaryEmail) " +
            "FROM CwcadminUsrEntity usr " +
            "JOIN CwcadminPrtyAtrbtValEntity pav ON pav.prtyId = usr.userId " +
            "WHERE pav.atrbtId = 35 " +
            "AND usr.sso IN (:users) " +
            "AND (usr.sso LIKE :ssoTerm " +
            "OR usr.firstName LIKE :firstTerm " +
            "OR usr.lastName LIKE :lastTerm)"
    )
    Page<UserRolesDto> searchDocAdminUsers(@Param("users") Set<String> users, @Param("ssoTerm") String ssoTerm, @Param("firstTerm") String firstTerm, @Param("lastTerm") String lastTerm, Pageable pageRequest);

    @Query("SELECT NEW com.geaviation.techpubs.models.techlib.dto.UserRolesDto(usr.sso, usr.firstName, usr.lastName, pav.prtyAtrbtVal, usr.primaryEmail) " +
            "FROM CwcadminUsrEntity usr " +
            "JOIN CwcadminPrtyAtrbtValEntity pav ON pav.prtyId = usr.userId " +
            "WHERE pav.atrbtId = 35 " +
            "AND usr.sso IN (:users)"
    )
    List<UserRolesDto> findDocAdminUsers(@Param("users") Set<String> users);

    @Query("SELECT NEW com.geaviation.techpubs.models.cwcadmin.dto.UserDto(usr.sso, usr.firstName, usr.lastName, pav.prtyAtrbtVal, usr.primaryEmail) " +
            "FROM CwcadminUsrEntity usr " +
            "JOIN CwcadminPrtyAtrbtValEntity pav ON pav.prtyId = usr.userId " +
            "WHERE pav.atrbtId = 35 " +
            "AND usr.sso = :userSsoId"
    )
    UserDto findDocAdminUser(@Param("userSsoId") String userSsoId);
}