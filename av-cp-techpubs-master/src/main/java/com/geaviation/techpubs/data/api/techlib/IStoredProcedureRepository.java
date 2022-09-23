package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.techlib.dto.EnabledPageblkFileDto;
import java.util.List;

public interface IStoredProcedureRepository {

  List<EnabledPageblkFileDto> getbookcaseenabledpgblksforicao(String icaoCode, String bookcaseKey, String bookcaseVersion, String bookKey);

}
