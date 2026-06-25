package com.hotel.controller;

import com.hotel.common.exception.BusinessException;
import com.hotel.model.entity.User;
import com.hotel.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public Result<User> login(String username, String password) {
        try {
            User user = userService.login(username, password);
            return Result.success(user, "登录成功");
        } catch (BusinessException e) {
            log.warn("登录失败: {}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    public Result<User> getUserById(Integer userId) {
        try {
            User user = userService.findById(userId);
            return Result.success(user);
        } catch (BusinessException e) {
            log.warn("查询用户失败: {}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    public Result<List<User>> getAllUsers() {
        try {
            List<User> users = userService.findAll();
            return Result.success(users);
        } catch (Exception e) {
            log.error("查询所有用户失败", e);
            return Result.fail("查询用户列表失败");
        }
    }

    public Result<Void> addUser(User user) {
        try {
            userService.addUser(user);
            return Result.success(null, "新增用户成功");
        } catch (BusinessException e) {
            log.warn("新增用户失败: {}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    public Result<Void> updateUser(User user) {
        try {
            userService.updateUser(user);
            return Result.success(null, "更新用户信息成功");
        } catch (BusinessException e) {
            log.warn("更新用户失败: {}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    public Result<Void> updatePassword(Integer userId, String oldPassword, String newPassword) {
        try {
            userService.updatePassword(userId, oldPassword, newPassword);
            return Result.success(null, "修改密码成功");
        } catch (BusinessException e) {
            log.warn("修改密码失败: {}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    public Result<Void> updateUserStatus(Integer userId, String status) {
        try {
            userService.updateStatus(userId, status);
            return Result.success(null, "用户状态已更新");
        } catch (BusinessException e) {
            log.warn("更新用户状态失败: {}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }
}