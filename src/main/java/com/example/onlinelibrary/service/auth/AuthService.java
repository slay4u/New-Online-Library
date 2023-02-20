package com.example.onlinelibrary.service.auth;

import com.example.onlinelibrary.domain.NotificationEmail;
import com.example.onlinelibrary.domain.Role;
import com.example.onlinelibrary.domain.User;
import com.example.onlinelibrary.domain.VerificationToken;
import com.example.onlinelibrary.dto.user.AuthenticationResponse;
import com.example.onlinelibrary.dto.user.LoginRequest;
import com.example.onlinelibrary.dto.user.RefreshTokenRequest;
import com.example.onlinelibrary.dto.user.RegisterRequest;
import com.example.onlinelibrary.exception.AuthenticationException;
import com.example.onlinelibrary.repository.UserDao;
import com.example.onlinelibrary.repository.VerificationTokenDao;
import com.example.onlinelibrary.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserDao userDao;
    private final VerificationTokenDao verificationTokenDao;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    @Value("${signup.token.time}")
    private Long tokenExpiration;

    public void signup(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false); // once validated, will be set to true
        user.setRole(Role.USER);

        userDao.save(user);

        String token = generateVerificationToken(user);
        mailService.sendEmail(new NotificationEmail("Please Active your Account",
                user.getEmail(), "Thank you for registering on online-library API." +
                                       "Please click the below URL to activate your account: " +
                                       "http://localhost:8080/online-library/v1/auth/accountVerification/" + token));
    }

    public void verifyToken(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenDao.findByToken(token);
        if(verificationToken.isEmpty()) {
            throw new AuthenticationException("Provided token does not exist!");
        }
        if(Instant.now().isAfter(verificationToken.get().getExpiryDate())) {
            throw new AuthenticationException("Verification token expired!");
        }
        fetchUserAndEnable(verificationToken.get());
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authenticate = getAuthentication(loginRequest);
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String token = jwtProvider.generateToken(authenticate);
        return new AuthenticationResponse(token,
                findUserByName(loginRequest.getUsername()).getUserId(),
                loginRequest.getUsername(),
                Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()),
                refreshTokenService.generateRefreshToken().getToken());
    }

    public Optional<org.springframework.security.core.userdetails.User> getCurrentUser() {
        org.springframework.security.core.userdetails.User principal =
                (org.springframework.security.core.userdetails.User)
                        SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Optional.of(principal);
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshToken) {
        refreshTokenService.validateRefreshToken(refreshToken.getRefreshToken());
        String token = jwtProvider.generateTokenWithUsername(refreshToken.getUsername());
        return new AuthenticationResponse(token,
                findUserByName(refreshToken.getUsername()).getUserId(),
                refreshToken.getUsername(),
                Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()),
                refreshToken.getRefreshToken());
    }

    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(Instant.now().plusMillis(tokenExpiration));

        verificationTokenDao.save(verificationToken);
        return token;
    }

    private void fetchUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        User presentUser = findUserByName(username);
        presentUser.setEnabled(true);
        userDao.save(presentUser);
    }

    private User findUserByName(String username) {
        Optional<User> user = userDao.findByUsername(username);
        if(user.isEmpty()) {
            throw new AuthenticationException("User not found with name " + username);
        }
        return user.get();
    }

    private Authentication getAuthentication(LoginRequest loginRequest) {
        Authentication authenticate;
        try {
            authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (Exception e) {
            throw new InsufficientAuthenticationException("Credentials are not valid!");
        }
        return authenticate;
    }
}
