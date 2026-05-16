package com.campus.market.service.impl;

import com.campus.market.dao.LogDao;
import com.campus.market.dao.impl.LogDaoImpl;
import com.campus.market.entity.OperationLog;
import com.campus.market.service.LogService;
import java.util.List;

public class LogServiceImpl implements LogService {
    private LogDao logDao = new LogDaoImpl();

    @Override
    public List<OperationLog> getAllLogs() {
        return logDao.findAll();
    }

    @Override
    public List<OperationLog> getLogsByUser(int userId) {
        return logDao.findByOperatorId(userId);
    }

    @Override
    public List<OperationLog> getLogsByUserWithPage(int userId, int pageNum, int pageSize) {
        return logDao.findByOperatorIdWithPage(userId, pageNum, pageSize);
    }

    @Override
    public List<OperationLog> getAllLogsWithPage(int pageNum, int pageSize) {
        return logDao.findAllWithPage(pageNum, pageSize);
    }

    @Override
    public int getUserLogCount(int userId) {
        return logDao.countByOperatorId(userId);
    }

    @Override
    public int getTotalLogCount() {
        return logDao.countAll();
    }
}