package com.hotel.dao.impl;

import com.hotel.dao.ConsumptionDao;
import com.hotel.dao.base.BaseDao;
import com.hotel.model.entity.Consumption;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ConsumptionDaoImpl extends BaseDao implements ConsumptionDao {

    public ConsumptionDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Consumption findById(Integer consId) {
        String sql = "SELECT * FROM consumption WHERE cons_id = ?";
        return queryOne(sql, consId);
    }

    @Override
    public List<Consumption> findByCheckinId(Integer checkinId) {
        String sql = "SELECT * FROM consumption WHERE checkin_id = ? ORDER BY cons_time";
        return queryList(sql, checkinId);
    }

    @Override
    public int insert(Consumption consumption) {
        String sql = "INSERT INTO consumption (checkin_id, item_name, item_price, quantity, remark) VALUES (?, ?, ?, ?, ?)";
        return executeUpdate(sql, consumption.getCheckinId(), consumption.getItemName(),
                consumption.getItemPrice(), consumption.getQuantity(), consumption.getRemark());
    }

    @Override
    public int update(Consumption consumption) {
        String sql = "UPDATE consumption SET item_name=?, item_price=?, quantity=?, remark=? WHERE cons_id=?";
        return executeUpdate(sql, consumption.getItemName(), consumption.getItemPrice(),
                consumption.getQuantity(), consumption.getRemark(), consumption.getConsId());
    }

    @Override
    public int delete(Integer consId) {
        String sql = "DELETE FROM consumption WHERE cons_id = ?";
        return executeUpdate(sql, consId);
    }

    private Consumption queryOne(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new com.hotel.common.exception.DataAccessException("查询消费记录失败", e);
        } finally {
            close(conn, stmt, rs);
        }
        return null;
    }

    private List<Consumption> queryList(String sql, Object... params) {
        List<Consumption> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new com.hotel.common.exception.DataAccessException("查询消费记录列表失败", e);
        } finally {
            close(conn, stmt, rs);
        }
        return list;
    }

    private int executeUpdate(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new com.hotel.common.exception.DataAccessException("消费数据操作失败", e);
        } finally {
            close(conn, stmt, null);
        }
    }

    private Consumption mapRow(ResultSet rs) throws SQLException {
        Consumption c = new Consumption();
        c.setConsId(rs.getInt("cons_id"));
        c.setCheckinId(rs.getInt("checkin_id"));
        c.setItemName(rs.getString("item_name"));
        c.setItemPrice(rs.getBigDecimal("item_price"));
        c.setQuantity(rs.getInt("quantity"));
        Timestamp ct = rs.getTimestamp("cons_time");
        if (ct != null) c.setConsTime(ct.toLocalDateTime());
        c.setRemark(rs.getString("remark"));
        return c;
    }
}