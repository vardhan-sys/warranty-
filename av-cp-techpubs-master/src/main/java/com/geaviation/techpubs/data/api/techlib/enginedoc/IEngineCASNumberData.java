package com.geaviation.techpubs.data.api.techlib.enginedoc;

import com.geaviation.techpubs.models.techlib.enginedoc.EngineCASNumberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IEngineCASNumberData extends JpaRepository <EngineCASNumberEntity, UUID>{
    Optional<EngineCASNumberEntity> findByEngineDocumentId(UUID engineDocumentId);
}
