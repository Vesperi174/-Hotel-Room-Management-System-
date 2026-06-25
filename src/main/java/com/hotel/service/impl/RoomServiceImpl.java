package com.hotel.service.impl;

import com.hotel.common.enums.RoomStatus;
import com.hotel.common.exception.BusinessException;
import com.hotel.dao.RoomDao;
import com.hotel.model.entity.Room;
import com.hotel.model.vo.RoomStatusVO;
import com.hotel.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {

    private static final Logger log = LoggerFactory.getLogger(RoomServiceImpl.class);

    private final RoomDao roomDao;

    public RoomServiceImpl(RoomDao roomDao) {
        this.roomDao = roomDao;
    }

    @Override
    public Room findById(Integer roomId) {
        log.debug("查询客房: roomId={}", roomId);
        Room room = roomDao.findById(roomId);
        if (room == null) {
            throw new BusinessException("ROOM_001", "客房不存在: roomId=" + roomId);
        }
        return room;
    }

    @Override
    public List<Room> findAll() {
        log.debug("查询所有客房");
        return roomDao.findAll();
    }

    @Override
    public List<Room> findByStatus(String status) {
        log.debug("按状态查询客房: status={}", status);
        return roomDao.findByStatus(status);
    }

    @Override
    public List<Room> findByTypeId(Integer typeId) {
        log.debug("按类型查询客房: typeId={}", typeId);
        return roomDao.findByTypeId(typeId);
    }

    @Override
    public List<RoomStatusVO> findAllRoomStatus() {
        log.debug("查询所有客房状态概览");
        return roomDao.findAllRoomStatus();
    }

    @Override
    public void addRoom(Room room) {
        validateRoom(room);
        log.info("新增客房: roomNumber={}", room.getRoomNumber());
        room.setRoomStatus(RoomStatus.AVAILABLE.getDescription());
        roomDao.insert(room);
    }

    @Override
    public void updateRoom(Room room) {
        Room existing = findById(room.getRoomId());
        log.info("更新客房: roomId={}", room.getRoomId());
        roomDao.update(room);
    }

    @Override
    public void updateStatus(Integer roomId, String status) {
        Room room = findById(roomId);
        if (!RoomStatus.isValidTransition(room.getRoomStatus(), status)) {
            throw new BusinessException("ROOM_002",
                    String.format("客房状态转换非法: %s -> %s", room.getRoomStatus(), status));
        }
        log.info("更新客房状态: roomId={}, {} -> {}", roomId, room.getRoomStatus(), status);
        roomDao.updateStatus(roomId, status);
    }

    @Override
    public void deleteRoom(Integer roomId) {
        Room room = findById(roomId);
        if (!RoomStatus.AVAILABLE.getDescription().equals(room.getRoomStatus())) {
            throw new BusinessException("ROOM_003", "只能删除空闲状态的客房");
        }
        log.info("删除客房: roomId={}, roomNumber={}", roomId, room.getRoomNumber());
        roomDao.delete(roomId);
    }

    private void validateRoom(Room room) {
        if (room.getRoomNumber() == null || room.getRoomNumber().isBlank()) {
            throw new BusinessException("ROOM_004", "客房编号不能为空");
        }
        if (room.getTypeId() == null) {
            throw new BusinessException("ROOM_005", "客房类型不能为空");
        }
        if (room.getFloor() == null || room.getFloor() <= 0) {
            throw new BusinessException("ROOM_006", "楼层信息非法");
        }
    }
}