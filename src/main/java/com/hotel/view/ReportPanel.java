package com.hotel.view;

import com.hotel.controller.Result;
import com.hotel.controller.ReportController;
import com.hotel.model.vo.RevenueReportVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ReportPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(ReportPanel.class);

    private final ReportController reportController;
    private JTable revenueTable;
    private DefaultTableModel tableModel;
    private JTextField startDateField;
    private JTextField endDateField;
    private JLabel occupancyLabel;
    private JLabel totalRevenueLabel;
    private JLabel avgRevenueLabel;

    public ReportPanel(ReportController reportController) {
        this.reportController = reportController;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.add(new JLabel("开始日期:"));
        startDateField = new JTextField(LocalDate.now().minusDays(7).toString(), 10);
        filterPanel.add(startDateField);
        filterPanel.add(new JLabel("结束日期:"));
        endDateField = new JTextField(LocalDate.now().toString(), 10);
        filterPanel.add(endDateField);

        JButton queryButton = new JButton("查询");
        queryButton.addActionListener(e -> queryReports());
        filterPanel.add(queryButton);
        add(filterPanel, BorderLayout.NORTH);

        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("汇总统计"));
        occupancyLabel = new JLabel("入住率: --", JLabel.CENTER);
        occupancyLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        totalRevenueLabel = new JLabel("总营收: --", JLabel.CENTER);
        totalRevenueLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        avgRevenueLabel = new JLabel("均单收入: --", JLabel.CENTER);
        avgRevenueLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        summaryPanel.add(occupancyLabel);
        summaryPanel.add(totalRevenueLabel);
        summaryPanel.add(avgRevenueLabel);
        add(summaryPanel, BorderLayout.SOUTH);

        String[] columns = {"日期", "退房数", "房费总计", "附加费总计", "总营收", "均单收入"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        revenueTable = new JTable(tableModel);
        revenueTable.setRowHeight(25);
        revenueTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(revenueTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshData() {
        queryReports();
    }

    private void queryReports() {
        try {
            LocalDate startDate = LocalDate.parse(startDateField.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate endDate = LocalDate.parse(endDateField.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);

            Result<List<RevenueReportVO>> revenueResult = reportController.getDailyRevenue(startDate, endDate);
            Result<RevenueReportVO> occupancyResult = reportController.getOccupancyRate(startDate, endDate);

            tableModel.setRowCount(0);
            if (revenueResult.isSuccess() && revenueResult.getData() != null) {
                for (RevenueReportVO vo : revenueResult.getData()) {
                    tableModel.addRow(new Object[]{
                            vo.getReportDate(), vo.getCheckoutCount(),
                            vo.getRoomChargeTotal(), vo.getExtraChargeTotal(),
                            vo.getTotalRevenue(), vo.getAvgRevenuePerRoom()
                    });
                }
            }

            if (occupancyResult.isSuccess() && occupancyResult.getData() != null) {
                RevenueReportVO occ = occupancyResult.getData();
                occupancyLabel.setText(String.format("入住率: %s%%", occ.getOccupancyRate()));
                totalRevenueLabel.setText(String.format("总营收: %s", occ.getTotalRevenue()));
                avgRevenueLabel.setText(String.format("均单收入: %s", occ.getAvgRevenuePerRoom()));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "日期格式错误，请使用 yyyy-MM-dd 格式", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}