package com.campus.market.dao;

import java.util.List;
import com.campus.market.entity.OperationLog;

public interface LogDao {
    // 添加日志
    boolean add(OperationLog log);

    // 查询所有日志（不分页，管理员全量）
    List<OperationLog> findAll();

    // 查询指定学生的日志（不分页）
    List<OperationLog> findByOperatorId(int operatorId);

    //分页方法
    // 分页查询指定学生的日志
    List<OperationLog> findByOperatorIdWithPage(int operatorId, int pageNum, int pageSize);

    // 分页查询所有日志
    List<OperationLog> findAllWithPage(int pageNum, int pageSize);

    // 统计指定学生的日志总数
    int countByOperatorId(int operatorId);

    // 统计所有日志总数
    int countAll();
}