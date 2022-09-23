package com.geaviation.techpubs.models.download;

import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OverlayDownloadRequest {
    private static final String COLON = ":";
    private static final String PIPE = "|";

    @ApiParam(name = "program", value = "eg. gek112865", allowMultiple = false, required = false)
    private String program;

    @ApiParam(name = "downloadType", value = "eg. source", allowMultiple = false, required = false)
    private String downloadType;

    @ApiParam(name = "type", value = "eg. ic", allowMultiple = false, required = false)
    private String type;

    @ApiParam(name = "files", value = "eg. ", allowMultiple = false, required = false)
    private List<OverlayDownloadFile> files;

    public OverlayDownloadRequest() {
        this.files = new ArrayList<>();
    }

    public OverlayDownloadRequest(String program, String downloadType, String type, List<OverlayDownloadFile> files) {
        this.program = program;
        this.downloadType = downloadType;
        this.type = type;
        this.files = files;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getDownloadType() {
        return downloadType;
    }

    public void setDownloadType(String downloadType) {
        this.downloadType = downloadType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFiles(String filesString) {
        List<OverlayDownloadFile> list = new ArrayList<>();
        if (StringUtils.isNotEmpty(filesString)) {
            String[] filesSplit = StringUtils.split(filesString, PIPE);
            for (String file : filesSplit) {
                String[] fileSplit = file.split(COLON);
                list.add(new OverlayDownloadFile(fileSplit[0], fileSplit[1]));
            }
        }

        this.files = list;
    }

    public List<OverlayDownloadFile> getFiles() {
        return files;
    }

    public String getFilesAsString() {
        return files.stream().map(OverlayDownloadFile::toString).collect(Collectors.joining(PIPE));
    }

    public int fileCount() {
        return files.size();
    }
}
