package com.hotel.controller;

import com.hotel.common.exception.BusinessException;
import com.hotel.model.entity.RoomType;
import com.hotel.service.RoomTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class RoomTypeController {

    private static final Logger log = LoggerFactory.getLogger(RoomTypeController.class);

    private final RoomTypeService roomTypeService;

    public RoomTypeController(RoomTypeService roomTypeService) {
        this.roomTypeService = roomTypeService;
    }

    public Result<List<RoomType>> getAllRoomTypes() {
        try {
            List<RoomType> list = roomTypeService.findAll();
            return Result.success(list);
        } catch (Exception e) {
            log.error("查询房型列表失败", e);
            return Result.fail("查询房型失败");
        }
    }

    public Result<RoomType> getById(Integer typeId) {
        try {
            RoomType roomType = roomTypeService.findById(typeId);
            return Result.success(roomType);
        } catch (BusinessException e) {
            log.warn("查询房型失败: {}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }
}