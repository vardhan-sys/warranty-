package com.geaviation.techpubs.models.techlib;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "book_section", schema = "techlib")
public class BookSectionEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "tree_depth", nullable = false)
    private Short treeDepth;

    @Column(name = "section_key", nullable = false, length = -1)
    private String sectionKey;

    @Column(name = "title", length = -1)
    private String title;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "parent_section_id", referencedColumnName = "id", nullable = true, insertable = false, updatable = false)
    private BookSectionEntity parentSection;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "book_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private BookEntity bookId;

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "book_section_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private List<BookSectionVersionEntity> versions;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Short getTreeDepth() {
        return treeDepth;
    }

    public void setTreeDepth(Short treeDepth) {
        this.treeDepth = treeDepth;
    }

    public String getSectionKey() {
        return sectionKey;
    }

    public void setSectionKey(String sectionKey) {
        this.sectionKey = sectionKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BookSectionEntity getParentSection() {
        return parentSection;
    }

    public void setParentSection(BookSectionEntity parentSection) {
        this.parentSection = parentSection;
    }

    public BookEntity getBookId() {
        return bookId;
    }

    public void setBookId(BookEntity bookId) {
        this.bookId = bookId;
    }

    public List<BookSectionVersionEntity> getVersions() {
        return versions;
    }

    public void setVersions(List<BookSectionVersionEntity> versions) {
        this.versions = versions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookSectionEntity that = (BookSectionEntity) o;
        return treeDepth == that.treeDepth &&
                Objects.equals(id, that.id) &&
                Objects.equals(sectionKey, that.sectionKey) &&
                Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, treeDepth, sectionKey, title);
    }
}
