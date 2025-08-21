package com.kt_giga_fms.analytics.service;

import com.kt_giga_fms.analytics.domain.DailyStatistics;
import com.kt_giga_fms.analytics.domain.MonthlyStatistics;
import com.kt_giga_fms.analytics.domain.TripRecord;
import com.kt_giga_fms.analytics.dto.StatisticsSummaryResponse;
import com.kt_giga_fms.analytics.repository.DailyStatisticsRepository;
import com.kt_giga_fms.analytics.repository.MonthlyStatisticsRepository;
import com.kt_giga_fms.analytics.repository.TripRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private TripRecordRepository tripRecordRepository;

    @Mock
    private DailyStatisticsRepository dailyStatisticsRepository;

    @Mock
    private MonthlyStatisticsRepository monthlyStatisticsRepository;

    @InjectMocks
    private StatisticsService statisticsService;

    private List<TripRecord> sampleTripRecords;

    @BeforeEach
    void setUp() {
        // 테스트용 샘플 데이터 설정
        sampleTripRecords = Arrays.asList(
            new TripRecord(
                "TEST001", "차량1",
                LocalDateTime.of(2024, 1, 1, 10, 0),
                LocalDateTime.of(2024, 1, 1, 12, 0),
                new BigDecimal("50.5"), 37.5665, 126.9780, 37.5665, 126.9780,
                new BigDecimal("5.2")
            ),
            new TripRecord(
                "TEST002", "차량2",
                LocalDateTime.of(2024, 1, 2, 14, 0),
                LocalDateTime.of(2024, 1, 2, 16, 0),
                new BigDecimal("75.0"), 37.5665, 126.9780, 37.5665, 126.9780,
                new BigDecimal("7.5")
            ),
            new TripRecord(
                "TEST003", "차량3",
                LocalDateTime.of(2024, 1, 3, 9, 0),
                LocalDateTime.of(2024, 1, 3, 11, 0),
                new BigDecimal("30.0"), 37.5665, 126.9780, 37.5665, 126.9780,
                new BigDecimal("3.0")
            )
        );
    }

    @Test
    @DisplayName("통계 요약 조회 테스트 - 데이터가 있는 경우")
    void getStatisticsSummary_WithData_ShouldReturnCorrectStatistics() {
        // Given
        when(tripRecordRepository.findAll()).thenReturn(sampleTripRecords);

        // When
        StatisticsSummaryResponse result = statisticsService.getStatisticsSummary();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getOverall()).isNotNull();
        assertThat(result.getOverall().getTotalTripCount()).isEqualTo(3L);
        assertThat(result.getOverall().getTotalDistance()).isEqualTo(new BigDecimal("155.5"));
        assertThat(result.getOverall().getAverageDistance()).isEqualTo(new BigDecimal("51.83"));
        assertThat(result.getHourlyTripCounts()).hasSize(24);
        assertThat(result.getDailyTripCounts()).hasSize(7);
        assertThat(result.getMonthlyTripCounts()).hasSize(12);
    }

    @Test
    @DisplayName("통계 요약 조회 테스트 - 데이터가 없는 경우")
    void getStatisticsSummary_NoData_ShouldReturnZeroStatistics() {
        // Given
        when(tripRecordRepository.findAll()).thenReturn(Arrays.asList());

        // When
        StatisticsSummaryResponse result = statisticsService.getStatisticsSummary();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getOverall()).isNotNull();
        assertThat(result.getOverall().getTotalTripCount()).isEqualTo(0L);
        assertThat(result.getOverall().getTotalDistance()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.getOverall().getAverageDistance()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.getHourlyTripCounts()).hasSize(24);
        assertThat(result.getDailyTripCounts()).hasSize(7);
        assertThat(result.getMonthlyTripCounts()).hasSize(12);
    }

    @Test
    @DisplayName("통계 요약 조회 테스트 - 예외 발생 시")
    void getStatisticsSummary_WithException_ShouldThrowRuntimeException() {
        // Given
        when(tripRecordRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThatThrownBy(() -> statisticsService.getStatisticsSummary())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("통계 데이터 조회에 실패했습니다");
    }

    @Test
    @DisplayName("캐시된 통계 요약 조회 테스트")
    void getStatisticsSummaryFromCache_ShouldReturnCachedStatistics() {
        // Given
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6);
        
        List<DailyStatistics> dailyStats = Arrays.asList(
            DailyStatistics.builder().statDate(today.minusDays(6)).tripCount(5).build(),
            DailyStatistics.builder().statDate(today.minusDays(5)).tripCount(3).build(),
            DailyStatistics.builder().statDate(today.minusDays(4)).tripCount(7).build(),
            DailyStatistics.builder().statDate(today.minusDays(3)).tripCount(2).build(),
            DailyStatistics.builder().statDate(today.minusDays(2)).tripCount(8).build(),
            DailyStatistics.builder().statDate(today.minusDays(1)).tripCount(4).build(),
            DailyStatistics.builder().statDate(today).tripCount(6).build()
        );
        
        List<MonthlyStatistics> monthlyStats = Arrays.asList(
            MonthlyStatistics.builder().year(2024).month(1).tripCount(150).build(),
            MonthlyStatistics.builder().year(2024).month(2).tripCount(120).build(),
            MonthlyStatistics.builder().year(2024).month(3).tripCount(180).build()
        );

        when(tripRecordRepository.findAll()).thenReturn(sampleTripRecords);
        when(dailyStatisticsRepository.findByDateRange(startDate, today)).thenReturn(dailyStats);
        when(monthlyStatisticsRepository.findByYear(today.getYear())).thenReturn(monthlyStats);

        // When
        StatisticsSummaryResponse result = statisticsService.getStatisticsSummaryFromCache();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getOverall()).isNotNull();
        assertThat(result.getOverall().getTotalTripCount()).isEqualTo(3L);
        assertThat(result.getDailyTripCounts()).hasSize(7);
        assertThat(result.getMonthlyTripCounts()).hasSize(12);
        
        verify(dailyStatisticsRepository, times(1)).findByDateRange(startDate, today);
        verify(monthlyStatisticsRepository, times(1)).findByYear(today.getYear());
    }

    @Test
    @DisplayName("시간대별 통계 계산 테스트")
    void calculateHourlyTripCounts_ShouldReturnCorrectHourlyCounts() {
        // Given
        when(tripRecordRepository.findAll()).thenReturn(sampleTripRecords);

        // When
        StatisticsSummaryResponse result = statisticsService.getStatisticsSummary();

        // Then
        assertThat(result.getHourlyTripCounts()).hasSize(24);
        // 10시, 14시, 9시에 각각 1건씩
        assertThat(result.getHourlyTripCounts().get(10)).isEqualTo(1);
        assertThat(result.getHourlyTripCounts().get(14)).isEqualTo(1);
        assertThat(result.getHourlyTripCounts().get(9)).isEqualTo(1);
        // 다른 시간대는 0
        assertThat(result.getHourlyTripCounts().get(0)).isEqualTo(0);
        assertThat(result.getHourlyTripCounts().get(23)).isEqualTo(0);
    }

    @Test
    @DisplayName("요일별 통계 계산 테스트")
    void calculateDailyTripCounts_ShouldReturnCorrectDailyCounts() {
        // Given
        when(tripRecordRepository.findAll()).thenReturn(sampleTripRecords);

        // When
        StatisticsSummaryResponse result = statisticsService.getStatisticsSummary();

        // Then
        assertThat(result.getDailyTripCounts()).hasSize(7);
        // 월요일(1월 1일), 화요일(1월 2일), 수요일(1월 3일)에 각각 1건씩
        assertThat(result.getDailyTripCounts().values()).contains(1);
    }

    @Test
    @DisplayName("월별 통계 계산 테스트")
    void calculateMonthlyTripCounts_ShouldReturnCorrectMonthlyCounts() {
        // Given
        when(tripRecordRepository.findAll()).thenReturn(sampleTripRecords);

        // When
        StatisticsSummaryResponse result = statisticsService.getStatisticsSummary();

        // Then
        assertThat(result.getMonthlyTripCounts()).hasSize(12);
        // 1월에 3건
        assertThat(result.getMonthlyTripCounts().get(1)).isEqualTo(3);
        // 다른 월은 0
        assertThat(result.getMonthlyTripCounts().get(2)).isEqualTo(0);
        assertThat(result.getMonthlyTripCounts().get(12)).isEqualTo(0);
    }

    @Test
    @DisplayName("캐시된 통계 조회 시 예외 처리 테스트")
    void getStatisticsSummaryFromCache_WithException_ShouldThrowRuntimeException() {
        // Given
        when(tripRecordRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThatThrownBy(() -> statisticsService.getStatisticsSummaryFromCache())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("통계 데이터 조회에 실패했습니다");
    }

    @Test
    @DisplayName("빈 데이터로 통계 계산 테스트")
    void getStatisticsSummary_EmptyData_ShouldHandleGracefully() {
        // Given
        when(tripRecordRepository.findAll()).thenReturn(Arrays.asList());

        // When
        StatisticsSummaryResponse result = statisticsService.getStatisticsSummary();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getOverall().getTotalTripCount()).isEqualTo(0L);
        assertThat(result.getOverall().getTotalDistance()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.getOverall().getAverageDistance()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.getOverall().getAverageDuration()).isEqualTo(0L);
    }
}
