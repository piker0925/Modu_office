package com.modu.office.repository;

import com.modu.office.entity.Account;
import com.modu.office.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByAccount(Account account);

    void deleteByAccount(Account account);
}
