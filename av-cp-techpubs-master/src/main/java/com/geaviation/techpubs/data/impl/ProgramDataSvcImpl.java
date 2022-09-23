package com.geaviation.techpubs.data.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.geaviation.dss.service.common.exception.TechnicalException;
import com.geaviation.techpubs.config.S3Config;
import com.geaviation.techpubs.data.api.IProgramData;
import com.geaviation.techpubs.data.factories.AmazonS3ClientFactory;
import com.geaviation.techpubs.data.s3.Util;
import com.geaviation.techpubs.data.api.techlib.IBookcaseVersionData;
import com.geaviation.techpubs.data.util.DataConstants;
import com.geaviation.techpubs.data.util.log.LogExecutionTime;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.*;
import com.geaviation.techpubs.models.techlib.BookcaseVersionEntity;
import com.geaviation.techpubs.services.util.StringUtils;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@Component
@Configuration
@ConfigurationProperties
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
public class ProgramDataSvcImpl implements IProgramData {

    private static final String CATEGORY2 = "category";
    private static final String KEY2 = "key";
    private static final String REVNBR = "revnbr";
    private static final String DOCTYPE = "doctype";
    private static final String MANUAL = "manual";
    private static final String SBLIST = "sblist";
    private static final String NOT_DOCNBR_OG_OR_DOCNBR_GNI_OR_DOCNBR_DA_OR_DOCNBR_COCS = "./*[not(@docnbr='og' or @docnbr='gni' or @docnbr='da' or @docnbr='cocs')]";
    private static final String CLOSE2 = "')]";
    private static final String OR_FILE = "' or @file='";
    private static final String AND_MFILE = "' and (@mfile='";
    private static final String DESCENDANT_OR_SELF_NODE_DOCNBR = "descendant-or-self::node()[@docnbr='";
    private static final String DESCENDANT_OR_SELF_NODE_FILE = "descendant-or-self::node()[*[@file]]";
    private static final String TOC2 = "toc";
    private static final String TITLE = "title";
    private static final String MULTIBROWSER = "multibrowser";
    private static final String TOC = "/toc/";
    private static final String NO = "N";
    private static final String YES = "Y";
    private static final String NODEID = "nodeid";
    private static final String TECHPUBS_TOC_PGMS = "/techpubs/toc/pgms/";
    private static final String FILE2 = "/file/";
    private static final String REVDATE = "revdate";
    private static final String NODE2 = "node";
    private static final String SUMMARY = "/summary/";
    private static final String DOCNBR = "docnbr";
    private static final String MANS = "/mans/";
    private static final String TECHPUBS_DOCS_PGMS = "/techpubs/techdocs/pgms/";
    private static final String SB = "sb";
    private static final String TYPE = "type";
    private static final String FILE = "file";
    private static final String SLASH_STAR = "./*";
    private static final String DESCENDANT_OR_SELF_NODE_NODEID = "descendant-or-self::node()[@nodeid='";
    private static final String SQ_CLOSE = "']";
    private static final String DOCNBR2 = "./*[@docnbr='";
    private static final String GEK114153 = "gek114153";
    private static final String GEK114152 = "gek114152";
    private static final String GEK112181 = "gek112181";
    private static final String GEK108753 = "gek108753";
    private static final String GEK109944 = "gek109944";
    private static final String GEK108750 = "gek108750";
    private static final String GEK112090 = "gek112090";
    private static final String GEK112080 = "gek112080";
    private static final String GEK112149 = "gek112149";

    private static final String VIEW_FILENAME = "VIEW_FILENAME";
    private static final String PARENTNODEID = "parentnodeid";

    private static final String FILE_NAME = "filename";
    private static final String CREATION_DATE = "creation_date";
    private static final Logger log = LogManager.getLogger(ProgramDataSvcImpl.class);

    @Autowired
    private AmazonS3ClientFactory amazonS3ClientFactory;

    @Autowired
    private AwsResourcesService awsResourcesService;

    @Autowired
    private S3Config s3Config;

    private Ehcache programDataCache;

    private static final String PROGRAMDATACACHENAME = "TechpubsProgramData";

    @Autowired
    @Qualifier("dataSourceTpsORA")
    private DataSource tpsDataSource;

    @Autowired
    private IBookcaseVersionData iBookcaseVersionData;

    private static final String SELECTPGMONLINEVERSIONSQL =
        "select bcv.bc_version" + "  from tps_getd_bookcase bc,"
            + "       tps_getd_bc_version bcv," + "       tps_getd_bc_version_status bcvs"
            + " where bc.getd_bc_seq_id = bcv.getd_bc_seq_id"
            + "   and bcv.getd_bc_version_status_seq_id = bcvs.getd_bc_version_status_seq_id"
            + "   and bcvs.bc_version_status_cd = 'online'" + "   and bc.bc_doc_num = ?";
    private static final String SELECTCATALOGITEMSQL =
        "select c.view_filename," + "       c.doc_nbr,"
            + "       c.rev_nbr," + "       to_char(c.rev_date,'YYYYMMDD') rev_date,"
            + "       c.title,"
            + "       c.category," + "       c.type," + "       c.toc_key"
            + "  from tps_getd_catalog c,"
            + "       tps_getd_bookcase bc," + "       tps_getd_manual_document md,"
            + "       tps_getd_catalog_type ct"
            + " where c.getd_bc_seq_id = bc.getd_bc_seq_id"
            + "   and c.getd_manual_doc_seq_id = md.getd_manual_doc_seq_id"
            + "   and c.getd_catalog_type_seq_id = ct.getd_catalog_type_seq_id"
            + "   and bc.bc_doc_num = ?"
            + "   and md.manual_doc_num = ?" + "   and ct.catalog_type = ? "
            + "order by c.doc_nbr, c.rev_nbr";

    private static final String SELECTTOCSQL = "select tps_getd_bookcase.bc_doc_num,"
        + "       tps_getd_manual_document.manual_doc_num," + "       tps_getd_toc.toc_title,"
        + "	   getd_toc_seq_id " + "  from tps_getd_toc," + "       tps_getd_bookcase,"
        + "  	   tps_getd_manual_document" + " where tps_getd_bookcase.bc_doc_num = ?"
        + "   and tps_getd_toc.getd_bc_seq_id = tps_getd_bookcase.getd_bc_seq_id"
        + "   and tps_getd_toc.getd_manual_doc_seq_id = tps_getd_manual_document.getd_manual_doc_seq_id";

    private static final String SELECTTOCDOCSSQL = "select tps_getd_bookcase.bc_doc_num,"
        + "       tps_getd_manual_document.manual_doc_num,"
        + "       tps_getd_toc_doc.getd_toc_doc_seq_id,"
        + "       tps_getd_toc_doc.view_filename," + "       tps_getd_toc_doc.toc_doc_title"
        + "  from tps_getd_toc_doc," + "  	 tps_getd_toc, " + "  	 tps_getd_bookcase, "
        + "  	 tps_getd_manual_document" + " where tps_getd_toc_doc.getd_toc_seq_id = ?"
        + "   and tps_getd_toc_doc.getd_toc_seq_id = tps_getd_toc.getd_toc_seq_id"
        + "   and tps_getd_toc.getd_bc_seq_id = tps_getd_bookcase.getd_bc_seq_id"
        + "   and tps_getd_toc.getd_manual_doc_seq_id = tps_getd_manual_document.getd_manual_doc_seq_id"
        + " order by tps_getd_toc_doc.toc_doc_title";

