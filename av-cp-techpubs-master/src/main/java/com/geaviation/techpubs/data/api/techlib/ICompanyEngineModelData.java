package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.techlib.CompanyEngineModelEntity;
import com.geaviation.techpubs.models.techlib.CompanyEngineModelEntityPK;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ICompanyEngineModelData extends JpaRepository<CompanyEngineModelEntity, CompanyEngineModelEntityPK> {
    List<CompanyEngineModelEntity> findByIcaoCodeIgnoreCase(String icaoCode);

    @Query("SELECT (COUNT(bookEnable) + COUNT(pageEnable) + COUNT(techLvEnable)) > 0 "
        + "FROM CompanyEngineModelEntity compEngModel "
        + "LEFT OUTER JOIN CompanyEngineBookEnablementEntity bookEnable ON compEngModel.icaoCode = bookEnable.icaoCode AND compEngModel.engineModel = bookEnable.engineModel "
        + "LEFT OUTER JOIN CompanyEnginePageblkEnablementEntity pageEnable ON compEngModel.icaoCode = pageEnable.icaoCode AND compEngModel.engineModel = pageEnable.engineModel "
        + "LEFT OUTER JOIN CompanyEngineTechlvEnablementEntity techLvEnable ON compEngModel.icaoCode = techLvEnable.icaoCode AND compEngModel.engineModel = techLvEnable.engineModel "
        + "WHERE compEngModel.icaoCode = :icaoCode AND compEngModel.engineModel = :engModel")
    Boolean isPreviouslyEnabled(@Param("icaoCode") String icaoCode, @Param("engModel") String engModel);
}
