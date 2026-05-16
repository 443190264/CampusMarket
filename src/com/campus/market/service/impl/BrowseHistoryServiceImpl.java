package com.campus.market.service.impl;

import com.campus.market.dao.BrowseHistoryDao;
import com.campus.market.dao.impl.BrowseHistoryDaoImpl;
import com.campus.market.entity.BrowseHistory;
import com.campus.market.exception.BusinessException;
import com.campus.market.service.BrowseHistoryService;

import java.util.List;

public class BrowseHistoryServiceImpl implements BrowseHistoryService {
    private BrowseHistoryDao browseHistoryDao = new BrowseHistoryDaoImpl();

    @Override
    public boolean recordBrowse(int studentId, int productId) {
        if (studentId <= 0) {
            throw new BusinessException("学生ID无效：" + studentId);
        }
        if (productId <= 0) {
            throw new BusinessException("商品ID无效：" + productId);
        }
        return browseHistoryDao.recordBrowse(studentId, productId);
    }

    @Override
    public List<BrowseHistory> getHistory(int studentId) {
        if (studentId <= 0) {
            throw new BusinessException("学生ID无效：" + studentId);
        }
        return browseHistoryDao.findByStudentId(studentId);
    }

    @Override
    public boolean deleteHistoryItem(int historyId) {
        if (historyId <= 0) {
            throw new BusinessException("历史记录ID无效：" + historyId);
        }
        return browseHistoryDao.deleteById(historyId);
    }

    @Override
    public boolean clearHistory(int studentId) {
        if (studentId <= 0) {
            throw new BusinessException("学生ID无效：" + studentId);
        }
        return browseHistoryDao.clearByStudentId(studentId);
    }

    @Override
    public List<BrowseHistory> getHistoryWithPage(int studentId, int pageNum, int pageSize) {
        if (studentId <= 0) {
            throw new BusinessException("学生ID无效：" + studentId);
        }
        if (pageNum <= 0 || pageSize <= 0) {
            throw new BusinessException("页码或每页大小无效");
        }
        return browseHistoryDao.findByStudentIdWithPage(studentId, pageNum, pageSize);
    }

    @Override
    public int getHistoryCount(int studentId) {
        if (studentId <= 0) {
            throw new BusinessException("学生ID无效：" + studentId);
        }
        return browseHistoryDao.countByStudentId(studentId);
    }
}