DELIMITER //

CREATE PROCEDURE sp_do_checkin(
    IN p_booking_id INT,
    IN p_customer_id INT,
    IN p_room_id INT,
    IN p_deposit DECIMAL(10,2),
    IN p_user_id INT,
    OUT p_result INT,
    OUT p_message VARCHAR(255)
)
BEGIN
    DECLARE v_room_status VARCHAR(20);
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result = 0;
        SET p_message = '入住办理失败，事务已回滚';
    END;

    START TRANSACTION;

    SELECT room_status INTO v_room_status FROM room WHERE room_id = p_room_id;
    IF v_room_status NOT IN ('空闲', '已预订') THEN
        SET p_result = 0;
        SET p_message = CONCAT('房间状态为"', v_room_status, '"，无法办理入住');
        ROLLBACK;
    ELSE
        INSERT INTO checkin (booking_id, customer_id, room_id, deposit, user_id, status)
        VALUES (p_booking_id, p_customer_id, p_room_id, p_deposit, p_user_id, '已入住');

        UPDATE room SET room_status = '已入住' WHERE room_id = p_room_id;

        IF p_booking_id IS NOT NULL THEN
            UPDATE booking SET booking_status = '已入住' WHERE booking_id = p_booking_id;
        END IF;

        COMMIT;
        SET p_result = 1;
        SET p_message = '入住办理成功';
    END IF;
END //

CREATE PROCEDURE sp_do_checkout(
    IN p_checkin_id INT,
    IN p_pay_method VARCHAR(20),
    IN p_user_id INT,
    OUT p_result INT,
    OUT p_message VARCHAR(255),
    OUT p_total_amount DECIMAL(10,2),
    OUT p_refund DECIMAL(10,2)
)
BEGIN
    DECLARE v_room_id INT;
    DECLARE v_checkin_time DATETIME;
    DECLARE v_deposit DECIMAL(10,2);
    DECLARE v_base_price DECIMAL(10,2);
    DECLARE v_nights INT;
    DECLARE v_room_charge DECIMAL(10,2);
    DECLARE v_extra_charge DECIMAL(10,2);
    DECLARE v_total DECIMAL(10,2);
    DECLARE v_refund DECIMAL(10,2);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result = 0;
        SET p_message = '退房结算失败，事务已回滚';
    END;

    START TRANSACTION;

    SELECT ci.room_id, ci.checkin_time, ci.deposit, rt.base_price
    INTO v_room_id, v_checkin_time, v_deposit, v_base_price
    FROM checkin ci
    JOIN room r ON ci.room_id = r.room_id
    JOIN room_type rt ON r.type_id = rt.type_id
    WHERE ci.checkin_id = p_checkin_id AND ci.status = '已入住';

    SET v_nights = GREATEST(DATEDIFF(NOW(), v_checkin_time), 1);
    SET v_room_charge = v_base_price * v_nights;

    SELECT COALESCE(SUM(item_price * quantity), 0)
    INTO v_extra_charge
    FROM consumption
    WHERE checkin_id = p_checkin_id;

    SET v_total = v_room_charge + v_extra_charge;
    SET v_refund = v_deposit - v_total;

    INSERT INTO bill (checkin_id, room_charge, extra_charge, deposit_paid,
                      total_amount, refund, pay_method, user_id)
    VALUES (p_checkin_id, v_room_charge, v_extra_charge, v_deposit,
            v_total, GREATEST(v_refund, 0), p_pay_method, p_user_id);

    UPDATE checkin
    SET checkout_time = NOW(), total_amount = v_total, status = '已退房'
    WHERE checkin_id = p_checkin_id;

    UPDATE room SET room_status = '清洁中' WHERE room_id = v_room_id;

    COMMIT;
    SET p_result = 1;
    SET p_message = '退房结算成功';
    SET p_total_amount = v_total;
    SET p_refund = GREATEST(v_refund, 0);
END //

CREATE PROCEDURE sp_stat_daily_revenue(
    IN p_start_date DATE,
    IN p_end_date DATE
)
BEGIN
    SELECT
        DATE(bill_time) AS report_date,
        SUM(room_charge) AS room_revenue,
        SUM(extra_charge) AS extra_revenue,
        SUM(total_amount) AS total_revenue,
        COUNT(*) AS checkout_count,
        ROUND(AVG(total_amount), 2) AS avg_revenue
    FROM bill
    WHERE DATE(bill_time) BETWEEN p_start_date AND p_end_date
    GROUP BY DATE(bill_time)
    ORDER BY report_date;
END //

CREATE PROCEDURE sp_stat_occupancy_rate(
    IN p_start_date DATE,
    IN p_end_date DATE
)
BEGIN
    SELECT
        (SELECT COUNT(*) FROM room) AS total_rooms,
        (SELECT COUNT(DISTINCT room_id) FROM checkin
         WHERE status = '已入住'
           AND DATE(checkin_time) <= p_end_date
           AND (checkout_time IS NULL OR DATE(checkout_time) >= p_start_date)
        ) AS occupied_rooms,
        ROUND(
            (SELECT COUNT(DISTINCT room_id) FROM checkin
             WHERE status = '已入住'
               AND DATE(checkin_time) <= p_end_date
               AND (checkout_time IS NULL OR DATE(checkout_time) >= p_start_date)
            ) * 100.0 / (SELECT COUNT(*) FROM room), 2
        ) AS occupancy_rate;
END //

CREATE PROCEDURE sp_stat_monthly_revenue(
    IN p_year INT
)
BEGIN
    SELECT
        MONTH(bill_time) AS month,
        SUM(total_amount) AS monthly_revenue,
        COUNT(DISTINCT ci.checkin_id) AS checkin_rooms,
        LAG(SUM(total_amount)) OVER (ORDER BY MONTH(bill_time)) AS prev_month_revenue,
        ROUND(
            (SUM(total_amount) - LAG(SUM(total_amount)) OVER (ORDER BY MONTH(bill_time)))
            / LAG(SUM(total_amount)) OVER (ORDER BY MONTH(bill_time)) * 100, 2
        ) AS growth_rate
    FROM bill b
    JOIN checkin ci ON b.checkin_id = ci.checkin_id
    WHERE YEAR(bill_time) = p_year
    GROUP BY MONTH(bill_time)
    ORDER BY month;
END //

DELIMITER ;