    private static final Map<String, EnumMap<SubSystem, String[]>> familyProgramMap; // Temporary
    private static final Map<String, EnumMap<SubSystem, String[]>> modelProgramMap; // Temporary

    public ProgramDataSvcImpl() {
        super();
        CacheManager cm = CacheManager
            .newInstance(this.getClass().getResource("/ehcacheTechpubs.xml"));
        this.programDataCache = cm.getCache(PROGRAMDATACACHENAME);
    }

    /**
     * Return a list of programs for a MDM family for the given subsystem (TD,CMM,FH,TP)
     *
     * @param family - MDM Family
     * @param subSystem - Subsystem (TD,CMM,FH,TP)
     * @return List<ProgramItemModel> - List of programs
     */
    @Override
    @LogExecutionTime
    public List<ProgramItemModel> getProgramItemsByFamily(String family, SubSystem subSystem) {
        List<ProgramItemModel> programList = new ArrayList<>();
        for (String program : getProgramsByFamily(family, subSystem)) {

            ProgramItemModel programItem = getProgramItem(program, subSystem);
            if (programItem != null) {
                programList.add(programItem);
            }
        }

        return programList;
    }

    /**
     * Return a list of programs for a MDM family for the given subsystem (TD,CMM,FH,TP) which are
     * present in the given program list
     *
     * @param family - MDM Family
     * @param subSystem - Subsystem (TD,CMM,FH,TP)
     * @return List<ProgramItemModel> - List of programs
     * @prarm List<String> - Restrict return programs to item in this list
     */
    @Override
    @LogExecutionTime
    public List<ProgramItemModel> getProgramItemsByFamily(String family, SubSystem subSystem,
        List<String> programList) {

        List<ProgramItemModel> programItemList = new ArrayList<>();
        for (String program : getProgramsByFamily(family, subSystem)) {
            if (programList.contains(program)) {
                ProgramItemModel programItem = getProgramItem(program, subSystem);
                if (programItem != null) {
                    programItemList.add(programItem);
                }
            }
        }

        return programItemList;
    }

    /**
     * Return a list of programs for a MDM model for the given subsystem (TD,CMM,FH,TP)
     *
     * @param model - MDM Model
     * @param subSystem - Subsystem (TD,CMM,FH,TP)
     * @return List<ProgramItemModel> - List of programs
     */
    @Override
    @LogExecutionTime
    public List<ProgramItemModel> getProgramItemsByModel(String model, SubSystem subSystem) {
        List<ProgramItemModel> programItemList = new ArrayList<>();
        for (String program : getProgramsByModel(model, subSystem)) {
            ProgramItemModel programItem = getProgramItem(program, subSystem);
            if (programItem != null) {
                programItemList.add(programItem);
            }
        }

        return programItemList;
    }

    /**
     * Return a list of programs for a MDM model for the given subsystem (TD,CMM,FH,TP) which are
     * present in the given program list
     *
     * @param model - MDM model
     * @param subSystem - Subsystem (TD,CMM,FH,TP)
     * @return List<ProgramItemModel> - List of programs
     * @prarm List<String> - Restrict return programs to item in this list
     */
    @Override
    @LogExecutionTime
    public List<ProgramItemModel> getProgramItemsByModel(String model, SubSystem subSystem,
        List<String> programList) {
        List<ProgramItemModel> programItemList = new ArrayList<>();
        for (String program : getProgramsByModel(model, subSystem)) {
            if (programList.contains(program)) {
                ProgramItemModel programItem = getProgramItem(program, subSystem);
                if (programItem != null) {
                    programItemList.add(programItem);
                }

            }
        }

        return programItemList;
    }

    /**
     * Return a list of programs for a list of roles for the given subsystem (TD,CMM,FH,TP)
     *
     * @param roleList - List of roles (e.g. techpubs_cf34-8e)
     * @param subSystem - Subsystem (TD,CMM,FH,TP)
     * @return List<ProgramItemModel> - List of programs
     */
    @Override
    @LogExecutionTime
    public List<String> getProgramsByRoles(List<String> roleList, SubSystem subSystem) {
        Set<String> programSet = new HashSet<>();

        if (roleList != null && !roleList.isEmpty()) {
            ResourceBundle roleProperties = ResourceBundle.getBundle("roles_td");
            for (String role : roleList) {
                processRoles(roleList, programSet, roleProperties, role);
            }
        }

        return new ArrayList<>(programSet);
    }

    /**
     *
     */
    private void processRoles(List<String> roleList, Set<String> programSet,
        ResourceBundle roleProperties,
        String role) {
        final long start = System.currentTimeMillis();

        log.debug("Method Invoked "
            + "processRoles(List<String> roleList, Set<String> programSet, ResourceBundle roleProperties,String role)"
            + role);

        try {
            String roleData = roleProperties.getString(role.toLowerCase());
            if (roleData != null) {
                if (roleData.indexOf(',') <= -1) {
                    final int idx = roleData.indexOf(':');
                    addProgramsByRoles(roleList, programSet, roleProperties, roleData, idx);
                } else {
                    for (String splitRoleData : roleData.split(",")) {
                        final int idx = splitRoleData.indexOf(':');
                        addProgramsByRoles(roleList, programSet, roleProperties, splitRoleData,
                            idx);
                    }
                }
            }
        } catch (MissingResourceException mre) {
            //don't change log level, this will be thrown for all roles we don't have in techpub
            log.debug(DataConstants.LOGGER_GETTPDOCS , mre);
        }

        final long executionTime = System.currentTimeMillis() - start;

        log.debug(
            "processRoles(List<String> roleList, Set<String> programSet, ResourceBundle roleProperties,String role)"
                + " executed in " + executionTime + "ms");
    }

    /**
     *
     */
    @LogExecutionTime
    private void addProgramsByRoles(List<String> roleList, Set<String> programSet,
        ResourceBundle roleProperties,
        String roleData, final int idx) {
        log.debug("Method Invoked " + "addProgramsByRoles " + System.currentTimeMillis());
        if (idx > -1) {
            String lrProgram = null;
            try {
                lrProgram = roleProperties.getString(roleData.substring(0, idx));
            } catch (MissingResourceException mre) {
                //don't change log level, this will be thrown for all roles we don't have in techpub
                log.debug(DataConstants.LOGGER_GETTPDOCS , mre);
            }
            if (lrProgram == null || !(
                roleList.contains(getRoleByProgram(roleProperties, lrProgram))
                    && iBookcaseVersionData.findOnlineBookcaseVersion(lrProgram) != null)) {
                programSet.add(roleData.substring(0, idx));
            }
        }
        log.debug("Method returned " + "addProgramsByRoles " + System.currentTimeMillis());
    }

