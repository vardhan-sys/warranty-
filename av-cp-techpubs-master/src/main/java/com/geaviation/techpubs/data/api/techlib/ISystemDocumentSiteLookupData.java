package com.geaviation.techpubs.data.api.techlib;


import com.geaviation.techpubs.models.techlib.SystemDocumentSiteLookupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ISystemDocumentSiteLookupData extends JpaRepository<SystemDocumentSiteLookupEntity, UUID>  {
}
