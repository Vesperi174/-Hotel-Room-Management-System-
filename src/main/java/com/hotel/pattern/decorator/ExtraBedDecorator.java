package com.hotel.pattern.decorator;

import java.math.BigDecimal;

public class ExtraBedDecorator extends ServiceDecorator {

    public ExtraBedDecorator(RoomServiceBill decoratedBill) {
        super(decoratedBill);
    }

    @Override
    public BigDecimal getCost() {
        return decoratedBill.getCost().add(BigDecimal.valueOf(100));
    }

    @Override
    public String getDescription() {
        return decoratedBill.getDescription() + " + 加床服务";
    }
}