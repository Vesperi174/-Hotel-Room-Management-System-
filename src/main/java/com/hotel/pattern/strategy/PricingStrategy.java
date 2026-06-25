package com.hotel.pattern.strategy;

import java.math.BigDecimal;

public interface PricingStrategy {
    BigDecimal calculate(BigDecimal basePrice, int nights);
}