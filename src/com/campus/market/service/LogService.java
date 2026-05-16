package com.campus.market.service;

import com.campus.market.entity.OperationLog;
import java.util.List;

public interface LogService {
    List<OperationLog> getAllLogs();
    List<OperationLog> getLogsByUser(int userId);

    // 分页方法
    List<OperationLog> getLogsByUserWithPage(int userId, int pageNum, int pageSize);
    List<OperationLog> getAllLogsWithPage(int pageNum, int pageSize);
    int getUserLogCount(int userId);
    int getTotalLogCount();
}