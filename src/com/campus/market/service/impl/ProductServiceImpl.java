package com.campus.market.service.impl;

import com.campus.market.dao.ProductDao;
import com.campus.market.dao.impl.ProductDaoImpl;
import com.campus.market.entity.Product;
import com.campus.market.service.ProductService;
import com.campus.market.exception.BusinessException;
import java.math.BigDecimal;
import java.util.List;

public class ProductServiceImpl implements ProductService {
    private ProductDao productDao = new ProductDaoImpl();

    @Override
    public boolean publish(Product product) {
        if (product.getTitle() == null || product.getTitle().trim().isEmpty()) {
            throw new BusinessException("商品标题不能为空");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("价格必须大于等于0");
        }
        product.setStatus("ON_SALE");
        return productDao.add(product);
    }

    @Override
    public boolean modify(Product product, int currentUserId) {
        Product old = productDao.findById(product.getId());
        if (old == null) {
            throw new BusinessException("商品不存在，ID=" + product.getId());
        }
        if (old.getSellerId() != currentUserId) {
            throw new BusinessException("只能修改自己的商品");
        }
        if ("SOLD".equals(old.getStatus())) {
            throw new BusinessException("商品已售出，无法修改");
        }
        product.setSellerId(old.getSellerId());
        return productDao.update(product);
    }

    @Override
    public boolean onShelf(int productId, int sellerId) {
        Product product = productDao.findById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在，ID=" + productId);
        }
        if (product.getSellerId() != sellerId) {
            throw new BusinessException("只能操作自己的商品");
        }
        if ("SOLD".equals(product.getStatus())) {
            throw new BusinessException("商品已售出，无法上架");
        }
        return productDao.updateStatus(productId, "ON_SALE");
    }

    @Override
    public boolean offShelf(int productId, int sellerId) {
        Product product = productDao.findById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在，ID=" + productId);
        }
        if (product.getSellerId() != sellerId) {
            throw new BusinessException("只能操作自己的商品");
        }
        if ("SOLD".equals(product.getStatus())) {
            throw new BusinessException("商品已售出，无法下架");
        }
        return productDao.updateStatus(productId, "OFF_SALE");
    }

    @Override
    public Product getProductById(int productId) {
        return productDao.findById(productId);
    }

    @Override
    public List<Product> searchProducts(String keyword, String category, BigDecimal minPrice, BigDecimal maxPrice,
                                        String status, String sortBy, int excludeUserId) {
        return productDao.search(keyword, category, minPrice, maxPrice, status, sortBy, excludeUserId);
    }

    @Override
    public List<Product> searchProductsByPage(String keyword, String category, BigDecimal minPrice, BigDecimal maxPrice,
                                              String status, String sortBy, int pageNum, int pageSize, int excludeUserId) {
        return productDao.searchByPage(keyword, category, minPrice, maxPrice, status, sortBy, pageNum, pageSize, excludeUserId);
    }

    @Override
    public int getProductCount(String keyword, String category, BigDecimal minPrice, BigDecimal maxPrice,
                               String status, int excludeUserId) {
        return productDao.count(keyword, category, minPrice, maxPrice, status, excludeUserId);
    }

    @Override
    public List<Product> getProductsBySeller(int sellerId) {
        return productDao.findBySellerId(sellerId);
    }

    @Override
    public List<Product> getProductsBySellerWithPage(int sellerId, int pageNum, int pageSize) {
        return productDao.findBySellerIdWithPage(sellerId, pageNum, pageSize);
    }

    @Override
    public int getProductCountBySeller(int sellerId) {
        return productDao.countBySellerId(sellerId);
    }
}