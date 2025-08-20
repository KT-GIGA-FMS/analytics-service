package com.kt_giga_fms.analytics.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "trip_records")
public class TripRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "vehicle_id", nullable = false)
    private String vehicleId;
    
    @Column(name = "vehicle_name")
    private String vehicleName;
    
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;
    
    @Column(name = "total_distance", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalDistance;
    
    @Column(name = "start_latitude", nullable = false)
    private Double startLatitude;
    
    @Column(name = "start_longitude", nullable = false)
    private Double startLongitude;
    
    @Column(name = "end_latitude", nullable = false)
    private Double endLatitude;
    
    @Column(name = "end_longitude", nullable = false)
    private Double endLongitude;
    
    @Column(name = "fuel_consumed", precision = 8, scale = 2)
    private BigDecimal fuelConsumed;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    // Constructor
    public TripRecord() {
        this.createdAt = LocalDateTime.now();
    }
    
    public TripRecord(String vehicleId, String vehicleName, LocalDateTime startTime, 
                     LocalDateTime endTime, BigDecimal totalDistance, 
                     Double startLatitude, Double startLongitude,
                     Double endLatitude, Double endLongitude, BigDecimal fuelConsumed) {
        this();
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
    
    // Getters
    public Long getId() { return id; }
    public String getVehicleId() { return vehicleId; }
    public String getVehicleName() { return vehicleName; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public BigDecimal getTotalDistance() { return totalDistance; }
    public Double getStartLatitude() { return startLatitude; }
    public Double getStartLongitude() { return startLongitude; }
    public Double getEndLatitude() { return endLatitude; }
    public Double getEndLongitude() { return endLongitude; }
    public BigDecimal getFuelConsumed() { return fuelConsumed; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
