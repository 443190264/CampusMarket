package com.campus.market.entity;

import java.time.LocalDateTime;

public class OperationLog {
    private Integer id;
    private Integer operatorId;
    private String action;
    private String detail;
    private LocalDateTime logTime;


    public OperationLog() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getOperatorId() { return operatorId; }
    public void setOperatorId(Integer operatorId) { this.operatorId = operatorId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public LocalDateTime getLogTime() { return logTime; }
    public void setLogTime(LocalDateTime logTime) { this.logTime = logTime; }
}