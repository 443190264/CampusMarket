package com.campus.market.dao;

import com.campus.market.entity.TransactionRecord;
import java.util.List;

public interface TransactionDao {
    boolean add(TransactionRecord record);
    TransactionRecord findByProductId(int productId);
    List<TransactionRecord> findByBuyerId(int buyerId);
    List<TransactionRecord> findBySellerId(int sellerId);
    boolean updateStatus(int transactionId, String status);
    TransactionRecord findById(int id);
}