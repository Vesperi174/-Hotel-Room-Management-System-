package com.hotel.pattern.strategy;

import java.math.BigDecimal;

public class PricingContext {

    private PricingStrategy strategy;

    public void setStrategy(PricingStrategy strategy) {
        this.strategy = strategy;
    }

    public BigDecimal executeStrategy(BigDecimal basePrice, int nights) {
        if (strategy == null) {
            throw new IllegalStateException("未设置房价策略");
        }
        return strategy.calculate(basePrice, nights);
    }
}