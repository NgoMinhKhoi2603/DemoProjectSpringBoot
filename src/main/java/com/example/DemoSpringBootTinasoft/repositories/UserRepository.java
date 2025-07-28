package com.example.DemoSpringBootTinasoft.repositories;

import com.example.DemoSpringBootTinasoft.entities.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    // Query để xóa user chưa kích hoạt sau 24h
    @Transactional
    @Modifying
    @Query("DELETE FROM User u WHERE u.enabled = false AND u.createdAt <= :twentyFourHoursAgo")
    void deleteUnactivatedUsers(@Param("twentyFourHoursAgo") LocalDateTime twentyFourHoursAgo);
}
