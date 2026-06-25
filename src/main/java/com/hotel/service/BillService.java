package com.hotel.service;

import com.hotel.model.entity.Bill;
import com.hotel.model.vo.BillDetailVO;

import java.util.List;

public interface BillService {

    Bill findById(Integer billId);

    Bill findByCheckinId(Integer checkinId);

    List<BillDetailVO> findAllBillDetail();
}