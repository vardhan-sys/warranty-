package com.geaviation.techpubs.models.techlib;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "pageblk_lookup", schema = "techlib")
public class PageblkLookupEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "bookcase_key")
    private String bookcaseKey;

    @Column(name = "bookcase_version")
    private String bookcaseVersion;

    @Column(name = "book_key")
    private String bookKey;

    @Column(name = "revision")
    private String revision;

    @Column(name = "pageblk_key")
    private String pageblkKey;

    @Column(name = "online_filename")
    private String onlineFilename;

    @Column(name = "target")
    private String target;

    /*
    ** Constructor. You probably know that... but sonar wants me to tell you. It's a constructor.
     */
    public PageblkLookupEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBookcaseKey() {
        return bookcaseKey;
    }

    public void setBookcaseKey(String bookcaseKey) {
        this.bookcaseKey = bookcaseKey;
    }

    public String getBookcaseVersion() {
        return bookcaseVersion;
    }

    public void setBookcaseVersion(String bookcaseVersion) {
        this.bookcaseVersion = bookcaseVersion;
    }

    public String getBookKey() {
        return bookKey;
    }

    public void setBookKey(String bookKey) {
        this.bookKey = bookKey;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getPageblkKey() {
        return pageblkKey;
    }

    public void setPageblkKey(String pageblkKey) {
        this.pageblkKey = pageblkKey;
    }

    public String getOnlineFilename() {
        return onlineFilename;
    }

    public void setOnlineFilename(String onlineFilename) {
        this.onlineFilename = onlineFilename;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
