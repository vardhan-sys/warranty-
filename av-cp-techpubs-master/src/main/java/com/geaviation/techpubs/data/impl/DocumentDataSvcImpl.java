package com.geaviation.techpubs.data.impl;

import com.geaviation.dss.service.common.exception.TechnicalException;
import com.geaviation.techpubs.data.api.IDocumentData;
import com.geaviation.techpubs.data.util.log.LogExecutionTime;
import com.geaviation.techpubs.models.DocumentItemICCatalogModel;
import com.geaviation.techpubs.models.DocumentItemLRCatalogModel;
import com.geaviation.techpubs.models.DocumentItemModel;
import com.geaviation.techpubs.models.DocumentItemSBCatalogModel;
import com.geaviation.techpubs.models.DocumentItemSourceCatalogModel;
import com.geaviation.techpubs.models.DocumentItemTRCatalogModel;
import com.geaviation.techpubs.models.ManualItemModel;
import com.geaviation.techpubs.models.ProgramItemModel;
import com.geaviation.techpubs.services.util.AppConstants;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
@ConfigurationProperties
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
public class DocumentDataSvcImpl implements IDocumentData {

    private static final String AND_TPS_GETD_MANUAL_DOCUMENT_MANUAL_DOC_NUM = " and tps_getd_manual_document.manual_doc_num = ?";

    private static final String CATALOG_TYPE_IS = "tps_getd_catalog_type.catalog_type = ?";

    private static final String OR_CATALOG_TYPE_SMM = " or tps_getd_catalog_type.catalog_type = '" + AppConstants.DOCUMENT_TYPE_SMM.toLowerCase() + "'";

    private static final String FILE = "/file/";

    private static final String TOC_KEY = "toc_key";

    private static final String TOC_KEY2 = TOC_KEY;

    private static final String MANS = "/mans/";

    private static final String MANS2 = MANS;

    private static final String MANS22 = MANS2;

    private static final String DOC_NBR = "doc_nbr";

    private static final String RELEASE_DATE = "release_date";

    private static final String DVD_FILENAME = "dvd_filename";

    private static final String REV_NBR = "rev_nbr";

    private static final String MANS_SBS_FILE = "/mans/sbs/file/";

    private static final String TECHPUBS_DOCS_PGMS = "/techpubs/techdocs/pgms/";

    private static final String ALERT_COVER = "alert-cover";

    private static final String TYPE = "type";

    private static final String ALERT = "alert";

    private static final String VIEW_FILENAME = "view_filename";

    private static final String CLOSE_SQ = "']";

    private static final String RELENDDATE = "relenddate";

    private static final String RELSTARTDATE = "relstartdate";

    private static final String REVENDDATE = "revenddate";

    private static final String ORDER_BY_DOC_NBR_REV_NBR_DESC = " order by doc_nbr, rev_nbr desc";

    private static final String SB = "sb";

    private static final String AND_TPS_GETD_CATALOG_SOURCE_FILENAME_IS_NOT_NULL = " and tps_getd_catalog.source_filename is not null";

    private static final String SOURCE = "source";

    private static final String AND_TPS_GETD_CATALOG_DVD_FILENAME_IS_NOT_NULL = " and tps_getd_catalog.dvd_filename is not null";

    private static final String DVD = "dvd";

    private static final String AND_LOWER_NVL_TPS_GETD_CATALOG_ACTIVE_IND_N_N = " and lower(NVL(tps_getd_catalog.active_ind,'N')) = 'n'";

    private static final String N2 = "N";

    private static final String AND_LOWER_NVL_TPS_GETD_CATALOG_ACTIVE_IND_N_Y = " and lower(NVL(tps_getd_catalog.active_ind,'N')) = 'y'";

    private static final String Y = "Y";

    private static final String AND_TO_NUMBER_REV_NBR_TO_NUMBER4 = " and to_number(rev_nbr) <= to_number(?)";

    private static final String AND_TO_NUMBER_REV_NBR_TO_NUMBER3 = " and to_number(rev_nbr) >= to_number(?)";

    private static final String AND_RELEASE_DATE_TO_DATE_YYYY_MM_DD_1 = " and release_date < to_date(?,'YYYY-MM-DD')+1";

