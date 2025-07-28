package com.example.DemoSpringBootTinasoft.service;

import com.example.DemoSpringBootTinasoft.repositories.TokenRepository;
import com.example.DemoSpringBootTinasoft.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledCleanupService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

     //Chạy vào lúc 2 giờ sáng mỗi ngày để xóa các user chưa kích hoạt sau 24 giờ.
    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupUnactivatedUsers() {
        log.info("CRON JOB: Starting cleanup for unactivated users...");
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minus(24, ChronoUnit.HOURS);
        userRepository.deleteUnactivatedUsers(twentyFourHoursAgo);
        log.info("CRON JOB: Finished cleanup for unactivated users.");
    }

    //Chạy mỗi giờ để xóa các token đã hết hạn.
    @Scheduled(cron = "0 0 * * * *")
    public void cleanupExpiredTokens() {
        log.info("CRON JOB: Starting cleanup for expired tokens...");
        tokenRepository.deleteExpiredTokens(LocalDateTime.now());
        log.info("CRON JOB: Finished cleanup for expired tokens.");
    }
}