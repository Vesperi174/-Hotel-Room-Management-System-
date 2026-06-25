package com.hotel.dao.impl;

import com.hotel.dao.CheckinDao;
import com.hotel.dao.base.BaseDao;
import com.hotel.model.entity.Checkin;
import com.hotel.model.vo.CheckinDetailVO;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CheckinDaoImpl extends BaseDao implements CheckinDao {

    public CheckinDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Checkin findById(Integer checkinId) {
        String sql = "SELECT * FROM checkin WHERE checkin_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, checkinId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new com.hotel.common.exception.DataAccessException("查询入住记录失败", e);
        } finally {
            close(conn, stmt, rs);
        }
        return null;
    }

    @Override
    public List<Checkin> findAll() {
        String sql = "SELECT * FROM checkin ORDER BY checkin_time DESC";
        return queryList(sql);
    }

    @Override
    public List<Checkin> findByStatus(String status) {
        String sql = "SELECT * FROM checkin WHERE status = ? ORDER BY checkin_time DESC";
        return queryList(sql, status);
    }

    @Override
    public List<Checkin> findByRoomId(Integer roomId) {
        String sql = "SELECT * FROM checkin WHERE room_id = ? ORDER BY checkin_time DESC";
        return queryList(sql, roomId);
    }

    @Override
    public List<Checkin> findByCustomerId(Integer customerId) {
        String sql = "SELECT * FROM checkin WHERE customer_id = ? ORDER BY checkin_time DESC";
        return queryList(sql, customerId);
    }

    @Override
    public List<CheckinDetailVO> findAllCheckinDetail() {
        String sql = "SELECT * FROM v_checkin_detail";
        List<CheckinDetailVO> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapDetailRow(rs));
            }
        } catch (SQLException e) {
            throw new com.hotel.common.exception.DataAccessException("查询入住详情失败", e);
        } finally {
            close(conn, stmt, rs);
        }
        return list;
    }

    @Override
    public int insert(Checkin checkin) {
        String sql = "INSERT INTO checkin (booking_id, customer_id, room_id, checkin_time, deposit, status, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            if (checkin.getBookingId() != null) {
                stmt.setInt(1, checkin.getBookingId());
            } else {
                stmt.setNull(1, java.sql.Types.INTEGER);
            }
            stmt.setInt(2, checkin.getCustomerId());
            stmt.setInt(3, checkin.getRoomId());
            stmt.setTimestamp(4, checkin.getCheckinTime() != null ? Timestamp.valueOf(checkin.getCheckinTime()) : null);
            stmt.setBigDecimal(5, checkin.getDeposit());
            stmt.setString(6, checkin.getStatus());
            stmt.setInt(7, checkin.getUserId());
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new com.hotel.common.exception.DataAccessException("新增入住记录失败", e);
        } finally {
            close(conn, stmt, null);
        }
    }

    @Override
    public int update(Checkin checkin) {
        String sql = "UPDATE checkin SET booking_id=?, customer_id=?, room_id=?, checkout_time=?, deposit=?, total_amount=?, status=?, user_id=? WHERE checkin_id=?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            if (checkin.getBookingId() != null) {
                stmt.setInt(1, checkin.getBookingId());
            } else {
                stmt.setNull(1, java.sql.Types.INTEGER);
            }
            stmt.setInt(2, checkin.getCustomerId());
            stmt.setInt(3, checkin.getRoomId());
            stmt.setTimestamp(4, checkin.getCheckoutTime() != null ? Timestamp.valueOf(checkin.getCheckoutTime()) : null);
            stmt.setBigDecimal(5, checkin.getDeposit());
            stmt.setBigDecimal(6, checkin.getTotalAmount());
            stmt.setString(7, checkin.getStatus());
            stmt.setInt(8, checkin.getUserId());
            stmt.setInt(9, checkin.getCheckinId());
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new com.hotel.common.exception.DataAccessException("更新入住记录失败", e);
        } finally {
            close(conn, stmt, null);
        }
    }

    @Override
    public int updateStatus(Integer checkinId, String status, BigDecimal totalAmount) {
        String sql = "UPDATE checkin SET status = ?, total_amount = ?, checkout_time = NOW() WHERE checkin_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setBigDecimal(2, totalAmount);
            stmt.setInt(3, checkinId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new com.hotel.common.exception.DataAccessException("更新入住状态失败", e);
        } finally {
            close(conn, stmt, null);
        }
    }

    @Override
    public int delete(Integer checkinId) {
        String sql = "DELETE FROM checkin WHERE checkin_id = ?";
        return executeUpdate(sql, checkinId);
    }

    private List<Checkin> queryList(String sql, Object... params) {
        List<Checkin> list = new ArrayList<>();
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
            throw new com.hotel.common.exception.DataAccessException("查询入住记录失败", e);
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
            throw new com.hotel.common.exception.DataAccessException("数据库操作失败", e);
        } finally {
            close(conn, stmt, null);
        }
    }

    private Checkin mapRow(ResultSet rs) throws SQLException {
        Checkin c = new Checkin();
        c.setCheckinId(rs.getInt("checkin_id"));
        c.setBookingId(rs.getObject("booking_id") != null ? rs.getInt("booking_id") : null);
        c.setCustomerId(rs.getInt("customer_id"));
        c.setRoomId(rs.getInt("room_id"));
        Timestamp ci = rs.getTimestamp("checkin_time");
        if (ci != null) c.setCheckinTime(ci.toLocalDateTime());
        Timestamp co = rs.getTimestamp("checkout_time");
        if (co != null) c.setCheckoutTime(co.toLocalDateTime());
        c.setDeposit(rs.getBigDecimal("deposit"));
        c.setTotalAmount(rs.getBigDecimal("total_amount"));
        c.setStatus(rs.getString("status"));
        c.setUserId(rs.getInt("user_id"));
        return c;
    }

    private CheckinDetailVO mapDetailRow(ResultSet rs) throws SQLException {
        CheckinDetailVO vo = new CheckinDetailVO();
        vo.setCheckinId(rs.getInt("checkin_id"));
        Timestamp ci = rs.getTimestamp("checkin_time");
        if (ci != null) vo.setCheckinTime(ci.toLocalDateTime());
        Timestamp co = rs.getTimestamp("checkout_time");
        if (co != null) vo.setCheckoutTime(co.toLocalDateTime());
        vo.setDeposit(rs.getBigDecimal("deposit"));
        vo.setTotalAmount(rs.getBigDecimal("total_amount"));
        vo.setStatus(rs.getString("status"));
        vo.setCustomerName(rs.getString("customer_name"));
        vo.setIdNumber(rs.getString("id_number"));
        vo.setPhone(rs.getString("phone"));
        vo.setRoomNumber(rs.getString("room_number"));
        vo.setTypeName(rs.getString("type_name"));
        vo.setBasePrice(rs.getBigDecimal("base_price"));
        vo.setOperatorName(rs.getString("operator_name"));
        return vo;
    }
}