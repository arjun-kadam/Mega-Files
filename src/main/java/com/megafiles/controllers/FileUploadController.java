package com.megafiles.controllers;


import com.megafiles.entity.Files;
import com.megafiles.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class FileUploadController {
    private final FileService fileService;
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
       try {
           Files uploadedFile=fileService.uploadFile(file);
           return ResponseEntity.status(HttpStatus.ACCEPTED).body(uploadedFile);
       }catch (Exception e){
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable To Upload File");
       }

    }
}