    private static final String AND_RELEASE_DATE_TO_DATE_YYYY_MM_DD = " and release_date >= to_date(?,'YYYY-MM-DD')";

    private static final String AND_REV_DATE_TO_DATE_YYYY_MM_DD_1 = " and rev_date < to_date(?,'YYYY-MM-DD')+1";

    private static final String AND_REV_DATE_TO_DATE_YYYY_MM_DD = " and rev_date >= to_date(?,'YYYY-MM-DD')";

    private static final String REVSTARTDATE = "revstartdate";

    private static final String REVSTARTDATE2 = REVSTARTDATE;

    private static final String AND_LOWER_TYPE = " and lower(type) = ?";

    private static final String OR_TYPE_SMM = " or type = 'smm'";
    private static final String AND_CATEGORY = " and category = ?";

    private static final String SBTYPE = "sbtype";

    private static final String CATEGORY = "category";

    private static final String CATEGORY2 = CATEGORY;

    private static final String MANUAL = "manual";

    private static final String SOURCE_FILENAME = "source_filename";

    private static final String SOURCE_FILENAME2 = SOURCE_FILENAME;

    private static final String CATALOG_FILENAME = "catalog_filename";

    private static final String DOC = "doc";

    private static final String PUBLISHED_TO_CWC_DATE = "published_to_cwc_date";

    private static final String DOLLAR1_2_3 = "$1-$2-$3";

    private static final String D_4_D_2_D_2 = "(\\d{4})(\\d{2})(\\d{2})";

    private static final String REV_DATE = "rev_date";

    private static final String CATALOG_KEY = "catalog_key";

    private static final String CATALOG_TYPE = "catalog_type";

    private static final String MANUAL_DOC_NUM = "manual_doc_num";

    private static final String DOCNBR2 = "./*[@docnbr='";

    private static final String GET_CATALOG_DOCUMENTS = "getCatalogDocuments (SELECTCATALOGDOCUMENTS)";

    private static final String MANUALDOCNBR = "manualdocnbr";

    private static final String CATALOGTYPE = "catalogtype";

    private static final String ACTIVEIND = "activeind";

    private static final String PREVIOUSREVNBR = "previousrevnbr";

    private static final String CURRENTREVNBR = "currentrevnbr";

    private static final String AND_LOWER_NVL_ACTIVE_IND_N_LOWER = " and lower(NVL(active_ind,'n')) = lower(?)";

    private static final String AND_TO_NUMBER_REV_NBR_TO_NUMBER2 = AND_TO_NUMBER_REV_NBR_TO_NUMBER4;

    private static final String AND_TO_NUMBER_REV_NBR_TO_NUMBER = AND_TO_NUMBER_REV_NBR_TO_NUMBER3;

    private static final String AND_1_1_OR_IS_NULL = " and (1=1 or ? is null)";

    private static final String HTM = "htm";

    private static final String HTML2 = "HTML";

    private static final String HTML = "html";

    private static final String N = N2;

    private static final String MULTIBROWSER = "multibrowser";

    private static final String REVNBR = "revnbr";

    private static final String REVDATE = "revdate";

    private static final String TITLE = "title";

    private static final String DOCNBR = "docnbr";

    private static final String GET_CATALOG_FILE_DOCUMENTS = "getCatalogFileDocuments (SELECTCATALOGFILEDOCUMENTS)";

    private static final Logger log = LogManager.getLogger(DocumentDataSvcImpl.class);

    @Autowired
    @Qualifier("dataSourceTpsORA")
    private DataSource tpsDataSource;

