package com.hotel.pattern.observer;

import com.hotel.model.dto.RoomStatusChangeEvent;

public interface RoomStatusObserver {
    void onRoomStatusChanged(RoomStatusChangeEvent event);
}