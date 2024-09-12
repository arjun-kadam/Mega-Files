package com.megafiles.controllers;


import com.megafiles.entity.Files;
import com.megafiles.repository.FilesRepository;
import com.megafiles.repository.UsersRepository;
import com.megafiles.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class FileController {
    private final FileService fileService;
    private final FilesRepository filesRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
       try {
           Files uploadedFile=fileService.uploadFile(file);
           return ResponseEntity.status(HttpStatus.ACCEPTED).body(uploadedFile);
       }catch (Exception e){
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable To Upload File");
       }

    }

    @GetMapping("/get-files")
    public List<Files> getAllFiles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return filesRepository.findByUserEmail(email);
    }


}
