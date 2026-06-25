package com.hotel.model.dto;

public class CheckoutRequest {
    private Integer checkinId;
    private String payMethod;
    private Integer userId;

    public CheckoutRequest() {}

    public Integer getCheckinId() { return checkinId; }
    public void setCheckinId(Integer checkinId) { this.checkinId = checkinId; }
    public String getPayMethod() { return payMethod; }
    public void setPayMethod(String payMethod) { this.payMethod = payMethod; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
}