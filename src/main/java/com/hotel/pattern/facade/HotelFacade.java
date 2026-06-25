package com.hotel.pattern.facade;

import com.hotel.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HotelFacade {

    private static final Logger log = LoggerFactory.getLogger(HotelFacade.class);

    private final RoomService roomService;
    private final CustomerService customerService;
    private final HotelService hotelService;
    private final BillService billService;
    private final ReportService reportService;
    private final UserService userService;

    public HotelFacade(RoomService roomService, CustomerService customerService,
                       HotelService hotelService, BillService billService,
                       ReportService reportService, UserService userService) {
        this.roomService = roomService;
        this.customerService = customerService;
        this.hotelService = hotelService;
        this.billService = billService;
        this.reportService = reportService;
        this.userService = userService;
    }

    public RoomService getRoomService() { return roomService; }
    public CustomerService getCustomerService() { return customerService; }
    public HotelService getHotelService() { return hotelService; }
    public BillService getBillService() { return billService; }
    public ReportService getReportService() { return reportService; }
    public UserService getUserService() { return userService; }

    public void updateRoomStatus(Integer roomId, String status) {
        log.info("外观模式: 更新房间状态 roomId={}, status={}", roomId, status);
        roomService.updateStatus(roomId, status);
    }
}