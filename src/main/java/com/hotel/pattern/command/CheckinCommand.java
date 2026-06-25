package com.hotel.pattern.command;

import com.hotel.model.entity.User;
import com.hotel.model.dto.RoomStatusChangeEvent;
import com.hotel.pattern.observer.RoomStatusSubject;
import com.hotel.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckinCommand implements Command {

    private static final Logger log = LoggerFactory.getLogger(CheckinCommand.class);

    private final RoomService roomService;
    private final RoomStatusSubject subject;
    private final Integer roomId;
    private final String oldStatus;
    private final User user;

    public CheckinCommand(RoomService roomService, RoomStatusSubject subject,
                          Integer roomId, String oldStatus, User user) {
        this.roomService = roomService;
        this.subject = subject;
        this.roomId = roomId;
        this.oldStatus = oldStatus;
        this.user = user;
    }

    @Override
    public void execute() {
        roomService.updateStatus(roomId, "已入住");
        subject.notifyObservers(new RoomStatusChangeEvent(
                roomId, String.valueOf(roomId), oldStatus, "已入住",
                user != null ? user.getRealName() : "系统"));
        log.info("执行入住命令: roomId={}", roomId);
    }

    @Override
    public void undo() {
        roomService.updateStatus(roomId, oldStatus);
        subject.notifyObservers(new RoomStatusChangeEvent(
                roomId, String.valueOf(roomId), "已入住", oldStatus,
                user != null ? user.getRealName() : "系统"));
        log.info("撤销入住命令: roomId={}, 恢复为 {}", roomId, oldStatus);
    }

    @Override
    public String getDescription() {
        return "办理入住: 房间ID " + roomId;
    }
}