package com.geaviation.techpubs.models.techlib;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "book_section_version", schema = "techlib")
public class BookSectionVersionEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "bookcase_version", nullable = false, length = -1)
    private String bookcaseVersion;

    @Column(name = "book_section_order")
    private Short bookSectionOrder;

    @JsonIgnore
    @Column(name = "created_by", length = -1)
    private String createdBy;

    @JsonIgnore
    @Column(name = "creation_date")
    private Timestamp creationDate;

    @JsonIgnore
    @Column(name = "last_updated_by", length = -1)
    private String lastUpdatedBy;

    @JsonIgnore
    @Column(name = "last_updated_date")
    private Timestamp lastUpdatedDate;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBookcaseVersion() {
        return bookcaseVersion;
    }

    public void setBookcaseVersion(String bookcaseVersion) {
        this.bookcaseVersion = bookcaseVersion;
    }

    public Short getBookSectionOrder() {
        return bookSectionOrder;
    }

    public void setBookSectionOrder(Short bookSectionOrder) {
        this.bookSectionOrder = bookSectionOrder;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Timestamp getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Timestamp lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookSectionVersionEntity that = (BookSectionVersionEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(bookcaseVersion, that.bookcaseVersion) &&
                Objects.equals(bookSectionOrder, that.bookSectionOrder) &&
                Objects.equals(createdBy, that.createdBy) &&
                Objects.equals(creationDate, that.creationDate) &&
                Objects.equals(lastUpdatedBy, that.lastUpdatedBy) &&
                Objects.equals(lastUpdatedDate, that.lastUpdatedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bookcaseVersion, bookSectionOrder, createdBy, creationDate, lastUpdatedBy, lastUpdatedDate);
    }
}
