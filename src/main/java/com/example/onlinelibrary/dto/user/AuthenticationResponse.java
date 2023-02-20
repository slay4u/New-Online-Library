package com.example.onlinelibrary.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String authenticationToken;
    private Long userId;
    private String username;
    private Instant expiresAt;
    private String refreshToken;
}
