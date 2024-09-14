package com.megafiles.repository;

import com.megafiles.entity.Files;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilesRepository extends JpaRepository<Files, Long> {
    List<Files> findByUserEmail(String email);
    List<Files> findTop10ByOrderByUploadTimeDesc();
    List<Files> findTop10ByOrderByDownloadCountDesc();
    Optional<Files> findFilesByShortUrl(String shortUrl);
}
