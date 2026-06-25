package com.hotel.service;

import com.hotel.model.entity.RoomType;
import java.util.List;

public interface RoomTypeService {
    RoomType findById(Integer typeId);
    List<RoomType> findAll();
    void addRoomType(RoomType roomType);
    void updateRoomType(RoomType roomType);
    void deleteRoomType(Integer typeId);
}