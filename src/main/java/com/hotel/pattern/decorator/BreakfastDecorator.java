package com.hotel.pattern.decorator;

import java.math.BigDecimal;

public class BreakfastDecorator extends ServiceDecorator {

    public BreakfastDecorator(RoomServiceBill decoratedBill) {
        super(decoratedBill);
    }

    @Override
    public BigDecimal getCost() {
        return decoratedBill.getCost().add(BigDecimal.valueOf(50));
    }

    @Override
    public String getDescription() {
        return decoratedBill.getDescription() + " + 早餐服务";
    }
}