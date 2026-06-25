package com.hotel.pattern.decorator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class BasicRoomCharge implements RoomServiceBill {

    private static final Logger log = LoggerFactory.getLogger(BasicRoomCharge.class);

    private final BigDecimal roomPrice;
    private final int nights;

    public BasicRoomCharge(BigDecimal roomPrice, int nights) {
        this.roomPrice = roomPrice;
        this.nights = nights;
        log.debug("创建基础房费: price={}, nights={}", roomPrice, nights);
    }

    @Override
    public BigDecimal getCost() {
        BigDecimal cost = roomPrice.multiply(BigDecimal.valueOf(nights));
        log.debug("计算基础房费: {}×{}={}", roomPrice, nights, cost);
        return cost;
    }

    @Override
    public String getDescription() {
        return "房费(" + nights + "晚)";
    }
}