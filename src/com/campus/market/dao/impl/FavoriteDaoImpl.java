package com.campus.market.dao.impl;

import com.campus.market.dao.FavoriteDao;
import com.campus.market.entity.Favorite;
import com.campus.market.exception.SystemException;
import com.campus.market.util.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class FavoriteDaoImpl implements FavoriteDao {

    @Override
    public boolean addFavorite(int studentId, int productId) {
        String sql = "INSERT IGNORE INTO favorite (student_id, product_id, fav_time) VALUES (?, ?, NOW())";
        return JdbcTemplate.executeUpdate(sql, List.of(studentId, productId)) > 0;
    }

    @Override
    public boolean removeFavorite(int studentId, int productId) {
        String sql = "DELETE FROM favorite WHERE student_id = ? AND product_id = ?";
        return JdbcTemplate.executeUpdate(sql, List.of(studentId, productId)) > 0;
    }

    @Override
    public boolean isFavorite(int studentId, int productId) {
        String sql = "SELECT 1 FROM favorite WHERE student_id = ? AND product_id = ? LIMIT 1";
        Integer result = JdbcTemplate.queryForObject(sql, List.of(studentId, productId), rs -> {
            try {
                return rs.getInt(1);
            } catch (SQLException e) {
                throw new SystemException("查询收藏状态失败", e);
            }
        });
        return result != null;
    }

    @Override
    public List<Favorite> findByStudentId(int studentId) {
        String sql = "SELECT * FROM favorite WHERE student_id = ? ORDER BY fav_time DESC";
        return JdbcTemplate.queryForList(sql, List.of(studentId), rs -> {
            try {
                return mapRowToFavorite(rs);
            } catch (SQLException e) {
                throw new SystemException("结果集映射失败", e);
            }
        });
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM favorite WHERE id = ?";
        return JdbcTemplate.executeUpdate(sql, List.of(id)) > 0;
    }

    private Favorite mapRowToFavorite(ResultSet rs) throws SQLException {
        Favorite fav = new Favorite();
        fav.setId(rs.getInt("id"));
        fav.setStudentId(rs.getInt("student_id"));
        fav.setProductId(rs.getInt("product_id"));
        Timestamp ts = rs.getTimestamp("fav_time");
        if (ts != null) {
            fav.setFavTime(ts.toLocalDateTime());
        }
        return fav;
    }
}