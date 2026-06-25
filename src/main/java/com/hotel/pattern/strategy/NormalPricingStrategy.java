package com.hotel.pattern.strategy;

import java.math.BigDecimal;

public class NormalPricingStrategy implements PricingStrategy {

    @Override
    public BigDecimal calculate(BigDecimal basePrice, int nights) {
        return basePrice.multiply(BigDecimal.valueOf(nights));
    }
}