package com.modu.office.repository;

import com.modu.office.entity.OfficeRoom;
import com.modu.office.entity.enums.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * OfficeRoom 엔티티에 대한 데이터 액세스 레포지토리
 */
@Repository
public interface OfficeRoomRepository extends JpaRepository<OfficeRoom, Long> {

    /**
     * 특정 지점의 모든 회의실 조회
     * 
     * @param officeId 지점 ID
     * @return 해당 지점의 회의실 목록
     */
    List<OfficeRoom> findByOfficeId(Long officeId);

    /**
     * 지점 ID와 회의실 코드로 회의실 찾기 (유니크 제약조건 활용)
     * 
     * @param officeId 지점 ID
     * @param roomCode 회의실 코드
     * @return 해당 회의실 (Optional)
     */
    Optional<OfficeRoom> findByOfficeIdAndRoomCode(Long officeId, String roomCode);

    /**
     * 특정 지점에서 상태별로 회의실 조회
     * 
     * @param officeId 지점 ID
     * @param status   회의실 상태
     * @return 해당 상태의 회의실 목록
     */
    List<OfficeRoom> findByOfficeIdAndStatus(Long officeId, RoomStatus status);

    /**
     * 특정 지점에서 최소 수용 인원 이상인 회의실 조회
     * 
     * @param officeId 지점 ID
     * @param capacity 최소 수용 인원
     * @return 조건에 맞는 회의실 목록
     */
    List<OfficeRoom> findByOfficeIdAndCapacityGreaterThanEqual(Long officeId, Integer capacity);
}
