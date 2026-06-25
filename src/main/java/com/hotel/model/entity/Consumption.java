package com.hotel.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Consumption {
    private Integer consId;
    private Integer checkinId;
    private String itemName;
    private BigDecimal itemPrice;
    private Integer quantity;
    private LocalDateTime consTime;
    private String remark;

    public Consumption() {}

    public Integer getConsId() { return consId; }
    public void setConsId(Integer consId) { this.consId = consId; }
    public Integer getCheckinId() { return checkinId; }
    public void setCheckinId(Integer checkinId) { this.checkinId = checkinId; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public BigDecimal getItemPrice() { return itemPrice; }
    public void setItemPrice(BigDecimal itemPrice) { this.itemPrice = itemPrice; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public LocalDateTime getConsTime() { return consTime; }
    public void setConsTime(LocalDateTime consTime) { this.consTime = consTime; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    @Override
    public String toString() {
        return "Consumption{consId=" + consId + ", itemName='" + itemName + "'}";
    }
}