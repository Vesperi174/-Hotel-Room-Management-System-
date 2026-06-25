package com.hotel.pattern.decorator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("装饰器模式测试")
class DecoratorPatternTest {

    @Test
    @DisplayName("BasicRoomCharge - 基础房费计算")
    void testBasicRoomCharge() {
        BasicRoomCharge basic = new BasicRoomCharge(new BigDecimal("200.00"), 3);
        assertEquals(0, new BigDecimal("600.00").compareTo(basic.getCost()));
        assertEquals("房费(3晚)", basic.getDescription());
    }

    @Test
    @DisplayName("BreakfastDecorator - 房费 + 早餐")
    void testBreakfastDecorator() {
        BasicRoomCharge basic = new BasicRoomCharge(new BigDecimal("200.00"), 2);
        BreakfastDecorator withBreakfast = new BreakfastDecorator(basic);
        assertEquals(0, new BigDecimal("450.00").compareTo(withBreakfast.getCost()));
        assertEquals("房费(2晚) + 早餐服务", withBreakfast.getDescription());
    }

    @Test
    @DisplayName("ExtraBedDecorator - 房费 + 加床")
    void testExtraBedDecorator() {
        BasicRoomCharge basic = new BasicRoomCharge(new BigDecimal("200.00"), 2);
        ExtraBedDecorator withExtraBed = new ExtraBedDecorator(basic);
        assertEquals(0, new BigDecimal("500.00").compareTo(withExtraBed.getCost()));
        assertEquals("房费(2晚) + 加床服务", withExtraBed.getDescription());
    }

    @Test
    @DisplayName("多层装饰叠加 - 房费 + 早餐 + 加床")
    void testMultipleDecorators() {
        BasicRoomCharge basic = new BasicRoomCharge(new BigDecimal("200.00"), 3);
        BreakfastDecorator withBreakfast = new BreakfastDecorator(basic);
        ExtraBedDecorator fullService = new ExtraBedDecorator(withBreakfast);
        assertEquals(0, new BigDecimal("750.00").compareTo(fullService.getCost()));
        assertEquals("房费(3晚) + 早餐服务 + 加床服务", fullService.getDescription());
    }

    @Test
    @DisplayName("多层装饰叠加 - 不同叠加顺序")
    void testMultipleDecoratorsReversed() {
        BasicRoomCharge basic = new BasicRoomCharge(new BigDecimal("200.00"), 3);
        ExtraBedDecorator withExtraBed = new ExtraBedDecorator(basic);
        BreakfastDecorator fullService = new BreakfastDecorator(withExtraBed);
        assertEquals(0, new BigDecimal("750.00").compareTo(fullService.getCost()));
        assertEquals("房费(3晚) + 加床服务 + 早餐服务", fullService.getDescription());
    }
}