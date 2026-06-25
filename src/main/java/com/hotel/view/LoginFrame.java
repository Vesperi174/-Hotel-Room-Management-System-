package com.hotel.view;

import com.hotel.controller.Result;
import com.hotel.controller.UserController;
import com.hotel.model.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class LoginFrame extends JFrame {

    private static final Logger log = LoggerFactory.getLogger(LoginFrame.class);

    private final UserController userController;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;

    private User currentUser;

    public LoginFrame(UserController userController) {
        this.userController = userController;
        initUI();
    }

    private void initUI() {
        setTitle("酒店客房管理系统 - 登录");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 340);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);

        JLabel titleLabel = new JLabel("酒店客房管理系统", JLabel.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("用户名:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        mainPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("密码:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        mainPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        loginButton = new JButton("登录");
        loginButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        loginButton.addActionListener(e -> login());
        mainPanel.add(loginButton, gbc);

        gbc.gridy = 4;
        statusLabel = new JLabel(" ", JLabel.CENTER);
        statusLabel.setForeground(Color.RED);
        mainPanel.add(statusLabel, gbc);

        getRootPane().setDefaultButton(loginButton);
        add(mainPanel);
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("请输入用户名和密码");
            return;
        }

        Result<User> result = userController.login(username, password);
        if (result.isSuccess()) {
            currentUser = result.getData();
            log.info("用户登录成功: {}", currentUser.getUsername());
            dispose();
        } else {
            statusLabel.setText(result.getMessage());
            passwordField.setText("");
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }
}