package com.hotel.view;

import com.hotel.controller.Result;
import com.hotel.controller.CustomerController;
import com.hotel.model.entity.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@Component
public class CustomerPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(CustomerPanel.class);

    private final CustomerController customerController;
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchButton;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;

    public CustomerPanel(CustomerController customerController) {
        this.customerController = customerController;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.add(new JLabel("搜索姓名:"));
        searchField = new JTextField(12);
        topPanel.add(searchField);
        searchButton = new JButton("搜索");
        searchButton.addActionListener(e -> searchCustomers());
        topPanel.add(searchButton);

        JButton refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> refreshData());
        topPanel.add(refreshButton);

        addButton = new JButton("新增客户");
        editButton = new JButton("编辑客户");
        deleteButton = new JButton("删除客户");

        addButton.addActionListener(e -> showCustomerDialog(null));
        editButton.addActionListener(e -> editSelectedCustomer());
        deleteButton.addActionListener(e -> deleteSelectedCustomer());

        topPanel.add(addButton);
        topPanel.add(editButton);
        topPanel.add(deleteButton);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"客户ID", "姓名", "性别", "身份证号", "手机号", "创建时间"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        customerTable = new JTable(tableModel);
        customerTable.setRowHeight(25);
        customerTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(customerTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        Result<List<Customer>> result = customerController.getAllCustomers();
        if (result.isSuccess() && result.getData() != null) {
            for (Customer c : result.getData()) {
                tableModel.addRow(new Object[]{
                        c.getCustomerId(), c.getCustomerName(), c.getGender(),
                        c.getIdNumber(), c.getPhone(), c.getCreateTime()
                });
            }
        }
    }

    private void searchCustomers() {
        String name = searchField.getText().trim();
        if (name.isEmpty()) {
            refreshData();
            return;
        }
        tableModel.setRowCount(0);
        Result<List<Customer>> result = customerController.searchCustomersByName(name);
        if (result.isSuccess() && result.getData() != null) {
            for (Customer c : result.getData()) {
                tableModel.addRow(new Object[]{
                        c.getCustomerId(), c.getCustomerName(), c.getGender(),
                        c.getIdNumber(), c.getPhone(), c.getCreateTime()
                });
            }
        }
    }

    private void editSelectedCustomer() {
        int row = customerTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一位客户");
            return;
        }
        Integer customerId = (Integer) tableModel.getValueAt(row, 0);
        Result<Customer> result = customerController.getCustomerById(customerId);
        if (result.isSuccess()) {
            showCustomerDialog(result.getData());
        }
    }

    private void showCustomerDialog(Customer existing) {
        boolean isEdit = existing != null;
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
                isEdit ? "编辑客户" : "新增客户", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(380, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        JTextField nameField = new JTextField(isEdit ? existing.getCustomerName() : "", 15);
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"男", "女"});
        if (isEdit && "女".equals(existing.getGender())) genderCombo.setSelectedItem("女");
        JTextField idNumberField = new JTextField(isEdit ? existing.getIdNumber() : "", 15);
        JTextField phoneField = new JTextField(isEdit ? existing.getPhone() : "", 15);

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("姓名:"), gbc);
        gbc.gridx = 1;
        dialog.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("性别:"), gbc);
        gbc.gridx = 1;
        dialog.add(genderCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("身份证号:"), gbc);
        gbc.gridx = 1;
        dialog.add(idNumberField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("手机号:"), gbc);
        gbc.gridx = 1;
        dialog.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(e -> {
            Customer customer = new Customer();
            if (isEdit) customer.setCustomerId(existing.getCustomerId());
            customer.setCustomerName(nameField.getText().trim());
            customer.setGender((String) genderCombo.getSelectedItem());
            customer.setIdNumber(idNumberField.getText().trim());
            customer.setPhone(phoneField.getText().trim());

            Result<Void> result = isEdit
                    ? customerController.updateCustomer(customer)
                    : customerController.addCustomer(customer);

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

    private void deleteSelectedCustomer() {
        int row = customerTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一位客户");
            return;
        }
        Integer customerId = (Integer) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除客户 " + name + " 吗？",
                "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Result<Void> result = customerController.deleteCustomer(customerId);
            if (result.isSuccess()) {
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, result.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}