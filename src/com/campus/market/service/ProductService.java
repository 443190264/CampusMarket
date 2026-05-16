package com.campus.market.service;

import com.campus.market.entity.Product;
import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    boolean publish(Product product);
    // 修改商品（业务上会校验 sellerId 是否匹配）
    boolean modify(Product product, int currentUserId);
    boolean offShelf(int productId, int sellerId);
    boolean onShelf(int productId, int sellerId);

    // 根据ID获取商品
    Product getProductById(int productId);

    //多条件搜索（不分页，返回所有匹配结果）
    List<Product> searchProducts(String keyword, String category, BigDecimal minPrice, BigDecimal maxPrice,
                                 String status, String sortBy, int excludeUserId);

    // 获取某个卖家的所有商品（不分页）
    List<Product> getProductsBySeller(int sellerId);


     //多条件搜索（分页）
    List<Product> searchProductsByPage(String keyword, String category, BigDecimal minPrice, BigDecimal maxPrice,
                                       String status, String sortBy, int pageNum, int pageSize, int excludeUserId);

     //获取符合条件的商品总数（用于分页）
    int getProductCount(String keyword, String category, BigDecimal minPrice, BigDecimal maxPrice,
                        String status, int excludeUserId);

    // 卖家自己的商品分页（无需排除）
    List<Product> getProductsBySellerWithPage(int sellerId, int pageNum, int pageSize);
    int getProductCountBySeller(int sellerId);
}