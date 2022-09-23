package com.geaviation.techpubs.models.cwcadmin.response;

import com.geaviation.techpubs.models.cwcadmin.dto.UserDto;

import java.util.List;

public class UserDetailsResponse {

    private List<UserDto> users;

    public UserDetailsResponse() { }

    public UserDetailsResponse(List<UserDto> users) { this.users = users; }

    public List<UserDto> getUsers() { return users; }

    public void setUsers(List<UserDto> users) { this.users = users; }
}
