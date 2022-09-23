package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.techlib.dto.EnabledPageblkFileDto;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

@Repository
public class StoredProcedureRepository implements IStoredProcedureRepository {
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public List<EnabledPageblkFileDto> getbookcaseenabledpgblksforicao(String icaoCode, String bookKey, String bookcaseKey, String bookcaseVersion){
    StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("techlib.getbookcaseenabledpgblksforicao");

    // Set the parameters of the stored procedure.
    String icaoParam = "icao";
    storedProcedure.registerStoredProcedureParameter(icaoParam, String.class, ParameterMode.IN);
    storedProcedure.setParameter(icaoParam, icaoCode);

    String bookcaseKeyParam = "bookcasekey";
    storedProcedure.registerStoredProcedureParameter(bookcaseKeyParam, String.class, ParameterMode.IN);
    storedProcedure.setParameter(bookcaseKeyParam, bookcaseKey);

    String bookcaseVersionParam = "bookcaseversion";
    storedProcedure.registerStoredProcedureParameter(bookcaseVersionParam, String.class, ParameterMode.IN);
    storedProcedure.setParameter(bookcaseVersionParam, bookcaseVersion);

    String bookKeyParam = "bookkey";
    storedProcedure.registerStoredProcedureParameter(bookKeyParam, String.class, ParameterMode.IN);
    storedProcedure.setParameter(bookKeyParam, bookKey);

    List<Object[]> storedProcedureResults = storedProcedure.getResultList();

    return storedProcedureResults.stream().map(result -> new EnabledPageblkFileDto(
        (String) result[0],
        (String) result[1],
        (String) result[2],
        (boolean) result[3]
    )).collect(Collectors.toList());

  }
}
