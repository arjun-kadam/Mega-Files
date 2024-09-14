package com.megafiles.service.impl;

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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final FilesRepository filesRepository;
    private final UsersRepository usersRepository;
    private final AzureService azureService;

    @Value("${azure.storage.user-files-container}")
    private String userFilesContainer;

    // Upload file (for logged-in users)
    public FileUploadResponse uploadFile(MultipartFile file, FileStatus status) throws IOException {
        Files uploadingFile = new Files();

        // Upload file to Azure Blob Storage and get the file name
        String fileName = azureService.uploadFile(file, userFilesContainer);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Users user = usersRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Save file metadata
        uploadingFile.setFilename(file.getOriginalFilename());
        uploadingFile.setFileSize(file.getSize());
        uploadingFile.setFileUrl(azureService.getFileUrl(fileName, userFilesContainer));
        uploadingFile.setUser(user);
        uploadingFile.setShortUrl(shortURL());
        uploadingFile.setFileStatus(status);
        uploadingFile.setUploadTime(LocalDateTime.now());
        uploadingFile.setDownloadCount(0);
        uploadingFile.setReportCount(0);
        Files savedFile = filesRepository.save(uploadingFile);
        String shortUrl = savedFile.getFileStatus().name().equals("PUBLIC") ? savedFile.getShortUrl() : null;


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
        return "http://localhost:8080/files/short/" + UUID.randomUUID().toString();
    }

    public List<Files> topTenFiles() {
        return filesRepository.findTop10ByOrderByUploadTimeDesc();
    }

    public List<Files> mostPopularFiles(){
        return filesRepository.findTop10ByOrderByDownloadCountDesc();
    }

    public List<Files> filesByUser(String email){
        return filesRepository.findByUserEmail(email);
    }


    public void fileDownload(Long fileId) {
        Optional<Files> file = filesRepository.findById(fileId);
        file.ifPresent(files -> {
            files.setDownloadCount(files.getDownloadCount() + 1);
            filesRepository.save(files);
        });
    }



    public void reportFile(Long id){
        Optional<Files> file = filesRepository.findById(id);
        file.ifPresent(files -> {
            files.setReportCount(files.getReportCount() + 1);
            filesRepository.save(files);
        });
    }

    public String getFileByShortURL(String randomId, HttpServletRequest request){
        Optional<Files> file = filesRepository.findFilesByShortUrl(request.getRequestURL().toString());
        if (file.isPresent()) {
            FileStatus status=file.get().getFileStatus();
            if (status.name().equals("PUBLIC")){
                return file.get().getFileUrl();
            }
            return "File is Private";
        }
        return "File Not Found";
    }




}