    // Returns the Catalog Documents for TD Download
    private static final String SELECTCATALOGDOCUMENTS = "select tps_getd_bookcase.bc_doc_num,"
        + "       tps_getd_manual_document.manual_doc_num,"
        + "	   tps_getd_catalog_type.catalog_type,"
        + "       getd_catalog_seq_id," + "	   view_filename, " + "       catalog_key, "
        + "       doc_nbr, "
        + "       rev_nbr, " + "       to_char(rev_date,'YYYYMMDD') as rev_date, "
        + "       to_char(release_date,'YYYYMMDD') as release_date," + "       title, "
        + "       NVL(active_ind,'N') as active_ind, "
        + "       tps_getd_manual_document.manual_type,"
        + "       ata_num, " + "       pgblk_num, " + "       pgblk_type, " + "       category, "
        + "       type, "
        + "       source_filename, " + "       dvd_filename, "
        + "       NVL(email_flag,'N') as email_flag, "
        + "       limited_distribution_ind, " + "       recurring_sb_ind, "
        + "       recurring_sb_value, "
        + "       airworthiness_directive, "
        + "       to_char(tps_getd_catalog.last_updated_date,'YYYYMMDD') as last_updated_date, "
        + "       to_char(tps_getd_catalog.published_to_cwc_date,'YYYYMMDD') as published_to_cwc_date, "
        + "       toc_key " + "  from tps_getd_catalog, " + "       tps_getd_bookcase, "
        + "       tps_getd_catalog_type," + "       tps_getd_manual_document"
        + " where tps_getd_bookcase.bc_doc_num = ?"
        + "   and tps_getd_bookcase.getd_bc_seq_id = tps_getd_catalog.getd_bc_seq_id"
        + "   and tps_getd_catalog_type.getd_catalog_type_seq_id = tps_getd_catalog.getd_catalog_type_seq_id"
        + "   and tps_getd_catalog.getd_manual_doc_seq_id = tps_getd_manual_document.getd_manual_doc_seq_id";

    // Returns the Catalog File Documents for TD Download
    private static final String SELECTCATALOGFILEDOCUMENTS = "select tps_getd_bookcase.bc_doc_num,"
        + "       tps_getd_catalog_type.catalog_type,"
        + "       tps_getd_manual_document.manual_doc_num,"
        + "       tps_getd_catalog.getd_catalog_seq_id," + "       tps_getd_catalog.catalog_key,"
        + "       tps_getd_catalog.rev_nbr," + "       tps_getd_catalog.title,"
        + "       to_char(tps_getd_catalog.rev_date,'YYYYMMDD') as rev_date,"
        + "       to_char(tps_getd_catalog.release_date,'YYYYMMDD') as release_date,"
        + "       tps_getd_catalog_file.catalog_filename,"
        + "       tps_getd_catalog_file.getd_catalog_file_seq_id,"
        + "	      to_char(tps_getd_catalog_file.last_updated_date,'YYYYMMDD') as last_updated_date, "
        + "	      to_char(tps_getd_catalog.published_to_cwc_date,'YYYYMMDD') as published_to_cwc_date "
        + "  from tps_getd_catalog_file," + "       tps_getd_catalog, "
        + "       tps_getd_bookcase, "
        + "       tps_getd_catalog_type," + "       tps_getd_manual_document"
        + " where tps_getd_bookcase.bc_doc_num = ?"
        + "   and tps_getd_catalog_type.catalog_type = ?"
        + "   and tps_getd_manual_document.manual_doc_num = ?"
        + "   and tps_getd_bookcase.getd_bc_seq_id = tps_getd_catalog.getd_bc_seq_id"
        + "   and tps_getd_catalog_type.getd_catalog_type_seq_id = tps_getd_catalog.getd_catalog_type_seq_id"
        + "   and tps_getd_catalog.getd_manual_doc_seq_id = tps_getd_manual_document.getd_manual_doc_seq_id"
        + "   and tps_getd_catalog.getd_catalog_seq_id = tps_getd_catalog_file.getd_catalog_seq_id";

