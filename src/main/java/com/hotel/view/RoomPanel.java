package com.hotel.view;

import com.hotel.controller.Result;
import com.hotel.controller.RoomController;
import com.hotel.model.entity.Room;
import com.hotel.model.vo.RoomStatusVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@Component
public class RoomPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(RoomPanel.class);

    private final RoomController roomController;
    private JTable roomTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusFilter;
    private JComboBox<String> typeFilter;
    private JButton refreshButton;
    private JButton addButton;
    private JButton statusButton;
    private JButton deleteButton;

    public RoomPanel(RoomController roomController) {
        this.roomController = roomController;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.add(new JLabel("状态筛选:"));
        statusFilter = new JComboBox<>(new String[]{"全部", "空闲", "已预订", "已入住", "清洁中", "维修中"});
        topPanel.add(statusFilter);
        topPanel.add(new JLabel("类型筛选:"));
        typeFilter = new JComboBox<>(new String[]{"全部", "标准间", "大床房", "双床房", "豪华套房", "总统套房"});
        topPanel.add(typeFilter);
        refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> refreshData());
        topPanel.add(refreshButton);

        addButton = new JButton("新增客房");
        statusButton = new JButton("状态变更");
        deleteButton = new JButton("删除客房");

        addButton.addActionListener(e -> showAddRoomDialog());
        statusButton.addActionListener(e -> showStatusChangeDialog());
        deleteButton.addActionListener(e -> deleteSelectedRoom());

        topPanel.add(addButton);
        topPanel.add(statusButton);
        topPanel.add(deleteButton);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"房间ID", "房间号", "类型ID", "楼层", "状态", "描述"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        roomTable = new JTable(tableModel);
        roomTable.setRowHeight(25);
        roomTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(roomTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        Result<List<Room>> result = roomController.getAllRooms();
        if (result.isSuccess() && result.getData() != null) {
            for (Room room : result.getData()) {
                String status = statusFilter.getSelectedItem() != null ? statusFilter.getSelectedItem().toString() : "全部";
                if (!"全部".equals(status) && !status.equals(room.getRoomStatus())) {
                    continue;
                }
                tableModel.addRow(new Object[]{
                        room.getRoomId(), room.getRoomNumber(), room.getTypeId(),
                        room.getFloor(), room.getRoomStatus(), room.getDescription()
                });
            }
        }
    }

    private void showAddRoomDialog() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "新增客房", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(350, 280);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        JTextField roomNumberField = new JTextField(10);
        JTextField typeIdField = new JTextField(10);
        JTextField floorField = new JTextField(10);
        JTextField descField = new JTextField(10);

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("房间号:"), gbc);
        gbc.gridx = 1;
        dialog.add(roomNumberField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("类型ID:"), gbc);
        gbc.gridx = 1;
        dialog.add(typeIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("楼层:"), gbc);
        gbc.gridx = 1;
        dialog.add(floorField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("描述:"), gbc);
        gbc.gridx = 1;
        dialog.add(descField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(e -> {
            try {
                Room room = new Room();
                room.setRoomNumber(roomNumberField.getText().trim());
                room.setTypeId(Integer.parseInt(typeIdField.getText().trim()));
                room.setFloor(Integer.parseInt(floorField.getText().trim()));
                room.setDescription(descField.getText().trim());
                Result<Void> result = roomController.addRoom(room);
                if (result.isSuccess()) {
                    JOptionPane.showMessageDialog(dialog, result.getMessage());
                    dialog.dispose();
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(dialog, result.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "请输入正确的数字格式", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.add(saveButton, gbc);
        dialog.setVisible(true);
    }

    private void showStatusChangeDialog() {
        int row = roomTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一间客房");
            return;
        }
        Integer roomId = (Integer) tableModel.getValueAt(row, 0);
        String currentStatus = (String) tableModel.getValueAt(row, 4);

        String[] statuses = {"空闲", "已预订", "已入住", "清洁中", "维修中"};
        String newStatus = (String) JOptionPane.showInputDialog(this, "当前状态: " + currentStatus + "\n选择新状态:",
                "状态变更", JOptionPane.QUESTION_MESSAGE, null, statuses, statuses[0]);
        if (newStatus != null) {
            Result<Void> result = roomController.updateRoomStatus(roomId, newStatus);
            if (result.isSuccess()) {
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, result.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedRoom() {
        int row = roomTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一间客房");
            return;
        }
        Integer roomId = (Integer) tableModel.getValueAt(row, 0);
        String roomNumber = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除客房 " + roomNumber + " 吗？",
                "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Result<Void> result = roomController.deleteRoom(roomId);
            if (result.isSuccess()) {
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, result.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}