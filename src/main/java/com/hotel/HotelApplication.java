package com.hotel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HotelApplication {

    private static final Logger log = LoggerFactory.getLogger(HotelApplication.class);

    public static void main(String[] args) {
        log.info("========== 酒店客房管理系统 启动中 ==========");
        SpringApplication.run(HotelApplication.class, args);
        log.info("========== 酒店客房管理系统 启动完成 ==========");
    }
}