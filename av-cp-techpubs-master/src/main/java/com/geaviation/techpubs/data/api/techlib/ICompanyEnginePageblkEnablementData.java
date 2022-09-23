package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.techlib.CompanyEnginePageblkEnablementEntity;
import com.geaviation.techpubs.models.techlib.CompanyEnginePageblkEnablementEntityPK;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ICompanyEnginePageblkEnablementData extends
    JpaRepository<CompanyEnginePageblkEnablementEntity, CompanyEnginePageblkEnablementEntityPK> {

    @Query("SELECT c FROM CompanyEnginePageblkEnablementEntity c WHERE c.icaoCode = :icaoCode AND c.engineModel = :engineModel")
    List<CompanyEnginePageblkEnablementEntity> findAllByIcaoCodeandEngineModel
        (@Param("icaoCode") String icaoCode, @Param("engineModel") String engineModel);
}
