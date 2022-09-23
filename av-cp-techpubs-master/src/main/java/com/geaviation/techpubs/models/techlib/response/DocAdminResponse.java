package com.geaviation.techpubs.models.techlib.response;

import java.util.List;

public class DocAdminResponse {

    private List<?> data;

    public DocAdminResponse() { }

    public DocAdminResponse(List<?> data) { this.data = data; }

    public List<?> getData() { return data; }

    public void setData(List<?> data) { this.data = data; }
}
