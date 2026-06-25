package com.hotel.pattern.template;

import com.hotel.common.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class JdbcTemplate<T> {

    private static final Logger log = LoggerFactory.getLogger(JdbcTemplate.class);

    @Autowired
    private DataSource dataSource;

    protected abstract T mapRow(ResultSet rs) throws SQLException;

    protected abstract String getTableName();

    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public T queryForObject(String sql, Object... params) {
        List<T> results = query(sql, params);
        return results.isEmpty() ? null : results.get(0);
    }

    public List<T> query(String sql, Object... params) {
        List<T> results = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameters(stmt, params);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
            log.debug("查询执行成功, 结果数: {}", results.size());
        } catch (SQLException e) {
            log.error("查询执行失败: {}", e.getMessage());
            throw new DataAccessException("数据查询失败", e);
        }
        return results;
    }

    public int update(String sql, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(true);
            setParameters(stmt, params);
            int result = stmt.executeUpdate();
            log.debug("更新执行成功, 影响行数: {}", result);
            return result;
        } catch (SQLException e) {
            log.error("更新执行失败: {}", e.getMessage());
            throw new DataAccessException("数据更新失败", e);
        }
    }

    protected void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
        }
    }
}