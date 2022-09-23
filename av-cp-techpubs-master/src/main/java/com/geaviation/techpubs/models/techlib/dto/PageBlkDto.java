package com.geaviation.techpubs.models.techlib.dto;

import java.util.UUID;

public class PageBlkDto implements Comparable {

    private UUID id;

    private String publicationType;

    private String pageblkKey;

    private String bookSectionKey;

    private UUID sectionId;

    private String bookKey;

    private String title;

    private String tocTitle;

    private String technologyLevel;

    private UUID technologyLevelId;

    private Boolean previouslyEnabled;

    public PageBlkDto() { }

    public PageBlkDto(UUID id, String publicationType, String pageblkKey, String bookSectionKey, UUID sectionId,
        String bookKey, String title, String tocTitle, String technologyLevel, UUID technologyLevelId) {
        this.id = id;
        this.publicationType = publicationType;
        this.pageblkKey = pageblkKey;
        this.bookSectionKey = bookSectionKey;
        this.sectionId = sectionId;
        this.bookKey = bookKey;
        this.title = title;
        this.tocTitle = tocTitle;
        this.technologyLevel = technologyLevel;
        this.technologyLevelId = technologyLevelId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPublicationType() {
        return publicationType;
    }

    public void setPublicationType(String publicationType) {
        this.publicationType = publicationType;
    }

    public String getPageblkKey() {
        return pageblkKey;
    }

    public void setPageblkKey(String pageblkKey) {
        this.pageblkKey = pageblkKey;
    }

    public String getBookSectionKey() {
        return bookSectionKey;
    }

    public UUID getSectionId() {
        return sectionId;
    }

    public void setSectionId(UUID sectionId) {
        this.sectionId = sectionId;
    }

    public void setBookSectionKey(String bookSectionKey) {
        this.bookSectionKey = bookSectionKey;
    }

    public String getBookKey() {
        return bookKey;
    }

    public void setBookKey(String bookKey) {
        this.bookKey = bookKey;
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

    public String getTechnologyLevel() {
        return technologyLevel;
    }

    public void setTechnologyLevel(String technologyLevel) {
        this.technologyLevel = technologyLevel;
    }

    public UUID getTechnologyLevelId() {
        return technologyLevelId;
    }

    public void setTechnologyLevelId(UUID technologyLevelId) {
        this.technologyLevelId = technologyLevelId;
    }

    public Boolean getPreviouslyEnabled() {
        return previouslyEnabled;
    }

    public void setPreviouslyEnabled(Boolean previouslyEnabled) {
        this.previouslyEnabled = previouslyEnabled;
    }

    @Override
    public int compareTo(Object o) {
        if (o == null || getClass() != o.getClass())
            return 0;
        return this.pageblkKey.compareTo(((PageBlkDto)o).getPageblkKey());
    }
}
