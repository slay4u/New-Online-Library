package com.example.onlinelibrary.service.auth;

import com.example.onlinelibrary.domain.User;
import com.example.onlinelibrary.exception.AuthenticationException;
import com.example.onlinelibrary.repository.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserDao userDao;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userDao.findByUsername(username);
        if(userOptional.isEmpty()) {
            throw new AuthenticationException("User with username " + username + " has not been found!");
        }
        User user = userOptional.get();
        return new org.springframework.security.core.userdetails.User(
              user.getUsername(),
              user.getPassword(),
              user.isEnabled(),
              true,
              true,
              true,
              getAuthorities(user.getRole().toString())
        );
    }

    public Optional<User> getUserById(Long id) {
        return userDao.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userDao.findByUsername(username);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }
}
