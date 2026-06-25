package com.hotel.model.entity;

import java.time.LocalDateTime;

public class Customer {
    private Integer customerId;
    private String customerName;
    private String idNumber;
    private String phone;
    private String gender;
    private String address;
    private LocalDateTime createTime;

    public Customer() {}

    public Customer(Integer customerId, String customerName, String idNumber, String phone) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.idNumber = idNumber;
        this.phone = phone;
    }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    @Override
    public String toString() {
        return "Customer{customerId=" + customerId + ", customerName='" + customerName + "'}";
    }
}