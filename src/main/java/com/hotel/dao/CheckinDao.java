package com.hotel.dao;

import com.hotel.model.entity.Checkin;
import com.hotel.model.vo.CheckinDetailVO;
import java.util.List;

public interface CheckinDao {
    Checkin findById(Integer checkinId);
    List<Checkin> findAll();
    List<Checkin> findByStatus(String status);
    List<Checkin> findByRoomId(Integer roomId);
    List<Checkin> findByCustomerId(Integer customerId);
    List<CheckinDetailVO> findAllCheckinDetail();
    int insert(Checkin checkin);
    int update(Checkin checkin);
    int updateStatus(Integer checkinId, String status, java.math.BigDecimal totalAmount);
    int delete(Integer checkinId);
}