package com.hotel.model.entity;

import java.time.LocalDateTime;

public class OperationLog {

    private Integer logId;
    private Integer userId;
    private LocalDateTime logTime;
    private String logType;
    private String logContent;
    private String ipAddress;

    public Integer getLogId() { return logId; }
    public void setLogId(Integer logId) { this.logId = logId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public LocalDateTime getLogTime() { return logTime; }
    public void setLogTime(LocalDateTime logTime) { this.logTime = logTime; }

    public String getLogType() { return logType; }
    public void setLogType(String logType) { this.logType = logType; }

    public String getLogContent() { return logContent; }
    public void setLogContent(String logContent) { this.logContent = logContent; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
}