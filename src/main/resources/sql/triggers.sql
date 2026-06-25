DELIMITER //

CREATE TRIGGER trg_room_status_sync
AFTER INSERT ON booking
FOR EACH ROW
BEGIN
    UPDATE room SET room_status = '已预订'
    WHERE room_id = NEW.room_id AND room_status = '空闲';
END //

CREATE TRIGGER trg_booking_cancel_room
AFTER UPDATE ON booking
FOR EACH ROW
BEGIN
    IF NEW.booking_status = '已取消' AND OLD.booking_status != '已取消' THEN
        UPDATE room SET room_status = '空闲'
        WHERE room_id = NEW.room_id;
    END IF;
END //

CREATE TRIGGER trg_room_status_after_checkout
AFTER UPDATE ON checkin
FOR EACH ROW
BEGIN
    IF NEW.status = '已退房' AND OLD.status != '已退房' THEN
        UPDATE room SET room_status = '清洁中'
        WHERE room_id = NEW.room_id;
    END IF;
END //

CREATE TRIGGER trg_checkin_validate_room
BEFORE INSERT ON checkin
FOR EACH ROW
BEGIN
    DECLARE v_status VARCHAR(20);
    SELECT room_status INTO v_status FROM room WHERE room_id = NEW.room_id;

    IF v_status NOT IN ('空闲', '已预订') THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '该房间当前不可入住，请检查房间状态';
    END IF;
END //

CREATE TRIGGER trg_log_booking
AFTER INSERT ON booking
FOR EACH ROW
BEGIN
    INSERT INTO operation_log (user_id, log_type, log_content)
    VALUES (1, '预订', CONCAT('新增预订#', NEW.booking_id, '，客户:', NEW.customer_id, '，房间:', NEW.room_id));
END //

CREATE TRIGGER trg_log_checkin
AFTER INSERT ON checkin
FOR EACH ROW
BEGIN
    INSERT INTO operation_log (user_id, log_type, log_content)
    VALUES (NEW.user_id, '入住', CONCAT('办理入住#', NEW.checkin_id, '，客户:', NEW.customer_id, '，房间:', NEW.room_id));
END //

CREATE TRIGGER trg_log_checkout
AFTER UPDATE ON checkin
FOR EACH ROW
BEGIN
    IF NEW.status = '已退房' AND OLD.status = '已入住' THEN
        INSERT INTO operation_log (user_id, log_type, log_content)
        VALUES (NEW.user_id, '退房', CONCAT('办理退房#', NEW.checkin_id, '，总金额:', NEW.total_amount));
    END IF;
END //

DELIMITER ;