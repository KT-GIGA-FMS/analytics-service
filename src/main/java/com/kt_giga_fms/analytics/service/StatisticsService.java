package com.kt_giga_fms.analytics.service;

import com.kt_giga_fms.analytics.dto.StatisticsSummaryResponse;
import com.kt_giga_fms.analytics.domain.TripRecord;
import com.kt_giga_fms.analytics.domain.DailyStatistics;
import com.kt_giga_fms.analytics.domain.MonthlyStatistics;
import com.kt_giga_fms.analytics.repository.TripRecordRepository;
import com.kt_giga_fms.analytics.repository.DailyStatisticsRepository;
import com.kt_giga_fms.analytics.repository.MonthlyStatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final TripRecordRepository tripRecordRepository;
    private final DailyStatisticsRepository dailyStatisticsRepository;
    private final MonthlyStatisticsRepository monthlyStatisticsRepository;

    public StatisticsSummaryResponse getStatisticsSummary() {
        log.info("통계 요약 데이터 조회 시작");
        
        try {
            // 전체 통계 계산
            StatisticsSummaryResponse.OverallStatistics overall = calculateOverallStatistics();
            
            // 시간대별 통계 계산
            Map<Integer, Integer> hourlyCounts = calculateHourlyTripCounts();
            
            // 요일별 통계 계산
            Map<String, Integer> dailyCounts = calculateDailyTripCounts();
            
            // 월별 통계 계산
            Map<Integer, Integer> monthlyCounts = calculateMonthlyTripCounts();
            
            return StatisticsSummaryResponse.builder()
                .overall(overall)
                .hourlyTripCounts(hourlyCounts)
                .dailyTripCounts(dailyCounts)
                .monthlyTripCounts(monthlyCounts)
                .build();
                
        } catch (Exception e) {
            log.error("통계 요약 데이터 조회 실패: {}", e.getMessage(), e);
            throw new RuntimeException("통계 데이터 조회에 실패했습니다", e);
        }
    }

    private StatisticsSummaryResponse.OverallStatistics calculateOverallStatistics() {
        List<TripRecord> allTrips = tripRecordRepository.findAll();
        
        if (allTrips.isEmpty()) {
            return StatisticsSummaryResponse.OverallStatistics.builder()
                .totalTripCount(0L)
                .totalDistance(BigDecimal.ZERO)
                .totalDuration(0L)
                .averageDistance(BigDecimal.ZERO)
                .averageDuration(0L)
                .build();
        }
        
        long totalCount = allTrips.size();
        BigDecimal totalDistance = allTrips.stream()
            .map(TripRecord::getTotalDistance)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        long totalDuration = allTrips.stream()
            .mapToLong(trip -> {
                if (trip.getStartTime() != null && trip.getEndTime() != null) {
                    return java.time.Duration.between(trip.getStartTime(), trip.getEndTime()).toMinutes();
                }
                return 0L;
            })
            .sum();
        
        BigDecimal averageDistance = totalCount > 0 ? 
            totalDistance.divide(BigDecimal.valueOf(totalCount), 2, RoundingMode.HALF_UP) : 
            BigDecimal.ZERO;
        long averageDuration = totalCount > 0 ? totalDuration / totalCount : 0L;
        
        return StatisticsSummaryResponse.OverallStatistics.builder()
            .totalTripCount(totalCount)
            .totalDistance(totalDistance)
            .totalDuration(totalDuration)
            .averageDistance(averageDistance)
            .averageDuration(averageDuration)
            .build();
    }

    private Map<Integer, Integer> calculateHourlyTripCounts() {
        Map<Integer, Integer> hourlyCounts = new HashMap<>();
        
        // 0-23시 초기화
        for (int hour = 0; hour < 24; hour++) {
            hourlyCounts.put(hour, 0);
        }
        
        List<TripRecord> allTrips = tripRecordRepository.findAll();
        
        allTrips.forEach(trip -> {
            if (trip.getStartTime() != null) {
                int hour = trip.getStartTime().getHour();
                hourlyCounts.put(hour, hourlyCounts.get(hour) + 1);
            }
        });
        
        return hourlyCounts;
    }

    private Map<String, Integer> calculateDailyTripCounts() {
        Map<String, Integer> dailyCounts = new LinkedHashMap<>();
        
        // 요일별 초기화 (월-일)
        for (DayOfWeek day : DayOfWeek.values()) {
            String dayName = day.getDisplayName(TextStyle.SHORT, Locale.KOREAN);
            dailyCounts.put(dayName, 0);
        }
        
        List<TripRecord> allTrips = tripRecordRepository.findAll();
        
        allTrips.forEach(trip -> {
            if (trip.getStartTime() != null) {
                DayOfWeek dayOfWeek = trip.getStartTime().getDayOfWeek();
                String dayName = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN);
                dailyCounts.put(dayName, dailyCounts.get(dayName) + 1);
            }
        });
        
        return dailyCounts;
    }

    private Map<Integer, Integer> calculateMonthlyTripCounts() {
        Map<Integer, Integer> monthlyCounts = new LinkedHashMap<>();
        
        // 1-12월 초기화
        for (int month = 1; month <= 12; month++) {
            monthlyCounts.put(month, 0);
        }
        
        List<TripRecord> allTrips = tripRecordRepository.findAll();
        
        allTrips.forEach(trip -> {
            if (trip.getStartTime() != null) {
                int month = trip.getStartTime().getMonthValue();
                monthlyCounts.put(month, monthlyCounts.get(month) + 1);
            }
        });
        
        return monthlyCounts;
    }
    
    // 캐시된 통계 데이터 사용 (더 빠른 응답)
    public StatisticsSummaryResponse getStatisticsSummaryFromCache() {
        log.info("캐시된 통계 요약 데이터 조회 시작");
        
        try {
            // 전체 통계는 여전히 실시간 계산
            StatisticsSummaryResponse.OverallStatistics overall = calculateOverallStatistics();
            
            // 시간대별 통계는 실시간 계산
            Map<Integer, Integer> hourlyCounts = calculateHourlyTripCounts();
            
            // 요일별 통계는 캐시된 데이터 사용
            Map<String, Integer> dailyCounts = getDailyStatisticsFromCache();
            
            // 월별 통계는 캐시된 데이터 사용
            Map<Integer, Integer> monthlyCounts = getMonthlyStatisticsFromCache();
            
            return StatisticsSummaryResponse.builder()
                .overall(overall)
                .hourlyTripCounts(hourlyCounts)
                .dailyTripCounts(dailyCounts)
                .monthlyTripCounts(monthlyCounts)
                .build();
                
        } catch (Exception e) {
            log.error("캐시된 통계 요약 데이터 조회 실패: {}", e.getMessage(), e);
            throw new RuntimeException("통계 데이터 조회에 실패했습니다", e);
        }
    }

    private Map<String, Integer> getDailyStatisticsFromCache() {
        Map<String, Integer> dailyCounts = new LinkedHashMap<>();
        
        // 최근 7일간의 통계 데이터 조회
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);
        
        List<DailyStatistics> dailyStats = dailyStatisticsRepository.findByDateRange(startDate, endDate);
        
        // 요일별로 데이터 매핑
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            String dayName = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN);
            
            int count = dailyStats.stream()
                .filter(stat -> stat.getStatDate().equals(date))
                .mapToInt(DailyStatistics::getTripCount)
                .findFirst()
                .orElse(0);
                
            dailyCounts.put(dayName, count);
        }
        
        return dailyCounts;
    }

    private Map<Integer, Integer> getMonthlyStatisticsFromCache() {
        Map<Integer, Integer> monthlyCounts = new LinkedHashMap<>();
        
        // 현재 연도의 월별 통계 데이터 조회
        int currentYear = LocalDate.now().getYear();
        List<MonthlyStatistics> monthlyStats = monthlyStatisticsRepository.findByYear(currentYear);
        
        // 1-12월 초기화
        for (int month = 1; month <= 12; month++) {
            monthlyCounts.put(month, 0);
        }
        
        // 실제 데이터로 업데이트
        monthlyStats.forEach(stat -> {
            monthlyCounts.put(stat.getMonth(), stat.getTripCount());
        });
        
        return monthlyCounts;
    }
}
