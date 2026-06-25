package com.hotel.view;

import com.hotel.controller.Result;
import com.hotel.controller.UserController;
import com.hotel.model.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@Component
public class UserPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(UserPanel.class);

    private final UserController userController;

    private JTable userTable;
    private DefaultTableModel tableModel;

    public UserPanel(UserController userController) {
        this.userController = userController;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("新增用户");
        JButton editButton = new JButton("编辑用户");
        JButton disableButton = new JButton("禁用/启用");
        JButton refreshButton = new JButton("刷新");

        addButton.addActionListener(e -> showAddUserDialog());
        editButton.addActionListener(e -> showEditUserDialog());
        disableButton.addActionListener(e -> toggleUserStatus());
        refreshButton.addActionListener(e -> refreshData());

        topPanel.add(addButton);
        topPanel.add(editButton);
        topPanel.add(disableButton);
        topPanel.add(refreshButton);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"用户ID", "用户名", "真实姓名", "角色ID", "手机号", "状态", "创建时间"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(tableModel);
        userTable.setRowHeight(25);
        add(new JScrollPane(userTable), BorderLayout.CENTER);
    }

    public void refreshData() {
        refreshData(null);
    }

    public void refreshData(User currentUser) {
        tableModel.setRowCount(0);
        try {
            Result<List<User>> result = userController.getAllUsers();
            if (result.isSuccess() && result.getData() != null) {
                for (User user : result.getData()) {
                    if (currentUser != null && currentUser.getUserId().equals(user.getUserId())) {
                        continue;
                    }
                    tableModel.addRow(new Object[]{
                            user.getUserId(), user.getUsername(), user.getRealName(),
                            user.getRoleId(), user.getPhone(), user.getStatus(),
                            user.getCreateTime()
                    });
                }
            }
        } catch (Exception e) {
            log.error("刷新用户列表失败", e);
        }
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
                "新增用户", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField usernameField = addField(panel, gbc, "用户名:", 0);
        JPasswordField passwordField = new JPasswordField(15);
        addField(panel, gbc, "密码:", 1, passwordField);
        JTextField realNameField = addField(panel, gbc, "真实姓名:", 2);
        JTextField roleIdField = addField(panel, gbc, "角色ID:", 3);
        JTextField phoneField = addField(panel, gbc, "手机号:", 4);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JButton saveButton = new JButton("保存");
        panel.add(saveButton, gbc);

        saveButton.addActionListener(e -> {
            try {
                User user = new User();
                user.setUsername(usernameField.getText().trim());
                user.setPassword(new String(passwordField.getPassword()));
                user.setRealName(realNameField.getText().trim());
                user.setRoleId(Integer.parseInt(roleIdField.getText().trim()));
                user.setPhone(phoneField.getText().trim());

                Result<Void> result = userController.addUser(user);
                if (result.isSuccess()) {
                    JOptionPane.showMessageDialog(dialog, result.getMessage());
                    dialog.dispose();
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(dialog, result.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "角色ID必须为数字", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showEditUserDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要编辑的用户", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Integer userId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Result<User> result = userController.getUserById(userId);
        if (!result.isSuccess() || result.getData() == null) {
            JOptionPane.showMessageDialog(this, "用户不存在", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        User user = result.getData();

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
                "编辑用户", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField realNameField = addField(panel, gbc, "真实姓名:", 0, user.getRealName());
        JTextField roleIdField = addField(panel, gbc, "角色ID:", 1, String.valueOf(user.getRoleId()));
        JTextField phoneField = addField(panel, gbc, "手机号:", 2, user.getPhone() != null ? user.getPhone() : "");

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JButton saveButton = new JButton("保存");
        panel.add(saveButton, gbc);

        saveButton.addActionListener(e -> {
            try {
                user.setRealName(realNameField.getText().trim());
                user.setRoleId(Integer.parseInt(roleIdField.getText().trim()));
                user.setPhone(phoneField.getText().trim());

                Result<Void> updateResult = userController.updateUser(user);
                if (updateResult.isSuccess()) {
                    JOptionPane.showMessageDialog(dialog, updateResult.getMessage());
                    dialog.dispose();
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(dialog, updateResult.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "角色ID必须为数字", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void toggleUserStatus() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择用户", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Integer userId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 5);
        String newStatus = "正常".equals(currentStatus) ? "禁用" : "正常";

        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要将该用户状态改为 \"" + newStatus + "\" 吗？",
                "确认操作", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Result<Void> result = userController.updateUserStatus(userId, newStatus);
            if (result.isSuccess()) {
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, result.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JTextField addField(JPanel panel, GridBagConstraints gbc, String label, int row) {
        return addField(panel, gbc, label, row, "");
    }

    private JTextField addField(JPanel panel, GridBagConstraints gbc, String label, int row, String defaultValue) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        JTextField field = new JTextField(defaultValue, 15);
        panel.add(field, gbc);
        return field;
    }

    private void addField(JPanel panel, GridBagConstraints gbc, String label, int row, JPasswordField field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }
}