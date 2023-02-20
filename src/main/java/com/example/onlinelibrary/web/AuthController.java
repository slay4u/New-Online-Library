package com.example.onlinelibrary.web;

import com.example.onlinelibrary.dto.user.AuthenticationResponse;
import com.example.onlinelibrary.dto.user.LoginRequest;
import com.example.onlinelibrary.dto.user.RefreshTokenRequest;
import com.example.onlinelibrary.dto.user.RegisterRequest;
import com.example.onlinelibrary.service.auth.AuthService;
import com.example.onlinelibrary.service.auth.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/online-library/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody RegisterRequest registerRequest) {
        authService.signup(registerRequest);
        return new ResponseEntity<>("User registration successful!", HttpStatus.OK);
    }

    @GetMapping("/accountVerification/{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable String token) {
        authService.verifyToken(token);
        return new ResponseEntity<>("Account activated successfully!", HttpStatus.OK);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthenticationResponse login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/refresh/token")
    public AuthenticationResponse refreshTokens(@Valid @RequestBody RefreshTokenRequest refreshToken) {
        return authService.refreshToken(refreshToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequest refreshToken) {
        refreshTokenService.deleteRefreshToken(refreshToken.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK).body("Please, log in!");
    }
}
