package com.hotel.controller;

import com.hotel.common.exception.BusinessException;
import com.hotel.model.entity.Room;
import com.hotel.model.vo.RoomStatusVO;
import com.hotel.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class RoomController {

    private static final Logger log = LoggerFactory.getLogger(RoomController.class);

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    public Result<Room> getRoomById(Integer roomId) {
        try {
            Room room = roomService.findById(roomId);
            return Result.success(room);
        } catch (BusinessException e) {
            log.warn("查询客房失败: {}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    public Result<List<Room>> getAllRooms() {
        try {
            List<Room> rooms = roomService.findAll();
            return Result.success(rooms);
        } catch (Exception e) {
            log.error("查询所有客房失败", e);
            return Result.fail("查询客房列表失败");
        }
    }

    public Result<List<Room>> getRoomsByStatus(String status) {
        try {
            List<Room> rooms = roomService.findByStatus(status);
            return Result.success(rooms);
        } catch (Exception e) {
            log.error("按状态查询客房失败", e);
            return Result.fail("查询客房失败");
        }
    }

    public Result<List<Room>> getRoomsByType(Integer typeId) {
        try {
            List<Room> rooms = roomService.findByTypeId(typeId);
            return Result.success(rooms);
        } catch (Exception e) {
            log.error("按类型查询客房失败", e);
            return Result.fail("查询客房失败");
        }
    }

    public Result<List<RoomStatusVO>> getRoomStatusOverview() {
        try {
            List<RoomStatusVO> statusList = roomService.findAllRoomStatus();
            return Result.success(statusList);
        } catch (Exception e) {
            log.error("查询客房状态概览失败", e);
            return Result.fail("查询客房状态失败");
        }
    }

    public Result<Void> addRoom(Room room) {
        try {
            roomService.addRoom(room);
            return Result.success(null, "新增客房成功");
        } catch (BusinessException e) {
            log.warn("新增客房失败: {}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    public Result<Void> updateRoom(Room room) {
        try {
            roomService.updateRoom(room);
            return Result.success(null, "更新客房成功");
        } catch (BusinessException e) {
            log.warn("更新客房失败: {}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    public Result<Void> updateRoomStatus(Integer roomId, String status) {
        try {
            roomService.updateStatus(roomId, status);
            return Result.success(null, "客房状态已更新为" + status);
        } catch (BusinessException e) {
            log.warn("更新客房状态失败: {}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    public Result<Void> deleteRoom(Integer roomId) {
        try {
            roomService.deleteRoom(roomId);
            return Result.success(null, "删除客房成功");
        } catch (BusinessException e) {
            log.warn("删除客房失败: {}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }
}