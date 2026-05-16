package com.campus.market.dao.impl;

import com.campus.market.dao.ProductDao;
import com.campus.market.entity.Product;
import com.campus.market.exception.SystemException;
import com.campus.market.util.JdbcTemplate;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ProductDaoImpl implements ProductDao {

    @Override
    public boolean add(Product product) {
        String sql = "INSERT INTO product(seller_id, title, description, price, category, status) VALUES(?,?,?,?,?,?)";
        List<Object> params = List.of(
                product.getSellerId(),
                product.getTitle(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getStatus() != null ? product.getStatus() : "ON_SALE"
        );
        int generatedId = JdbcTemplate.executeInsert(sql, params);
        if (generatedId > 0) {
            product.setId(generatedId);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM product WHERE id = ?";
        return JdbcTemplate.executeUpdate(sql, List.of(id)) > 0;
    }

    @Override
    public boolean update(Product product) {
        String sql = "UPDATE product SET seller_id=?, title=?, description=?, price=?, category=?, status=? WHERE id=?";
        List<Object> params = List.of(
                product.getSellerId(),
                product.getTitle(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getStatus(),
                product.getId()
        );
        return JdbcTemplate.executeUpdate(sql, params) > 0;
    }

    @Override
    public Product findById(int id) {
        String sql = "SELECT * FROM product WHERE id = ?";
        return JdbcTemplate.queryForObject(sql, List.of(id), rs -> {
            try {
                return mapRowToProduct(rs);
            } catch (SQLException e) {
                throw new SystemException("结果集映射失败", e);
            }
        });
    }

    @Override
    public List<Product> findBySellerId(int sellerId) {
        String sql = "SELECT * FROM product WHERE seller_id = ?";
        return JdbcTemplate.queryForList(sql, List.of(sellerId), rs -> {
            try {
                return mapRowToProduct(rs);
            } catch (SQLException e) {
                throw new SystemException("结果集映射失败", e);
            }
        });
    }

    @Override
    public List<Product> findBySellerIdWithPage(int sellerId, int pageNum, int pageSize) {
        String sql = "SELECT * FROM product WHERE seller_id = ? ORDER BY publish_time DESC LIMIT ? OFFSET ?";
        List<Object> params = List.of(sellerId, pageSize, (pageNum - 1) * pageSize);
        return JdbcTemplate.queryForList(sql, params, rs -> {
            try {
                return mapRowToProduct(rs);
            } catch (SQLException e) {
                throw new SystemException("结果集映射失败", e);
            }
        });
    }

    @Override
    public int countBySellerId(int sellerId) {
        String sql = "SELECT COUNT(*) FROM product WHERE seller_id = ?";
        return JdbcTemplate.queryForCount(sql, List.of(sellerId));
    }

    @Override
    public List<Product> search(String keyword, String category, BigDecimal minPrice, BigDecimal maxPrice,
                                String status, String sortBy, int excludeUserId) {
        StringBuilder sql = new StringBuilder("SELECT * FROM product WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND title LIKE ?");
            params.add("%" + keyword + "%");
        }
        if (category != null && !category.trim().isEmpty()) {
            sql.append(" AND category = ?");
            params.add(category);
        }
        if (minPrice != null) {
            sql.append(" AND price >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append(" AND price <= ?");
            params.add(maxPrice);
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status);
        }
        if (excludeUserId > 0) {
            sql.append(" AND seller_id != ?");
            params.add(excludeUserId);
        }

        if (sortBy != null) {
            switch (sortBy) {
                case "price_asc":
                    sql.append(" ORDER BY price ASC");
                    break;
                case "price_desc":
                    sql.append(" ORDER BY price DESC");
                    break;
                default:
                    sql.append(" ORDER BY publish_time DESC");
            }
        } else {
            sql.append(" ORDER BY publish_time DESC");
        }

        return JdbcTemplate.queryForList(sql.toString(), params, rs -> {
            try {
                return mapRowToProduct(rs);
            } catch (SQLException e) {
                throw new SystemException("结果集映射失败", e);
            }
        });
    }

    @Override
    public List<Product> searchByPage(String keyword, String category, BigDecimal minPrice, BigDecimal maxPrice,
                                      String status, String sortBy, int pageNum, int pageSize, int excludeUserId) {
        StringBuilder sql = new StringBuilder("SELECT * FROM product WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND title LIKE ?");
            params.add("%" + keyword + "%");
        }
        if (category != null && !category.trim().isEmpty()) {
            sql.append(" AND category = ?");
            params.add(category);
        }
        if (minPrice != null) {
            sql.append(" AND price >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append(" AND price <= ?");
            params.add(maxPrice);
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status);
        }
        if (excludeUserId > 0) {
            sql.append(" AND seller_id != ?");
            params.add(excludeUserId);
        }

        if (sortBy != null) {
            switch (sortBy) {
                case "price_asc":
                    sql.append(" ORDER BY price ASC");
                    break;
                case "price_desc":
                    sql.append(" ORDER BY price DESC");
                    break;
                default:
                    sql.append(" ORDER BY publish_time DESC");
            }
        } else {
            sql.append(" ORDER BY publish_time DESC");
        }

        sql.append(" LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((pageNum - 1) * pageSize);

        return JdbcTemplate.queryForList(sql.toString(), params, rs -> {
            try {
                return mapRowToProduct(rs);
            } catch (SQLException e) {
                throw new SystemException("结果集映射失败", e);
            }
        });
    }

    @Override
    public int count(String keyword, String category, BigDecimal minPrice, BigDecimal maxPrice,
                     String status, int excludeUserId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM product WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND title LIKE ?");
            params.add("%" + keyword + "%");
        }
        if (category != null && !category.trim().isEmpty()) {
            sql.append(" AND category = ?");
            params.add(category);
        }
        if (minPrice != null) {
            sql.append(" AND price >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append(" AND price <= ?");
            params.add(maxPrice);
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status);
        }
        if (excludeUserId > 0) {
            sql.append(" AND seller_id != ?");
            params.add(excludeUserId);
        }

        return JdbcTemplate.queryForCount(sql.toString(), params);
    }

    @Override
    public boolean updateStatus(int productId, String status) {
        String sql = "UPDATE product SET status = ? WHERE id = ?";
        return JdbcTemplate.executeUpdate(sql, List.of(status, productId)) > 0;
    }

    private Product mapRowToProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setSellerId(rs.getInt("seller_id"));
        p.setTitle(rs.getString("title"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setCategory(rs.getString("category"));
        p.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("publish_time");
        if (ts != null) {
            p.setPublishTime(ts.toLocalDateTime());
        }
        return p;
    }
}