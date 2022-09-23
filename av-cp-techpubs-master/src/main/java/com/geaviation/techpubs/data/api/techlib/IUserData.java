package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.techlib.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUserData extends JpaRepository<UserEntity, String> {

    @Query("SELECT u, ur.role " +
            "FROM UserEntity u " +
            "JOIN UserRoleEntity ur ON ur.sso = u.sso"
    )
    List<Object[]> findAllUsersWithRoles();
}
