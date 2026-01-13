package com.modu.modu_office.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "office")
    private List<OfficeRoom> rooms = new ArrayList<>();

    @Builder
    public Office(String name, String location) {
        this.name = name;
        this.location = location;
    }
}
