package com.hotel.service.impl;

import com.hotel.dao.BillDao;
import com.hotel.dao.RoomDao;
import com.hotel.model.vo.RevenueReportVO;
import com.hotel.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);

    private final BillDao billDao;
    private final RoomDao roomDao;

    public ReportServiceImpl(BillDao billDao, RoomDao roomDao) {
        this.billDao = billDao;
        this.roomDao = roomDao;
    }

    @Override
    public List<RevenueReportVO> getDailyRevenue(LocalDate startDate, LocalDate endDate) {
        log.info("查询日营收报表: {} ~ {}", startDate, endDate);
        return billDao.findRevenueReport(startDate, endDate);
    }

    @Override
    public RevenueReportVO getOccupancyRate(LocalDate startDate, LocalDate endDate) {
        log.info("查询入住率统计: {} ~ {}", startDate, endDate);
        int totalRooms = roomDao.findAll().size();
        List<RevenueReportVO> revenueList = billDao.findRevenueReport(startDate, endDate);

        RevenueReportVO vo = new RevenueReportVO();
        vo.setTotalRooms(totalRooms);

        if (revenueList.isEmpty()) {
            vo.setOccupiedRooms(0);
            vo.setOccupancyRate(BigDecimal.ZERO);
            vo.setTotalRevenue(BigDecimal.ZERO);
            vo.setAvgRevenuePerRoom(BigDecimal.ZERO);
            return vo;
        }

        int totalCheckoutCount = revenueList.stream()
                .mapToInt(r -> r.getCheckoutCount() != null ? r.getCheckoutCount() : 0)
                .sum();
        BigDecimal totalRevenue = revenueList.stream()
                .map(r -> r.getTotalRevenue() != null ? r.getTotalRevenue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal occupancyRate = totalRooms > 0
                ? BigDecimal.valueOf(totalCheckoutCount)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(totalRooms), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal avgRevenue = totalCheckoutCount > 0
                ? totalRevenue.divide(BigDecimal.valueOf(totalCheckoutCount), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        vo.setOccupiedRooms(totalCheckoutCount);
        vo.setOccupancyRate(occupancyRate);
        vo.setTotalRevenue(totalRevenue);
        vo.setAvgRevenuePerRoom(avgRevenue);

        return vo;
    }
}