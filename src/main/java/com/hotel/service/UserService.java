package com.hotel.service;

import com.hotel.model.entity.User;

import java.util.List;

public interface UserService {

    User login(String username, String password);

    User findById(Integer userId);

    List<User> findAll();

    void addUser(User user);

    void updateUser(User user);

    void updatePassword(Integer userId, String oldPassword, String newPassword);

    void updateStatus(Integer userId, String status);
}