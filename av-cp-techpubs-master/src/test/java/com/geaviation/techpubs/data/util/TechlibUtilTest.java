package com.geaviation.techpubs.data.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class TechlibUtilTest {

    public static class IsPageBlkTest{
        @Test
        public void whenTypeIsICThenShouldReturnTrue(){
            assert(TechlibUtil.isPageBlkType(DataConstants.IC_PAGEBLK_TYPE));
        }

        @Test
        public void whenTypeIsTRThenShouldReturnTrue(){
            assert(TechlibUtil.isPageBlkType(DataConstants.TR_PAGEBLK_TYPE));
        }

        @Test
        public void whenTypeIsManualThenShouldReturnTrue(){
            assert(TechlibUtil.isPageBlkType(DataConstants.MANUAL_PAGEBLK_TYPE));
        }

        @Test
        public void whenTypeIsNotIcTrOrManualThenShouldReturnFalse(){
            assert(!TechlibUtil.isPageBlkType("random string"));
        }

        @Test
        public void whenTypeIsNullThenShouldReturnFalse(){
            assert(!TechlibUtil.isPageBlkType(null));
        }

    }

    public static class  createFileResourceUriTest {
        private static final String bookcaseKey = "bookcaseKey";
        private static final String bookKey = "bookKey";
        private static final String filename = "filename";

        @Test
        public void whenbookcaseKeyParameterIsNullShouldReturnNull(){
            assertEquals(TechlibUtil.createFileResourceUri(null, bookKey, filename), null);
        }

        @Test
        public void whenbookKeyParameterIsNullShouldReturnNull(){
            assertEquals(TechlibUtil.createFileResourceUri(bookcaseKey, null, filename), null);
        }

        @Test
        public void whenfilenameParameterIsNullShouldReturnNull(){
            assertEquals(TechlibUtil.createFileResourceUri(bookcaseKey, bookKey, null), null);
        }

        @Test
        public void whenBookcaseKeyBookKeyAndFilenameIsNotNullShouldReturnResourceURIWithBookcaseKeyBookKeyAndFilename() {
            String expectedSBPageblk1ResourceUri =
                    DataConstants.TECHPUBS_FILE_URI.replace(DataConstants.BOOKCASE_KEY_URI_PARAMETER, bookcaseKey)
                            .replace(DataConstants.BOOK_KEY_URI_PARAMETER, bookKey)
                            .replace(DataConstants.FILENAME_URI_PARAMETER, filename);

            assertEquals(TechlibUtil.createFileResourceUri(bookcaseKey, bookKey, filename), expectedSBPageblk1ResourceUri);
        }
    }

    public static class GetFileTypeTest{
        private static final String fileName = "filename.";
        @Test
        public void whenFileNameParameterIsNullThenShouldReturnFileTypeNull(){
            assertEquals(TechlibUtil.getFileType(null), null);
        }


        @Test
        public void whenFileNameParameterIsNotNullAndExtensionIsHtmThenShouldReturnFileTypeHTML(){
            assertEquals(TechlibUtil.getFileType(fileName+ DataConstants.FILE_EXTENSION_HTM), DataConstants.FIELD_TYPE_HTML);
        }

        @Test
        public void whenFileNameParameterIsNotNullAndExtensionIsNotHtmThenShouldReturnFileTypeEqualToTheExtensionInUpperCase(){
            String testFileExtension = "test";

            assertEquals(TechlibUtil.getFileType(fileName + testFileExtension),  testFileExtension.toUpperCase());
        }
    }



}