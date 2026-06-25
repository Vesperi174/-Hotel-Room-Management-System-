package com.hotel.dao;

import com.hotel.model.entity.Room;
import com.hotel.model.vo.RoomStatusVO;
import java.util.List;

public interface RoomDao {
    Room findById(Integer roomId);
    List<Room> findAll();
    List<Room> findByStatus(String status);
    List<Room> findByTypeId(Integer typeId);
    List<RoomStatusVO> findAllRoomStatus();
    int insert(Room room);
    int update(Room room);
    int updateStatus(Integer roomId, String status);
    int delete(Integer roomId);
}