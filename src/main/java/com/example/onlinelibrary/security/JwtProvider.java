package com.example.onlinelibrary.security;

import com.example.onlinelibrary.exception.AuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtProvider {

    private Key key;

    @Value("${jwt.expiration.time}")
    private Long jwtExpirationInMillis;

    @PostConstruct
    public void init() {
        key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    public String generateToken(Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .signWith(key)
                .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
                .compact();
    }

    public String generateTokenWithUsername(String username) {
        return Jwts.builder()
                .setSubject(username)
                .signWith(key)
                .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
                .compact();
    }

    public boolean validateToken(String jwt) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt);
        } catch (ExpiredJwtException e) {
            SecurityContextHolder.clearContext();
            throw new AuthenticationException("Token expired!");
        } catch (Exception e) {
            throw new AuthenticationException("Error while trying to read jwt token from client!");
        }
        return true;
    }

    public String getUsernameFromJwt(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public Long getJwtExpirationInMillis() {
        return jwtExpirationInMillis;
    }
}
