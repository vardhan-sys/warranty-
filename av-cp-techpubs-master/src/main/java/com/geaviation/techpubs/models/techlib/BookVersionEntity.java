package com.geaviation.techpubs.models.techlib;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "book_version", schema = "techlib")
public class BookVersionEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "title", nullable = false, length = -1)
    private String title;

    @Column(name = "bookcase_version", nullable = false, length = -1)
    private String bookcaseVersion;

    @Column(name = "book_order")
    private Short bookOrder;

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

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "book_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private BookEntity book;

    @Column(name = "revision", length = -1)
    private String revision;

    @Column(name="revision_date")
    @Convert(converter = BookRevisionConverter.class)
    private Date revisionDate;
    
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBookcaseVersion() {
        return bookcaseVersion;
    }

    public void setBookcaseVersion(String bookcaseVersion) {
        this.bookcaseVersion = bookcaseVersion;
    }

    public Short getBookOrder() {
        return bookOrder;
    }

    public void setBookOrder(Short bookOrder) {
        this.bookOrder = bookOrder;
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

    public BookEntity getBook() {
        return book;
    }

    public void setBook(BookEntity book) {
        this.book = book;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public Date getRevisionDate() {
        return revisionDate;
    }

    public void setRevisionDate(Date revisionDate) {
        this.revisionDate = revisionDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookVersionEntity that = (BookVersionEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(bookcaseVersion, that.bookcaseVersion) &&
                Objects.equals(bookOrder, that.bookOrder) &&
                Objects.equals(createdBy, that.createdBy) &&
                Objects.equals(creationDate, that.creationDate) &&
                Objects.equals(lastUpdatedBy, that.lastUpdatedBy) &&
                Objects.equals(lastUpdatedDate, that.lastUpdatedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bookcaseVersion, bookOrder, createdBy, creationDate, lastUpdatedBy, lastUpdatedDate);
    }

    @Converter
    public static class BookRevisionConverter implements AttributeConverter<Date, String> {

        private final Logger log = LogManager.getLogger(BookRevisionConverter.class);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

        @Override
        public String convertToDatabaseColumn(Date entityDate) {
            // format entityDate and return String
            if (entityDate.getTime() == Long.MIN_VALUE) {
                return "";
            }
            return format.format(entityDate);
        }

        @Override
        public Date convertToEntityAttribute(String databaseDate) {
            // parse databaseDate and return Date object
            try {
                return format.parse(databaseDate);
            } catch (Exception e) {
                log.error("Error parsing date from database: " + e.getMessage());
            }

            return new Date(Long.MIN_VALUE);
        }
    }
}

