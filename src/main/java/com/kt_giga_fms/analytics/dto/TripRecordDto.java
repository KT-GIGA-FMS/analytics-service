package com.kt_giga_fms.analytics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Schema(description = "운행 기록 DTO")
public class TripRecordDto {
    
    @Schema(description = "차량 ID", example = "CAR001", required = true)
    private String vehicleId;
    
    @Schema(description = "차량명", example = "현대 아반떼")
    private String vehicleName;
    
    @Schema(description = "운행 시작 시간", example = "2024-01-15T09:00:00")
    private LocalDateTime startTime;
    
    @Schema(description = "운행 종료 시간", example = "2024-01-15T17:00:00")
    private LocalDateTime endTime;
    
    @Schema(description = "총 운행 거리 (km)", example = "150.5")
    private BigDecimal totalDistance;
    
    @Schema(description = "시작 위도", example = "37.5665")
    private Double startLatitude;
    
    @Schema(description = "시작 경도", example = "126.9780")
    private Double startLongitude;
    
    @Schema(description = "종료 위도", example = "37.5665")
    private Double endLatitude;
    
    @Schema(description = "종료 경도", example = "126.9780")
    private Double endLongitude;
    
    @Schema(description = "연료 소비량 (L)", example = "12.5")
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
