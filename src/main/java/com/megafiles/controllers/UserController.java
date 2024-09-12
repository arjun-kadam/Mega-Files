package com.megafiles.controllers;

import com.megafiles.entity.Files;
import com.megafiles.entity.Users;
import com.megafiles.repository.UsersRepository;
import com.megafiles.service.FileService;
import com.megafiles.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final FileService fileService;
    private final UserService userService;


    @PostMapping("/update-profile")
    public ResponseEntity<String> updateProfile(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "profile-pic", required = false) MultipartFile profilePicture) {

        // Get the currently logged-in user's email
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            Users updatedUser = userService.updateProfile(email, name, password, profilePicture);
            return ResponseEntity.ok("Profile updated successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("An error occurred while uploading the profile picture.");
        }
    }


    @DeleteMapping("/files/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable Long fileId){
        try {
            fileService.deleteFile(fileId);
            return ResponseEntity.status(HttpStatus.OK).body("File Deleted Success");
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable To Delete File");
        }
    }



}
