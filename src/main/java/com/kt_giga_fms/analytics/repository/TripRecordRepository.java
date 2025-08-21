package com.kt_giga_fms.analytics.repository;

import com.kt_giga_fms.analytics.domain.TripRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TripRecordRepository extends JpaRepository<TripRecord, Long> {
    
    List<TripRecord> findByVehicleId(String vehicleId);
    
    @Query("SELECT t FROM TripRecord t WHERE t.vehicleId = :vehicleId AND t.startTime >= :startDate AND t.startTime < :endDate")
    List<TripRecord> findByVehicleIdAndDateRange(@Param("vehicleId") String vehicleId, 
                                                @Param("startDate") LocalDateTime startDate, 
                                                @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COALESCE(SUM(t.totalDistance), 0) FROM TripRecord t WHERE t.vehicleId = :vehicleId")
    BigDecimal getTotalDistanceByVehicleId(@Param("vehicleId") String vehicleId);
    
    @Query("SELECT COALESCE(SUM(t.totalDistance), 0) FROM TripRecord t WHERE t.vehicleId = :vehicleId AND t.startTime >= :startDate AND t.startTime < :endDate")
    BigDecimal getMonthlyDistanceByVehicleId(@Param("vehicleId") String vehicleId, 
                                           @Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(t) FROM TripRecord t WHERE t.vehicleId = :vehicleId")
    Long getTotalTripsByVehicleId(@Param("vehicleId") String vehicleId);
    
    @Query("SELECT COUNT(t) FROM TripRecord t WHERE t.vehicleId = :vehicleId AND t.startTime >= :startDate AND t.startTime < :endDate")
    Long getMonthlyTripsByVehicleId(@Param("vehicleId") String vehicleId, 
                                   @Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate);
    
    // 날짜 범위로 운행 기록 조회
    @Query("SELECT t FROM TripRecord t WHERE t.startTime BETWEEN :startTime AND :endTime ORDER BY t.startTime")
    List<TripRecord> findByStartTimeBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
