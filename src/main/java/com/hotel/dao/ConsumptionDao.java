package com.hotel.dao;

import com.hotel.model.entity.Consumption;
import java.util.List;

public interface ConsumptionDao {
    Consumption findById(Integer consId);
    List<Consumption> findByCheckinId(Integer checkinId);
    int insert(Consumption consumption);
    int update(Consumption consumption);
    int delete(Integer consId);
}