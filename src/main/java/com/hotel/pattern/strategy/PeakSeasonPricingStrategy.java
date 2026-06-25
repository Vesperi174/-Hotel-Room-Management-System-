package com.hotel.pattern.strategy;

import java.math.BigDecimal;

public class PeakSeasonPricingStrategy implements PricingStrategy {

    @Override
    public BigDecimal calculate(BigDecimal basePrice, int nights) {
        return basePrice.multiply(BigDecimal.valueOf(1.3))
                        .multiply(BigDecimal.valueOf(nights));
    }
}