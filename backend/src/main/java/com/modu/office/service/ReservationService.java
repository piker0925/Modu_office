package com.modu.office.service;

import com.modu.office.dto.request.ReservationRequest;
import com.modu.office.dto.request.ReservationUpdateRequest;
import com.modu.office.dto.response.ReservationResponse;
import com.modu.office.entity.AppUser;
import com.modu.office.entity.Office;
import com.modu.office.entity.OfficeRoom;
import com.modu.office.entity.Reservation;
import com.modu.office.entity.enums.ReservationStatus;
import com.modu.office.repository.AppUserRepository;
import com.modu.office.repository.OfficeRepository;
import com.modu.office.repository.OfficeRoomRepository;
import com.modu.office.repository.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Reservation 비즈니스 로직 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final OfficeRepository officeRepository;
    private final OfficeRoomRepository officeRoomRepository;
    private final AppUserRepository appUserRepository;

    /**
     * 새 예약 생성
     */
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        // 1. 시간 범위 유효성 검증
        validateTimeRange(request.getStartAt(), request.getEndAt());

        // 2. 관련 엔티티 존재 확인
        Office office = officeRepository.findById(request.getOfficeId())
                .orElseThrow(() -> new EntityNotFoundException("지점을 찾을 수 없습니다. ID: " + request.getOfficeId()));

        OfficeRoom room = officeRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new EntityNotFoundException("회의실을 찾을 수 없습니다. ID: " + request.getRoomId()));

        AppUser customer = appUserRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + request.getCustomerId()));

        // 3. 회의실이 해당 지점에 속하는지 확인
        if (!room.getOffice().getId().equals(office.getId())) {
            throw new IllegalArgumentException("회의실이 해당 지점에 속하지 않습니다.");
        }

        // 4. 시간 충돌 확인 (PENDING, CONFIRMED 상태만 확인)
        List<ReservationStatus> activeStatuses = Arrays.asList(
                ReservationStatus.PENDING,
                ReservationStatus.CONFIRMED);
        List<Reservation> conflicts = reservationRepository.findConflictingReservations(
                room.getId(),
                request.getStartAt(),
                request.getEndAt(),
                activeStatuses);

        if (!conflicts.isEmpty()) {
            throw new IllegalStateException("해당 시간대에 이미 예약이 존재합니다.");
        }

        // 5. 예약 생성
        Reservation reservation = Reservation.builder()
                .title(request.getTitle())
                .office(office)
                .room(room)
                .customer(customer)
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .status(ReservationStatus.PENDING)
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);
        return ReservationResponse.fromEntity(savedReservation);
    }

    /**
     * ID로 예약 조회
     */
    public ReservationResponse getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("예약을 찾을 수 없습니다. ID: " + id));
        return ReservationResponse.fromEntity(reservation);
    }

    /**
     * 모든 예약 조회
     */
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 특정 사용자의 예약 조회
     */
    public List<ReservationResponse> getReservationsByCustomer(Long customerId) {
        return reservationRepository.findByCustomerId(customerId).stream()
                .map(ReservationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 특정 회의실의 예약 조회
     */
    public List<ReservationResponse> getReservationsByRoom(Long roomId) {
        return reservationRepository.findByRoomId(roomId).stream()
                .map(ReservationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 상태별 예약 조회
     */
    public List<ReservationResponse> getReservationsByStatus(ReservationStatus status) {
        return reservationRepository.findByStatus(status).stream()
                .map(ReservationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 예약 정보 수정
     */
    @Transactional
    public ReservationResponse updateReservation(Long id, ReservationUpdateRequest request) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("예약을 찾을 수 없습니다. ID: " + id));

        // 취소된 예약은 수정 불가
        if (reservation.isCancelled()) {
            throw new IllegalStateException("취소된 예약은 수정할 수 없습니다.");
        }

        // 시간 수정
        if (request.getStartAt() != null && request.getEndAt() != null) {
            validateTimeRange(request.getStartAt(), request.getEndAt());

            // 시간 충돌 확인 (현재 예약 제외)
            List<ReservationStatus> activeStatuses = Arrays.asList(
                    ReservationStatus.PENDING,
                    ReservationStatus.CONFIRMED);
            List<Reservation> conflicts = reservationRepository.findConflictingReservationsExcluding(
                    reservation.getRoom().getId(),
                    id,
                    request.getStartAt(),
                    request.getEndAt(),
                    activeStatuses);

            if (!conflicts.isEmpty()) {
                throw new IllegalStateException("해당 시간대에 이미 예약이 존재합니다.");
            }

            reservation.updateTimeRange(request.getStartAt(), request.getEndAt());
        }

        // 상태 수정
        if (request.getStatus() != null) {
            reservation.updateStatus(request.getStatus());
        }

        return ReservationResponse.fromEntity(reservation);
    }

    /**
     * 예약 상태 변경
     */
    @Transactional
    public ReservationResponse updateReservationStatus(Long id, ReservationStatus newStatus) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("예약을 찾을 수 없습니다. ID: " + id));

        reservation.updateStatus(newStatus);
        return ReservationResponse.fromEntity(reservation);
    }

    /**
     * 예약 취소 (soft delete)
     */
    @Transactional
    public void cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("예약을 찾을 수 없습니다. ID: " + id));

        if (reservation.isCancelled()) {
            throw new IllegalStateException("이미 취소된 예약입니다.");
        }

        reservation.cancel();
    }

    /**
     * 예약 삭제 (hard delete)
     */
    @Transactional
    public void deleteReservation(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new EntityNotFoundException("예약을 찾을 수 없습니다. ID: " + id);
        }
        reservationRepository.deleteById(id);
    }

    /**
     * 시간 범위 유효성 검증
     */
    private void validateTimeRange(LocalDateTime startAt, LocalDateTime endAt) {
        if (endAt.isBefore(startAt) || endAt.isEqual(startAt)) {
            throw new IllegalArgumentException("종료 시간은 시작 시간 이후여야 합니다.");
        }

        LocalDateTime now = LocalDateTime.now();
        if (startAt.isBefore(now)) {
            throw new IllegalArgumentException("시작 시간은 현재 시간 이후여야 합니다.");
        }
    }
}
