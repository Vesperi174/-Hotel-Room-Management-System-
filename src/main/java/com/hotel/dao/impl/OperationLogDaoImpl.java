package com.hotel.dao.impl;

import com.hotel.dao.OperationLogDao;
import com.hotel.model.entity.OperationLog;
import com.hotel.pattern.template.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class OperationLogDaoImpl extends JdbcTemplate<OperationLog> implements OperationLogDao {

    private static final Logger log = LoggerFactory.getLogger(OperationLogDaoImpl.class);

    @Override
    protected OperationLog mapRow(ResultSet rs) throws SQLException {
        OperationLog log = new OperationLog();
        log.setLogId(rs.getInt("log_id"));
        log.setUserId(rs.getInt("user_id"));
        Timestamp ts = rs.getTimestamp("log_time");
        if (ts != null) {
            log.setLogTime(ts.toLocalDateTime());
        }
        log.setLogType(rs.getString("log_type"));
        log.setLogContent(rs.getString("log_content"));
        log.setIpAddress(rs.getString("ip_address"));
        return log;
    }

    @Override
    protected String getTableName() {
        return "operation_log";
    }

    @Override
    public int insert(OperationLog log) {
        String sql = "INSERT INTO operation_log (user_id, log_type, log_content, ip_address) VALUES (?, ?, ?, ?)";
        return update(sql, log.getUserId(), log.getLogType(), log.getLogContent(), log.getIpAddress());
    }

    @Override
    public List<OperationLog> findAll() {
        String sql = "SELECT * FROM operation_log ORDER BY log_time DESC";
        return query(sql);
    }

    @Override
    public List<OperationLog> findByUserId(Integer userId) {
        String sql = "SELECT * FROM operation_log WHERE user_id = ? ORDER BY log_time DESC";
        return query(sql, userId);
    }

    @Override
    public List<OperationLog> findByType(String logType) {
        String sql = "SELECT * FROM operation_log WHERE log_type = ? ORDER BY log_time DESC";
        return query(sql, logType);
    }

    @Override
    public List<OperationLog> findByDateRange(String startDate, String endDate) {
        String sql = "SELECT * FROM operation_log WHERE DATE(log_time) BETWEEN ? AND ? ORDER BY log_time DESC";
        return query(sql, startDate, endDate);
    }

    @Override
    public int deleteBefore(String date) {
        String sql = "DELETE FROM operation_log WHERE DATE(log_time) < ?";
        return update(sql, date);
    }
}