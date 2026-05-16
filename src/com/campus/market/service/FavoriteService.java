package com.campus.market.service;

import com.campus.market.entity.Favorite;
import java.util.List;

public interface FavoriteService {
    boolean addFavorite(int studentId, int productId);
    boolean cancelFavorite(int studentId, int productId);
    boolean isFavorite(int studentId, int productId);
    List<Favorite> getMyFavorites(int studentId);
}