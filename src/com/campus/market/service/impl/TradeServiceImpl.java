package com.campus.market.service.impl;

import com.campus.market.dao.ProductDao;
import com.campus.market.dao.StudentDao;
import com.campus.market.dao.TransactionDao;
import com.campus.market.dao.impl.ProductDaoImpl;
import com.campus.market.dao.impl.StudentDaoImpl;
import com.campus.market.dao.impl.TransactionDaoImpl;
import com.campus.market.entity.Product;
import com.campus.market.entity.Student;
import com.campus.market.entity.TransactionRecord;
import com.campus.market.exception.BusinessException;
import com.campus.market.exception.SystemException;
import com.campus.market.service.TradeService;
import com.campus.market.util.DBUtil;
import com.campus.market.util.LogUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TradeServiceImpl implements TradeService {

    private StudentDao studentDao = new StudentDaoImpl();
    private ProductDao productDao = new ProductDaoImpl();
    private TransactionDao transactionDao = new TransactionDaoImpl();

    @Override
    public boolean buyProduct(int buyerId, int productId) {
        // 1. 查询商品
        Product product = productDao.findById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在，ID=" + productId);
        }
        if (!"ON_SALE".equals(product.getStatus())) {
            throw new BusinessException("商品已下架或已售出");
        }
        if (product.getSellerId() == buyerId) {
            throw new BusinessException("不能购买自己发布的商品");
        }

        Student buyer = studentDao.findById(buyerId);
        Student seller = studentDao.findById(product.getSellerId());
        if (buyer == null || seller == null) {
            throw new BusinessException("买家或卖家信息不存在");
        }

        BigDecimal price = product.getPrice();
        if (buyer.getBalance().compareTo(price) < 0) {
            throw new BusinessException("余额不足，需要：" + price + "，当前余额：" + buyer.getBalance());
        }

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // 扣买家余额
            BigDecimal newBuyerBalance = buyer.getBalance().subtract(price);
            if (!updateBalance(conn, buyerId, newBuyerBalance)) {
                conn.rollback();
                throw new SystemException("更新买家余额失败");
            }

            // 加卖家余额
            BigDecimal newSellerBalance = seller.getBalance().add(price);
            if (!updateBalance(conn, seller.getId(), newSellerBalance)) {
                conn.rollback();
                throw new SystemException("更新卖家余额失败");
            }

            // 修改商品状态为 SOLD
            if (!updateProductStatus(conn, productId, "SOLD")) {
                conn.rollback();
                throw new SystemException("更新商品状态失败");
            }

            // 插入交易记录
            int transactionId = insertTransaction(conn, productId, buyerId, seller.getId(), price, "COMPLETED");
            if (transactionId == -1) {
                conn.rollback();
                throw new SystemException("生成交易记录失败");
            }

            conn.commit();
            LogUtil.record(buyerId, "购买商品", "商品ID=" + productId + "，金额=" + price);
            System.out.println("购买成功，交易ID=" + transactionId);
            return true;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw new SystemException("购买失败，数据库错误", e);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean returnProduct(int transactionId, int operatorId) {
        TransactionRecord record = transactionDao.findById(transactionId);
        if (record == null) {
            throw new BusinessException("交易记录不存在，ID=" + transactionId);
        }
        if (!"COMPLETED".equals(record.getStatus())) {
            throw new BusinessException("该交易已完成退货或状态异常，当前状态：" + record.getStatus());
        }
        if (operatorId != record.getBuyerId() && operatorId != record.getSellerId()) {
            throw new BusinessException("只有买卖双方可以退货，操作者ID=" + operatorId);
        }

        Product product = productDao.findById(record.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在，ID=" + record.getProductId());
        }

        BigDecimal amount = record.getAmount();

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            Student buyer = studentDao.findById(record.getBuyerId());
            Student seller = studentDao.findById(record.getSellerId());
            if (buyer == null || seller == null) {
                conn.rollback();
                throw new BusinessException("买家或卖家信息不存在");
            }

            // 卖家余额减少
            BigDecimal newSellerBalance = seller.getBalance().subtract(amount);
            if (newSellerBalance.compareTo(BigDecimal.ZERO) < 0) {
                conn.rollback();
                throw new BusinessException("卖家余额不足，无法退货，当前余额：" + seller.getBalance());
            }
            if (!updateBalance(conn, seller.getId(), newSellerBalance)) {
                conn.rollback();
                throw new SystemException("更新卖家余额失败");
            }

            // 买家余额增加
            BigDecimal newBuyerBalance = buyer.getBalance().add(amount);
            if (!updateBalance(conn, buyer.getId(), newBuyerBalance)) {
                conn.rollback();
                throw new SystemException("更新买家余额失败");
            }

            // 商品状态改回 ON_SALE
            if (!updateProductStatus(conn, product.getId(), "ON_SALE")) {
                conn.rollback();
                throw new SystemException("更新商品状态失败");
            }

            // 更新交易记录状态为 RETURNED
            if (!updateTransactionStatus(conn, transactionId, "RETURNED")) {
                conn.rollback();
                throw new SystemException("更新交易状态失败");
            }

            conn.commit();
            LogUtil.record(operatorId, "退货", "交易ID=" + transactionId + "，金额=" + amount);
            System.out.println("退货成功");
            return true;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw new SystemException("退货失败，数据库错误", e);
        } catch (BusinessException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw e;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 私有辅助方法（使用传入的 Connection）
    private boolean updateBalance(Connection conn, int studentId, BigDecimal newBalance) throws SQLException {
        String sql = "UPDATE student SET balance = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, newBalance);
            pstmt.setInt(2, studentId);
            return pstmt.executeUpdate() > 0;
        }
    }

    private boolean updateProductStatus(Connection conn, int productId, String status) throws SQLException {
        String sql = "UPDATE product SET status = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, productId);
            return pstmt.executeUpdate() > 0;
        }
    }

    private int insertTransaction(Connection conn, int productId, int buyerId, int sellerId,
                                  BigDecimal amount, String status) throws SQLException {
        String sql = "INSERT INTO transaction (product_id, buyer_id, seller_id, amount, status) VALUES (?,?,?,?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, productId);
            pstmt.setInt(2, buyerId);
            pstmt.setInt(3, sellerId);
            pstmt.setBigDecimal(4, amount);
            pstmt.setString(5, status);
            int affected = pstmt.executeUpdate();
            if (affected == 0) return -1;
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                return -1;
            }
        }
    }

    private boolean updateTransactionStatus(Connection conn, int transactionId, String status) throws SQLException {
        String sql = "UPDATE transaction SET status = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, transactionId);
            return pstmt.executeUpdate() > 0;
        }
    }
}