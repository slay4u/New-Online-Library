package com.example.onlinelibrary.repository;

import com.example.onlinelibrary.domain.ImageData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageDataRepository extends JpaRepository<ImageData, Long> {
}
