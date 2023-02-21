package com.example.onlinelibrary.config.aspect;

import com.example.onlinelibrary.dto.favorites.FavoritesCreateDto;
import com.example.onlinelibrary.dto.user.RefreshTokenRequest;
import com.example.onlinelibrary.exception.AuthenticationException;
import com.example.onlinelibrary.service.auth.AuthService;
import com.example.onlinelibrary.service.auth.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Optional;

@Aspect
@Configuration
@Slf4j
@RequiredArgsConstructor
public class UserAccessAspect {

    private final AuthService authService;

    private final UserDetailsServiceImpl userDetailsService;

    @Before("execution(* com.example.onlinelibrary.service.impl.*.*(..))")
    public void checkAuth(JoinPoint joinPoint) {
        log.info("Check for user access...");
        User user = authService.getCurrentUser()
                .orElseThrow(() -> new AuthenticationException("Unauthorized access!"));
        log.info("Request from user: {}", user.getUsername());
        log.info("Allowed execution for {}", joinPoint);
    }

    @Before("execution(* com.example.onlinelibrary.service.impl.FavoritesServiceImpl.addFavorites(..))")
    public void checkIndividualFavoritesAdd(JoinPoint joinPoint) {
        User user = authService.getCurrentUser().orElseThrow();
        Object[] signatureArgs = joinPoint.getArgs();
        FavoritesCreateDto toCheck = null;
        for (Object signatureArg: signatureArgs) {
             toCheck = (FavoritesCreateDto) signatureArg;
        }
        assert toCheck != null;
        Long idUser = toCheck.getIdUser();
        com.example.onlinelibrary.domain.User userToCheck = getUserOrThrow(idUser);
        compareUsers(user, userToCheck);
    }

    @Before("execution(* com.example.onlinelibrary.service.impl.FavoritesServiceImpl.getFavoritesByUserId(..))")
    public void checkIndividualFavoritesAll(JoinPoint joinPoint) {
        checkTwoUsers(joinPoint);
    }

    @Before("execution(* com.example.onlinelibrary.service.impl.FavoritesServiceImpl.deleteBookFromFavoritesByUserId(..))")
    public void checkIndividualFavoritesDelete(JoinPoint joinPoint) {
        checkTwoUsers(joinPoint);
    }

    @Before("execution(* com.example.onlinelibrary.service.auth.AuthService.refreshToken(..))")
    public void checkUsernameRefreshToken(JoinPoint joinPoint) {
        User user = authService.getCurrentUser().orElseThrow();
        Object[] signatureArgs = joinPoint.getArgs();
        RefreshTokenRequest rToken = (RefreshTokenRequest) Arrays.stream(signatureArgs)
                .findFirst()
                .orElseThrow();
        String nameToCheck = rToken.getUsername();
        com.example.onlinelibrary.domain.User userToCheck = userDetailsService
                .getUserByUsername(nameToCheck).orElseThrow(() ->
                        new AuthenticationException("User with username " + nameToCheck + " has not been found!"));
        compareUsers(user, userToCheck);

    }

    private void checkTwoUsers(JoinPoint joinPoint) {
        User user = authService.getCurrentUser().orElseThrow();
        Object[] signatureArgs = joinPoint.getArgs();
        Long idToCheck = (Long) Arrays.stream(signatureArgs).findFirst().orElseThrow();
        com.example.onlinelibrary.domain.User userToCheck = getUserOrThrow(idToCheck);
        compareUsers(user, userToCheck);
    }

    private com.example.onlinelibrary.domain.User getUserOrThrow(Long idUser) {
        return userDetailsService.getUserById(idUser)
                .orElseThrow(() -> new AuthenticationException("User id not found " + idUser));
    }

    private void compareUsers(User user, com.example.onlinelibrary.domain.User userToCheck) {
        if(user.getUsername().equals(userToCheck.getUsername())) {
            log.info("Authorized " + user.getUsername());
        } else {
            log.warn("Authenticated user: " + user.getUsername() + "\n" +
                    "Trying to access: " + userToCheck.getUsername());
            throw new AuthenticationException("Trying to access another's resource!");
        }
    }
}
