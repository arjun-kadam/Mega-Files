package com.megafiles.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AzureUploadService {
    String uploadFile(MultipartFile file, String containerName) throws IOException;
    String getFileUrl(String fileName, String containerName);
}
