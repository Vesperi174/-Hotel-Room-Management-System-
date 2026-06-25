package com.hotel.model.entity;

public class Room {
    private Integer roomId;
    private String roomNumber;
    private Integer typeId;
    private Integer floor;
    private String roomStatus;
    private String description;

    private RoomType roomType;

    public Room() {}

    public Room(Integer roomId, String roomNumber, Integer typeId, Integer floor, String roomStatus) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.typeId = typeId;
        this.floor = floor;
        this.roomStatus = roomStatus;
    }

    public Integer getRoomId() { return roomId; }
    public void setRoomId(Integer roomId) { this.roomId = roomId; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public Integer getTypeId() { return typeId; }
    public void setTypeId(Integer typeId) { this.typeId = typeId; }
    public Integer getFloor() { return floor; }
    public void setFloor(Integer floor) { this.floor = floor; }
    public String getRoomStatus() { return roomStatus; }
    public void setRoomStatus(String roomStatus) { this.roomStatus = roomStatus; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public RoomType getRoomType() { return roomType; }
    public void setRoomType(RoomType roomType) { this.roomType = roomType; }

    @Override
    public String toString() {
        return "Room{roomId=" + roomId + ", roomNumber='" + roomNumber + "', status='" + roomStatus + "'}";
    }
}