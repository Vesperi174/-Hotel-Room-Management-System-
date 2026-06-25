package com.hotel.view;

import com.hotel.controller.Result;
import com.hotel.controller.HotelController;
import com.hotel.model.dto.CheckinRequest;
import com.hotel.model.dto.CheckoutRequest;
import com.hotel.model.vo.BillDetailVO;
import com.hotel.model.vo.CheckinDetailVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

@Component
public class CheckinPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(CheckinPanel.class);

    private final HotelController hotelController;
    private JTable checkinTable;
    private DefaultTableModel tableModel;
    private JButton checkinButton;
    private JButton checkoutButton;
    private JComboBox<String> statusFilter;

    public CheckinPanel(HotelController hotelController) {
        this.hotelController = hotelController;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.add(new JLabel("状态筛选:"));
        statusFilter = new JComboBox<>(new String[]{"全部", "已入住", "已退房"});
        topPanel.add(statusFilter);

        checkinButton = new JButton("入住登记");
        checkoutButton = new JButton("退房结算");
        JButton refreshButton = new JButton("刷新");

        checkinButton.addActionListener(e -> showCheckinDialog());
        checkoutButton.addActionListener(e -> showCheckoutDialog());
        refreshButton.addActionListener(e -> refreshData());

        topPanel.add(checkinButton);
        topPanel.add(checkoutButton);
        topPanel.add(refreshButton);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"入住ID", "客户名", "房间号", "房型", "入住时间", "退房时间", "状态", "总金额"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        checkinTable = new JTable(tableModel);
        checkinTable.setRowHeight(25);
        checkinTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(checkinTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        Result<List<CheckinDetailVO>> result = hotelController.getAllCheckinDetail();
        if (result.isSuccess() && result.getData() != null) {
            String filter = (String) statusFilter.getSelectedItem();
            for (CheckinDetailVO vo : result.getData()) {
                if (!"全部".equals(filter) && !filter.equals(vo.getStatus())) {
                    continue;
                }
                tableModel.addRow(new Object[]{
                        vo.getCheckinId(), vo.getCustomerName(), vo.getRoomNumber(),
                        vo.getTypeName(), vo.getCheckinTime(), vo.getCheckoutTime(),
                        vo.getStatus(), vo.getTotalAmount()
                });
            }
        }
    }

    private void showCheckinDialog() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "入住登记", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        JTextField customerIdField = new JTextField(10);
        JTextField roomIdField = new JTextField(10);
        JTextField bookingIdField = new JTextField(10);
        JTextField depositField = new JTextField("0.00", 10);
        JTextField userIdField = new JTextField("1", 10);

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("客户ID:"), gbc);
        gbc.gridx = 1;
        dialog.add(customerIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("房间ID:"), gbc);
        gbc.gridx = 1;
        dialog.add(roomIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("预订ID(可选):"), gbc);
        gbc.gridx = 1;
        dialog.add(bookingIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("押金:"), gbc);
        gbc.gridx = 1;
        dialog.add(depositField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        dialog.add(new JLabel("操作员ID:"), gbc);
        gbc.gridx = 1;
        dialog.add(userIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        JButton saveButton = new JButton("确认入住");
        saveButton.addActionListener(e -> {
            try {
                CheckinRequest request = new CheckinRequest();
                request.setCustomerId(Integer.parseInt(customerIdField.getText().trim()));
                request.setRoomId(Integer.parseInt(roomIdField.getText().trim()));
                String bookingIdText = bookingIdField.getText().trim();
                if (!bookingIdText.isEmpty()) {
                    request.setBookingId(Integer.parseInt(bookingIdText));
                }
                request.setDeposit(new BigDecimal(depositField.getText().trim()));
                request.setUserId(Integer.parseInt(userIdField.getText().trim()));

                Result<?> result = hotelController.checkin(request);
                if (result.isSuccess()) {
                    JOptionPane.showMessageDialog(dialog, result.getMessage());
                    dialog.dispose();
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(dialog, result.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "输入格式错误: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.add(saveButton, gbc);
        dialog.setVisible(true);
    }

    private void showCheckoutDialog() {
        int row = checkinTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一条入住记录");
            return;
        }
        Integer checkinId = (Integer) tableModel.getValueAt(row, 0);
        String status = (String) tableModel.getValueAt(row, 6);
        if (!"已入住".equals(status)) {
            JOptionPane.showMessageDialog(this, "只能对已入住的记录进行退房");
            return;
        }

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "退房结算", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        JComboBox<String> payMethodCombo = new JComboBox<>(new String[]{"现金", "微信", "支付宝", "银行卡", "信用卡"});
        JTextField userIdField = new JTextField("1", 10);

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("支付方式:"), gbc);
        gbc.gridx = 1;
        dialog.add(payMethodCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("操作员ID:"), gbc);
        gbc.gridx = 1;
        dialog.add(userIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton confirmButton = new JButton("确认退房");
        confirmButton.addActionListener(e -> {
            try {
                CheckoutRequest request = new CheckoutRequest();
                request.setCheckinId(checkinId);
                request.setPayMethod((String) payMethodCombo.getSelectedItem());
                request.setUserId(Integer.parseInt(userIdField.getText().trim()));

                Result<BillDetailVO> result = hotelController.checkout(request);
                if (result.isSuccess()) {
                    BillDetailVO bill = result.getData();
                    String msg = String.format("退房成功!\n\n房费: %.2f\n附加费: %.2f\n总计: %.2f\n退款: %.2f",
                            bill.getRoomCharge(), bill.getExtraCharge(),
                            bill.getTotalAmount(), bill.getRefund());
                    JOptionPane.showMessageDialog(dialog, msg);
                    dialog.dispose();
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(dialog, result.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "输入格式错误: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.add(confirmButton, gbc);
        dialog.setVisible(true);
    }
}