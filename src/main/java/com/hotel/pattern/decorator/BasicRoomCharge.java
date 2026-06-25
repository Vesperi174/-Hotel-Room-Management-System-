package com.hotel.pattern.decorator;

import java.math.BigDecimal;

public class BasicRoomCharge implements RoomServiceBill {

    private final BigDecimal roomPrice;
    private final int nights;

    public BasicRoomCharge(BigDecimal roomPrice, int nights) {
        this.roomPrice = roomPrice;
        this.nights = nights;
    }

    @Override
    public BigDecimal getCost() {
        return roomPrice.multiply(BigDecimal.valueOf(nights));
    }

    @Override
    public String getDescription() {
        return "房费(" + nights + "晚)";
    }
}