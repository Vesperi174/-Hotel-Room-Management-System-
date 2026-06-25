package com.hotel.view;

import com.hotel.controller.Result;
import com.hotel.controller.BillController;
import com.hotel.model.vo.BillDetailVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@Component
public class BillPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(BillPanel.class);

    private final BillController billController;
    private JTable billTable;
    private DefaultTableModel tableModel;

    public BillPanel(BillController billController) {
        this.billController = billController;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> refreshData());
        topPanel.add(refreshButton);

        JButton detailButton = new JButton("查看详情");
        detailButton.addActionListener(e -> showBillDetail());
        topPanel.add(detailButton);

        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"账单ID", "入住ID", "房费", "附加费", "押金", "总金额", "退款", "支付方式", "结账时间"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        billTable = new JTable(tableModel);
        billTable.setRowHeight(25);
        billTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(billTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        Result<List<BillDetailVO>> result = billController.getAllBillDetail();
        if (result.isSuccess() && result.getData() != null) {
            for (BillDetailVO vo : result.getData()) {
                tableModel.addRow(new Object[]{
                        vo.getBillId(), vo.getCheckinId(), vo.getRoomCharge(),
                        vo.getExtraCharge(), vo.getDepositPaid(), vo.getTotalAmount(),
                        vo.getRefund(), vo.getPayMethod(), vo.getBillTime()
                });
            }
        }
    }

    private void showBillDetail() {
        int row = billTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一条账单记录");
            return;
        }
        Object roomCharge = tableModel.getValueAt(row, 2);
        Object extraCharge = tableModel.getValueAt(row, 3);
        Object deposit = tableModel.getValueAt(row, 4);
        Object total = tableModel.getValueAt(row, 5);
        Object refund = tableModel.getValueAt(row, 6);
        Object payMethod = tableModel.getValueAt(row, 7);
        Object billTime = tableModel.getValueAt(row, 8);

        String msg = String.format("""
                账单详情
                ------------------------------
                房费:    %s
                附加费:  %s
                押金:    %s
                总金额:  %s
                退款:    %s
                支付方式: %s
                结账时间: %s
                """, roomCharge, extraCharge, deposit, total, refund, payMethod, billTime);

        JOptionPane.showMessageDialog(this, msg, "账单详情", JOptionPane.INFORMATION_MESSAGE);
    }
}