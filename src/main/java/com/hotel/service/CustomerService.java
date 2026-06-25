package com.hotel.service;

import com.hotel.model.entity.Customer;

import java.util.List;

public interface CustomerService {

    Customer findById(Integer customerId);

    Customer findByIdNumber(String idNumber);

    List<Customer> findAll();

    List<Customer> findByName(String name);

    void addCustomer(Customer customer);

    void updateCustomer(Customer customer);

    void deleteCustomer(Integer customerId);
}