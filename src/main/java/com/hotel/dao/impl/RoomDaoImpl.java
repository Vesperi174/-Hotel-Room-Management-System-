package com.hotel.dao.impl;

import com.hotel.dao.RoomDao;
import com.hotel.dao.base.BaseDao;
import com.hotel.model.entity.Room;
import com.hotel.model.vo.RoomStatusVO;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RoomDaoImpl extends BaseDao implements RoomDao {

    public RoomDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Room findById(Integer roomId) {
        String sql = "SELECT * FROM room WHERE room_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, roomId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new com.hotel.common.exception.DataAccessException("查询客房失败", e);
        } finally {
            close(conn, stmt, rs);
        }
        return null;
    }

    @Override
    public List<Room> findAll() {
        String sql = "SELECT * FROM room ORDER BY floor, room_number";
        List<Room> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new com.hotel.common.exception.DataAccessException("查询客房列表失败", e);
        } finally {
            close(conn, stmt, rs);
        }
        return list;
    }

    @Override
    public List<Room> findByStatus(String status) {
        String sql = "SELECT * FROM room WHERE room_status = ? ORDER BY floor, room_number";
        List<Room> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new com.hotel.common.exception.DataAccessException("查询客房状态失败", e);
        } finally {
            close(conn, stmt, rs);
        }
        return list;
    }

    @Override
    public List<Room> findByTypeId(Integer typeId) {
        String sql = "SELECT * FROM room WHERE type_id = ? ORDER BY floor, room_number";
        List<Room> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, typeId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new com.hotel.common.exception.DataAccessException("查询客房失败", e);
        } finally {
            close(conn, stmt, rs);
        }
        return list;
    }

    @Override
    public List<RoomStatusVO> findAllRoomStatus() {
        String sql = "SELECT * FROM v_room_status";
        List<RoomStatusVO> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                RoomStatusVO vo = new RoomStatusVO();
                vo.setRoomId(rs.getInt("room_id"));
                vo.setRoomNumber(rs.getString("room_number"));
                vo.setTypeName(rs.getString("type_name"));
                vo.setFloor(rs.getInt("floor"));
                vo.setRoomStatus(rs.getString("room_status"));
                vo.setBasePrice(rs.getBigDecimal("base_price"));
                vo.setArea(rs.getBigDecimal("area"));
                vo.setBedType(rs.getString("bed_type"));
                vo.setCapacity(rs.getInt("capacity"));
                list.add(vo);
            }
        } catch (SQLException e) {
            throw new com.hotel.common.exception.DataAccessException("查询客房状态视图失败", e);
        } finally {
            close(conn, stmt, rs);
        }
        return list;
    }

    @Override
    public int insert(Room room) {
        String sql = "INSERT INTO room (room_number, type_id, floor, room_status, description) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, room.getRoomNumber());
            stmt.setInt(2, room.getTypeId());
            stmt.setInt(3, room.getFloor());
            stmt.setString(4, room.getRoomStatus());
            stmt.setString(5, room.getDescription());
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new com.hotel.common.exception.DataAccessException("新增客房失败", e);
        } finally {
            close(conn, stmt, null);
        }
    }

    @Override
    public int update(Room room) {
        String sql = "UPDATE room SET room_number=?, type_id=?, floor=?, room_status=?, description=? WHERE room_id=?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, room.getRoomNumber());
            stmt.setInt(2, room.getTypeId());
            stmt.setInt(3, room.getFloor());
            stmt.setString(4, room.getRoomStatus());
            stmt.setString(5, room.getDescription());
            stmt.setInt(6, room.getRoomId());
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new com.hotel.common.exception.DataAccessException("更新客房失败", e);
        } finally {
            close(conn, stmt, null);
        }
    }

    @Override
    public int updateStatus(Integer roomId, String status) {
        String sql = "UPDATE room SET room_status = ? WHERE room_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, roomId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new com.hotel.common.exception.DataAccessException("更新客房状态失败", e);
        } finally {
            close(conn, stmt, null);
        }
    }

    @Override
    public int delete(Integer roomId) {
        String sql = "DELETE FROM room WHERE room_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, roomId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new com.hotel.common.exception.DataAccessException("删除客房失败", e);
        } finally {
            close(conn, stmt, null);
        }
    }

    private Room mapRow(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setRoomId(rs.getInt("room_id"));
        room.setRoomNumber(rs.getString("room_number"));
        room.setTypeId(rs.getInt("type_id"));
        room.setFloor(rs.getInt("floor"));
        room.setRoomStatus(rs.getString("room_status"));
        room.setDescription(rs.getString("description"));
        return room;
    }
}