package com.hotel.controller;

import com.hotel.model.entity.OperationLog;
import com.hotel.model.entity.User;
import com.hotel.service.OperationLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OperationLogController {

    private static final Logger log = LoggerFactory.getLogger(OperationLogController.class);

    private final OperationLogService operationLogService;

    public OperationLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    public Result<List<OperationLog>> getAllLogs() {
        try {
            return Result.success(operationLogService.findAll());
        } catch (Exception e) {
            log.error("查询操作日志失败", e);
            return Result.fail("查询操作日志失败: " + e.getMessage());
        }
    }

    public Result<List<OperationLog>> getLogsByUser(Integer userId) {
        try {
            return Result.success(operationLogService.findByUserId(userId));
        } catch (Exception e) {
            log.error("查询用户操作日志失败", e);
            return Result.fail("查询用户操作日志失败: " + e.getMessage());
        }
    }

    public Result<List<OperationLog>> getLogsByType(String logType) {
        try {
            return Result.success(operationLogService.findByType(logType));
        } catch (Exception e) {
            log.error("按类型查询操作日志失败", e);
            return Result.fail("按类型查询操作日志失败: " + e.getMessage());
        }
    }

    public Result<List<OperationLog>> getLogsByDateRange(String startDate, String endDate) {
        try {
            return Result.success(operationLogService.findByDateRange(startDate, endDate));
        } catch (Exception e) {
            log.error("按日期查询操作日志失败", e);
            return Result.fail("按日期查询操作日志失败: " + e.getMessage());
        }
    }

    public void recordLog(User user, String logType, String logContent) {
        if (user != null) {
            operationLogService.log(user.getUserId(), logType, logContent);
        }
    }

    public int deleteBefore(String date) {
        try {
            return operationLogService.deleteBefore(date);
        } catch (Exception e) {
            log.error("清理操作日志失败", e);
            return 0;
        }
    }
}