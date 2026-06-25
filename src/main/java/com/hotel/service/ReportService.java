package com.hotel.service;

import com.hotel.model.vo.RevenueReportVO;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    List<RevenueReportVO> getDailyRevenue(LocalDate startDate, LocalDate endDate);

    RevenueReportVO getOccupancyRate(LocalDate startDate, LocalDate endDate);
}