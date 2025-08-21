package com.kt_giga_fms.analytics.repository;

import com.kt_giga_fms.analytics.domain.WeeklyStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeeklyStatisticsRepository extends JpaRepository<WeeklyStatistics, Long> {
    
    Optional<WeeklyStatistics> findByYearAndWeekNumber(Integer year, Integer weekNumber);
    
    @Query("SELECT w FROM WeeklyStatistics w WHERE w.year = :year ORDER BY w.weekNumber")
    List<WeeklyStatistics> findByYear(@Param("year") Integer year);
    
    @Query("SELECT w FROM WeeklyStatistics w WHERE w.weekStartDate >= :startDate ORDER BY w.weekStartDate DESC")
    List<WeeklyStatistics> findRecentStatistics(@Param("startDate") LocalDate startDate);
}
