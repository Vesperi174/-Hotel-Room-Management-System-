package com.hotel.pattern.strategy;

import java.math.BigDecimal;

public class MemberDiscountStrategy implements PricingStrategy {

    private final BigDecimal discountRate;

    public MemberDiscountStrategy(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    @Override
    public BigDecimal calculate(BigDecimal basePrice, int nights) {
        return basePrice.multiply(discountRate)
                        .multiply(BigDecimal.valueOf(nights));
    }
}