package com.modu.office.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Office 생성/수정 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfficeRequest {

    @NotBlank(message = "지점 이름은 필수입니다.")
    @Size(min = 1, max = 150, message = "지점 이름은 1~150자 이내여야 합니다.")
    private String name;

    @NotBlank(message = "위치 정보는 필수입니다.")
    @Size(min = 1, max = 255, message = "위치 정보는 1~255자 이내여야 합니다.")
    private String location;
}
