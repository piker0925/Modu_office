---
description: "낙관적 락 기반 동시성 제어 및 PostgreSQL 데이터 무결성 규칙"
globs:
  - "src/main/java/**/entity/*.java"
  - "src/main/java/**/repository/*.java"
  - "src/main/java/**/service/ReservationService.java"
alwaysApply: true
---

# 🔒 Concurrency & DB Integrity

## 1. Optimistic Locking (@Version)
- 예약(Reservation) 및 자원(OfficeRoom) 엔티티에는 반드시 `private Long version;` 필드와 `@Version` 어노테이션을 추가하십시오.
- 수정 로직 작성 시 `OptimisticLockingFailureException` 발생 상황을 고려하여 재시도 로직을 제안하십시오.

## 2. PostgreSQL Schema
- ENUM 타입 사용 시 `PostgreSQLEnumType` 설정을 확인하고, Java Enum과 `@Enumerated(EnumType.STRING)`으로 매핑하십시오.
- 중복 예약 방지를 위해 DB 레벨의 `UNIQUE INDEX` (status != 'CANCELED' 조건 포함) 활용을 고려하십시오.

## 3. Composite Foreign Keys
- `office_id`와 `room_id`가 결합된 복합 외래키 구조를 사용하여 데이터 정합성을 유지하십시오.