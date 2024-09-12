package com.megafiles.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AzureService {
    String uploadFile(MultipartFile file, String containerName) throws IOException;
    String getFileUrl(String fileName, String containerName);
    public void deleteFile(Long fileId,String containerName);
}
