package com.hotel.controller;

import com.hotel.common.exception.BusinessException;
import com.hotel.model.dto.CheckinRequest;
import com.hotel.model.dto.CheckoutRequest;
import com.hotel.model.entity.Booking;
import com.hotel.model.entity.Checkin;
import com.hotel.model.vo.BillDetailVO;
import com.hotel.model.vo.CheckinDetailVO;
import com.hotel.service.HotelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class HotelController {

    private static final Logger log = LoggerFactory.getLogger(HotelController.class);

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    public Result<Booking> booking(Booking booking) {
        try {
            Booking result = hotelService.booking(booking);
            return Result.success(result, "预订成功");
        } catch (BusinessException e) {
            log.warn("预订失败: {}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    public Result<Void> cancelBooking(Integer bookingId) {
        try {
            hotelService.cancelBooking(bookingId);
            return Result.success(null, "取消预订成功");
        } catch (BusinessException e) {
            log.warn("取消预订失败: {}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    public Result<Checkin> checkin(CheckinRequest request) {
        try {
            Checkin checkin = hotelService.checkin(request);
            return Result.success(checkin, "入住登记成功");
        } catch (BusinessException e) {
            log.warn("入住登记失败: {}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    public Result<BillDetailVO> checkout(CheckoutRequest request) {
        try {
            BillDetailVO bill = hotelService.checkout(request);
            return Result.success(bill, "退房结算成功");
        } catch (BusinessException e) {
            log.warn("退房结算失败: {}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    public Result<List<CheckinDetailVO>> getAllCheckinDetail() {
        try {
            List<CheckinDetailVO> list = hotelService.findAllCheckinDetail();
            return Result.success(list);
        } catch (Exception e) {
            log.error("查询入住详情失败", e);
            return Result.fail("查询入住记录失败");
        }
    }

    public Result<List<CheckinDetailVO>> getCheckinByStatus(String status) {
        try {
            List<CheckinDetailVO> list = hotelService.findCheckinByStatus(status);
            return Result.success(list);
        } catch (Exception e) {
            log.error("按状态查询入住记录失败", e);
            return Result.fail("查询入住记录失败");
        }
    }
}