package com.hotel.service.impl;

import com.hotel.common.exception.BusinessException;
import com.hotel.common.util.ValidationUtil;
import com.hotel.dao.CustomerDao;
import com.hotel.model.entity.Customer;
import com.hotel.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerDao customerDao;

    public CustomerServiceImpl(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @Override
    public Customer findById(Integer customerId) {
        log.debug("查询客户: customerId={}", customerId);
        Customer customer = customerDao.findById(customerId);
        if (customer == null) {
            throw new BusinessException("CUS_001", "客户不存在: customerId=" + customerId);
        }
        return customer;
    }

    @Override
    public Customer findByIdNumber(String idNumber) {
        log.debug("按身份证号查询客户: idNumber={}", idNumber);
        return customerDao.findByIdNumber(idNumber);
    }

    @Override
    public List<Customer> findAll() {
        log.debug("查询所有客户");
        return customerDao.findAll();
    }

    @Override
    public List<Customer> findByName(String name) {
        log.debug("按姓名查询客户: name={}", name);
        return customerDao.findByName(name);
    }

    @Override
    public void addCustomer(Customer customer) {
        validateCustomer(customer);
        Customer existing = customerDao.findByIdNumber(customer.getIdNumber());
        if (existing != null) {
            log.info("客户已存在，更新信息: customerId={}", existing.getCustomerId());
            customer.setCustomerId(existing.getCustomerId());
            customerDao.update(customer);
            return;
        }
        log.info("新增客户: name={}, phone={}", customer.getCustomerName(), customer.getPhone());
        customerDao.insert(customer);
    }

    @Override
    public void updateCustomer(Customer customer) {
        findById(customer.getCustomerId());
        validateCustomer(customer);
        log.info("更新客户: customerId={}", customer.getCustomerId());
        customerDao.update(customer);
    }

    @Override
    public void deleteCustomer(Integer customerId) {
        findById(customerId);
        log.info("删除客户: customerId={}", customerId);
        customerDao.delete(customerId);
    }

    private void validateCustomer(Customer customer) {
        if (customer.getCustomerName() == null || customer.getCustomerName().isBlank()) {
            throw new BusinessException("CUS_002", "客户姓名不能为空");
        }
        if (customer.getIdNumber() == null || customer.getIdNumber().isBlank()) {
            throw new BusinessException("CUS_003", "身份证号不能为空");
        }
        if (!ValidationUtil.isValidIdNumber(customer.getIdNumber())) {
            throw new BusinessException("CUS_004", "身份证号格式不正确");
        }
        if (customer.getPhone() == null || !ValidationUtil.isValidPhone(customer.getPhone())) {
            throw new BusinessException("CUS_005", "手机号格式不正确");
        }
    }
}