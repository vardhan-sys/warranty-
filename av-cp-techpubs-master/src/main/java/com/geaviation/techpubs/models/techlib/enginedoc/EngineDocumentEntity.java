package com.geaviation.techpubs.models.techlib.enginedoc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geaviation.techpubs.models.techlib.EngineModelEntity;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;


@Entity
@Table(name = "engine_document", schema = "techlib")
public class EngineDocumentEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "engine_document_type_id", nullable = false, insertable = false, updatable = false)
    private UUID engineDocumentTypeId;

    @Column(name = "document_title")
    private String documentTitle;

    @Column(name = "part_name")
    private String partName;

    @Column(name = "email_notification", nullable = false)
    private Boolean emailNotification;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted;

    @Column(name = "file_name")
    private String fileName;

    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "created_date", nullable = false)
    @CreationTimestamp
    private Date createdDate;

    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "last_updated_date", nullable = false)
    @UpdateTimestamp
    private Date lastUpdatedDate;

    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "issue_date")
    private Date issueDate;

    @Column(name = "last_email_sent_date")
    private Date lastEmailSentDate;

    // Many-to-One Relationship with enginedocumenttypelookupentity
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "engine_document_type_id", referencedColumnName = "id", nullable = false)
    private EngineDocumentTypeLookupEntity engineDocumentTypeLookupEntity;

    // Many-to-many with EnginePartNumberLookupEntity
    @JsonIgnore
    @ManyToMany
    @JoinTable( name="engine_document_part_number_affected",
            joinColumns = {@JoinColumn(name = "engine_document_id")},
            inverseJoinColumns = {@JoinColumn(name = "engine_part_number_id")}
    )
    private Set<EnginePartNumberLookupEntity> enginePartNumbers;

    //One-to-Many with the CAS Number entity - Owner side of the relationship
    @JsonIgnore
    @OneToMany(mappedBy = "engineDocumentEntity", cascade = {CascadeType.ALL},
            orphanRemoval=true)
    private Set<EngineCASNumberEntity> engineCASNumberEntity;


    // Many-to-Many relationship with Engine Model Entity
    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "engine_document_engine_model",
            joinColumns = {@JoinColumn(name = "engine_document_id")},
            inverseJoinColumns = {@JoinColumn(name = "engine_model")})
    private Set<EngineModelEntity> engineModelEntity;

    public EngineDocumentEntity() {

    }

    public EngineDocumentEntity(UUID engineDocumentTypeId, String documentTitle, String partName, Boolean emailNotification,
                                Boolean deleted, String fileName, Date createdDate, Date lastUpdatedDate,
                                EngineDocumentTypeLookupEntity engineDocumentTypeLookupEntity,
                                Set<EnginePartNumberLookupEntity> enginePartNumbers,
                                Set<EngineCASNumberEntity> engineCASNumberEntity, Set<EngineModelEntity> engineModelEntity,
                                Date issueDate, Date lastEmailSentDate) {
        this.documentTitle = documentTitle;
        this.engineDocumentTypeId = engineDocumentTypeId;
        this.partName = partName;
        this.emailNotification = emailNotification;
        this.deleted = deleted;
        this.fileName = fileName;
        this.createdDate = createdDate;
        this.lastUpdatedDate = lastUpdatedDate;
        this.engineDocumentTypeLookupEntity = engineDocumentTypeLookupEntity;
        this.enginePartNumbers = enginePartNumbers;
        this.engineCASNumberEntity = engineCASNumberEntity;
        this.engineModelEntity = engineModelEntity;
        this.issueDate = issueDate;
        this.lastEmailSentDate = lastEmailSentDate;
    }

    //Getter and Setter Methods

    public Set<EnginePartNumberLookupEntity> getEnginePartNumbers() {
        return enginePartNumbers;
    }

    public void setEnginePartNumbers(Set<EnginePartNumberLookupEntity> enginePartNumbers) {
        this.enginePartNumbers = enginePartNumbers;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public Boolean getEmailNotification() {
        return emailNotification;
    }

    public void setEmailNotification(Boolean emailNotification) {
        this.emailNotification = emailNotification;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public EngineDocumentTypeLookupEntity getEngineDocumentTypeLookupEntity() {
        return engineDocumentTypeLookupEntity;
    }

    public void setEngineDocumentTypeLookupEntity(EngineDocumentTypeLookupEntity engineDocumentTypeLookupEntity) {
        this.engineDocumentTypeLookupEntity = engineDocumentTypeLookupEntity;
    }

    public Set<EngineCASNumberEntity> getEngineCASNumberEntity() {
        return engineCASNumberEntity;
    }

    public void setEngineCASNumberEntity(Set<EngineCASNumberEntity> engineCASNumberEntity) {
        this.engineCASNumberEntity = engineCASNumberEntity;
    }

    // Convenience setter for when we receive a list of strings from the UI,
    // we can use this to set the entities
    public void setEngineCasNumberEntity(List<String> casNumbers) {
        if (this.engineCASNumberEntity == null) {
            this.engineCASNumberEntity = new HashSet<>();
        }
        if (casNumbers == null) {
            this.engineCASNumberEntity.clear();
        } else {
            // Adding all the new ones if not already in list
            for (String casNumber : casNumbers) {
                EngineCASNumberEntity casNumberEntity = this.engineCASNumberEntity.stream()
                        .filter(c -> c.getCasNumber().equals(casNumber))
                        .findFirst()
                        .orElse(null);
                if (casNumberEntity == null) {
                    this.engineCASNumberEntity.add(new EngineCASNumberEntity(casNumber, this));
                }
            }

            // Remove old ones not present in new list
            this.engineCASNumberEntity.removeIf(p -> !casNumbers.contains(p.getCasNumber()));
        }
    }

    public Set<EngineModelEntity> getEngineModelEntity() {
        return engineModelEntity;
    }

    public void setEngineModelEntity(Set<EngineModelEntity> engineModelEntity) {
        this.engineModelEntity = engineModelEntity;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getLastEmailSentDate() { return lastEmailSentDate; }

    public void setLastEmailSentDate(Date lastEmailSentDate) { this.lastEmailSentDate = lastEmailSentDate; }

    public UUID getEngineDocumentTypeId() { return engineDocumentTypeId; }

    public void setEngineDocumentTypeId(UUID engineDocumentTypeId) { this.engineDocumentTypeId = engineDocumentTypeId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EngineDocumentEntity that = (EngineDocumentEntity) o;
        return id.equals(that.id) && engineDocumentTypeId.equals(that.engineDocumentTypeId) && documentTitle.equals(that.documentTitle) && partName.equals(that.partName) &&
                emailNotification.equals(that.emailNotification) && deleted.equals(that.deleted) &&
                fileName.equals(that.fileName) && createdDate.equals(that.createdDate) && lastUpdatedDate.equals(that.lastUpdatedDate) &&
                engineDocumentTypeLookupEntity.equals(that.engineDocumentTypeLookupEntity) &&
                enginePartNumbers.equals(that.enginePartNumbers) &&
                engineCASNumberEntity.equals(that.engineCASNumberEntity) && engineModelEntity.equals(that.engineModelEntity)
                && issueDate.equals(that.issueDate) && lastEmailSentDate.equals(that.lastEmailSentDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, engineDocumentTypeId, documentTitle, partName, emailNotification, deleted, fileName, createdDate,
                lastUpdatedDate, engineDocumentTypeLookupEntity, enginePartNumbers,
                engineCASNumberEntity, engineModelEntity, issueDate, lastEmailSentDate);
    }

    @Override
    public String toString() {
        return "{ id : "+this.id+",\nengineDocumentTypeId : "+this.engineDocumentTypeId+",\ndocumentTitle : "+this.documentTitle+",\npartName : "+this.partName
                + ",\nemailNotification : "+this.emailNotification+", \nfileName : "+this.fileName+",\ndeleted : "+this.deleted+",\ncreatedDate : "+
                this.createdDate+",\nlastUpdatedDate : "+this.lastUpdatedDate + ",\nissueDate : " + this.issueDate + ",\nlastEmailSentDate : " + this.lastEmailSentDate + "\n}";
    }
}