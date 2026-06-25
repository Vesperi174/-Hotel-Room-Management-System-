package com.hotel.dao.impl;

import com.hotel.dao.BookingDao;
import com.hotel.dao.base.BaseDao;
import com.hotel.model.entity.Booking;
import com.hotel.model.vo.CheckinDetailVO;
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
public class BookingDaoImpl extends BaseDao implements BookingDao {

    public BookingDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Booking findById(Integer bookingId) {
        String sql = "SELECT * FROM booking WHERE booking_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, bookingId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new com.hotel.common.exception.DataAccessException("查询预订失败", e);
        } finally {
            close(conn, stmt, rs);
        }
        return null;
    }

    @Override
    public List<Booking> findAll() {
        String sql = "SELECT * FROM booking ORDER BY booking_date DESC";
        return queryList(sql);
    }

    @Override
    public List<Booking> findByCustomerId(Integer customerId) {
        String sql = "SELECT * FROM booking WHERE customer_id = ? ORDER BY booking_date DESC";
        return queryList(sql, customerId);
    }

    @Override
    public List<Booking> findByStatus(String status) {
        String sql = "SELECT * FROM booking WHERE booking_status = ? ORDER BY booking_date DESC";
        return queryList(sql, status);
    }

    @Override
    public List<CheckinDetailVO> findAllBookingDetail() {
        String sql = "SELECT * FROM v_booking_detail";
        List<CheckinDetailVO> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                CheckinDetailVO vo = new CheckinDetailVO();
                vo.setCheckinId(rs.getInt("booking_id"));
                vo.setCustomerName(rs.getString("customer_name"));
                vo.setPhone(rs.getString("phone"));
                vo.setRoomNumber(rs.getString("room_number"));
                vo.setTypeName(rs.getString("type_name"));
                vo.setBasePrice(rs.getBigDecimal("base_price"));
                list.add(vo);
            }
        } catch (SQLException e) {
            throw new com.hotel.common.exception.DataAccessException("查询预订详情失败", e);
        } finally {
            close(conn, stmt, rs);
        }
        return list;
    }

    @Override
    public int insert(Booking booking) {
        String sql = "INSERT INTO booking (customer_id, room_id, booking_date, expected_arrival, expected_leave, booking_status, deposit_paid, remark) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, booking.getCustomerId());
            stmt.setInt(2, booking.getRoomId());
            stmt.setTimestamp(3, booking.getBookingDate() != null ? Timestamp.valueOf(booking.getBookingDate()) : null);
            stmt.setDate(4, booking.getExpectedArrival() != null ? java.sql.Date.valueOf(booking.getExpectedArrival()) : null);
            stmt.setDate(5, booking.getExpectedLeave() != null ? java.sql.Date.valueOf(booking.getExpectedLeave()) : null);
            stmt.setString(6, booking.getBookingStatus());
            stmt.setBigDecimal(7, booking.getDepositPaid());
            stmt.setString(8, booking.getRemark());
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new com.hotel.common.exception.DataAccessException("新增预订失败", e);
        } finally {
            close(conn, stmt, null);
        }
    }

    @Override
    public int update(Booking booking) {
        String sql = "UPDATE booking SET customer_id=?, room_id=?, expected_arrival=?, expected_leave=?, booking_status=?, deposit_paid=?, remark=? WHERE booking_id=?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, booking.getCustomerId());
            stmt.setInt(2, booking.getRoomId());
            stmt.setDate(3, booking.getExpectedArrival() != null ? java.sql.Date.valueOf(booking.getExpectedArrival()) : null);
            stmt.setDate(4, booking.getExpectedLeave() != null ? java.sql.Date.valueOf(booking.getExpectedLeave()) : null);
            stmt.setString(5, booking.getBookingStatus());
            stmt.setBigDecimal(6, booking.getDepositPaid());
            stmt.setString(7, booking.getRemark());
            stmt.setInt(8, booking.getBookingId());
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new com.hotel.common.exception.DataAccessException("更新预订失败", e);
        } finally {
            close(conn, stmt, null);
        }
    }

    @Override
    public int updateStatus(Integer bookingId, String status) {
        String sql = "UPDATE booking SET booking_status = ? WHERE booking_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, bookingId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new com.hotel.common.exception.DataAccessException("更新预订状态失败", e);
        } finally {
            close(conn, stmt, null);
        }
    }

    @Override
    public int delete(Integer bookingId) {
        String sql = "DELETE FROM booking WHERE booking_id = ?";
        return executeUpdate(sql, bookingId);
    }

    private List<Booking> queryList(String sql, Object... params) {
        List<Booking> list = new ArrayList<>();
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
            throw new com.hotel.common.exception.DataAccessException("查询预订失败", e);
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

    private Booking mapRow(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setBookingId(rs.getInt("booking_id"));
        b.setCustomerId(rs.getInt("customer_id"));
        b.setRoomId(rs.getInt("room_id"));
        Timestamp bd = rs.getTimestamp("booking_date");
        if (bd != null) b.setBookingDate(bd.toLocalDateTime());
        java.sql.Date ea = rs.getDate("expected_arrival");
        if (ea != null) b.setExpectedArrival(ea.toLocalDate());
        java.sql.Date el = rs.getDate("expected_leave");
        if (el != null) b.setExpectedLeave(el.toLocalDate());
        b.setBookingStatus(rs.getString("booking_status"));
        b.setDepositPaid(rs.getBigDecimal("deposit_paid"));
        b.setRemark(rs.getString("remark"));
        return b;
    }
}