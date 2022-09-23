package com.geaviation.techpubs.service.util;

import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.BookDAO;
import com.geaviation.techpubs.models.BookcaseContentDAO;
import com.geaviation.techpubs.services.util.AppConstants;
import com.geaviation.techpubs.services.util.TechpubsAppUtil;
import org.apache.commons.collections.map.HashedMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TechpubsAppUtilTest {


    public static class sortBooksTest{

        TechpubsAppUtil techpubsAppUtil;
        List<BookcaseContentDAO> bookcaseContentDAOList = new ArrayList();
        BookDAO bookcase1Book1RevDate1;
        BookDAO bookcase1Book1RevDate2;
        BookDAO bookcase1Book1RevDate3;
        BookDAO bookcase1Book2RevDate1;
        BookDAO bookcase1Book2RevDate2;
        BookDAO bookcase2Book1RevDate1;
        BookDAO bookcase1Book3RevDate1;
        BookDAO bookcase3Book1RevDate1;
        Map<String, String> queryParams;


        @Before
        public void setup(){
            techpubsAppUtil = new TechpubsAppUtil();

            bookcase1Book1RevDate1 = new BookDAO();
            bookcase1Book1RevDate1.setKey("1");
            bookcase1Book1RevDate1.setRevisiondate("20190101");
            bookcase1Book1RevDate1.setTitle("BookDAO 1");
            bookcase1Book1RevDate1.setBookcasetitle("Bookcase 1");

            bookcase1Book1RevDate2 = new BookDAO();
            bookcase1Book1RevDate2.setKey("2");
            bookcase1Book1RevDate2.setRevisiondate("20190102");
            bookcase1Book1RevDate2.setTitle("BookDAO 1");
            bookcase1Book1RevDate2.setBookcasetitle("Bookcase 1");

            bookcase1Book1RevDate3 = new BookDAO();
            bookcase1Book1RevDate3.setKey("3");
            bookcase1Book1RevDate3.setRevisiondate("20190103");
            bookcase1Book1RevDate3.setTitle("BookDAO 1");
            bookcase1Book1RevDate3.setBookcasetitle("Bookcase 1");

            bookcase1Book2RevDate1 = new BookDAO();
            bookcase1Book2RevDate1.setKey("4");
            bookcase1Book2RevDate1.setRevisiondate("20190101");
            bookcase1Book2RevDate1.setTitle("BookDAO 2");
            bookcase1Book2RevDate1.setBookcasetitle("Bookcase 1");

            bookcase1Book2RevDate2 = new BookDAO();
            bookcase1Book2RevDate2.setKey("5");
            bookcase1Book2RevDate2.setRevisiondate("20190102");
            bookcase1Book2RevDate2.setTitle("BookDAO 2");
            bookcase1Book2RevDate2.setBookcasetitle("Bookcase 1");

            bookcase1Book2RevDate1 = new BookDAO();
            bookcase1Book2RevDate1.setKey("6");
            bookcase1Book2RevDate1.setRevisiondate("20190101");
            bookcase1Book2RevDate1.setTitle("BookDAO 2");
            bookcase1Book2RevDate1.setBookcasetitle("Bookcase 1");


            bookcase1Book3RevDate1 = new BookDAO();
            bookcase1Book3RevDate1.setKey("7");
            bookcase1Book3RevDate1.setRevisiondate("20190101");
            bookcase1Book3RevDate1.setTitle("BookDAO 3");
            bookcase1Book3RevDate1.setBookcasetitle("Bookcase 1");

            bookcase2Book1RevDate1 = new BookDAO();
            bookcase2Book1RevDate1.setKey("8");
            bookcase2Book1RevDate1.setRevisiondate("20190101");
            bookcase2Book1RevDate1.setTitle("BookDAO 1");
            bookcase2Book1RevDate1.setBookcasetitle("Bookcase 2");

            bookcase3Book1RevDate1 = new BookDAO();
            bookcase3Book1RevDate1.setKey("8");
            bookcase3Book1RevDate1.setRevisiondate("20190101");
            bookcase3Book1RevDate1.setTitle("BookDAO 1");
            bookcase3Book1RevDate1.setBookcasetitle("Bookcase 2");

            bookcaseContentDAOList = new ArrayList(Arrays.asList(bookcase1Book1RevDate2, bookcase1Book1RevDate1, bookcase1Book1RevDate3));

            queryParams = new HashedMap();

            queryParams.put(AppConstants.ICOLUMNS, "8");
            queryParams.put(AppConstants.MDATAPROP+0, "bookcasetitle");
            queryParams.put(AppConstants.MDATAPROP+1, "title");
            queryParams.put(AppConstants.MDATAPROP+2, "revisiondate");
        }

        @Test
        public void whenFirstSortColumnIsRevisionDateAndSortDirectionIsAscThenShouldSortByRevisiondateDesc(){
            queryParams.put(AppConstants.ISORT_COL+0, "2");
            queryParams.put(AppConstants.SSORTDIR+0, "asc");

            bookcaseContentDAOList = new ArrayList(Arrays.asList(bookcase1Book1RevDate2, bookcase1Book1RevDate1, bookcase1Book1RevDate3));

            TechpubsAppUtil.sortBooks(bookcaseContentDAOList, queryParams);

            assertEquals(bookcase1Book1RevDate1.getKey(), ((BookDAO)bookcaseContentDAOList.get(0)).getKey());
            assertEquals(bookcase1Book1RevDate3.getKey(), ((BookDAO)bookcaseContentDAOList.get(2)).getKey());

        }

        @Test
        public void whenFirstSortColumnIsRevisionDateAndSortDirectionIsDescThenShouldSortByRevisiondateDesc(){
            queryParams.put(AppConstants.ISORT_COL+0, "2");
            queryParams.put(AppConstants.SSORTDIR+0, "desc");

            bookcaseContentDAOList = new ArrayList(Arrays.asList(bookcase1Book1RevDate2, bookcase1Book1RevDate1, bookcase1Book1RevDate3));

            TechpubsAppUtil.sortBooks(bookcaseContentDAOList, queryParams);

            assertEquals(bookcase1Book1RevDate1.getKey(), ((BookDAO)bookcaseContentDAOList.get(2)).getKey());
            assertEquals(bookcase1Book1RevDate3.getKey(), ((BookDAO)bookcaseContentDAOList.get(0)).getKey());

        }

        @Test
        public void whenSecondSortColumnIsRevisionDateAndFirstSortColumnIsTitleDescThenShouldSortByTitleThenRevisiondateDesc(){
            queryParams.put(AppConstants.ISORT_COL+0, "1");
            queryParams.put(AppConstants.SSORTDIR+0, "desc");

            queryParams.put(AppConstants.ISORT_COL+1, "2");
            queryParams.put(AppConstants.SSORTDIR+1, "desc");

            bookcaseContentDAOList = new ArrayList(Arrays.asList(bookcase1Book1RevDate2, bookcase1Book1RevDate1, bookcase1Book1RevDate3, bookcase1Book2RevDate1, bookcase1Book2RevDate2));

            TechpubsAppUtil.sortBooks(bookcaseContentDAOList, queryParams);

            assertEquals(bookcase1Book1RevDate3.getKey(), ((BookDAO)bookcaseContentDAOList.get(2)).getKey());
            assertEquals(bookcase1Book1RevDate1.getKey(), ((BookDAO)bookcaseContentDAOList.get(4)).getKey());

        }

        @Test
        public void whenThirdSortColumnIsRevisionDateAndSecondSortColumnAndFirstBookcasenameIsTitleDescThenShouldSortByTitleThenRevisiondateDesc(){
            queryParams.put(AppConstants.ISORT_COL+0, "0");
            queryParams.put(AppConstants.SSORTDIR+0, "desc");

            queryParams.put(AppConstants.ISORT_COL+1, "1");
            queryParams.put(AppConstants.SSORTDIR+1, "desc");

            queryParams.put(AppConstants.ISORT_COL+2, "2");
            queryParams.put(AppConstants.SSORTDIR+2, "desc");

            bookcaseContentDAOList = new ArrayList(Arrays.asList(bookcase1Book1RevDate2, bookcase1Book1RevDate1, bookcase1Book1RevDate3, bookcase1Book2RevDate1, bookcase1Book2RevDate2, bookcase2Book1RevDate1));

            TechpubsAppUtil.sortBooks(bookcaseContentDAOList, queryParams);

            assertEquals(bookcase1Book1RevDate3.getKey(), ((BookDAO)bookcaseContentDAOList.get(3)).getKey());
            assertEquals(bookcase1Book1RevDate1.getKey(), ((BookDAO)bookcaseContentDAOList.get(5)).getKey());

        }

        @Test
        public void whenFirstSortColumnIsTitleAndSortDirectionIsAscThenShouldSortByTitleAsc(){
            queryParams.put(AppConstants.ISORT_COL+0, "1");
            queryParams.put(AppConstants.SSORTDIR+0, "asc");

            bookcaseContentDAOList = new ArrayList(Arrays.asList(bookcase1Book2RevDate1, bookcase1Book1RevDate1, bookcase1Book3RevDate1));

            TechpubsAppUtil.sortBooks(bookcaseContentDAOList, queryParams);

            assertEquals(bookcase1Book1RevDate1.getKey(), ((BookDAO)bookcaseContentDAOList.get(0)).getKey());
            assertEquals(bookcase1Book3RevDate1.getKey(), ((BookDAO)bookcaseContentDAOList.get(2)).getKey());

        }

        @Test
        public void whenFirstSortColumnIsTitleAndSortDirectionIsDescThenShouldSortByTitleDesc(){
            queryParams.put(AppConstants.ISORT_COL+0, "1");
            queryParams.put(AppConstants.SSORTDIR+0, "desc");

            bookcaseContentDAOList = new ArrayList(Arrays.asList(bookcase1Book2RevDate1, bookcase1Book1RevDate1, bookcase1Book3RevDate1));

            TechpubsAppUtil.sortBooks(bookcaseContentDAOList, queryParams);

            assertEquals(bookcase1Book1RevDate1.getKey(), ((BookDAO)bookcaseContentDAOList.get(2)).getKey());
            assertEquals(bookcase1Book3RevDate1.getKey(), ((BookDAO)bookcaseContentDAOList.get(0)).getKey());

        }

        @Test
        public void whenSecondSortColumnIsTitleAndFirstSortColumnIsBookcasetitleDescThenShouldSortByBookcasetitleThenTitleDesc(){
            queryParams.put(AppConstants.ISORT_COL+0, "0");
            queryParams.put(AppConstants.SSORTDIR+0, "desc");

            queryParams.put(AppConstants.ISORT_COL+1, "1");
            queryParams.put(AppConstants.SSORTDIR+1, "desc");

            bookcaseContentDAOList = new ArrayList(Arrays.asList(bookcase2Book1RevDate1, bookcase1Book2RevDate1, bookcase1Book1RevDate1, bookcase1Book3RevDate1));

            TechpubsAppUtil.sortBooks(bookcaseContentDAOList, queryParams);

            assertEquals(bookcase1Book3RevDate1.getKey(), ((BookDAO)bookcaseContentDAOList.get(1)).getKey());
            assertEquals(bookcase1Book1RevDate1.getKey(), ((BookDAO)bookcaseContentDAOList.get(3)).getKey());

        }

        @Test
        public void whenThirdSortColumnsTitleAndSecondSortColumnIsBookcasetitleAndFirstSortColumnIsRevisionDateDescThenShouldSortByRevisionDateThenBookcasetitleThenTitleDesc(){
            queryParams.put(AppConstants.ISORT_COL+0, "2");
            queryParams.put(AppConstants.SSORTDIR+0, "desc");

            queryParams.put(AppConstants.ISORT_COL+1, "0");
            queryParams.put(AppConstants.SSORTDIR+1, "desc");

            queryParams.put(AppConstants.ISORT_COL+2, "1");
            queryParams.put(AppConstants.SSORTDIR+2, "desc");

            bookcaseContentDAOList = new ArrayList(Arrays.asList(bookcase1Book3RevDate1, bookcase2Book1RevDate1, bookcase1Book1RevDate1, bookcase1Book1RevDate2, bookcase1Book2RevDate1));

            TechpubsAppUtil.sortBooks(bookcaseContentDAOList, queryParams);

            assertEquals(bookcase1Book3RevDate1.getKey(), ((BookDAO)bookcaseContentDAOList.get(2)).getKey());
            assertEquals(bookcase1Book2RevDate1.getKey(), ((BookDAO)bookcaseContentDAOList.get(3)).getKey());
            assertEquals(bookcase1Book1RevDate1.getKey(), ((BookDAO)bookcaseContentDAOList.get(4)).getKey());

        }

        @Test
        public void whenFirstSortColumnIsBookcasetitleAndSortDirectionIsAscThenShouldSortByBookcasetitleAsc(){
            queryParams.put(AppConstants.ISORT_COL+0, "0");
            queryParams.put(AppConstants.SSORTDIR+0, "asc");

            bookcaseContentDAOList = new ArrayList(Arrays.asList(bookcase3Book1RevDate1, bookcase2Book1RevDate1, bookcase1Book1RevDate1));

            TechpubsAppUtil.sortBooks(bookcaseContentDAOList, queryParams);

            assertEquals(bookcase1Book1RevDate1.getKey(), ((BookDAO)bookcaseContentDAOList.get(0)).getKey());
            assertEquals(bookcase3Book1RevDate1.getKey(), ((BookDAO)bookcaseContentDAOList.get(2)).getKey());
        }


        @Test
        public void whenFirstSortColumnIsBookcasetitleAndSortDirectionIsAscThenShouldSortByBookcasetitleDesc(){
            queryParams.put(AppConstants.ISORT_COL+0, "0");
            queryParams.put(AppConstants.SSORTDIR+0, "desc");

            bookcaseContentDAOList = new ArrayList(Arrays.asList(bookcase3Book1RevDate1,bookcase1Book1RevDate1, bookcase2Book1RevDate1));

            TechpubsAppUtil.sortBooks(bookcaseContentDAOList, queryParams);

            assertEquals(bookcase3Book1RevDate1.getKey(), ((BookDAO)bookcaseContentDAOList.get(0)).getKey());
            assertEquals(bookcase1Book1RevDate1.getKey(), ((BookDAO)bookcaseContentDAOList.get(2)).getKey());

        }

        @Test
        public void wheSecondSortColumnIsBookcasetitleAndFirstSortColumnIsTitleDescThenShouldSortByTitleThenBookcasetitleDesc(){
            queryParams.put(AppConstants.ISORT_COL+0, "1");
            queryParams.put(AppConstants.SSORTDIR+0, "desc");

            queryParams.put(AppConstants.ISORT_COL+1, "0");
            queryParams.put(AppConstants.SSORTDIR+1, "desc");

            bookcaseContentDAOList = new ArrayList(Arrays.asList(bookcase3Book1RevDate1, bookcase1Book1RevDate1, bookcase1Book2RevDate1, bookcase2Book1RevDate1));

            TechpubsAppUtil.sortBooks(bookcaseContentDAOList, queryParams);

            assertEquals(bookcase3Book1RevDate1.getKey(), ((BookDAO)bookcaseContentDAOList.get(1)).getKey());
            assertEquals(bookcase2Book1RevDate1.getKey(), ((BookDAO)bookcaseContentDAOList.get(2)).getKey());
            assertEquals(bookcase1Book1RevDate1.getKey(), ((BookDAO)bookcaseContentDAOList.get(3)).getKey());

        }

        @Test
        public void whenThirdSortColumnIsBookcasetitleAndSecondSortColumnIsTitleAndFirstSortColumnIsRevisionDateDescThenShouldSortByRevisionDateThenTitleThenBookcasetitleDesc(){
            queryParams.put(AppConstants.ISORT_COL+0, "2");
            queryParams.put(AppConstants.SSORTDIR+0, "desc");

            queryParams.put(AppConstants.ISORT_COL+1, "1");
            queryParams.put(AppConstants.SSORTDIR+1, "desc");

            queryParams.put(AppConstants.ISORT_COL+2, "0");
            queryParams.put(AppConstants.SSORTDIR+2, "desc");

            bookcaseContentDAOList = new ArrayList(Arrays.asList(bookcase1Book1RevDate1, bookcase1Book2RevDate1, bookcase2Book1RevDate1, bookcase1Book2RevDate2, bookcase3Book1RevDate1));

            techpubsAppUtil.sortBooks(bookcaseContentDAOList, queryParams);


            assertEquals(bookcase3Book1RevDate1.getKey(), ((BookDAO)bookcaseContentDAOList.get(2)).getKey());
            assertEquals(bookcase2Book1RevDate1.getKey(), ((BookDAO)bookcaseContentDAOList.get(3)).getKey());
            assertEquals(bookcase1Book1RevDate1.getKey(), ((BookDAO)bookcaseContentDAOList.get(4)).getKey());

        }

        @Test
        public void whenThirdSortColumnSortDirectionIsAscAndSecondSortColumnSortDirectionIsDescAndFirstSortColumnSortDirectionIsDescThenShouldSortByFirstSortColumnDescSecondSortColumnDescThenThirdSortColumnAsc(){
            queryParams.put(AppConstants.ISORT_COL+0, "2");
            queryParams.put(AppConstants.SSORTDIR+0, "desc");

            queryParams.put(AppConstants.ISORT_COL+1, "1");
            queryParams.put(AppConstants.SSORTDIR+1, "desc");

            queryParams.put(AppConstants.ISORT_COL+2, "0");
            queryParams.put(AppConstants.SSORTDIR+2, "asc");

            bookcaseContentDAOList = new ArrayList(Arrays.asList(bookcase1Book1RevDate1, bookcase1Book2RevDate1, bookcase2Book1RevDate1, bookcase1Book2RevDate2, bookcase3Book1RevDate1));

            techpubsAppUtil.sortBooks(bookcaseContentDAOList, queryParams);

            assertEquals(bookcase3Book1RevDate1.getKey(), ((BookDAO)bookcaseContentDAOList.get(4)).getKey());
            assertEquals(bookcase2Book1RevDate1.getKey(), ((BookDAO)bookcaseContentDAOList.get(3)).getKey());
            assertEquals(bookcase1Book1RevDate1.getKey(), ((BookDAO)bookcaseContentDAOList.get(2)).getKey());

        }

        @Test
        public void whenSecondSortColumnSortDirectionIsAscAndFirstandThirdSortColumnSortDirectionIsDescThenShouldSortByFirstAndThirdSortColumnDescAndSecondSortColumnAsc(){
            queryParams.put(AppConstants.ISORT_COL+0, "2");
            queryParams.put(AppConstants.SSORTDIR+0, "desc");

            queryParams.put(AppConstants.ISORT_COL+1, "1");
            queryParams.put(AppConstants.SSORTDIR+1, "asc");

            queryParams.put(AppConstants.ISORT_COL+2, "0");
            queryParams.put(AppConstants.SSORTDIR+2, "desc");

            bookcaseContentDAOList = new ArrayList(Arrays.asList(bookcase1Book1RevDate1, bookcase1Book2RevDate1, bookcase2Book1RevDate1, bookcase1Book2RevDate2, bookcase3Book1RevDate1));

            techpubsAppUtil.sortBooks(bookcaseContentDAOList, queryParams);

            assertEquals(bookcase1Book2RevDate1.getKey(), ((BookDAO)bookcaseContentDAOList.get(4)).getKey());

        }

        @Test
        public void whenFirstSortColumnSortDirectionIsAscAndSecondAndThirdSortColumnSortDirectionIsDescThenShouldSortBySecondAndThirdSortColumnDescAndFirstSortColumnAsc(){
            queryParams.put(AppConstants.ISORT_COL+0, "2");
            queryParams.put(AppConstants.SSORTDIR+0, "asc");

            queryParams.put(AppConstants.ISORT_COL+1, "0");
            queryParams.put(AppConstants.SSORTDIR+1, "desc");

            queryParams.put(AppConstants.ISORT_COL+2, "1");
            queryParams.put(AppConstants.SSORTDIR+2, "desc");

            bookcaseContentDAOList = new ArrayList(Arrays.asList(bookcase1Book1RevDate1, bookcase1Book2RevDate1, bookcase2Book1RevDate1, bookcase1Book2RevDate2, bookcase3Book1RevDate1));

            techpubsAppUtil.sortBooks(bookcaseContentDAOList, queryParams);

            assertEquals(bookcase1Book2RevDate2.getKey(), ((BookDAO)bookcaseContentDAOList.get(4)).getKey());

        }

    }

    public static class validateDatatableParametersTest{

        Map<String, String> queryParams = new HashedMap();

        @Before
        public void setup(){
            queryParams.put(AppConstants.IDISPLAYLENGTH, "1");
            queryParams.put(AppConstants.IDISPLAYSTART, "1");
            queryParams.put(AppConstants.ICOLUMNS, "1");
            queryParams.put(AppConstants.SECHO, "1");
        }

        @Test(expected = Test.None.class /* no exception expected */)
        public void whenAnIntegerIsProvidedForAllValuesShouldNotThrowException() throws TechpubsException {
            TechpubsAppUtil.validateDatatableParameters(queryParams);
        }

        @Test
        public void whenIdisplayLengthIsNullThenShouldThrowTechpubsExceptionWithInvalidParameterError(){
            queryParams.put(AppConstants.IDISPLAYLENGTH, null);
            Throwable e =  Assertions.assertThrows(TechpubsException.class, () -> TechpubsAppUtil.validateDatatableParameters(queryParams));
            assertTrue(((TechpubsException) e).getTechpubsAppError().equals(TechpubsException.TechpubsAppError.INVALID_PARAMETER));
        }

        @Test
        public void whenIdisplayLengthIsNotAnIntegerThenShouldThrowTechpubsExceptionWithInvalidParameterError(){
            queryParams.put(AppConstants.IDISPLAYLENGTH, "some text");
            Throwable e =  Assertions.assertThrows(TechpubsException.class, () -> TechpubsAppUtil.validateDatatableParameters(queryParams));
            assertTrue(((TechpubsException) e).getTechpubsAppError().equals(TechpubsException.TechpubsAppError.INVALID_PARAMETER));
        }

        @Test
        public void whenIdisplayLengthIsLessThanOneThenShouldThrowTechpubsExceptionWithInvalidParameterError(){
            queryParams.put(AppConstants.IDISPLAYLENGTH, "0");
            Throwable e =  Assertions.assertThrows(TechpubsException.class, () -> TechpubsAppUtil.validateDatatableParameters(queryParams));
            assertTrue(((TechpubsException) e).getTechpubsAppError().equals(TechpubsException.TechpubsAppError.INVALID_PARAMETER));
        }

        @Test
        public void whenIdisplayStartIsNullThenShouldThrowTechpubsExceptionWithInvalidParameterError(){
            queryParams.put(AppConstants.IDISPLAYSTART, null);
            Throwable e =  Assertions.assertThrows(TechpubsException.class, () -> TechpubsAppUtil.validateDatatableParameters(queryParams));
            assertTrue(((TechpubsException) e).getTechpubsAppError().equals(TechpubsException.TechpubsAppError.INVALID_PARAMETER));
        }

        @Test
        public void whenIdisplayStartIsNotAnIntegerThenShouldThrowTechpubsExceptionWithInvalidParameterError(){
            queryParams.put(AppConstants.IDISPLAYSTART, "some text");
            Throwable e =  Assertions.assertThrows(TechpubsException.class, () -> TechpubsAppUtil.validateDatatableParameters(queryParams));
            assertTrue(((TechpubsException) e).getTechpubsAppError().equals(TechpubsException.TechpubsAppError.INVALID_PARAMETER));
        }

        @Test
        public void whenIdisplayStartIsLessThanZeroThenShouldThrowTechpubsExceptionWithInvalidParameterError(){
            queryParams.put(AppConstants.IDISPLAYSTART, "-1");
            Throwable e =  Assertions.assertThrows(TechpubsException.class, () -> TechpubsAppUtil.validateDatatableParameters(queryParams));
            assertTrue(((TechpubsException) e).getTechpubsAppError().equals(TechpubsException.TechpubsAppError.INVALID_PARAMETER));
        }

        @Test
        public void whenIColumnsStartIsNullThenShouldThrowTechpubsExceptionWithInvalidParameterError(){
            queryParams.put(AppConstants.ICOLUMNS, null);
            Throwable e =  Assertions.assertThrows(TechpubsException.class, () -> TechpubsAppUtil.validateDatatableParameters(queryParams));
            assertTrue(((TechpubsException) e).getTechpubsAppError().equals(TechpubsException.TechpubsAppError.INVALID_PARAMETER));
        }

        @Test
        public void whenIColumnsIsNotAnIntegerThenShouldThrowTechpubsExceptionWithInvalidParameterError(){
            queryParams.put(AppConstants.ICOLUMNS, "some text");
            Throwable e =  Assertions.assertThrows(TechpubsException.class, () -> TechpubsAppUtil.validateDatatableParameters(queryParams));
            assertTrue(((TechpubsException) e).getTechpubsAppError().equals(TechpubsException.TechpubsAppError.INVALID_PARAMETER));
        }

        @Test
        public void whenIColumnsIsLessThanZeroThenShouldThrowTechpubsExceptionWithInvalidParameterError(){
            queryParams.put(AppConstants.ICOLUMNS, "-1");
            Throwable e =  Assertions.assertThrows(TechpubsException.class, () -> TechpubsAppUtil.validateDatatableParameters(queryParams));
            assertTrue(((TechpubsException) e).getTechpubsAppError().equals(TechpubsException.TechpubsAppError.INVALID_PARAMETER));
        }

        @Test
        public void whenSEchoIsNullThenShouldThrowTechpubsExceptionWithInvalidParameterError(){
            queryParams.put(AppConstants.ICOLUMNS, null);
            Throwable e =  Assertions.assertThrows(TechpubsException.class, () -> TechpubsAppUtil.validateDatatableParameters(queryParams));
            assertTrue(((TechpubsException) e).getTechpubsAppError().equals(TechpubsException.TechpubsAppError.INVALID_PARAMETER));
        }

        @Test
        public void whenSEchoIsEmptyThenShouldThrowTechpubsExceptionWithInvalidParameterError(){
            queryParams.put(AppConstants.ICOLUMNS, "");
            Throwable e =  Assertions.assertThrows(TechpubsException.class, () -> TechpubsAppUtil.validateDatatableParameters(queryParams));
            assertTrue(((TechpubsException) e).getTechpubsAppError().equals(TechpubsException.TechpubsAppError.INVALID_PARAMETER));
        }

    }
}
