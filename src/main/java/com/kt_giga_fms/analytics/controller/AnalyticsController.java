package com.kt_giga_fms.analytics.controller;

import com.kt_giga_fms.analytics.dto.ApiResponse;
import com.kt_giga_fms.analytics.dto.TripRecordDto;
import com.kt_giga_fms.analytics.dto.VehicleStatisticsResponse;
import com.kt_giga_fms.analytics.service.AnalyticsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    
    @PostMapping("/trip-records")
    public ResponseEntity<ApiResponse<String>> saveTripRecord(
            @Valid @RequestBody TripRecordDto tripRecordDto) {
        
        log.info("운행 기록 저장 API 호출: {}", tripRecordDto.getVehicleId());
        
        try {
            analyticsService.saveTripRecord(tripRecordDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("운행 기록이 성공적으로 저장되었습니다."));
        } catch (Exception e) {
            log.error("운행 기록 저장 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("운행 기록 저장에 실패했습니다: " + e.getMessage()));
        }
    }
    
    @GetMapping("/vehicles/{vehicleId}/statistics")
    public ResponseEntity<ApiResponse<VehicleStatisticsResponse>> getVehicleStatistics(
            @PathVariable String vehicleId) {
        
        log.info("차량 통계 조회 API 호출: {}", vehicleId);
        
        try {
            VehicleStatisticsResponse statistics = analyticsService.getVehicleStatistics(vehicleId);
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            log.error("차량 통계 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("차량 통계 조회에 실패했습니다: " + e.getMessage()));
        }
    }
    
    @GetMapping("/vehicles/statistics")
    public ResponseEntity<ApiResponse<List<VehicleStatisticsResponse>>> getAllVehicleStatistics() {
        
        log.info("전체 차량 통계 조회 API 호출");
        
        try {
            List<VehicleStatisticsResponse> statistics = analyticsService.getAllVehicleStatistics();
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            log.error("전체 차량 통계 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("전체 차량 통계 조회에 실패했습니다: " + e.getMessage()));
        }
    }
    
    @PostMapping("/vehicles/statistics/batch")
    public ResponseEntity<ApiResponse<List<VehicleStatisticsResponse>>> getVehicleStatisticsBatch(
            @RequestBody List<String> vehicleIds) {
        
        log.info("차량 통계 일괄 조회 API 호출: {}건", vehicleIds.size());
        
        try {
            List<VehicleStatisticsResponse> statistics = analyticsService.getVehicleStatisticsBatch(vehicleIds);
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            log.error("차량 통계 일괄 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("차량 통계 일괄 조회에 실패했습니다: " + e.getMessage()));
        }
    }
}
