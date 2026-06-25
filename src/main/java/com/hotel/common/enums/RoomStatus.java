package com.hotel.common.enums;

public enum RoomStatus {
    AVAILABLE("空闲"),
    BOOKED("已预订"),
    OCCUPIED("已入住"),
    CLEANING("清洁中"),
    MAINTENANCE("维修中");

    private final String description;

    RoomStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static boolean isValidTransition(String from, String to) {
        return switch (from) {
            case "空闲" -> "已预订".equals(to) || "已入住".equals(to) || "维修中".equals(to);
            case "已预订" -> "已入住".equals(to) || "已取消".equals(to) || "空闲".equals(to);
            case "已入住" -> "清洁中".equals(to) || "已退房".equals(to);
            case "清洁中" -> "空闲".equals(to);
            case "维修中" -> "空闲".equals(to);
            default -> false;
        };
    }

    public static boolean canCheckin(String status) {
        return "空闲".equals(status) || "已预订".equals(status);
    }
}