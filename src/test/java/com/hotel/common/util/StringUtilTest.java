package com.hotel.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StringUtil 工具类测试")
class StringUtilTest {

    @Test
    @DisplayName("isEmpty - null 返回 true")
    void testIsEmptyNull() {
        assertTrue(StringUtil.isEmpty(null));
    }

    @Test
    @DisplayName("isEmpty - 空串返回 true")
    void testIsEmptyBlank() {
        assertTrue(StringUtil.isEmpty(""));
    }

    @Test
    @DisplayName("isEmpty - 纯空格返回 true")
    void testIsEmptyWhitespace() {
        assertTrue(StringUtil.isEmpty("   "));
    }

    @Test
    @DisplayName("isEmpty - 非空字符串返回 false")
    void testIsEmptyNonEmpty() {
        assertFalse(StringUtil.isEmpty("hello"));
    }

    @Test
    @DisplayName("isNotEmpty - 与 isEmpty 逻辑相反")
    void testIsNotEmpty() {
        assertFalse(StringUtil.isNotEmpty(null));
        assertFalse(StringUtil.isNotEmpty(""));
        assertTrue(StringUtil.isNotEmpty("hello"));
    }

    @Test
    @DisplayName("isNumeric - 纯数字返回 true")
    void testIsNumericTrue() {
        assertTrue(StringUtil.isNumeric("123456"));
    }

    @Test
    @DisplayName("isNumeric - 含字母返回 false")
    void testIsNumericWithLetters() {
        assertFalse(StringUtil.isNumeric("123abc"));
    }

    @Test
    @DisplayName("isNumeric - 空串返回 false")
    void testIsNumericEmpty() {
        assertFalse(StringUtil.isNumeric(""));
    }

    @Test
    @DisplayName("isNumeric - null 返回 false")
    void testIsNumericNull() {
        assertFalse(StringUtil.isNumeric(null));
    }

    @Test
    @DisplayName("maskIdNumber - 18 位身份证脱敏")
    void testMaskIdNumber() {
        assertEquals("1101****1234", StringUtil.maskIdNumber("110101199001011234"));
    }

    @Test
    @DisplayName("maskIdNumber - 短字符串原样返回")
    void testMaskIdNumberShort() {
        assertEquals("1234567", StringUtil.maskIdNumber("1234567"));
    }

    @Test
    @DisplayName("maskIdNumber - null 返回 null")
    void testMaskIdNumberNull() {
        assertNull(StringUtil.maskIdNumber(null));
    }
}