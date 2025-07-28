package com.example.DemoSpringBootTinasoft.controller;

import com.example.DemoSpringBootTinasoft.dtos.AuthenticationRequest;
import com.example.DemoSpringBootTinasoft.dtos.AuthenticationResponse;
import com.example.DemoSpringBootTinasoft.dtos.RegistrationRequest;
import com.example.DemoSpringBootTinasoft.service.AuthenticationService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register(@RequestBody @Valid RegistrationRequest request) throws MessagingException {
        service.register(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @GetMapping("/activate-account")
    public ResponseEntity<?> confirm(@RequestParam String token) {
        try {
            service.activateAccount(token);
            return ResponseEntity.ok("Account activated successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
