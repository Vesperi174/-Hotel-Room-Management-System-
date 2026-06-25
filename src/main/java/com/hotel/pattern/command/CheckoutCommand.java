package com.hotel.pattern.command;

import com.hotel.model.entity.User;
import com.hotel.model.dto.RoomStatusChangeEvent;
import com.hotel.pattern.observer.RoomStatusSubject;
import com.hotel.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckoutCommand implements Command {

    private static final Logger log = LoggerFactory.getLogger(CheckoutCommand.class);

    private final RoomService roomService;
    private final RoomStatusSubject subject;
    private final Integer roomId;
    private final User user;

    public CheckoutCommand(RoomService roomService, RoomStatusSubject subject,
                           Integer roomId, User user) {
        this.roomService = roomService;
        this.subject = subject;
        this.roomId = roomId;
        this.user = user;
    }

    @Override
    public void execute() {
        roomService.updateStatus(roomId, "清洁中");
        subject.notifyObservers(new RoomStatusChangeEvent(
                roomId, String.valueOf(roomId), "已入住", "清洁中",
                user != null ? user.getRealName() : "系统"));
        log.info("执行退房命令: roomId={}, 房间状态: 已入住 -> 清洁中", roomId);
    }

    @Override
    public void undo() {
        roomService.updateStatus(roomId, "已入住");
        subject.notifyObservers(new RoomStatusChangeEvent(
                roomId, String.valueOf(roomId), "清洁中", "已入住",
                user != null ? user.getRealName() : "系统"));
        log.info("撤销退房命令: roomId={}, 恢复为已入住", roomId);
    }

    @Override
    public String getDescription() {
        return "办理退房: 房间ID " + roomId;
    }
}