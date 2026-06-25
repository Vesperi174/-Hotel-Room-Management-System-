package com.hotel.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Bill {
    private Integer billId;
    private Integer checkinId;
    private BigDecimal roomCharge;
    private BigDecimal extraCharge;
    private BigDecimal depositPaid;
    private BigDecimal totalAmount;
    private BigDecimal refund;
    private String payMethod;
    private LocalDateTime billTime;
    private Integer userId;

    public Bill() {}

    public Integer getBillId() { return billId; }
    public void setBillId(Integer billId) { this.billId = billId; }
    public Integer getCheckinId() { return checkinId; }
    public void setCheckinId(Integer checkinId) { this.checkinId = checkinId; }
    public BigDecimal getRoomCharge() { return roomCharge; }
    public void setRoomCharge(BigDecimal roomCharge) { this.roomCharge = roomCharge; }
    public BigDecimal getExtraCharge() { return extraCharge; }
    public void setExtraCharge(BigDecimal extraCharge) { this.extraCharge = extraCharge; }
    public BigDecimal getDepositPaid() { return depositPaid; }
    public void setDepositPaid(BigDecimal depositPaid) { this.depositPaid = depositPaid; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public BigDecimal getRefund() { return refund; }
    public void setRefund(BigDecimal refund) { this.refund = refund; }
    public String getPayMethod() { return payMethod; }
    public void setPayMethod(String payMethod) { this.payMethod = payMethod; }
    public LocalDateTime getBillTime() { return billTime; }
    public void setBillTime(LocalDateTime billTime) { this.billTime = billTime; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    @Override
    public String toString() {
        return "Bill{billId=" + billId + ", totalAmount=" + totalAmount + "}";
    }
}