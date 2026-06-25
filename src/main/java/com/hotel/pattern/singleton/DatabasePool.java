package com.hotel.pattern.singleton;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabasePool {

    private static volatile HikariDataSource dataSource;

    private DatabasePool() {}

    public static HikariDataSource getInstance() {
        if (dataSource == null) {
            synchronized (DatabasePool.class) {
                if (dataSource == null) {
                    HikariConfig config = new HikariConfig();
                    config.setJdbcUrl("jdbc:mysql://localhost:3306/hotel_db?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8mb4");
                    config.setUsername("root");
                    config.setPassword("your_password");
                    config.setMaximumPoolSize(20);
                    dataSource = new HikariDataSource(config);
                }
            }
        }
        return dataSource;
    }
}