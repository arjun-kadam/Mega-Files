package com.megafiles.repository;

import com.megafiles.entity.Files;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilesRepository extends JpaRepository<Files, Long> {
    List<Files> findByUserEmail(String email);
}
