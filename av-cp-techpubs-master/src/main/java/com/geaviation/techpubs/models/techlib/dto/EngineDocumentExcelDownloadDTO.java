package com.geaviation.techpubs.models.techlib.dto;

import com.geaviation.techpubs.services.excel.annotation.Excel;
import com.geaviation.techpubs.services.excel.annotation.ExcelColumn;
import com.geaviation.techpubs.services.util.AppConstants;

import java.util.Date;

@Excel(fileName = "engine_documents", orderMatters = true)
public class EngineDocumentExcelDownloadDTO {
	
	@ExcelColumn(name = AppConstants.DOCUMENT_TITLE, order = 2)
	private String documentTitle;
	
	@ExcelColumn(name = AppConstants.PART_NAME, order = 3)
    	private String partName;
	
	@ExcelColumn(name = AppConstants.EMAIL_NOTIFICATION, order = 4)
    	private Boolean emailNotification;
	
	@ExcelColumn(name = AppConstants.CREATED_DATE, order = 8)
    	private String createdDate;
	
	@ExcelColumn(name = AppConstants.LAST_UPDATED_DATE, order = 9)
    	private String lastUpdatedDate;
	
	@ExcelColumn(name = AppConstants.DOCUMENT_TYPE, order = 1)
    	private String documentType;
	
	@ExcelColumn(name = AppConstants.ENGINEMODELS, order = 5)
    	private String engineModels;
	
	@ExcelColumn(name = AppConstants.PARTNUMBERS, order = 6)
    	private String partNumbers;
	
	@ExcelColumn(name = AppConstants.CAS_NUMBERS, order = 7)
    	private String casNumbers;
	
	@ExcelColumn(name = AppConstants.ISSUE_DATE, order = 9)
    	private String issueDate;
	
	public EngineDocumentExcelDownloadDTO() {
    	}

	
	public String getDocumentTitle() {
		return documentTitle;
	}

	public void setDocumentTitle(String documentTitle) {
		this.documentTitle = documentTitle;
	}

	public String getPartName() {
		return partName;
	}

	public void setPartName(String partName) {
		this.partName = partName;
	}

	public Boolean getEmailNotification() {
		return emailNotification;
	}

	public void setEmailNotification(Boolean emailNotification) {
		this.emailNotification = emailNotification;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(String lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getEngineModels() {
		return engineModels;
	}

	public void setEngineModels(String engineModels) {
		this.engineModels = engineModels;
	}

	public String getPartNumbers() {
		return partNumbers;
	}

	public void setPartNumbers(String partNumbers) {
		this.partNumbers = partNumbers;
	}

	public String getCasNumbers() {
		return casNumbers;
	}

	public void setCasNumbers(String casNumbers) {
		this.casNumbers = casNumbers;
	}
	
	public String getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}
	
}
