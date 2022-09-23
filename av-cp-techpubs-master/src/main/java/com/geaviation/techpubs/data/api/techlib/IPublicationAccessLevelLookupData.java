package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.techlib.PublicationAccessLevelLookupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IPublicationAccessLevelLookupData extends JpaRepository<PublicationAccessLevelLookupEntity, UUID> {
}
