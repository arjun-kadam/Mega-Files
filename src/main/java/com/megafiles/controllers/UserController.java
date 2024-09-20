package com.megafiles.controllers;

import com.megafiles.config.JwtAuthenticationFilter;
import com.megafiles.dto.FileUploadResponse;
import com.megafiles.dto.ProfileResponse;
import com.megafiles.entity.Files;
import com.megafiles.entity.Users;
import com.megafiles.enums.FileStatus;
import com.megafiles.enums.Roles;
import com.megafiles.repository.UsersRepository;
import com.megafiles.service.FileService;
import com.megafiles.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {
    private final FileService fileService;
    private final UserService userService;


    @PutMapping("/update-profile")
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
            return ResponseEntity.status(500).body("An error occurred : "+e.getMessage());

        }
    }


    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long id){
        try{
            Optional<Users> user=userService.getUserById(id);
            if (user.isPresent()){
                ProfileResponse profileResponse= new ProfileResponse(
                     user.get().getId(),
                     user.get().getName(),
                     user.get().getProfilePictureUrl(),
                     user.get().getRegisterDate(),
                     user.get().getLastProfileUpdate()
                );
                return ResponseEntity.status(HttpStatus.FOUND).body(profileResponse);
            }else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");
            }

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something Went Wrong");
        }
    }


    @DeleteMapping("/file/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable Long fileId) {
        try {
            fileService.deleteFile(fileId);
            return ResponseEntity.status(HttpStatus.OK).body("File Deleted Success");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable To Delete File");
        }
    }


    @PostMapping("/unblock-request")
    public ResponseEntity<?> unblockRequest(@RequestParam String email){
        try{
            userService.unblockRequest(email);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Request Send For Unblocking Wait For Approval");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something Went Wrong");
        }
    }


    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file ,@RequestParam("status") FileStatus status) throws IOException {
        try {
            FileUploadResponse uploadedFile=fileService.uploadFile(file,status);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(uploadedFile);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable To Upload File");
        }

    }

    @GetMapping("/all-files")
    public List<Files> getAllFiles() {
        String currentUser = JwtAuthenticationFilter.CURRENT_USER;
        System.out.println(currentUser);
        return fileService.filesByUser(currentUser);
    }





}
