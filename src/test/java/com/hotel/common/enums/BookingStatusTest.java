package com.hotel.common.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BookingStatus 枚举测试")
class BookingStatusTest {

    @Test
    @DisplayName("所有枚举值描述正确")
    void testDescriptions() {
        assertEquals("已预订", BookingStatus.BOOKED.getDescription());
        assertEquals("已入住", BookingStatus.CHECKED_IN.getDescription());
        assertEquals("已取消", BookingStatus.CANCELLED.getDescription());
        assertEquals("已过期", BookingStatus.EXPIRED.getDescription());
    }

    @Test
    @DisplayName("枚举值数量为 4")
    void testValuesCount() {
        assertEquals(4, BookingStatus.values().length);
    }
}