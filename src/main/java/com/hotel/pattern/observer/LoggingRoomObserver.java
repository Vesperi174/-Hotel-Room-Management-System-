package com.hotel.pattern.observer;

import com.hotel.model.dto.RoomStatusChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingRoomObserver implements RoomStatusObserver {

    private static final Logger log = LoggerFactory.getLogger(LoggingRoomObserver.class);

    @Override
    public void onRoomStatusChanged(RoomStatusChangeEvent event) {
        log.info("操作日志: 房间{}状态变更 {} -> {}, 操作员: {}",
                event.getRoomNumber(), event.getOldStatus(),
                event.getNewStatus(), event.getOperatorName());
    }
}