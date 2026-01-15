package com.modu.office.service;

import com.modu.office.dto.request.OfficeRequest;
import com.modu.office.dto.response.OfficeResponse;
import com.modu.office.entity.Office;
import com.modu.office.repository.OfficeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Office 비즈니스 로직 서비스
 */
@SuppressWarnings("null")
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OfficeService {

    private final OfficeRepository officeRepository;

    /**
     * 새 지점 생성
     */
    @Transactional
    public OfficeResponse createOffice(OfficeRequest request) {
        Office office = Office.builder()
                .name(request.getName())
                .location(request.getLocation())
                .build();

        Office savedOffice = officeRepository.save(office);
        return OfficeResponse.fromEntity(savedOffice);
    }

    /**
     * ID로 지점 조회
     */
    public OfficeResponse getOfficeById(Long id) {
        Office office = officeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("지점을 찾을 수 없습니다. ID: " + id));
        return OfficeResponse.fromEntity(office);
    }

    /**
     * 모든 지점 조회
     */
    public List<OfficeResponse> getAllOffices() {
        return officeRepository.findAll().stream()
                .map(OfficeResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 지점 정보 수정
     */
    @Transactional
    public OfficeResponse updateOffice(Long id, OfficeRequest request) {
        Office office = officeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("지점을 찾을 수 없습니다. ID: " + id));

        office.updateInfo(request.getName(), request.getLocation());

        return OfficeResponse.fromEntity(office);
    }

    /**
     * 지점 삭제 (연결된 회의실도 함께 삭제됨)
     */
    @Transactional
    public void deleteOffice(Long id) {
        if (!officeRepository.existsById(id)) {
            throw new EntityNotFoundException("지점을 찾을 수 없습니다. ID: " + id);
        }
        officeRepository.deleteById(id);
    }

    /**
     * 이름으로 지점 검색
     */
    public List<OfficeResponse> searchOfficesByName(String name) {
        return officeRepository.findByNameContaining(name).stream()
                .map(OfficeResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 위치로 지점 검색
     */
    public List<OfficeResponse> searchOfficesByLocation(String location) {
        return officeRepository.findByLocationContaining(location).stream()
                .map(OfficeResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
