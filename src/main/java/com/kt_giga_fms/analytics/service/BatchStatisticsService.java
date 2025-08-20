package com.kt_giga_fms.analytics.service;

import com.kt_giga_fms.analytics.domain.TripRecord;
import com.kt_giga_fms.analytics.domain.VehicleStatistics;
import com.kt_giga_fms.analytics.repository.TripRecordRepository;
import com.kt_giga_fms.analytics.repository.VehicleStatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchStatisticsService {
    
    private final TripRecordRepository tripRecordRepository;
    private final VehicleStatisticsRepository vehicleStatisticsRepository;
    
    /**
     * 매일 새벽 2시에 전날의 통계를 계산하여 업데이트합니다.
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void updateDailyStatistics() {
        log.info("일일 통계 업데이트 배치 작업 시작");
        
        try {
            LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
            String yearMonth = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            
            // 어제 운행이 있었던 모든 차량 ID 조회
            List<String> vehicleIds = tripRecordRepository.findByVehicleIdAndDateRange(
                null, 
                yesterday.withHour(0).withMinute(0).withSecond(0),
                yesterday.withHour(23).withMinute(59).withSecond(59)
            ).stream()
            .map(TripRecord::getVehicleId)
            .distinct()
            .toList();
            
            // 각 차량별로 통계 업데이트
            for (String vehicleId : vehicleIds) {
                updateVehicleMonthlyStatistics(vehicleId, yearMonth);
            }
            
            log.info("일일 통계 업데이트 배치 작업 완료: {}건", vehicleIds.size());
            
        } catch (Exception e) {
            log.error("일일 통계 업데이트 배치 작업 실패: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 매월 1일 새벽 3시에 전월 통계를 계산하여 업데이트합니다.
     */
    @Scheduled(cron = "0 0 3 1 * ?")
    @Transactional
    public void updateMonthlyStatistics() {
        log.info("월간 통계 업데이트 배치 작업 시작");
        
        try {
            LocalDateTime lastMonth = LocalDateTime.now().minusMonths(1);
            String yearMonth = lastMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            
            // 전월에 운행이 있었던 모든 차량 ID 조회
            List<String> vehicleIds = tripRecordRepository.findByVehicleIdAndDateRange(
                null,
                lastMonth.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0),
                lastMonth.withDayOfMonth(lastMonth.toLocalDate().lengthOfMonth())
                    .withHour(23).withMinute(59).withSecond(59)
            ).stream()
            .map(TripRecord::getVehicleId)
            .distinct()
            .toList();
            
            // 각 차량별로 전월 통계 계산
            for (String vehicleId : vehicleIds) {
                updateVehicleMonthlyStatistics(vehicleId, yearMonth);
            }
            
            log.info("월간 통계 업데이트 배치 작업 완료: {}건", vehicleIds.size());
            
        } catch (Exception e) {
            log.error("월간 통계 업데이트 배치 작업 실패: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 특정 차량의 월별 통계를 업데이트합니다.
     */
    private void updateVehicleMonthlyStatistics(String vehicleId, String yearMonth) {
        try {
            // 해당 월의 운행 기록 조회
            LocalDateTime startOfMonth = LocalDateTime.parse(yearMonth + "-01T00:00:00");
            LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
            
            List<TripRecord> monthlyTrips = tripRecordRepository.findByVehicleIdAndDateRange(
                vehicleId, startOfMonth, endOfMonth
            );
            
            if (monthlyTrips.isEmpty()) {
                log.debug("차량 {}의 {}월 운행 기록이 없습니다.", vehicleId, yearMonth);
                return;
            }
            
            // 월별 거리 및 운행 횟수 계산
            BigDecimal monthlyDistance = monthlyTrips.stream()
                .map(TripRecord::getTotalDistance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            int monthlyTripCount = monthlyTrips.size();
            
            // 전체 누적 거리 및 운행 횟수 계산
            BigDecimal totalDistance = tripRecordRepository.getTotalDistanceByVehicleId(vehicleId);
            Long totalTrips = tripRecordRepository.getTotalTripsByVehicleId(vehicleId);
            
            // 차량 이름 가져오기 (첫 번째 운행 기록에서)
            String vehicleName = monthlyTrips.get(0).getVehicleName();
            
            // 통계 저장 또는 업데이트
            Optional<VehicleStatistics> existingStats = vehicleStatisticsRepository
                .findByVehicleIdAndYearMonth(vehicleId, yearMonth);
            
            VehicleStatistics vehicleStatistics;
            if (existingStats.isPresent()) {
                vehicleStatistics = existingStats.get();
                vehicleStatistics.updateStatistics(totalDistance, monthlyDistance, 
                                                totalTrips.intValue(), monthlyTripCount);
            } else {
                vehicleStatistics = new VehicleStatistics(
                    vehicleId, vehicleName, totalDistance, monthlyDistance, 
                    yearMonth, totalTrips.intValue(), monthlyTripCount
                );
            }
            
            vehicleStatisticsRepository.save(vehicleStatistics);
            
            log.debug("차량 {}의 {}월 통계 업데이트 완료: 거리={}km, 운행={}회", 
                     vehicleId, yearMonth, monthlyDistance, monthlyTrips);
            
        } catch (Exception e) {
            log.error("차량 {}의 {}월 통계 업데이트 실패: {}", vehicleId, yearMonth, e.getMessage(), e);
        }
    }
    
    /**
     * 수동으로 특정 차량의 통계를 업데이트합니다.
     */
    @Transactional
    public void updateVehicleStatisticsManually(String vehicleId) {
        log.info("차량 {}의 통계 수동 업데이트 시작", vehicleId);
        
        try {
            String currentYearMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            updateVehicleMonthlyStatistics(vehicleId, currentYearMonth);
            
            log.info("차량 {}의 통계 수동 업데이트 완료", vehicleId);
            
        } catch (Exception e) {
            log.error("차량 {}의 통계 수동 업데이트 실패: {}", vehicleId, e.getMessage(), e);
            throw e;
        }
    }
}
