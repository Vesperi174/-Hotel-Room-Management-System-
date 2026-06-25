package com.hotel.model.dto;

public class RoomStatusChangeEvent {

    private final int roomId;
    private final String roomNumber;
    private final String oldStatus;
    private final String newStatus;
    private final String operatorName;

    public RoomStatusChangeEvent(int roomId, String roomNumber, String oldStatus,
                                 String newStatus, String operatorName) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.operatorName = operatorName;
    }

    public int getRoomId() { return roomId; }
    public String getRoomNumber() { return roomNumber; }
    public String getOldStatus() { return oldStatus; }
    public String getNewStatus() { return newStatus; }
    public String getOperatorName() { return operatorName; }
}