package com.campus.market.dao;

import com.campus.market.entity.Favorite;
import java.util.List;

public interface FavoriteDao {
    // 添加收藏
    boolean addFavorite(int studentId, int productId);

    // 取消收藏
    boolean removeFavorite(int studentId, int productId);

    // 检查是否已收藏
    boolean isFavorite(int studentId, int productId);

    // 查询某个学生的所有收藏（包含商品详情，可以联查，但为简单先只查收藏表，连商品信息在 Service 层完成）
    List<Favorite> findByStudentId(int studentId);

    // 根据 id 删除（另一种删除方式）
    boolean deleteById(int id);
}