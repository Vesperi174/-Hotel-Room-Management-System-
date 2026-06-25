package com.hotel.service.impl;

import com.hotel.common.exception.BusinessException;
import com.hotel.dao.UserDao;
import com.hotel.model.entity.User;
import com.hotel.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User login(String username, String password) {
        log.info("用户登录: username={}", username);
        if (username == null || username.isBlank()) {
            throw new BusinessException("USR_001", "用户名不能为空");
        }
        if (password == null || password.isBlank()) {
            throw new BusinessException("USR_002", "密码不能为空");
        }

        User user = userDao.findByUsername(username);
        if (user == null) {
            throw new BusinessException("USR_003", "用户名或密码错误");
        }
        if (!"正常".equals(user.getStatus())) {
            throw new BusinessException("USR_004", "账号已被禁用");
        }

        if (!password.equals(user.getPassword())) {
            throw new BusinessException("USR_003", "用户名或密码错误");
        }

        log.info("登录成功: userId={}, realName={}", user.getUserId(), user.getRealName());
        return user;
    }

    @Override
    public User findById(Integer userId) {
        log.debug("查询用户: userId={}", userId);
        User user = userDao.findById(userId);
        if (user == null) {
            throw new BusinessException("USR_005", "用户不存在: userId=" + userId);
        }
        return user;
    }

    @Override
    public List<User> findAll() {
        log.debug("查询所有用户");
        return userDao.findAll();
    }

    @Override
    public void addUser(User user) {
        validateUser(user);
        User existing = userDao.findByUsername(user.getUsername());
        if (existing != null) {
            throw new BusinessException("USR_006", "用户名已存在: " + user.getUsername());
        }
        log.info("新增用户: username={}", user.getUsername());
        userDao.insert(user);
    }

    @Override
    public void updateUser(User user) {
        findById(user.getUserId());
        log.info("更新用户: userId={}", user.getUserId());
        userDao.update(user);
    }

    @Override
    public void updatePassword(Integer userId, String oldPassword, String newPassword) {
        User user = findById(userId);
        if (!oldPassword.equals(user.getPassword())) {
            throw new BusinessException("USR_007", "原密码错误");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new BusinessException("USR_008", "新密码长度不能少于6位");
        }
        log.info("修改密码: userId={}", userId);
        userDao.updatePassword(userId, newPassword);
    }

    @Override
    public void updateStatus(Integer userId, String status) {
        findById(userId);
        log.info("更新用户状态: userId={}, status={}", userId, status);
        userDao.updateStatus(userId, status);
    }

    private void validateUser(User user) {
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new BusinessException("USR_009", "用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new BusinessException("USR_010", "密码不能为空");
        }
        if (user.getRealName() == null || user.getRealName().isBlank()) {
            throw new BusinessException("USR_011", "真实姓名不能为空");
        }
        if (user.getRoleId() == null) {
            throw new BusinessException("USR_012", "角色不能为空");
        }
    }
}