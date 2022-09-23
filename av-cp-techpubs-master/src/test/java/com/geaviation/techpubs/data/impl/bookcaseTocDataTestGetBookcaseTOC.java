package com.geaviation.techpubs.data.impl;

import com.geaviation.dss.service.common.exception.TechnicalException;
import com.geaviation.techpubs.data.api.IProgramData;
import com.geaviation.techpubs.data.api.techlib.*;
import com.geaviation.techpubs.data.util.DataConstants;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.BookcaseTocDAO;
import com.geaviation.techpubs.models.TocDocModel;
import com.geaviation.techpubs.models.TocModel;
import com.geaviation.techpubs.models.techlib.BookEntity;
import com.geaviation.techpubs.models.techlib.BookVersionEntity;
import com.geaviation.techpubs.models.techlib.BookcaseEntity;
import com.geaviation.techpubs.services.util.StringUtils;
import com.google.common.collect.Lists;
import org.apache.commons.collections.map.HashedMap;
import org.apache.poi.ss.usermodel.DateUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.geaviation.techpubs.data.util.DataConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;


public class bookcaseTocDataTestGetBookcaseTOC {

    public static class createBookcaseTocDAOListTest {
        private static final String BOOKCASE_KEY = "gek112060";

        @Mock(name="techLibJDBCTemplate")
        private NamedParameterJdbcTemplate techLibJDBCTemplateMock;

        @Mock(name="programDataSvc")
        private IProgramData programDataMock;

        @Mock
        private IBookcaseVersionData iBookcaseVersionDataMock;

        @InjectMocks
        private BookcaseTOCData bookcaseTocData;

        Map<String,Object> bookItem1;
        Map<String,Object> bookItem2;
        Map<String,Object> bookItem3;
        Map<String,Object> bookItem4;
        Map<String,Object> bookItem5;
        Map<String,Object> bookItem6;
        Map<String,Object> sectionItem1;
        Map<String,Object> sectionItem2;
        Map<String,Object> sectionItem3;
        Map<String,Object> sectionItem4;
        Map<String,Object> sectionItem5;
        Map<String,Object> pageblkItem1;
        Map<String,Object> pageblkItem2;
        Map<String,Object> pageblkItem3;
        Map<String,Object> pageblkItem4;
        Map<String,Object> pageblkItem5;
        Map<String,Object> pageblkItem6;
        Map<String,Object> pageblkItem7;
        Map<String,Object> pageblkItem8;
        Map<String,Object> pageblkItem9;

        private List<Map<String, Object>> mockDBReturnListWithIcsAndTrs;
        private List<Map<String, Object>> mockDBReturnListWithoutIcsAndTrs;

        private static final String TYPE_LMM = "lmm";
        private static final String FILE_EXTENSION =  "test";
        private static final String FILE_EXTENSION_HTM =  "htm";
        @Before
        public void setUp()  {

            // Instantiate class we're testing and inject mocks
            bookcaseTocData = new BookcaseTOCData();
            MockitoAnnotations.initMocks(this);

            bookItem1 = new HashedMap();
            bookItem1.put("id", UUID.fromString("10000000-0000-0000-0000-000000000000"));
            bookItem1.put("parent_id", null);
            bookItem1.put("node_order", null);
            bookItem1.put("title", "BookDAO 1 Title");
            bookItem1.put("toc_title", null);
            bookItem1.put("node_type", null);
            bookItem1.put("revision_date", null);
            bookItem1.put("node_key", "book1Key");
            bookItem1.put(TECHLIB_TREE_DEPTH, 1);

            bookItem2 = new HashedMap();
            bookItem2.put("id", UUID.fromString("20000000-0000-0000-0000-000000000000"));
            bookItem2.put("parent_id", null);
            bookItem2.put("node_order", new Integer(2));
            bookItem2.put("title", "BookDAO 2");
            bookItem2.put("toc_title", "BookDAO 2");
            bookItem2.put("node_type", TYPE_LMM);
            bookItem2.put("revision_date", "20190101");
            bookItem2.put("node_key", "book2Key");
            bookItem2.put("filename", "Filename");
            bookItem2.put(TECHLIB_TREE_DEPTH, 1);

            bookItem3 = new HashedMap();
            bookItem3.put("id", UUID.fromString("30000000-0000-0000-0000-000000000000"));
            bookItem3.put("parent_id", null);
            bookItem3.put("node_order", new Integer(3));
            bookItem3.put("title", null);
            bookItem3.put("toc_title", "toc_title");
            bookItem3.put("node_key", "book3Key");
            bookItem3.put("node_type", TYPE_LMM);
            bookItem3.put(TECHLIB_TREE_DEPTH, 1);

            bookItem4 = new HashedMap();
            bookItem4.put("id", UUID.fromString("21000000-0000-0000-0000-000000000000"));
            bookItem4.put("parent_id", null);
            bookItem4.put("node_order", new Integer(4));
            bookItem4.put("title", null);
            bookItem4.put("toc_title", null);
            bookItem4.put("node_type", "eipc");
            bookItem4.put("node_key", "book3Key");
            bookItem4.put(TECHLIB_TREE_DEPTH, 1);

            bookItem5 = new HashedMap();
            bookItem5.put("id", UUID.fromString("45000000-0000-0000-0000-000000000000"));
            bookItem5.put("parent_id", null);
            bookItem5.put("node_order", new Integer(5));
            bookItem5.put("title", TR_BOOKCASE_TOC_MODEL_TITLE);
            bookItem5.put("toc_title", TR_BOOKCASE_TOC_MODEL_TITLE);
            bookItem5.put("node_type", "tr");
            bookItem5.put("node_key", "book4Key");
            bookItem5.put("revision_date", null);
            bookItem5.put(TECHLIB_TREE_DEPTH, 1);

            bookItem6 = new HashedMap();
            bookItem6.put("id", UUID.fromString("55000000-0000-0000-0000-000000000000"));
            bookItem6.put("parent_id", null);
            bookItem6.put("node_order", new Integer(6));
            bookItem6.put("title",IC_BOOKCASE_TOC_MODEL_TITLE);
            bookItem6.put("toc_title", IC_BOOKCASE_TOC_MODEL_TITLE);
            bookItem6.put("node_type", "ic");
            bookItem6.put("node_key", "book5Key");
            bookItem6.put("revision_date", null);
            bookItem6.put(TECHLIB_TREE_DEPTH, 1);

            sectionItem1 = new HashedMap();
            sectionItem1.put("id", UUID.fromString("40000000-0000-0000-0000-000000000000"));
            sectionItem1.put("parent_id", UUID.fromString("10000000-0000-0000-0000-000000000000"));
            sectionItem1.put("node_order", new Integer(1));
            sectionItem1.put("title", "section 1");
            sectionItem1.put(TECHLIB_TREE_DEPTH, 2);

            sectionItem2 = new HashedMap();
            sectionItem2.put("id", UUID.fromString("50000000-0000-0000-0000-000000000000"));
            sectionItem2.put("parent_id", UUID.fromString("10000000-0000-0000-0000-000000000000"));
            sectionItem2.put("node_order", new Integer(2));
            sectionItem2.put("title", "section 2");
            sectionItem2.put(TECHLIB_TREE_DEPTH, 2);

            sectionItem5 = new HashedMap();
            sectionItem5.put("id", UUID.fromString("16000000-0000-0000-0000-000000000000"));
            sectionItem5.put("parent_id", UUID.fromString("10000000-0000-0000-0000-000000000000"));
            sectionItem5.put("node_order", new Integer(0));
            sectionItem5.put("tree_depth", new Integer(0));
            sectionItem5.put("title", "Dummy Section");
            sectionItem5.put(TECHLIB_TREE_DEPTH, 0);

            sectionItem3 = new HashedMap();
            sectionItem3.put("id", UUID.fromString("12000000-0000-0000-0000-000000000000"));
            sectionItem3.put("parent_id", UUID.fromString("20000000-0000-0000-0000-000000000000"));
            sectionItem3.put("node_order", new Integer(1));
            sectionItem3.put("title", "section 3");
            sectionItem3.put(TECHLIB_TREE_DEPTH, 2);

            sectionItem4 = new HashedMap();
            sectionItem4.put("id", UUID.fromString("15000000-0000-0000-0000-000000000000"));
            sectionItem4.put("parent_id", UUID.fromString("12000000-0000-0000-0000-000000000000"));
            sectionItem4.put("node_order", new Integer(1));
            sectionItem4.put("title", "section 3 - 1");
            sectionItem4.put("node_key", "sectionItem4 key");
            sectionItem4.put(TECHLIB_TREE_DEPTH, 3);

            pageblkItem1 = new HashedMap();
            pageblkItem1.put("id", UUID.fromString("60000000-0000-0000-0000-000000000000"));
            pageblkItem1.put("parent_id", UUID.fromString("40000000-0000-0000-0000-000000000000"));
            pageblkItem1.put("node_order", new Integer(1));
            pageblkItem1.put("node_type", DataConstants.MANUAL_PAGEBLK_TYPE);
            pageblkItem1.put("title", "PageblkDAO 1");
            pageblkItem1.put("node_key", "key 1");
            pageblkItem1.put("filename", "filename." + FILE_EXTENSION);
            pageblkItem1.put(TECHLIB_TREE_DEPTH, 3);

            pageblkItem2 = new HashedMap();
            pageblkItem2.put("id", UUID.fromString("70000000-0000-0000-0000-000000000000"));
            pageblkItem2.put("parent_id", UUID.fromString("40000000-0000-0000-0000-000000000000"));
            pageblkItem2.put("node_order", new Integer(1));
            pageblkItem2.put("node_type", DataConstants.IC_PAGEBLK_TYPE);
            pageblkItem2.put("title", "PageblkDAO 2");
            pageblkItem2.put("node_key", "key 1");
            pageblkItem2.put("filename", "filename." + FILE_EXTENSION_HTM);
            pageblkItem2.put(TECHLIB_TREE_DEPTH, 3);

            pageblkItem3 = new HashedMap();
            pageblkItem3.put("id", UUID.fromString("80000000-0000-0000-0000-000000000000"));
            pageblkItem3.put("parent_id", UUID.fromString("40000000-0000-0000-0000-000000000000"));
            pageblkItem3.put("node_order", new Integer(2));
            pageblkItem3.put("node_type", DataConstants.MANUAL_PAGEBLK_TYPE);
            pageblkItem3.put("title", "PageblkDAO 3");
            pageblkItem3.put("node_key", "key 2");
            pageblkItem3.put(TECHLIB_TREE_DEPTH, 3);

            pageblkItem4 = new HashedMap();
            pageblkItem4.put("id", UUID.fromString("90000000-0000-0000-0000-000000000000"));
            pageblkItem4.put("parent_id", UUID.fromString("40000000-0000-0000-0000-000000000000"));
            pageblkItem4.put("node_order", new Integer(2));
            pageblkItem4.put("node_type", DataConstants.TR_PAGEBLK_TYPE);
            pageblkItem4.put("title", "PageblkDAO 4");
            pageblkItem4.put("node_key", "key 2");
            pageblkItem4.put(TECHLIB_TREE_DEPTH, 3);

            pageblkItem5 = new HashedMap();
            pageblkItem5.put("id", UUID.fromString("22000000-0000-0000-0000-000000000000"));
            pageblkItem5.put("parent_id", UUID.fromString("50000000-0000-0000-0000-000000000000"));
            pageblkItem5.put("node_order", new Integer(1));
            pageblkItem5.put("node_type", DataConstants.IC_PAGEBLK_TYPE);
            pageblkItem5.put("title", "PageblkDAO 5");
            pageblkItem5.put("node_key", "key 3");
            pageblkItem5.put(TECHLIB_TREE_DEPTH, 3);

            pageblkItem6 = new HashedMap();
            pageblkItem6.put("id", UUID.fromString("11000000-0000-0000-0000-000000000000"));
            pageblkItem6.put("parent_id", UUID.fromString("15000000-0000-0000-0000-000000000000"));
            pageblkItem6.put("node_order", new Integer(1));
            pageblkItem6.put("node_type", DataConstants.IC_PAGEBLK_TYPE);
            pageblkItem6.put("title", "PageblkDAO 6");
            pageblkItem6.put("node_key", "key 4");
            pageblkItem6.put(TECHLIB_FILENAME, "pageblkItem6_filename");
            pageblkItem6.put(TECHLIB_TREE_DEPTH, 4);

            pageblkItem7 = new HashedMap();
            pageblkItem7.put("id", UUID.fromString("13000000-0000-0000-0000-000000000000"));
            pageblkItem7.put("parent_id", UUID.fromString("50000000-0000-0000-0000-000000000000"));
            pageblkItem7.put("node_order", new Integer(2));
            pageblkItem7.put("node_type", DataConstants.TR_PAGEBLK_TYPE);
            pageblkItem7.put("title", "PageblkDAO 7");
            pageblkItem7.put("node_key", "key 5");
            pageblkItem7.put(TECHLIB_TREE_DEPTH, 3);

            pageblkItem8 = new HashedMap();
            pageblkItem8.put("id", UUID.fromString("14000000-0000-0000-0000-000000000000"));
            pageblkItem8.put("parent_id", UUID.fromString("15000000-0000-0000-0000-000000000000"));
            pageblkItem8.put("node_order", new Integer(2));
            pageblkItem8.put("node_type", DataConstants.TR_PAGEBLK_TYPE);
            pageblkItem8.put("title", "PageblkDAO 8");
            pageblkItem8.put("node_key", "key 6");
            pageblkItem8.put(TECHLIB_TREE_DEPTH, 4);

            pageblkItem9 = new HashedMap();
            pageblkItem9.put("id", UUID.fromString("17000000-0000-0000-0000-000000000000"));
            pageblkItem9.put("parent_id", UUID.fromString("16000000-0000-0000-0000-000000000000"));
            pageblkItem9.put("node_order", new Integer(3));
            pageblkItem9.put("node_type", DataConstants.MANUAL_PAGEBLK_TYPE);
            pageblkItem9.put("title", "PageblkDAO 9");
            pageblkItem9.put("node_key", "key 9");
            pageblkItem9.put(TECHLIB_TREE_DEPTH, 3);


            mockDBReturnListWithIcsAndTrs = new ArrayList<>(Arrays.asList(bookItem6, bookItem5, bookItem4, bookItem3, bookItem2, bookItem1, sectionItem1, sectionItem2, sectionItem3, sectionItem4, sectionItem5,
                    pageblkItem1, pageblkItem2, pageblkItem3, pageblkItem4, pageblkItem5, pageblkItem6, pageblkItem7, pageblkItem8, pageblkItem9));

            mockDBReturnListWithoutIcsAndTrs = new ArrayList<>(Arrays.asList(bookItem4, bookItem3, bookItem2, bookItem1, sectionItem1, sectionItem2, sectionItem3, sectionItem5,
                    pageblkItem1, pageblkItem3, pageblkItem9));

            when(techLibJDBCTemplateMock.queryForList(isA(String.class), isA(SqlParameterSource.class))).thenReturn(mockDBReturnListWithIcsAndTrs);
            when(iBookcaseVersionDataMock.findOnlineBookcaseVersion(isA(String.class))).thenReturn("9.9");
            when(programDataMock.getTocsByProgram(isA(String.class))).thenReturn(new ArrayList<>());
            when(programDataMock.getCatalogItemFilename(isA(String.class), isA(String.class), isA(String.class))).thenReturn(new HashedMap());
        }


