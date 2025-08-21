package com.kt_giga_fms.analytics.controller;

import com.kt_giga_fms.analytics.dto.ApiResponse;
import com.kt_giga_fms.analytics.dto.TripRecordDto;
import com.kt_giga_fms.analytics.dto.VehicleStatisticsResponse;
import com.kt_giga_fms.analytics.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Analytics", description = "차량 분석 및 통계 API")
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    
    @PostMapping("/trip-records")
    @Operation(summary = "운행 기록 저장", description = "새로운 운행 기록을 저장합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "운행 기록 저장 성공", 
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<ApiResponse<String>> saveTripRecord(
            @Parameter(description = "운행 기록 정보") @Valid @RequestBody TripRecordDto tripRecordDto) {
        
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
    @Operation(summary = "차량별 통계 조회", description = "특정 차량의 통계 정보를 조회합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "통계 조회 성공", 
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "차량을 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<ApiResponse<VehicleStatisticsResponse>> getVehicleStatistics(
            @Parameter(description = "차량 ID") @PathVariable String vehicleId) {
        
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
    @Operation(summary = "전체 차량 통계 조회", description = "모든 차량의 통계 정보를 조회합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "통계 조회 성공", 
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
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
    @Operation(summary = "차량 통계 일괄 조회", description = "여러 차량의 통계 정보를 일괄적으로 조회합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "일괄 조회 성공", 
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<ApiResponse<List<VehicleStatisticsResponse>>> getVehicleStatisticsBatch(
            @Parameter(description = "차량 ID 목록") @RequestBody List<String> vehicleIds) {
        
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
