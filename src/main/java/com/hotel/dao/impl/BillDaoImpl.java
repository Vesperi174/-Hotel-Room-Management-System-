package com.hotel.dao.impl;

import com.hotel.dao.BillDao;
import com.hotel.dao.base.BaseDao;
import com.hotel.model.entity.Bill;
import com.hotel.model.vo.BillDetailVO;
import com.hotel.model.vo.RevenueReportVO;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BillDaoImpl extends BaseDao implements BillDao {

    public BillDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Bill findById(Integer billId) {
        String sql = "SELECT * FROM bill WHERE bill_id = ?";
        return queryOne(sql, billId);
    }

    @Override
    public Bill findByCheckinId(Integer checkinId) {
        String sql = "SELECT * FROM bill WHERE checkin_id = ?";
        return queryOne(sql, checkinId);
    }

    @Override
    public List<Bill> findAll() {
        String sql = "SELECT * FROM bill ORDER BY bill_time DESC";
        return queryList(sql);
    }

    @Override
    public List<BillDetailVO> findAllBillDetail() {
        String sql = "SELECT * FROM v_bill_detail";
        List<BillDetailVO> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                BillDetailVO vo = new BillDetailVO();
                vo.setBillId(rs.getInt("bill_id"));
                vo.setRoomCharge(rs.getBigDecimal("room_charge"));
                vo.setExtraCharge(rs.getBigDecimal("extra_charge"));
                vo.setDepositPaid(rs.getBigDecimal("deposit_paid"));
                vo.setTotalAmount(rs.getBigDecimal("total_amount"));
                vo.setRefund(rs.getBigDecimal("refund"));
                vo.setPayMethod(rs.getString("pay_method"));
                Timestamp bt = rs.getTimestamp("bill_time");
                if (bt != null) vo.setBillTime(bt.toLocalDateTime());
                vo.setCustomerName(rs.getString("customer_name"));
                vo.setRoomNumber(rs.getString("room_number"));
                vo.setTypeName(rs.getString("type_name"));
                Timestamp ci = rs.getTimestamp("checkin_time");
                if (ci != null) vo.setCheckinTime(ci.toLocalDateTime());
                Timestamp co = rs.getTimestamp("checkout_time");
                if (co != null) vo.setCheckoutTime(co.toLocalDateTime());
                vo.setOperatorName(rs.getString("operator_name"));
                list.add(vo);
            }
        } catch (SQLException e) {
            throw new com.hotel.common.exception.DataAccessException("查询账单详情失败", e);
        } finally {
            close(conn, stmt, rs);
        }
        return list;
    }

    @Override
    public List<RevenueReportVO> findRevenueReport(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT * FROM v_revenue_report WHERE report_date BETWEEN ? AND ?";
        List<RevenueReportVO> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setDate(1, java.sql.Date.valueOf(startDate));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));
            rs = stmt.executeQuery();
            while (rs.next()) {
                RevenueReportVO vo = new RevenueReportVO();
                vo.setReportDate(rs.getString("report_date"));
                vo.setCheckoutCount(rs.getInt("checkout_count"));
                vo.setRoomChargeTotal(rs.getBigDecimal("room_charge_total"));
                vo.setExtraChargeTotal(rs.getBigDecimal("extra_charge_total"));
                vo.setTotalRevenue(rs.getBigDecimal("total_revenue"));
                vo.setAvgRevenuePerRoom(rs.getBigDecimal("avg_revenue_per_room"));
                list.add(vo);
            }
        } catch (SQLException e) {
            throw new com.hotel.common.exception.DataAccessException("查询营收报表失败", e);
        } finally {
            close(conn, stmt, rs);
        }
        return list;
    }

    @Override
    public int insert(Bill bill) {
        String sql = "INSERT INTO bill (checkin_id, room_charge, extra_charge, deposit_paid, total_amount, refund, pay_method, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        return executeUpdate(sql, bill.getCheckinId(), bill.getRoomCharge(),
                bill.getExtraCharge(), bill.getDepositPaid(), bill.getTotalAmount(),
                bill.getRefund(), bill.getPayMethod(), bill.getUserId());
    }

    @Override
    public int update(Bill bill) {
        String sql = "UPDATE bill SET room_charge=?, extra_charge=?, deposit_paid=?, total_amount=?, refund=?, pay_method=? WHERE bill_id=?";
        return executeUpdate(sql, bill.getRoomCharge(), bill.getExtraCharge(),
                bill.getDepositPaid(), bill.getTotalAmount(), bill.getRefund(),
                bill.getPayMethod(), bill.getBillId());
    }

    @Override
    public int delete(Integer billId) {
        String sql = "DELETE FROM bill WHERE bill_id = ?";
        return executeUpdate(sql, billId);
    }

    private Bill queryOne(String sql, Object... params) {
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
            throw new com.hotel.common.exception.DataAccessException("查询账单失败", e);
        } finally {
            close(conn, stmt, rs);
        }
        return null;
    }

    private List<Bill> queryList(String sql, Object... params) {
        List<Bill> list = new ArrayList<>();
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
            throw new com.hotel.common.exception.DataAccessException("查询账单列表失败", e);
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
            throw new com.hotel.common.exception.DataAccessException("账单数据操作失败", e);
        } finally {
            close(conn, stmt, null);
        }
    }

    private Bill mapRow(ResultSet rs) throws SQLException {
        Bill b = new Bill();
        b.setBillId(rs.getInt("bill_id"));
        b.setCheckinId(rs.getInt("checkin_id"));
        b.setRoomCharge(rs.getBigDecimal("room_charge"));
        b.setExtraCharge(rs.getBigDecimal("extra_charge"));
        b.setDepositPaid(rs.getBigDecimal("deposit_paid"));
        b.setTotalAmount(rs.getBigDecimal("total_amount"));
        b.setRefund(rs.getBigDecimal("refund"));
        b.setPayMethod(rs.getString("pay_method"));
        Timestamp bt = rs.getTimestamp("bill_time");
        if (bt != null) b.setBillTime(bt.toLocalDateTime());
        b.setUserId(rs.getInt("user_id"));
        return b;
    }
}