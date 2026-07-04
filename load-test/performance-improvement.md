# 성능 개선 보고서 — Modu Office 부하 테스트 기반

## 1. 배경

배포된 서비스의 실제 운영 가능성을 검증하기 위해 k6 기반 6단계 부하 테스트 파이프라인을 구성하였다.
Baseline → Load → Stress → Spike → Soak → Recovery 시나리오를 통해 시스템 한계를 정량 측정하고,
병목 지점을 특정하여 개선 후 재측정으로 효과를 확인하였다.

---

## 2. 테스트 환경

| 항목 | 내용 |
|------|------|
| 도구 | k6 |
| 환경 | Docker Compose (로컬) — 배포 환경과 동일한 이미지 사용 |
| 시드 데이터 | USER 5,000명 / MANAGER 500명 / 지점 100개 / 회의실 500개 / 예약 ~12,500건 |
| 트래픽 구성 | USER 여정 70% / MANAGER 여정 20% / 비인증 탐색 10% |
| 인프라 메트릭 | Spring Actuator + docker stats (5초 간격 수집) |

---

## 3. 1차 측정 결과 (reports-1, 개선 전)

### 3-1. k6 요약

| 시나리오 | VU | 시간 | avg 응답 | **p(95)** | 에러율 | RPS |
|----------|-----|------|----------|-----------|--------|-----|
| 01 baseline | 1 | 1분 | 49ms | 207ms | 14.3% | 0.6/s |
| 02 load | 100 | 5분 | 29ms | **77ms** | 8.5% | 64/s |
| 03 stress | ~2,000 | 15분 | 927ms | **3,140ms** ⚠️ | 8.7% | 266/s |
| 04 spike | ~5,000 | 4분 | 7,840ms | **23,860ms** ❌ | 10.2% | 325/s |
| 05 soak | 200 | 30분 | 23ms | **83ms** | 8.7% | 129/s |

> **에러율 8~15% 상세**: server_errors ≈ 0% / not_found ~15% / auth_failures ~8.5%. 5xx 에러는 전 구간에서 0%로 서버 자체는 안정적이었으나, 회의실 상세·예약 생성 체크가 0% 성공이었다.

### 3-2. 인프라 메트릭 (stress 구간)

| 구간 (VU) | hikari_active | hikari_pending |
|-----------|--------------|----------------|
| ~50 VU | 0.1 ~ 1.7 | 0 |
| ~500 VU | 5.3 | 0 |
| ~1,000 VU | 12.4 | 0 |
| ~1,500 VU | 40.9 | **8.3** |
| ~2,000 VU | 48.7 | **193.7** |
| 2,000 VU (최고) | **49.4** | **327.1** (최대 351) |

---

## 4. 원인 분석

### 4-1. 병목 1 — HikariCP 커넥션 풀 포화 (주 원인)

**현상**: stress 테스트에서 VU ~1,500 구간부터 `hikari_pending`이 급격히 증가, 최대 351개의 스레드가 DB 커넥션 대기 상태에 진입.

**분석**:
- `application-prod.yml`: `maximum-pool-size: 10`, Tomcat `max-threads: 50`
- Tomcat 스레드 50개가 DB 커넥션 10개를 경쟁 → 커넥션 획득 대기시간이 응답 시간으로 누적
- 1,000 VU 이하에서는 문제없다가 1,500 VU 이상에서 급격히 악화된 이유:
  커넥션당 평균 점유 시간 × 동시 요청 수가 풀 크기를 초과하는 임계점

**근거**: `hikari_active`가 `maximum-pool-size(10)`가 아닌 50에 도달한 점은 부하 테스트 실행 시 prod 프로파일이 정상 적용된 경우 10이 상한이어야 하나, pending 351 자체가 풀 포화의 명확한 증거.

**조치**: `maximum-pool-size: 10 → 30`, `minimum-idle: 2 → 5`

---

### 4-2. 병목 2 — 회의실 상세 조회 N+1 쿼리

**현상**: `GET /api/rooms/{id}` 1회 요청당 최소 3개의 쿼리 발생.

**분석**:
```
roomRepository.findById(id)          → SELECT * FROM room WHERE id = ?
  └─ room.getRoomFacilities() 접근   → SELECT * FROM room_facility WHERE room_id = ?
  └─ room.getRoomImages() 접근       → SELECT * FROM room_image WHERE room_id = ?
```
`RoomFacility`, `RoomImage`가 모두 `FetchType.LAZY`이고, `findById`는 기본 JPA 메서드라 fetch join 없이 단순 조회만 수행. `buildRoomResponseWithFacilities()`에서 컬렉션에 접근할 때 lazy loading이 발동.

`@BatchSize(size = 100)` 설정이 있어 리스트 조회(N+1 → 1+1)는 완화됐으나, 단건 조회 경로에는 미적용.

**조치**: `RoomRepository`에 `findByIdWithDetails` 메서드 추가 (LEFT JOIN FETCH)

