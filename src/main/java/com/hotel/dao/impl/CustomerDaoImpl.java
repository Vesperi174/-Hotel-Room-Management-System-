package com.hotel.dao.impl;

import com.hotel.dao.CustomerDao;
import com.hotel.dao.base.BaseDao;
import com.hotel.model.entity.Customer;
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
public class CustomerDaoImpl extends BaseDao implements CustomerDao {

    public CustomerDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Customer findById(Integer customerId) {
        String sql = "SELECT * FROM customer WHERE customer_id = ?";
        return queryOne(sql, customerId);
    }

    @Override
    public Customer findByIdNumber(String idNumber) {
        String sql = "SELECT * FROM customer WHERE id_number = ?";
        return queryOne(sql, idNumber);
    }

    @Override
    public List<Customer> findAll() {
        String sql = "SELECT * FROM customer ORDER BY create_time DESC";
        return queryList(sql);
    }

    @Override
    public List<Customer> findByName(String name) {
        String sql = "SELECT * FROM customer WHERE customer_name LIKE ? ORDER BY create_time DESC";
        return queryList(sql, "%" + name + "%");
    }

    @Override
    public int insert(Customer customer) {
        String sql = "INSERT INTO customer (customer_name, id_number, phone, gender, address) VALUES (?, ?, ?, ?, ?)";
        return executeUpdate(sql, customer.getCustomerName(), customer.getIdNumber(),
                customer.getPhone(), customer.getGender(), customer.getAddress());
    }

    @Override
    public int update(Customer customer) {
        String sql = "UPDATE customer SET customer_name=?, id_number=?, phone=?, gender=?, address=? WHERE customer_id=?";
        return executeUpdate(sql, customer.getCustomerName(), customer.getIdNumber(),
                customer.getPhone(), customer.getGender(), customer.getAddress(), customer.getCustomerId());
    }

    @Override
    public int delete(Integer customerId) {
        String sql = "DELETE FROM customer WHERE customer_id = ?";
        return executeUpdate(sql, customerId);
    }

    private Customer queryOne(String sql, Object... params) {
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
            throw new com.hotel.common.exception.DataAccessException("查询客户失败", e);
        } finally {
            close(conn, stmt, rs);
        }
        return null;
    }

    private List<Customer> queryList(String sql, Object... params) {
        List<Customer> list = new ArrayList<>();
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
            throw new com.hotel.common.exception.DataAccessException("查询客户列表失败", e);
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
            throw new com.hotel.common.exception.DataAccessException("客户数据操作失败", e);
        } finally {
            close(conn, stmt, null);
        }
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setCustomerId(rs.getInt("customer_id"));
        c.setCustomerName(rs.getString("customer_name"));
        c.setIdNumber(rs.getString("id_number"));
        c.setPhone(rs.getString("phone"));
        c.setGender(rs.getString("gender"));
        c.setAddress(rs.getString("address"));
        Timestamp ct = rs.getTimestamp("create_time");
        if (ct != null) c.setCreateTime(ct.toLocalDateTime());
        return c;
    }
}