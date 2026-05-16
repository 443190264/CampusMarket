package com.campus.market.service;

import com.campus.market.entity.BrowseHistory;
import java.util.List;

public interface BrowseHistoryService {
    boolean recordBrowse(int studentId, int productId);
    List<BrowseHistory> getHistory(int studentId);
    boolean deleteHistoryItem(int historyId);
    boolean clearHistory(int studentId);
    List<BrowseHistory> getHistoryWithPage(int studentId, int pageNum, int pageSize);
    int getHistoryCount(int studentId);
}