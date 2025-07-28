package com.example.DemoSpringBootTinasoft.repositories;

import com.example.DemoSpringBootTinasoft.entities.Token;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    Optional<Token> findByToken(String token);

    // Query để xóa các token đã hết hạn
    @Transactional
    @Modifying
    @Query("DELETE FROM Token t WHERE t.expiresAt <= :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
}