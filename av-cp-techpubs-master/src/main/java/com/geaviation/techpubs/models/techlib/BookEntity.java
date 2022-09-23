package com.geaviation.techpubs.models.techlib;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "book", schema = "techlib")
public class BookEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "book_key", nullable = false, length = -1)
    private String bookKey;

    @Column(name = "book_type", nullable = false, length = -1)
    private String bookType;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "bookcase_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private BookcaseEntity bookcase;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "book_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private List<BookVersionEntity> versions;

    @Transient
    private Boolean previouslyEnabled = Boolean.FALSE;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBookKey() {
        return bookKey;
    }

    public void setBookKey(String bookKey) {
        this.bookKey = bookKey;
    }

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    public Boolean getPreviouslyEnabled() {
        return previouslyEnabled;
    }

    public void setPreviouslyEnabled(Boolean previouslyEnabled) {
        this.previouslyEnabled = previouslyEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookEntity that = (BookEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(bookKey, that.bookKey) &&
                Objects.equals(bookType, that.bookType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bookKey, bookType);
    }
}
