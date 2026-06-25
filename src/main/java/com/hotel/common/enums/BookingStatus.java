package com.hotel.common.enums;

public enum BookingStatus {
    BOOKED("已预订"),
    CHECKED_IN("已入住"),
    CANCELLED("已取消"),
    EXPIRED("已过期");

    private final String description;

    BookingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}