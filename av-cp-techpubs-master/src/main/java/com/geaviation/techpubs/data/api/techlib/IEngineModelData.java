package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.techlib.EngineModelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IEngineModelData extends JpaRepository<EngineModelEntity, String> {

    List<String> findModelByModelIn(List<String> models);

    List<EngineModelEntity> findByModelIn(List<String> models);

}