    /**
     * Return a list of programs for a MDM family for the given subsystem (TD,CMM,FH,TP)
     *
     * @param family - MDM Family
     * @param subSystem - Subsystem (TD,CMM,FH,TP)
     * @return List<String> - List of programs
     */
    public List<String> getProgramsByFamily(String family, SubSystem subSystem) {
        Set<String> programSet = new HashSet<>();
        if (family == null) {
            for (Map.Entry<String, EnumMap<SubSystem, String[]>> entry : familyProgramMap
                .entrySet()) {
                programSet.addAll(Arrays.asList(entry.getValue().get(subSystem)));
            }
        } else {
            if (familyProgramMap.containsKey(family.toLowerCase())) {
                programSet.addAll(
                    Arrays.asList(familyProgramMap.get(family.toLowerCase()).get(subSystem)));
            }
        }
        return new ArrayList<>(programSet);
    }

    /**
     * Return a list of programs for a MDM model for the given subsystem (TD,CMM,FH,TP)
     *
     * @param model - MDM Model
     * @param subSystem - Subsystem (TD,CMM,FH,TP)
     * @return List<String> - List of programs
     */
    public List<String> getProgramsByModel(String model, SubSystem subSystem) {
        Set<String> programSet = new HashSet<>();

        if (modelProgramMap.containsKey(model.toLowerCase())) {
            programSet
                .addAll(Arrays.asList(modelProgramMap.get(model.toLowerCase()).get(subSystem)));
        }

        return new ArrayList<>(programSet);
    }

    /**
     * Return role for a given program
     *
     * @param bundle - resource bundle for properties file
     * @param program - program (e.g. gek108749)
     * @return String - Role
     */
    private String getRoleByProgram(ResourceBundle bundle, String program) {
        String role = null;

        for (String key : bundle.keySet()) {
            for (String splitRoleData : bundle.getString(key).split(",")) {
                if (splitRoleData.startsWith(program + ":")) {
                    role = key;
                    break;
                }
            }
            if (role != null) {
                break;
            }
        }
        log.debug(
            "Method return " + "getRoleByProgram(ResourceBundle bundle, String program) " + System
                .currentTimeMillis());

        return role;
    }

    /**
     * Return the TD Program for the SPM
     *
     * @return ProgramItemModel - SPM Program
     */
    public ProgramItemModel getSpmProgramItem() {
        return getProgramItem("gek108792", SubSystem.TD);
    }

    /**
     * Return the TD Program for the Honda-SPM
     *
     * @return ProgramItemModel - SPM Program
     */
    public ProgramItemModel getHondaSpmProgramItem() {
        return getProgramItem("gek119360", SubSystem.TD);
    }

    /**
     * Return the program object for a Subsystem
     *
     * @param program - Program name
     * @param subSystem - Subsystem (TD,CMM,FH,TP)
     * @return ProgramItemModel - Program
     */
    @Override
    @LogExecutionTime
    public ProgramItemModel getProgramItem(String program, SubSystem subSystem) {
        ProgramItemModel programItem = null;
        if (subSystem == SubSystem.TD) {
            net.sf.ehcache.Element element;
            if (programDataCache != null && (element = programDataCache.get(program)) != null) {
                programItem = (ProgramItemModel) element.getObjectValue();
                log.debug("***** Techpubs - Using Cached Program - " + program);
            } else {
                // Load Program
                log.debug("***** Techpubs - Loading Program - " + program);

                BookcaseVersionEntity versionedProgram = new BookcaseVersionEntity();
                versionedProgram.setTitle(program);
                try {
                    programItem = loadProgram(versionedProgram);
                } catch (Exception e) {
                    this.log.info(e.getMessage());
                }

                if (programItem != null) {
                    programItem.setSubSystem(subSystem);
                    // Add Program to cache
                    if (programDataCache != null) {
                        log.debug("***** Techpubs - Caching Program - " + program);
                        programDataCache.put(new net.sf.ehcache.Element(program, programItem));
                    }

                }
            }
        } else {
            programItem = new ProgramItemModel(program.split(":")[0]);
            if (subSystem == SubSystem.FH) {
                programItem.setParentProgramList(
                    new ArrayList<>(Arrays.asList((program.split(":")[1]).split("\\|"))));
            }
            programItem.setSubSystem(subSystem);
        }

        return programItem;
    }

    /**
     * Return the program object for a Subsystem
     *
     * @param versionedProgram - object containing program as its title and version as its version
     * @param subSystem - Subsystem (TD,CMM,FH,TP)
     * @return ProgramItemModel - Program
     */
    @Override
    @LogExecutionTime
    public ProgramItemModel getProgramItemVersion(BookcaseVersionEntity versionedProgram, SubSystem subSystem) {
        ProgramItemModel programItem = null;
        if (subSystem == SubSystem.TD) {
            net.sf.ehcache.Element element;
            if (programDataCache != null && (element = programDataCache.get(versionedProgram)) != null) {
                programItem = (ProgramItemModel) element.getObjectValue();
                log.debug("***** Techpubs - Using Cached Program - " + versionedProgram.getTitle());
            } else {
                // Load Program
                log.debug("***** Techpubs - Loading Program - " + versionedProgram.getTitle());
                try {
                    programItem = loadProgram(versionedProgram);
                } catch (Exception e) {
                    this.log.info(e.getMessage());
                }

                if (programItem != null) {
                    programItem.setSubSystem(subSystem);
                    // Add Program to cache
                    if (programDataCache != null) {
                        log.debug("***** Techpubs - Caching Program - " + versionedProgram.getTitle());
                        programDataCache.put(new net.sf.ehcache.Element(versionedProgram, programItem));
                    }

                }
            }
        } else {
            programItem = new ProgramItemModel(versionedProgram.getTitle().split(":")[0]);
            if (subSystem == SubSystem.FH) {
                programItem.setParentProgramList(
                        new ArrayList<>(Arrays.asList((versionedProgram.getTitle().split(":")[1]).split("\\|"))));
            }
            programItem.setSubSystem(subSystem);
        }

        return programItem;
    }

