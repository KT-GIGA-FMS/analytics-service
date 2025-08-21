package com.kt_giga_fms.analytics.controller;

import com.kt_giga_fms.analytics.dto.ApiResponse;
import com.kt_giga_fms.analytics.dto.StatisticsSummaryResponse;
import com.kt_giga_fms.analytics.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
@Tag(name = "Statistics", description = "통계 데이터 조회 API")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/summary")
    @Operation(summary = "통계 요약 조회", description = "전체 시스템의 통계 요약 정보를 조회합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "통계 요약 조회 성공", 
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<ApiResponse<StatisticsSummaryResponse>> getStatisticsSummary() {
        log.info("통계 요약 API 호출");
        
        try {
            StatisticsSummaryResponse statistics = statisticsService.getStatisticsSummary();
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            log.error("통계 요약 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("통계 데이터 조회에 실패했습니다: " + e.getMessage()));
        }
    }
    
    @GetMapping("/summary/cache")
    @Operation(summary = "캐시된 통계 요약 조회", description = "Redis 캐시에서 통계 요약 정보를 조회합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "캐시된 통계 요약 조회 성공", 
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<ApiResponse<StatisticsSummaryResponse>> getStatisticsSummaryFromCache() {
        log.info("캐시된 통계 요약 API 호출");
        
        try {
            StatisticsSummaryResponse statistics = statisticsService.getStatisticsSummaryFromCache();
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            log.error("캐시된 통계 요약 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("캐시된 통계 데이터 조회에 실패했습니다: " + e.getMessage()));
        }
    }
}
