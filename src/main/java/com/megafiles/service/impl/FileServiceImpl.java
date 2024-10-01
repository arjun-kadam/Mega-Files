package com.megafiles.service.impl;

import com.megafiles.dto.FileDTO;
import com.megafiles.dto.FileUploadResponse;
import com.megafiles.entity.Files;
import com.megafiles.entity.Users;
import com.megafiles.enums.FileStatus;
import com.megafiles.repository.FilesRepository;
import com.megafiles.repository.UsersRepository;
import com.megafiles.service.AzureService;
import com.megafiles.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final FilesRepository filesRepository;
    private final UsersRepository usersRepository;
    private final AzureService azureService;

    private static final Long USER_LIMIT= 1024L;

    @Value("${azure.storage.user-files-container}")
    private String userFilesContainer;

    private Long getUserLimit(String email){
        long fileSize=0;
        List<Files> allFiles = filesRepository.findByUserEmailOrderByUploadTimeDesc(email);
        for (Files file : allFiles) {
            fileSize+=file.getFileSize();
        }
        return fileSize/(1024*1024);
    }

    public FileUploadResponse uploadFile(MultipartFile file, FileStatus status) throws IOException {


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Users user = usersRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Long currentUserFileSize = getUserLimit(auth.getName());
        Long newFileSizeInMB = file.getSize() / (1024 * 1024);
        if (currentUserFileSize+newFileSizeInMB>USER_LIMIT){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your File Upload Limit Exceeded More Than 1GB !!!");
        }

        String fileName = azureService.uploadFile(file, userFilesContainer);

        Files uploadingFile = new Files();
        uploadingFile.setFilename(Objects.requireNonNull(file.getOriginalFilename()).length() > 35 ? file.getOriginalFilename().substring(0, 35) : file.getOriginalFilename());
        uploadingFile.setFileSize(file.getSize());
        uploadingFile.setFileUrl(azureService.getFileUrl(fileName, userFilesContainer));
        uploadingFile.setUser(user);
        uploadingFile.setShortUrl(shortURL());
        uploadingFile.setFileStatus(status);
        uploadingFile.setUploadTime(LocalDateTime.now());
        uploadingFile.setDownloadCount(0);
        uploadingFile.setReportCount(0);
        Files savedFile = filesRepository.save(uploadingFile);
        String shortUrl = savedFile.getShortUrl();


        return new FileUploadResponse(
                savedFile.getFileId(),
                savedFile.getFilename(),
                savedFile.getFileSize(),
                savedFile.getFileStatus(),
                savedFile.getFileUrl(),
                shortUrl,
                savedFile.getUploadTime()
        );


    }


    public void deleteFile(Long fileId) {
        azureService.deleteFile(fileId, userFilesContainer);
    }

    private String shortURL() {
        return "https://megafiles-pro.netlify.app/files/short/" + UUID.randomUUID().toString();
    }

    public List<FileDTO> topTenFiles() {
        return filesRepository.findTop10ByOrderByUploadTimeDesc()
                .stream()
                .filter(file -> isFilePublic(file.getFileId()))
                .map(file -> {
                    FileDTO dto = new FileDTO();
                    dto.setFileId(file.getFileId());
                    dto.setFilename(file.getFilename());
                    dto.setFileSize(file.getFileSize());
                    dto.setFileUrl(file.getFileUrl());
                    dto.setShortUrl(file.getShortUrl());
                    dto.setFileStatus(file.getFileStatus());
                    dto.setUploadTime(file.getUploadTime());
                    dto.setDownloadCount(file.getDownloadCount());
                    dto.setReportCount(file.getReportCount());

                    if (file.getUser() != null) {
                        dto.setUsername(file.getUser().getName());
                        dto.setProfilePictureUrl(file.getUser().getProfilePictureUrl());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }


    public List<FileDTO> mostPopularFiles() {
        return filesRepository.findTop10ByOrderByDownloadCountDesc()
                .stream()
                .filter(file -> isFilePublic(file.getFileId()))
                .map(file -> {
                    FileDTO dto = new FileDTO();
                    dto.setFileId(file.getFileId());
                    dto.setFilename(file.getFilename());
                    dto.setFileSize(file.getFileSize());
                    dto.setFileUrl(file.getFileUrl());
                    dto.setShortUrl(file.getShortUrl());
                    dto.setFileStatus(file.getFileStatus());
                    dto.setUploadTime(file.getUploadTime());
                    dto.setDownloadCount(file.getDownloadCount());
                    dto.setReportCount(file.getReportCount());

                    if (file.getUser() != null) {
                        dto.setUsername(file.getUser().getName());
                        dto.setProfilePictureUrl(file.getUser().getProfilePictureUrl());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<Files> filesByUser(String email) {
        return filesRepository.findByUserEmailOrderByUploadTimeDesc(email);
    }


    public void fileDownload(Long fileId) {
        Optional<Files> file = filesRepository.findById(fileId);
        file.ifPresent(files -> {
            files.setDownloadCount(files.getDownloadCount() + 1);
            filesRepository.save(files);
        });
    }


    public void reportFile(Long id) {
        Optional<Files> file = filesRepository.findById(id);
        file.ifPresent(files -> {
            files.setReportCount(files.getReportCount() + 1);
            filesRepository.save(files);
        });
    }

    public String getFileByShortURL(String randomId, HttpServletRequest request) {
        Optional<Files> file = filesRepository.findFilesByShortUrl(request.getRequestURL().toString());
        if (file.isPresent()) {
            FileStatus status = file.get().getFileStatus();
            if (status.name().equals("PUBLIC")) {
                return file.get().getFileUrl();
            }
            return "File is Private";
        }
        return "File Not Found";
    }

    private boolean isFilePublic(Long fileId) {
        return filesRepository.findById(fileId)
                .map(file -> file.getFileStatus() == FileStatus.PUBLIC)
                .orElse(false);
    }


    public List<FileDTO> getAllPublicFiles() {
        return filesRepository.findAll()
                .stream()
                .filter(file -> isFilePublic(file.getFileId()))
                .map(file -> {
                    FileDTO dto = new FileDTO();
                    dto.setFileId(file.getFileId());
                    dto.setFilename(file.getFilename());
                    dto.setFileSize(file.getFileSize());
                    dto.setFileUrl(file.getFileUrl());
                    dto.setShortUrl(file.getShortUrl());
                    dto.setFileStatus(file.getFileStatus());
                    dto.setUploadTime(file.getUploadTime());
                    dto.setDownloadCount(file.getDownloadCount());
                    dto.setReportCount(file.getReportCount());

                    if (file.getUser() != null) {
                        dto.setUsername(file.getUser().getName());
                        dto.setProfilePictureUrl(file.getUser().getProfilePictureUrl());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }



    public Files changeFileAccess(FileStatus status,Long fileId){
        Files file=filesRepository.findById(fileId).orElseThrow(()->new RuntimeException("File Not Found with id"+fileId));
        file.setFileStatus(status);
        return filesRepository.save(file);
    }

}
