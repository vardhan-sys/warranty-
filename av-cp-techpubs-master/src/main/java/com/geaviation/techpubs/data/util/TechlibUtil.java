package com.geaviation.techpubs.data.util;

import static com.geaviation.techpubs.data.util.DataConstants.IC_PAGEBLK_TYPE;
import static com.geaviation.techpubs.data.util.DataConstants.MANUAL_PAGEBLK_TYPE;
import static com.geaviation.techpubs.data.util.DataConstants.TR_PAGEBLK_TYPE;

import com.geaviation.techpubs.services.util.TechpubsAppUtil;
import org.apache.commons.io.FilenameUtils;

public  class TechlibUtil {

    private TechlibUtil() {}

    public static boolean isPageBlkType(String type){
        if (type == null)
            return false;

        return type.equals(IC_PAGEBLK_TYPE)
                || type.equals(TR_PAGEBLK_TYPE)
                || type.equals(MANUAL_PAGEBLK_TYPE);

    }

    public static String createFileResourceUri(String bookcaseKey, String bookKey, String filename){
        String resourceUri = null;
        if (TechpubsAppUtil.isNotNullandEmpty(bookcaseKey) && TechpubsAppUtil.isNotNullandEmpty(bookKey) && TechpubsAppUtil.isNotNullandEmpty(filename)){
            resourceUri =  DataConstants.TECHPUBS_FILE_URI.replace(DataConstants.BOOKCASE_KEY_URI_PARAMETER, bookcaseKey)
                    .replace(DataConstants.BOOK_KEY_URI_PARAMETER, bookKey)
                    .replace(DataConstants.FILENAME_URI_PARAMETER, filename);
        }

        return resourceUri;
    }


    public static String getFileType(String filename) {
        String fileType = null;

        if (filename != null) {
            String fileExtension = FilenameUtils.getExtension(filename);
            if (DataConstants.FILE_EXTENSION_HTM.equalsIgnoreCase(fileExtension)) {
                fileType = DataConstants.FIELD_TYPE_HTML;
            } else {
                fileType = (fileExtension == null ? "" : fileExtension.toUpperCase());
            }
        }

        return fileType;
    }
}
