package com.campus.market.dao;

import com.campus.market.entity.Product;
import java.math.BigDecimal;
import java.util.List;

public interface ProductDao {
    // 新增物品（返回生成的id并回填）
    boolean add(Product product);

    // 根据id删除（物理删除，但通常业务上是下架，这里为了简单提供删除，但谨慎使用）
    boolean deleteById(int id);

    // 更新物品（所有字段）
    boolean update(Product product);

    // 根据id查询
    Product findById(int id);

    // 查询某卖家发布的所有物品
    List<Product> findBySellerId(int sellerId);

    /**
     * 多条件查询 + 排序（不分页，返回所有匹配结果）
     * @param excludeUserId 需要排除的卖家ID（通常为当前登录用户ID，若不需要排除则传 -1）
     */
    List<Product> search(String keyword, String category, BigDecimal minPrice, BigDecimal maxPrice,
                         String status, String sortBy, int excludeUserId);

    // 上下架（快捷方法）
    boolean updateStatus(int productId, String status);

     // 分页搜索
    List<Product> searchByPage(String keyword, String category, BigDecimal minPrice, BigDecimal maxPrice,
                               String status, String sortBy, int pageNum, int pageSize, int excludeUserId);

    // 统计符合条件的商品总数（用于分页计算总页数）
    int count(String keyword, String category, BigDecimal minPrice, BigDecimal maxPrice,
              String status, int excludeUserId);

    // 卖家自己的商品分页
    List<Product> findBySellerIdWithPage(int sellerId, int pageNum, int pageSize);
    int countBySellerId(int sellerId);
}