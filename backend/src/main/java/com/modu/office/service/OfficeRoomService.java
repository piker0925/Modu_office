package com.modu.office.service;

import com.modu.office.dto.request.OfficeRoomRequest;
import com.modu.office.dto.response.OfficeRoomResponse;
import com.modu.office.entity.Office;
import com.modu.office.entity.OfficeRoom;
import com.modu.office.entity.enums.RoomStatus;
import com.modu.office.repository.OfficeRepository;
import com.modu.office.repository.OfficeRoomRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * OfficeRoom 비즈니스 로직 서비스
 */
@SuppressWarnings("null")
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OfficeRoomService {

    private final OfficeRoomRepository officeRoomRepository;
    private final OfficeRepository officeRepository;

    /**
     * 새 회의실 생성
     */
    @Transactional
    public OfficeRoomResponse createRoom(Long officeId, OfficeRoomRequest request) {
        Office office = officeRepository.findById(officeId)
                .orElseThrow(() -> new EntityNotFoundException("지점을 찾을 수 없습니다. ID: " + officeId));

        OfficeRoom room = OfficeRoom.builder()
                .office(office)
                .name(request.getName())
                .roomCode(request.getRoomCode())
                .floor(request.getFloor())
                .status(request.getStatus())
                .capacity(request.getCapacity())
                .category(request.getCategory())
                .build();

        OfficeRoom savedRoom = officeRoomRepository.save(room);
        return OfficeRoomResponse.fromEntity(savedRoom);
    }

    /**
     * ID로 회의실 조회
     */
    public OfficeRoomResponse getRoomById(Long roomId) {
        OfficeRoom room = officeRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("회의실을 찾을 수 없습니다. ID: " + roomId));
        return OfficeRoomResponse.fromEntity(room);
    }

    /**
     * 특정 지점의 모든 회의실 조회
     */
    public List<OfficeRoomResponse> getRoomsByOfficeId(Long officeId) {
        // 지점 존재 여부 확인
        if (!officeRepository.existsById(officeId)) {
            throw new EntityNotFoundException("지점을 찾을 수 없습니다. ID: " + officeId);
        }

        return officeRoomRepository.findByOfficeId(officeId).stream()
                .map(OfficeRoomResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 회의실 정보 수정
     */
    @Transactional
    public OfficeRoomResponse updateRoom(Long roomId, OfficeRoomRequest request) {
        OfficeRoom room = officeRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("회의실을 찾을 수 없습니다. ID: " + roomId));

        room.updateInfo(
                request.getName(),
                request.getRoomCode(),
                request.getFloor(),
                request.getStatus(),
                request.getCapacity(),
                request.getCategory());

        return OfficeRoomResponse.fromEntity(room);
    }

    /**
     * 회의실 삭제
     */
    @Transactional
    public void deleteRoom(Long roomId) {
        if (!officeRoomRepository.existsById(roomId)) {
            throw new EntityNotFoundException("회의실을 찾을 수 없습니다. ID: " + roomId);
        }
        officeRoomRepository.deleteById(roomId);
    }

    /**
     * 특정 지점에서 상태별로 회의실 조회
     */
    public List<OfficeRoomResponse> getRoomsByStatus(Long officeId, RoomStatus status) {
        return officeRoomRepository.findByOfficeIdAndStatus(officeId, status).stream()
                .map(OfficeRoomResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 특정 지점에서 최소 수용 인원 이상인 회의실 조회
     */
    public List<OfficeRoomResponse> getRoomsByMinCapacity(Long officeId, Integer minCapacity) {
        return officeRoomRepository.findByOfficeIdAndCapacityGreaterThanEqual(officeId, minCapacity).stream()
                .map(OfficeRoomResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
