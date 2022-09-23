package com.geaviation.techpubs.models.techlib.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.geaviation.techpubs.services.excel.annotation.Excel;
import com.geaviation.techpubs.services.excel.annotation.ExcelColumn;
import com.geaviation.techpubs.services.util.AppConstants;
import java.util.Date;

import java.sql.Timestamp;
import java.util.Date;

@Excel(fileName = "bookcases_with_online_versions", orderMatters = true)
public class BookcaseWithOnlineVersionDto {

    @ExcelColumn(name = AppConstants.BOOKCASE_TITLE, order = 1)
    private String bookcaseTitle;

    @ExcelColumn(name = AppConstants.GEK, order = 2)
    private String bookcaseKey;

    @ExcelColumn(name = AppConstants.ENGINE_FAMILY, order = 3)
    private String engineFamily;

    @ExcelColumn(name = AppConstants.ONLINE_VERSION, order = 4)
    private String onlineVersion;

    @ExcelColumn(name = "send_email", order = 5)
    private Boolean sendEmail;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ExcelColumn(name = "last_email_sent_date", order = 6)
    private Date lastEmailSentDate;

    public BookcaseWithOnlineVersionDto(String bookcaseTitle, String bookcaseKey, String engineFamily, String onlineVersion) {
        this.bookcaseTitle = bookcaseTitle;
        this.bookcaseKey = bookcaseKey;
        this.engineFamily = engineFamily;
        this.onlineVersion = onlineVersion;
    }

    public BookcaseWithOnlineVersionDto(String bookcaseTitle, String bookcaseKey, String engineFamily, String onlineVersion, Boolean sendEmail, Date lastEmailSentDate) {
        this.bookcaseTitle = bookcaseTitle;
        this.bookcaseKey = bookcaseKey;
        this.engineFamily = engineFamily;
        this.onlineVersion = onlineVersion;
        this.sendEmail = sendEmail;
        this.lastEmailSentDate = lastEmailSentDate;
    }

    public String getBookcaseTitle() { return bookcaseTitle; }

    public void setBookcaseTitle(String bookcaseTitle) { this.bookcaseTitle = bookcaseTitle; }

    public String getBookcaseKey() { return bookcaseKey; }

    public void setBookcaseKey(String bookcaseKey) { this.bookcaseKey = bookcaseKey; }

    public String getEngineFamily() { return engineFamily; }

    public void setEngineFamily(String engineFamily) { this.engineFamily = engineFamily; }

    public String getOnlineVersion() { return onlineVersion; }

    public void setOnlineVersion(String onlineVersion) { this.onlineVersion = onlineVersion; }

    public Boolean getSendEmail() { return sendEmail; }

    public void setSendEmail(Boolean sendEmail) { this.sendEmail = sendEmail;  }

    public Date getLastEmailSentDate() { return lastEmailSentDate; }

    public void setLastEmailSentDate(Date lastEmailSentDate) { this.lastEmailSentDate = lastEmailSentDate; }
}