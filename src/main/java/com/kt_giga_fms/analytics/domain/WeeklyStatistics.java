package com.kt_giga_fms.analytics.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "weekly_statistics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyStatistics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "week_start_date", nullable = false)
    private LocalDate weekStartDate;
    
    @Column(name = "week_end_date", nullable = false)
    private LocalDate weekEndDate;
    
    @Column(name = "week_number")
    private Integer weekNumber;
    
    @Column(name = "year")
    private Integer year;
    
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
