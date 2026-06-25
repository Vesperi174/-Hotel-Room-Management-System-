package com.hotel.view;

import com.hotel.model.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@Component
public class MainFrame extends JFrame {

    private static final Logger log = LoggerFactory.getLogger(MainFrame.class);

    private final RoomPanel roomPanel;
    private final CustomerPanel customerPanel;
    private final BookingPanel bookingPanel;
    private final CheckinPanel checkinPanel;
    private final BillPanel billPanel;
    private final ReportPanel reportPanel;

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JLabel userLabel;
    private User currentUser;

    public MainFrame(RoomPanel roomPanel, CustomerPanel customerPanel,
                     BookingPanel bookingPanel, CheckinPanel checkinPanel,
                     BillPanel billPanel, ReportPanel reportPanel) {
        this.roomPanel = roomPanel;
        this.customerPanel = customerPanel;
        this.bookingPanel = bookingPanel;
        this.checkinPanel = checkinPanel;
        this.billPanel = billPanel;
        this.reportPanel = reportPanel;
    }

    public void init(User user) {
        this.currentUser = user;
        initUI();
        setVisible(true);
        log.info("主界面已启动，当前用户: {}", user.getUsername());
    }

    private void initUI() {
        setTitle("酒店客房管理系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                log.info("系统退出");
            }
        });

        setJMenuBar(createMenuBar());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(40, 120, 200));
        topPanel.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        JLabel titleLabel = new JLabel("酒店客房管理系统");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.WEST);

        userLabel = new JLabel("当前用户: " + currentUser.getRealName() + " | 角色: " + currentUser.getRoleId());
        userLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        userLabel.setForeground(Color.WHITE);
        topPanel.add(userLabel, BorderLayout.EAST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        contentPanel.add(roomPanel, "room");
        contentPanel.add(customerPanel, "customer");
        contentPanel.add(bookingPanel, "booking");
        contentPanel.add(checkinPanel, "checkin");
        contentPanel.add(billPanel, "bill");
        contentPanel.add(reportPanel, "report");

        add(topPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        showPanel("room");
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu roomMenu = new JMenu("客房管理");
        JMenuItem roomStatusItem = new JMenuItem("客房状态");
        roomStatusItem.addActionListener(e -> showPanel("room"));
        roomMenu.add(roomStatusItem);
        menuBar.add(roomMenu);

        JMenu bookingMenu = new JMenu("预订管理");
        JMenuItem newBookingItem = new JMenuItem("新建预订");
        newBookingItem.addActionListener(e -> showPanel("booking"));
        bookingMenu.add(newBookingItem);
        menuBar.add(bookingMenu);

        JMenu checkinMenu = new JMenu("入住/退房");
        JMenuItem checkinItem = new JMenuItem("入住登记");
        checkinItem.addActionListener(e -> showPanel("checkin"));
        checkinMenu.add(checkinItem);
        menuBar.add(checkinMenu);

        JMenu customerMenu = new JMenu("客户管理");
        JMenuItem customerItem = new JMenuItem("客户信息");
        customerItem.addActionListener(e -> showPanel("customer"));
        customerMenu.add(customerItem);
        menuBar.add(customerMenu);

        JMenu billMenu = new JMenu("账单管理");
        JMenuItem billItem = new JMenuItem("账单查询");
        billItem.addActionListener(e -> showPanel("bill"));
        billMenu.add(billItem);
        menuBar.add(billMenu);

        JMenu reportMenu = new JMenu("统计报表");
        JMenuItem revenueItem = new JMenuItem("营收报表");
        revenueItem.addActionListener(e -> showPanel("report"));
        reportMenu.add(revenueItem);
        menuBar.add(reportMenu);

        return menuBar;
    }

    public void showPanel(String name) {
        cardLayout.show(contentPanel, name);
        switch (name) {
            case "room" -> roomPanel.refreshData();
            case "customer" -> customerPanel.refreshData();
            case "booking" -> bookingPanel.refreshData();
            case "checkin" -> checkinPanel.refreshData();
            case "bill" -> billPanel.refreshData();
            case "report" -> reportPanel.refreshData();
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }
}