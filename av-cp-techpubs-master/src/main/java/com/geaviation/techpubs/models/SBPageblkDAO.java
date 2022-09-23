package com.geaviation.techpubs.models;

import com.geaviation.techpubs.data.util.DataConstants;
import com.geaviation.techpubs.data.util.DataUtil;

public class SBPageblkDAO extends PageblkDAO {
    private String category;
    private boolean sbAlert;

    public SBPageblkDAO(PageblkDAO pageblkDAO, String sbtype, String category, String tocVersion){
        setKey(pageblkDAO.getKey());
        setBookcaseKey(pageblkDAO.getBookcaseKey());
        setTitle(pageblkDAO.getTitle());
        setRevisiondate(pageblkDAO.getRevisiondate());
        setBookcasetitle(pageblkDAO.getBookcasetitle());
        setFileType(pageblkDAO.getFileType());
        setBookKey(pageblkDAO.getBookKey());
        setFileName(pageblkDAO.getFileName());
        setType(pageblkDAO.getType());
        setSbAlert(DataUtil.isSBAlert(sbtype));
        setCategory(category);
        setResourceUri(DataUtil
            .createFileResourceUri(pageblkDAO.getBookcaseKey(), DataConstants.SB_BOOK_KEY, pageblkDAO
                .getFileName(), tocVersion));
    }

    public SBPageblkDAO() {}

    public boolean isSbAlert() {
        return sbAlert;
    }

    public void setSbAlert(boolean sbAlert) {
        this.sbAlert = sbAlert;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
