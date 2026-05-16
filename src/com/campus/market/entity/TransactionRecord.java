package com.campus.market.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionRecord {
    private Integer id;
    private Integer productId;
    private Integer buyerId;
    private Integer sellerId;
    private BigDecimal amount;
    private String status;    // COMPLETED, RETURNED
    private LocalDateTime tradeTime;

    public TransactionRecord() {}

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getProductId() {
        return productId;
    }
    public void setProductId(Integer productId) {
        this.productId = productId;
    }
    public Integer getBuyerId() {
        return buyerId;
    }
    public void setBuyerId(Integer buyerId) {
        this.buyerId = buyerId;
    }
    public Integer getSellerId() {
        return sellerId;
    }
    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public LocalDateTime getTradeTime() {
        return tradeTime;
    }
    public void setTradeTime(LocalDateTime tradeTime) {
        this.tradeTime = tradeTime;
    }

    public TransactionRecord(Integer productId, Integer buyerId, Integer sellerId, BigDecimal amount, String status) {
        this.productId = productId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.amount = amount;
        this.status = status;
    }

}