package com.hotel.common.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RoomStatus 枚举测试")
class RoomStatusTest {

    @Test
    @DisplayName("枚举值描述正确")
    void testDescriptions() {
        assertEquals("空闲", RoomStatus.AVAILABLE.getDescription());
        assertEquals("已预订", RoomStatus.BOOKED.getDescription());
        assertEquals("已入住", RoomStatus.OCCUPIED.getDescription());
        assertEquals("清洁中", RoomStatus.CLEANING.getDescription());
        assertEquals("维修中", RoomStatus.MAINTENANCE.getDescription());
    }

    @Test
    @DisplayName("枚举值数量为 5")
    void testValuesCount() {
        assertEquals(5, RoomStatus.values().length);
    }

    @Test
    @DisplayName("isValidTransition - 空闲可转换为已预订")
    void testValidTransitionAvailableToBooked() {
        assertTrue(RoomStatus.isValidTransition("空闲", "已预订"));
    }

    @Test
    @DisplayName("isValidTransition - 空闲可转换为已入住")
    void testValidTransitionAvailableToOccupied() {
        assertTrue(RoomStatus.isValidTransition("空闲", "已入住"));
    }

    @Test
    @DisplayName("isValidTransition - 空闲可转换为维修中")
    void testValidTransitionAvailableToMaintenance() {
        assertTrue(RoomStatus.isValidTransition("空闲", "维修中"));
    }

    @Test
    @DisplayName("isValidTransition - 已预订可转换为已入住")
    void testValidTransitionBookedToOccupied() {
        assertTrue(RoomStatus.isValidTransition("已预订", "已入住"));
    }

    @Test
    @DisplayName("isValidTransition - 已预订可转换为空闲(取消)")
    void testValidTransitionBookedToAvailable() {
        assertTrue(RoomStatus.isValidTransition("已预订", "空闲"));
    }

    @Test
    @DisplayName("isValidTransition - 已入住可转换为清洁中")
    void testValidTransitionOccupiedToCleaning() {
        assertTrue(RoomStatus.isValidTransition("已入住", "清洁中"));
    }

    @Test
    @DisplayName("isValidTransition - 清洁中可转换为空闲")
    void testValidTransitionCleaningToAvailable() {
        assertTrue(RoomStatus.isValidTransition("清洁中", "空闲"));
    }

    @Test
    @DisplayName("isValidTransition - 维修中可转换为空闲")
    void testValidTransitionMaintenanceToAvailable() {
        assertTrue(RoomStatus.isValidTransition("维修中", "空闲"));
    }

    @Test
    @DisplayName("isValidTransition - 非法转换返回 false")
    void testInvalidTransition() {
        assertFalse(RoomStatus.isValidTransition("清洁中", "已入住"));
        assertFalse(RoomStatus.isValidTransition("维修中", "已入住"));
        assertFalse(RoomStatus.isValidTransition("空闲", "清洁中"));
    }

    @Test
    @DisplayName("isValidTransition - 未知状态返回 false")
    void testInvalidTransitionUnknown() {
        assertFalse(RoomStatus.isValidTransition("不存在", "空闲"));
    }
}