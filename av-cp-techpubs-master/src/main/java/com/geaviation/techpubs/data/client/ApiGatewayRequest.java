package com.geaviation.techpubs.data.client;

import com.amazonaws.http.HttpMethodName;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ApiGatewayRequest implements Serializable {

    private HttpMethodName httpMethod;
    private String resourcePath;
    private InputStream body;
    private Map<String, String> headers;
    private Map<String, List<String>> parameters;

    public ApiGatewayRequest() { /* empty constructor */ }

    public HttpMethodName getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethodName httpMethod) {
        this.httpMethod = httpMethod;
    }

    public ApiGatewayRequest withHttpMethod(HttpMethodName httpMethod) {
        this.setHttpMethod(httpMethod);
        return this;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public ApiGatewayRequest withResourcePath(String resourcePath) {
        this.setResourcePath(resourcePath);
        return this;
    }

    public InputStream getBody() {
        return body;
    }

    public void setBody(InputStream body) {
        this.body = body;
    }

    public ApiGatewayRequest withBody(InputStream body) {
        this.setBody(body);
        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public ApiGatewayRequest withHeaders(Map<String, String> headers) {
        this.setHeaders(headers);
        return this;
    }

    public Map<String, List<String>> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, List<String>> parameters) {
        this.parameters = parameters;
    }

    public ApiGatewayRequest withParameters(Map<String, List<String>> parameters) {
        this.setParameters(parameters);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getHttpMethod() != null) {
            sb.append("HttpMethod: ").append(this.getHttpMethod()).append(",");
        }

        if (this.getResourcePath() != null) {
            sb.append("ResourcePath: ").append(this.getResourcePath()).append(",");
        }

        if (this.getBody() != null) {
            sb.append("Body: ").append(this.getBody()).append(",");
        }

        if (this.getHeaders() != null) {
            sb.append("Headers: ").append(this.getHeaders()).append(",");
        }

        if (this.getParameters() != null) {
            sb.append("Parameters: ").append(this.getParameters()).append(",");
        }

        sb.append("}");
        return sb.toString();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiGatewayRequest request = (ApiGatewayRequest) o;
        return httpMethod == request.httpMethod &&
                resourcePath.equals(request.resourcePath) &&
                body.equals(request.body) &&
                headers.equals(request.headers) &&
                parameters.equals(request.parameters);
    }

    public int hashCode() {
        return Objects.hash(httpMethod, resourcePath, body, headers, parameters);
    }

}
