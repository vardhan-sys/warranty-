package com.geaviation.techpubs.models.techlib.dto;

import com.geaviation.techpubs.services.excel.annotation.Excel;
import com.geaviation.techpubs.services.excel.annotation.ExcelColumn;
import com.geaviation.techpubs.services.util.AppConstants;

import java.util.List;

@Excel(fileName = "permissions", orderMatters = true)
public class RolePermissionsDto {

    @ExcelColumn(name = AppConstants.ROLE, order = 1)
    private String role;

    @ExcelColumn(name = AppConstants.ENGINE_MODELS, order = 2)
    private List<String> engineModels;

    @ExcelColumn(name = AppConstants.AIRFRAMES, order = 3)
    private List<String> airFrames;

    @ExcelColumn(name = AppConstants.DOCUMENT_TYPES, order = 4)
    private List<String> docTypes;

    public RolePermissionsDto() { }

    public RolePermissionsDto(String role, List<String> engineModels, List<String> airFrames, List<String> docTypes) {
        this.role = role;
        this.engineModels = engineModels;
        this.airFrames = airFrames;
        this.docTypes = docTypes;
    }

    public String getRole() { return role; }

    public void setRole(String role) { this.role = role; }

    public List<String> getEngineModels() { return engineModels; }

    public void setEngineModels(List<String> engineModels) { this.engineModels = engineModels; }

    public List<String> getAirFrames() { return airFrames; }

    public void setAirFrames(List<String> airFrames) { this.airFrames = airFrames; }

    public List<String> getDocTypes() { return docTypes; }

    public void setDocTypes(List<String> docTypes) { this.docTypes = docTypes; }
}
