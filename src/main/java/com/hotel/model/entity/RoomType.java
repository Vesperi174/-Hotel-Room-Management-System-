package com.hotel.model.entity;

import java.math.BigDecimal;

public class RoomType {
    private Integer typeId;
    private String typeName;
    private String bedType;
    private BigDecimal area;
    private BigDecimal basePrice;
    private Integer capacity;
    private String description;

    public RoomType() {}

    public RoomType(Integer typeId, String typeName, String bedType, BigDecimal basePrice, Integer capacity) {
        this.typeId = typeId;
        this.typeName = typeName;
        this.bedType = bedType;
        this.basePrice = basePrice;
        this.capacity = capacity;
    }

    public Integer getTypeId() { return typeId; }
    public void setTypeId(Integer typeId) { this.typeId = typeId; }
    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
    public String getBedType() { return bedType; }
    public void setBedType(String bedType) { this.bedType = bedType; }
    public BigDecimal getArea() { return area; }
    public void setArea(BigDecimal area) { this.area = area; }
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "RoomType{typeId=" + typeId + ", typeName='" + typeName + "'}";
    }
}