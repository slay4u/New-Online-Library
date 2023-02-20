package com.example.onlinelibrary.repository;

import com.example.onlinelibrary.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDao extends JpaRepository<User, Long> {

    @Query(value = "SELECT * FROM public.usert" +
            " WHERE username = ?1", nativeQuery = true)
    Optional<User> findByUsername(String username);
}
