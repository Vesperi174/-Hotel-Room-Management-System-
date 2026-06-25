package com.hotel.service.impl;

import com.hotel.dao.OperationLogDao;
import com.hotel.model.entity.OperationLog;
import com.hotel.service.OperationLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    private static final Logger log = LoggerFactory.getLogger(OperationLogServiceImpl.class);

    private final OperationLogDao operationLogDao;

    public OperationLogServiceImpl(OperationLogDao operationLogDao) {
        this.operationLogDao = operationLogDao;
    }

    @Override
    public void log(Integer userId, String logType, String logContent) {
        log(userId, logType, logContent, "127.0.0.1");
    }

    @Override
    public void log(Integer userId, String logType, String logContent, String ipAddress) {
        OperationLog opLog = new OperationLog();
        opLog.setUserId(userId);
        opLog.setLogType(logType);
        opLog.setLogContent(logContent);
        opLog.setIpAddress(ipAddress);
        operationLogDao.insert(opLog);
        log.info("操作日志记录: userId={}, type={}, content={}", userId, logType, logContent);
    }

    @Override
    public List<OperationLog> findAll() {
        return operationLogDao.findAll();
    }

    @Override
    public List<OperationLog> findByUserId(Integer userId) {
        return operationLogDao.findByUserId(userId);
    }

    @Override
    public List<OperationLog> findByType(String logType) {
        return operationLogDao.findByType(logType);
    }

    @Override
    public List<OperationLog> findByDateRange(String startDate, String endDate) {
        return operationLogDao.findByDateRange(startDate, endDate);
    }

    @Override
    public int deleteBefore(String date) {
        return operationLogDao.deleteBefore(date);
    }
}