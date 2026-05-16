package com.campus.market.entity;

import java.time.LocalDateTime;

public class BrowseHistory {
    private Integer id;
    private Integer studentId;
    private Integer productId;
    private LocalDateTime browseTime;

    public BrowseHistory() {}
    public BrowseHistory(Integer studentId, Integer productId) {
        this.studentId = studentId;
        this.productId = productId;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public LocalDateTime getBrowseTime() { return browseTime; }
    public void setBrowseTime(LocalDateTime browseTime) { this.browseTime = browseTime; }

    @Override
    public String toString() {
        return "BrowseHistory{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", productId=" + productId +
                ", browseTime=" + browseTime +
                '}';
    }
}