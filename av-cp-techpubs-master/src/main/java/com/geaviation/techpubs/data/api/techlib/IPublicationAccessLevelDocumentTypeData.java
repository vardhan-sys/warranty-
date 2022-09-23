package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.techlib.PublicationAccessLevelDocumentTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IPublicationAccessLevelDocumentTypeData extends JpaRepository<PublicationAccessLevelDocumentTypeEntity, UUID> {
}
