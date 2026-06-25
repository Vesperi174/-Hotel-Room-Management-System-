package com.hotel.common.enums;

public enum CheckinStatus {
    CHECKED_IN("已入住"),
    CHECKED_OUT("已退房");

    private final String description;

    CheckinStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}