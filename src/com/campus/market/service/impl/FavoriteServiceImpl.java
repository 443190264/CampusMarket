package com.campus.market.service.impl;

import com.campus.market.dao.FavoriteDao;
import com.campus.market.dao.ProductDao;
import com.campus.market.dao.impl.FavoriteDaoImpl;
import com.campus.market.dao.impl.ProductDaoImpl;
import com.campus.market.entity.Favorite;
import com.campus.market.entity.Product;
import com.campus.market.exception.BusinessException;
import com.campus.market.service.FavoriteService;

import java.util.List;

public class FavoriteServiceImpl implements FavoriteService {
    private FavoriteDao favoriteDao = new FavoriteDaoImpl();
    private ProductDao productDao = new ProductDaoImpl();

    @Override
    public boolean addFavorite(int studentId, int productId) {
        if (studentId <= 0) {
            throw new BusinessException("学生ID无效：" + studentId);
        }
        if (productId <= 0) {
            throw new BusinessException("商品ID无效：" + productId);
        }
        Product product = productDao.findById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在，ID=" + productId);
        }
        return favoriteDao.addFavorite(studentId, productId);
    }

    @Override
    public boolean cancelFavorite(int studentId, int productId) {
        if (studentId <= 0) {
            throw new BusinessException("学生ID无效：" + studentId);
        }
        if (productId <= 0) {
            throw new BusinessException("商品ID无效：" + productId);
        }
        return favoriteDao.removeFavorite(studentId, productId);
    }

    @Override
    public boolean isFavorite(int studentId, int productId) {
        if (studentId <= 0 || productId <= 0) {
            return false;
        }
        return favoriteDao.isFavorite(studentId, productId);
    }

    @Override
    public List<Favorite> getMyFavorites(int studentId) {
        if (studentId <= 0) {
            throw new BusinessException("学生ID无效：" + studentId);
        }
        return favoriteDao.findByStudentId(studentId);
    }
}