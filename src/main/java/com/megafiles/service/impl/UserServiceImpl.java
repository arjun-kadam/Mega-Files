package com.megafiles.service.impl;

import com.megafiles.entity.Users;
import com.megafiles.repository.UsersRepository;
import com.megafiles.service.AzureService;
import com.megafiles.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList("image/png", "image/jpeg", "image/jpg");
    private static final long MAX_FILE_SIZE = 300 * 1024; // 300 KB


    private final UsersRepository usersRepository;
    private final AzureService azureService;

    @Value("${azure.storage.profile-pics-container}")
    private String profilePicsContainer;

    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return usersRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
            }
        };
    }


    public Users updateProfile(String email, String name, String password, MultipartFile profilePicture) throws IOException {
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (name != null && !name.isEmpty()) {
            user.setName(name);
        }
        if (password != null && !password.isEmpty()) {
            user.setPassword(password);
        }
        if (profilePicture != null && !profilePicture.isEmpty()) {
            if (!ALLOWED_CONTENT_TYPES.contains(profilePicture.getContentType())) {
                throw new IllegalArgumentException("Invalid file type. Only PNG, JPEG, and JPG are allowed.");
            }
            if (profilePicture.getSize() > MAX_FILE_SIZE) {
                throw new IllegalArgumentException("File size exceeds the maximum limit of 300KB.");
            }
            String fileName = azureService.uploadFile(profilePicture, profilePicsContainer);

            user.setProfilePictureUrl(azureService.getFileUrl(fileName, profilePicsContainer));
        }

        // Save updated user profile
        return usersRepository.save(user);
    }

}
