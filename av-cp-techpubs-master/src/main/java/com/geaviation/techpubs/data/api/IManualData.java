package com.geaviation.techpubs.data.api;

import com.geaviation.techpubs.models.DocumentItemModel;
import com.geaviation.techpubs.models.ManualItemModel;
import com.geaviation.techpubs.models.ProgramItemModel;
import java.util.List;

public interface IManualData {

    /**
     * Gets the manuals by program.
     *
     * @param program the program
     * @return the manuals by program
     */
    List<ManualItemModel> getManualsByProgram(ProgramItemModel program);

    /**
     * Gets the document item.
     *
     * @param programItem the program item
     * @param manual the manual
     * @param filename the filename
     * @return the document item
     */
    DocumentItemModel getDocumentItem(ProgramItemModel programItem, String manual, String filename);

    /**
     * Gets the documents by parent toc id.
     *
     * @param programItem the program item
     * @param manual the manual
     * @param parentnodeid the parentnodeid
     * @return the documents by parent toc id
     */
    List<DocumentItemModel> getDocumentsByParentTocId(ProgramItemModel programItem, String manual,
        String parentnodeid);

}
