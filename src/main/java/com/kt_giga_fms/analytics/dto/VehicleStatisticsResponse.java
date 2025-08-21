package com.kt_giga_fms.analytics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "차량 통계 응답 DTO")
public class VehicleStatisticsResponse {
    
    @Schema(description = "차량 ID", example = "CAR001")
    private String vehicleId;
    
    @Schema(description = "차량명", example = "현대 아반떼")
    private String vehicleName;
    
    @Schema(description = "총 운행 거리 (km)", example = "1500.5")
    private BigDecimal totalDistance;
    
    @Schema(description = "월간 운행 거리 (km)", example = "150.5")
    private BigDecimal monthlyDistance;
    
    @Schema(description = "년월", example = "2024-01")
    private String yearMonth;
    
    @Schema(description = "총 운행 횟수", example = "25")
    private Integer totalTrips;
    
    @Schema(description = "월간 운행 횟수", example = "3")
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
