package com.kt_giga_fms.analytics.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "daily_statistics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyStatistics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "stat_date", nullable = false, unique = true)
    private LocalDate statDate;
    
    @Column(name = "trip_count")
    private Integer tripCount;
    
    @Column(name = "total_distance", precision = 10, scale = 2)
    private BigDecimal totalDistance;
    
    @Column(name = "total_duration")
    private Long totalDuration;
    
    @Column(name = "average_distance", precision = 8, scale = 2)
    private BigDecimal averageDistance;
    
    @Column(name = "average_duration")
    private Long averageDuration;
    
    @Column(name = "created_at")
    private LocalDate createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
    }
}


