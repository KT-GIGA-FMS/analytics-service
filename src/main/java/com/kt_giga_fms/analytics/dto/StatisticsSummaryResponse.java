package com.kt_giga_fms.analytics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "통계 요약 응답 DTO")
public class StatisticsSummaryResponse {
    
    @Schema(description = "전체 통계 정보")
    private OverallStatistics overall;
    
    @Schema(description = "시간대별 운행 건수 (0-23시)", example = "{\"9\": 15, \"10\": 20, \"17\": 25}")
    private Map<Integer, Integer> hourlyTripCounts;
    
    @Schema(description = "요일별 운행 건수 (월-일)", example = "{\"MONDAY\": 50, \"TUESDAY\": 45, \"WEDNESDAY\": 55}")
    private Map<String, Integer> dailyTripCounts;
    
    @Schema(description = "월별 운행 건수 (1-12월)", example = "{\"1\": 150, \"2\": 180, \"3\": 200}")
    private Map<Integer, Integer> monthlyTripCounts;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "전체 통계 정보")
    public static class OverallStatistics {
        
        @Schema(description = "전체 운행 건수", example = "1500")
        private Long totalTripCount;
        
        @Schema(description = "전체 운행 거리 (km)", example = "15000.5")
        private BigDecimal totalDistance;
        
        @Schema(description = "전체 운행 시간 (분)", example = "45000")
        private Long totalDuration;
        
        @Schema(description = "평균 운행 거리 (km)", example = "10.0")
        private BigDecimal averageDistance;
        
        @Schema(description = "평균 운행 시간 (분)", example = "30")
        private Long averageDuration;
    }
}


