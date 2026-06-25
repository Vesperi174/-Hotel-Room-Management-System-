package com.hotel.dao.impl;

import com.hotel.dao.RoomDao;
import com.hotel.dao.base.BaseDao;
import com.hotel.model.entity.Room;
import com.hotel.model.entity.RoomType;
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
        log.debug("查询客房: roomId={}", roomId);
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
                Room room = mapRow(rs);
                log.debug("查询到客房: roomNumber={}", room.getRoomNumber());
                return room;
            }
        } catch (SQLException e) {
            log.error("查询客房失败: roomId={}", roomId, e);
            throw new com.hotel.common.exception.DataAccessException("查询客房失败", e);
        } finally {
            close(conn, stmt, rs);
        }
        log.warn("客房不存在: roomId={}", roomId);
        return null;
    }

    @Override
    public List<Room> findAll() {
        String sql = "SELECT r.*, rt.type_name, rt.bed_type, rt.area, rt.base_price, rt.capacity, rt.description AS type_desc " +
                     "FROM room r LEFT JOIN room_type rt ON r.type_id = rt.type_id ORDER BY r.floor, r.room_number";
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
        log.info("新增客房: roomNumber={}, floor={}", room.getRoomNumber(), room.getFloor());
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
            int result = stmt.executeUpdate();
            log.info("新增客房成功: roomNumber={}", room.getRoomNumber());
            return result;
        } catch (SQLException e) {
            log.error("新增客房失败: roomNumber={}", room.getRoomNumber(), e);
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
        log.info("更新客房状态: roomId={}, status={}", roomId, status);
        String sql = "UPDATE room SET room_status = ? WHERE room_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, roomId);
            int result = stmt.executeUpdate();
            log.info("客房状态更新成功: roomId={}, status={}", roomId, status);
            return result;
        } catch (SQLException e) {
            log.error("更新客房状态失败: roomId={}", roomId, e);
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

        try {
            RoomType roomType = new RoomType();
            roomType.setTypeId(rs.getInt("type_id"));
            roomType.setTypeName(rs.getString("type_name"));
            roomType.setBedType(rs.getString("bed_type"));
            roomType.setArea(rs.getBigDecimal("area"));
            roomType.setBasePrice(rs.getBigDecimal("base_price"));
            roomType.setCapacity(rs.getInt("capacity"));
            roomType.setDescription(rs.getString("type_desc"));
            room.setRoomType(roomType);
        } catch (SQLException ignored) {
        }

        return room;
    }
}