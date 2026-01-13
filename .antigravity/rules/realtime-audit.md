---
description: "WebSocket(STOMP) 실시간 알림 및 감사 로그(Audit Log) 구현 규칙"
globs:
  - "src/main/java/**/service/*.java"
  - "src/main/java/**/controller/WebSocketController.java"
alwaysApply: true
---

# 📡 Real-time & Audit Trail

## 1. WebSocket (STOMP)
- 예약 상태가 변경(생성, 수정, 취소)될 때 반드시 `SimpMessagingTemplate`을 사용하여 `/topic/reservations` 채널로 메시지를 발행(Publish)하십시오.
- 전송되는 메시지는 클라이언트가 즉시 UI를 갱신할 수 있는 최소한의 필수 정보(예약 ID, 상태, 변경 시간)를 포함해야 합니다.

## 2. Audit Logging
- 예약의 상태 변화가 일어날 때 `update_log` 테이블에 `before_data`와 `after_data`를 JSON 형식으로 저장하는 로직을 포함하십시오.
- 로그에는 `actor_user_id`(변경 수행자) 정보를 반드시 기록하십시오.