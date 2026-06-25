package com.hotel.service;

import com.hotel.model.dto.CheckinRequest;
import com.hotel.model.dto.CheckoutRequest;
import com.hotel.model.entity.Booking;
import com.hotel.model.entity.Checkin;
import com.hotel.model.vo.BillDetailVO;
import com.hotel.model.vo.CheckinDetailVO;

import java.util.List;

public interface HotelService {

    Booking booking(Booking booking);

    void cancelBooking(Integer bookingId);

    Checkin checkin(CheckinRequest request);

    BillDetailVO checkout(CheckoutRequest request);

    List<CheckinDetailVO> findAllCheckinDetail();

    List<CheckinDetailVO> findCheckinByStatus(String status);
}