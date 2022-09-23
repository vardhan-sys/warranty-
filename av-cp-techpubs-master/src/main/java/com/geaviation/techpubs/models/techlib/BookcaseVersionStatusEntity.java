package com.geaviation.techpubs.models.techlib;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bookcase_version_status", schema = "techlib")
public class BookcaseVersionStatusEntity {
    @Id
    @Column(name = "code", nullable = false, length = -1)
    private String code;

    @Column(name = "description", length = -1)
    private String description;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookcaseVersionStatusEntity that = (BookcaseVersionStatusEntity) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, description);
    }
}
