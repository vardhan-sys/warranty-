package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.techlib.TechnologyLevelEntity;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ITechnologyLevelData extends JpaRepository<TechnologyLevelEntity, UUID> {

    @Query("SELECT tl " +
            "FROM TechnologyLevelEntity tl " +
            "WHERE tl.level IN (:levels)"
    )
    List<TechnologyLevelEntity> findByListOfLevels(@Param("levels") List<String> levels);
}


