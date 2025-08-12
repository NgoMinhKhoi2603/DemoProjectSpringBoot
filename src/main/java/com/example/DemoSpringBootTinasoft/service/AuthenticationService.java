package com.example.DemoSpringBootTinasoft.service;

import com.example.DemoSpringBootTinasoft.dtos.AuthenticationRequest;
import com.example.DemoSpringBootTinasoft.dtos.AuthenticationResponse;
import com.example.DemoSpringBootTinasoft.dtos.RegistrationRequest;
import com.example.DemoSpringBootTinasoft.entities.Role;
import com.example.DemoSpringBootTinasoft.entities.Token;
import com.example.DemoSpringBootTinasoft.entities.User;
import com.example.DemoSpringBootTinasoft.repositories.RoleRepository;
import com.example.DemoSpringBootTinasoft.repositories.TokenRepository;
import com.example.DemoSpringBootTinasoft.repositories.UserRepository;
import com.example.DemoSpringBootTinasoft.security.JwtService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.jwt.expiration-in-ms}")
    private long jwtExpiration;

    @Value("${app.frontend.activation-url}")
    private String activationUrl;

    public void register(RegistrationRequest request) throws MessagingException {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("Email already exists");
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("ROLE USER was not initiated"));

        var user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(userRole))
                .enabled(false)
                .build();
        userRepository.save(user);

        sendValidationEmail(user);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);

        // Key là chính token, value là email của user.
        // Thời gian sống của key trong Redis bằng thời gian sống của JWT.
        redisTemplate.opsForValue().set(
                jwtToken,
                user.getEmail(),
                jwtExpiration,
                TimeUnit.MILLISECONDS
        );

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public void activateAccount(String token) {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token has expired. A new token has been sent.");
        }

        var user = savedToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }

    private void sendValidationEmail(User user) {
        var newToken = generateAndSaveActivationToken(user);

        String activationLink = activationUrl + "?token=" + newToken;

        try {
            emailService.sendActivationEmail(user.getEmail(), user.getFullName(), activationLink);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send activation email", e);
        }
    }

    private String generateAndSaveActivationToken(User user) {
        String generatedToken = UUID.randomUUID().toString();
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(60)) // Token hết hạn sau 60 phút
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }
}