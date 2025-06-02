package ru.spbstu.telematics;

import java.util.Map;

class HttpRequest {
    private String method;
    private String path;
    private Map<String, String> headers;
    private byte[] body;

    public String getMethod() { return method; }
    public String getPath() { return path; }
    public Map<String, String> getHeaders() { return headers; }
    public byte[] getBody() { return body; }
    public void setMethod(String method) { this.method = method; }
    public void setPath(String path) { this.path = path; }
    public void setHeaders(Map<String, String> headers) { this.headers = headers; }
    public void setBody(byte[] body) { this.body = body; }
}
