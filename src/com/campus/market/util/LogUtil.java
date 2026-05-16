package com.campus.market.util;

import com.campus.market.dao.LogDao;
import com.campus.market.dao.impl.LogDaoImpl;
import com.campus.market.entity.OperationLog;

import java.time.LocalDateTime;

public class LogUtil {
    private static LogDao logDao = new LogDaoImpl();

    public static void record(Integer operatorId, String action, String detail) {
        OperationLog log = new OperationLog();
        log.setOperatorId(operatorId);
        log.setAction(action);
        log.setDetail(detail);
        log.setLogTime(LocalDateTime.now());
        logDao.add(log);
    }
}