    @Override
    @LogExecutionTime
    public List<DocumentItemModel> getCatalogFileDocuments(ProgramItemModel programItem,
        String downloadtype,
        Map<String, String> criteriaMap) {
        List<DocumentItemModel> documentItemList = new ArrayList<>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection connection = null;
        int pos = 1;

        // Create SQL Statement
        StringBuilder sqlStmtBuffer = new StringBuilder(SELECTCATALOGFILEDOCUMENTS);
        // Process Additional Criteria

        sqlStmtBuffer
            .append(criteriaMap.get(CURRENTREVNBR) == null ? AND_1_1_OR_IS_NULL
                : AND_TO_NUMBER_REV_NBR_TO_NUMBER);
        sqlStmtBuffer.append(
            criteriaMap.get(PREVIOUSREVNBR) == null ? AND_1_1_OR_IS_NULL
                : AND_TO_NUMBER_REV_NBR_TO_NUMBER2);
        sqlStmtBuffer
            .append(criteriaMap.get(ACTIVEIND) == null ? AND_1_1_OR_IS_NULL
                : AND_LOWER_NVL_ACTIVE_IND_N_LOWER);

        try {
            connection = this.tpsDataSource.getConnection();
            pstmt = connection.prepareStatement(sqlStmtBuffer.toString());
            pstmt.setString(pos++, programItem.getProgramDocnbr().toLowerCase());
            pstmt.setString(pos++, criteriaMap.get(CATALOGTYPE).toLowerCase());

            pstmt.setString(pos++, criteriaMap.get(MANUALDOCNBR));
            pstmt.setString(pos++, criteriaMap.get(CURRENTREVNBR));
            pstmt.setString(pos++, criteriaMap.get(PREVIOUSREVNBR));
            pstmt.setString(pos++, criteriaMap.get(ACTIVEIND));

            rs = pstmt.executeQuery();
            while (rs != null && rs.next()) {
                try {
                    documentItemList.add(createSourceDocument(programItem, downloadtype,
                        criteriaMap.get(CATALOGTYPE).toLowerCase(), rs));
                } catch (Exception e){
                    String sourceFileName = DOC.equals(criteriaMap.get(CATALOGTYPE).toLowerCase()) ? rs.getString(SOURCE_FILENAME2)
                        : rs.getString(CATALOG_FILENAME);
                    log.error("Unable to create DocumentItemSourceCatalogModel for source file " + sourceFileName + "in book " + criteriaMap.get(MANUALDOCNBR));
                }
            }
        } catch (SQLException e) {
            log.error(GET_CATALOG_FILE_DOCUMENTS, e);
            throw new TechnicalException(GET_CATALOG_FILE_DOCUMENTS, e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    log.warn(GET_CATALOG_FILE_DOCUMENTS, e);
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    log.warn(GET_CATALOG_FILE_DOCUMENTS, e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.warn(GET_CATALOG_FILE_DOCUMENTS, e);
                }
            }
        }

        return documentItemList;
    }

    private DocumentItemModel createSourceDocument(ProgramItemModel programItemModel,
        String downloadtype,
        String catalogType, ResultSet rs) throws SQLException {
        DocumentItemSourceCatalogModel documentItemSource = new DocumentItemSourceCatalogModel();

        documentItemSource.setProgramItem(programItemModel);
        documentItemSource.setDownloadtype(downloadtype);
        Element manualElement = (Element) programItemModel.getTocRoot()
            .selectSingleNode(DOCNBR2 + rs.getString(MANUAL_DOC_NUM) + CLOSE_SQ);
        documentItemSource
            .setManualItem(
                (manualElement == null ? null : createManualItem(programItemModel, manualElement)));
        documentItemSource.setManualdocnbr(rs.getString(MANUAL_DOC_NUM));
        documentItemSource.setId(rs.getString(CATALOG_KEY));
        documentItemSource.setRevisionDate((rs.getString(REV_DATE) != null
            ? rs.getString(REV_DATE).replaceFirst(D_4_D_2_D_2, DOLLAR1_2_3) : null));
        documentItemSource.setPubcwcdate((rs.getString(PUBLISHED_TO_CWC_DATE) != null
            ? rs.getString(PUBLISHED_TO_CWC_DATE).replaceFirst(D_4_D_2_D_2, DOLLAR1_2_3) : null));
        documentItemSource.setTitle(
            (DOC.equals(catalogType) ? rs.getString(TITLE) : rs.getString(CATALOG_FILENAME)));
        documentItemSource.setCatalogkey(rs.getString(CATALOG_KEY));
        documentItemSource.setSourcefilename(
            (DOC.equals(catalogType) ? rs.getString(SOURCE_FILENAME2)
                : rs.getString(CATALOG_FILENAME)));
        documentItemSource.setFileType(getFileType(documentItemSource.getDownloadfilename()));
        documentItemSource.setType(MANUAL);

        return documentItemSource;
    }

