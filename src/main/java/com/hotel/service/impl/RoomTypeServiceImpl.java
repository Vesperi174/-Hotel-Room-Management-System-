package com.hotel.service.impl;

import com.hotel.common.exception.BusinessException;
import com.hotel.dao.RoomTypeDao;
import com.hotel.model.entity.RoomType;
import com.hotel.service.RoomTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomTypeServiceImpl implements RoomTypeService {

    private static final Logger log = LoggerFactory.getLogger(RoomTypeServiceImpl.class);

    private final RoomTypeDao roomTypeDao;

    public RoomTypeServiceImpl(RoomTypeDao roomTypeDao) {
        this.roomTypeDao = roomTypeDao;
    }

    @Override
    public RoomType findById(Integer typeId) {
        RoomType roomType = roomTypeDao.findById(typeId);
        if (roomType == null) {
            throw new BusinessException("RT_001", "房型不存在: typeId=" + typeId);
        }
        return roomType;
    }

    @Override
    public List<RoomType> findAll() {
        return roomTypeDao.findAll();
    }

    @Override
    public void addRoomType(RoomType roomType) {
        roomTypeDao.insert(roomType);
        log.info("新增房型: {}", roomType.getTypeName());
    }

    @Override
    public void updateRoomType(RoomType roomType) {
        roomTypeDao.update(roomType);
        log.info("更新房型: {}", roomType.getTypeName());
    }

    @Override
    public void deleteRoomType(Integer typeId) {
        roomTypeDao.delete(typeId);
        log.info("删除房型: typeId={}", typeId);
    }
}