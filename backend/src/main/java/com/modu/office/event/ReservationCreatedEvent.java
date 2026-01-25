package com.modu.office.event;

import com.modu.office.entity.AppUser;
import com.modu.office.entity.Reservation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 예약 생성 시 발행되는 이벤트
 */
@Getter
@RequiredArgsConstructor
public class ReservationCreatedEvent {

    private final Reservation reservation;
    private final AppUser actor;
}
