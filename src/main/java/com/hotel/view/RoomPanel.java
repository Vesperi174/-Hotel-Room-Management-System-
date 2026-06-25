package com.hotel.view;

import com.hotel.controller.Result;
import com.hotel.controller.RoomController;
import com.hotel.controller.RoomTypeController;
import com.hotel.model.entity.Room;
import com.hotel.model.entity.RoomType;
import com.hotel.model.vo.RoomStatusVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RoomPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(RoomPanel.class);

    private final RoomController roomController;
    private final RoomTypeController roomTypeController;
    private JTable roomTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusFilter;
    private JComboBox<String> typeFilter;
    private JButton refreshButton;
    private JButton addButton;
    private JButton statusButton;
    private JButton deleteButton;

    private Map<String, Integer> typeNameToId = new HashMap<>();
    private List<RoomType> roomTypes;

    public RoomPanel(RoomController roomController, RoomTypeController roomTypeController) {
        this.roomController = roomController;
        this.roomTypeController = roomTypeController;
        initUI();
        loadRoomTypes();
    }

    private void loadRoomTypes() {
        Result<List<RoomType>> result = roomTypeController.getAllRoomTypes();
        if (result.isSuccess() && result.getData() != null) {
            roomTypes = result.getData();
            typeNameToId.clear();
            for (RoomType rt : roomTypes) {
                typeNameToId.put(rt.getTypeName(), rt.getTypeId());
            }
            updateTypeFilterItems();
        }
    }

    private void updateTypeFilterItems() {
        typeFilter.removeAllItems();
        typeFilter.addItem("全部");
        for (RoomType rt : roomTypes) {
            typeFilter.addItem(rt.getTypeName());
        }
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.add(new JLabel("状态筛选:"));
        statusFilter = new JComboBox<>(new String[]{"全部", "空闲", "已预订", "已入住", "清洁中", "维修中"});
        topPanel.add(statusFilter);
        topPanel.add(new JLabel("类型筛选:"));
        typeFilter = new JComboBox<>();
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

        String[] columns = {"房间ID", "房间号", "类型", "楼层", "状态", "描述"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        roomTable = new JTable(tableModel);
        roomTable.setRowHeight(25);
        roomTable.getTableHeader().setReorderingAllowed(false);

        Map<String, Color> roomColors = new HashMap<>();
        roomColors.put("空闲", new Color(0xE8, 0xF5, 0xE9));
        roomColors.put("已预订", new Color(0xE3, 0xF2, 0xFD));
        roomColors.put("入住", new Color(0xFF, 0xF3, 0xE0));
        roomColors.put("维修中", new Color(0xFF, 0xEB, 0xEE));
        StatusColorRenderer.applyToTable(roomTable, 4, roomColors, Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(roomTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        Result<List<Room>> result = roomController.getAllRooms();
        if (result.isSuccess() && result.getData() != null) {
            for (Room room : result.getData()) {
                String status = statusFilter.getSelectedItem() != null
                        ? statusFilter.getSelectedItem().toString() : "全部";
                if (!"全部".equals(status) && !status.equals(room.getRoomStatus())) {
                    continue;
                }
                String typeFilterVal = typeFilter.getSelectedItem() != null
                        ? typeFilter.getSelectedItem().toString() : "全部";
                if (!"全部".equals(typeFilterVal)) {
                    Integer typeId = typeNameToId.get(typeFilterVal);
                    if (typeId == null || !typeId.equals(room.getTypeId())) {
                        continue;
                    }
                }
                String typeName = room.getRoomType() != null
                        ? room.getRoomType().getTypeName()
                        : "类型" + room.getTypeId();
                tableModel.addRow(new Object[]{
                        room.getRoomId(), room.getRoomNumber(), typeName,
                        room.getFloor(), room.getRoomStatus(), room.getDescription()
                });
            }
        }
    }

    private void showAddRoomDialog() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "新增客房", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(350, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        JTextField roomNumberField = new JTextField(10);

        JComboBox<String> typeCombo = new JComboBox<>();
        for (RoomType rt : roomTypes) {
            typeCombo.addItem(rt.getTypeName() + " (" + rt.getBedType() + ")");
        }

        JComboBox<String> floorCombo = new JComboBox<>();
        for (int i = 1; i <= 10; i++) {
            floorCombo.addItem(i + " 层");
        }

        String[] statusOptions = {"空闲", "维修中"};
        JComboBox<String> statusCombo = new JComboBox<>(statusOptions);

        JTextField descField = new JTextField(10);

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("房间号:"), gbc);
        gbc.gridx = 1;
        dialog.add(roomNumberField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("房型:"), gbc);
        gbc.gridx = 1;
        dialog.add(typeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("楼层:"), gbc);
        gbc.gridx = 1;
        dialog.add(floorCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("状态:"), gbc);
        gbc.gridx = 1;
        dialog.add(statusCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        dialog.add(new JLabel("描述:"), gbc);
        gbc.gridx = 1;
        dialog.add(descField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(e -> {
            String roomNumber = roomNumberField.getText().trim();
            if (roomNumber.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "请输入房间号", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int typeIdx = typeCombo.getSelectedIndex();
            if (typeIdx < 0 || typeIdx >= roomTypes.size()) {
                JOptionPane.showMessageDialog(dialog, "请选择房型", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Room room = new Room();
            room.setRoomNumber(roomNumber);
            room.setTypeId(roomTypes.get(typeIdx).getTypeId());
            room.setFloor(floorCombo.getSelectedIndex() + 1);
            room.setRoomStatus(statusCombo.getSelectedItem().toString());
            room.setDescription(descField.getText().trim());

            Result<Void> result = roomController.addRoom(room);
            if (result.isSuccess()) {
                JOptionPane.showMessageDialog(dialog, result.getMessage());
                dialog.dispose();
                refreshData();
            } else {
                JOptionPane.showMessageDialog(dialog, result.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
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