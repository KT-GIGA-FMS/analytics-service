package com.kt_giga_fms.analytics.dto;

import java.math.BigDecimal;

public class VehicleStatisticsResponse {
    private String vehicleId;
    private String vehicleName;
    private BigDecimal totalDistance;
    private BigDecimal monthlyDistance;
    private String yearMonth;
    private Integer totalTrips;
    private Integer monthlyTrips;
    
    // Constructor
    public VehicleStatisticsResponse() {}
    
    public VehicleStatisticsResponse(String vehicleId, String vehicleName, BigDecimal totalDistance, 
                                   BigDecimal monthlyDistance, String yearMonth, 
                                   Integer totalTrips, Integer monthlyTrips) {
        this.vehicleId = vehicleId;
        this.vehicleName = vehicleName;
        this.totalDistance = totalDistance;
        this.monthlyDistance = monthlyDistance;
        this.yearMonth = yearMonth;
        this.totalTrips = totalTrips;
        this.monthlyTrips = monthlyTrips;
    }
    
    // Getters and Setters
    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
    
    public String getVehicleName() { return vehicleName; }
    public void setVehicleName(String vehicleName) { this.vehicleName = vehicleName; }
    
    public BigDecimal getTotalDistance() { return totalDistance; }
    public void setTotalDistance(BigDecimal totalDistance) { this.totalDistance = totalDistance; }
    
    public BigDecimal getMonthlyDistance() { return monthlyDistance; }
    public void setMonthlyDistance(BigDecimal monthlyDistance) { this.monthlyDistance = monthlyDistance; }
    
    public String getYearMonth() { return yearMonth; }
    public void setYearMonth(String yearMonth) { this.yearMonth = yearMonth; }
    
    public Integer getTotalTrips() { return totalTrips; }
    public void setTotalTrips(Integer totalTrips) { this.totalTrips = totalTrips; }
    
    public Integer getMonthlyTrips() { return monthlyTrips; }
    public void setMonthlyTrips(Integer monthlyTrips) { this.monthlyTrips = monthlyTrips; }
}
