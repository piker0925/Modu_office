package com.modu.office.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

/**
 * 지점 정보를 관리하는 엔티티
 */
@Entity
@Getter
@Table(name = "office")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Office extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "location", nullable = false, length = 255)
    private String location;

    @OneToMany(mappedBy = "office", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficeRoom> rooms = new ArrayList<>();

    @Builder
    public Office(String name, String location) {
        this.name = name;
        this.location = location;
    }

    public void addRoom(OfficeRoom room) {
        this.rooms.add(room);
    }
}
