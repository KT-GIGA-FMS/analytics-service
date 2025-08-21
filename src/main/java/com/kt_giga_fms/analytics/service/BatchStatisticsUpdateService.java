package com.kt_giga_fms.analytics.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchStatisticsUpdateService {

    private final StatisticsCalculationService statisticsCalculationService;

    // 매일 자정에 전날 통계 계산
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateDailyStatistics() {
        log.info("일일 통계 업데이트 시작");
        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            statisticsCalculationService.calculateDailyStatistics(yesterday);
            log.info("일일 통계 업데이트 완료: {}", yesterday);
        } catch (Exception e) {
            log.error("일일 통계 업데이트 실패: {}", e.getMessage(), e);
        }
    }

    // 매주 월요일 자정에 지난주 통계 계산
    @Scheduled(cron = "0 0 0 ? * MON")
    public void updateWeeklyStatistics() {
        log.info("주간 통계 업데이트 시작");
        try {
            LocalDate lastWeekStart = LocalDate.now().minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            statisticsCalculationService.calculateWeeklyStatistics(lastWeekStart);
            log.info("주간 통계 업데이트 완료: {}주차", lastWeekStart.get(WeekFields.ISO.weekOfWeekBasedYear()));
        } catch (Exception e) {
            log.error("주간 통계 업데이트 실패: {}", e.getMessage(), e);
        }
    }

    // 매월 1일 자정에 지난달 통계 계산
    @Scheduled(cron = "0 0 0 1 * ?")
    public void updateMonthlyStatistics() {
        log.info("월간 통계 업데이트 시작");
        try {
            LocalDate lastMonth = LocalDate.now().minusMonths(1);
            statisticsCalculationService.calculateMonthlyStatistics(lastMonth.getYear(), lastMonth.getMonthValue());
            log.info("월간 통계 업데이트 완료: {}-{}", lastMonth.getYear(), lastMonth.getMonthValue());
        } catch (Exception e) {
            log.error("월간 통계 업데이트 실패: {}", e.getMessage(), e);
        }
    }
}
