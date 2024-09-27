package com.megafiles.service;

import com.megafiles.dto.FileDTO;
import com.megafiles.dto.FileUploadResponse;
import com.megafiles.entity.Files;
import com.megafiles.enums.FileStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface FileService {
    FileUploadResponse uploadFile(MultipartFile file, FileStatus fileStatus) throws IOException;
    void deleteFile(Long fileId);
    List<FileDTO> topTenFiles();
    void fileDownload(Long fileId);
    List<FileDTO> mostPopularFiles();
    void reportFile(Long id);
    List<Files> filesByUser(String email);
    String getFileByShortURL(String randomId, HttpServletRequest request);
    List<FileDTO> getAllPublicFiles();
    Files changeFileAccess(FileStatus status,Long fileId);
}
