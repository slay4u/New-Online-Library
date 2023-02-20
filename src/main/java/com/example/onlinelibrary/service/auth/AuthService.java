package com.example.onlinelibrary.service.impl;

import com.example.onlinelibrary.domain.User;
import com.example.onlinelibrary.domain.VerificationToken;
import com.example.onlinelibrary.dto.user.RegisterRequest;
import com.example.onlinelibrary.repository.UserDao;
import com.example.onlinelibrary.repository.VerificationTokenDao;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserDao userDao;
    private final VerificationTokenDao verificationTokenDao;

    @Transactional
    public void signup(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false); // once validated, will be set to true

        userDao.save(user);

        String token = generateVerificationToken(user);

    }

    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verificationTokenDao.save(verificationToken);
        return token;
    }
}
