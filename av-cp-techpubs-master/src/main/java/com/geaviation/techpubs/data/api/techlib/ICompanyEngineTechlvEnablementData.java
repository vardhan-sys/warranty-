package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.techlib.CompanyEngineTechlvEnablementEntity;
import com.geaviation.techpubs.models.techlib.CompanyEngineTechlvEnablementEntityPK;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ICompanyEngineTechlvEnablementData extends
    JpaRepository<CompanyEngineTechlvEnablementEntity, CompanyEngineTechlvEnablementEntityPK> {

    @Query("SELECT c FROM CompanyEngineTechlvEnablementEntity c " +
               "WHERE c.icaoCode = :icaoCode AND c.engineModel = :engineModel " +
               "AND c.bookcaseKey = :bookcaseKey")
    List<CompanyEngineTechlvEnablementEntity> findAllByIcaoCodeEngineModelandBookcase
        (@Param("icaoCode") String icaoCode, @Param("engineModel") String engineModel,
         @Param("bookcaseKey") String bookcaseKey);
}
