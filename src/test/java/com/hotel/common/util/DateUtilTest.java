package com.hotel.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DateUtil 工具类测试")
class DateUtilTest {

    @Test
    @DisplayName("formatDate - 正常日期格式化")
    void testFormatDate() {
        LocalDate date = LocalDate.of(2026, 6, 25);
        assertEquals("2026-06-25", DateUtil.formatDate(date));
    }

    @Test
    @DisplayName("formatDate - null 输入返回空串")
    void testFormatDateNull() {
        assertEquals("", DateUtil.formatDate(null));
    }

    @Test
    @DisplayName("formatDateTime - 正常日期时间格式化")
    void testFormatDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 6, 25, 14, 30, 0);
        assertEquals("2026-06-25 14:30:00", DateUtil.formatDateTime(dateTime));
    }

    @Test
    @DisplayName("formatDateTime - null 输入返回空串")
    void testFormatDateTimeNull() {
        assertEquals("", DateUtil.formatDateTime(null));
    }

    @Test
    @DisplayName("parseDate - 字符串解析为 LocalDate")
    void testParseDate() {
        LocalDate date = DateUtil.parseDate("2026-06-25");
        assertEquals(LocalDate.of(2026, 6, 25), date);
    }

    @Test
    @DisplayName("parseDateTime - 字符串解析为 LocalDateTime")
    void testParseDateTime() {
        LocalDateTime dateTime = DateUtil.parseDateTime("2026-06-25 14:30:00");
        assertEquals(LocalDateTime.of(2026, 6, 25, 14, 30, 0), dateTime);
    }

    @Test
    @DisplayName("daysBetween - 计算两个日期之间的天数")
    void testDaysBetween() {
        LocalDateTime start = LocalDateTime.of(2026, 6, 20, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 6, 25, 12, 0);
        assertEquals(5, DateUtil.daysBetween(start, end));
    }

    @Test
    @DisplayName("daysBetween - 同一天返回 0")
    void testDaysBetweenSameDay() {
        LocalDateTime start = LocalDateTime.of(2026, 6, 25, 8, 0);
        LocalDateTime end = LocalDateTime.of(2026, 6, 25, 20, 0);
        assertEquals(0, DateUtil.daysBetween(start, end));
    }

    @Test
    @DisplayName("nightsBetween - 最少返回 1 晚")
    void testNightsBetweenMinimum() {
        LocalDateTime checkin = LocalDateTime.of(2026, 6, 25, 14, 0);
        LocalDateTime checkout = LocalDateTime.of(2026, 6, 25, 18, 0);
        assertEquals(1, DateUtil.nightsBetween(checkin, checkout));
    }

    @Test
    @DisplayName("nightsBetween - 多晚计算")
    void testNightsBetween() {
        LocalDateTime checkin = LocalDateTime.of(2026, 6, 20, 14, 0);
        LocalDateTime checkout = LocalDateTime.of(2026, 6, 25, 12, 0);
        assertEquals(5, DateUtil.nightsBetween(checkin, checkout));
    }
}