package com.hotel.pattern.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class PricingContext {

    private static final Logger log = LoggerFactory.getLogger(PricingContext.class);

    private PricingStrategy strategy;

    public void setStrategy(PricingStrategy strategy) {
        log.debug("设置房价策略: {}", strategy.getClass().getSimpleName());
        this.strategy = strategy;
    }

    public BigDecimal executeStrategy(BigDecimal basePrice, int nights) {
        if (strategy == null) {
            log.error("房价策略未设置");
            throw new IllegalStateException("未设置房价策略");
        }
        log.info("执行房价策略: {}, 基础价格={}, 晚数={}", strategy.getClass().getSimpleName(), basePrice, nights);
        BigDecimal result = strategy.calculate(basePrice, nights);
        log.info("房价计算结果: {}", result);
        return result;
    }
}