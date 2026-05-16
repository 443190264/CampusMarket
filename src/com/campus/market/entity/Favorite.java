package com.campus.market.entity;

import java.time.LocalDateTime;

public class Favorite {
    private Integer id;
    private Integer studentId;
    private Integer productId;
    private LocalDateTime favTime;

    public Favorite() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public LocalDateTime getFavTime() {
        return favTime;
    }

    public void setFavTime(LocalDateTime favTime) {
        this.favTime = favTime;
    }

    public Favorite(Integer studentId, Integer productId) {
        this.studentId = studentId;
        this.productId = productId;
    }



}