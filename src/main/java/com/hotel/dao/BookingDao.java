package com.hotel.dao;

import com.hotel.model.entity.Booking;
import com.hotel.model.vo.CheckinDetailVO;
import java.util.List;

public interface BookingDao {
    Booking findById(Integer bookingId);
    List<Booking> findAll();
    List<Booking> findByCustomerId(Integer customerId);
    List<Booking> findByStatus(String status);
    List<CheckinDetailVO> findAllBookingDetail();
    int insert(Booking booking);
    int update(Booking booking);
    int updateStatus(Integer bookingId, String status);
    int delete(Integer bookingId);
}