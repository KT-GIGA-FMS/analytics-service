package com.kt_giga_fms.analytics.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "vehicle_statistics")
public class VehicleStatistics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "vehicle_id", nullable = false)
    private String vehicleId;
    
    @Column(name = "vehicle_name")
    private String vehicleName;
    
    @Column(name = "total_distance", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalDistance;
    
    @Column(name = "monthly_distance", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyDistance;
    
    @Column(name = "year_month", nullable = false)
    private String yearMonth; // YYYY-MM 형식
    
    @Column(name = "total_trips")
    private Integer totalTrips;
    
    @Column(name = "monthly_trips")
    private Integer monthlyTrips;
    
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;
    
    // Constructor
    public VehicleStatistics() {
        this.lastUpdated = LocalDateTime.now();
    }
    
    public VehicleStatistics(String vehicleId, String vehicleName, BigDecimal totalDistance, 
                           BigDecimal monthlyDistance, String yearMonth, 
                           Integer totalTrips, Integer monthlyTrips) {
        this();
        this.vehicleId = vehicleId;
        this.vehicleName = vehicleName;
        this.totalDistance = totalDistance;
        this.monthlyDistance = monthlyDistance;
        this.yearMonth = yearMonth;
        this.totalTrips = totalTrips;
        this.monthlyTrips = monthlyTrips;
    }
    
    // Getters
    public Long getId() { return id; }
    public String getVehicleId() { return vehicleId; }
    public String getVehicleName() { return vehicleName; }
    public BigDecimal getTotalDistance() { return totalDistance; }
    public BigDecimal getMonthlyDistance() { return monthlyDistance; }
    public String getYearMonth() { return yearMonth; }
    public Integer getTotalTrips() { return totalTrips; }
    public Integer getMonthlyTrips() { return monthlyTrips; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    
    // Update methods (no setters)
    public void updateStatistics(BigDecimal newTotalDistance, BigDecimal newMonthlyDistance, 
                               Integer newTotalTrips, Integer newMonthlyTrips) {
        this.totalDistance = newTotalDistance;
        this.monthlyDistance = newMonthlyDistance;
        this.totalTrips = newTotalTrips;
        this.monthlyTrips = newMonthlyTrips;
        this.lastUpdated = LocalDateTime.now();
    }
}
