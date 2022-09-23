package com.geaviation.techpubs.data.api;

import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.DVDInfoModel;
import com.geaviation.techpubs.models.ProgramItemModel;
import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.models.TocItemModel;
import com.geaviation.techpubs.models.TocItemNodeModel;
import com.geaviation.techpubs.models.TocModel;
import com.geaviation.techpubs.models.techlib.BookcaseVersionEntity;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.dom4j.DocumentException;

/**
 * The Interface IProgramData.
 */
public interface IProgramData {

    /**
     * Gets the program items by family.
     *
     * @param family the family
     * @param subSystem the sub system
     * @return the program items by family
     */
    List<ProgramItemModel> getProgramItemsByFamily(String family, SubSystem subSystem)
        throws DocumentException, TechpubsException, IOException;

    /**
     * Gets the program items by family.
     *
     * @param family the family
     * @param subSystem the sub system
     * @param programList the program list
     * @return the program items by family
     */
    List<ProgramItemModel> getProgramItemsByFamily(String family, SubSystem subSystem,
        List<String> programList) throws DocumentException, TechpubsException, IOException;

    /**
     * Gets the program items by model.
     *
     * @param model the model
     * @param subSystem the sub system
     * @return the program items by model
     */
    List<ProgramItemModel> getProgramItemsByModel(String model, SubSystem subSystem);

    /**
     * Gets the program items by model.
     *
     * @param model the model
     * @param subSystem the sub system
     * @param programList the program list
     * @return the program items by model
     */
    List<ProgramItemModel> getProgramItemsByModel(String model, SubSystem subSystem,
        List<String> programList);

    /**
     * Gets the programs by roles.
     *
     * @param roleList the role list
     * @param subSystem the sub system
     * @return the programs by roles
     */
    List<String> getProgramsByRoles(List<String> roleList, SubSystem subSystem);

    /**
     * Gets the spm program item.
     *
     * @return the spm program item
     */
    ProgramItemModel getSpmProgramItem();

    /**
     * Gets the program item.
     *
     * @param program the program
     * @param subSystem the sub system
     * @return the program item
     */
    ProgramItemModel getProgramItem(String program, SubSystem subSystem)
        throws DocumentException, TechpubsException, IOException;

    /**
     * Gets the program item with the specified version.
     *
     * @param versionedProgram object with the program as its title and version as its version
     * @param subSystem the sub system
     * @return the program item
     */
    ProgramItemModel getProgramItemVersion(BookcaseVersionEntity versionedProgram, SubSystem subSystem);

    /**
     * Gets the content by program.
     *
     * @param programItem the program item
     * @return the content by program
     */
    List<TocItemNodeModel> getContentByProgram(ProgramItemModel programItem);

    /**
     * Gets the content by toc node id.
     *
     * @param programItem the program item
     * @param manual the manual
     * @param parentnodeid the parentnodeid
     * @return the content by toc node id
     */
    List<TocItemNodeModel> getContentByTocNodeId(ProgramItemModel programItem, String manual,
        String parentnodeid);

    /**
     * Gets the content by doc file.
     *
     * @param programItem the program item
     * @param manual the manual
     * @param file the file
     * @return the content by doc file
     */
    List<TocItemNodeModel> getContentByDocFile(ProgramItemModel programItem, String manual,
        String file);

    /**
     * Gets the content by manual.
     *
     * @param programItem the program item
     * @param manual the manual
     * @return the content by manual
     */
    List<TocItemModel> getContentByManual(ProgramItemModel programItem, String manual);

    /**
     * Gets the Honda Spm program item.
     *
     * @return the Honda Spm program item
     */
    ProgramItemModel getHondaSpmProgramItem();

    /**
     * Return a list of programs for a MDM family for the given subsystem (TD,CMM,FH,TP)
     *
     * @param family - MDM Family
     * @param subSystem - Subsystem (TD,CMM,FH,TP)
     * @return List<String> - List of programs
     */
    List<String> getProgramsByFamily(String family, SubSystem subSystem);

    /**
     * Return a list of programs for a MDM model for the given subsystem (TD,CMM,FH,TP)
     *
     * @param model - MDM Model
     * @param subSystem - Subsystem (TD,CMM,FH,TP)
     * @return List<String> - List of programs
     */
    List<String> getProgramsByModel(String model, SubSystem subSystem);

    List<TocModel> getTocsByProgram(String programItem);

    Map<String, Map<String, String>> getCatalogItemFilename(String programNum,
        String manual,
        String catalogType);

}
