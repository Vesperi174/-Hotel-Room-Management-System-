package com.hotel.dao.impl;

import com.hotel.dao.UserDao;
import com.hotel.dao.base.BaseDao;
import com.hotel.model.entity.User;
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
public class UserDaoImpl extends BaseDao implements UserDao {

    public UserDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public User findById(Integer userId) {
        String sql = "SELECT * FROM user WHERE user_id = ?";
        return queryOne(sql, userId);
    }

    @Override
    public User findByUsername(String username) {
        String sql = "SELECT * FROM user WHERE username = ?";
        return queryOne(sql, username);
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM user ORDER BY create_time DESC";
        return queryList(sql);
    }

    @Override
    public int insert(User user) {
        String sql = "INSERT INTO user (username, password, real_name, role_id, phone, status) VALUES (?, ?, ?, ?, ?, ?)";
        return executeUpdate(sql, user.getUsername(), user.getPassword(),
                user.getRealName(), user.getRoleId(), user.getPhone(), user.getStatus());
    }

    @Override
    public int update(User user) {
        String sql = "UPDATE user SET real_name=?, role_id=?, phone=?, status=? WHERE user_id=?";
        return executeUpdate(sql, user.getRealName(), user.getRoleId(),
                user.getPhone(), user.getStatus(), user.getUserId());
    }

    @Override
    public int updatePassword(Integer userId, String newPassword) {
        String sql = "UPDATE user SET password = ? WHERE user_id = ?";
        return executeUpdate(sql, newPassword, userId);
    }

    @Override
    public int updateStatus(Integer userId, String status) {
        String sql = "UPDATE user SET status = ? WHERE user_id = ?";
        return executeUpdate(sql, status, userId);
    }

    @Override
    public int delete(Integer userId) {
        String sql = "DELETE FROM user WHERE user_id = ?";
        return executeUpdate(sql, userId);
    }

    private User queryOne(String sql, Object... params) {
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
            throw new com.hotel.common.exception.DataAccessException("查询用户失败", e);
        } finally {
            close(conn, stmt, rs);
        }
        return null;
    }

    private List<User> queryList(String sql, Object... params) {
        List<User> list = new ArrayList<>();
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
            throw new com.hotel.common.exception.DataAccessException("查询用户列表失败", e);
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
            throw new com.hotel.common.exception.DataAccessException("用户数据操作失败", e);
        } finally {
            close(conn, stmt, null);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setRealName(rs.getString("real_name"));
        u.setRoleId(rs.getInt("role_id"));
        u.setPhone(rs.getString("phone"));
        u.setStatus(rs.getString("status"));
        Timestamp ct = rs.getTimestamp("create_time");
        if (ct != null) u.setCreateTime(ct.toLocalDateTime());
        return u;
    }
}