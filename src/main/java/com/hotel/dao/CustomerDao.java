package com.hotel.dao;

import com.hotel.model.entity.Customer;
import java.util.List;

public interface CustomerDao {
    Customer findById(Integer customerId);
    Customer findByIdNumber(String idNumber);
    List<Customer> findAll();
    List<Customer> findByName(String name);
    int insert(Customer customer);
    int update(Customer customer);
    int delete(Integer customerId);
}