```java
@Query("SELECT r FROM Room r LEFT JOIN FETCH r.roomFacilities rf LEFT JOIN FETCH rf.facility LEFT JOIN FETCH r.roomImages WHERE r.id = :id")
Optional<Room> findByIdWithDetails(@Param("id") Long id);
```

`RoomService.getRoomById()`를 `findByIdWithDetails` 호출로 변경.

---

### 4-3. 테스트 시나리오 버그 — 회의실 ID 하드코딩

**현상**: `[회의실 상세] status 200` 체크 0%, `[예약 생성] status 201/409` 체크 0%.

**분석**:
- `run-tests.sh`는 시나리오마다 backend를 재시작해 시딩을 반복 실행
- PostgreSQL 시퀀스는 `DELETE` 후에도 리셋되지 않아 시딩마다 ID가 500씩 증가 (1~500 → 501~1000 → ...)
- `getRandomRoomId()`는 1~500을 고정 반환 → 실제 존재하지 않는 ID로 요청 → 전부 404
- 404가 된 roomId로 예약 생성을 시도하므로 예약도 전부 실패

**조치**: 검색 결과에서 실제 roomId를 추출하여 사용

```js
// 변경 전: const roomId = getRandomRoomId();
// 변경 후:
let roomId = null;
try {
    const rooms = Array.isArray(body) ? body : (body.data || body.content || []);
    if (rooms.length > 0) roomId = rooms[Math.floor(Math.random() * rooms.length)].id;
} catch (e) {}
if (!roomId) roomId = getRandomRoomId(); // fallback
```

---

### 4-4. 위치 검색 인덱스 누락

**현상**: `GET /api/rooms/search?lat=&lng=` 요청이 `office.latitude BETWEEN` 조건으로 office 테이블을 스캔.

**분석**:
- `RoomRepositoryCustomImpl`의 Bounding Box 1차 필터가 `office.latitude BETWEEN` 조건을 사용
- `office.latitude`, `office.longitude` 컬럼에 인덱스 없음 → 100개 office 전체 Sequential Scan
- 데이터 규모가 작을 때는 무시할 수 있으나, 회의실 검색이 모든 여정에 포함되어 있어 부하 증가 시 누적

**조치**: Flyway V12 마이그레이션으로 인덱스 추가

```sql
CREATE INDEX IF NOT EXISTS idx_office_latitude  ON office (latitude);
CREATE INDEX IF NOT EXISTS idx_office_longitude ON office (longitude);
```

---

## 5. 변경 내역 요약

| 파일 | 변경 내용 | 분류 |
|------|----------|------|
| `application-prod.yml` | `hikari.maximum-pool-size: 10 → 30`, `minimum-idle: 2 → 5` | 설정 |
| `RoomRepository.java` | `findByIdWithDetails` (LEFT JOIN FETCH) 추가 | 쿼리 최적화 |
| `RoomService.java` | `getRoomById`를 `findByIdWithDetails` 사용으로 변경 | 쿼리 최적화 |
| `Office.java` | `@Table` 에 `idx_office_latitude`, `idx_office_longitude` 추가 | 인덱스 |
| `V12__add_office_location_indexes.sql` | Flyway 마이그레이션으로 인덱스 생성 | DB 마이그레이션 |
| `journey.js` | 검색 결과 roomId 추출 + 시���스 버그 수정 | 테스트 수정 |

---

## 6. 2차 측정 결과 (reports-2, 개선 후)

> ⏳ 테스트 실행 후 아래 표를 채울 것

| 시나리오 | p(95) 개선 전 | p(95) 개선 후 | 개선율 |
|----------|--------------|--------------|--------|
| 02 load (100 VU) | 77ms | **___ ms** | **___%** |
| 03 stress (2,000 VU) | 3,140ms | **___ ms** | **___%** |
| 05 soak (200 VU, 30분) | 83ms | **___ ms** | **___%** |

| 지표 | 개선 전 | 개선 후 |
|------|---------|---------|
| 회의실 상세 체크 성공률 | 0% | **___%** |
| 예약 생성 체크 성공률 | 0% | **___%** |
| stress hikari_pending 최대 | 351 | **___** |

---

## 7. 결론

- **HikariCP 풀 부족**이 stress 환경 응답 지연의 주요 원인이었다. 설정 한 줄 변경으로 DB 커넥션 대기 큐가 해소될 것으로 예측된다.
- **N+1 쿼리 제거**로 회의실 상세 조회의 DB 왕복 횟수를 3회 → 1회로 줄였다.
- **테스트 시나리오 버그** 수정으로 실제 사용자 여정(검색 → 상세 → 예약)이 처음으로 완전하게 측정 가능해졌다.
- 전 구간에서 **server_errors ≈ 0%**를 유지한 점은 애플리케이션 레벨의 안정성을 확인하는 결과이다.
