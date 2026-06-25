package com.hotel.dao;

import com.hotel.model.entity.OperationLog;

import java.util.List;

public interface OperationLogDao {

    int insert(OperationLog log);

    List<OperationLog> findAll();

    List<OperationLog> findByUserId(Integer userId);

    List<OperationLog> findByType(String logType);

    List<OperationLog> findByDateRange(String startDate, String endDate);

    int deleteBefore(String date);
}