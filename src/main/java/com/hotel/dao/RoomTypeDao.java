package com.hotel.dao;

import com.hotel.model.entity.RoomType;
import java.util.List;

public interface RoomTypeDao {
    RoomType findById(Integer typeId);
    List<RoomType> findAll();
    int insert(RoomType roomType);
    int update(RoomType roomType);
    int delete(Integer typeId);
}