package com.kt_giga_fms.analytics.repository;

import com.kt_giga_fms.analytics.domain.VehicleStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleStatisticsRepository extends JpaRepository<VehicleStatistics, Long> {
    
    Optional<VehicleStatistics> findByVehicleIdAndYearMonth(String vehicleId, String yearMonth);
    
    List<VehicleStatistics> findByVehicleId(String vehicleId);
    
    @Query("SELECT v FROM VehicleStatistics v WHERE v.vehicleId = :vehicleId ORDER BY v.yearMonth DESC")
    List<VehicleStatistics> findByVehicleIdOrderByYearMonthDesc(@Param("vehicleId") String vehicleId);
    
    @Query("SELECT v FROM VehicleStatistics v WHERE v.yearMonth = :yearMonth")
    List<VehicleStatistics> findByYearMonth(@Param("yearMonth") String yearMonth);
}
