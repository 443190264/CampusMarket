package com.campus.market.dao.impl;

import com.campus.market.dao.TransactionDao;
import com.campus.market.entity.TransactionRecord;
import com.campus.market.exception.SystemException;
import com.campus.market.util.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class TransactionDaoImpl implements TransactionDao {

    @Override
    public boolean add(TransactionRecord record) {
        String sql = "INSERT INTO transaction (product_id, buyer_id, seller_id, amount, status) VALUES (?,?,?,?,?)";
        List<Object> params = List.of(
                record.getProductId(),
                record.getBuyerId(),
                record.getSellerId(),
                record.getAmount(),
                record.getStatus()
        );
        int generatedId = JdbcTemplate.executeInsert(sql, params);
        if (generatedId > 0) {
            record.setId(generatedId);
            return true;
        }
        return false;
    }

    @Override
    public TransactionRecord findById(int id) {
        String sql = "SELECT * FROM transaction WHERE id = ?";
        return JdbcTemplate.queryForObject(sql, List.of(id), rs -> {
            try {
                return mapRowToTransaction(rs);
            } catch (SQLException e) {
                throw new SystemException("结果集映射失败", e);
            }
        });
    }

    @Override
    public TransactionRecord findByProductId(int productId) {
        String sql = "SELECT * FROM transaction WHERE product_id = ?";
        return JdbcTemplate.queryForObject(sql, List.of(productId), rs -> {
            try {
                return mapRowToTransaction(rs);
            } catch (SQLException e) {
                throw new SystemException("结果集映射失败", e);
            }
        });
    }

    @Override
    public List<TransactionRecord> findByBuyerId(int buyerId) {
        String sql = "SELECT * FROM transaction WHERE buyer_id = ? ORDER BY trade_time DESC";
        return JdbcTemplate.queryForList(sql, List.of(buyerId), rs -> {
            try {
                return mapRowToTransaction(rs);
            } catch (SQLException e) {
                throw new SystemException("结果集映射失败", e);
            }
        });
    }

    @Override
    public List<TransactionRecord> findBySellerId(int sellerId) {
        String sql = "SELECT * FROM transaction WHERE seller_id = ? ORDER BY trade_time DESC";
        return JdbcTemplate.queryForList(sql, List.of(sellerId), rs -> {
            try {
                return mapRowToTransaction(rs);
            } catch (SQLException e) {
                throw new SystemException("结果集映射失败", e);
            }
        });
    }

    @Override
    public boolean updateStatus(int transactionId, String status) {
        String sql = "UPDATE transaction SET status = ? WHERE id = ?";
        return JdbcTemplate.executeUpdate(sql, List.of(status, transactionId)) > 0;
    }

    private TransactionRecord mapRowToTransaction(ResultSet rs) throws SQLException {
        TransactionRecord record = new TransactionRecord();
        record.setId(rs.getInt("id"));
        record.setProductId(rs.getInt("product_id"));
        record.setBuyerId(rs.getInt("buyer_id"));
        record.setSellerId(rs.getInt("seller_id"));
        record.setAmount(rs.getBigDecimal("amount"));
        record.setStatus(rs.getString("status"));
        Timestamp timestamp = rs.getTimestamp("trade_time");
        if (timestamp != null) {
            record.setTradeTime(timestamp.toLocalDateTime());
        }
        return record;
    }
}