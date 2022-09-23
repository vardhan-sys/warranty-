package com.geaviation.techpubs.data.api.techlib.enginedoc;

import com.geaviation.techpubs.models.techlib.enginedoc.EnginePartNumberLookupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

public interface IEnginePartNumberLookupData extends JpaRepository <EnginePartNumberLookupEntity, UUID>{

    Optional<EnginePartNumberLookupEntity> findByValue(String value);

}
