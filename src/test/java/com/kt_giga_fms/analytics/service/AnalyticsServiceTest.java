package com.kt_giga_fms.analytics.service;

import com.kt_giga_fms.analytics.domain.TripRecord;
import com.kt_giga_fms.analytics.domain.VehicleStatistics;
import com.kt_giga_fms.analytics.dto.TripRecordDto;
import com.kt_giga_fms.analytics.dto.VehicleStatisticsResponse;
import com.kt_giga_fms.analytics.repository.TripRecordRepository;
import com.kt_giga_fms.analytics.repository.VehicleStatisticsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private TripRecordRepository tripRecordRepository;

    @Mock
    private VehicleStatisticsRepository vehicleStatisticsRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    private TripRecordDto sampleTripRecordDto;
    private VehicleStatistics sampleVehicleStatistics;

    @BeforeEach
    void setUp() {
        // 테스트용 샘플 데이터 설정
        sampleTripRecordDto = new TripRecordDto(
                "TEST001",
                "테스트 차량",
                LocalDateTime.of(2024, 1, 1, 10, 0),
                LocalDateTime.of(2024, 1, 1, 12, 0),
                new BigDecimal("50.5"),
                37.5665,
                126.9780,
                37.5665,
                126.9780,
                new BigDecimal("5.2")
        );

        sampleVehicleStatistics = new VehicleStatistics(
                "TEST001",
                "테스트 차량",
                new BigDecimal("150.5"),
                new BigDecimal("50.5"),
                "2024-01",
                3,
                1
        );
    }

    @Test
    @DisplayName("운행 기록 저장 및 통계 업데이트 테스트")
    void saveTripRecord_ShouldSaveTripRecordAndUpdateStatistics() {
        // Given
        String currentYearMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        
        when(tripRecordRepository.getTotalDistanceByVehicleId("TEST001"))
                .thenReturn(new BigDecimal("150.5"));
        when(tripRecordRepository.getMonthlyDistanceByVehicleId(eq("TEST001"), any(), any()))
                .thenReturn(new BigDecimal("50.5"));
        when(tripRecordRepository.getTotalTripsByVehicleId("TEST001"))
                .thenReturn(3L);
        when(tripRecordRepository.getMonthlyTripsByVehicleId(eq("TEST001"), any(), any()))
                .thenReturn(1L);
        when(vehicleStatisticsRepository.findByVehicleIdAndYearMonth("TEST001", currentYearMonth))
                .thenReturn(Optional.of(sampleVehicleStatistics));

        // When
        analyticsService.saveTripRecord(sampleTripRecordDto);

        // Then
        verify(tripRecordRepository, times(1)).save(any(TripRecord.class));
        verify(vehicleStatisticsRepository, times(1)).save(any(VehicleStatistics.class));
    }

    @Test
    @DisplayName("새로운 차량의 운행 기록 저장 시 통계 생성 테스트")
    void saveTripRecord_NewVehicle_ShouldCreateNewStatistics() {
        // Given
        String currentYearMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        
        when(tripRecordRepository.getTotalDistanceByVehicleId("NEW001"))
                .thenReturn(new BigDecimal("25.0"));
        when(tripRecordRepository.getMonthlyDistanceByVehicleId(eq("NEW001"), any(), any()))
                .thenReturn(new BigDecimal("25.0"));
        when(tripRecordRepository.getTotalTripsByVehicleId("NEW001"))
                .thenReturn(1L);
        when(tripRecordRepository.getMonthlyTripsByVehicleId(eq("NEW001"), any(), any()))
                .thenReturn(1L);
        when(vehicleStatisticsRepository.findByVehicleIdAndYearMonth("NEW001", currentYearMonth))
                .thenReturn(Optional.empty());

        TripRecordDto newVehicleTrip = new TripRecordDto(
                "NEW001",
                "새 차량",
                LocalDateTime.of(2024, 1, 1, 10, 0),
                LocalDateTime.of(2024, 1, 1, 11, 0),
                new BigDecimal("25.0"),
                37.5665,
                126.9780,
                37.5665,
                126.9780,
                new BigDecimal("2.5")
        );

        // When
        analyticsService.saveTripRecord(newVehicleTrip);

        // Then
        verify(tripRecordRepository, times(1)).save(any(TripRecord.class));
        verify(vehicleStatisticsRepository, times(1)).save(any(VehicleStatistics.class));
    }

    @Test
    @DisplayName("차량 통계 조회 테스트 - 통계가 존재하는 경우")
    void getVehicleStatistics_ExistingStatistics_ShouldReturnStatistics() {
        // Given
        String currentYearMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        when(vehicleStatisticsRepository.findByVehicleIdAndYearMonth("TEST001", currentYearMonth))
                .thenReturn(Optional.of(sampleVehicleStatistics));

        // When
        VehicleStatisticsResponse result = analyticsService.getVehicleStatistics("TEST001");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getVehicleId()).isEqualTo("TEST001");
        assertThat(result.getVehicleName()).isEqualTo("테스트 차량");
        assertThat(result.getTotalDistance()).isEqualTo(new BigDecimal("150.5"));
        assertThat(result.getMonthlyDistance()).isEqualTo(new BigDecimal("50.5"));
        assertThat(result.getTotalTrips()).isEqualTo(3);
        assertThat(result.getMonthlyTrips()).isEqualTo(1);
    }

    @Test
    @DisplayName("차량 통계 조회 테스트 - 통계가 없는 경우")
    void getVehicleStatistics_NoStatistics_ShouldReturnDefaultValues() {
        // Given
        String currentYearMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        when(vehicleStatisticsRepository.findByVehicleIdAndYearMonth("UNKNOWN", currentYearMonth))
                .thenReturn(Optional.empty());

        // When
        VehicleStatisticsResponse result = analyticsService.getVehicleStatistics("UNKNOWN");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getVehicleId()).isEqualTo("UNKNOWN");
        assertThat(result.getVehicleName()).isEqualTo("");
        assertThat(result.getTotalDistance()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.getMonthlyDistance()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.getTotalTrips()).isEqualTo(0);
        assertThat(result.getMonthlyTrips()).isEqualTo(0);
    }

    @Test
    @DisplayName("모든 차량 통계 조회 테스트")
    void getAllVehicleStatistics_ShouldReturnAllStatistics() {
        // Given
        String currentYearMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        VehicleStatistics stats1 = new VehicleStatistics("TEST001", "차량1", 
                new BigDecimal("100.0"), new BigDecimal("50.0"), currentYearMonth, 2, 1);
        VehicleStatistics stats2 = new VehicleStatistics("TEST002", "차량2", 
                new BigDecimal("200.0"), new BigDecimal("75.0"), currentYearMonth, 3, 2);

        when(vehicleStatisticsRepository.findByYearMonth(currentYearMonth))
                .thenReturn(Arrays.asList(stats1, stats2));

        // When
        List<VehicleStatisticsResponse> results = analyticsService.getAllVehicleStatistics();

        // Then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getVehicleId()).isEqualTo("TEST001");
        assertThat(results.get(1).getVehicleId()).isEqualTo("TEST002");
    }

    @Test
    @DisplayName("차량 통계 일괄 조회 테스트")
    void getVehicleStatisticsBatch_ShouldReturnBatchStatistics() {
        // Given
        String currentYearMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        List<String> vehicleIds = Arrays.asList("TEST001", "TEST002", "UNKNOWN");
        
        when(vehicleStatisticsRepository.findByVehicleIdAndYearMonth("TEST001", currentYearMonth))
                .thenReturn(Optional.of(sampleVehicleStatistics));
        when(vehicleStatisticsRepository.findByVehicleIdAndYearMonth("TEST002", currentYearMonth))
                .thenReturn(Optional.of(new VehicleStatistics("TEST002", "차량2", 
                        new BigDecimal("200.0"), new BigDecimal("75.0"), currentYearMonth, 3, 2)));
        when(vehicleStatisticsRepository.findByVehicleIdAndYearMonth("UNKNOWN", currentYearMonth))
                .thenReturn(Optional.empty());

        // When
        List<VehicleStatisticsResponse> results = analyticsService.getVehicleStatisticsBatch(vehicleIds);

        // Then
        assertThat(results).hasSize(3);
        assertThat(results.get(0).getVehicleId()).isEqualTo("TEST001");
        assertThat(results.get(1).getVehicleId()).isEqualTo("TEST002");
        assertThat(results.get(2).getVehicleId()).isEqualTo("UNKNOWN");
        assertThat(results.get(2).getTotalDistance()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("차량 통계 일괄 조회 시 예외 처리 테스트")
    void getVehicleStatisticsBatch_WithException_ShouldHandleGracefully() {
        // Given
        String currentYearMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        List<String> vehicleIds = Arrays.asList("TEST001", "ERROR001");
        
        when(vehicleStatisticsRepository.findByVehicleIdAndYearMonth("TEST001", currentYearMonth))
                .thenReturn(Optional.of(sampleVehicleStatistics));
        when(vehicleStatisticsRepository.findByVehicleIdAndYearMonth("ERROR001", currentYearMonth))
                .thenThrow(new RuntimeException("Database error"));

        // When
        List<VehicleStatisticsResponse> results = analyticsService.getVehicleStatisticsBatch(vehicleIds);

        // Then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getVehicleId()).isEqualTo("TEST001");
        assertThat(results.get(1).getVehicleId()).isEqualTo("ERROR001");
        assertThat(results.get(1).getTotalDistance()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("빈 차량 ID 리스트로 일괄 조회 테스트")
    void getVehicleStatisticsBatch_EmptyList_ShouldReturnEmptyList() {
        // Given
        List<String> vehicleIds = Arrays.asList();

        // When
        List<VehicleStatisticsResponse> results = analyticsService.getVehicleStatisticsBatch(vehicleIds);

        // Then
        assertThat(results).isEmpty();
    }
}
