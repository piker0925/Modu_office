package com.modu.office.dto.response;

import com.modu.office.entity.Reservation;
import com.modu.office.entity.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Reservation 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {

    private Long id;
    private String title;

    // Office info
    private Long officeId;
    private String officeName;

    // Room info
    private Long roomId;
    private String roomName;
    private String roomCode;

    // Customer info
    private Long customerId;
    private String customerName;

    // Reservation details
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private ReservationStatus status;

    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;

    /**
     * Entity를 DTO로 변환
     */
    public static ReservationResponse fromEntity(Reservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .title(reservation.getTitle())
                .officeId(reservation.getOffice().getId())
                .officeName(reservation.getOffice().getName())
                .roomId(reservation.getRoom().getId())
                .roomName(reservation.getRoom().getName())
                .roomCode(reservation.getRoom().getRoomCode())
                .customerId(reservation.getCustomer().getId())
                .customerName(reservation.getCustomer().getName())
                .startAt(reservation.getStartAt())
                .endAt(reservation.getEndAt())
                .status(reservation.getStatus())
                .createdAt(reservation.getCreatedAt())
                .updatedAt(reservation.getUpdatedAt())
                .version(reservation.getVersion())
                .build();
    }
}
