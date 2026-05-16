package com.campus.market.util;

import com.campus.market.exception.SystemException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class JdbcTemplate {

    public static <T> List<T> queryForList(String sql, List<Object> params, Function<ResultSet, T> mapper) {
        List<T> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapper.apply(rs));
            }
        } catch (SQLException e) {
            throw new SystemException("数据库查询失败: " + sql, e);
        }
        return list;
    }

    public static <T> T queryForObject(String sql, List<Object> params, Function<ResultSet, T> mapper) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapper.apply(rs);
            }
        } catch (SQLException e) {
            throw new SystemException("数据库查询失败: " + sql, e);
        }
        return null;
    }

    public static int queryForCount(String sql, List<Object> params) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new SystemException("数据库统计失败: " + sql, e);
        }
        return 0;
    }

    public static int executeUpdate(String sql, List<Object> params) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SystemException("数据库更新失败: " + sql, e);
        }
    }

    public static int executeInsert(String sql, List<Object> params) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            int affected = pstmt.executeUpdate();
            if (affected == 0) return -1;
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new SystemException("数据库插入失败: " + sql, e);
        }
        return -1;
    }
}