    private ManualItemModel createManualItem(ProgramItemModel programItem, Element manualElement) {
        ManualItemModel manualItem = new ManualItemModel();
        try {
            manualItem.setProgramItem(programItem);
            manualItem.setManualdocnbr(manualElement.attributeValue(DOCNBR));

            manualItem.setTitle(manualElement.attributeValue(TITLE));
            manualItem.setRevisiondate(manualElement.attributeValue(REVDATE));
            manualItem.setRevisionnumber(manualElement.attributeValue(REVNBR));
            manualItem.setMultibrowser((manualElement.attributeValue(MULTIBROWSER) == null ? N
                : manualElement.attributeValue(MULTIBROWSER).toUpperCase()));

        } catch(Exception e){
            String bookcase  =  programItem != null ? programItem.getProgramDocnbr() : null;
            String book  = manualElement != null ? manualElement.attributeValue(DOCNBR) : null;
            log.error("Unable to create ManualItem for bookcase " + bookcase + " book " + book + " bookcase " + programItem.getProgramDocnbr());

            throw e;
        }

        return manualItem;
    }

    private String getFileType(String filename) {
        String fileType = "";

        if (filename != null) {
            String fileExtension = FilenameUtils.getExtension(filename);
            if (HTML.equalsIgnoreCase(fileExtension) || HTM.equalsIgnoreCase(fileExtension)) {
                fileType = HTML2;
            } else {
                fileType = (fileExtension == null ? "" : fileExtension.toUpperCase());
            }
        }

        return fileType;
    }

    @Override
    @LogExecutionTime
    public List<DocumentItemModel> getCatalogDocuments(ProgramItemModel programItem,
        String downloadtype,
        Map<String, String> criteriaMap) {
        List<DocumentItemModel> documentItemList = new ArrayList<>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection connection = null;
        int pos = 1;

        StringBuilder sqlStmtBuffer = getSQLCatalogDocuments(downloadtype, criteriaMap);

        try {
            connection = this.tpsDataSource.getConnection();

            pstmt = connection.prepareStatement(sqlStmtBuffer.toString());
            pstmt.setString(pos++, programItem.getProgramDocnbr().toLowerCase());
            pstmt.setString(pos++, criteriaMap.get(CATALOGTYPE).toLowerCase());

            pstmt.setString(pos++, criteriaMap.get(MANUALDOCNBR));
            pstmt.setString(pos++, criteriaMap.get(CATEGORY2));
            pstmt.setString(pos++,
                (criteriaMap.get(SBTYPE) == null ? null : criteriaMap.get(SBTYPE).toLowerCase()));
            pstmt.setString(pos++, criteriaMap.get(REVSTARTDATE2));
            pstmt.setString(pos++, criteriaMap.get(REVENDDATE));
            pstmt.setString(pos++, criteriaMap.get(RELSTARTDATE));
            pstmt.setString(pos++, criteriaMap.get(RELENDDATE));
            pstmt.setString(pos++, criteriaMap.get(CURRENTREVNBR));
            pstmt.setString(pos++, criteriaMap.get(PREVIOUSREVNBR));
            pstmt.setString(pos++, criteriaMap.get("previousrevnbrlow"));

            rs = pstmt.executeQuery();
            while (rs != null && rs.next()) {
                try {
                    DocumentItemModel documentItem = null;
                    if (SB.equalsIgnoreCase(criteriaMap.get(CATALOGTYPE))) {
                        documentItem = createSBDocument(programItem, downloadtype, rs);
                    } else if ("tr".equals(criteriaMap.get(CATALOGTYPE))) {
                        documentItem = createTRDocument(programItem, downloadtype, rs);
                    } else if ("ic".equalsIgnoreCase(criteriaMap.get(CATALOGTYPE))) {
                        documentItem = createICDocument(programItem, downloadtype, rs);
                    } else if ("lr".equalsIgnoreCase(criteriaMap.get(CATALOGTYPE))) {
                        documentItem = createLRDocument(programItem, downloadtype, rs);
                    } else if (DOC.equalsIgnoreCase(criteriaMap.get(CATALOGTYPE))) {
                        documentItem = createSourceDocument(programItem, downloadtype,
                            criteriaMap.get(CATALOGTYPE).toLowerCase(), rs);
                    }
                    documentItemList.add(documentItem);
                } catch(Exception e) {
                    String onlineFileName = rs.getString(VIEW_FILENAME);
                    log.error("Unable to create DocumentItemSourceCatalogModel for source file " + onlineFileName + "in book " + criteriaMap.get(MANUALDOCNBR) + " bookcase " + programItem.getProgramDocnbr());
                }

            }
        } catch (SQLException e) {
            log.error(GET_CATALOG_DOCUMENTS, e);
            throw new TechnicalException(GET_CATALOG_DOCUMENTS, e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    log.warn(GET_CATALOG_DOCUMENTS, e);
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    log.warn(GET_CATALOG_DOCUMENTS, e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.warn(GET_CATALOG_DOCUMENTS, e);
                }
            }
        }

        return documentItemList;
    }

