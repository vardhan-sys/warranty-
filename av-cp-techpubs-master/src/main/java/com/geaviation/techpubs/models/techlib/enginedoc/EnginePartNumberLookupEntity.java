package com.geaviation.techpubs.models.techlib.enginedoc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "engine_part_number_lookup", schema = "techlib")
public class EnginePartNumberLookupEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private UUID id;

	@JsonIgnore
	@Column(name = "value", length = -1)
	private String value;

	@JsonIgnore
	@Column(name = "last_updated_by", length = -1)
	private String lastUpdatedBy;

	@JsonIgnore
	@Column(name = "last_updated_date")
	@UpdateTimestamp
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date lastUpdatedDate;

	@JsonIgnore
	@ManyToMany(mappedBy = "enginePartNumbers")
	private Set<EngineDocumentEntity> engineDocuments = new HashSet<>();

	public EnginePartNumberLookupEntity() {
	}

	public EnginePartNumberLookupEntity(UUID id, String value, String lastUpdatedBy, Date lastUpdatedDate) {
		this.id = id;
		this.value = value;
		this.lastUpdatedBy = lastUpdatedBy;
		this.lastUpdatedDate = lastUpdatedDate;
	}

	public Set<EngineDocumentEntity> getEngineDocuments() {
		return engineDocuments;
	}

	public void setEngineDocuments(Set<EngineDocumentEntity> engineDocuments) {
		this.engineDocuments = engineDocuments;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public Date getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(Date lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		EnginePartNumberLookupEntity that = (EnginePartNumberLookupEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(value, that.value)
				&& Objects.equals(lastUpdatedBy, that.lastUpdatedBy)
				&& Objects.equals(lastUpdatedDate, that.lastUpdatedDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, value, lastUpdatedBy, lastUpdatedDate);
	}

}
