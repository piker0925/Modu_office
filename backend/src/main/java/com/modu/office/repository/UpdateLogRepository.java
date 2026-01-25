package com.modu.office.repository;

import com.modu.office.entity.UpdateLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * UpdateLog 엔티티에 대한 데이터 액세스 레포지토리
 */
@Repository
public interface UpdateLogRepository extends JpaRepository<UpdateLog, Long> {

    /**
     * 특정 예약의 모든 변경 로그 조회 (페이징 지원)
     */
    Page<UpdateLog> findByReservationId(Long reservationId, Pageable pageable);

    /**
     * 특정 사용자가 수행한 모든 변경 로그 조회
     */
    List<UpdateLog> findByActorId(Long actorId);
}
