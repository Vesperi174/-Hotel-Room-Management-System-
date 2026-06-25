package com.hotel.pattern.observer;

import com.hotel.model.dto.RoomStatusChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RoomStatusSubject {

    private static final Logger log = LoggerFactory.getLogger(RoomStatusSubject.class);

    private final List<RoomStatusObserver> observers = new ArrayList<>();

    public void attach(RoomStatusObserver observer) {
        observers.add(observer);
    }

    public void detach(RoomStatusObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(RoomStatusChangeEvent event) {
        log.info("房间状态变更通知: roomId={}, {} -> {}",
                event.getRoomId(), event.getOldStatus(), event.getNewStatus());
        for (RoomStatusObserver observer : observers) {
            observer.onRoomStatusChanged(event);
        }
    }
}