    private StringBuilder getSQLCatalogDocuments(String downloadtype,
        Map<String, String> criteriaMap) {
        // Create SQL Statement
        StringBuilder sqlStmtBuffer = new StringBuilder(SELECTCATALOGDOCUMENTS);


        sqlStmtBuffer.append(AppConstants.LR.equals(criteriaMap.get(CATALOGTYPE)) ? " and (" + CATALOG_TYPE_IS + OR_CATALOG_TYPE_SMM + ")"
            : " and " + CATALOG_TYPE_IS);
        sqlStmtBuffer.append(criteriaMap.get(MANUALDOCNBR) == null ? AND_1_1_OR_IS_NULL
            : AND_TPS_GETD_MANUAL_DOCUMENT_MANUAL_DOC_NUM);
        sqlStmtBuffer
            .append(criteriaMap.get(CATEGORY2) == null ? AND_1_1_OR_IS_NULL : AND_CATEGORY);
        sqlStmtBuffer.append(criteriaMap.get(SBTYPE) == null ? AND_1_1_OR_IS_NULL : AND_LOWER_TYPE);

        if(criteriaMap.get(SBTYPE) == AppConstants.LR) sqlStmtBuffer.append(OR_TYPE_SMM);

       sqlStmtBuffer
            .append(criteriaMap.get(REVSTARTDATE2) == null ? AND_1_1_OR_IS_NULL
                : AND_REV_DATE_TO_DATE_YYYY_MM_DD);
        sqlStmtBuffer
            .append(criteriaMap.get(REVENDDATE) == null ? AND_1_1_OR_IS_NULL
                : AND_REV_DATE_TO_DATE_YYYY_MM_DD_1);
        sqlStmtBuffer.append(
            criteriaMap.get(RELSTARTDATE) == null ? AND_1_1_OR_IS_NULL
                : AND_RELEASE_DATE_TO_DATE_YYYY_MM_DD);
        sqlStmtBuffer.append(
            criteriaMap.get(RELENDDATE) == null ? AND_1_1_OR_IS_NULL
                : AND_RELEASE_DATE_TO_DATE_YYYY_MM_DD_1);
        sqlStmtBuffer
            .append(criteriaMap.get(CURRENTREVNBR) == null ? AND_1_1_OR_IS_NULL
                : AND_TO_NUMBER_REV_NBR_TO_NUMBER3);
        sqlStmtBuffer.append(
            criteriaMap.get(PREVIOUSREVNBR) == null ? AND_1_1_OR_IS_NULL
                : AND_TO_NUMBER_REV_NBR_TO_NUMBER4);
        sqlStmtBuffer.append(
            criteriaMap.get("previousrevnbrlow") == null ? AND_1_1_OR_IS_NULL
                : " and to_number(rev_nbr) >= to_number(?)");
        if (Y.equalsIgnoreCase(criteriaMap.get(ACTIVEIND))) {
            sqlStmtBuffer.append(AND_LOWER_NVL_TPS_GETD_CATALOG_ACTIVE_IND_N_Y);
        } else if (N2.equalsIgnoreCase(criteriaMap.get(ACTIVEIND))) {
            sqlStmtBuffer.append(AND_LOWER_NVL_TPS_GETD_CATALOG_ACTIVE_IND_N_N);
        }
        if (DVD.equalsIgnoreCase(downloadtype)) {
            sqlStmtBuffer.append(AND_TPS_GETD_CATALOG_DVD_FILENAME_IS_NOT_NULL);
        } else if (SOURCE.equalsIgnoreCase(downloadtype)) {
            sqlStmtBuffer.append(AND_TPS_GETD_CATALOG_SOURCE_FILENAME_IS_NOT_NULL);
        }

        if (SB.equalsIgnoreCase(criteriaMap.get(CATALOGTYPE))) {
            sqlStmtBuffer.append(ORDER_BY_DOC_NBR_REV_NBR_DESC); // required if
        }
        // SB need
        // filtering
        return sqlStmtBuffer;
    }

