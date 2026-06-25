CREATE VIEW v_room_status AS
SELECT
    r.room_id,
    r.room_number,
    rt.type_name,
    r.floor,
    r.room_status,
    rt.base_price,
    rt.area,
    rt.bed_type,
    rt.capacity
FROM room r
JOIN room_type rt ON r.type_id = rt.type_id
ORDER BY r.floor, r.room_number;

CREATE VIEW v_checkin_detail AS
SELECT
    ci.checkin_id,
    ci.checkin_time,
    ci.checkout_time,
    ci.deposit,
    ci.total_amount,
    ci.status,
    c.customer_name,
    c.id_number,
    c.phone,
    r.room_number,
    rt.type_name,
    rt.base_price,
    u.real_name AS operator_name
FROM checkin ci
JOIN customer c ON ci.customer_id = c.customer_id
JOIN room r ON ci.room_id = r.room_id
JOIN room_type rt ON r.type_id = rt.type_id
JOIN user u ON ci.user_id = u.user_id
ORDER BY ci.checkin_time DESC;

CREATE VIEW v_booking_detail AS
SELECT
    b.booking_id,
    b.booking_date,
    b.expected_arrival,
    b.expected_leave,
    b.booking_status,
    b.deposit_paid,
    b.remark,
    c.customer_name,
    c.phone,
    r.room_number,
    rt.type_name,
    rt.base_price
FROM booking b
JOIN customer c ON b.customer_id = c.customer_id
JOIN room r ON b.room_id = r.room_id
JOIN room_type rt ON r.type_id = rt.type_id
ORDER BY b.booking_date DESC;

CREATE VIEW v_bill_detail AS
SELECT
    bl.bill_id,
    bl.room_charge,
    bl.extra_charge,
    bl.deposit_paid,
    bl.total_amount,
    bl.refund,
    bl.pay_method,
    bl.bill_time,
    c.customer_name,
    r.room_number,
    rt.type_name,
    ci.checkin_time,
    ci.checkout_time,
    u.real_name AS operator_name
FROM bill bl
JOIN checkin ci ON bl.checkin_id = ci.checkin_id
JOIN customer c ON ci.customer_id = c.customer_id
JOIN room r ON ci.room_id = r.room_id
JOIN room_type rt ON r.type_id = rt.type_id
JOIN user u ON bl.user_id = u.user_id
ORDER BY bl.bill_time DESC;

CREATE VIEW v_revenue_report AS
SELECT
    DATE(bill_time) AS report_date,
    COUNT(*) AS checkout_count,
    SUM(room_charge) AS room_charge_total,
    SUM(extra_charge) AS extra_charge_total,
    SUM(total_amount) AS total_revenue,
    ROUND(AVG(total_amount), 2) AS avg_revenue_per_room
FROM bill
GROUP BY DATE(bill_time)
ORDER BY report_date DESC;