    /**
     * Load a TD Program into program object
     *
     * @param versionedProgram - object with the program as its title and version as its version. Version can be null. Title cannot be null;
     * @return ProgramItemModel - TD Program
     */
    private ProgramItemModel loadProgram(BookcaseVersionEntity versionedProgram)
        throws DocumentException, TechpubsException, IOException {
        ProgramItemModel programItem = null;

        //get online version if no version passed in
        String onlineVersion = StringUtils.isEmpty(versionedProgram.getBookcaseVersion()) ?
                iBookcaseVersionData.findOnlineBookcaseVersion(versionedProgram.getTitle()) : versionedProgram.getBookcaseVersion();

        if (onlineVersion != null) {
            programItem = new ProgramItemModel(versionedProgram.getTitle());


            programItem.setProgramOnlineVersion(onlineVersion);
            programItem.setLrProgramDocnbr(null);
            // Check roles.properties file to see if this program has a licensed
            // repair version
            String lrProgramDocnbr = null;
            ResourceBundle roleProperties = ResourceBundle.getBundle("roles_td");
            try {
                lrProgramDocnbr = roleProperties.getString(versionedProgram.getTitle());
            } catch (MissingResourceException mre) {
                //don't change log level, this will be thrown for all roles we don't have in techpub
                log.debug(DataConstants.LOGGER_GETTPDOCS , mre);
            }
            programItem.setLrProgramDocnbr(lrProgramDocnbr);
            loadProgramTOC(programItem);
            if (programItem.getTocRoot() != null) {
                loadProgramDownloadInfo(programItem);
                programItem.setLicensedProgram(
                    programItem.getTocRoot().selectSingleNode("./*[@docnbr='lrs']") != null);
            }
        } else {
            log.error("getProgramOnlinVersion is null for programDocnbr : " + versionedProgram.getTitle());
        }
        return programItem;
    }

    /**
     * Load a TD Program TOC into program object
     *
     * @param programItem - TD Program
     */
    @SuppressWarnings("unchecked")
    private void loadProgramTOC(ProgramItemModel programItem)
        throws IOException, DocumentException, TechpubsException {
        File tocFile = null;
        Element element = null;
        S3ObjectInputStream s3ObjectInputStream = null;

        try {
            String s3ObjKey = Util.createXmlFolderObjKey(programItem);
            S3Object s3Object = awsResourcesService
                .getS3Object(s3Config.getS3Bucket().getBucketName(), s3ObjKey);
            s3ObjectInputStream = s3Object.getObjectContent();
            element = ((new SAXReader()).read(s3ObjectInputStream).getRootElement());

        } catch (Exception ex) {
            this.log.error(ex.getMessage());
        }


        if (element != null) {
            programItem.setTocRoot(element);
            programItem.setDvdSbModel(programItem.getTocRoot().attributeValue("dvd_sb_model"));
            programItem.setDvdInfoTxt(programItem.getTocRoot().attributeValue("dvd_info_txt"));
            programItem.setDvdVersion(programItem.getTocRoot().attributeValue("dvd_version"));
            programItem.setTitle(programItem.getTocRoot().attributeValue(TITLE));
            int iNodeId = 0;
            programItem.getTocRoot().addAttribute(NODEID, String.valueOf(iNodeId++));
            // Load Manual Level
            for (Element childElement : (List<Element>) programItem.getTocRoot().elements()) {
                iNodeId = loadProgramTOCManual(programItem, childElement, iNodeId);

            }

            // populate TOC with manuals from database
            for (TocModel tocModel : getTocsByProgram(programItem.getProgramDocnbr())) {
                Element manualElement = programItem.getTocRoot().addElement(MANUAL);
                manualElement.addAttribute(DOCNBR, tocModel.getManualDocnbr());
                manualElement.addAttribute(TITLE, tocModel.getTitle());
                manualElement.addAttribute(REVDATE, "");
                manualElement.addAttribute(REVNBR, "");
                manualElement.addAttribute(TYPE, "");
                for (TocDocModel tocDocModel : tocModel.getTocDocList()) {
                    Element docElement = manualElement.addElement("doc");
                    docElement.addAttribute(FILE, tocDocModel.getViewFileName());
                    docElement.addAttribute(KEY2, tocDocModel.getViewFileName());
                    docElement.addAttribute(TITLE, tocDocModel.getTitle());
                    docElement.addAttribute(REVDATE, "");
                }
                iNodeId = loadProgramTOCManual(programItem, manualElement, iNodeId);
            }
        }
    }

    /**
     * Load a TD Program Manual from TOC into program object
     *
     * @param programItem - TD Program
     * @param element - TOC node
     * @param iNodeId - Current Node Id
     * @return int - Updated Node Id
     */
    @SuppressWarnings("unchecked")
    private int loadProgramTOCManual(ProgramItemModel programItem, Element element, int iNodeId) {
        Element parentElement = programItem.getTocRoot();
        int outNodeId = iNodeId;
        element.addAttribute(NODEID, String.valueOf(outNodeId++));
        element.addAttribute(PARENTNODEID, parentElement.attributeValue(NODEID));
        for (Element childElement : (List<Element>) element.elements()) {
            outNodeId = loadProgramTOCContent(element, element, childElement, outNodeId);
        }

        if (element.getName().equalsIgnoreCase(SBLIST)) {

            outNodeId = updateSBFromDB(programItem, outNodeId);
        }
        return outNodeId;
    }

    /**
     * Load a TD Program Content from TOC into program object
     *
     * @param parentElement - TOC node
     * @param manualElement - TOC node
     * @param element - TOC node
     * @param iNodeId - Current Node Id
     * @return int - Updated Node Id
     */
    @SuppressWarnings("unchecked")
    private int loadProgramTOCContent(Element parentElement, Element manualElement,
        Element element, int iNodeId) {
        int outNodeId = iNodeId;
        element.addAttribute(NODEID, String.valueOf(outNodeId++));
        element.addAttribute(PARENTNODEID, parentElement.attributeValue(NODEID));
        if (element.attributeValue(DOCNBR) == null) {
            element.addAttribute(DOCNBR, manualElement.attributeValue(DOCNBR));
        }
        for (Element childElement : (List<Element>) element.elements()) {
            outNodeId = loadProgramTOCContent(element, manualElement, childElement, outNodeId);
        }
        return outNodeId;
    }

    /**
     * Load a TD Download Information into program object
     *
     * @param programItem - TD Program
     */
    @SuppressWarnings("unchecked")
    private void loadProgramDownloadInfo(ProgramItemModel programItem) throws TechpubsException {
        Map<String, Map<String, String>> downloadTypeMap = new HashMap<>();

        File downloadFile = null;
        byte[] s3ObjectByteArray = null;
        String s3ObjKey = programItem.getProgramDocnbr() + "/program/xml/mygea_download.xml";
        S3Object s3Object = awsResourcesService
            .getS3Object(s3Config.getS3Bucket().getBucketName(), s3ObjKey);

        try {
            s3ObjectByteArray = IOUtils.toByteArray(s3Object.getObjectContent());

        } catch (IOException e) {
            log.error(DataConstants.GET_BINARY_RESOURCE_LOGGER + s3ObjKey + ")", e);
            throw new TechnicalException(
                DataConstants.GET_BINARY_RESOURCE_LOGGER + s3ObjKey + ")", e);

        } catch (NullPointerException e) {
            log.error(DataConstants.GET_BINARY_RESOURCE_LOGGER + s3ObjKey + ")", e);
            s3ObjectByteArray = null;
        }

        InputStream targetStream = new ByteArrayInputStream(s3ObjectByteArray);
        if (targetStream != null) {
            try {
                Element downloadRoot = (new SAXReader()).read(targetStream).getRootElement();
                for (Element downloadElement : (List<Element>) downloadRoot.elements("download")) {

                    Map<String, String> downloadManualMap = new LinkedHashMap<>();
                    for (Element manualElement : (List<Element>) downloadElement.elements(MANUAL)) {
                        downloadManualMap.put(manualElement.attributeValue(DOCNBR).toLowerCase(),
                            manualElement.attributeValue(TITLE));
                    }
                    String key = downloadElement.attributeValue(TYPE) + "|" + downloadElement
                        .attributeValue(DOCTYPE);
                    downloadTypeMap.put(key.toLowerCase(), downloadManualMap);

                }
            } catch (DocumentException e) {
                log.error("loadProgramDownloadInfo (" + downloadFile.getAbsolutePath() + ")", e);
                throw new TechnicalException(
                    "loadProgramDownloadInfo (" + downloadFile.getAbsolutePath() + ")", e);
            }
        }
        programItem.setDownloadTypeMap(downloadTypeMap);

    }