    private DocumentItemModel createSBDocument(ProgramItemModel programItemModel,
        String downloadtype, ResultSet rs)
        throws SQLException {
        DocumentItemSBCatalogModel documentItemSB = new DocumentItemSBCatalogModel();

        documentItemSB.setProgramItem(programItemModel);
        documentItemSB.setDownloadtype(downloadtype);
        Element manualElement = (Element) programItemModel.getTocRoot()
            .selectSingleNode(DOCNBR2 + rs.getString(MANUAL_DOC_NUM) + CLOSE_SQ);
        documentItemSB.setManualItem(createManualItem(programItemModel, manualElement));
        documentItemSB.setFilename(rs.getString(VIEW_FILENAME));
        documentItemSB.setFileType(getFileType(documentItemSB.getFilename()));
        documentItemSB.setCategory(rs.getString(CATEGORY2));
        documentItemSB.setSbalert(
            ALERT.equalsIgnoreCase(rs.getString(TYPE)) || ALERT_COVER
                .equalsIgnoreCase(rs.getString(TYPE)) ? true
                : false);
        documentItemSB.setId(rs.getString(DOC_NBR));
        String revisionDate = (rs.getString(REV_DATE) != null
            ? rs.getString(REV_DATE).replaceFirst(D_4_D_2_D_2, DOLLAR1_2_3) : null);
        documentItemSB.setRevisionDate(revisionDate);
        documentItemSB
            .setResourceUri(TECHPUBS_DOCS_PGMS + programItemModel.getProgramDocnbr() + MANS_SBS_FILE
                + documentItemSB.getFilename());
        documentItemSB.setReleaseDate(revisionDate);
        documentItemSB.setTitle(rs.getString(TITLE));
        documentItemSB.setToctitle(rs.getString(TITLE));
        documentItemSB.setVersion(rs.getString(REV_NBR));
        documentItemSB.setCatalogkey(rs.getString(CATALOG_KEY));
        documentItemSB.setSourcefilename(rs.getString(SOURCE_FILENAME2));
        documentItemSB.setDvdfilename(rs.getString(DVD_FILENAME));

        return documentItemSB;
    }

    private DocumentItemModel createTRDocument(ProgramItemModel programItemModel,
        String downloadtype, ResultSet rs)
        throws SQLException {
        DocumentItemTRCatalogModel documentItemTR = new DocumentItemTRCatalogModel();

        documentItemTR.setProgramItem(programItemModel);
        documentItemTR.setDownloadtype(downloadtype);
        Element manualElement = (Element) programItemModel.getTocRoot()
            .selectSingleNode(DOCNBR2 + rs.getString(MANUAL_DOC_NUM) + CLOSE_SQ);
        documentItemTR.setManualItem(createManualItem(programItemModel, manualElement));
        documentItemTR.setFilename(rs.getString(VIEW_FILENAME));
        documentItemTR.setFileType(getFileType(documentItemTR.getFilename()));
        documentItemTR.setId(rs.getString(DOC_NBR));
        documentItemTR.setRevisionDate((rs.getString(REV_DATE) != null
            ? rs.getString(REV_DATE).replaceFirst(D_4_D_2_D_2, DOLLAR1_2_3) : null));
        documentItemTR.setReleaseDate((rs.getString(RELEASE_DATE) != null
            ? rs.getString(RELEASE_DATE).replaceFirst(D_4_D_2_D_2, DOLLAR1_2_3) : null));
        documentItemTR
            .setResourceUri(TECHPUBS_DOCS_PGMS + programItemModel.getProgramDocnbr() + MANS22
                + rs.getString(MANUAL_DOC_NUM) + FILE + documentItemTR.getFilename());
        documentItemTR.setTitle(rs.getString(TITLE));
        documentItemTR.setToctitle(rs.getString(TITLE));
        documentItemTR.setCatalogkey(rs.getString(CATALOG_KEY));
        documentItemTR.setSourcefilename(rs.getString(SOURCE_FILENAME2));
        documentItemTR.setDvdfilename(rs.getString(DVD_FILENAME));
        documentItemTR.setAtanum(rs.getString("ata_num"));

        return documentItemTR;
    }

