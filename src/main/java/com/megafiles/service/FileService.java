package com.megafiles.service;

import com.megafiles.entity.Files;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    public Files uploadFile(MultipartFile file) throws IOException;
    public void deleteFile(Long fileId);
}
