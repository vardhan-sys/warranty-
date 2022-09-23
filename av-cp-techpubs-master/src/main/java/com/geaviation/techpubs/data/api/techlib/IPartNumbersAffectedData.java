package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.techlib.PartNumbersAffectedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IPartNumbersAffectedData extends JpaRepository<PartNumbersAffectedEntity, UUID> {

    Optional<List<PartNumbersAffectedEntity>> findByPartNumberContaining(@Param("partNumber") String partNumber);
}
