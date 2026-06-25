package com.hotel.dao.impl;

import com.hotel.dao.RoleDao;
import com.hotel.dao.base.BaseDao;
import com.hotel.model.entity.Role;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RoleDaoImpl extends BaseDao implements RoleDao {

    public RoleDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Role findById(Integer roleId) {
        String sql = "SELECT * FROM role WHERE role_id = ?";
        return queryOne(sql, roleId);
    }

    @Override
    public List<Role> findAll() {
        String sql = "SELECT * FROM role ORDER BY role_id";
        return queryList(sql);
    }

    @Override
    public int insert(Role role) {
        String sql = "INSERT INTO role (role_name, permissions, description) VALUES (?, ?, ?)";
        return executeUpdate(sql, role.getRoleName(), role.getPermissions(), role.getDescription());
    }

    @Override
    public int update(Role role) {
        String sql = "UPDATE role SET role_name=?, permissions=?, description=? WHERE role_id=?";
        return executeUpdate(sql, role.getRoleName(), role.getPermissions(),
                role.getDescription(), role.getRoleId());
    }

    @Override
    public int delete(Integer roleId) {
        String sql = "DELETE FROM role WHERE role_id = ?";
        return executeUpdate(sql, roleId);
    }

    private Role queryOne(String sql, Object... params) {
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
            throw new com.hotel.common.exception.DataAccessException("查询角色失败", e);
        } finally {
            close(conn, stmt, rs);
        }
        return null;
    }

    private List<Role> queryList(String sql, Object... params) {
        List<Role> list = new ArrayList<>();
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
            throw new com.hotel.common.exception.DataAccessException("查询角色列表失败", e);
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
            throw new com.hotel.common.exception.DataAccessException("角色数据操作失败", e);
        } finally {
            close(conn, stmt, null);
        }
    }

    private Role mapRow(ResultSet rs) throws SQLException {
        Role r = new Role();
        r.setRoleId(rs.getInt("role_id"));
        r.setRoleName(rs.getString("role_name"));
        r.setPermissions(rs.getString("permissions"));
        r.setDescription(rs.getString("description"));
        return r;
    }
}