    /**
     * Update TD Program Object with Service Bulletins from Database
     *
     * @param programItem - TD Program
     * @param iNodeId - Current Node Id
     * @return int - Updated Node Id
     */
    @SuppressWarnings("unchecked")
    private int updateSBFromDB(ProgramItemModel programItem, int iNodeId) {
        int outNodeId = iNodeId;
        Element sblistElement = (Element) programItem.getTocRoot()
            .selectSingleNode("./sblist[@docnbr='sbs']");

        if (sblistElement != null) {
            List<Node> sbNodeList = sblistElement.selectNodes(".//sb[@file]");
            Map<String, Map<String, String>> catalogSbItemMap = getCatalogItemFilename(programItem.getProgramDocnbr(),
                "sbs", "sb");
            if (sbNodeList.size() > 0) {
                if (catalogSbItemMap.size() > 0) {
                    for (Node node : sbNodeList) {
                        Element element = (Element) node;
                        String filename = element.attributeValue("file");
                        if (filename != null && catalogSbItemMap.get(filename) != null) {
                            String category = catalogSbItemMap.get(filename).get("CATEGORY");
                            if (category != null) {
                                element.addAttribute("category", category);
                            }
                            catalogSbItemMap.remove(filename);
                        }
                    }
                }
            }

            if (catalogSbItemMap.size() > 0) {
                // Add Archived SBs
                Element archiveElement = sblistElement.addElement("sbarchive");
                archiveElement.addAttribute("parentnodeid", sblistElement.attributeValue("nodeid"));
                String archiveNodeId = String.valueOf(outNodeId++);
                archiveElement.addAttribute("nodeid", archiveNodeId);
                archiveElement.addAttribute("title", "Archived SBs");
                archiveElement.addAttribute("docnbr", "sbs");

                for (String key : catalogSbItemMap.keySet()) {
                    Map<String, String> catalogSBItem = catalogSbItemMap.get(key);
                    Element archiveSBElement = archiveElement.addElement("sb");
                    archiveSBElement.addAttribute("parentnodeid", archiveNodeId);
                    archiveSBElement.addAttribute("nodeid", String.valueOf(outNodeId++));
                    archiveSBElement.addAttribute("docnbr", "sbs");
                    archiveSBElement.addAttribute("file", catalogSBItem.get("VIEW_FILENAME"));
                    archiveSBElement.addAttribute("sbnbr", catalogSBItem.get("DOC_NBR"));
                    archiveSBElement.addAttribute("revnbr", catalogSBItem.get("REV_NBR"));
                    archiveSBElement.addAttribute("revdate", catalogSBItem.get("REV_DATE"));
                    archiveSBElement.addAttribute("title", catalogSBItem.get("TITLE"));
                    archiveSBElement.addAttribute("type", catalogSBItem.get("TYPE"));
                    archiveSBElement.addAttribute("key", catalogSBItem.get("TOC_KEY"));
                    String category = catalogSBItem.get("CATEGORY");
                    if (category != null) {
                        archiveSBElement.addAttribute("category", category);
                    }
                }
            }
        }
        return outNodeId;
    }

    /**
     *
     */
    private void removeSBFromDB(Map<String, Map<String, String>> catalogSbItemMap, Node node) {
        Element element = (Element) node;
        String filename = element.attributeValue(FILE);
        if (filename != null && catalogSbItemMap.get(filename) != null) {
            String category = catalogSbItemMap.get(filename).get("CATEGORY");
            if (category != null) {
                element.addAttribute(CATEGORY2, category);
            }
            catalogSbItemMap.remove(filename);
        }
    }

    /**
     * Retrieve all TD catalog item records from Database
     *
     * @param programNum - TD Program
     * @param manual - TD Manual
     * @param catalogType - TD Catelog Type (SB,TR,IC)
     * @return Map<String, Map < String, String>> - Catalog Items
     */
    public Map<String, Map<String, String>> getCatalogItemFilename(String programNum,
        String manual,
        String catalogType) {
        Map<String, Map<String, String>> catalogItemMap = new LinkedHashMap<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection connection = null;
        int pos = 1;

        try {
            connection = this.tpsDataSource.getConnection();
            pstmt = connection.prepareStatement(SELECTCATALOGITEMSQL);
            pstmt.setString(pos++, programNum.toLowerCase());
            pstmt.setString(pos++, manual.toLowerCase());
            pstmt.setString(pos++, catalogType.toLowerCase());
            rs = pstmt.executeQuery();
            rs.setFetchSize(500);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (rs != null && rs.next()) {
                extractFile(catalogItemMap, rs, metaData, columnCount);
            }
        } catch (SQLException e) {
            log.error("getCatalogItemFilename (SELECTCATALOGITEMSQL)", e);
            throw new TechnicalException("getCatalogItemFilename (SELECTCATALOGITEMSQL)", e);
        } finally {
            closeConnection(pstmt, rs, connection);
        }

        return catalogItemMap;
    }

