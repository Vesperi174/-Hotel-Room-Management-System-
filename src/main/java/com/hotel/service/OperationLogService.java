package com.hotel.service;

import com.hotel.model.entity.OperationLog;

import java.util.List;

public interface OperationLogService {

    void log(Integer userId, String logType, String logContent);

    void log(Integer userId, String logType, String logContent, String ipAddress);

    List<OperationLog> findAll();

    List<OperationLog> findByUserId(Integer userId);

    List<OperationLog> findByType(String logType);

    List<OperationLog> findByDateRange(String startDate, String endDate);

    int deleteBefore(String date);
}