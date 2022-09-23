package com.geaviation.techpubs.models.techlib.response;

import com.geaviation.techpubs.models.techlib.dto.UserRolesDto;

import java.util.List;

public class UserListResponse {

    private List<UserRolesDto> users;

    public UserListResponse() { }

    public UserListResponse(List<UserRolesDto> users) { this.users = users; }

    public List<UserRolesDto> getUsers() { return users; }

    public void setUsers(List<UserRolesDto> users) { this.users = users; }
}
