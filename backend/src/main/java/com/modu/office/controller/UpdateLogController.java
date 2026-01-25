package com.modu.office.controller;

import com.modu.office.common.ApiResponse;
import com.modu.office.dto.response.UpdateLogResponse;
import com.modu.office.service.UpdateLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * UpdateLog 조회 REST API Controller
 * 관리자용 감사 로그 조회 기능 제공
 */
@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class UpdateLogController {

    private final UpdateLogService updateLogService;

    /**
     * 전체 감사 로그 조회 (페이징)
     * 기본: 20개씩, 최신순
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UpdateLogResponse>>> getAllLogs(
            @PageableDefault(size = 20, sort = "occurredAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<UpdateLogResponse> logs = updateLogService.getAllLogs(pageable);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    /**
     * 특정 예약의 감사 로그 조회 (페이징)
     * 기본: 20개씩, 최신순
     */
    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<ApiResponse<Page<UpdateLogResponse>>> getLogsByReservation(
            @PathVariable Long reservationId,
            @PageableDefault(size = 20, sort = "occurredAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<UpdateLogResponse> logs = updateLogService.getLogsByReservation(reservationId, pageable);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }
}
