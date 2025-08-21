package com.kt_giga_fms.analytics.service;

import com.kt_giga_fms.analytics.domain.*;
import com.kt_giga_fms.analytics.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsCalculationService {

    private final TripRecordRepository tripRecordRepository;
    private final DailyStatisticsRepository dailyStatisticsRepository;
    private final WeeklyStatisticsRepository weeklyStatisticsRepository;
    private final MonthlyStatisticsRepository monthlyStatisticsRepository;

    public void calculateDailyStatistics(LocalDate date) {
        log.info("일일 통계 계산 시작: {}", date);
        
        try {
            // 해당 날짜의 운행 기록 조회
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);
            
            List<TripRecord> dailyTrips = tripRecordRepository.findByStartTimeBetween(startOfDay, endOfDay);
            
            if (dailyTrips.isEmpty()) {
                log.info("해당 날짜에 운행 기록이 없습니다: {}", date);
                return;
            }
            
            // 통계 계산
            DailyStatistics statistics = calculateDailyStats(dailyTrips, date);
            
            // 기존 통계가 있으면 업데이트, 없으면 새로 생성
            dailyStatisticsRepository.findByStatDate(date)
                .ifPresentOrElse(
                    existing -> {
                        existing.setTripCount(statistics.getTripCount());
                        existing.setTotalDistance(statistics.getTotalDistance());
                        existing.setTotalDuration(statistics.getTotalDuration());
                        existing.setAverageDistance(statistics.getAverageDistance());
                        existing.setAverageDuration(statistics.getAverageDuration());
                        dailyStatisticsRepository.save(existing);
                        log.info("일일 통계 업데이트 완료: {}", date);
                    },
                    () -> {
                        dailyStatisticsRepository.save(statistics);
                        log.info("일일 통계 생성 완료: {}", date);
                    }
                );
                
        } catch (Exception e) {
            log.error("일일 통계 계산 실패: {}", e.getMessage(), e);
        }
    }

    public void calculateWeeklyStatistics(LocalDate weekStartDate) {
        log.info("주간 통계 계산 시작: {}", weekStartDate);
        
        try {
            LocalDate weekEndDate = weekStartDate.plusDays(6);
            LocalDateTime startOfWeek = weekStartDate.atStartOfDay();
            LocalDateTime endOfWeek = weekEndDate.atTime(23, 59, 59);
            
            List<TripRecord> weeklyTrips = tripRecordRepository.findByStartTimeBetween(startOfWeek, endOfWeek);
            
            if (weeklyTrips.isEmpty()) {
                log.info("해당 주에 운행 기록이 없습니다: {} ~ {}", weekStartDate, weekEndDate);
                return;
            }
            
            // 통계 계산
            WeeklyStatistics statistics = calculateWeeklyStats(weeklyTrips, weekStartDate, weekEndDate);
            
            // 기존 통계가 있으면 업데이트, 없으면 새로 생성
            weeklyStatisticsRepository.findByYearAndWeekNumber(statistics.getYear(), statistics.getWeekNumber())
                .ifPresentOrElse(
                    existing -> {
                        existing.setTripCount(statistics.getTripCount());
                        existing.setTotalDistance(statistics.getTotalDistance());
                        existing.setTotalDuration(statistics.getTotalDuration());
                        existing.setAverageDistance(statistics.getAverageDistance());
                        existing.setAverageDuration(statistics.getAverageDuration());
                        weeklyStatisticsRepository.save(existing);
                        log.info("주간 통계 업데이트 완료: {}주차", statistics.getWeekNumber());
                    },
                    () -> {
                        weeklyStatisticsRepository.save(statistics);
                        log.info("주간 통계 생성 완료: {}주차", statistics.getWeekNumber());
                    }
                );
                
        } catch (Exception e) {
            log.error("주간 통계 계산 실패: {}", e.getMessage(), e);
        }
    }

    public void calculateMonthlyStatistics(Integer year, Integer month) {
        log.info("월간 통계 계산 시작: {}-{}", year, month);
        
        try {
            LocalDate monthStart = LocalDate.of(year, month, 1);
            LocalDate monthEnd = monthStart.with(TemporalAdjusters.lastDayOfMonth());
            LocalDateTime startOfMonth = monthStart.atStartOfDay();
            LocalDateTime endOfMonth = monthEnd.atTime(23, 59, 59);
            
            List<TripRecord> monthlyTrips = tripRecordRepository.findByStartTimeBetween(startOfMonth, endOfMonth);
            
            if (monthlyTrips.isEmpty()) {
                log.info("해당 월에 운행 기록이 없습니다: {}-{}", year, month);
                return;
            }
            
            // 통계 계산
            MonthlyStatistics statistics = calculateMonthlyStats(monthlyTrips, year, month);
            
            // 기존 통계가 있으면 업데이트, 없으면 새로 생성
            monthlyStatisticsRepository.findByYearAndMonth(year, month)
                .ifPresentOrElse(
                    existing -> {
                        existing.setTripCount(statistics.getTripCount());
                        existing.setTotalDistance(statistics.getTotalDistance());
                        existing.setTotalDuration(statistics.getTotalDuration());
                        existing.setAverageDistance(statistics.getAverageDistance());
                        existing.setAverageDuration(statistics.getAverageDuration());
                        monthlyStatisticsRepository.save(existing);
                        log.info("월간 통계 업데이트 완료: {}-{}", year, month);
                    },
                    () -> {
                        monthlyStatisticsRepository.save(statistics);
                        log.info("월간 통계 생성 완료: {}-{}", year, month);
                    }
                );
                
        } catch (Exception e) {
            log.error("월간 통계 계산 실패: {}", e.getMessage(), e);
        }
    }

    private DailyStatistics calculateDailyStats(List<TripRecord> trips, LocalDate date) {
        int tripCount = trips.size();
        BigDecimal totalDistance = trips.stream()
            .map(TripRecord::getTotalDistance)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        long totalDuration = trips.stream()
            .mapToLong(trip -> {
                if (trip.getStartTime() != null && trip.getEndTime() != null) {
                    return Duration.between(trip.getStartTime(), trip.getEndTime()).toMinutes();
                }
                return 0L;
            })
            .sum();
        
        BigDecimal averageDistance = tripCount > 0 ? 
            totalDistance.divide(BigDecimal.valueOf(tripCount), 2, RoundingMode.HALF_UP) : 
            BigDecimal.ZERO;
        long averageDuration = tripCount > 0 ? totalDuration / tripCount : 0L;
        
        return DailyStatistics.builder()
            .statDate(date)
            .tripCount(tripCount)
            .totalDistance(totalDistance)
            .totalDuration(totalDuration)
            .averageDistance(averageDistance)
            .averageDuration(averageDuration)
            .build();
    }

    private WeeklyStatistics calculateWeeklyStats(List<TripRecord> trips, LocalDate weekStart, LocalDate weekEnd) {
        int tripCount = trips.size();
        BigDecimal totalDistance = trips.stream()
            .map(TripRecord::getTotalDistance)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        long totalDuration = trips.stream()
            .mapToLong(trip -> {
                if (trip.getStartTime() != null && trip.getEndTime() != null) {
                    return Duration.between(trip.getStartTime(), trip.getEndTime()).toMinutes();
                }
                return 0L;
            })
            .sum();
        
        BigDecimal averageDistance = tripCount > 0 ? 
            totalDistance.divide(BigDecimal.valueOf(tripCount), 2, RoundingMode.HALF_UP) : 
            BigDecimal.ZERO;
        long averageDuration = tripCount > 0 ? totalDuration / tripCount : 0L;
        
        int weekNumber = weekStart.get(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear());
        int year = weekStart.getYear();
        
        return WeeklyStatistics.builder()
            .weekStartDate(weekStart)
            .weekEndDate(weekEnd)
            .weekNumber(weekNumber)
            .year(year)
            .tripCount(tripCount)
            .totalDistance(totalDistance)
            .totalDuration(totalDuration)
            .averageDistance(averageDistance)
            .averageDuration(averageDuration)
            .build();
    }

    private MonthlyStatistics calculateMonthlyStats(List<TripRecord> trips, Integer year, Integer month) {
        int tripCount = trips.size();
        BigDecimal totalDistance = trips.stream()
            .map(TripRecord::getTotalDistance)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        long totalDuration = trips.stream()
            .mapToLong(trip -> {
                if (trip.getStartTime() != null && trip.getEndTime() != null) {
                    return Duration.between(trip.getStartTime(), trip.getEndTime()).toMinutes();
                }
                return 0L;
            })
            .sum();
        
        BigDecimal averageDistance = tripCount > 0 ? 
            totalDistance.divide(BigDecimal.valueOf(tripCount), 2, RoundingMode.HALF_UP) : 
            BigDecimal.ZERO;
        long averageDuration = tripCount > 0 ? totalDuration / tripCount : 0L;
        
        return MonthlyStatistics.builder()
            .year(year)
            .month(month)
            .tripCount(tripCount)
            .totalDistance(totalDistance)
            .totalDuration(totalDuration)
            .averageDistance(averageDistance)
            .averageDuration(averageDuration)
            .build();
    }
}
