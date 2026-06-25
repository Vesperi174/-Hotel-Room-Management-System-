package com.hotel.controller;

import com.hotel.common.exception.BusinessException;
import com.hotel.model.entity.Customer;
import com.hotel.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    public Result<Customer> getCustomerById(Integer customerId) {
        try {
            Customer customer = customerService.findById(customerId);
            return Result.success(customer);
        } catch (BusinessException e) {
            log.warn("查询客户失败: {}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    public Result<Customer> getCustomerByIdNumber(String idNumber) {
        try {
            Customer customer = customerService.findByIdNumber(idNumber);
            if (customer == null) {
                return Result.fail("未找到该客户");
            }
            return Result.success(customer);
        } catch (Exception e) {
            log.error("按身份证号查询客户失败", e);
            return Result.fail("查询客户失败");
        }
    }

    public Result<List<Customer>> getAllCustomers() {
        try {
            List<Customer> customers = customerService.findAll();
            return Result.success(customers);
        } catch (Exception e) {
            log.error("查询所有客户失败", e);
            return Result.fail("查询客户列表失败");
        }
    }

    public Result<List<Customer>> searchCustomersByName(String name) {
        try {
            List<Customer> customers = customerService.findByName(name);
            return Result.success(customers);
        } catch (Exception e) {
            log.error("按姓名搜索客户失败", e);
            return Result.fail("搜索客户失败");
        }
    }

    public Result<Void> addCustomer(Customer customer) {
        try {
            customerService.addCustomer(customer);
            return Result.success(null, "新增客户成功");
        } catch (BusinessException e) {
            log.warn("新增客户失败: {}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    public Result<Void> updateCustomer(Customer customer) {
        try {
            customerService.updateCustomer(customer);
            return Result.success(null, "更新客户信息成功");
        } catch (BusinessException e) {
            log.warn("更新客户失败: {}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    public Result<Void> deleteCustomer(Integer customerId) {
        try {
            customerService.deleteCustomer(customerId);
            return Result.success(null, "删除客户成功");
        } catch (BusinessException e) {
            log.warn("删除客户失败: {}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }
}