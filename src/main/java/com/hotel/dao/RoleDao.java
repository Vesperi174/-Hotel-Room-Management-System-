package com.hotel.dao;

import com.hotel.model.entity.Role;
import java.util.List;

public interface RoleDao {
    Role findById(Integer roleId);
    List<Role> findAll();
    int insert(Role role);
    int update(Role role);
    int delete(Integer roleId);
}