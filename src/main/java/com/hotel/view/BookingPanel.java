package com.hotel.view;

import com.hotel.controller.Result;
import com.hotel.controller.CustomerController;
import com.hotel.controller.HotelController;
import com.hotel.controller.RoomController;
import com.hotel.model.entity.Booking;
import com.hotel.model.entity.Customer;
import com.hotel.model.entity.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class BookingPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(BookingPanel.class);

    private final HotelController hotelController;
    private final CustomerController customerController;
    private final RoomController roomController;
    private JTable roomTable;
    private DefaultTableModel tableModel;
    private JButton newBookingButton;
    private JButton cancelButton;

    private List<Customer> customerCache = new ArrayList<>();
    private List<Room> roomCache = new ArrayList<>();

    public BookingPanel(HotelController hotelController, CustomerController customerController,
                        RoomController roomController) {
        this.hotelController = hotelController;
        this.customerController = customerController;
        this.roomController = roomController;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        newBookingButton = new JButton("新建预订");
        cancelButton = new JButton("取消预订");
        JButton refreshButton = new JButton("刷新");

        newBookingButton.addActionListener(e -> showBookingDialog());
        cancelButton.addActionListener(e -> cancelSelectedBooking());
        refreshButton.addActionListener(e -> refreshData());

        topPanel.add(newBookingButton);
        topPanel.add(cancelButton);
        topPanel.add(refreshButton);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"预订ID", "客户ID", "客户名", "房间ID", "房间号", "预订日期", "预计到达", "预计离开", "状态", "押金"};
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

    private void loadCache() {
        Result<List<Customer>> cResult = customerController.getAllCustomers();
        if (cResult.isSuccess() && cResult.getData() != null) {
            customerCache = cResult.getData();
        }
        Result<List<Room>> rResult = roomController.getAllRooms();
        if (rResult.isSuccess() && rResult.getData() != null) {
            roomCache = rResult.getData();
        }
    }

    public void refreshData() {
        tableModel.setRowCount(0);
    }

    private void showBookingDialog() {
        loadCache();

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "新建预订", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(420, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        JComboBox<String> customerCombo = new JComboBox<>();
        for (Customer c : customerCache) {
            customerCombo.addItem(c.getCustomerId() + " - " + c.getCustomerName() + " (" + c.getPhone() + ")");
        }

        JComboBox<String> roomCombo = new JComboBox<>();
        for (Room r : roomCache) {
            String typeName = r.getRoomType() != null ? r.getRoomType().getTypeName() : "类型" + r.getTypeId();
            roomCombo.addItem(r.getRoomId() + " - " + r.getRoomNumber() + " (" + typeName + ") [" + r.getRoomStatus() + "]");
        }

        JTextField arrivalField = new JTextField(LocalDate.now().plusDays(1).toString(), 10);
        JTextField leaveField = new JTextField(LocalDate.now().plusDays(3).toString(), 10);
        JTextField depositField = new JTextField("0.00", 10);

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("客户:"), gbc);
        gbc.gridx = 1;
        dialog.add(customerCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("房间:"), gbc);
        gbc.gridx = 1;
        dialog.add(roomCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("预计到达(yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        dialog.add(arrivalField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("预计离开(yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        dialog.add(leaveField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        dialog.add(new JLabel("押金:"), gbc);
        gbc.gridx = 1;
        dialog.add(depositField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        JButton saveButton = new JButton("确认预订");
        saveButton.addActionListener(e -> {
            try {
                int cIdx = customerCombo.getSelectedIndex();
                int rIdx = roomCombo.getSelectedIndex();
                if (cIdx < 0 || cIdx >= customerCache.size() || rIdx < 0 || rIdx >= roomCache.size()) {
                    JOptionPane.showMessageDialog(dialog, "请选择客户和房间", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Booking booking = new Booking();
                booking.setCustomerId(customerCache.get(cIdx).getCustomerId());
                booking.setRoomId(roomCache.get(rIdx).getRoomId());
                booking.setExpectedArrival(LocalDate.parse(arrivalField.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE));
                booking.setExpectedLeave(LocalDate.parse(leaveField.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE));
                booking.setDepositPaid(new BigDecimal(depositField.getText().trim()));

                Result<Booking> result = hotelController.booking(booking);
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

    private void cancelSelectedBooking() {
        int row = roomTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一条预订记录");
            return;
        }
        Integer bookingId = (Integer) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "确定要取消该预订吗？",
                "确认取消", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Result<Void> result = hotelController.cancelBooking(bookingId);
            if (result.isSuccess()) {
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, result.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}