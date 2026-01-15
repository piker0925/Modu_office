package com.modu.office.dto.response;

import com.modu.office.entity.Office;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Office 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfficeResponse {

    private Long id;
    private String name;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity를 DTO로 변환
     */
    public static OfficeResponse fromEntity(Office office) {
        return OfficeResponse.builder()
                .id(office.getId())
                .name(office.getName())
                .location(office.getLocation())
                .createdAt(office.getCreatedAt())
                .updatedAt(office.getUpdatedAt())
                .build();
    }
}
