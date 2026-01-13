package com.modu.office.domain;

import com.modu.office.config.JpaConfig;
import com.modu.office.domain.enums.RoomStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
class EntityMappingTest {

    @Autowired
    private OfficeRepository officeRepository;

    @Autowired
    private OfficeRoomRepository officeRoomRepository;

    @Test
    @DisplayName("지점과 회의실 엔티티 매핑 및 감사 기능 확인")
    @SuppressWarnings("null")
    void officeAndRoomMappingTest() {
        // given
        Office office = Office.builder()
                .name("서울 본점")
                .location("서울특별시 강남구")
                .build();
        Office savedOffice = Objects.requireNonNull(officeRepository.save(office));
        assertThat(savedOffice).isNotNull();

        OfficeRoom room = OfficeRoom.builder()
                .office(savedOffice)
                .name("대회의실 1")
                .roomCode("SEOUL-CONF-01")
                .capacity(10)
                .status(RoomStatus.AVAILABLE)
                .build();

        // when
        OfficeRoom savedRoom = Objects.requireNonNull(officeRoomRepository.save(room));
        assertThat(savedRoom).isNotNull();

        // then
        assertThat(savedOffice.getId()).isNotNull();
        assertThat(savedOffice.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(savedOffice.getUpdatedAt()).isBeforeOrEqualTo(LocalDateTime.now());

        assertThat(savedRoom.getId()).isNotNull();
        assertThat(savedRoom.getOffice().getId()).isEqualTo(savedOffice.getId());
        assertThat(savedRoom.getName()).isEqualTo("대회의실 1");
        assertThat(savedRoom.getCapacity()).isEqualTo(10);
        assertThat(savedRoom.getCreatedAt()).isNotNull();
    }
}
