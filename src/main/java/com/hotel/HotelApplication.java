package com.hotel;

import com.hotel.model.entity.User;
import com.hotel.view.LoginFrame;
import com.hotel.view.MainFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;

@SpringBootApplication
public class HotelApplication {

    private static final Logger log = LoggerFactory.getLogger(HotelApplication.class);

    public static void main(String[] args) {
        log.info("========== 酒店客房管理系统 启动中 ==========");
        ConfigurableApplicationContext context = SpringApplication.run(HotelApplication.class, args);
        log.info("========== 酒店客房管理系统 启动完成 ==========");

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = context.getBean(LoginFrame.class);
            loginFrame.setVisible(true);

            new Thread(() -> {
                while (loginFrame.isVisible()) {
                    try { Thread.sleep(200); } catch (InterruptedException ignored) {}
                }
                User currentUser = loginFrame.getCurrentUser();
                if (currentUser != null) {
                    SwingUtilities.invokeLater(() -> {
                        MainFrame mainFrame = context.getBean(MainFrame.class);
                        mainFrame.init(currentUser);
                    });
                } else {
                    log.info("用户取消登录，系统退出");
                    context.close();
                    System.exit(0);
                }
            }).start();
        });
    }
}