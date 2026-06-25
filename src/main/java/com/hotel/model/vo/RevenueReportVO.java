package com.hotel.model.vo;

import java.math.BigDecimal;

public class RevenueReportVO {
    private String reportDate;
    private Integer checkoutCount;
    private BigDecimal roomChargeTotal;
    private BigDecimal extraChargeTotal;
    private BigDecimal totalRevenue;
    private BigDecimal avgRevenuePerRoom;
    private Integer totalRooms;
    private Integer occupiedRooms;
    private BigDecimal occupancyRate;

    public RevenueReportVO() {}

    public String getReportDate() { return reportDate; }
    public void setReportDate(String reportDate) { this.reportDate = reportDate; }
    public Integer getCheckoutCount() { return checkoutCount; }
    public void setCheckoutCount(Integer checkoutCount) { this.checkoutCount = checkoutCount; }
    public BigDecimal getRoomChargeTotal() { return roomChargeTotal; }
    public void setRoomChargeTotal(BigDecimal roomChargeTotal) { this.roomChargeTotal = roomChargeTotal; }
    public BigDecimal getExtraChargeTotal() { return extraChargeTotal; }
    public void setExtraChargeTotal(BigDecimal extraChargeTotal) { this.extraChargeTotal = extraChargeTotal; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    public BigDecimal getAvgRevenuePerRoom() { return avgRevenuePerRoom; }
    public void setAvgRevenuePerRoom(BigDecimal avgRevenuePerRoom) { this.avgRevenuePerRoom = avgRevenuePerRoom; }
    public Integer getTotalRooms() { return totalRooms; }
    public void setTotalRooms(Integer totalRooms) { this.totalRooms = totalRooms; }
    public Integer getOccupiedRooms() { return occupiedRooms; }
    public void setOccupiedRooms(Integer occupiedRooms) { this.occupiedRooms = occupiedRooms; }
    public BigDecimal getOccupancyRate() { return occupancyRate; }
    public void setOccupancyRate(BigDecimal occupancyRate) { this.occupancyRate = occupancyRate; }
}