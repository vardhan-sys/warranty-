package com.geaviation.techpubs.data.model.response;

import java.util.List;
import java.util.Map;

public class AuditLogResponse {

    private int count;
    private Map<String, String> lastEvaluatedKey;
    private List<? extends AuditLog> logs;

    public AuditLogResponse() { }

    public AuditLogResponse(int count, Map<String, String> lastEvaluatedKey, List<? extends AuditLog> logs) {
        this.count = count;
        this.lastEvaluatedKey = lastEvaluatedKey;
        this.logs = logs;
    }

    public int getCount() { return count; }

    public void setCount(int count) { this.count = count; }

    public Map<String, String> getLastEvaluatedKey() { return lastEvaluatedKey; }

    public void setLastEvaluatedKey(Map<String, String> lastEvaluatedKey) { this.lastEvaluatedKey = lastEvaluatedKey; }

    public List<? extends AuditLog> getLogs() { return logs; }

    public void setLogs(List<? extends AuditLog> logs) {
        this.logs = logs;
    }
}
