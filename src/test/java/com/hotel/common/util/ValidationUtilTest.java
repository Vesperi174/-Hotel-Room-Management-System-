package com.hotel.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ValidationUtil 校验工具类测试")
class ValidationUtilTest {

    @Test
    @DisplayName("isValidPhone - 合法手机号返回 true")
    void testValidPhone() {
        assertTrue(ValidationUtil.isValidPhone("13800138000"));
        assertTrue(ValidationUtil.isValidPhone("15912345678"));
        assertTrue(ValidationUtil.isValidPhone("18888888888"));
    }

    @Test
    @DisplayName("isValidPhone - 非 1 开头返回 false")
    void testInvalidPhoneStart() {
        assertFalse(ValidationUtil.isValidPhone("23800138000"));
    }

    @Test
    @DisplayName("isValidPhone - 位数不对返回 false")
    void testInvalidPhoneLength() {
        assertFalse(ValidationUtil.isValidPhone("1380013800"));
        assertFalse(ValidationUtil.isValidPhone("138001380000"));
    }

    @Test
    @DisplayName("isValidPhone - null 返回 false")
    void testInvalidPhoneNull() {
        assertFalse(ValidationUtil.isValidPhone(null));
    }

    @Test
    @DisplayName("isValidIdNumber - 合法 18 位身份证号返回 true")
    void testValidIdNumber() {
        assertTrue(ValidationUtil.isValidIdNumber("110101199001011234"));
        assertTrue(ValidationUtil.isValidIdNumber("11010119900101123X"));
    }

    @Test
    @DisplayName("isValidIdNumber - 位数不对返回 false")
    void testInvalidIdNumberLength() {
        assertFalse(ValidationUtil.isValidIdNumber("11010119900101123"));
        assertFalse(ValidationUtil.isValidIdNumber("1101011990010112345"));
    }

    @Test
    @DisplayName("isValidIdNumber - null 返回 false")
    void testInvalidIdNumberNull() {
        assertFalse(ValidationUtil.isValidIdNumber(null));
    }
}