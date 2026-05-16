package com.campus.market.service;

public interface TradeService {
    boolean buyProduct(int buyerId, int productId);
    boolean returnProduct(int transactionId, int operatorId);
}