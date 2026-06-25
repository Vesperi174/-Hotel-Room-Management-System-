package com.hotel.controller;

import com.hotel.model.vo.RevenueReportVO;
import com.hotel.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Component
public class ReportController {

    private static final Logger log = LoggerFactory.getLogger(ReportController.class);

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    public Result<List<RevenueReportVO>> getDailyRevenue(LocalDate startDate, LocalDate endDate) {
        try {
            List<RevenueReportVO> report = reportService.getDailyRevenue(startDate, endDate);
            return Result.success(report);
        } catch (Exception e) {
            log.error("查询日营收报表失败", e);
            return Result.fail("查询营收报表失败");
        }
    }

    public Result<RevenueReportVO> getOccupancyRate(LocalDate startDate, LocalDate endDate) {
        try {
            RevenueReportVO report = reportService.getOccupancyRate(startDate, endDate);
            return Result.success(report);
        } catch (Exception e) {
            log.error("查询入住率统计失败", e);
            return Result.fail("查询入住率统计失败");
        }
    }
}