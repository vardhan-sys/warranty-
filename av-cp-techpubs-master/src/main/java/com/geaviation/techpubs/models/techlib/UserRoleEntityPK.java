package com.geaviation.techpubs.models.techlib;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class UserRoleEntityPK implements Serializable {

    @Id
    @Column(name = "sso")
    private String sso;

    @Id
    @Column(name = "role")
    private String role;

    public UserRoleEntityPK() { }

    public UserRoleEntityPK(String sso, String role) {
        this.sso = sso;
        this.role = role;
    }

    public String getSso() {
        return sso;
    }

    public void setSso(String sso) {
        this.sso = sso;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRoleEntityPK that = (UserRoleEntityPK) o;
        return Objects.equals(sso, that.sso) &&
                Objects.equals(role, that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sso, role);
    }
}
