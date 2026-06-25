package com.hotel.pattern.strategy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("房价策略模式测试")
class PricingStrategyTest {

    @Test
    @DisplayName("NormalPricingStrategy - 房价 × 晚数")
    void testNormalPricing() {
        NormalPricingStrategy strategy = new NormalPricingStrategy();
        BigDecimal result = strategy.calculate(new BigDecimal("200.00"), 3);
        assertEquals(0, new BigDecimal("600.00").compareTo(result));
    }

    @Test
    @DisplayName("NormalPricingStrategy - 1 晚")
    void testNormalPricingOneNight() {
        NormalPricingStrategy strategy = new NormalPricingStrategy();
        BigDecimal result = strategy.calculate(new BigDecimal("350.00"), 1);
        assertEquals(0, new BigDecimal("350.00").compareTo(result));
    }

    @Test
    @DisplayName("PeakSeasonPricingStrategy - 旺季溢价 30%")
    void testPeakSeasonPricing() {
        PeakSeasonPricingStrategy strategy = new PeakSeasonPricingStrategy();
        BigDecimal result = strategy.calculate(new BigDecimal("200.00"), 3);
        assertEquals(0, new BigDecimal("780.00").compareTo(result));
    }

    @Test
    @DisplayName("PeakSeasonPricingStrategy - 1 晚旺季")
    void testPeakSeasonPricingOneNight() {
        PeakSeasonPricingStrategy strategy = new PeakSeasonPricingStrategy();
        BigDecimal result = strategy.calculate(new BigDecimal("100.00"), 1);
        assertEquals(0, new BigDecimal("130.00").compareTo(result));
    }

    @Test
    @DisplayName("MemberDiscountStrategy - 会员折扣 85 折")
    void testMemberDiscount() {
        MemberDiscountStrategy strategy = new MemberDiscountStrategy(new BigDecimal("0.85"));
        BigDecimal result = strategy.calculate(new BigDecimal("200.00"), 3);
        assertEquals(0, new BigDecimal("510.00").compareTo(result));
    }

    @Test
    @DisplayName("MemberDiscountStrategy - 会员折扣 9 折")
    void testMemberDiscount90() {
        MemberDiscountStrategy strategy = new MemberDiscountStrategy(new BigDecimal("0.90"));
        BigDecimal result = strategy.calculate(new BigDecimal("100.00"), 2);
        assertEquals(0, new BigDecimal("180.00").compareTo(result));
    }

    @Test
    @DisplayName("PricingContext - 策略切换与执行")
    void testPricingContext() {
        PricingContext context = new PricingContext();

        context.setStrategy(new NormalPricingStrategy());
        BigDecimal normalResult = context.executeStrategy(new BigDecimal("200.00"), 2);
        assertEquals(0, new BigDecimal("400.00").compareTo(normalResult));

        context.setStrategy(new PeakSeasonPricingStrategy());
        BigDecimal peakResult = context.executeStrategy(new BigDecimal("200.00"), 2);
        assertEquals(0, new BigDecimal("520.00").compareTo(peakResult));
    }

    @Test
    @DisplayName("PricingContext - 未设置策略时抛异常")
    void testPricingContextNoStrategy() {
        PricingContext context = new PricingContext();
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> context.executeStrategy(new BigDecimal("200.00"), 2));
        assertEquals("未设置房价策略", exception.getMessage());
    }
}