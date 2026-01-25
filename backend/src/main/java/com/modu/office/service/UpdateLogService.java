package com.modu.office.service;

import com.modu.office.dto.response.UpdateLogResponse;
import com.modu.office.entity.UpdateLog;
import com.modu.office.repository.UpdateLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UpdateLog 조회 비즈니스 로직 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UpdateLogService {

    private final UpdateLogRepository updateLogRepository;

    /**
     * 전체 감사 로그 조회 (최신순, 페이징)
     */
    public Page<UpdateLogResponse> getAllLogs(Pageable pageable) {
        // 최신순 정렬을 보장
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "occurredAt"));

        Page<UpdateLog> logs = updateLogRepository.findAll(sortedPageable);
        return logs.map(UpdateLogResponse::fromEntity);
    }

    /**
     * 특정 예약의 감사 로그 조회 (최신순, 페이징)
     */
    public Page<UpdateLogResponse> getLogsByReservation(Long reservationId, Pageable pageable) {
        // 최신순 정렬을 보장
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "occurredAt"));

        Page<UpdateLog> logs = updateLogRepository.findByReservationId(reservationId, sortedPageable);
        return logs.map(UpdateLogResponse::fromEntity);
    }
}
