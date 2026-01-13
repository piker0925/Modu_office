package com.modu.office.domain;

import com.modu.office.domain.enums.RoomStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 지점 내 개별 회의실 정보를 관리하는 엔티티
 */
@Entity
@Getter
@Table(name = "office_room", uniqueConstraints = {
        @UniqueConstraint(name = "uq_room_office_roomcode", columnNames = { "office_id", "room_code" }),
        // ✅ A안 핵심: (id, office_id) 조합의 유일성을 보장하여 Reservation에서 이를 복합 FK로 참조할 수 있게 함
        @UniqueConstraint(name = "uq_office_room_id_office", columnNames = { "id", "office_id" })
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OfficeRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "office_id", nullable = false)
    private Office office;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "room_code", nullable = false, length = 50)
    private String roomCode;

    @Column(name = "floor")
    private Integer floor;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RoomStatus status = RoomStatus.AVAILABLE;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "category", length = 100)
    private String category;

    @Builder
    public OfficeRoom(Office office, String name, String roomCode, Integer floor, RoomStatus status, Integer capacity,
            String category) {
        this.office = office;
        this.name = name;
        this.roomCode = roomCode;
        this.floor = floor;
        this.status = status != null ? status : RoomStatus.AVAILABLE;
        this.capacity = capacity;
        this.category = category;
    }
}
