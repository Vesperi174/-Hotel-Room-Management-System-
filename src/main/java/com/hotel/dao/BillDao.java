package com.hotel.dao;

import com.hotel.model.entity.Bill;
import com.hotel.model.vo.BillDetailVO;
import com.hotel.model.vo.RevenueReportVO;
import java.time.LocalDate;
import java.util.List;

public interface BillDao {
    Bill findById(Integer billId);
    Bill findByCheckinId(Integer checkinId);
    List<Bill> findAll();
    List<BillDetailVO> findAllBillDetail();
    List<RevenueReportVO> findRevenueReport(LocalDate startDate, LocalDate endDate);
    int insert(Bill bill);
    int update(Bill bill);
    int delete(Integer billId);
}