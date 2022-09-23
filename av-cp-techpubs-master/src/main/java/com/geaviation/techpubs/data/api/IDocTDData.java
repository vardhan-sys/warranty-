package com.geaviation.techpubs.data.api;

import com.geaviation.techpubs.models.DocumentItemModel;
import com.geaviation.techpubs.models.ProgramItemModel;
import java.util.List;

/**
 * The Interface IDocTDData.
 */
public interface IDocTDData extends IDocSubSystemData {

    /**
     * Gets the documents by program type.
     *
     * @param programItemModel the program item model
     * @param type the type
     * @return the documents by program type
     */
    List<DocumentItemModel> getDocumentsByProgramType(ProgramItemModel programItemModel,
        String type);
}
