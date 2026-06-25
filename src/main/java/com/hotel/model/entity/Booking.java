package com.hotel.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Booking {
    private Integer bookingId;
    private Integer customerId;
    private Integer roomId;
    private LocalDateTime bookingDate;
    private LocalDate expectedArrival;
    private LocalDate expectedLeave;
    private String bookingStatus;
    private BigDecimal depositPaid;
    private String remark;

    private Customer customer;
    private Room room;

    public Booking() {}

    public Integer getBookingId() { return bookingId; }
    public void setBookingId(Integer bookingId) { this.bookingId = bookingId; }
    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
    public Integer getRoomId() { return roomId; }
    public void setRoomId(Integer roomId) { this.roomId = roomId; }
    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }
    public LocalDate getExpectedArrival() { return expectedArrival; }
    public void setExpectedArrival(LocalDate expectedArrival) { this.expectedArrival = expectedArrival; }
    public LocalDate getExpectedLeave() { return expectedLeave; }
    public void setExpectedLeave(LocalDate expectedLeave) { this.expectedLeave = expectedLeave; }
    public String getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }
    public BigDecimal getDepositPaid() { return depositPaid; }
    public void setDepositPaid(BigDecimal depositPaid) { this.depositPaid = depositPaid; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    @Override
    public String toString() {
        return "Booking{bookingId=" + bookingId + ", status='" + bookingStatus + "'}";
    }
}