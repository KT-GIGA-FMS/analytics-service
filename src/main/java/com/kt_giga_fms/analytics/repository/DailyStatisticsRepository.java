package com.kt_giga_fms.analytics.repository;

import com.kt_giga_fms.analytics.domain.DailyStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyStatisticsRepository extends JpaRepository<DailyStatistics, Long> {
    
    Optional<DailyStatistics> findByStatDate(LocalDate statDate);
    
    @Query("SELECT d FROM DailyStatistics d WHERE d.statDate BETWEEN :startDate AND :endDate ORDER BY d.statDate")
    List<DailyStatistics> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT d FROM DailyStatistics d WHERE d.statDate >= :startDate ORDER BY d.statDate DESC")
    List<DailyStatistics> findRecentStatistics(@Param("startDate") LocalDate startDate);
}
