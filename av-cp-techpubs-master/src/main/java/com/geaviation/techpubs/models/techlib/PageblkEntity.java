package com.geaviation.techpubs.models.techlib;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "pageblk", schema = "techlib")
@TypeDef(
        name = "jsonb",
        typeClass = JsonBinaryType.class
)
public class PageblkEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "pageblk_key", nullable = false, length = -1)
    private String pageblkKey;

    @Column(name = "title", nullable = false, length = -1)
    private String title;

    @Column(name = "toc_title", length = -1)
    private String tocTitle;

    @Column(name = "approved_for_publish", length = -1)
    private boolean approvedForPublish;

    @Column(name = "email_notification", nullable = false)
    private boolean emailNotification;

    @JsonIgnore
    @Type(type = "jsonb")
    @Column(name = "metadata", nullable = false, columnDefinition = "jsonb")
    private String metadata;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "publication_type_code", referencedColumnName = "code", nullable = false, insertable = false, updatable = false)
    private PublicationTypeEntity publicationType;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "book_section_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private BookSectionEntity bookSection;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "pageblk_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private List<PageblkVersionEntity> versions;

    @Transient
    private Boolean previouslyEnabled = Boolean.FALSE;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPageblkKey() {
        return pageblkKey;
    }

    public void setPageblkKey(String pageblkKey) {
        this.pageblkKey = pageblkKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTocTitle() {
        return tocTitle;
    }

    public void setTocTitle(String tocTitle) {
        this.tocTitle = tocTitle;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Boolean isPreviouslyEnabled() {
        return previouslyEnabled;
    }

    public void setPreviouslyEnabled(Boolean previouslyEnabled) {
        this.previouslyEnabled = previouslyEnabled;
    }

    public BookSectionEntity getBookSection() {
        return bookSection;
    }

    public void setBookSection(BookSectionEntity bookSection) {
        this.bookSection = bookSection;
    }

    public boolean isApprovedForPublish() {
        return approvedForPublish;
    }

    public void setApprovedForPublish(boolean approvedForPublish) {
        this.approvedForPublish = approvedForPublish;
    }

    public boolean isEmailNotification() {
        return emailNotification;
    }

    public void setEmailNotification(boolean emailNotification) {
        this.emailNotification = emailNotification;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageblkEntity that = (PageblkEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(pageblkKey, that.pageblkKey) &&
                Objects.equals(title, that.title) &&
                Objects.equals(tocTitle, that.tocTitle) &&
                Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pageblkKey, title, tocTitle, metadata);
    }

    public PublicationTypeEntity getPublicationType() {
        return publicationType;
    }

    public void setPublicationType(PublicationTypeEntity publicationType) {
        this.publicationType = publicationType;
    }
}
