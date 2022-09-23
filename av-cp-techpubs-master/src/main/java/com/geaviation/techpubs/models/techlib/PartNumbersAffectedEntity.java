package com.geaviation.techpubs.models.techlib;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

@Table(name = "part_numbers_affected", schema = "techlib")
@Entity
public class PartNumbersAffectedEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "part_number", nullable = false)
    private String partNumber;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "system_document_id", referencedColumnName = "id", nullable = false)
    private SystemDocumentEntity systemDocumentEntity;


    public PartNumbersAffectedEntity() {
    }

    public PartNumbersAffectedEntity(String partNumber, SystemDocumentEntity systemDocument) {
        this.partNumber = partNumber;
        this.systemDocumentEntity = systemDocument;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public SystemDocumentEntity getSystemDocumentEntity() {
        return systemDocumentEntity;
    }

    public void setSystemDocumentEntity(SystemDocumentEntity systemDocumentEntity) {
        this.systemDocumentEntity = systemDocumentEntity;
    }

}
