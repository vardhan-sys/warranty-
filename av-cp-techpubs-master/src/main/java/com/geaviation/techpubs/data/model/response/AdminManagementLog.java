package com.geaviation.techpubs.data.model.response;

import com.geaviation.techpubs.services.excel.annotation.Excel;
import com.geaviation.techpubs.services.excel.annotation.ExcelColumn;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

@Excel(fileName = "docadmin_admin_audit", orderMatters = true)
public class AdminManagementLog implements AuditLog, Serializable {

    @ExcelColumn(name = "user", order = 1)
    private String user;

    @ExcelColumn(name = "action", order = 2)
    private String action;

    @ExcelColumn(name = "roles", order = 3)
    private String roles;

    @ExcelColumn(name = "engine_models", order = 4)
    private String engineModels;

    @ExcelColumn(name = "air_frames", order = 5)
    private String airFrames;

    @ExcelColumn(name = "document_types", order = 6)
    private String documentTypes;

    @ExcelColumn(name = "modified_by", order = 7)
    private String modifiedBy;

    @ExcelColumn(name = "modified_date", order = 8)
    private String modifiedDate;

    private String category;

    private String appId;

    private String UUID;

    public AdminManagementLog() { }

    public String getUser() { return user; }

    public void setUser(String user) { this.user = user; }

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public String getRoles() { return roles; }

    public void setRoles(String roles) { this.roles = roles; }

    public String getEngineModels() { return engineModels; }

    public void setEngineModels(String engineModels) { this.engineModels = engineModels; }

    public String getAirFrames() { return airFrames; }

    public void setAirFrames(String airFrames) { this.airFrames = airFrames; }

    public String getDocumentTypes() { return documentTypes; }

    public void setDocumentTypes(String documentTypes) { this.documentTypes = documentTypes; }

    public String getModifiedBy() { return modifiedBy; }

    public void setModifiedBy(String modifiedBy) { this.modifiedBy = modifiedBy; }

    public String getModifiedDate() { return modifiedDate; }

    public void setModifiedDate(String modifiedDate) { this.modifiedDate = modifiedDate; }

    @JsonIgnore
    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }

    @JsonIgnore
    public String getAppId() { return appId; }

    public void setAppId(String appId) { this.appId = appId; }

    @JsonIgnore
    public String getUUID() { return UUID; }

    public void setUUID(String UUID) { this.UUID = UUID; }
}
