package com.hotel.common.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CheckinStatus 枚举测试")
class CheckinStatusTest {

    @Test
    @DisplayName("已入住描述为'已入住'")
    void testCheckedInDescription() {
        assertEquals("已入住", CheckinStatus.CHECKED_IN.getDescription());
    }

    @Test
    @DisplayName("已退房描述为'已退房'")
    void testCheckedOutDescription() {
        assertEquals("已退房", CheckinStatus.CHECKED_OUT.getDescription());
    }

    @Test
    @DisplayName("枚举值数量为 2")
    void testValuesCount() {
        assertEquals(2, CheckinStatus.values().length);
    }
}