package com.geaviation.techpubs.data.impl;

import com.geaviation.techpubs.models.ProgramItemModel;
import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.models.techlib.BookcaseVersionEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public class ProgramDataSvcImplTest {

    private static final Logger log = LogManager.getLogger(ProgramDataSvcImplTest.class);

    public static final String GEK112060 = "gek112060";
    public static final String BC_VERSION = "BC_VERSION";

    @Mock
    private DataSource tpsDatasourceMock;

    @Mock
    private Connection tpsConnectionMock;

    @Mock
    private PreparedStatement tpsPreparedStatementOnline;

    @Mock
    private PreparedStatement tpsPreparedStatementFalse;

    @Mock
    private ResultSet resultSet;

    @Mock
    private ResultSet resultSetFalse;

    @Mock
    private ResultSetMetaData resultSetMetaData;

    private ProgramDataSvcImpl pgmDataSvcTestObj;

    private static final String SELECTCATALOGITEMSQL =
        "select c.view_filename,"
            + "       c.doc_nbr,"
            + "       c.rev_nbr,"
            + "       to_char(c.rev_date,'YYYYMMDD') rev_date,"
            + "       c.title,"
            + "       c.category,"
            + "       c.type,"
            + "       c.toc_key"
            + "  from tps_getd_catalog c,"
            + "       tps_getd_bookcase bc,"
            + "       tps_getd_manual_document md,"
            + "       tps_getd_catalog_type ct"
            + " where c.getd_bc_seq_id = bc.getd_bc_seq_id"
            + "   and c.getd_manual_doc_seq_id = md.getd_manual_doc_seq_id"
            + "   and c.getd_catalog_type_seq_id = ct.getd_catalog_type_seq_id"
            + "   and bc.bc_doc_num = ?"
            + "   and md.manual_doc_num = ?"
            + "   and ct.catalog_type = ? "
            + "order by c.doc_nbr, c.rev_nbr";

    private static final String SELECTTOCSQL = "select tps_getd_bookcase.bc_doc_num,"
        + "       tps_getd_manual_document.manual_doc_num," + "       tps_getd_toc.toc_title,"
        + "	   getd_toc_seq_id " + "  from tps_getd_toc," + "       tps_getd_bookcase,"
        + "  	   tps_getd_manual_document" + " where tps_getd_bookcase.bc_doc_num = ?"
        + "   and tps_getd_toc.getd_bc_seq_id = tps_getd_bookcase.getd_bc_seq_id"
        + "   and tps_getd_toc.getd_manual_doc_seq_id = tps_getd_manual_document.getd_manual_doc_seq_id";

    @Before
    public void setup() throws SQLException {
        MockitoAnnotations.initMocks(this);
        pgmDataSvcTestObj = new ProgramDataSvcImpl();

        when(tpsPreparedStatementOnline.executeQuery()).thenReturn(resultSet);
        when(tpsConnectionMock.prepareStatement(SELECTCATALOGITEMSQL)).thenReturn(
            tpsPreparedStatementFalse);
        when(tpsPreparedStatementFalse.executeQuery()).thenReturn(resultSetFalse);
        when(resultSetFalse.getMetaData()).thenReturn(resultSetMetaData);
        when(tpsConnectionMock.prepareStatement(SELECTTOCSQL)).thenReturn(
            tpsPreparedStatementFalse);
        when(resultSetFalse.next()).thenReturn(false).thenReturn(false);
        when(resultSet.getMetaData()).thenReturn(resultSetMetaData);
        when(resultSetMetaData.getColumnCount()).thenReturn(1);
        when(resultSet.getString("VIEW_FILENAME")).thenReturn("TestFileName");
        when(resultSetMetaData.getColumnLabel(anyInt())).thenReturn("ColumnLabel");
        when(resultSet.getString(anyInt())).thenReturn("TestFileName");
        when(tpsDatasourceMock.getConnection()).thenReturn(tpsConnectionMock);
        when(tpsPreparedStatementOnline.executeQuery()).thenReturn(resultSet).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString(BC_VERSION)).thenReturn("2.0");

        Field programDataSvc = null;
        try {
            programDataSvc = ProgramDataSvcImpl.class.getDeclaredField("tpsDataSource");
            programDataSvc.setAccessible(true);
        } catch (NoSuchFieldException | NullPointerException e) {
            log.error(e);
        }

        try {
            if (programDataSvc != null) {
                programDataSvc.set(pgmDataSvcTestObj, tpsDatasourceMock);
            }
        } catch (IllegalAccessException e) {
            log.error(e);
        }

        String path = "src/test/resources/data4/techpubs";
        File file = new File(path);
        String absolutePath = file.getAbsolutePath();
    }

    @Ignore
    @Test
    public void testGetProgramItemsByFamily () {
        List<ProgramItemModel> resultList = pgmDataSvcTestObj.getProgramItemsByFamily("PASSPORT", SubSystem.TD);

        assertEquals(GEK112060, resultList.get(0).getProgramDocnbr());
        assertEquals(SubSystem.TD, resultList.get(0).getSubSystem());
        assertEquals("2.0", resultList.get(0).getProgramOnlineVersion());
        assertEquals("gek112059", resultList.get(1).getProgramDocnbr());
        assertEquals(SubSystem.TD, resultList.get(1).getSubSystem());
        assertEquals("2.0", resultList.get(1).getProgramOnlineVersion());
    }

    @Ignore
    @Test
    public void testGetProgramItemsByFamilyInProgramList (){
        List<String> programsToRetrieve = new ArrayList<>();
        programsToRetrieve.add(GEK112060);

        List<ProgramItemModel> resultList = pgmDataSvcTestObj
            .getProgramItemsByFamily("PASSPORT", SubSystem.TD, programsToRetrieve);

        assertEquals(GEK112060, resultList.get(0).getProgramDocnbr());
        assertEquals(SubSystem.TD, resultList.get(0).getSubSystem());
        assertEquals("2.0", resultList.get(0).getProgramOnlineVersion());
    }

    @Ignore
    @Test
    public void testGetProgramItemsByModel() throws SQLException {
        List<ProgramItemModel> resultList = pgmDataSvcTestObj
            .getProgramItemsByModel("PASSPORT20", SubSystem.TD);

        assertEquals(GEK112060, resultList.get(0).getProgramDocnbr());
        assertEquals(SubSystem.TD, resultList.get(0).getSubSystem());
        assertEquals("2.0", resultList.get(0).getProgramOnlineVersion());
        assertEquals("gek112059", resultList.get(1).getProgramDocnbr());
        assertEquals(SubSystem.TD, resultList.get(1).getSubSystem());
        assertEquals("2.0", resultList.get(1).getProgramOnlineVersion());
    }

    @Ignore
    @Test
    public void testGetProgramItemsByModelInProgramList() {
        List<String> programsToRetrieve = new ArrayList<>();
        programsToRetrieve.add(GEK112060);

        List<ProgramItemModel> resultList = pgmDataSvcTestObj
            .getProgramItemsByModel("PASSPORT20", SubSystem.TD, programsToRetrieve);

        assertEquals(GEK112060, resultList.get(0).getProgramDocnbr());
        assertEquals(SubSystem.TD, resultList.get(0).getSubSystem());
        assertEquals("2.0", resultList.get(0).getProgramOnlineVersion());
    }

    @Test
    public void testGetProgramsByRoles() {
        List<String> testRoles = new ArrayList<>();
        testRoles.add("techpubs_passport20_sm");
        testRoles.add("enigma cf34rj");

        List<String> resultList = pgmDataSvcTestObj.getProgramsByRoles(testRoles,SubSystem.TP);

        assertEquals("gek108751", resultList.get(0));
        assertEquals(GEK112060,resultList.get(1));
    }

    @Ignore
    @Test
    public void testGetSpmProgramItem() throws SQLException {
        when(resultSet.getString(BC_VERSION)).thenReturn("12");
        ProgramItemModel result = pgmDataSvcTestObj.getSpmProgramItem();

        assertEquals("gek108792", result.getProgramDocnbr());
        assertEquals("12", result.getProgramOnlineVersion());
        assertEquals(SubSystem.TD, result.getSubSystem());
    }

    @Ignore
    @Test
    public void testGetHondaSpmProgramItem() throws SQLException {
        when(resultSet.getString(BC_VERSION)).thenReturn("1.1");
        ProgramItemModel result = pgmDataSvcTestObj.getHondaSpmProgramItem();

        assertEquals("gek119360", result.getProgramDocnbr());
        assertEquals("1.1", result.getProgramOnlineVersion());
        assertEquals(SubSystem.TD, result.getSubSystem());
    }

    @Ignore
    @Test
    public void testGetProgramItemTD() {
        ProgramItemModel result = pgmDataSvcTestObj.getProgramItem(GEK112060, SubSystem.TD);

        assertEquals(GEK112060, result.getProgramDocnbr());
        assertEquals("2.0", result.getProgramOnlineVersion());
        assertEquals(SubSystem.TD, result.getSubSystem());
    }

    @Test
    public void testGetProgramItemNonTD() {
        ProgramItemModel result = pgmDataSvcTestObj.getProgramItem("112060:112064", SubSystem.FH);

        assertEquals("112060", result.getProgramDocnbr());
        assertNull(result.getProgramOnlineVersion());
        assertEquals(SubSystem.FH, result.getSubSystem());
    }

    @Test
    @Ignore
    public void testGetProgramItemVersionOne() throws SQLException {
        final String program = "gek112865_lr";
        final String version = "4.8";

        BookcaseVersionEntity bookcaseVersionEntity = new BookcaseVersionEntity();
        bookcaseVersionEntity.setTitle(program);
        bookcaseVersionEntity.setBookcaseVersion(version);

        ProgramItemModel programItemModel = pgmDataSvcTestObj.getProgramItemVersion(bookcaseVersionEntity, SubSystem.TD);

        assertNotNull(programItemModel);
        assertNotNull(programItemModel.getTitle());
        assertEquals(program, (programItemModel.getProgramDocnbr()));
        assertEquals(version, programItemModel.getProgramOnlineVersion());
    }

    @Test
    @Ignore
    public void testGetProgramItemVersionTwoSequentialOneNext() throws SQLException {
        final String program = "gek112865_lr";
        final String version = "4.8";
        final String nextVersion = "4.9";

        BookcaseVersionEntity bookcaseVersionEntity = new BookcaseVersionEntity();
        bookcaseVersionEntity.setTitle(program);
        bookcaseVersionEntity.setBookcaseVersion(version);

        ProgramItemModel programItemModelOne = pgmDataSvcTestObj.getProgramItemVersion(bookcaseVersionEntity, SubSystem.TD);


        assertNotNull(programItemModelOne);
        assertNotNull(programItemModelOne.getTitle());
        assertEquals(program, (programItemModelOne.getProgramDocnbr()));
        assertEquals(version, programItemModelOne.getProgramOnlineVersion());

        programItemModelOne = pgmDataSvcTestObj.getProgramItemVersion(bookcaseVersionEntity, SubSystem.TD);
        assertEquals(program, (programItemModelOne.getProgramDocnbr()));
        assertEquals(version, programItemModelOne.getProgramOnlineVersion());

        bookcaseVersionEntity.setBookcaseVersion(nextVersion);
        ProgramItemModel programItemModelTwo = pgmDataSvcTestObj.getProgramItemVersion(bookcaseVersionEntity, SubSystem.TD);
        assertEquals(program, (programItemModelTwo.getProgramDocnbr()));
        assertEquals(nextVersion, programItemModelTwo.getProgramOnlineVersion());

        assertNotEquals(programItemModelOne.getProgramOnlineVersion(), programItemModelTwo.getProgramOnlineVersion());
    }
}
