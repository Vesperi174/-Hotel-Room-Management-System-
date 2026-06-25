package com.hotel.pattern.command;

import com.hotel.model.entity.User;
import com.hotel.model.dto.RoomStatusChangeEvent;
import com.hotel.pattern.observer.RoomStatusSubject;
import com.hotel.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CleanRoomCommand implements Command {

    private static final Logger log = LoggerFactory.getLogger(CleanRoomCommand.class);

    private final RoomService roomService;
    private final RoomStatusSubject subject;
    private final Integer roomId;
    private final User user;

    public CleanRoomCommand(RoomService roomService, RoomStatusSubject subject,
                            Integer roomId, User user) {
        this.roomService = roomService;
        this.subject = subject;
        this.roomId = roomId;
        this.user = user;
    }

    @Override
    public void execute() {
        roomService.updateStatus(roomId, "空闲");
        subject.notifyObservers(new RoomStatusChangeEvent(
                roomId, String.valueOf(roomId), "清洁中", "空闲",
                user != null ? user.getRealName() : "系统"));
        log.info("执行清洁完成命令: roomId={}, 房间状态: 清洁中 -> 空闲", roomId);
    }

    @Override
    public void undo() {
        roomService.updateStatus(roomId, "清洁中");
        subject.notifyObservers(new RoomStatusChangeEvent(
                roomId, String.valueOf(roomId), "空闲", "清洁中",
                user != null ? user.getRealName() : "系统"));
        log.info("撤销清洁完成命令: roomId={}, 恢复为清洁中", roomId);
    }

    @Override
    public String getDescription() {
        return "清洁完成: 房间ID " + roomId;
    }
}