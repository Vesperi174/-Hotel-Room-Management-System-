package com.hotel.controller;

import com.hotel.common.exception.BusinessException;
import com.hotel.model.entity.Bill;
import com.hotel.model.vo.BillDetailVO;
import com.hotel.service.BillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class BillController {

    private static final Logger log = LoggerFactory.getLogger(BillController.class);

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    public Result<Bill> getBillById(Integer billId) {
        try {
            Bill bill = billService.findById(billId);
            return Result.success(bill);
        } catch (BusinessException e) {
            log.warn("查询账单失败: {}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    public Result<Bill> getBillByCheckinId(Integer checkinId) {
        try {
            Bill bill = billService.findByCheckinId(checkinId);
            if (bill == null) {
                return Result.fail("未找到该入住记录的账单");
            }
            return Result.success(bill);
        } catch (Exception e) {
            log.error("按入住记录查询账单失败", e);
            return Result.fail("查询账单失败");
        }
    }

    public Result<List<BillDetailVO>> getAllBillDetail() {
        try {
            List<BillDetailVO> bills = billService.findAllBillDetail();
            return Result.success(bills);
        } catch (Exception e) {
            log.error("查询所有账单失败", e);
            return Result.fail("查询账单列表失败");
        }
    }
}