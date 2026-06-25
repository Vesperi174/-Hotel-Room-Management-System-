CREATE DATABASE IF NOT EXISTS hotel_db
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE hotel_db;

CREATE TABLE role (
    role_id     INT PRIMARY KEY AUTO_INCREMENT,
    role_name   VARCHAR(50) NOT NULL UNIQUE,
    permissions VARCHAR(500) NOT NULL,
    description VARCHAR(255)
) ENGINE=InnoDB;

CREATE TABLE user (
    user_id     INT PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(50) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    real_name   VARCHAR(50) NOT NULL,
    role_id     INT NOT NULL,
    phone       VARCHAR(20),
    status      VARCHAR(10) DEFAULT '正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES role(role_id)
) ENGINE=InnoDB;

CREATE TABLE room_type (
    type_id     INT PRIMARY KEY AUTO_INCREMENT,
    type_name   VARCHAR(50)  NOT NULL UNIQUE,
    bed_type    VARCHAR(20)  NOT NULL,
    area        DECIMAL(6,2),
    base_price  DECIMAL(10,2) NOT NULL CHECK (base_price > 0),
    capacity    INT DEFAULT 2,
    description VARCHAR(255)
) ENGINE=InnoDB;

CREATE TABLE room (
    room_id     INT PRIMARY KEY AUTO_INCREMENT,
    room_number VARCHAR(10)  NOT NULL UNIQUE,
    type_id     INT NOT NULL,
    floor       INT NOT NULL,
    room_status VARCHAR(20) NOT NULL DEFAULT '空闲',
    description VARCHAR(255),
    FOREIGN KEY (type_id) REFERENCES room_type(type_id)
) ENGINE=InnoDB;

CREATE TABLE customer (
    customer_id   INT PRIMARY KEY AUTO_INCREMENT,
    customer_name VARCHAR(50)  NOT NULL,
    id_number     VARCHAR(18)  NOT NULL UNIQUE,
    phone         VARCHAR(20)  NOT NULL,
    gender        VARCHAR(4),
    address       VARCHAR(255),
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE booking (
    booking_id       INT PRIMARY KEY AUTO_INCREMENT,
    customer_id      INT NOT NULL,
    room_id          INT NOT NULL,
    booking_date     DATETIME DEFAULT CURRENT_TIMESTAMP,
    expected_arrival DATE NOT NULL,
    expected_leave   DATE NOT NULL,
    booking_status   VARCHAR(20) DEFAULT '已预订',
    deposit_paid     DECIMAL(10,2) DEFAULT 0.00,
    remark           VARCHAR(255),
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
    FOREIGN KEY (room_id) REFERENCES room(room_id),
    CHECK (expected_leave >= expected_arrival)
) ENGINE=InnoDB;

CREATE TABLE checkin (
    checkin_id    INT PRIMARY KEY AUTO_INCREMENT,
    booking_id    INT,
    customer_id   INT NOT NULL,
    room_id       INT NOT NULL,
    checkin_time  DATETIME DEFAULT CURRENT_TIMESTAMP,
    checkout_time DATETIME,
    deposit       DECIMAL(10,2) DEFAULT 0.00,
    total_amount  DECIMAL(10,2),
    status        VARCHAR(20) DEFAULT '已入住',
    user_id       INT NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES booking(booking_id),
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
    FOREIGN KEY (room_id) REFERENCES room(room_id),
    FOREIGN KEY (user_id) REFERENCES user(user_id)
) ENGINE=InnoDB;

CREATE TABLE consumption (
    cons_id    INT PRIMARY KEY AUTO_INCREMENT,
    checkin_id INT NOT NULL,
    item_name  VARCHAR(100) NOT NULL,
    item_price DECIMAL(10,2) NOT NULL CHECK (item_price > 0),
    quantity   INT DEFAULT 1 CHECK (quantity > 0),
    cons_time  DATETIME DEFAULT CURRENT_TIMESTAMP,
    remark     VARCHAR(255),
    FOREIGN KEY (checkin_id) REFERENCES checkin(checkin_id)
) ENGINE=InnoDB;

CREATE TABLE bill (
    bill_id      INT PRIMARY KEY AUTO_INCREMENT,
    checkin_id   INT NOT NULL UNIQUE,
    room_charge  DECIMAL(10,2) DEFAULT 0.00,
    extra_charge DECIMAL(10,2) DEFAULT 0.00,
    deposit_paid DECIMAL(10,2) DEFAULT 0.00,
    total_amount DECIMAL(10,2) NOT NULL,
    refund       DECIMAL(10,2) DEFAULT 0.00,
    pay_method   VARCHAR(20) NOT NULL,
    bill_time    DATETIME DEFAULT CURRENT_TIMESTAMP,
    user_id      INT NOT NULL,
    FOREIGN KEY (checkin_id) REFERENCES checkin(checkin_id),
    FOREIGN KEY (user_id) REFERENCES user(user_id)
) ENGINE=InnoDB;

CREATE TABLE operation_log (
    log_id      INT PRIMARY KEY AUTO_INCREMENT,
    user_id     INT NOT NULL,
    log_time    DATETIME DEFAULT CURRENT_TIMESTAMP,
    log_type    VARCHAR(20) NOT NULL,
    log_content TEXT NOT NULL,
    ip_address  VARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES user(user_id)
) ENGINE=InnoDB;

CREATE INDEX idx_room_status ON room(room_status);
CREATE INDEX idx_room_type_id ON room(type_id);
CREATE INDEX idx_booking_status ON booking(booking_status);
CREATE INDEX idx_booking_dates ON booking(expected_arrival, expected_leave);
CREATE INDEX idx_checkin_status ON checkin(status);
CREATE INDEX idx_checkin_room ON checkin(room_id);
CREATE INDEX idx_checkin_customer ON checkin(customer_id);
CREATE INDEX idx_consumption_checkin ON consumption(checkin_id);
CREATE INDEX idx_bill_time ON bill(bill_time);
CREATE INDEX idx_log_time ON operation_log(log_time);