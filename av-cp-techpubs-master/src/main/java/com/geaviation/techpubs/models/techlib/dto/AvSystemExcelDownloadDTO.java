package com.geaviation.techpubs.models.techlib.dto;

import java.util.Date;

import com.geaviation.techpubs.services.excel.annotation.Excel;
import com.geaviation.techpubs.services.excel.annotation.ExcelColumn;
import com.geaviation.techpubs.services.util.AppConstants;

@Excel(fileName = "Aviation_Systems", orderMatters = true)
public class AvSystemExcelDownloadDTO {
    @ExcelColumn(name = AppConstants.DOCUMENT_TITLE,  order = 2)
    private String documentTitle;

    @ExcelColumn(name = AppConstants.DOCUMENT_SITE,  order = 5)
    private String documentSite;

    @ExcelColumn(name = AppConstants.DOCUMENT_TYPE,  order = 4)
    private String documentType;

    @ExcelColumn(name = AppConstants.DOCUMENT_NUMBER, order = 1)
    private String documentNumber;

    @ExcelColumn(name = AppConstants.PARTNUMBERS, order = 6)
    private String partsAffected;

    @ExcelColumn(name = AppConstants.AIR_FRAMES, order = 3)
    private String airFrames;

    @ExcelColumn(name = AppConstants.REVISION_DATE, order = 8)
    private String documentRevisionDate;
    
    @ExcelColumn(name = AppConstants.REVISION_NUMBER, order = 7)
    private String revisionNumber;
    
    @ExcelColumn(name = AppConstants.PUBLISH_DATE, order = 9)
    private String publishedDate;

	

	public AvSystemExcelDownloadDTO(String documentTitle, String documentSite, String documentType,
			String documentNumber, String partsAffected, String airFrames, String documentRevisionDate,
			String revisionNumber, String publishedDate) {
		
		this.documentTitle = documentTitle;
		this.documentSite = documentSite;
		this.documentType = documentType;
		this.documentNumber = documentNumber;
		this.partsAffected = partsAffected;
		this.airFrames = airFrames;
		this.documentRevisionDate = documentRevisionDate;
		this.revisionNumber = revisionNumber;
		this.publishedDate = publishedDate;
	}

	public String getDocumentTitle() {
		return documentTitle;
	}

	public void setDocumentTitle(String documentTitle) {
		this.documentTitle = documentTitle;
	}
	
	public String getDocumentSite() {
		return documentSite;
	}

	public void setDocumentSite(String documentSite) {
		this.documentSite = documentSite;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public String getPartsAffected() {
		return partsAffected;
	}

	public void setPartsAffected(String partsAffected) {
		this.partsAffected = partsAffected;
	}

	public String getAirFrames() {
		return airFrames;
	}

	public void setAirFrames(String airFrames) {
		this.airFrames = airFrames;
	}

	public String getDocumentRevisionDate() {
		return documentRevisionDate;
	}

	public void setDocumentRevisionDate(String documentRevisionDate) {
		this.documentRevisionDate = documentRevisionDate;
	}

	public String getRevisionNumber() {
		return revisionNumber;
	}

	public void setRevisionNumber(String revisionNumber) {
		this.revisionNumber = revisionNumber;
	}

	public String getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(String publishedDate) {
		this.publishedDate = publishedDate;
	}
	
}
