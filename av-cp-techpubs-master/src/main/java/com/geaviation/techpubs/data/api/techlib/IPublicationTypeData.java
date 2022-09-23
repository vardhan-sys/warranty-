package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.techlib.PublicationTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPublicationTypeData extends JpaRepository<PublicationTypeEntity, String> {
}