    private DocumentItemModel createICDocument(ProgramItemModel programItemModel,
        String downloadtype, ResultSet rs)
        throws SQLException {
        DocumentItemICCatalogModel documentItemIC = new DocumentItemICCatalogModel();
        documentItemIC.setDownloadtype(downloadtype);
        documentItemIC.setProgramItem(programItemModel);
        documentItemIC.setDownloadtype(downloadtype);
        Element manualElement = (Element) programItemModel.getTocRoot()
            .selectSingleNode(DOCNBR2 + rs.getString(MANUAL_DOC_NUM) + CLOSE_SQ);
        documentItemIC.setManualItem(createManualItem(programItemModel, manualElement));
        documentItemIC.setFilename(rs.getString(VIEW_FILENAME));
        documentItemIC.setFileType(getFileType(documentItemIC.getFilename()));
        documentItemIC.setId(rs.getString(TOC_KEY2));
        documentItemIC.setRevisionDate((rs.getString(REV_DATE) != null
            ? rs.getString(REV_DATE).replaceFirst(D_4_D_2_D_2, DOLLAR1_2_3) : null));
        documentItemIC
            .setResourceUri(TECHPUBS_DOCS_PGMS + programItemModel.getProgramDocnbr() + MANS22
                + rs.getString(MANUAL_DOC_NUM) + FILE + documentItemIC.getFilename());
        documentItemIC.setReleaseDate((rs.getString(RELEASE_DATE) != null
            ? rs.getString(RELEASE_DATE).replaceFirst(D_4_D_2_D_2, DOLLAR1_2_3) : null));
        documentItemIC.setTitle(rs.getString(TITLE));
        documentItemIC.setToctitle(rs.getString(TITLE));
        documentItemIC.setCatalogkey(rs.getString(CATALOG_KEY));
        documentItemIC.setSourceFileName(rs.getString(SOURCE_FILENAME2));
        documentItemIC.setDvdFileName(rs.getString(DVD_FILENAME));
        documentItemIC.setOnlineFileName(rs.getString(VIEW_FILENAME));
        documentItemIC.setRevnbr(rs.getString(REV_NBR));
        documentItemIC.setAtanum(rs.getString("ata_num"));

        return documentItemIC;
    }

    private DocumentItemModel createLRDocument(ProgramItemModel programItemModel,
        String downloadtype, ResultSet rs)
        throws SQLException {
        DocumentItemLRCatalogModel documentItemLR = new DocumentItemLRCatalogModel();
        documentItemLR.setDownloadtype(downloadtype);
        documentItemLR.setProgramItem(programItemModel);
        documentItemLR.setDownloadtype(downloadtype);
        Element manualElement = (Element) programItemModel.getTocRoot()
            .selectSingleNode(DOCNBR2 + rs.getString(MANUAL_DOC_NUM) + CLOSE_SQ);
        documentItemLR.setManualItem(createManualItem(programItemModel, manualElement));
        documentItemLR.setId(rs.getString(CATALOG_KEY));
        documentItemLR.setRevisionDate((rs.getString(REV_DATE) != null
            ? rs.getString(REV_DATE).replaceFirst(D_4_D_2_D_2, DOLLAR1_2_3) : null));
        documentItemLR.setPubcwcdate(rs.getString(PUBLISHED_TO_CWC_DATE) != null
            ? rs.getString(PUBLISHED_TO_CWC_DATE).replaceFirst(D_4_D_2_D_2, DOLLAR1_2_3) : null);
        documentItemLR.setTitle(rs.getString(TITLE));
        documentItemLR.setCatalogkey(rs.getString(CATALOG_KEY));
        documentItemLR.setSourceFileName(rs.getString(SOURCE_FILENAME2));
        documentItemLR.setDvdFileName(rs.getString(DVD_FILENAME));
        documentItemLR.setOnlineFileName(rs.getString(VIEW_FILENAME));
        documentItemLR.setFileType(getFileType(documentItemLR.getDownloadfilename()));
        documentItemLR.setType(rs.getString(CATALOG_TYPE));

        return documentItemLR;
    }
}
