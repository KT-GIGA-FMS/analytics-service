package com.kt_giga_fms.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsSummaryResponse {
    
    // 전체 통계
    private OverallStatistics overall;
    
    // 시간대별 운행 건수 (0-23시)
    private Map<Integer, Integer> hourlyTripCounts;
    
    // 요일별 운행 건수 (월-일)
    private Map<String, Integer> dailyTripCounts;
    
    // 월별 운행 건수 (1-12월)
    private Map<Integer, Integer> monthlyTripCounts;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OverallStatistics {
        private Long totalTripCount;        // 전체 운행 건수
        private BigDecimal totalDistance;   // 전체 운행 거리 (km)
        private Long totalDuration;         // 전체 운행 시간 (분)
        private BigDecimal averageDistance; // 평균 운행 거리 (km)
        private Long averageDuration;       // 평균 운행 시간 (분)
    }
}
