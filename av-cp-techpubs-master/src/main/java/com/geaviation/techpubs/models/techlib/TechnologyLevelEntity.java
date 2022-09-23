package com.geaviation.techpubs.models.techlib;

import java.util.Objects;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "technology_level", schema = "techlib")
public class TechnologyLevelEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "level", nullable = false, length = -1)
    private String level;

    @Column(name = "description", nullable = false, length = -1)
    private String description;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TechnologyLevelEntity that = (TechnologyLevelEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(level, that.level) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, level, description);
    }
}
