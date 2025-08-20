package com.kt_giga_fms.analytics.dto;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public class TripRecordDto {
    private String vehicleId;
    private String vehicleName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal totalDistance;
    private Double startLatitude;
    private Double startLongitude;
    private Double endLatitude;
    private Double endLongitude;
    private BigDecimal fuelConsumed;
    
    // Constructor
    public TripRecordDto() {}
    
    public TripRecordDto(String vehicleId, String vehicleName, LocalDateTime startTime, 
                        LocalDateTime endTime, BigDecimal totalDistance, 
                        Double startLatitude, Double startLongitude,
                        Double endLatitude, Double endLongitude, BigDecimal fuelConsumed) {
        this.vehicleId = vehicleId;
        this.vehicleName = vehicleName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalDistance = totalDistance;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
        this.fuelConsumed = fuelConsumed;
    }
    
    // Getters and Setters
    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
    
    public String getVehicleName() { return vehicleName; }
    public void setVehicleName(String vehicleName) { this.vehicleName = vehicleName; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public BigDecimal getTotalDistance() { return totalDistance; }
    public void setTotalDistance(BigDecimal totalDistance) { this.totalDistance = totalDistance; }
    
    public Double getStartLatitude() { return startLatitude; }
    public void setStartLatitude(Double startLatitude) { this.startLatitude = startLatitude; }
    
    public Double getStartLongitude() { return startLongitude; }
    public void setStartLongitude(Double startLongitude) { this.startLongitude = startLongitude; }
    
    public Double getEndLatitude() { return endLatitude; }
    public void setEndLatitude(Double endLatitude) { this.endLatitude = endLatitude; }
    
    public Double getEndLongitude() { return endLongitude; }
    public void setEndLongitude(Double endLongitude) { this.endLongitude = endLongitude; }
    
    public BigDecimal getFuelConsumed() { return fuelConsumed; }
    public void setFuelConsumed(BigDecimal fuelConsumed) { this.fuelConsumed = fuelConsumed; }
}
