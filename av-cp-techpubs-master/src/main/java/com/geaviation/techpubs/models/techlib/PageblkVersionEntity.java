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
@Table(name = "pageblk_version", schema = "techlib")
public class PageblkVersionEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "bookcase_version", nullable = false, length = -1)
    private String bookcaseVersion;

    @Column(name = "online_filename", nullable = false, length = -1)
    private String onlineFilename;

    @Column(name = "filepath", nullable = false, length = -1)
    private String filepath;

    @Column(name = "offline_filename", nullable = true, length = -1)
    private String offlineFilename;

    @Column(name = "ded_filename", nullable = true, length = -1)
    private String dedFilename;

    @Column(name = "pageblk_order")
    private Short pageblkOrder;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "pageblk_id", referencedColumnName = "id", insertable = false, updatable = false)
    private PageblkEntity pageBlk;

    @Column(name = "pageblk_id")
    private UUID pageblk_id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "technology_level_id", referencedColumnName = "id", insertable = false, updatable = false)
    private TechnologyLevelEntity technologyLevel;

    @Column(name = "revision")
    private String revisionNumber;

    @Column(name="revision_date")
    @Convert(converter = PageBlkRevisionConverter.class)
    private Date revisionDate;

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

    public String getOnlineFilename() {
        return onlineFilename;
    }

    public void setOnlineFilename(String onlineFilename) {
        this.onlineFilename = onlineFilename;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getOfflineFilename() {
        return offlineFilename;
    }

    public void setOfflineFilename(String offlineFilename) {
        this.offlineFilename = offlineFilename;
    }

    public String getDedFilename() {
        return dedFilename;
    }

    public void setDedFilename(String dedFilename) {
        this.dedFilename = dedFilename;
    }

    public Short getPageblkOrder() {
        return pageblkOrder;
    }

    public void setPageblkOrder(Short pageblkOrder) {
        this.pageblkOrder = pageblkOrder;
    }

    public TechnologyLevelEntity getTechnologyLevel() {
        return technologyLevel;
    }

    public void setTechnologyLevel(TechnologyLevelEntity technologyLevel) {
        this.technologyLevel = technologyLevel;
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

    public String getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(String revisionNumber) {
        this.revisionNumber = revisionNumber;
    }

    public Date getRevisionDate() {
        return revisionDate;
    }

    public void setRevisionDate(Date revisionDate) {
        this.revisionDate = revisionDate;
    }

    public PageblkEntity getPageBlk() {
        return pageBlk;
    }

    public void setPageBlk(PageblkEntity pageBlk) {
        this.pageBlk = pageBlk;
    }

    public UUID getPageblk_id() {
        return pageblk_id;
    }

    public void setPageblk_id(UUID pageblk_id) {
        this.pageblk_id = pageblk_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageblkVersionEntity that = (PageblkVersionEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(bookcaseVersion, that.bookcaseVersion) &&
                Objects.equals(onlineFilename, that.onlineFilename) &&
                Objects.equals(filepath, that.filepath) &&
                Objects.equals(offlineFilename, that.offlineFilename) &&
                Objects.equals(dedFilename, that.dedFilename) &&
                Objects.equals(pageblkOrder, that.pageblkOrder) &&
                Objects.equals(createdBy, that.createdBy) &&
                Objects.equals(creationDate, that.creationDate) &&
                Objects.equals(lastUpdatedBy, that.lastUpdatedBy) &&
                Objects.equals(lastUpdatedDate, that.lastUpdatedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bookcaseVersion, onlineFilename, filepath, offlineFilename, dedFilename, pageblkOrder, createdBy, creationDate, lastUpdatedBy, lastUpdatedDate);
    }

    @Converter
    public static class PageBlkRevisionConverter implements AttributeConverter<Date, String> {
        private final Logger log = LogManager.getLogger(PageBlkRevisionConverter.class);
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

            Date date = null;
            if(databaseDate!= null && !databaseDate.isEmpty()) {
                // parse databaseDate and return Date object
                try {
                    date = format.parse(databaseDate);
                } catch (Exception e) {
                    //log.error("Error parsing date from database: " + e.getMessage());
                }
            }

            return date;
        }
    }
}
