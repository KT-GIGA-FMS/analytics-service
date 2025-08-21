package com.kt_giga_fms.analytics.service;

import com.kt_giga_fms.analytics.domain.TripRecord;
import com.kt_giga_fms.analytics.repository.TripRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class SampleDataService {

    private final TripRecordRepository tripRecordRepository;
    private final Random random = new Random();

    @EventListener(ApplicationReadyEvent.class)
    public void generateSampleData() {
        // 이미 데이터가 있으면 생성하지 않음
        if (tripRecordRepository.count() > 0) {
            log.info("샘플 데이터가 이미 존재합니다. 건너뜁니다.");
            return;
        }

        log.info("샘플 운행 데이터 생성 시작");
        
        // 최근 3개월간의 샘플 데이터 생성
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minus(3, ChronoUnit.MONTHS);
        
        for (int i = 0; i < 150; i++) {
            TripRecord trip = createSampleTrip(startDate, now, i);
            tripRecordRepository.save(trip);
        }
        
        log.info("샘플 운행 데이터 150건 생성 완료");
    }

    private TripRecord createSampleTrip(LocalDateTime startDate, LocalDateTime endDate, int index) {
        // 랜덤 시작 시간 (최근 3개월 내)
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        long randomDays = random.nextInt((int) daysBetween);
        LocalDateTime tripStart = startDate.plusDays(randomDays);
        
        // 랜덤 시작 시간 (0-23시)
        int randomHour = random.nextInt(24);
        tripStart = tripStart.withHour(randomHour).withMinute(random.nextInt(60));
        
        // 랜덤 운행 시간 (30분 ~ 3시간)
        int durationMinutes = 30 + random.nextInt(150);
        LocalDateTime tripEnd = tripStart.plusMinutes(durationMinutes);
        
        // 랜덤 거리 (5km ~ 50km)
        BigDecimal distance = BigDecimal.valueOf(5 + random.nextDouble() * 45)
            .setScale(2, BigDecimal.ROUND_HALF_UP);
        
        // 랜덤 차량 ID
        String vehicleId = "VEH" + String.format("%03d", 1 + random.nextInt(20));
        
        return new TripRecord(
            vehicleId,
            "차량" + vehicleId,
            tripStart,
            tripEnd,
            distance,
            37.5665 + (random.nextDouble() - 0.5) * 0.1, // 서울 위도
            126.9780 + (random.nextDouble() - 0.5) * 0.1, // 서울 경도
            37.5665 + (random.nextDouble() - 0.5) * 0.1, // 서울 위도
            126.9780 + (random.nextDouble() - 0.5) * 0.1, // 서울 경도
            BigDecimal.valueOf(2 + random.nextDouble() * 8)
                .setScale(2, BigDecimal.ROUND_HALF_UP)
        );
    }
}
