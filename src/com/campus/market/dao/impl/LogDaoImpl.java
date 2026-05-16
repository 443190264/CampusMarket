package com.campus.market.dao.impl;

import com.campus.market.dao.LogDao;
import com.campus.market.entity.OperationLog;
import com.campus.market.exception.SystemException;
import com.campus.market.util.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class LogDaoImpl implements LogDao {

    @Override
    public boolean add(OperationLog log) {
        String sql = "INSERT INTO operation_log(operator_id, action, detail, log_time) VALUES(?,?,?,?)";
        List<Object> params = List.of(
                log.getOperatorId(),
                log.getAction(),
                log.getDetail(),
                Timestamp.valueOf(log.getLogTime())
        );
        return JdbcTemplate.executeUpdate(sql, params) > 0;
    }

    @Override
    public List<OperationLog> findAll() {
        String sql = "SELECT * FROM operation_log ORDER BY log_time DESC";
        return JdbcTemplate.queryForList(sql, List.of(), rs -> {
            try {
                return mapRowToLog(rs);
            } catch (SQLException e) {
                throw new SystemException("结果集映射失败", e);
            }
        });
    }

    @Override
    public List<OperationLog> findByOperatorId(int operatorId) {
        String sql = "SELECT * FROM operation_log WHERE operator_id = ? ORDER BY log_time DESC";
        return JdbcTemplate.queryForList(sql, List.of(operatorId), rs -> {
            try {
                return mapRowToLog(rs);
            } catch (SQLException e) {
                throw new SystemException("结果集映射失败", e);
            }
        });
    }

    @Override
    public List<OperationLog> findByOperatorIdWithPage(int operatorId, int pageNum, int pageSize) {
        String sql = "SELECT * FROM operation_log WHERE operator_id = ? ORDER BY log_time DESC LIMIT ? OFFSET ?";
        List<Object> params = List.of(operatorId, pageSize, (pageNum - 1) * pageSize);
        return JdbcTemplate.queryForList(sql, params, rs -> {
            try {
                return mapRowToLog(rs);
            } catch (SQLException e) {
                throw new SystemException("结果集映射失败", e);
            }
        });
    }

    @Override
    public List<OperationLog> findAllWithPage(int pageNum, int pageSize) {
        String sql = "SELECT * FROM operation_log ORDER BY log_time DESC LIMIT ? OFFSET ?";
        List<Object> params = List.of(pageSize, (pageNum - 1) * pageSize);
        return JdbcTemplate.queryForList(sql, params, rs -> {
            try {
                return mapRowToLog(rs);
            } catch (SQLException e) {
                throw new SystemException("结果集映射失败", e);
            }
        });
    }

    @Override
    public int countByOperatorId(int operatorId) {
        String sql = "SELECT COUNT(*) FROM operation_log WHERE operator_id = ?";
        return JdbcTemplate.queryForCount(sql, List.of(operatorId));
    }

    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM operation_log";
        return JdbcTemplate.queryForCount(sql, List.of());
    }

    private OperationLog mapRowToLog(ResultSet rs) throws SQLException {
        OperationLog log = new OperationLog();
        log.setId(rs.getInt("id"));
        Integer operatorId = (Integer) rs.getObject("operator_id");
        log.setOperatorId(operatorId);
        log.setAction(rs.getString("action"));
        log.setDetail(rs.getString("detail"));
        Timestamp ts = rs.getTimestamp("log_time");
        if (ts != null) {
            log.setLogTime(ts.toLocalDateTime());
        }
        return log;
    }
}