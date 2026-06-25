package com.hotel.dao.base;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class BaseDao {

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
            // ignore
        }
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            // ignore
        }
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            // ignore
        }
    }
}