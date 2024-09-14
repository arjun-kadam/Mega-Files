package com.megafiles.service;



import com.megafiles.entity.Users;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;


public interface UserService {
    public UserDetailsService userDetailsService();
    Users updateProfile(String email, String name, String password, MultipartFile profilePicture) throws IOException;
    public Optional<Users> getUserById(Long id);
    public void unblockRequest(String email);
}
