package com.hotel.model.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BillDetailVO {
    private Integer billId;
    private Integer checkinId;
    private BigDecimal roomCharge;
    private BigDecimal extraCharge;
    private BigDecimal depositPaid;
    private BigDecimal totalAmount;
    private BigDecimal refund;
    private String payMethod;
    private LocalDateTime billTime;
    private String customerName;
    private String roomNumber;
    private String typeName;
    private LocalDateTime checkinTime;
    private LocalDateTime checkoutTime;
    private String operatorName;

    public BillDetailVO() {}

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
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
    public LocalDateTime getCheckinTime() { return checkinTime; }
    public void setCheckinTime(LocalDateTime checkinTime) { this.checkinTime = checkinTime; }
    public LocalDateTime getCheckoutTime() { return checkoutTime; }
    public void setCheckoutTime(LocalDateTime checkoutTime) { this.checkoutTime = checkoutTime; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
}