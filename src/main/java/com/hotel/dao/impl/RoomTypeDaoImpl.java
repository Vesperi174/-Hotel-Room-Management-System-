package com.hotel.dao.impl;

import com.hotel.dao.RoomTypeDao;
import com.hotel.dao.base.BaseDao;
import com.hotel.model.entity.RoomType;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RoomTypeDaoImpl extends BaseDao implements RoomTypeDao {

    public RoomTypeDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public RoomType findById(Integer typeId) {
        String sql = "SELECT * FROM room_type WHERE type_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, typeId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new com.hotel.common.exception.DataAccessException("查询房型失败", e);
        } finally {
            close(conn, stmt, rs);
        }
        return null;
    }

    @Override
    public List<RoomType> findAll() {
        String sql = "SELECT * FROM room_type";
        List<RoomType> list = new ArrayList<>();
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
            throw new com.hotel.common.exception.DataAccessException("查询房型列表失败", e);
        } finally {
            close(conn, stmt, rs);
        }
        return list;
    }

    @Override
    public int insert(RoomType roomType) {
        String sql = "INSERT INTO room_type (type_name, bed_type, area, base_price, capacity, description) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, roomType.getTypeName());
            stmt.setString(2, roomType.getBedType());
            stmt.setBigDecimal(3, roomType.getArea());
            stmt.setBigDecimal(4, roomType.getBasePrice());
            stmt.setInt(5, roomType.getCapacity());
            stmt.setString(6, roomType.getDescription());
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new com.hotel.common.exception.DataAccessException("新增房型失败", e);
        } finally {
            close(conn, stmt, null);
        }
    }

    @Override
    public int update(RoomType roomType) {
        String sql = "UPDATE room_type SET type_name=?, bed_type=?, area=?, base_price=?, capacity=?, description=? WHERE type_id=?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, roomType.getTypeName());
            stmt.setString(2, roomType.getBedType());
            stmt.setBigDecimal(3, roomType.getArea());
            stmt.setBigDecimal(4, roomType.getBasePrice());
            stmt.setInt(5, roomType.getCapacity());
            stmt.setString(6, roomType.getDescription());
            stmt.setInt(7, roomType.getTypeId());
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new com.hotel.common.exception.DataAccessException("更新房型失败", e);
        } finally {
            close(conn, stmt, null);
        }
    }

    @Override
    public int delete(Integer typeId) {
        String sql = "DELETE FROM room_type WHERE type_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, typeId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new com.hotel.common.exception.DataAccessException("删除房型失败", e);
        } finally {
            close(conn, stmt, null);
        }
    }

    private RoomType mapRow(ResultSet rs) throws SQLException {
        RoomType rt = new RoomType();
        rt.setTypeId(rs.getInt("type_id"));
        rt.setTypeName(rs.getString("type_name"));
        rt.setBedType(rs.getString("bed_type"));
        rt.setArea(rs.getBigDecimal("area"));
        rt.setBasePrice(rs.getBigDecimal("base_price"));
        rt.setCapacity(rs.getInt("capacity"));
        rt.setDescription(rs.getString("description"));
        return rt;
    }
}