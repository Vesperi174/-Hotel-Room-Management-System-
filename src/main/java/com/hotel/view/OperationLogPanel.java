package com.hotel.view;

import com.hotel.controller.Result;
import com.hotel.controller.OperationLogController;
import com.hotel.model.entity.OperationLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@Component
public class OperationLogPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(OperationLogPanel.class);

    private final OperationLogController operationLogController;

    private JTable logTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> typeFilter;

    public OperationLogPanel(OperationLogController operationLogController) {
        this.operationLogController = operationLogController;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.add(new JLabel("操作类型:"));

        typeFilter = new JComboBox<>(new String[]{"全部", "登录", "预订", "入住", "退房", "用户管理", "客房管理", "客户管理", "其他"});
        typeFilter.addActionListener(e -> refreshData());
        topPanel.add(typeFilter);

        JButton refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> refreshData());
        topPanel.add(refreshButton);

        JButton clearOldButton = new JButton("清理30天前日志");
        clearOldButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "确定要清理30天前的操作日志吗？", "确认清理", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                operationLogController.deleteBefore(
                        java.time.LocalDate.now().minusDays(30).toString());
                refreshData();
            }
        });
        topPanel.add(clearOldButton);

        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"日志ID", "用户ID", "操作时间", "操作类型", "操作内容", "IP地址"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        logTable = new JTable(tableModel);
        logTable.setRowHeight(25);
        logTable.getTableHeader().setReorderingAllowed(false);
        logTable.getColumnModel().getColumn(4).setPreferredWidth(300);
        add(new JScrollPane(logTable), BorderLayout.CENTER);
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        try {
            String filter = (String) typeFilter.getSelectedItem();
            Result<List<OperationLog>> result = "全部".equals(filter)
                    ? operationLogController.getAllLogs()
                    : operationLogController.getLogsByType(filter);

            if (result.isSuccess() && result.getData() != null) {
                for (OperationLog log : result.getData()) {
                    tableModel.addRow(new Object[]{
                            log.getLogId(), log.getUserId(), log.getLogTime(),
                            log.getLogType(), log.getLogContent(), log.getIpAddress()
                    });
                }
            }
        } catch (Exception e) {
            log.error("刷新操作日志失败", e);
        }
    }
}