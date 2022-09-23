package com.geaviation.techpubs.data.api.techlib.enginedoc;

import com.geaviation.techpubs.models.techlib.enginedoc.EngineDocumentTypeLookupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IEngineDocumentTypeLookupData extends JpaRepository<EngineDocumentTypeLookupEntity, UUID> {

    Optional<EngineDocumentTypeLookupEntity> findByValue(String value);

}
