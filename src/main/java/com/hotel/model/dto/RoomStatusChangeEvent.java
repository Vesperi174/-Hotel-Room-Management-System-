package com.hotel.model.dto;

public class RoomStatusChangeEvent {

    private final int roomId;
    private final String newStatus;

    public RoomStatusChangeEvent(int roomId, String newStatus) {
        this.roomId = roomId;
        this.newStatus = newStatus;
    }

    public int getRoomId() { return roomId; }
    public String getNewStatus() { return newStatus; }
}