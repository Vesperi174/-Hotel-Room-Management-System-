package com.hotel.dao.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class BaseDao {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final DataSource dataSource;

    protected BaseDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    protected void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            log.warn("关闭 ResultSet 异常", e);
        }
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            log.warn("关闭 Statement 异常", e);
        }
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            log.warn("关闭 Connection 异常", e);
        }
    }
}