    private void closeConnection(PreparedStatement pstmt, ResultSet rs, Connection connection) {
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                log.error(DataConstants.LOGGER_GETTPDOCS, e);
            }
        }
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.error(DataConstants.LOGGER_GETTPDOCS, e);
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                log.error(DataConstants.LOGGER_GETTPDOCS, e);
            }
        }
    }

    /**
     *
     */
    private void extractFile(Map<String, Map<String, String>> catalogItemMap, ResultSet rs,
        ResultSetMetaData metaData,
        int columnCount) throws SQLException {
        String filename = rs.getString(VIEW_FILENAME);
        if (filename != null) {
            Map<String, String> row = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                row.put(metaData.getColumnLabel(i), rs.getString(i));
            }
            catalogItemMap.put(filename, row);
        }
    }

    /**
     * Retrieve TOC Entries by program
     *
     * @return List<TocModel> - List of TOC Entries
     */
    public List<TocModel> getTocsByProgram(String programDocNum) {
        List<TocModel> tocList = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
            Connection connection = null;
            int pos = 1;

            try {
                connection = this.tpsDataSource.getConnection();
                pstmt = connection.prepareStatement(SELECTTOCSQL);
                pstmt.setString(pos++, programDocNum.toLowerCase());
                rs = pstmt.executeQuery();
                rs.setFetchSize(500);
            while (rs != null && rs.next()) {
                TocModel tocModel = new TocModel();
                tocModel.setProgramDocnbr(programDocNum);
                tocModel.setManualDocNbr(rs.getString("MANUAL_DOC_NUM"));
                tocModel.setTitle(rs.getString("TOC_TITLE"));
                tocModel.setTocDocList(getTocDocsByManual(rs.getString("getd_toc_seq_id")));
                tocList.add(tocModel);
            }
        } catch (SQLException e) {
            log.error("getTocsByProgram (SELECTTOCSQL)", e);
            throw new TechnicalException("getTocsByProgram (SELECTTOCSQL)", e);
        } finally {
            closeConnection(pstmt, rs, connection);
        }

        return tocList;
    }

    /**
     * Retrieve TD Documents for manual in TOC
     *
     * @param tocSeqId - TOC Node #
     * @return List<TocDocModel> - List of TOC Entries
     */
    private List<TocDocModel> getTocDocsByManual(String tocSeqId) {
        List<TocDocModel> tocDocList = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection connection = null;
        int pos = 1;

        try {
            connection = this.tpsDataSource.getConnection();
            pstmt = connection.prepareStatement(SELECTTOCDOCSSQL);
            pstmt.setString(pos++, tocSeqId);
            rs = pstmt.executeQuery();
            rs.setFetchSize(500);
            while (rs != null && rs.next()) {
                TocDocModel tocDocModel = new TocDocModel();
                tocDocModel.setProgramDocnbr(rs.getString("BC_DOC_NUM"));
                tocDocModel.setManualDocnbr(rs.getString("MANUAL_DOC_NUM"));
                tocDocModel.setViewFileName(rs.getString(VIEW_FILENAME));
                tocDocModel.setTitle(rs.getString("TOC_DOC_TITLE"));
                tocDocList.add(tocDocModel);
            }
        } catch (SQLException e) {
            log.error("getTocDocsByManual (SELECTTOCDOCSSQL)", e);
            throw new TechnicalException("getTocDocsByManual (SELECTTOCDOCSSQL)", e);
        } finally {
            closeConnection(pstmt, rs, connection);
        }

        return tocDocList;
    }

    /**************************************************************************
     * Temporary Mappings pending the development of a common 'repository'
     **************************************************************************
     * Static maps to support translation from MDM Family/Model to each
     * subsystem's (TechDocs(TD),CMM(CMM),Fleet Highlights(FH), Technical
     * Presentaions(TP)) engine programs/families Each Item will have a EnumMap
     * with the following format: {Array of Tech Docs (TD) Engine Program(s)},
     * {Array of CMM (CMM) Engine Family(s)}, {Array of Fleet Highlites (FH)
     * Engine Program(s):Parent Programs}, {Array of Technical Presentations
     * (TP) Engine Program(s)}
     **************************************************************************/
    static {
        EnumMap<SubSystem, String[]> enumMap;

        Map<String, EnumMap<SubSystem, String[]>> tmpFamilyMap = new HashMap<>();
        // CF34
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD,
            new String[]{GEK112149, GEK112080, GEK112090, GEK108750, "gek108751", "gek108752",
                "gek112030"});
        tmpFamilyMap.put("cf34", enumMap);
        // CF6
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD,
            new String[]{"gek108745", "gek108744", "gek108747-01", "gek108747-10", "gek108746",
                "gek108746-02",
                "gek108746-20", "gek108746-30", "gek113959", /* "gek117479", */"gek113960",
                /* "gek117480", */"gek108748"});
        tmpFamilyMap.put("cf6", enumMap);
        // CFE
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{});
        tmpFamilyMap.put("cfe", enumMap);
        // CFM56
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{});
        tmpFamilyMap.put("cfm56", enumMap);
        // CJ610
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{});
        tmpFamilyMap.put("cj610", enumMap);
        // CT58
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{GEK109944});
        tmpFamilyMap.put("ct58", enumMap);
        // CT7
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD,
            new String[]{"gek114154", GEK108753, "gek114155", "gek112212", GEK112181,
                "gek114154-01",
                "gek114154-02", "gek114154-03", "gek114154-10", "gek114154-20",
                "gek114154-30", "gek112212-01", "gek112212-10"/* ,"gek117478" */});
        tmpFamilyMap.put("ct7", enumMap);
        // GE90
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{"gek108749", "gek108786"});
        tmpFamilyMap.put("ge90", enumMap);
        // GE9X
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{"gek131810"});
        tmpFamilyMap.put("ge9x", enumMap);
        // GENX
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD,
            new String[]{"gek112865", "gek114118", "gek112865_lr", "gek114118_lr"});
        tmpFamilyMap.put("genx", enumMap);
        // GP7000
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{});
        tmpFamilyMap.put("gp7000", enumMap);
        // H80
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{});
        tmpFamilyMap.put("h80", enumMap);
        // CT64
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{GEK114152, GEK114153});
        tmpFamilyMap.put("ct64", enumMap);
        // HF
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{"gek112121", "gek112110"});
        tmpFamilyMap.put("hf", enumMap);

        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{"gek112059", "gek112060"});
        tmpFamilyMap.put("passport", enumMap);

        familyProgramMap = Collections.unmodifiableMap(tmpFamilyMap);

        Map<String, EnumMap<SubSystem, String[]>> tmpModelMap = new HashMap<>();
        // CF34-1
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{GEK112149});
        tmpModelMap.put("cf34-1", enumMap);
        // CF34-10
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{GEK112080, GEK112090});
        tmpModelMap.put("cf34-10", enumMap);
        // CF34-10A
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{GEK112090});
        tmpModelMap.put("cf34-10a", enumMap);
        // CF34-10E
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{GEK112080});
        tmpModelMap.put("cf34-10e", enumMap);
        // CF34-3A
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{GEK112149});
        tmpModelMap.put("cf34-3a", enumMap);
        // CF34-3A1
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{GEK108750, "gek108751"});
        tmpModelMap.put("cf34-3a1", enumMap);
        tmpModelMap.put("cf34-3", enumMap);
        // CF34-3B
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{GEK108750});
        tmpModelMap.put("cf34-3b", enumMap);
        // CF34-8C
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{"gek108752"});
        tmpModelMap.put("cf34-8c", enumMap);
        // CF34-8E
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{"gek112030"});
        tmpModelMap.put("cf34-8e", enumMap);
        // CF6-45
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{});
        tmpModelMap.put("cf6-45", enumMap);
        // CF6-50
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{"gek108745"});
        tmpModelMap.put("cf6-50", enumMap);
        // CF6-6
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{"gek108744"});
        tmpModelMap.put("cf6-6", enumMap);
        // CF6-80A
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{"gek108747-01", "gek108747-10"});
        tmpModelMap.put("cf6-80a", enumMap);
        // CF6-80C2
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD,
            new String[]{"gek108746", "gek108746-02", "gek108746-20", "gek108746-30",
                "gek113959", "gek117479", "gek113960", "gek117480"});
        tmpModelMap.put("cf6-80c2", enumMap);
        // CF6-80E
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{"gek108748"});
        tmpModelMap.put("cf6-80e", enumMap);
        // CFE738
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{});
        tmpModelMap.put("cfe738", enumMap);
        // CFM56-2A
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{});
        tmpModelMap.put("cfm56-2a", enumMap);
        // CFM56-2B
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{});
        tmpModelMap.put("cfm56-2b", enumMap);
        // CFM56-2C
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{});
        tmpModelMap.put("cfm56-2c", enumMap);
        // CFM56-3
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{});
        tmpModelMap.put("cfm56-3", enumMap);
        // CFM56-5A
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{});
        tmpModelMap.put("cfm56-5a", enumMap);
        // CFM56-5B
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{});
        tmpModelMap.put("cfm56-5b", enumMap);
        // CFM56-5C
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{});
        tmpModelMap.put("cfm56-5c", enumMap);
        // CFM56-7B
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{});
        tmpModelMap.put("cfm56-7b", enumMap);
        // cj610
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{});
        tmpModelMap.put("cj610", enumMap);
        // CT58-100
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{GEK109944});
        tmpModelMap.put("ct58-100", enumMap);
        // CT58-110
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{GEK109944});
        tmpModelMap.put("ct58-110", enumMap);
        // CT58-140
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{GEK109944});
        tmpModelMap.put("ct58-140", enumMap);
        // CT7-2
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD,
            new String[]{"gek114154", "gek114154-01", "gek114154-02", "gek114154-03",
                "gek114154-10", "gek114154-20", "gek114154-30"});
        tmpModelMap.put("ct7-2", enumMap);
        // CT7-5
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{GEK108753});
        tmpModelMap.put("ct7-5", enumMap);
        // CT7-5A
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{GEK108753});
        tmpModelMap.put("ct7-5a", enumMap);
        // CT7-6
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{"gek114155"});
        tmpModelMap.put("ct7-6", enumMap);
        // CT7-7
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{GEK108753});
        tmpModelMap.put("ct7-7", enumMap);
        // CT7-8
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{"gek112212", "gek112212-01", "gek112212-10"});
        tmpModelMap.put("ct7-8", enumMap);
        // CT7-9
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{GEK112181});
        tmpModelMap.put("ct7-9", enumMap);
        // CT7-9B
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{GEK112181});
        tmpModelMap.put("ct7-9b", enumMap);
        // CT7-9C
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{GEK112181});
        tmpModelMap.put("ct7-9c", enumMap);
        // CT7-TS
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{"gek117478"});
        tmpModelMap.put("ct7-ts", enumMap);
        // GE90
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{"gek108749"});
        tmpModelMap.put("ge90", enumMap);
        tmpModelMap.put("ge90-90", enumMap);
        // GE90-100
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{"gek108786"});
        tmpModelMap.put("ge90-100", enumMap);
        // GENX-1B
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{"gek112865", "gek112865_lr"});
        tmpModelMap.put("genx-1b", enumMap);
        // GENX-2B
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{"gek114118", "gek114118_lr"});
        tmpModelMap.put("genx-2b", enumMap);
        // GE9X
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{"gek131810"});
        tmpModelMap.put("ge9x-105", enumMap);
        // GP7200
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{});
        tmpModelMap.put("gp7200", enumMap);
        // H80
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{});
        tmpModelMap.put("h80", enumMap);
        // CT64-1
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{"gek117477"});
        tmpModelMap.put("ct64-1", enumMap);
        // CT64-820-3
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{GEK114152});
        tmpModelMap.put("ct64-820-3", enumMap);
        // CT64-820-4
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{GEK114153});
        tmpModelMap.put("ct64-820-4", enumMap);
        // CT64-820
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{GEK114152, GEK114153});
        tmpModelMap.put("ct64-820", enumMap);
        // HF120
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{"gek112110", "gek112121"});
        tmpModelMap.put("hf120", enumMap);

        // PASSPORT20
        enumMap = new EnumMap<>(SubSystem.class);
        enumMap.put(SubSystem.TD, new String[]{"gek112059", "gek112060"});
        tmpModelMap.put("passport20", enumMap);

        modelProgramMap = Collections.unmodifiableMap(tmpModelMap);
    }

    /**
     * Returns a list of Tech Doc (Subsystem = TD) TOC entries a specific program
     *
     * @param programItem the program item
     * @return List<TocItemModel> - List of TD TOC entries
     */
    @SuppressWarnings("unchecked")
    @Override
    @LogExecutionTime
    public List<TocItemNodeModel> getContentByProgram(ProgramItemModel programItem) {
        List<TocItemNodeModel> tocItemNodeList = new ArrayList<>();

        if (programItem != null) {
            Element tocRootElement = programItem.getTocRoot();
            // Exclude "cocs","gni","og","da"
            for (Node node : (List<Node>) tocRootElement
                .selectNodes(NOT_DOCNBR_OG_OR_DOCNBR_GNI_OR_DOCNBR_DA_OR_DOCNBR_COCS)) {
                Element element = (Element) node;
                TocItemNodeModel tocItemNode = new TocItemNodeModel();
                tocItemNode.setProgramItem(programItem);
                tocItemNode.setType(NODE2);
                tocItemNode.setRevdate(element.attributeValue(REVDATE));
                tocItemNode.setResourceUri(TECHPUBS_TOC_PGMS + programItem.getProgramDocnbr() + MANS
                    + element.attributeValue(DOCNBR) + TOC + element.attributeValue(NODEID));
                tocItemNode.setTitle(element.attributeValue(TITLE));
                tocItemNode.setToctitle(
                    (element.attributeValue(TOC2) != null ? element.attributeValue(TOC2)
                        : element.attributeValue(TITLE)));
                tocItemNode.setChildren(element.nodeCount() > 0 ? YES : NO);
                tocItemNode.setMultibrowser((element.attributeValue(MULTIBROWSER) == null ? NO
                    : element.attributeValue(MULTIBROWSER).toUpperCase()));
                tocItemNodeList.add(tocItemNode);
            }
        }

        return tocItemNodeList;
    }

    /**
     * Returns a list of Tech Doc (Subsystem = TD) TOC entries a specific program and manual
     *
     * @param programItem the program item
     * @param manual the manual
     * @param parentnodeid the parentnodeid
     * @return List<TocItemModel> - List of TD TOC entries
     */
    @SuppressWarnings("unchecked")
    @Override
    @LogExecutionTime
    public List<TocItemNodeModel> getContentByTocNodeId(ProgramItemModel programItem, String manual,
        String parentnodeid) {
        List<TocItemNodeModel> tocItemNodeList = new ArrayList<>();

        if (programItem != null) {
            Element manualElement = (Element) programItem.getTocRoot()
                .selectSingleNode(DOCNBR2 + manual + SQ_CLOSE);
            Element tocSubElement = (Element) programItem.getTocRoot()
                .selectSingleNode(DESCENDANT_OR_SELF_NODE_NODEID + parentnodeid + SQ_CLOSE);
            for (Node node : (List<Node>) tocSubElement.selectNodes(SLASH_STAR)) {
                Element element = (Element) node;
                String fileName = element.attributeValue(FILE);
                boolean isFile = fileName != null && fileName.length() > 0;
                String docType = isFile ? element.getName() : "";
                String summaryUri = "";
                if (SB.equalsIgnoreCase(docType)) {
                    docType += element.attributeValue(TYPE);
                    if (element.nodeCount() > 0) {
                        summaryUri = TECHPUBS_DOCS_PGMS + programItem.getProgramDocnbr() + MANS
                            + element.attributeValue(DOCNBR) + SUMMARY + fileName;
                    }
                }
                String nodeType =
                    isFile ? fileName.substring(fileName.lastIndexOf('.') + 1).toUpperCase()
                        : NODE2;
                TocItemNodeModel tocItemNode = new TocItemNodeModel();
                tocItemNode.setProgramItem(programItem);
                tocItemNode.setType(nodeType);
                tocItemNode.setDoctype(docType);
                tocItemNode.setSummaryUri(summaryUri);
                tocItemNode.setRevdate(element.attributeValue(REVDATE));
                if (isFile) {
                    tocItemNode
                        .setResourceUri(TECHPUBS_DOCS_PGMS + programItem.getProgramDocnbr() + MANS
                            + element.attributeValue(DOCNBR) + FILE2 + fileName);
                    tocItemNode.setChildren(NO);
                } else {
                    tocItemNode
                        .setResourceUri(TECHPUBS_TOC_PGMS + programItem.getProgramDocnbr() + MANS
                            + element.attributeValue(DOCNBR) + TOC + element
                            .attributeValue(NODEID));
                    tocItemNode.setChildren(element.nodeCount() > 0 ? YES : NO);
                }
                tocItemNode.setMultibrowser(
                    (manualElement != null ? (manualElement.attributeValue(MULTIBROWSER) == null
                        ? NO : manualElement.attributeValue(MULTIBROWSER).toUpperCase()) : NO));
                tocItemNode.setTitle(element.attributeValue(TITLE));
                tocItemNode.setToctitle(
                    (element.attributeValue(TOC2) != null ? element.attributeValue(TOC2)
                        : element.attributeValue(TITLE)));
                tocItemNodeList.add(tocItemNode);
            }
        }

        return tocItemNodeList;
    }

    /**
     * Returns a list of Tech Doc (Subsystem = TD) TOC entries a specific program and manual
     *
     * @param programItem the program item
     * @param manual the manual
     * @param file the file
     * @return List<TocItemModel> - List of TD TOC entries
     */
    @Override
    @LogExecutionTime
    public List<TocItemNodeModel> getContentByDocFile(ProgramItemModel programItem, String manual,
        String file) {
        List<TocItemNodeModel> tocItemNodeList = new ArrayList<>();

        if (programItem != null) {
            Element manualElement = (Element) programItem.getTocRoot()
                .selectSingleNode(DOCNBR2 + manual + SQ_CLOSE);
            Element fileElement = (Element) programItem.getTocRoot().selectSingleNode(
                DESCENDANT_OR_SELF_NODE_DOCNBR + manual + AND_MFILE + file + OR_FILE + file
                    + CLOSE2);
            Element parent = fileElement;
            while (parent != null && parent.getParent() != null) {
                String fileName = parent.attributeValue(FILE);
                boolean isFile = fileName != null && fileName.length() > 0;
                String docType = isFile ? parent.getName() : "";
                String summaryUri = "";
                if (SB.equalsIgnoreCase(docType)) {
                    docType += parent.attributeValue(TYPE);
                    if (parent.nodeCount() > 0) {
                        summaryUri = TECHPUBS_DOCS_PGMS + programItem.getProgramDocnbr() + MANS
                            + parent.attributeValue(DOCNBR) + SUMMARY + fileName;
                    }
                }
                String nodeType =
                    isFile ? fileName.substring(fileName.lastIndexOf('.') + 1).toUpperCase()
                        : NODE2;
                TocItemNodeModel tocItemNode = new TocItemNodeModel();
                tocItemNode.setProgramItem(programItem);
                tocItemNode.setType(nodeType);
                tocItemNode.setDoctype(docType);
                tocItemNode.setSummaryUri(summaryUri);
                tocItemNode.setRevdate(parent.attributeValue(REVDATE));
                if (isFile) {
                    tocItemNode
                        .setResourceUri(TECHPUBS_DOCS_PGMS + programItem.getProgramDocnbr() + MANS
                            + parent.attributeValue(DOCNBR) + FILE2 + fileName);
                    tocItemNode.setChildren(NO);
                } else {
                    tocItemNode
                        .setResourceUri(TECHPUBS_TOC_PGMS + programItem.getProgramDocnbr() + MANS
                            + parent.attributeValue(DOCNBR) + TOC + parent.attributeValue(NODEID));
                    tocItemNode.setChildren(parent.nodeCount() > 0 ? YES : NO);
                }
                tocItemNode.setMultibrowser(
                    (manualElement != null ? (manualElement.attributeValue(MULTIBROWSER) == null
                        ? NO : manualElement.attributeValue(MULTIBROWSER).toUpperCase()) : NO));
                tocItemNode.setTitle(parent.attributeValue(TITLE));
                tocItemNode
                    .setToctitle((parent.attributeValue(TOC2) != null ? parent.attributeValue(TOC2)
                        : parent.attributeValue(TITLE)));
                tocItemNodeList.add(0, tocItemNode);
                parent = parent.getParent();
            }
        }

        return tocItemNodeList;
    }

    /**
     * Returns a list of Tech Doc (Subsystem = TD) TOC entries a specific program and manual
     *
     * @param programItem the program item
     * @param manual the manual
     * @return List<TocItemModel> - List of TD TOC entries
     */
    @SuppressWarnings("unchecked")
    @Override
    @LogExecutionTime
    public List<TocItemModel> getContentByManual(ProgramItemModel programItem, String manual) {
        List<TocItemModel> tocItemList = new ArrayList<>();

        if (programItem != null) {
            Element manualElement = (Element) programItem.getTocRoot()
                .selectSingleNode(DOCNBR2 + manual + SQ_CLOSE);
            for (Node node : (List<Node>) manualElement.selectNodes(DESCENDANT_OR_SELF_NODE_FILE)) {
                Element element = (Element) node;
                TocItemModel tocItem = new TocItemModel();
                tocItem.setProgramItem(programItem);
                tocItem.setManualdocnbr(manual);
                tocItem.setManualtitle(manualElement.attributeValue(TITLE));
                tocItem.setManualrevdate(manualElement.attributeValue(REVDATE));
                tocItem.setId(element.attributeValue(NODEID));
                tocItem.setTitle(element.attributeValue(TITLE));
                tocItem.setToctitle(
                    (element.attributeValue(TOC2) != null ? element.attributeValue(TOC2)
                        : element.attributeValue(TITLE)));
                tocItemList.add(tocItem);
            }
        }

        return tocItemList;
    }
}

	
