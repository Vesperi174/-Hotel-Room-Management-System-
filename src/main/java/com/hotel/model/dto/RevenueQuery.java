package com.hotel.model.dto;

import java.time.LocalDate;

public class RevenueQuery {
    private LocalDate startDate;
    private LocalDate endDate;

    public RevenueQuery() {}

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}