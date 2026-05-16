package com.campus.market.dao.impl;

import com.campus.market.dao.BrowseHistoryDao;
import com.campus.market.entity.BrowseHistory;
import com.campus.market.exception.SystemException;
import com.campus.market.util.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class BrowseHistoryDaoImpl implements BrowseHistoryDao {

    @Override
    public boolean recordBrowse(int studentId, int productId) {
        String sql = "INSERT INTO browse_history (student_id, product_id, browse_time) VALUES (?, ?, NOW()) " +
                "ON DUPLICATE KEY UPDATE browse_time = NOW()";
        return JdbcTemplate.executeUpdate(sql, List.of(studentId, productId)) > 0;
    }

    @Override
    public List<BrowseHistory> findByStudentId(int studentId) {
        String sql = "SELECT * FROM browse_history WHERE student_id = ? ORDER BY browse_time DESC";
        return JdbcTemplate.queryForList(sql, List.of(studentId), rs -> {
            try {
                return mapRowToHistory(rs);
            } catch (SQLException e) {
                throw new SystemException("结果集映射失败", e);
            }
        });
    }

    @Override
    public List<BrowseHistory> findByStudentIdWithPage(int studentId, int pageNum, int pageSize) {
        String sql = "SELECT * FROM browse_history WHERE student_id = ? ORDER BY browse_time DESC LIMIT ? OFFSET ?";
        List<Object> params = List.of(studentId, pageSize, (pageNum - 1) * pageSize);
        return JdbcTemplate.queryForList(sql, params, rs -> {
            try {
                return mapRowToHistory(rs);
            } catch (SQLException e) {
                throw new SystemException("结果集映射失败", e);
            }
        });
    }

    @Override
    public int countByStudentId(int studentId) {
        String sql = "SELECT COUNT(*) FROM browse_history WHERE student_id = ?";
        return JdbcTemplate.queryForCount(sql, List.of(studentId));
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM browse_history WHERE id = ?";
        return JdbcTemplate.executeUpdate(sql, List.of(id)) > 0;
    }

    @Override
    public boolean clearByStudentId(int studentId) {
        String sql = "DELETE FROM browse_history WHERE student_id = ?";
        return JdbcTemplate.executeUpdate(sql, List.of(studentId)) > 0;
    }

    private BrowseHistory mapRowToHistory(ResultSet rs) throws SQLException {
        BrowseHistory bh = new BrowseHistory();
        bh.setId(rs.getInt("id"));
        bh.setStudentId(rs.getInt("student_id"));
        bh.setProductId(rs.getInt("product_id"));
        Timestamp ts = rs.getTimestamp("browse_time");
        if (ts != null) {
            bh.setBrowseTime(ts.toLocalDateTime());
        }
        return bh;
    }
}