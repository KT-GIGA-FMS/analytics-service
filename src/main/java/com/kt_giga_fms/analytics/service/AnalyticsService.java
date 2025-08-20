package com.kt_giga_fms.analytics.service;

import com.kt_giga_fms.analytics.domain.TripRecord;
import com.kt_giga_fms.analytics.domain.VehicleStatistics;
import com.kt_giga_fms.analytics.dto.TripRecordDto;
import com.kt_giga_fms.analytics.dto.VehicleStatisticsResponse;
import com.kt_giga_fms.analytics.repository.TripRecordRepository;
import com.kt_giga_fms.analytics.repository.VehicleStatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Transactional(readOnly = true)
public class AnalyticsService {
    
    private final TripRecordRepository tripRecordRepository;
    private final VehicleStatisticsRepository vehicleStatisticsRepository;
    
    /**
     * 운행 기록을 저장하고 통계를 업데이트합니다.
     */
    @Transactional
    public void saveTripRecord(TripRecordDto tripRecordDto) {
        log.info("운행 기록 저장: {}", tripRecordDto.getVehicleId());
        
        // TripRecord 저장
        TripRecord tripRecord = new TripRecord(
            tripRecordDto.getVehicleId(),
            tripRecordDto.getVehicleName(),
            tripRecordDto.getStartTime(),
            tripRecordDto.getEndTime(),
            tripRecordDto.getTotalDistance(),
            tripRecordDto.getStartLatitude(),
            tripRecordDto.getStartLongitude(),
            tripRecordDto.getEndLatitude(),
            tripRecordDto.getEndLongitude(),
            tripRecordDto.getFuelConsumed()
        );
        
        tripRecordRepository.save(tripRecord);
        
        // 통계 업데이트
        updateVehicleStatistics(tripRecordDto.getVehicleId(), tripRecordDto.getVehicleName());
    }
    
    /**
     * 차량 통계를 업데이트합니다.
     */
    private void updateVehicleStatistics(String vehicleId, String vehicleName) {
        String currentYearMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        
        // 현재 월 통계 조회 또는 생성
        Optional<VehicleStatistics> existingStats = vehicleStatisticsRepository
            .findByVehicleIdAndYearMonth(vehicleId, currentYearMonth);
        
        VehicleStatistics vehicleStatistics;
        if (existingStats.isPresent()) {
            vehicleStatistics = existingStats.get();
        } else {
            vehicleStatistics = new VehicleStatistics(
                vehicleId, vehicleName, BigDecimal.ZERO, BigDecimal.ZERO, 
                currentYearMonth, 0, 0
            );
        }
        
        // 전체 및 월별 통계 계산
        BigDecimal totalDistance = tripRecordRepository.getTotalDistanceByVehicleId(vehicleId);
        BigDecimal monthlyDistance = tripRecordRepository.getMonthlyDistanceByVehicleId(
            vehicleId, 
            LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0),
            LocalDateTime.now().plusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
        );
        
        Long totalTrips = tripRecordRepository.getTotalTripsByVehicleId(vehicleId);
        Long monthlyTrips = tripRecordRepository.getMonthlyTripsByVehicleId(
            vehicleId,
            LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0),
            LocalDateTime.now().plusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
        );
        
        // 통계 업데이트
        vehicleStatistics.updateStatistics(
            totalDistance, 
            monthlyDistance, 
            totalTrips.intValue(), 
            monthlyTrips.intValue()
        );
        
        vehicleStatisticsRepository.save(vehicleStatistics);
        log.info("차량 통계 업데이트 완료: {} - 총거리: {}km, 월거리: {}km", 
                vehicleId, totalDistance, monthlyDistance);
    }
    
    /**
     * 차량의 전체 누적거리와 이번달 운행거리를 조회합니다.
     */
    public VehicleStatisticsResponse getVehicleStatistics(String vehicleId) {
        log.info("차량 통계 조회: {}", vehicleId);
        
        String currentYearMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        
        Optional<VehicleStatistics> stats = vehicleStatisticsRepository
            .findByVehicleIdAndYearMonth(vehicleId, currentYearMonth);
        
        if (stats.isPresent()) {
            VehicleStatistics vehicleStatistics = stats.get();
            return new VehicleStatisticsResponse(
                vehicleStatistics.getVehicleId(),
                vehicleStatistics.getVehicleName(),
                vehicleStatistics.getTotalDistance(),
                vehicleStatistics.getMonthlyDistance(),
                vehicleStatistics.getYearMonth(),
                vehicleStatistics.getTotalTrips(),
                vehicleStatistics.getMonthlyTrips()
            );
        } else {
            // 통계가 없는 경우 기본값 반환
            return new VehicleStatisticsResponse(
                vehicleId, "", BigDecimal.ZERO, BigDecimal.ZERO, 
                currentYearMonth, 0, 0
            );
        }
    }
    
    /**
     * 모든 차량의 통계를 조회합니다.
     */
    public List<VehicleStatisticsResponse> getAllVehicleStatistics() {
        String currentYearMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        
        return vehicleStatisticsRepository.findByYearMonth(currentYearMonth)
            .stream()
            .map(stats -> new VehicleStatisticsResponse(
                stats.getVehicleId(),
                stats.getVehicleName(),
                stats.getTotalDistance(),
                stats.getMonthlyDistance(),
                stats.getYearMonth(),
                stats.getTotalTrips(),
                stats.getMonthlyTrips()
            ))
            .toList();
    }
    
    /**
     * 특정 차량 ID들에 대한 통계를 일괄로 조회합니다.
     */
    public List<VehicleStatisticsResponse> getVehicleStatisticsBatch(List<String> vehicleIds) {
        log.info("차량 통계 일괄 조회: {}건", vehicleIds.size());
        
        String currentYearMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        
        return vehicleIds.stream()
            .map(vehicleId -> {
                try {
                    return getVehicleStatistics(vehicleId);
                } catch (Exception e) {
                    log.warn("차량 {} 통계 조회 실패: {}", vehicleId, e.getMessage());
                    // 실패한 경우 기본값 반환
                    return new VehicleStatisticsResponse(
                        vehicleId, "", BigDecimal.ZERO, BigDecimal.ZERO, 
                        currentYearMonth, 0, 0
                    );
                }
            })
            .toList();
    }
}
