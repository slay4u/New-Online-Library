package com.example.onlinelibrary.repository;

import com.example.onlinelibrary.domain.ImageData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageDataDao extends JpaRepository<ImageData, Long> {

    @Query(value = "SELECT * FROM public.image_data" +
            " WHERE name = ?1 AND file_path = ?2", nativeQuery = true)
    Optional<ImageData> findByNameAndFilePath(String name, String filePath);

    @Query(value = "SELECT * FROM public.image_data" +
            " WHERE id_book = ?1", nativeQuery = true)
    List<ImageData> findAllByBookId(Long id);
}
