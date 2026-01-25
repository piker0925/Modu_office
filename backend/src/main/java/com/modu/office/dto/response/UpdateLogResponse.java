package com.modu.office.dto.response;

import com.modu.office.entity.UpdateLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * UpdateLog 응답 DTO
 * 감사 로그 조회 시 사용되며, before_data와 after_data를 포함
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLogResponse {

    private Long id;

    // Reservation info
    private Long reservationId;
    private String reservationTitle;

    // Log action
    private String action; // CREATE, UPDATE, CANCEL

    // Actor info
    private Long actorId;
    private String actorName;

    // Change tracking (JSONB data)
    private Map<String, Object> beforeData;
    private Map<String, Object> afterData;

    // Timestamp
    private LocalDateTime occurredAt;

    /**
     * Entity를 DTO로 변환
     */
    public static UpdateLogResponse fromEntity(UpdateLog log) {
        return UpdateLogResponse.builder()
                .id(log.getId())
                .reservationId(log.getReservation().getId())
                .reservationTitle(log.getReservation().getTitle())
                .action(log.getAction().name())
                .actorId(log.getActor().getId())
                .actorName(log.getActor().getName())
                .beforeData(log.getBeforeData())
                .afterData(log.getAfterData())
                .occurredAt(log.getOccurredAt())
                .build();
    }
}
