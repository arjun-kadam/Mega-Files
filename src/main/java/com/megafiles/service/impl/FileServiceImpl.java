package com.megafiles.service.impl;

import com.megafiles.entity.Files;
import com.megafiles.entity.Users;
import com.megafiles.repository.FilesRepository;
import com.megafiles.repository.UsersRepository;
import com.megafiles.service.AzureService;
import com.megafiles.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final FilesRepository filesRepository;
    private final UsersRepository usersRepository;
    private final AzureService azureService;

    @Value("${azure.storage.user-files-container}")
    private String userFilesContainer;

    // Upload file (for logged-in users)
    public Files uploadFile(MultipartFile file) throws IOException {
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

        filesRepository.save(uploadingFile);

        return uploadingFile;
    }


    public void deleteFile(Long fileId){
        azureService.deleteFile(fileId,userFilesContainer);
    }

}
