package com.campus.market.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Product {
    private Integer id;
    private Integer sellerId;          // 发布者ID（关联 student.id）
    private String title;
    private String description;
    private BigDecimal price;
    private String category;
    private String status;
    private LocalDateTime publishTime;


    public Product() {
    }

    // 发布时需要的构造（不含id和publishTime）
    public Product(Integer sellerId, String title, String description, BigDecimal price, String category, String status) {
        this.sellerId = sellerId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
        this.status = status;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getSellerId() { return sellerId; }
    public void setSellerId(Integer sellerId) { this.sellerId = sellerId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getPublishTime() { return publishTime; }
    public void setPublishTime(LocalDateTime publishTime) { this.publishTime = publishTime; }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", sellerId=" + sellerId +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", status='" + status + '\'' +
                ", publishTime=" + publishTime +
                '}';
    }
}