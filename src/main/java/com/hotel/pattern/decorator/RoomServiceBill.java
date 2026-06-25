package com.hotel.pattern.decorator;

import java.math.BigDecimal;

public interface RoomServiceBill {
    BigDecimal getCost();
    String getDescription();
}