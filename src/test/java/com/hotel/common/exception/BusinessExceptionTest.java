package com.hotel.common.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BusinessException 异常类测试")
class BusinessExceptionTest {

    @Test
    @DisplayName("基本消息构造")
    void testConstructorWithMessage() {
        BusinessException ex = new BusinessException("房间不存在");
        assertEquals("房间不存在", ex.getMessage());
        assertNull(ex.getErrorCode());
    }

    @Test
    @DisplayName("带错误码构造")
    void testConstructorWithErrorCode() {
        BusinessException ex = new BusinessException("ROOM_001", "房间不存在");
        assertEquals("房间不存在", ex.getMessage());
        assertEquals("ROOM_001", ex.getErrorCode());
    }

    @Test
    @DisplayName("带原因异常构造")
    void testConstructorWithCause() {
        RuntimeException cause = new RuntimeException("数据库连接失败");
        BusinessException ex = new BusinessException("操作失败", cause);
        assertEquals("操作失败", ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    @DisplayName("AccessDeniedException 基本构造")
    void testAccessDeniedException() {
        AccessDeniedException ex = new AccessDeniedException("权限不足");
        assertEquals("权限不足", ex.getMessage());
        assertTrue(ex instanceof RuntimeException);
    }
}