# Analytics Service Swagger API 문서

## 개요
Analytics Service에 Swagger UI가 통합되어 차량 분석 및 통계 API 문서를 쉽게 확인하고 테스트할 수 있습니다.

## 접근 방법

### 1. Swagger UI
- **URL**: `http://localhost:8080/swagger-ui.html`
- **설명**: 웹 기반 API 문서 및 테스트 인터페이스

### 2. OpenAPI JSON
- **URL**: `http://localhost:8080/api-docs`
- **설명**: OpenAPI 3.0 스펙에 따른 JSON 형태의 API 문서

## 주요 기능

### API 그룹

#### 1. Analytics (차량 분석)
- **운행 기록 저장**: 새로운 운행 기록을 저장
- **차량별 통계 조회**: 특정 차량의 통계 정보 조회
- **전체 차량 통계 조회**: 모든 차량의 통계 정보 조회
- **차량 통계 일괄 조회**: 여러 차량의 통계 정보를 일괄적으로 조회

#### 2. Statistics (통계 데이터)
- **통계 요약 조회**: 전체 시스템의 통계 요약 정보 조회
- **캐시된 통계 요약 조회**: Redis 캐시에서 통계 요약 정보 조회

## 상세 정보
각 API 엔드포인트는 다음 정보를 포함합니다:
- **요약**: API 기능 간단 설명
- **상세 설명**: API 동작 방식 상세 설명
- **요청/응답 스키마**: DTO 클래스 구조
- **응답 코드**: HTTP 상태 코드별 설명
- **예시 값**: 실제 사용 예시

## DTO 문서화

### 주요 DTO 클래스

#### 1. ApiResponse<T>
- **설명**: API 응답 공통 DTO
- **필드**: success, message, data, errors
- **용도**: 모든 API 응답의 표준화된 형식

#### 2. TripRecordDto
- **설명**: 운행 기록 DTO
- **주요 필드**: vehicleId, startTime, endTime, totalDistance, fuelConsumed
- **용도**: 운행 기록 저장 및 조회

#### 3. VehicleStatisticsResponse
- **설명**: 차량 통계 응답 DTO
- **주요 필드**: totalDistance, monthlyDistance, totalTrips, monthlyTrips
- **용도**: 차량별 통계 정보 제공

#### 4. StatisticsSummaryResponse
- **설명**: 통계 요약 응답 DTO
- **주요 필드**: overall, hourlyTripCounts, dailyTripCounts, monthlyTripCounts
- **용도**: 시스템 전체 통계 요약 정보 제공

## 설정

### SwaggerConfig.java
- API 정보 (제목, 설명, 버전, 연락처, 라이선스)
- 서버 정보 (로컬, 프로덕션)

### application.properties
- API 문서 경로 설정
- Swagger UI 커스터마이징 옵션
- 정렬 및 표시 옵션

## 사용 예시

### 1. 운행 기록 저장
```bash
POST /api/v1/analytics/trip-records
Content-Type: application/json

{
  "vehicleId": "CAR001",
  "vehicleName": "현대 아반떼",
  "startTime": "2024-01-15T09:00:00",
  "endTime": "2024-01-15T17:00:00",
  "totalDistance": 150.5,
  "startLatitude": 37.5665,
  "startLongitude": 126.9780,
  "endLatitude": 37.5665,
  "endLongitude": 126.9780,
  "fuelConsumed": 12.5
}
```

### 2. 차량별 통계 조회
```bash
GET /api/v1/analytics/vehicles/CAR001/statistics
```

### 3. 전체 차량 통계 조회
```bash
GET /api/v1/analytics/vehicles/statistics
```

### 4. 차량 통계 일괄 조회
```bash
POST /api/v1/analytics/vehicles/statistics/batch
Content-Type: application/json

["CAR001", "CAR002", "CAR003"]
```

### 5. 통계 요약 조회
```bash
GET /api/v1/analytics/statistics/summary
```

### 6. 캐시된 통계 요약 조회
```bash
GET /api/v1/analytics/statistics/summary/cache
```

## 데이터 구조

### 통계 데이터 예시
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "overall": {
      "totalTripCount": 1500,
      "totalDistance": 15000.5,
      "totalDuration": 45000,
      "averageDistance": 10.0,
      "averageDuration": 30
    },
    "hourlyTripCounts": {
      "9": 15,
      "10": 20,
      "17": 25
    },
    "dailyTripCounts": {
      "MONDAY": 50,
      "TUESDAY": 45,
      "WEDNESDAY": 55
    },
    "monthlyTripCounts": {
      "1": 150,
      "2": 180,
      "3": 200
    }
  }
}
```

## 보안

- 모든 API는 적절한 인증 및 권한 검증이 필요합니다
- Swagger UI는 개발 환경에서만 활성화하는 것을 권장합니다

## 문제 해결

### Swagger UI가 표시되지 않는 경우
1. 애플리케이션이 정상적으로 실행되었는지 확인
2. 포트 8080이 올바르게 설정되었는지 확인
3. 의존성이 올바르게 추가되었는지 확인

### API 문서가 업데이트되지 않는 경우
1. 애플리케이션 재시작
2. 캐시 클리어
3. 브라우저 새로고침

## 추가 정보

- **Spring Boot Version**: 3.2.0
- **SpringDoc OpenAPI Version**: 2.0.4
- **Java Version**: 17
- **OpenAPI Specification**: 3.0
- **주요 기능**: 차량 운행 분석, 통계 데이터 처리, Redis 캐싱
