package com.hotel.service;

import com.hotel.model.entity.Room;
import com.hotel.model.vo.RoomStatusVO;

import java.util.List;

public interface RoomService {

    Room findById(Integer roomId);

    List<Room> findAll();

    List<Room> findByStatus(String status);

    List<Room> findByTypeId(Integer typeId);

    List<RoomStatusVO> findAllRoomStatus();

    void addRoom(Room room);

    void updateRoom(Room room);

    void updateStatus(Integer roomId, String status);

    void deleteRoom(Integer roomId);
}