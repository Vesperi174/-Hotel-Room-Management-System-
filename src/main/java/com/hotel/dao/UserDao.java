package com.hotel.dao;

import com.hotel.model.entity.User;
import java.util.List;

public interface UserDao {
    User findById(Integer userId);
    User findByUsername(String username);
    List<User> findAll();
    int insert(User user);
    int update(User user);
    int updatePassword(Integer userId, String newPassword);
    int updateStatus(Integer userId, String status);
    int delete(Integer userId);
}