package com.modu.office.domain;

import com.modu.office.domain.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Table(name = "account")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class Account extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false, columnDefinition = "TEXT")
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "account_status")
    private AccountStatus status = AccountStatus.ACTIVE;

    @Builder
    public Account(String email, String passwordHash, AccountStatus status) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.status = status != null ? status : AccountStatus.ACTIVE;
    }
}
