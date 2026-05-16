package com.campus.market.dao;

import com.campus.market.entity.BrowseHistory;
import java.util.List;

public interface BrowseHistoryDao {
    // 记录或更新浏览历史
    boolean recordBrowse(int studentId, int productId);

    // 查询某个学生的所有浏览历史（按时间倒序）
    List<BrowseHistory> findByStudentId(int studentId);

    // 删除单条历史记录
    boolean deleteById(int id);

    // 清空某个学生的所有浏览历史
    boolean clearByStudentId(int studentId);

    //分页查询某个学生的浏览历史（按时间倒序）
    List<BrowseHistory> findByStudentIdWithPage(int studentId, int pageNum, int pageSize);

    // 统计某个学生的浏览历史数量
    int countByStudentId(int studentId);
}