package com.modu.office.dto.request;

import com.modu.office.entity.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Reservation 수정 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationUpdateRequest {

    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private ReservationStatus status;
}
