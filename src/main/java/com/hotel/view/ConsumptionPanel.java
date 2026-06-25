package com.hotel.view;

import com.hotel.controller.Result;
import com.hotel.controller.HotelController;
import com.hotel.model.entity.Consumption;
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
public class ConsumptionPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(ConsumptionPanel.class);

    private final HotelController hotelController;

    private JTable checkinTable;
    private JTable consumptionTable;
    private DefaultTableModel checkinTableModel;
    private DefaultTableModel consumptionTableModel;
    private JLabel currentCheckinLabel;

    public ConsumptionPanel(HotelController hotelController) {
        this.hotelController = hotelController;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(420);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("已入住房间"));
        String[] checkinCols = {"入住ID", "客户名", "房间号", "房型", "入住时间"};
        checkinTableModel = new DefaultTableModel(checkinCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        checkinTable = new JTable(checkinTableModel);
        checkinTable.setRowHeight(25);
        checkinTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadConsumptionForSelected();
            }
        });
        JButton refreshCheckinBtn = new JButton("刷新");
        refreshCheckinBtn.addActionListener(e -> refreshCheckinList());
        JPanel leftTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftTop.add(refreshCheckinBtn);
        leftPanel.add(leftTop, BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(checkinTable), BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("消费记录"));

        currentCheckinLabel = new JLabel("请选择左侧入住记录");
        currentCheckinLabel.setForeground(Color.GRAY);
        rightPanel.add(currentCheckinLabel, BorderLayout.NORTH);

        String[] consCols = {"消费ID", "项目名称", "单价", "数量", "金额", "消费时间", "备注"};
        consumptionTableModel = new DefaultTableModel(consCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        consumptionTable = new JTable(consumptionTableModel);
        consumptionTable.setRowHeight(25);
        rightPanel.add(new JScrollPane(consumptionTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("新增消费");
        JButton deleteBtn = new JButton("删除消费");
        addBtn.addActionListener(e -> showAddConsumptionDialog());
        deleteBtn.addActionListener(e -> deleteConsumption());
        btnPanel.add(addBtn);
        btnPanel.add(deleteBtn);
        rightPanel.add(btnPanel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);
    }

    public void refreshData() {
        refreshCheckinList();
    }

    private void refreshCheckinList() {
        checkinTableModel.setRowCount(0);
        consumptionTableModel.setRowCount(0);
        currentCheckinLabel.setText("请选择左侧入住记录");
        try {
            Result<List<CheckinDetailVO>> result = hotelController.getAllCheckinDetail();
            if (result.isSuccess() && result.getData() != null) {
                for (CheckinDetailVO vo : result.getData()) {
                    if ("已入住".equals(vo.getStatus())) {
                        checkinTableModel.addRow(new Object[]{
                                vo.getCheckinId(), vo.getCustomerName(), vo.getRoomNumber(),
                                vo.getTypeName(), vo.getCheckinTime()
                        });
                    }
                }
            }
        } catch (Exception e) {
            log.error("刷新入住列表失败", e);
        }
    }

    private void loadConsumptionForSelected() {
        int row = checkinTable.getSelectedRow();
        if (row == -1) return;

        Integer checkinId = (Integer) checkinTableModel.getValueAt(row, 0);
        String customerName = (String) checkinTableModel.getValueAt(row, 1);
        String roomNumber = (String) checkinTableModel.getValueAt(row, 2);
        currentCheckinLabel.setText("客户: " + customerName + " | 房间: " + roomNumber + " | 入住ID: " + checkinId);

        consumptionTableModel.setRowCount(0);
        try {
            Result<List<Consumption>> result = hotelController.getConsumptionsByCheckinId(checkinId);
            if (result.isSuccess() && result.getData() != null) {
                for (Consumption c : result.getData()) {
                    BigDecimal total = c.getItemPrice().multiply(new BigDecimal(c.getQuantity()));
                    consumptionTableModel.addRow(new Object[]{
                            c.getConsId(), c.getItemName(), c.getItemPrice(),
                            c.getQuantity(), total, c.getConsTime(), c.getRemark()
                    });
                }
            }
        } catch (Exception e) {
            log.error("加载消费记录失败", e);
        }
    }

    private void showAddConsumptionDialog() {
        int row = checkinTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一条入住记录");
            return;
        }
        Integer checkinId = (Integer) checkinTableModel.getValueAt(row, 0);

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
                "新增消费记录", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        JComboBox<String> itemCombo = new JComboBox<>(new String[]{
                "矿泉水", "饮料", "方便面", "零食", "洗衣服务", "送餐服务", "加床", "其他"
        });
        itemCombo.setEditable(true);

        JTextField priceField = new JTextField("10.00", 10);
        JTextField quantityField = new JTextField("1", 10);
        JTextField remarkField = new JTextField(10);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("消费项目:"), gbc);
        gbc.gridx = 1;
        panel.add(itemCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("单价:"), gbc);
        gbc.gridx = 1;
        panel.add(priceField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("数量:"), gbc);
        gbc.gridx = 1;
        panel.add(quantityField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("备注:"), gbc);
        gbc.gridx = 1;
        panel.add(remarkField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        JButton saveBtn = new JButton("确认添加");
        saveBtn.addActionListener(e -> {
            try {
                Consumption consumption = new Consumption();
                consumption.setCheckinId(checkinId);
                consumption.setItemName(itemCombo.getSelectedItem().toString());
                consumption.setItemPrice(new BigDecimal(priceField.getText().trim()));
                consumption.setQuantity(Integer.parseInt(quantityField.getText().trim()));
                consumption.setRemark(remarkField.getText().trim());

                Result<Void> result = hotelController.addConsumption(consumption);
                if (result.isSuccess()) {
                    JOptionPane.showMessageDialog(dialog, result.getMessage());
                    dialog.dispose();
                    loadConsumptionForSelected();
                } else {
                    JOptionPane.showMessageDialog(dialog, result.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "请填写正确的数字格式", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(saveBtn, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void deleteConsumption() {
        int row = consumptionTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一条消费记录");
            return;
        }
        Integer consId = (Integer) consumptionTableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要删除该消费记录吗？", "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Result<Void> result = hotelController.deleteConsumption(consId);
            if (result.isSuccess()) {
                loadConsumptionForSelected();
            } else {
                JOptionPane.showMessageDialog(this, result.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}