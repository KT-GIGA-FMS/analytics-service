package com.kt_giga_fms.analytics.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "monthly_statistics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyStatistics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "year", nullable = false)
    private Integer year;
    
    @Column(name = "month", nullable = false)
    private Integer month;
    
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
    
    // 복합 유니크 키를 위한 메서드
    public String getYearMonth() {
        return year + "-" + String.format("%02d", month);
    }
}