        /**********************************************************************************************
         *********************************Tree Structure Tests*****************************************
         **********************************************************************************************/

        @Test
        public void whenEmptyListIsReturnedFromDBShouldReturnAnEmtpyList(){
            when(techLibJDBCTemplateMock.queryForList(isA(String.class), isA(SqlParameterSource.class))).thenReturn(new ArrayList());
            assertEquals(true, bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null).isEmpty());
        }

        @Test
        public void whenNullIsReturnedFromDBShouldReturnAnEmtpyList(){
            when(techLibJDBCTemplateMock.queryForList(isA(String.class), isA(SqlParameterSource.class) )).thenReturn(null);
            assertEquals(true, bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null).isEmpty());
        }

        @Test
        public void whenTOCItemListIsNotEmptyTheReturnedListShouldOnlyContainABookcaseTOCModelForEachRootItem(){
            Collection<BookcaseTocDAO> resultList = bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            assert(listContainsId("10000000-0000-0000-0000-000000000000", resultList));
            assert(listContainsId("20000000-0000-0000-0000-000000000000", resultList));
            assert(listContainsId("30000000-0000-0000-0000-000000000000", resultList));
            assert(listContainsId("21000000-0000-0000-0000-000000000000", resultList));
            assertEquals(6, resultList.size());

        }

        @Test
        public void whenDBReturnsItemsWithAParentIdThoseItemsShouldBeChildrenOfTheBookcaseTOCModelWithTheParentId(){
            List<BookcaseTocDAO> resultList = bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            assert(listContainsId("40000000-0000-0000-0000-000000000000", resultList.get(0).getChildren()));
            assert(listContainsId("50000000-0000-0000-0000-000000000000", resultList.get(0).getChildren()));
        }

        @Test
        public void whenDBReturnsANumberOfItemsWithAGivenParentIdTheBookcaseTOCModelWithTheParentIdShouldHaveTheSameNumberOfChildren(){
            List<BookcaseTocDAO> resultList = bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            assert(resultList.get(0).getChildren().size() == 3);
        }


        @Test
        public void whenItemsReturnedFromDBWithTreeDepthZeroTheyShouldNotBeAddedToTheResultTreeStructure(){
            when(techLibJDBCTemplateMock.queryForList(isA(String.class), isA(SqlParameterSource.class))).thenReturn(mockDBReturnListWithoutIcsAndTrs);
            List<BookcaseTocDAO> resultList = bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            List<BookcaseTocDAO> book1Children = getBookcaseTOCModelById("10000000-0000-0000-0000-000000000000", resultList).getChildren();
            assert(!listContainsId("16000000-0000-0000-0000-000000000000", book1Children));
        }

        @Test
        public void whenAPhotoGuideBookIsReturnedFromTheDBTheResultListShouldIncludeThePhotoGuideBook(){


            Map<String, Object> daBook = new HashedMap();
            daBook.put("id", UUID.fromString("99000000-0000-0000-0000-000000000000"));
            daBook.put("parent_id", null);
            daBook.put("node_order", new Integer(1));
            daBook.put("title", "DA Book");
            daBook.put("node_key", "DA key");
            daBook.put("node_type", BOOK_TYPE_PHOTO_GUIDE);
            daBook.put(TECHLIB_TREE_DEPTH, 1);

            List<Map<String, Object>> mockDBReturnList = new ArrayList<>();
            mockDBReturnList.addAll(mockDBReturnListWithIcsAndTrs);
            mockDBReturnList.add(daBook);

            when(techLibJDBCTemplateMock.queryForList(isA(String.class), isA(SqlParameterSource.class))).thenReturn(mockDBReturnList);

            List<BookcaseTocDAO> resultList = bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);

            assert(listContainsId("99000000-0000-0000-0000-000000000000", resultList));
        }

        @Test
        public void whenItemsReturnedFromDBWithParentHavingTreeDepthZeroThenItemShouldBeAddedAsAChildOfItsGrandparent(){
            when(techLibJDBCTemplateMock.queryForList(isA(String.class), isA(SqlParameterSource.class))).thenReturn(mockDBReturnListWithoutIcsAndTrs);
            List<BookcaseTocDAO> resultList = bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            List<BookcaseTocDAO> book1Children = getBookcaseTOCModelById("10000000-0000-0000-0000-000000000000", resultList).getChildren();
            assert(listContainsId("17000000-0000-0000-0000-000000000000", book1Children));
        }

        @Test
        public void whenItemsReturnedFromDBWithParentHavingTreeDepthZeroThenParentOfItemShouldBeItsGrandparent(){
            when(techLibJDBCTemplateMock.queryForList(isA(String.class), isA(SqlParameterSource.class))).thenReturn(mockDBReturnListWithoutIcsAndTrs);
            List<BookcaseTocDAO> resultList = bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            BookcaseTocDAO book1 = getBookcaseTOCModelById("10000000-0000-0000-0000-000000000000", resultList);
            List<BookcaseTocDAO> book1Children = book1.getChildren();
            assert(getBookcaseTOCModelById("17000000-0000-0000-0000-000000000000", book1Children).getParent().equals(book1));
        }

        @Test
        public void when_JDBC_throws_exception_should_throw_technical_exception(){
            when(techLibJDBCTemplateMock.queryForList(isA(String.class), isA(SqlParameterSource.class))).thenThrow(new DataAccessException("..."){ });
            Assertions.assertThrows(TechnicalException.class, () -> bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null));
        }

        @Test
        public void when_getOnlineVersion_throws_exception_then_should_throw_technical_exception(){
            when(iBookcaseVersionDataMock.findOnlineBookcaseVersion(isA(String.class))).thenThrow(TechnicalException.class);
            Assertions.assertThrows(TechnicalException.class, () -> bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null));
        }


        /**********************************************************************************************
         ************************************** POJO Mapping Tests ************************************
         **********************************************************************************************/

        @Test
        public void whenIdAttributeIsNotNullThenBookcaseTOCItemIdShouldBeEqualToIdAttribute() {
            List<BookcaseTocDAO> resultList =  bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            assert (resultList.get(1).getId().equals(bookItem2.get("id").toString()));
        }

        @Test
        public void whenParentIdAttributeIsNotNullThenBookcaseTOCItemParentIdShouldBeEqualToParentIdAttribute() {
            List<BookcaseTocDAO> resultList =  bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            BookcaseTocDAO item = getBookcaseTOCModelById("40000000-0000-0000-0000-000000000000", resultList.get(0).getChildren());
            String mapValue =  sectionItem1.get("parent_id").toString();
            assert (getBookcaseTOCModelById("40000000-0000-0000-0000-000000000000", resultList.get(0).getChildren()).getParentId()).equals(sectionItem1.get("parent_id").toString());
        }

        @Test
        public void whenParentIdAttributeIsNullThenBookcaseTOCItemParentIdShouldBeNull() {
            List<BookcaseTocDAO> resultList =  bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            assert (resultList.get(0).getParentId() == null);
        }

        @Test
        public void whenParentIdAttributeIsNotNullThenBookcaseTOCItemIsRootShouldBeFalse() {
            List<BookcaseTocDAO> resultList =  bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            assert (!getBookcaseTOCModelById("40000000-0000-0000-0000-000000000000", resultList.get(0).getChildren()).getIsRoot());
        }

        @Test
        public void whenParentIdAttributeIsNullThenBookcaseTOCItemIsRootShouldBeTrue() {
            List<BookcaseTocDAO> resultList =  bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            assert (resultList.get(0).getIsRoot());
        }

        @Test
        public void whenTitleAttributeIsNotNullThenBookcaseTOCItemTitleShouldBeEqualToTocTitleAttribute() {
            List<BookcaseTocDAO> resultList =  bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            assert (resultList.get(1).getTitle().equals(bookItem2.get("title")));
        }

        @Test
        public void whenTitleAttributeIsNullThenBookcaseTOCItemTitleShouldBeTocTitle() {
            List<BookcaseTocDAO> resultList =  bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            assertEquals(resultList.get(2).getTitle(), bookItem3.get("toc_title"));
        }

        @Test
        public void whenTocTitleAttributeIsNotNullThenBookcaseTOCItemTocTitleShouldBeEqualToTocTitleAttribute() {
            List<BookcaseTocDAO> resultList =  bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            BookcaseTocDAO item= resultList.get(1);
            assert (resultList.get(1).getTitle().equals(bookItem2.get("toc_title")));
        }

        @Test
        public void whenTocTitleAttributeIsNullThenBookcaseTOCItemTocTitleShouldBeEqualToTitle() {
            List<BookcaseTocDAO> resultList =  bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            assertEquals(resultList.get(0).getTocTitle(), bookItem1.get("title"));
        }

        @Test
        public void whenBothTOCTitleAndTitleFromDBAreNullThenTOCTitleShouldBeNull(){
            List<BookcaseTocDAO> resultList =  bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            assertEquals(resultList.get(3).getTocTitle(), null);
        }


        @Test
        public void whenBothTOCTitleAndTitleFromDBAreNullThenTitleShouldBeNull(){
            List<BookcaseTocDAO> resultList =  bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            assertEquals(resultList.get(3).getTitle(), null);
        }

        @Test
        public void whenFileNameNotNullShouldReturnItemWithFileExtensionEqualToTheExtensionUpperCase(){
            List<BookcaseTocDAO> result = bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            BookcaseTocDAO itemWithFileName = getBookcaseTOCModelById("60000000-0000-0000-0000-000000000000", result.get(0).getChildren().get(0).getChildren());
            assertEquals(itemWithFileName.getFileExtension(), FILE_EXTENSION.toUpperCase());
        }

        @Test
        public void whenFileNameISHTMShouldReturnItemWithFileExtensionEqualToHTML(){
            List<BookcaseTocDAO> result = bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            BookcaseTocDAO itemWithFileName = getBookcaseTOCModelById("70000000-0000-0000-0000-000000000000", result.get(0).getChildren().get(0).getChildren());
            assertEquals(itemWithFileName.getFileExtension(), DataConstants.FIELD_TYPE_HTML);
        }

        @Test
        public void when_type_attribute_is_not_null_then_bookcaseTOCItem_type_should_be_nodetype_from_DB(){
            List<BookcaseTocDAO> resultList =  bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            List<BookcaseTocDAO> childList = resultList.get(0).getChildren().get(0).getChildren();
            BookcaseTocDAO test = childList.stream().filter(e -> e.getId().equals("90000000-0000-0000-0000-000000000000")).findFirst().get();
            assertEquals(DataConstants.TR_PAGEBLK_TYPE, test.getType());
        }

        @Test
        public void when_type_attribute_is_null_then_bookcaseTOCItem_type_should_be_null(){
            List<BookcaseTocDAO> resultList =  bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            assertEquals( resultList.get(0).getType(), null);
        }


        @Test
        public void whenTypeAttributeIsICThenBookcaseTocItemIsLeafShouldBeTrue(){
            List<BookcaseTocDAO> resultList =  bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            assert(resultList.get(0).getChildren().get(0).getChildren().get(1).getIsLeaf());
        }

        @Test
        public void whenTypeAttributeIsTRThenBookcaseTocItemIsLeafShouldBeTrue(){
            List<BookcaseTocDAO> resultList =  bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            assert(resultList.get(0).getChildren().get(0).getChildren().get(3).getIsLeaf());
        }

        @Test
        public void whenTypeAttributeIsManualThenBookcaseTocItemIsLeafShouldBeTrue(){
            List<BookcaseTocDAO> resultList =  bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            assert(resultList.get(0).getChildren().get(0).getChildren().get(0).getIsLeaf());
        }

        @Test
        public void whenTypeAttributeIsNotIcTrOrManualThenBookcaseTocItemIsLeafShouldBeFalse(){
            List<BookcaseTocDAO> resultList =  bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            assert(!resultList.get(0).getIsLeaf());
        }


        @Test
        public void whenTypeAttributeIsNullThenBookcaseTocItemIsLeafShouldBeFalse(){
            List<BookcaseTocDAO> resultList =  bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            assert(!resultList.get(0).getIsLeaf());
        }

        @Test
        public void whenRevisionDateAttributeIsNotNullThenBookcaseTOCItemRevisionDateShouldBeEqualToRevisionDateAttribute() {
            List<BookcaseTocDAO> resultList =  bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            assert (resultList.get(1).getRevisionDate().equals(bookItem2.get("revision_date")));
        }

        @Test
        public void whenRevisionDateAttributeIsNullThenBookcaseTOCItemRevisionDateShouldBeNull() {
            List<BookcaseTocDAO> resultList =  bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null);
            assertTrue (StringUtils.isEmpty(resultList.get(0).getRevisionDate()));
        }

        @Test
        public void whenNodeOrderAttributeIsNotNullThenBookcaseTOCItemNodeOrderShouldBeEqualToNodeOrderAttribute() {
            List<BookcaseTocDAO> resultList =  bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            assert (new Integer(resultList.get(1).getOrder()).equals(bookItem2.get("node_order")));
        }

        @Test
        public void whenNodeOrderAttributeIsNullThenBookcaseTOCItemNodeOrderShouldBeZero() {
            List<BookcaseTocDAO> resultList =  bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            assert (resultList.get(0).getOrder() == 0);
        }

        @Test
        public void whenKeyAttributeIsNotNullThenBookcaseTOCItemRevisionDateShouldBeEqualToKeyAttribute() {
            List<BookcaseTocDAO> resultList =  bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            assert (resultList.get(1).getKey().equals(bookItem2.get("node_key")));
        }

        @Test
        public void whenFilenameAttributeIsNotNullThenBookcaseTOCItemFilenameShouldBeEqualToKeyAttribute() {
            List<BookcaseTocDAO> resultList =  bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            assert (resultList.get(1).getFileName().equals(bookItem2.get("filename")));
        }

        @Test
        public void when_filename_attribute_is_null_then_bookcaseTOCItem_key_should_be_null() {
            List<BookcaseTocDAO> resultList =  bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            assert (resultList.get(0).getFileName() == null);
        }

        @Test
        public void whenTreeDepthAttributeIsNotNullThenBookcaseTOCItemTreeDepthShouldBeEqualToTreeDepthAttribute() {
            List<BookcaseTocDAO> resultList =  bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);

            assert (new Integer(resultList.get(1).getTreeDepth()).equals (bookItem2.get("tree_depth")));
        }



        /**********************************************************************************************
         *********************************IC And TR BookDAO Construction Tests****************************
         **********************************************************************************************/


        @Test
        public void whenDBReturnsICsTheReturnedListShouldContainABookOfICsThatContainsAllICPageblks(){
            List<BookcaseTocDAO> resultList = bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            BookcaseTocDAO icBook = getBookByTitle(DataConstants.IC_BOOKCASE_TOC_MODEL_TITLE, resultList);
            List<BookcaseTocDAO> icBookLeaves = icBook.getLeaves();

            assert(listContainsId("70000000-0000-0000-0000-000000000000", icBookLeaves));
            assert(listContainsId("22000000-0000-0000-0000-000000000000", icBookLeaves));
            assert(listContainsId("11000000-0000-0000-0000-000000000000", icBookLeaves));

        }

        @Test
        public void whenDBReturnsANumberOfICsTheReturnedListShouldContainABookOfICsThatHasTheSameNumberOfPageblks(){
            List<BookcaseTocDAO> resultList = bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            BookcaseTocDAO icBook = getBookByTitle(DataConstants.IC_BOOKCASE_TOC_MODEL_TITLE, resultList);
            List<BookcaseTocDAO> icBookLeaves = icBook.getLeaves();

            assert(icBookLeaves.size() == 3);

        }

        @Test
        public void whenDBReturnsTRsTheReturnedListShouldContainABookOfTRsThatContainsAllTRPageblks(){
            List<BookcaseTocDAO> resultList = bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            BookcaseTocDAO trBook = getBookByTitle(DataConstants.TR_BOOKCASE_TOC_MODEL_TITLE, resultList);
            List<BookcaseTocDAO> trBookLeaves = trBook.getLeaves();

            assert(listContainsId("90000000-0000-0000-0000-000000000000", trBookLeaves));
            assert(listContainsId("13000000-0000-0000-0000-000000000000", trBookLeaves));
            assert(listContainsId("14000000-0000-0000-0000-000000000000", trBookLeaves));

        }

        @Test
        public void whenDBReturnsANumberOfTRsTheReturnedListShouldContainABookOfTRsThatHasTheSameNumberOfPageblks(){
            List<BookcaseTocDAO> resultList = bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            BookcaseTocDAO trBook = getBookByTitle(DataConstants.TR_BOOKCASE_TOC_MODEL_TITLE, resultList);
            List<BookcaseTocDAO> trBookLeaves = trBook.getLeaves();

            assert(trBookLeaves.size() == 3);

        }


        @Test
        public void whenTheReturnedListContainsABookOfTRsEachTRPageblkInTheBookShouldHaveAGrandParentWithTheSameTitleAsTheTRsParentInTheBookThatTheTRReferences(){
            List<BookcaseTocDAO> resultList = bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            BookcaseTocDAO trBook = getBookByTitle(DataConstants.TR_BOOKCASE_TOC_MODEL_TITLE, resultList);
            List<BookcaseTocDAO> trBookLeaves = trBook.getLeaves();

            for(BookcaseTocDAO trPageblk : trBookLeaves){
                String uppercaseNodeType = getPageblksParentBook(resultList, trPageblk.getId()) != null && getPageblksParentBook(resultList, trPageblk.getId()).getType() != null ?  getPageblksParentBook(resultList, trPageblk.getId()).getType().toUpperCase() :  null;
                assertEquals(uppercaseNodeType, trPageblk.getParent().getParent().getTitle());
            }
        }

        @Test
        public void whenTheReturnedListContainsABookOfTRsEachTRPageblkInTheBookShouldHaveAParentWithTitleTheSameAsTheKeyInTheTRsParentInTheBookThatTheTRReferences(){
            List<BookcaseTocDAO> resultList = bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            BookcaseTocDAO trBook = getBookByTitle(DataConstants.TR_BOOKCASE_TOC_MODEL_TITLE, resultList);

            assertEquals(trBook.getChildren().get(1).getChildren().get(0).getTitle(), sectionItem4.get("node_key"));
        }

        @Test
        public void whenTheReturnedListContainsABookOfICsEachICPageblkInTheBookShouldHaveAGrandParentWithTitleEqualToNodeTypeOfTheBookThatTheICReferences(){
            List<BookcaseTocDAO> resultList = bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            BookcaseTocDAO icBook = getBookByTitle(DataConstants.IC_BOOKCASE_TOC_MODEL_TITLE, resultList);
            List<BookcaseTocDAO> icBookLeaves = icBook.getLeaves();

            for(BookcaseTocDAO icPageblk : icBookLeaves){
                String uppercaseNodeType = getPageblksParentBook(resultList, icPageblk.getId()) != null && getPageblksParentBook(resultList, icPageblk.getId()).getType() != null ?  getPageblksParentBook(resultList, icPageblk.getId()).getType().toUpperCase() :  null;
                assertEquals(uppercaseNodeType, icPageblk.getParent().getParent().getTitle());
            }
        }

        @Test
        public void whenTheReturnedListContainsABookOfICsEachICPageblkInTheBookShouldHaveAParentWithTheSameTitleAsTheICsParentInTheBookThatTheICReferences(){
            List<BookcaseTocDAO> resultList = bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            BookcaseTocDAO icBook = getBookByTitle(DataConstants.IC_BOOKCASE_TOC_MODEL_TITLE, resultList);


            assertEquals(icBook.getChildren().get(1).getChildren().get(0).getTitle(), sectionItem4.get("node_key"));
        }

        @Test
        public void whenTheReturnedListContainsABookOfICsEachICPageblkInTheBookShouldHaveThreeParentLevels(){
            List<BookcaseTocDAO> resultList = bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            BookcaseTocDAO icBook = getBookByTitle(DataConstants.IC_BOOKCASE_TOC_MODEL_TITLE, resultList);
            List<BookcaseTocDAO> icBookLeaves = icBook.getLeaves();

            for(BookcaseTocDAO icPageblk : icBookLeaves){
                assert(icPageblk.getParent() != null && !icPageblk.getParent().getIsRoot());
                assert(icPageblk.getParent().getParent() != null && !icPageblk.getParent().getParent().getIsRoot());
                assert(icPageblk.getParent().getParent().getParent() != null && icPageblk.getParent().getParent().getParent().getIsRoot());
            }
        }

        @Test
        public void whenTheReturnedListContainsABookOfTRsEachTRPageblkInTheBookShouldHaveThreeParentLevels(){
            List<BookcaseTocDAO> resultList = bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            BookcaseTocDAO trBook = getBookByTitle(DataConstants.TR_BOOKCASE_TOC_MODEL_TITLE, resultList);
            List<BookcaseTocDAO> trBookLeaves = trBook.getLeaves();

            for(BookcaseTocDAO trPageblk : trBookLeaves){
                assert(trPageblk.getParent() != null && !trPageblk.getParent().getIsRoot());
                assert(trPageblk.getParent().getParent() != null && !trPageblk.getParent().getParent().getIsRoot());
                assert(trPageblk.getParent().getParent().getParent() != null && trPageblk.getParent().getParent().getParent().getIsRoot());
            }
        }

        @Test
        public void whenTheDBReturnsABookWithTwoSectionsWithICSThenTheICBookSectionShouldHaveTwoChildSections(){
            Map<String, Object> mockSection = new HashedMap();
            mockSection.put("id", UUID.fromString("99000000-0000-0000-0000-000000000000"));
            mockSection.put("parent_id", UUID.fromString("20000000-0000-0000-0000-000000000000"));
            mockSection.put("node_order", new Integer(1));
            mockSection.put("title", "mock title");
            mockSection.put("node_key", "mock key");
            mockSection.put(TECHLIB_TREE_DEPTH, 3);

            Map<String, Object> mockPageblk = new HashedMap();
            mockPageblk.put("id", UUID.fromString("88000000-0000-0000-0000-000000000000"));
            mockPageblk.put("parent_id", UUID.fromString("99000000-0000-0000-0000-000000000000"));
            mockPageblk.put("node_order", new Integer(1));
            mockPageblk.put("node_type", IC_PAGEBLK_TYPE);
            mockPageblk.put("title", "ABC Pageblk");
            mockPageblk.put("node_key", "ABC key");
            mockPageblk.put("filename", "filename." + FILE_EXTENSION);
            mockPageblk.put(TECHLIB_TREE_DEPTH, 3);


            List<Map<String, Object>> mockDBReturnList = new ArrayList<>();
            mockDBReturnList.addAll(mockDBReturnListWithIcsAndTrs);
            mockDBReturnList.add(mockSection);
            mockDBReturnList.add(mockPageblk);
            when(techLibJDBCTemplateMock.queryForList(isA(String.class), isA(SqlParameterSource.class))).thenReturn(mockDBReturnList);

            List<BookcaseTocDAO> resultList = bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null);
            BookcaseTocDAO trBook = getBookByTitle(IC_BOOKCASE_TOC_MODEL_TITLE, resultList);
            BookcaseTocDAO lmmBookSection = getBookByTitle(TYPE_LMM.toUpperCase(), trBook.getChildren());

            assertEquals(2, lmmBookSection.getChildren().size());

        }

        @Test
        public void whenTheDBReturnsABookWithTwoSectionsWithTRsThenTheTRBookSectionShouldHaveTwoChildSections(){
            Map<String, Object> mockSection = new HashedMap();
            mockSection.put("id", UUID.fromString("99000000-0000-0000-0000-000000000000"));
            mockSection.put("parent_id", UUID.fromString("20000000-0000-0000-0000-000000000000"));
            mockSection.put("node_order", new Integer(1));
            mockSection.put("title", "mock title");
            mockSection.put("node_key", "mock key");
            mockSection.put(TECHLIB_TREE_DEPTH, 3);

            Map<String, Object> mockPageblk = new HashedMap();
            mockPageblk.put("id", UUID.fromString("88000000-0000-0000-0000-000000000000"));
            mockPageblk.put("parent_id", UUID.fromString("99000000-0000-0000-0000-000000000000"));
            mockPageblk.put("node_order", new Integer(1));
            mockPageblk.put("node_type", TR_PAGEBLK_TYPE);
            mockPageblk.put("title", "ABC Pageblk");
            mockPageblk.put("node_key", "ABC key");
            mockPageblk.put("filename", "filename." + FILE_EXTENSION);
            mockPageblk.put(TECHLIB_TREE_DEPTH, 3);


            List<Map<String, Object>> mockDBReturnList = new ArrayList<>();
            mockDBReturnList.addAll(mockDBReturnListWithIcsAndTrs);
            mockDBReturnList.add(mockSection);
            mockDBReturnList.add(mockPageblk);
            when(techLibJDBCTemplateMock.queryForList(isA(String.class), isA(SqlParameterSource.class))).thenReturn(mockDBReturnList);

            List<BookcaseTocDAO> resultList = bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null);
            BookcaseTocDAO trBook = getBookByTitle(TR_BOOKCASE_TOC_MODEL_TITLE, resultList);
            BookcaseTocDAO lmmBookSection = getBookByTitle(TYPE_LMM.toUpperCase(), trBook.getChildren());

            assertEquals(2, lmmBookSection.getChildren().size());

        }

        @Test
        public void whenTheDBReturnsABookWithTwoSectionsWithICSThenTheICsShouldBeInTheICBookAsChildrenOfSectionsWithTheirParentSectionsTitle(){
            Map<String, Object> mockSection = new HashedMap();
            mockSection.put("id", UUID.fromString("99000000-0000-0000-0000-000000000000"));
            mockSection.put("parent_id", UUID.fromString("20000000-0000-0000-0000-000000000000"));
            mockSection.put("node_order", new Integer(1));
            mockSection.put("title", "mock title");
            mockSection.put("node_key", "mock key");
            mockSection.put(TECHLIB_TREE_DEPTH, 3);

            Map<String, Object> mockICPageblk = new HashedMap();
            mockICPageblk.put("id", UUID.fromString("88000000-0000-0000-0000-000000000000"));
            mockICPageblk.put("parent_id", UUID.fromString("99000000-0000-0000-0000-000000000000"));
            mockICPageblk.put("node_order", new Integer(1));
            mockICPageblk.put("node_type", IC_PAGEBLK_TYPE);
            mockICPageblk.put("title", "ABC Pageblk");
            mockICPageblk.put("node_key", "ABC key");
            mockICPageblk.put("filename", "filename." + FILE_EXTENSION);
            mockICPageblk.put(TECHLIB_TREE_DEPTH, 3);


            List<Map<String, Object>> mockDBReturnList = new ArrayList<>();
            mockDBReturnList.addAll(mockDBReturnListWithIcsAndTrs);
            mockDBReturnList.add(mockSection);
            mockDBReturnList.add(mockICPageblk);
            when(techLibJDBCTemplateMock.queryForList(isA(String.class), isA(SqlParameterSource.class))).thenReturn(mockDBReturnList);

            List<BookcaseTocDAO> resultList = bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null);
            BookcaseTocDAO trBook = getBookByTitle(IC_BOOKCASE_TOC_MODEL_TITLE, resultList);
            List<BookcaseTocDAO> lmmBookICs = getBookByTitle(TYPE_LMM.toUpperCase(), trBook.getChildren()).getLeaves();
            BookcaseTocDAO abcPageblk = getBookByTitle(mockICPageblk.get("title").toString(),lmmBookICs);
            BookcaseTocDAO pageblk6 = getBookByTitle(pageblkItem6.get("title").toString(), lmmBookICs);

            assertEquals(pageblk6.getParent().getTitle(), sectionItem4.get("node_key"));
            assertEquals(abcPageblk.getParent().getTitle(), mockSection.get("node_key"));

        }

        @Test
        public void whenTheDBReturnsABookWithTwoSectionsWithTRsThenTheTRsShouldBeInTheTRBookAsChildrenOfSectionsWithTheirParentSectionsTitle(){
            Map<String, Object> mockSection = new HashedMap();
            mockSection.put("id", UUID.fromString("99000000-0000-0000-0000-000000000000"));
            mockSection.put("parent_id", UUID.fromString("20000000-0000-0000-0000-000000000000"));
            mockSection.put("node_order", new Integer(1));
            mockSection.put("title", "mock title");
            mockSection.put("node_key", "mock key");
            mockSection.put(TECHLIB_TREE_DEPTH, 3);

            Map<String, Object> mockTRPageblk = new HashedMap();
            mockTRPageblk.put("id", UUID.fromString("88000000-0000-0000-0000-000000000000"));
            mockTRPageblk.put("parent_id", UUID.fromString("99000000-0000-0000-0000-000000000000"));
            mockTRPageblk.put("node_order", new Integer(1));
            mockTRPageblk.put("node_type", TR_PAGEBLK_TYPE);
            mockTRPageblk.put("title", "ABC Pageblk");
            mockTRPageblk.put("node_key", "ABC key");
            mockTRPageblk.put("filename", "filename." + FILE_EXTENSION);
            mockTRPageblk.put(TECHLIB_TREE_DEPTH, 3);


            List<Map<String, Object>> mockDBReturnList = new ArrayList<>();
            mockDBReturnList.addAll(mockDBReturnListWithIcsAndTrs);
            mockDBReturnList.add(mockSection);
            mockDBReturnList.add(mockTRPageblk);
            when(techLibJDBCTemplateMock.queryForList(isA(String.class), isA(SqlParameterSource.class))).thenReturn(mockDBReturnList);

            List<BookcaseTocDAO> resultList = bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null);
            BookcaseTocDAO trBook = getBookByTitle(TR_BOOKCASE_TOC_MODEL_TITLE, resultList);
            List<BookcaseTocDAO> lmmBookICs = getBookByTitle(TYPE_LMM.toUpperCase(), trBook.getChildren()).getLeaves();
            BookcaseTocDAO abcPageblk = getBookByTitle(mockTRPageblk.get("title").toString(),lmmBookICs);
            BookcaseTocDAO pageblk8 = getBookByTitle(pageblkItem8.get("title").toString(), lmmBookICs);

            assertEquals(pageblk8.getParent().getTitle(), sectionItem4.get("node_key"));
            assertEquals(abcPageblk.getParent().getTitle(), mockSection.get("node_key"));

        }

        @Test
        public void whenTheDBReturnsTwoBooksWithTheSameTypeThatHaveICsThenTheICBookShouldStillOnlyContainOneBookSectionWithThatType(){
            Map<String, Object> mockSection = new HashedMap();
            mockSection.put("id", UUID.fromString("99000000-0000-0000-0000-000000000000"));
            mockSection.put("parent_id", UUID.fromString("30000000-0000-0000-0000-000000000000"));
            mockSection.put("node_order", new Integer(1));
            mockSection.put("title", "Section with ICs");
            mockSection.put("node_key", "mockSection key");
            mockSection.put(TECHLIB_TREE_DEPTH, 3);

            Map<String, Object> mockICPageblk = new HashedMap();
            mockICPageblk.put("id", UUID.fromString("88000000-0000-0000-0000-000000000000"));
            mockICPageblk.put("parent_id", UUID.fromString("99000000-0000-0000-0000-000000000000"));
            mockICPageblk.put("node_order", new Integer(1));
            mockICPageblk.put("node_type", IC_PAGEBLK_TYPE);
            mockICPageblk.put("title", "Mock IC Pageblk");
            mockICPageblk.put("node_key", "mockICPageblk key");
            mockICPageblk.put("filename", "filename." + FILE_EXTENSION);
            mockICPageblk.put(TECHLIB_TREE_DEPTH, 3);


            List<Map<String, Object>> mockDBReturnList = new ArrayList<>();
            mockDBReturnList.addAll(mockDBReturnListWithIcsAndTrs);
            mockDBReturnList.add(mockSection);
            mockDBReturnList.add(mockICPageblk);

            when(techLibJDBCTemplateMock.queryForList(isA(String.class), isA(SqlParameterSource.class))).thenReturn(mockDBReturnList);

            List<BookcaseTocDAO> resultList = bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null);
            BookcaseTocDAO icBook = getBookByTitle(IC_BOOKCASE_TOC_MODEL_TITLE, resultList);

            int sectionsWithTitleLMM = 0;
            for(BookcaseTocDAO section: icBook.getChildren()){
                if(section.getTitle() != null && section.getTitle().equals(TYPE_LMM.toUpperCase()))
                    sectionsWithTitleLMM++;
            }
            assertEquals(sectionsWithTitleLMM, 1);

        }

        @Test
        public void whenTheDBReturnsTwoBooksWithTheSameTypeThatHaveTRsThenTheTRBookShouldStillOnlyContainOneBookSectionWithThatType(){
            Map<String, Object> mockSection = new HashedMap();
            mockSection.put("id", UUID.fromString("99000000-0000-0000-0000-000000000000"));
            mockSection.put("parent_id", UUID.fromString("30000000-0000-0000-0000-000000000000"));
            mockSection.put("node_order", new Integer(1));
            mockSection.put("title", "ABC Section");
            mockSection.put("node_key", "mockSection key");
            mockSection.put(TECHLIB_TREE_DEPTH, 3);

            Map<String, Object> mockTRPageblk = new HashedMap();
            mockTRPageblk.put("id", UUID.fromString("88000000-0000-0000-0000-000000000000"));
            mockTRPageblk.put("parent_id", UUID.fromString("99000000-0000-0000-0000-000000000000"));
            mockTRPageblk.put("node_order", new Integer(1));
            mockTRPageblk.put("node_type", TR_PAGEBLK_TYPE);
            mockTRPageblk.put("title", "ABC Pageblk");
            mockTRPageblk.put("node_key", "ABC key");
            mockTRPageblk.put("filename", "filename." + FILE_EXTENSION);
            mockTRPageblk.put(TECHLIB_TREE_DEPTH, 3);


            List<Map<String, Object>> mockDBReturnList = new ArrayList<>();
            mockDBReturnList.addAll(mockDBReturnListWithIcsAndTrs);
            mockDBReturnList.add(mockSection);
            mockDBReturnList.add(mockTRPageblk);

            when(techLibJDBCTemplateMock.queryForList(isA(String.class), isA(SqlParameterSource.class))).thenReturn(mockDBReturnList);

            List<BookcaseTocDAO> resultList = bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null);
            BookcaseTocDAO icBook = getBookByTitle(TR_BOOKCASE_TOC_MODEL_TITLE, resultList);

            int sectionsWithTitleLMM = 0;
            for(BookcaseTocDAO section: icBook.getChildren()){
                if(section.getTitle() != null && section.getTitle().equals(TYPE_LMM.toUpperCase()))
                    sectionsWithTitleLMM++;
            }
            assertEquals(sectionsWithTitleLMM, 1);

        }

        @Test
        public void whenTheDBReturnsTwoBooksWithTheSameTypeThatHaveICsThenTheICsfromBothBooksShouldBeInOneBookSectionInTheIcBook(){
            Map<String, Object> mockSection = new HashedMap();
            mockSection.put("id", UUID.fromString("99000000-0000-0000-0000-000000000000"));
            mockSection.put("parent_id", UUID.fromString("30000000-0000-0000-0000-000000000000"));
            mockSection.put("node_order", new Integer(1));
            mockSection.put("title", "Section with ICs");
            mockSection.put("node_key", "mockSection key");
            mockSection.put(TECHLIB_TREE_DEPTH, 3);

            Map<String, Object> mockICPageblk = new HashedMap();
            mockICPageblk.put("id", UUID.fromString("88000000-0000-0000-0000-000000000000"));
            mockICPageblk.put("parent_id", UUID.fromString("99000000-0000-0000-0000-000000000000"));
            mockICPageblk.put("node_order", new Integer(1));
            mockICPageblk.put("node_type", IC_PAGEBLK_TYPE);
            mockICPageblk.put("title", "Mock IC Pageblk");
            mockICPageblk.put("node_key", "mockICPageblk key");
            mockICPageblk.put("filename", "filename." + FILE_EXTENSION);
            mockICPageblk.put(TECHLIB_TREE_DEPTH, 3);


            List<Map<String, Object>> mockDBReturnList = new ArrayList<>();
            mockDBReturnList.addAll(mockDBReturnListWithIcsAndTrs);
            mockDBReturnList.add(mockSection);
            mockDBReturnList.add(mockICPageblk);

            when(techLibJDBCTemplateMock.queryForList(isA(String.class), isA(SqlParameterSource.class))).thenReturn(mockDBReturnList);

            List<BookcaseTocDAO> resultList = bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null);
            BookcaseTocDAO icBook = getBookByTitle(IC_BOOKCASE_TOC_MODEL_TITLE, resultList);
            BookcaseTocDAO lmmSection = getBookByTitle(TYPE_LMM.toUpperCase(), icBook.getChildren());
            assert(listContainsId(mockICPageblk.get("id").toString(), lmmSection.getLeaves()));
            assert(listContainsId(pageblkItem6.get("id").toString(), lmmSection.getLeaves()));

        }

        @Test
        public void whenTheDBReturnsTwoBooksWithTheSameTypeThatHaveTRsThenTheTRsfromBothBooksShouldBeInOneBookSectionInTheTrBook(){
            Map<String, Object> mockSection = new HashedMap();
            mockSection.put("id", UUID.fromString("99000000-0000-0000-0000-000000000000"));
            mockSection.put("parent_id", UUID.fromString("30000000-0000-0000-0000-000000000000"));
            mockSection.put("node_order", new Integer(1));
            mockSection.put("title", "ABC Section");
            mockSection.put("node_key", "mockSection key");
            mockSection.put(TECHLIB_TREE_DEPTH, 3);

            Map<String, Object> mockTRPageblk = new HashedMap();
            mockTRPageblk.put("id", UUID.fromString("88000000-0000-0000-0000-000000000000"));
            mockTRPageblk.put("parent_id", UUID.fromString("99000000-0000-0000-0000-000000000000"));
            mockTRPageblk.put("node_order", new Integer(1));
            mockTRPageblk.put("node_type", TR_PAGEBLK_TYPE);
            mockTRPageblk.put("title", "ABC Pageblk");
            mockTRPageblk.put("node_key", "ABC key");
            mockTRPageblk.put("filename", "filename." + FILE_EXTENSION);
            mockTRPageblk.put(TECHLIB_TREE_DEPTH, 3);


            List<Map<String, Object>> mockDBReturnList = new ArrayList<>();
            mockDBReturnList.addAll(mockDBReturnListWithIcsAndTrs);
            mockDBReturnList.add(mockSection);
            mockDBReturnList.add(mockTRPageblk);
            when(techLibJDBCTemplateMock.queryForList(isA(String.class), isA(SqlParameterSource.class))).thenReturn(mockDBReturnList);

            List<BookcaseTocDAO> resultList = bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null);
            BookcaseTocDAO trBook = getBookByTitle(TR_BOOKCASE_TOC_MODEL_TITLE, resultList);
            BookcaseTocDAO lmmSection = getBookByTitle(TYPE_LMM.toUpperCase(), trBook.getChildren());
            assert(listContainsId(mockTRPageblk.get("id").toString(), lmmSection.getLeaves()));
            assert(listContainsId(pageblkItem8.get("id").toString(), lmmSection.getLeaves()));

        }

        @Test
        public void whenTheDBReturnsTwoBooksWithTheSameTypeThatHaveSectionsWithTheSameTitleWhichHaveICsThenTheICsfromBothBooksShouldBeInOneSectionInABookSectionInTheICBook(){
            Map<String, Object> mockSection = new HashedMap();
            mockSection.put("id", UUID.fromString("99000000-0000-0000-0000-000000000000"));
            mockSection.put("parent_id", UUID.fromString("30000000-0000-0000-0000-000000000000"));
            mockSection.put("node_order", new Integer(1));
            mockSection.put("title", sectionItem4.get("title"));
            mockSection.put("node_key", sectionItem4.get("node_key"));
            mockSection.put(TECHLIB_TREE_DEPTH, 3);

            Map<String, Object> mockTRPageblk = new HashedMap();
            mockTRPageblk.put("id", UUID.fromString("88000000-0000-0000-0000-000000000000"));
            mockTRPageblk.put("parent_id", UUID.fromString("99000000-0000-0000-0000-000000000000"));
            mockTRPageblk.put("node_order", new Integer(1));
            mockTRPageblk.put("node_type", IC_PAGEBLK_TYPE);
            mockTRPageblk.put("title", "ABC Pageblk");
            mockTRPageblk.put("node_key", "ABC key");
            mockTRPageblk.put("filename", "filename." + FILE_EXTENSION);
            mockTRPageblk.put(TECHLIB_TREE_DEPTH, 3);


            List<Map<String, Object>> mockDBReturnList = new ArrayList<>();
            mockDBReturnList.addAll(mockDBReturnListWithIcsAndTrs);
            mockDBReturnList.add(mockSection);
            mockDBReturnList.add(mockTRPageblk);
            when(techLibJDBCTemplateMock.queryForList(isA(String.class), isA(SqlParameterSource.class))).thenReturn(mockDBReturnList);

            List<BookcaseTocDAO> resultList = bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null);
            BookcaseTocDAO trBook = getBookByTitle(IC_BOOKCASE_TOC_MODEL_TITLE, resultList);
            BookcaseTocDAO lmmSection = getBookByTitle(TYPE_LMM.toUpperCase(), trBook.getChildren());

            assertEquals(1, lmmSection.getChildren().size());
            assertEquals(lmmSection.getChildren().get(0).getTitle(), sectionItem4.get("node_key"));

        }

        @Test
        public void whenTheDBReturnsTwoBooksWithTheSameTypeThatHaveSectionsWithTheSameTitleWhichHaveTRsThenTheTRsfromBothBooksShouldBeInOneSectionInABookSectionInTheTrBook(){
            Map<String, Object> mockSection = new HashedMap();
            mockSection.put("id", UUID.fromString("99000000-0000-0000-0000-000000000000"));
            mockSection.put("parent_id", UUID.fromString("30000000-0000-0000-0000-000000000000"));
            mockSection.put("node_order", new Integer(1));
            mockSection.put("title", sectionItem4.get("title"));
            mockSection.put("node_key", sectionItem4.get("node_key"));
            mockSection.put(TECHLIB_TREE_DEPTH, 3);

            Map<String, Object> mockTRPageblk = new HashedMap();
            mockTRPageblk.put("id", UUID.fromString("88000000-0000-0000-0000-000000000000"));
            mockTRPageblk.put("parent_id", UUID.fromString("99000000-0000-0000-0000-000000000000"));
            mockTRPageblk.put("node_order", new Integer(1));
            mockTRPageblk.put("node_type", TR_PAGEBLK_TYPE);
            mockTRPageblk.put("title", "ABC Pageblk");
            mockTRPageblk.put("node_key", "ABC key");
            mockTRPageblk.put("filename", "filename." + FILE_EXTENSION);
            mockTRPageblk.put(TECHLIB_TREE_DEPTH, 3);


            List<Map<String, Object>> mockDBReturnList = new ArrayList<>();
            mockDBReturnList.addAll(mockDBReturnListWithIcsAndTrs);
            mockDBReturnList.add(mockSection);
            mockDBReturnList.add(mockTRPageblk);
            when(techLibJDBCTemplateMock.queryForList(isA(String.class), isA(SqlParameterSource.class))).thenReturn(mockDBReturnList);

            List<BookcaseTocDAO> resultList = bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null);
            BookcaseTocDAO trBook = getBookByTitle(TR_BOOKCASE_TOC_MODEL_TITLE, resultList);
            BookcaseTocDAO lmmSection = getBookByTitle(TYPE_LMM.toUpperCase(), trBook.getChildren());

            assertEquals(1, lmmSection.getChildren().size());
            assertEquals(lmmSection.getChildren().get(0).getTitle(), sectionItem4.get("node_key"));

        }

        @Test
        public void whenTheDBReturnsTwoBooksWithTheSameTypeThatHaveICsThenTheICsfromBothBooksShouldBeInOneBookSectionInTheIcBookSortedByTitleAscending(){
            Map<String, Object> mockSection1 = new HashedMap();
            mockSection1.put("id", UUID.fromString("99000000-0000-0000-0000-000000000000"));
            mockSection1.put("parent_id", UUID.fromString("30000000-0000-0000-0000-000000000000"));
            mockSection1.put("node_order", new Integer(1));
            mockSection1.put("title", "ABC Section");
            mockSection1.put("node_key", "mockSection key");
            mockSection1.put(TECHLIB_TREE_DEPTH, 3);

            Map<String, Object> mockICPageblk1 = new HashedMap();
            mockICPageblk1.put("id", UUID.fromString("88000000-0000-0000-0000-000000000000"));
            mockICPageblk1.put("parent_id", UUID.fromString("99000000-0000-0000-0000-000000000000"));
            mockICPageblk1.put("node_order", new Integer(1));
            mockICPageblk1.put("node_type", IC_PAGEBLK_TYPE);
            mockICPageblk1.put("title", "ABC Pageblk Title");
            mockICPageblk1.put("node_key", "ABC key");
            mockICPageblk1.put("filename", "filename." + FILE_EXTENSION);
            mockICPageblk1.put(TECHLIB_TREE_DEPTH, 3);

            Map<String, Object> mockSection2 = new HashedMap();
            mockSection2.put("id", UUID.fromString("99900000-0000-0000-0000-000000000000"));
            mockSection2.put("parent_id", UUID.fromString("30000000-0000-0000-0000-000000000000"));
            mockSection2.put("node_order", new Integer(1));
            mockSection2.put("title", "ZYX Section");
            mockSection2.put("node_key", "ZYX key");
            mockSection2.put(TECHLIB_TREE_DEPTH, 3);

            Map<String, Object> mockICPageblk2 = new HashedMap();
            mockICPageblk2.put("id", UUID.fromString("88800000-0000-0000-0000-000000000000"));
            mockICPageblk2.put("parent_id", UUID.fromString("99900000-0000-0000-0000-000000000000"));
            mockICPageblk2.put("node_order", new Integer(1));
            mockICPageblk2.put("node_type", IC_PAGEBLK_TYPE);
            mockICPageblk2.put("title", "ZYX Pageblk");
            mockICPageblk2.put("node_key", "mockICPageblk key");
            mockICPageblk2.put("filename", "filename." + FILE_EXTENSION);
            mockICPageblk2.put(TECHLIB_TREE_DEPTH, 3);


            List<Map<String, Object>> mockDBReturnList = new ArrayList<>();
            mockDBReturnList.addAll(mockDBReturnListWithIcsAndTrs);
            mockDBReturnList.add(mockSection1);
            mockDBReturnList.add(mockICPageblk1);
            mockDBReturnList.add(mockSection2);
            mockDBReturnList.add(mockICPageblk2);

            when(techLibJDBCTemplateMock.queryForList(isA(String.class), isA(SqlParameterSource.class))).thenReturn(mockDBReturnList);

            List<BookcaseTocDAO> resultList = bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null);
            BookcaseTocDAO icBook = getBookByTitle(IC_BOOKCASE_TOC_MODEL_TITLE, resultList);

            List<BookcaseTocDAO> lmmSections = getBookByTitle(TYPE_LMM.toUpperCase(), icBook.getChildren()).getChildren();
            assertEquals(lmmSections.get(0).getId(), mockSection1.get("id").toString());
            assertEquals(lmmSections.get(1).getId(), sectionItem4.get("id").toString());
            assertEquals(lmmSections.get(2).getId(), mockSection2.get("id").toString());
        }

        @Test
        public void whenTheDBReturnsTwoBooksWithTheSameTypeThatHaveTRsThenTheTRsfromBothBooksShouldBeInOneBookSectionInTheTrBookSortedByTitleAscending(){
            Map<String, Object> mockSection1 = new HashedMap();
            mockSection1.put("id", UUID.fromString("99000000-0000-0000-0000-000000000000"));
            mockSection1.put("parent_id", UUID.fromString("30000000-0000-0000-0000-000000000000"));
            mockSection1.put("node_order", new Integer(1));
            mockSection1.put("title", "ABC Section");
            mockSection1.put("node_key", "ABC key");
            mockSection1.put(TECHLIB_TREE_DEPTH, 3);

            Map<String, Object> mockTRPageblk1 = new HashedMap();
            mockTRPageblk1.put("id", UUID.fromString("88000000-0000-0000-0000-000000000000"));
            mockTRPageblk1.put("parent_id", UUID.fromString("99000000-0000-0000-0000-000000000000"));
            mockTRPageblk1.put("node_order", new Integer(1));
            mockTRPageblk1.put("node_type", TR_PAGEBLK_TYPE);
            mockTRPageblk1.put("title", "ABC Pageblk Title");
            mockTRPageblk1.put("node_key", "ABC key");
            mockTRPageblk1.put("filename", "filename." + FILE_EXTENSION);
            mockTRPageblk1.put(TECHLIB_TREE_DEPTH, 3);

            Map<String, Object> mockSection2 = new HashedMap();
            mockSection2.put("id", UUID.fromString("99900000-0000-0000-0000-000000000000"));
            mockSection2.put("parent_id", UUID.fromString("30000000-0000-0000-0000-000000000000"));
            mockSection2.put("node_order", new Integer(1));
            mockSection2.put("title", "ZYX Section");
            mockSection2.put("node_key", "ZYX key");
            mockSection2.put(TECHLIB_TREE_DEPTH, 3);

            Map<String, Object> mockTRPageblk2 = new HashedMap();
            mockTRPageblk2.put("id", UUID.fromString("88800000-0000-0000-0000-000000000000"));
            mockTRPageblk2.put("parent_id", UUID.fromString("99900000-0000-0000-0000-000000000000"));
            mockTRPageblk2.put("node_order", new Integer(1));
            mockTRPageblk2.put("node_type", TR_PAGEBLK_TYPE);
            mockTRPageblk2.put("title", "ZYX Pageblk");
            mockTRPageblk2.put("node_key", "ZYX key");
            mockTRPageblk2.put("filename", "filename." + FILE_EXTENSION);
            mockTRPageblk2.put(TECHLIB_TREE_DEPTH, 3);

            List<Map<String, Object>> mockDBReturnList = new ArrayList<>();
            mockDBReturnList.addAll(mockDBReturnListWithIcsAndTrs);
            mockDBReturnList.add(mockSection1);
            mockDBReturnList.add(mockTRPageblk1);
            mockDBReturnList.add(mockSection2);
            mockDBReturnList.add(mockTRPageblk2);

            when(techLibJDBCTemplateMock.queryForList(isA(String.class), isA(SqlParameterSource.class))).thenReturn(mockDBReturnList);

            List<BookcaseTocDAO> resultList = bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null);
            BookcaseTocDAO trBook = getBookByTitle(TR_BOOKCASE_TOC_MODEL_TITLE, resultList);

            List<BookcaseTocDAO> lmmSections = getBookByTitle(TYPE_LMM.toUpperCase(), trBook.getChildren()).getChildren();
            assertEquals(lmmSections.get(0).getId(), mockSection1.get("id").toString());
            assertEquals(lmmSections.get(1).getId(), sectionItem4.get("id").toString());
            assertEquals(lmmSections.get(2).getId(), mockSection2.get("id").toString());
        }

        @Test
        public void whenTheDBReturnsTRPageblkThatIsADirectChildOfABookThenTheTRBookShouldHaveASectionPlaceholderTitledWithTwoDashes(){
            Map<String, Object> mockTRPageblk1 = new HashedMap();
            mockTRPageblk1.put("id", UUID.fromString("88000000-0000-0000-0000-000000000000"));
            mockTRPageblk1.put("parent_id", UUID.fromString("30000000-0000-0000-0000-000000000000"));
            mockTRPageblk1.put("node_order", new Integer(1));
            mockTRPageblk1.put("node_type", TR_PAGEBLK_TYPE);
            mockTRPageblk1.put("title", "ABC Pageblk Title");
            mockTRPageblk1.put("node_key", "ABC key");
            mockTRPageblk1.put("filename", "filename." + FILE_EXTENSION);
            mockTRPageblk1.put(TECHLIB_TREE_DEPTH, 3);

            List<Map<String, Object>> mockDBReturnList = new ArrayList<>();
            mockDBReturnList.addAll(mockDBReturnListWithIcsAndTrs);
            mockDBReturnList.add(mockTRPageblk1);

            when(techLibJDBCTemplateMock.queryForList(isA(String.class), isA(SqlParameterSource.class))).thenReturn(mockDBReturnList);

            List<BookcaseTocDAO> resultList = bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null);
            BookcaseTocDAO trBook = getBookByTitle(TR_BOOKCASE_TOC_MODEL_TITLE, resultList);

            List<BookcaseTocDAO> lmmSections = getBookByTitle(TYPE_LMM.toUpperCase(), trBook.getChildren()).getChildren();
            BookcaseTocDAO placeholderSection = getBookByTitle(PLACEHOLDER_SECTION_TITLE, lmmSections);

            assert(placeholderSection != null);

        }

        @Test
        public void whenTheDBReturnsTRPageblkThatIsADirectChildOfABookThenThatPageblkShouldHaveAParentInTheTRBookThatIsASectionPlaceholder(){
            Map<String, Object> mockTRPageblk1 = new HashedMap();
            mockTRPageblk1.put("id", UUID.fromString("88000000-0000-0000-0000-000000000000"));
            mockTRPageblk1.put("parent_id", UUID.fromString("30000000-0000-0000-0000-000000000000"));
            mockTRPageblk1.put("node_order", new Integer(1));
            mockTRPageblk1.put("node_type", TR_PAGEBLK_TYPE);
            mockTRPageblk1.put("title", "ABC Pageblk Title");
            mockTRPageblk1.put("node_key", "ABC key");
            mockTRPageblk1.put("filename", "filename." + FILE_EXTENSION);
            mockTRPageblk1.put(TECHLIB_TREE_DEPTH, 3);

            List<Map<String, Object>> mockDBReturnList = new ArrayList<>();
            mockDBReturnList.addAll(mockDBReturnListWithIcsAndTrs);
            mockDBReturnList.add(mockTRPageblk1);

            when(techLibJDBCTemplateMock.queryForList(isA(String.class), isA(SqlParameterSource.class))).thenReturn(mockDBReturnList);

            List<BookcaseTocDAO> resultList = bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null);
            BookcaseTocDAO trBook = getBookByTitle(TR_BOOKCASE_TOC_MODEL_TITLE, resultList);

            List<BookcaseTocDAO> lmmSections = getBookByTitle(TYPE_LMM.toUpperCase(), trBook.getChildren()).getChildren();
            BookcaseTocDAO placeholderSection = getBookByTitle(PLACEHOLDER_SECTION_TITLE, lmmSections);

            assertEquals(placeholderSection.getChildren().get(0).getId(), mockTRPageblk1.get("id").toString());

        }


        /**********************************************************************************************
         *************************************Sort Order Tests*****************************************
         **********************************************************************************************/

        @Test
        public void methodShouldReturnAListOfBookcaseTOCModelsSortedByTheOrderFieldDescending(){
            List<BookcaseTocDAO> resultList = bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);

            int counter = 0;
            for (int i = 0; i < resultList.size(); i++){
                if (!resultList.get(i).getId().equals("10000000-0000-0000-0000-000000000000"))
                    assertEquals(i +1, resultList.get(i).getOrder());
            }
        }

        @Test
        public void methodShouldReturnAListOfBookcaseTOCModelsWithIcBookLast(){
            List<BookcaseTocDAO> resultList = bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            assertEquals(resultList.get(resultList.size() - 1), getBookByTitle(DataConstants.IC_BOOKCASE_TOC_MODEL_TITLE, resultList));

        }

        @Test
        public void methodShouldReturnAListOfBookcaseTOCModelsWithTRBookSecondToLast(){
            List<BookcaseTocDAO> resultList = bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            assertEquals(resultList.get(resultList.size() - 2), getBookByTitle(DataConstants.TR_BOOKCASE_TOC_MODEL_TITLE, resultList));
        }


        @Test
        public void methodShouldReturnAListOfBookcaseTOCModelWithAllChildrenLevelsSortedByOrderDescendingWithICandTRPageblksAfterTheManualPageblkTheyReference(){
            Map<String, Object> mockSection1 = new HashedMap();
            mockSection1.put("id", UUID.fromString("99000000-0000-0000-0000-000000000000"));
            mockSection1.put("parent_id", UUID.fromString("30000000-0000-0000-0000-000000000000"));
            mockSection1.put("node_order", new Integer(1));
            mockSection1.put("title", "ABC Section");
            mockSection1.put("node_key", "ABC key");
            mockSection1.put(TECHLIB_TREE_DEPTH, 3);

            Map<String, Object> mockTRPageblk1 = new HashedMap();
            mockTRPageblk1.put("id", UUID.fromString("88000000-0000-0000-0000-000000000000"));
            mockTRPageblk1.put("parent_id", UUID.fromString("99000000-0000-0000-0000-000000000000"));
            mockTRPageblk1.put("node_order", new Integer(1));
            mockTRPageblk1.put("node_type", TR_PAGEBLK_TYPE);
            mockTRPageblk1.put("title", "ABC Pageblk Title");
            mockTRPageblk1.put("node_key", "ABC key");
            mockTRPageblk1.put("filename", "filename." + FILE_EXTENSION);
            mockTRPageblk1.put(TECHLIB_TREE_DEPTH, 3);

            Map<String, Object> mockPageblk1 = new HashedMap();
            mockPageblk1.put("id", UUID.fromString("89000000-0000-0000-0000-000000000000"));
            mockPageblk1.put("parent_id", UUID.fromString("99000000-0000-0000-0000-000000000000"));
            mockPageblk1.put("node_order", new Integer(1));
            mockPageblk1.put("node_type", MANUAL_PAGEBLK_TYPE);
            mockPageblk1.put("title", "ABC Pageblk Title");
            mockPageblk1.put("node_key", "ABC key");
            mockPageblk1.put("filename", "filename." + FILE_EXTENSION);
            mockPageblk1.put(TECHLIB_TREE_DEPTH, 3);

            Map<String, Object> mockSection2 = new HashedMap();
            mockSection2.put("id", UUID.fromString("99900000-0000-0000-0000-000000000000"));
            mockSection2.put("parent_id", UUID.fromString("30000000-0000-0000-0000-000000000000"));
            mockSection2.put("node_order", new Integer(2));
            mockSection2.put("title", "ZYX Section");
            mockSection2.put("node_key", "ZYX key");
            mockSection2.put(TECHLIB_TREE_DEPTH, 3);

            Map<String, Object> mockTRPageblk2 = new HashedMap();
            mockTRPageblk2.put("id", UUID.fromString("88800000-0000-0000-0000-000000000000"));
            mockTRPageblk2.put("parent_id", UUID.fromString("99900000-0000-0000-0000-000000000000"));
            mockTRPageblk2.put("node_order", new Integer(1));
            mockTRPageblk2.put("node_type", IC_PAGEBLK_TYPE);
            mockTRPageblk2.put("title", "ZYX Pageblk");
            mockTRPageblk2.put("node_key", "ZYX key");
            mockTRPageblk2.put("filename", "filename." + FILE_EXTENSION);
            mockTRPageblk2.put(TECHLIB_TREE_DEPTH, 3);

            Map<String, Object> mockPageblk2 = new HashedMap();
            mockPageblk2.put("id", UUID.fromString("88900000-0000-0000-0000-000000000000"));
            mockPageblk2.put("parent_id", UUID.fromString("99900000-0000-0000-0000-000000000000"));
            mockPageblk2.put("node_order", new Integer(1));
            mockPageblk2.put("node_type", MANUAL_PAGEBLK_TYPE);
            mockPageblk2.put("title", "ZYX Pageblk");
            mockPageblk2.put("node_key", "ZYX key");
            mockPageblk2.put("filename", "filename." + FILE_EXTENSION);
            mockPageblk2.put(TECHLIB_TREE_DEPTH, 3);

            List<Map<String, Object>> mockDBReturnList = new ArrayList<>();
            mockDBReturnList.add(bookItem3);
            mockDBReturnList.add(mockSection1);
            mockDBReturnList.add(mockPageblk1);
            mockDBReturnList.add(mockTRPageblk1);
            mockDBReturnList.add(mockSection2);
            mockDBReturnList.add(mockPageblk2);
            mockDBReturnList.add(mockTRPageblk2);

            when(techLibJDBCTemplateMock.queryForList(isA(String.class), isA(SqlParameterSource.class))).thenReturn(mockDBReturnList);
            when(programDataMock.getTocsByProgram(isA(String.class))).thenReturn(new ArrayList<>());
            when(programDataMock.getCatalogItemFilename(isA(String.class), isA(String.class), isA(String.class))).thenReturn(new HashedMap());

            List<BookcaseTocDAO> resultList = bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null);

            for (BookcaseTocDAO book : resultList) {
                //exclude tr and ic books as these are sorted by title
                if (!"tr".equalsIgnoreCase(book.getType()) || !"ic".equalsIgnoreCase(book.getType()))
                    assert(allChildLevelsAreSortedByOrderDescendingWithICandTRPageblksAfterTheManualPageblkTheyReference(book));
            }
        }

        /**********************************************************************************************
         *************************************Resource URI Tests************************************
         **********************************************************************************************/

        @Test
        public void resourceURIShouldNotBeNullOrEmptyForPageBlksWithFileNameBookcasekeyAndBookKey(){
            List<BookcaseTocDAO> resultList = bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);
            BookcaseTocDAO itemWithFileName = resultList.get(1).getChildren().get(0).getChildren().get(0).getChildren().get(0);
            assert(!(itemWithFileName.getResourceUri()  == null || itemWithFileName.getResourceUri().trim().length() == 0));
        }


        @Test
        public void resourceURIShouldBeNullForPageblksWithEmptyFilename(){
            List<BookcaseTocDAO> resultList = bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);

            BookcaseTocDAO itemWithFileName = getBookcaseTOCModelById("80000000-0000-0000-0000-000000000000", resultList.get(0).getChildren().get(0).getChildren());
            assert(itemWithFileName.getResourceUri()  == null);
        }

        @Test
        public void resourceURIShouldBeNullForPageblksWithNullFilename(){
            List<BookcaseTocDAO> resultList = bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);

            BookcaseTocDAO itemWithFileName = getBookcaseTOCModelById("90000000-0000-0000-0000-000000000000", resultList.get(0).getChildren().get(0).getChildren());
            assert(itemWithFileName.getResourceUri()  == null);
        }

        @Test
        public void resourceURIShouldBeNullForNonLeafNodes(){
            List<BookcaseTocDAO> resultList = bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null);

            assert (resourceUriIsNullForNonLeafNodes(resultList));
        }


        /**********************************************************************************************
         *************************************Books From TPS DB Tests**********************************
         **********************************************************************************************/


        @Test
        public void whenGetTOCsByProgramReturnsItemsThenResultShouldContainThoseItemsInTheOrderTheyAreReturnedFromTheDB(){
            String tocModel1Title = "tocModel1 Title";
            String tocModel2Title = "tocModel2 Title";

            TocModel tocModel1 = new TocModel();
            tocModel1.setTitle(tocModel1Title);

            TocModel tocModel2 = new TocModel();
            tocModel2.setTitle(tocModel2Title);


            when(programDataMock.getTocsByProgram(isA(String.class))).thenReturn(new ArrayList<>(Arrays.asList(tocModel1, tocModel2)));
            List<BookcaseTocDAO> resultList = bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null);

            assert(resultList.get(6).getTitle().equals(tocModel1.getTitle()));
            assert(resultList.get(7).getTitle().equals(tocModel2.getTitle()));
        }

        @Test
        public void whenGetTOCsByProgramReturnsATocModelWithTocDocModelsThenResultShouldContainABookThatHasAllOfTheTocDocModelsAsChildren(){
            String tocModel1Title = "tocModel1 Title";
            String tocDocModelTitle1 = "tocDocModel1 Title";
            String tocDocModelTitle2 = "tocDocModel2 Title";

            TocModel tocModel1 = new TocModel();
            tocModel1.setTitle(tocModel1Title);

            TocDocModel tocDocModel1 = new TocDocModel();
            tocDocModel1.setTitle(tocDocModelTitle1);
            tocDocModel1.setViewFileName("filename1");

            TocDocModel tocDocModel2 = new TocDocModel();
            tocDocModel2.setTitle(tocDocModelTitle2);
            tocDocModel2.setViewFileName("filename2");

            tocModel1.setTocDocList(Arrays.asList(tocDocModel1, tocDocModel2));
            when(programDataMock.getTocsByProgram(isA(String.class))).thenReturn(new ArrayList<>(Arrays.asList(tocModel1)));
            List<BookcaseTocDAO> resultList = bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null);

            assert(resultList.get(6).getChildren().size() == 2);
            assert(resultList.get(6).getChildren().get(0).getTitle().equals(tocDocModelTitle1));
            assert(resultList.get(6).getChildren().get(1).getTitle().equals(tocDocModelTitle2));
        }

        @Test
        public void whenGetTOCsByProgramReturnsATocModelWithTocDocModelsThenResultShouldContainABookThatHasAllOfTheTocDocModelsWithAResourceURI(){
            String tocModel1Title = "tocModel1 Title";
            String tocDocModelTitle1 = "tocDocModel1 Title";
            String tocDocModelTitle2 = "tocDocModel2 Title";

            TocModel tocModel1 = new TocModel();
            tocModel1.setTitle(tocModel1Title);
            tocModel1.setManualDocNbr("manualDocNbr");

            TocDocModel tocDocModel1 = new TocDocModel();
            tocDocModel1.setTitle(tocDocModelTitle1);
            tocDocModel1.setViewFileName("filename1");

            TocDocModel tocDocModel2 = new TocDocModel();
            tocDocModel2.setTitle(tocDocModelTitle2);
            tocDocModel2.setViewFileName("filename2");

            tocModel1.setTocDocList(Arrays.asList(tocDocModel1, tocDocModel2));
            when(programDataMock.getTocsByProgram(isA(String.class))).thenReturn(new ArrayList<>(Arrays.asList(tocModel1)));
            List<BookcaseTocDAO> resultList = bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null);

            assert(!(resultList.get(6).getChildren().get(0).getResourceUri() ==null || resultList.get(6).getChildren().get(0).getResourceUri().trim().length() == 0));
            assert(!(resultList.get(6).getChildren().get(1).getResourceUri() ==null || resultList.get(6).getChildren().get(0).getResourceUri().trim().length() == 0));
        }

        @Test
        public void whenGetTOCsByProgramThrowsExceptionThenShouldThrowTechnicalException(){
            when(programDataMock.getTocsByProgram(isA(String.class))).thenThrow(TechnicalException.class);
            Assertions.assertThrows(TechnicalException.class, () -> bookcaseTocData
                .getBookData(BOOKCASE_KEY, null,  null));
        }


        @Test
        public void whenDBReturnEmtpyListAndGetTocsByProgramReturnsItemsThenReturnListShouldBeEmpty(){
            String tocModel1Title = "tocModel1 Title";
            String tocModel2Title = "tocModel2 Title";

            TocModel tocModel1 = new TocModel();
            tocModel1.setTitle(tocModel1Title);

            TocModel tocModel2 = new TocModel();
            tocModel2.setTitle(tocModel2Title);


            when(programDataMock.getTocsByProgram(isA(String.class))).thenReturn(new ArrayList<>(Arrays.asList(tocModel1, tocModel2)));
            when(techLibJDBCTemplateMock.queryForList(isA(String.class), isA(SqlParameterSource.class))).thenReturn(new ArrayList());
            assertEquals(true, bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null).isEmpty());
        }


        /**********************************************************************************************
         ************************************** Archived SBs Tests ************************************
         **********************************************************************************************/
        @Test
        public void whenTpsDBReturnsArchivedSBsThenSbBookShouldContainSectionWithTitleArchivedSbs(){
            Map<String, Object> sbBook = new HashedMap();
            sbBook.put("id", UUID.fromString("33000000-0000-0000-0000-000000000000"));
            sbBook.put("parent_id", null);
            sbBook.put("node_order", new Integer(5));
            sbBook.put("title", "SB book");
            sbBook.put("toc_title", "SB book");
            sbBook.put("node_type", "sb");
            sbBook.put(TECHLIB_TREE_DEPTH, 1);

            mockDBReturnListWithIcsAndTrs.add(sbBook);


            String filename = "filename.html";
            Map<String, String> mockSB = new HashedMap();
            mockSB.put(TPS_VIEW_FILENAME,filename);
            Map<String, Map<String, String>> mockSBMap = new HashedMap();
            mockSBMap.put(filename, mockSB);
            when(programDataMock.getCatalogItemFilename(isA(String.class), isA(String.class), isA(String.class))).thenReturn(mockSBMap);

            List<BookcaseTocDAO> result = bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null);
            assertEquals(result.get(4).getChildren().get(0).getTitle(), DataConstants.ARCHIVED_SB_BOOK_TITLE);
        }

        @Test
        public void whenTpsDBDoesNotReturnArchivedSBsThenSbBookShouldNotContainSectionWithTitleArchivedSbs(){
            Map<String, Object> sbBook = new HashedMap();
            sbBook.put("id", UUID.fromString("33000000-0000-0000-0000-000000000000"));
            sbBook.put("parent_id", null);
            sbBook.put("node_order", new Integer(5));
            sbBook.put("title", "SB book");
            sbBook.put("toc_title", "SB book");
            sbBook.put("node_type", "sb");
            sbBook.put(TECHLIB_TREE_DEPTH, 1);

            mockDBReturnListWithIcsAndTrs.add(sbBook);



            Map<String, Map<String, String>> mockSBMap = new HashedMap();
            when(programDataMock.getCatalogItemFilename(isA(String.class), isA(String.class), isA(String.class))).thenReturn(mockSBMap);

            List<BookcaseTocDAO> result = bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null);
            assert(result.get(4).getChildren().isEmpty());
        }


        @Test
        public void whenTpsDBReturnsArchivedSBsThenSbBookShouldContainArchivedSbsSectionWithItsNumberOfChildrenEqualToTheNumberOfArchivedSBsThatDoNotMatchAnSbFromTechlib(){
            String filenameOfTechLibSB = "filenameOfTechLibSB.html";
            Map<String, Object> sbBook = new HashedMap();
            sbBook.put("id", UUID.fromString("33000000-0000-0000-0000-000000000000"));
            sbBook.put("parent_id", null);
            sbBook.put("node_order", new Integer(5));
            sbBook.put("title", "SB book");
            sbBook.put("toc_title", "SB book");
            sbBook.put("node_type", "sb");
            sbBook.put(TECHLIB_TREE_DEPTH, 1);

            Map<String, Object> sBpageblk= new HashedMap();
            sBpageblk.put("id", UUID.fromString("34000000-0000-0000-0000-000000000000"));
            sBpageblk.put("parent_id", UUID.fromString("33000000-0000-0000-0000-000000000000"));
            sBpageblk.put("node_order", new Integer(1));
            sBpageblk.put("title", "SB book");
            sBpageblk.put("toc_title", "SB book");
            sBpageblk.put("node_type", "sb");
            sBpageblk.put(TECHLIB_FILENAME, filenameOfTechLibSB);
            sBpageblk.put(TECHLIB_TREE_DEPTH, 1);

            mockDBReturnListWithIcsAndTrs.addAll(Arrays.asList(sbBook, sBpageblk));

            Map<String, Map<String, String>> mockSBMap = new HashedMap();

            String archivedSBfilename1 = "archivedSBfilename1.html";
            Map<String, String> mockSB1 = new HashedMap();
            mockSB1.put(TPS_VIEW_FILENAME,archivedSBfilename1);
            mockSBMap.put(archivedSBfilename1, mockSB1);

            String archivedSBfilename2 = "archivedSBfilename2.html";
            Map<String, String> mockSB2 = new HashedMap();
            mockSB2.put(TPS_VIEW_FILENAME,archivedSBfilename2);
            mockSBMap.put(archivedSBfilename2, mockSB2);

            Map<String, String> mockSB3 = new HashedMap();
            mockSB3.put(TPS_VIEW_FILENAME,filenameOfTechLibSB);
            mockSBMap.put(filenameOfTechLibSB, mockSB3);

            when(programDataMock.getCatalogItemFilename(isA(String.class), isA(String.class), isA(String.class))).thenReturn(mockSBMap);

            List<BookcaseTocDAO> result = bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null);
            assertEquals(result.get(4).getChildren().get(1).getChildren().size(), 2);
        }

        @Test
        public void whenTpsDBReturnsArchivedSBsThenSbBookShouldContainArchivedSbsSectionWithAChildNodeForEachArchivedSBThatDoesNotMatchTheFilenameOfASbInTechlib() {
            String filenameOfTechLibSB1 = "filenameOfTechLibSB1.html";
            String filenameOfTechLibSB2 = "filenameOfTechLibSB2.html";

            Map<String, Object> sbBook = new HashedMap();
            sbBook.put("id", UUID.fromString("33000000-0000-0000-0000-000000000000"));
            sbBook.put("parent_id", null);
            sbBook.put("node_order", new Integer(5));
            sbBook.put("title", "SB book");
            sbBook.put("toc_title", "SB book");
            sbBook.put("node_type", "sb");
            sbBook.put(TECHLIB_TREE_DEPTH, 1);

            Map<String, Object> sBpageblk1= new HashedMap();
            sBpageblk1.put("id", UUID.fromString("34000000-0000-0000-0000-000000000000"));
            sBpageblk1.put("parent_id", UUID.fromString("33000000-0000-0000-0000-000000000000"));
            sBpageblk1.put("node_order", new Integer(1));
            sBpageblk1.put("title", "SB book");
            sBpageblk1.put("toc_title", "SB book");
            sBpageblk1.put("node_type", "sb");
            sBpageblk1.put(TECHLIB_FILENAME, filenameOfTechLibSB1);
            sBpageblk1.put(TECHLIB_TREE_DEPTH, 1);

            Map<String, Object> sBpageblk2= new HashedMap();
            sBpageblk2.put("id", UUID.fromString("35000000-0000-0000-0000-000000000000"));
            sBpageblk2.put("parent_id", UUID.fromString("33000000-0000-0000-0000-000000000000"));
            sBpageblk2.put("node_order", new Integer(1));
            sBpageblk2.put("title", "SB book");
            sBpageblk2.put("toc_title", "SB book");
            sBpageblk2.put("node_type", "sb");
            sBpageblk2.put(TECHLIB_FILENAME, filenameOfTechLibSB2);
            sBpageblk2.put(TECHLIB_TREE_DEPTH, 1);

            Map<String, Map<String, String>> mockSBMap = new LinkedHashMap<>();

            Map<String, String> mockSB3 = new HashedMap();
            mockSB3.put(TPS_VIEW_FILENAME,filenameOfTechLibSB1);
            mockSBMap.put(filenameOfTechLibSB1, mockSB3);

            Map<String, String> mockSB4 = new HashedMap();
            mockSB4.put(TPS_VIEW_FILENAME,filenameOfTechLibSB2);
            mockSBMap.put(filenameOfTechLibSB2, mockSB4);

            String archivedSBfilename2 = "archivedSBfilename2.html";
            Map<String, String> mockSB2 = new HashedMap();
            mockSB2.put(TPS_VIEW_FILENAME,archivedSBfilename2);
            mockSBMap.put(archivedSBfilename2, mockSB2);

            String archivedSBfilename1 = "archivedSBfilename1.html";
            Map<String, String> mockSB1 = new HashedMap();
            mockSB1.put(TPS_VIEW_FILENAME,archivedSBfilename1);
            mockSBMap.put(archivedSBfilename1, mockSB1);



            when(programDataMock.getCatalogItemFilename(isA(String.class), isA(String.class), isA(String.class))).thenReturn(mockSBMap);
            when(techLibJDBCTemplateMock.queryForList(isA(String.class), isA(SqlParameterSource.class))).thenReturn(Arrays.asList(sbBook, sBpageblk1, sBpageblk2));

            List<BookcaseTocDAO> result = bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null);
            assertEquals(result.get(0).getLeaves().size(), 2);
            assertEquals(result.get(0).getChildren().get(2).getChildren().get(0).getFileName(), archivedSBfilename2);
            assertEquals(result.get(0).getChildren().get(2).getChildren().get(1).getFileName(), archivedSBfilename1);
        }

        @Test
        public void whenTpsDBReturnsArchivedSBsThenTheResultShouldContainItemIntheArchivedSbSectionWithFileExtensionEqualToTheArchivedSbsFileExtension(){
            Map<String, Object> sbBook = new HashedMap();
            sbBook.put("id", UUID.fromString("33000000-0000-0000-0000-000000000000"));
            sbBook.put("parent_id", null);
            sbBook.put("node_order", new Integer(5));
            sbBook.put("title", "SB book");
            sbBook.put("toc_title", "SB book");
            sbBook.put("node_type", "sb");
            sbBook.put(TECHLIB_TREE_DEPTH, 1);

            mockDBReturnListWithIcsAndTrs.add(sbBook);

            Map<String, Map<String, String>> mockSBMap = new HashedMap();
            String archivedSBfilename1 = "archivedSBfilename1.html";
            Map<String, String> mockSB1 = new HashedMap();
            mockSB1.put(TPS_VIEW_FILENAME,archivedSBfilename1);
            mockSBMap.put(archivedSBfilename1, mockSB1);

            when(programDataMock.getCatalogItemFilename(isA(String.class), isA(String.class), isA(String.class))).thenReturn(mockSBMap);

            List<BookcaseTocDAO> result = bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null);
            assertEquals(result.get(4).getChildren().get(0).getChildren().get(0).getFileExtension(), DataConstants.FIELD_TYPE_HTML);
        }

        @Test
        public void whenTpsDBReturnsArchivedSBsWithTypeNotEqualToAlertOrAlertCoverThenTheResultShouldContainItemIntheArchivedSbSectionWithTypeSB(){
            Map<String, Object> sbBook = new HashedMap();
            sbBook.put("id", UUID.fromString("33000000-0000-0000-0000-000000000000"));
            sbBook.put("parent_id", null);
            sbBook.put("node_order", new Integer(5));
            sbBook.put("title", "SB book");
            sbBook.put("toc_title", "SB book");
            sbBook.put("node_type", "sb");
            sbBook.put(TECHLIB_TREE_DEPTH, 1);

            mockDBReturnListWithIcsAndTrs.add(sbBook);

            Map<String, Map<String, String>> mockSBMap = new HashedMap();
            String archivedSBfilename1 = "archivedSBfilename1.html";
            Map<String, String> mockSB1 = new HashedMap();
            mockSB1.put(TPS_VIEW_FILENAME,archivedSBfilename1);
            mockSBMap.put(archivedSBfilename1, mockSB1);

            when(programDataMock.getCatalogItemFilename(isA(String.class), isA(String.class), isA(String.class))).thenReturn(mockSBMap);

            List<BookcaseTocDAO> result = bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null);
            assertEquals(result.get(4).getChildren().get(0).getChildren().get(0).getType(), DataConstants.SB);
        }

        @Test
        public void whenTpsDBReturnsArchivedSBsThenTheResultShouldContainItemIntheArchivedSbSectioWithRevisionDateEqualToTheArchivedSbsRevisionDate(){
            Map<String, Object> sbBook = new HashedMap();
            sbBook.put("id", UUID.fromString("33000000-0000-0000-0000-000000000000"));
            sbBook.put("parent_id", null);
            sbBook.put("node_order", new Integer(5));
            sbBook.put("title", "SB book");
            sbBook.put("toc_title", "SB book");
            sbBook.put("node_type", "sb");
            sbBook.put(TECHLIB_TREE_DEPTH, 1);

            mockDBReturnListWithIcsAndTrs.add(sbBook);

            Map<String, Map<String, String>> mockSBMap = new HashedMap();
            String archivedSBfilename1 = "archivedSBfilename1.html";
            String revisionDate = "20000101";
            Map<String, String> mockSB1 = new HashedMap();
            mockSB1.put(TPS_VIEW_FILENAME,archivedSBfilename1);
            mockSB1.put(TPS_REV_DATE, revisionDate);
            mockSBMap.put(archivedSBfilename1, mockSB1);

            when(programDataMock.getCatalogItemFilename(isA(String.class), isA(String.class), isA(String.class))).thenReturn(mockSBMap);

            List<BookcaseTocDAO> result = bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null);
            assertEquals(result.get(4).getChildren().get(0).getChildren().get(0).getRevisionDate(), revisionDate);
        }

        @Test
        public void whenTpsDBReturnsArchivedSBsWithTheTypeEqualToAlertThenTheResultShouldContainItemIntheArchivedSbSectioWithTypeSbAlert(){
            Map<String, Object> sbBook = new HashedMap();
            sbBook.put("id", UUID.fromString("33000000-0000-0000-0000-000000000000"));
            sbBook.put("parent_id", null);
            sbBook.put("node_order", new Integer(5));
            sbBook.put("title", "SB book");
            sbBook.put("toc_title", "SB book");
            sbBook.put("node_type", "sb");
            sbBook.put(TECHLIB_TREE_DEPTH, 1);

            mockDBReturnListWithIcsAndTrs.add(sbBook);

            Map<String, Map<String, String>> mockSBMap = new HashedMap();
            String archivedSBfilename1 = "archivedSBfilename1.html";
            String revisionDate = "20000101";
            Map<String, String> mockSB1 = new HashedMap();
            mockSB1.put(TPS_VIEW_FILENAME,archivedSBfilename1);
            mockSB1.put(TPS_TYPE, DataConstants.ALERT);
            mockSBMap.put(archivedSBfilename1, mockSB1);

            when(programDataMock.getCatalogItemFilename(isA(String.class), isA(String.class), isA(String.class))).thenReturn(mockSBMap);

            List<BookcaseTocDAO> result = bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null);
            assertEquals(result.get(4).getChildren().get(0).getChildren().get(0).getType(), SBALERT_PAGEBLK_TYPE);
        }

        @Test
        public void whenTpsDBReturnsArchivedSBsWithTheTypeEqualToAlertCoverThenTheResultShouldContainItemIntheArchivedSbSectioWithTypeSbAlert(){
            Map<String, Object> sbBook = new HashedMap();
            sbBook.put("id", UUID.fromString("33000000-0000-0000-0000-000000000000"));
            sbBook.put("parent_id", null);
            sbBook.put("node_order", new Integer(5));
            sbBook.put("title", "SB book");
            sbBook.put("toc_title", "SB book");
            sbBook.put("node_type", "sb");
            sbBook.put(TECHLIB_TREE_DEPTH, 1);

            mockDBReturnListWithIcsAndTrs.add(sbBook);

            Map<String, Map<String, String>> mockSBMap = new HashedMap();
            String archivedSBfilename1 = "archivedSBfilename1.html";
            String revisionDate = "20000101";
            Map<String, String> mockSB1 = new HashedMap();
            mockSB1.put(TPS_VIEW_FILENAME,archivedSBfilename1);
            mockSB1.put(TPS_TYPE, DataConstants.ALERT_COVER);
            mockSBMap.put(archivedSBfilename1, mockSB1);

            when(programDataMock.getCatalogItemFilename(isA(String.class), isA(String.class), isA(String.class))).thenReturn(mockSBMap);

            List<BookcaseTocDAO> result = bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null);
            assertEquals(result.get(4).getChildren().get(0).getChildren().get(0).getType(), SBALERT_PAGEBLK_TYPE);
        }

        @Test
        public void whenTpsDBReturnsArchivedSBsThenTheResultShouldContainItemIntheArchivedSbSectioWithTitleEqualToTheArchivedSbsTitle(){
            Map<String, Object> sbBook = new HashedMap();
            sbBook.put("id", UUID.fromString("33000000-0000-0000-0000-000000000000"));
            sbBook.put("parent_id", null);
            sbBook.put("node_order", new Integer(5));
            sbBook.put("title", "SB book");
            sbBook.put("toc_title", "SB book");
            sbBook.put("node_type", "sb");
            sbBook.put(TECHLIB_TREE_DEPTH, 1);

            mockDBReturnListWithIcsAndTrs.add(sbBook);

            Map<String, Map<String, String>> mockSBMap = new HashedMap();
            String archivedSBfilename1 = "archivedSBfilename1.html";
            String title = "title";
            Map<String, String> mockSB1 = new HashedMap();
            mockSB1.put(TPS_VIEW_FILENAME,archivedSBfilename1);
            mockSB1.put(TPS_TITLE,title);
            mockSBMap.put(archivedSBfilename1, mockSB1);

            when(programDataMock.getCatalogItemFilename(isA(String.class), isA(String.class), isA(String.class))).thenReturn(mockSBMap);

            List<BookcaseTocDAO> result = bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null);
            assertEquals(result.get(4).getChildren().get(0).getChildren().get(0).getTitle(), title);
        }

        @Test
        public void whenTpsDBReturnsArchivedSBsThenTheResultShouldContainItemIntheArchivedSbSectioWithToctitleEqualToTheArchivedSbsTitle(){
            Map<String, Object> sbBook = new HashedMap();
            sbBook.put("id", UUID.fromString("33000000-0000-0000-0000-000000000000"));
            sbBook.put("parent_id", null);
            sbBook.put("node_order", new Integer(5));
            sbBook.put("title", "SB book");
            sbBook.put("toc_title", "SB book");
            sbBook.put("node_type", "sb");
            sbBook.put(TECHLIB_TREE_DEPTH, 1);

            mockDBReturnListWithIcsAndTrs.add(sbBook);

            Map<String, Map<String, String>> mockSBMap = new HashedMap();
            String archivedSBfilename1 = "archivedSBfilename1.html";
            String title = "title";
            Map<String, String> mockSB1 = new HashedMap();
            mockSB1.put(TPS_VIEW_FILENAME,archivedSBfilename1);
            mockSB1.put(TPS_TITLE,title);
            mockSBMap.put(archivedSBfilename1, mockSB1);

            when(programDataMock.getCatalogItemFilename(isA(String.class), isA(String.class), isA(String.class))).thenReturn(mockSBMap);

            List<BookcaseTocDAO> result = bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null);
            assertEquals(result.get(4).getChildren().get(0).getChildren().get(0).getTocTitle(), title);
        }

        @Test
        public void whenTpsDBReturnsArchivedSBsThenTheResultShouldContainItemIntheArchivedSbSectioWithResourceUri(){
            Map<String, Object> sbBook = new HashedMap();
            sbBook.put("id", UUID.fromString("33000000-0000-0000-0000-000000000000"));
            sbBook.put("parent_id", null);
            sbBook.put("node_order", new Integer(5));
            sbBook.put("title", "SB book");
            sbBook.put("toc_title", "SB book");
            sbBook.put("node_type", "sb");
            sbBook.put(TECHLIB_TREE_DEPTH, 1);

            mockDBReturnListWithIcsAndTrs.add(sbBook);

            Map<String, Map<String, String>> mockSBMap = new HashedMap();
            String archivedSBfilename1 = "archivedSBfilename1.html";
            Map<String, String> mockSB1 = new HashedMap();
            mockSB1.put(TPS_VIEW_FILENAME,archivedSBfilename1);
            mockSBMap.put(archivedSBfilename1, mockSB1);

            when(programDataMock.getCatalogItemFilename(isA(String.class), isA(String.class), isA(String.class))).thenReturn(mockSBMap);

            List<BookcaseTocDAO> result = bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null);
            assertEquals(result.get(4).getChildren().get(0).getChildren().get(0).getResourceUri(), DataConstants.TECHPUBS_FILE_URI.replace(DataConstants.BOOKCASE_KEY_URI_PARAMETER,
                BOOKCASE_KEY)
                .replace(DataConstants.BOOK_KEY_URI_PARAMETER, SB_BOOK_KEY)
                .replace(DataConstants.FILENAME_URI_PARAMETER, archivedSBfilename1));
        }

        @Test
        public void whenTpsDBReturnsArchivedSBsThenTheResultShouldContainItemIntheArchivedSbSectioWithKeyEqualToTheArchivedSbsKey(){
            Map<String, Object> sbBook = new HashedMap();
            sbBook.put("id", UUID.fromString("33000000-0000-0000-0000-000000000000"));
            sbBook.put("parent_id", null);
            sbBook.put("node_order", new Integer(5));
            sbBook.put("title", "SB book");
            sbBook.put("toc_title", "SB book");
            sbBook.put("node_type", "sb");
            sbBook.put(TECHLIB_TREE_DEPTH, 1);

            mockDBReturnListWithIcsAndTrs.add(sbBook);

            Map<String, Map<String, String>> mockSBMap = new HashedMap();
            String archivedSBfilename1 = "archivedSBfilename1.html";
            String key = "archived SB Key";
            Map<String, String> mockSB1 = new HashedMap();
            mockSB1.put(TPS_VIEW_FILENAME,archivedSBfilename1);
            mockSB1.put(TPS_TOC_KEY,key);
            mockSBMap.put(archivedSBfilename1, mockSB1);

            when(programDataMock.getCatalogItemFilename(isA(String.class), isA(String.class), isA(String.class))).thenReturn(mockSBMap);

            List<BookcaseTocDAO> result = bookcaseTocData.getBookData(BOOKCASE_KEY, null,  null);
            assertEquals(result.get(4).getChildren().get(0).getChildren().get(0).getKey(), key);

        }

        /**********************************************************************************************
         *************************************Limit and Book Key Tests************************************
         **********************************************************************************************/

        @Test
        public void whenBookKeyNotNullAndLimitFalseThenResponseShouldBeFilteredByBook(){
            List<BookcaseTocDAO> response = bookcaseTocData.getBookData(BOOKCASE_KEY, "book1Key",  null);
            assert(listContainsId("10000000-0000-0000-0000-000000000000", response));

            assertEquals(1, response.size());

            List<BookcaseTocDAO> book1Children = getBookcaseTOCModelById("10000000-0000-0000-0000-000000000000", response).getChildren();
            assert(listContainsId("40000000-0000-0000-0000-000000000000", book1Children));
            assert(listContainsId("50000000-0000-0000-0000-000000000000", book1Children));
            assert(listContainsId("17000000-0000-0000-0000-000000000000", book1Children));
        }

        /**********************************************************************************************
         *************************************Private Helper Methods************************************
         **********************************************************************************************/


        private boolean listContainsId(String id, Collection<BookcaseTocDAO> bookcaseTocDAOList){
            for (BookcaseTocDAO bookcaseTOCDAO : bookcaseTocDAOList){
                if (bookcaseTOCDAO.getId().equals(id))
                    return true;
            }

            return false;

        }

        private BookcaseTocDAO getBookcaseTOCModelById(String id, Collection<BookcaseTocDAO> bookcaseTocDAOList){
            BookcaseTocDAO bookcaseTocDAOWithId = null;
            for (BookcaseTocDAO bookcaseTOCDAO : bookcaseTocDAOList){
                if (bookcaseTOCDAO.getId().equals(id))
                    bookcaseTocDAOWithId = bookcaseTOCDAO;
            }

            return bookcaseTocDAOWithId;

        }

        private boolean bookExistsWithTitle(String title, Collection<BookcaseTocDAO> bookcaseTocDAOList){
            for (BookcaseTocDAO bookcaseTOCDAO : bookcaseTocDAOList){
                if (bookcaseTOCDAO.getTitle() != null && bookcaseTOCDAO.getTitle().equals(title))
                    return true;
            }

            return false;

        }

        private BookcaseTocDAO getBookByTitle(String title, Collection<BookcaseTocDAO> bookcaseTocDAOList){
            BookcaseTocDAO book = null;
            for (BookcaseTocDAO bookcaseTOCDAO : bookcaseTocDAOList){
                if (bookcaseTOCDAO.getTitle() != null && bookcaseTOCDAO.getTitle().equals(title))
                    book = bookcaseTOCDAO;
            }

            return book;
        }

        //return the immediate parent section of a pageblk given its id in book (excluding ic and tr books)
        private BookcaseTocDAO getPageBlkParentSection(List<BookcaseTocDAO> bookList, String id){
            BookcaseTocDAO parentSection = null;

            for (BookcaseTocDAO book : bookList){
                if((book.getTitle() == null || !(book.getTitle().equals(DataConstants.IC_BOOKCASE_TOC_MODEL_TITLE)
                        || book.getTitle().equals(DataConstants.TR_BOOKCASE_TOC_MODEL_TITLE)))
                        && listContainsId(id, book.getLeaves())){
                    BookcaseTocDAO pageblk = getBookcaseTOCModelById(id, book.getLeaves());
                    parentSection = pageblk.getParent();
                }
            }

            return parentSection;
        }

        //return the parent book of a pageblk given its id (excluding ic and tr books)
        private BookcaseTocDAO getPageblksParentBook(List<BookcaseTocDAO> bookList, String id){
            BookcaseTocDAO parentBook = null;

            for (BookcaseTocDAO book : bookList){
                if(book.getTitle() != null && !(book.getTitle().equals(DataConstants.IC_BOOKCASE_TOC_MODEL_TITLE))
                        && !(book.getTitle().equals(DataConstants.TR_BOOKCASE_TOC_MODEL_TITLE))
                        && listContainsId(id, book.getLeaves()))
                    parentBook = book;
            }

            return parentBook;
        }

        //recursively determine if the children (direct and indirect) of a bookcaseTOCModel are in the correct order at each level
        //compares child items index in the list to its order to insure they match
        // account for ic's and tr's having the same order as their manual pageblk
        private boolean allChildLevelsAreSortedByOrderDescendingWithICandTRPageblksAfterTheManualPageblkTheyReference(
            BookcaseTocDAO dao){
            List<BookcaseTocDAO> children = dao.getChildren();

            if (!children.isEmpty()){
                int orderOffset = 0;
                for (int i = 0; i < children.size(); i++) {
                    BookcaseTocDAO child = children.get(i);
                    String childNodeType = child.getType() == null ? "" : child.getType();

                    //if this item is a tr or ic that has the same order as another pageblk, then it should be after the pageblk with node type manual
                    if (childNodeType.equals(IC_PAGEBLK_TYPE) || childNodeType.equals(TR_PAGEBLK_TYPE)) {
                        String matchingPageblkId = getManualPageblkIdWithSameOrderAndKey(child.getOrder(), child.getKey(), children);

                        if (!StringUtils.isEmpty(matchingPageblkId)) {
                            if (i + 1 != child.getOrder() + orderOffset + 1 ||
                                !children.get(i - 1).getType().equals(MANUAL_PAGEBLK_TYPE))
                                return false;

                            //offset to account for ic/tr order being the same as its manual pageblk when comparing items index to its order
                            orderOffset++;
                        }

                    }else if (child.getOrder() == 0 && child.getOrder() != i){
                        return false;
                     }else if (child.getOrder() > 0 && i + 1 != child.getOrder()+ orderOffset) {
                        return false;
                    }

                    //recursively check the sort order of all children
                    if(!allChildLevelsAreSortedByOrderDescendingWithICandTRPageblksAfterTheManualPageblkTheyReference(child))
                        return false;
                }
            }

            return true;
        }

        private String getManualPageblkIdWithSameOrderAndKey(int order, String key, List<BookcaseTocDAO> list){
            for (BookcaseTocDAO item : list) {
                if(item.getOrder() == order && item.getType().equals(DataConstants.MANUAL_PAGEBLK_TYPE) && item.getKey().equals(key))
                    return item.getId();
            }

            return null;
        }

        private boolean resourceUriIsNullForNonLeafNodes(List<BookcaseTocDAO> bookcaseTocDAOList){
            boolean resourceUriIsNull = true;
            for (BookcaseTocDAO node: bookcaseTocDAOList){
                if (node.getResourceUri() != null){
                    resourceUriIsNull = false;
                    break;
                }

                if (!node.getChildren().isEmpty())
                    resourceUriIsNullForNonLeafNodes(node.getChildren());
            }

            return resourceUriIsNull;
        }

    }

    public static class createBookcaseTocDAOForBooksTest {
        private static final String BOOKCASE_KEY = "gek112060";
        private static final String VERSION = "9.8";
        private static final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

        @Mock
        private IBookData iBookDataMock;

        @Mock
        private IBookcaseData iBookcaseDataMock;

        @Mock
        private IBookVersionData iBookVersionDataMock;

        @Mock
        private IBookcaseVersionData iBookcaseVersionDataMock;

        @Mock
        private IPageBlkData iPageBlkDataMock;

        @InjectMocks
        private BookcaseTOCData bookcaseTocData;

        private BookcaseEntity bookcaseEntity = new BookcaseEntity();

        private BookEntity bookEntity = new BookEntity();
        private BookEntity bookEntity2 = new BookEntity();

        private BookVersionEntity bookVersionEntity = new BookVersionEntity();
        private BookVersionEntity bookVersionEntity2 = new BookVersionEntity();

        @Before
        public void setUp() {
            bookcaseTocData = new BookcaseTOCData();
            MockitoAnnotations.initMocks(this);

            bookEntity.setId(UUID.fromString("10000000-0000-0000-0000-000000000000"));
            bookEntity.setBookKey("bookKey1");
            bookEntity.setBookType("bookType1");

            bookVersionEntity.setBook(bookEntity);
            bookVersionEntity.setTitle("bookTitle1");
            bookVersionEntity.setRevisionDate(DateUtil.parseYYYYMMDDDate("2020/01/01"));
            bookVersionEntity.setBookOrder((short) 1);

            bookEntity2.setId(UUID.fromString("20000000-0000-0000-0000-000000000000"));
            bookEntity2.setBookKey("bookKey2");
            bookEntity2.setBookType("bookType2");

            bookVersionEntity2.setBook(bookEntity);
            bookVersionEntity2.setTitle("bookTitle2");
            bookVersionEntity2.setRevisionDate(DateUtil.parseYYYYMMDDDate("2020/01/02"));
            bookVersionEntity2.setBookOrder((short) 2);

            when(iBookcaseVersionDataMock.findOnlineBookcaseVersion(BOOKCASE_KEY)).thenReturn(VERSION);
            when(iBookcaseDataMock.findByBookcaseKeyAndBookcaseVersion(BOOKCASE_KEY, VERSION)).thenReturn(bookcaseEntity);
            when(iBookDataMock.findByBookcaseAndBookcaseVersion(bookcaseEntity, VERSION)).thenReturn(Lists.newArrayList(bookEntity, bookEntity2));
            when(iBookVersionDataMock.findByBookAndBookcaseVersion(bookEntity, VERSION)).thenReturn(Lists.newArrayList(bookVersionEntity));
            when(iBookVersionDataMock.findByBookAndBookcaseVersion(bookEntity2, VERSION)).thenReturn(Lists.newArrayList(bookVersionEntity2));
            when(iPageBlkDataMock.hasPageblksForBookcaseIdAndVersionAndPubType(bookEntity.getId(), VERSION, "bookType1")).thenReturn(true);
            when(iPageBlkDataMock.hasPageblksForBookcaseIdAndVersionAndPubType(bookEntity.getId(), VERSION, "bookType2")).thenReturn(true);
            when(iPageBlkDataMock.hasPageblksForBookIdAndVersion(bookEntity.getId(), VERSION)).thenReturn(true);
        }

        @Test
        public void whenVersionEmptyVerifyGetProgramOnlinVersionCalled() throws TechpubsException {
            bookcaseTocData.getBooks(BOOKCASE_KEY, "");
            verify(iBookcaseVersionDataMock.findOnlineBookcaseVersion(isA(String.class)));
        }

        @Test
        public void whenVersionNotEmptyVerifyProgramOnlineNotCalled() throws TechpubsException {
            bookcaseTocData.getBooks(BOOKCASE_KEY, VERSION);
            verify(iBookcaseVersionDataMock, times(0)).findOnlineBookcaseVersion(isA(String.class));
        }

        @Test(expected = TechpubsException.class)
        public void whenVersionEmptyAndOnlineVersionEmptyThrowException() throws TechpubsException {
            when(iBookcaseVersionDataMock.findOnlineBookcaseVersion(BOOKCASE_KEY)).thenReturn("");
            bookcaseTocData.getBooks(BOOKCASE_KEY, "");
        }

        @Test
        public void whenBookParsedVerifyFieldsParsedCorrectly() throws TechpubsException {
            List<BookcaseTocDAO> response = bookcaseTocData.getBooks(BOOKCASE_KEY, VERSION);

            assertEquals(2, response.size());
            BookcaseTocDAO book1 = response.get(0);

            assertEquals(book1.getId(), bookEntity.getId().toString());
            assertEquals(book1.getKey(), bookEntity.getBookKey());
            assertEquals(book1.getTitle(), bookVersionEntity.getTitle());
            assertEquals(book1.getTocTitle(), bookVersionEntity.getTitle());
            assertEquals(book1.getRevisionDate(), format.format(bookVersionEntity.getRevisionDate()));
            assertEquals(book1.getType(), bookEntity.getBookType());
            assertEquals(book1.getOrder(), bookVersionEntity.getBookOrder().intValue());
            assertTrue(book1.getIsRoot());
            assertTrue(book1.getHasChildren());
        }
    }
}
