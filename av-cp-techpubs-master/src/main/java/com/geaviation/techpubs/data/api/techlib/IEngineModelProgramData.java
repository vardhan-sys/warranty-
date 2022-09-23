package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.techlib.EngineModelProgramEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IEngineModelProgramData extends JpaRepository<EngineModelProgramEntity, String> {

        List<EngineModelProgramEntity> findByEngineModelIgnoreCase(@Param("engineModel") String engineModel);

        @Query("FROM EngineModelProgramEntity emp " +
                "WHERE emp.engineModel IN (:engineModels)")
        List<EngineModelProgramEntity> findByEngineModelList(@Param("engineModels") List<String> engineModels);

        @Query("SELECT emp.engineModel " +
                "FROM EngineModelProgramEntity emp " +
                "WHERE emp.bookcaseKey = :bookcaseKey")
        List<String> findEngineModelsByBookcaseKey(@Param("bookcaseKey") String bookcaseKey);
}
