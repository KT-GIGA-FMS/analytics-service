package com.kt_giga_fms.analytics.repository;

import com.kt_giga_fms.analytics.domain.MonthlyStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonthlyStatisticsRepository extends JpaRepository<MonthlyStatistics, Long> {
    
    Optional<MonthlyStatistics> findByYearAndMonth(Integer year, Integer month);
    
    @Query("SELECT m FROM MonthlyStatistics m WHERE m.year = :year ORDER BY m.month")
    List<MonthlyStatistics> findByYear(@Param("year") Integer year);
    
    @Query("SELECT m FROM MonthlyStatistics m WHERE m.year >= :startYear ORDER BY m.year DESC, m.month DESC")
    List<MonthlyStatistics> findRecentStatistics(@Param("startYear") Integer startYear);
}
