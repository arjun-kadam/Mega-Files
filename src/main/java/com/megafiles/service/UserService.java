package com.megafiles.service;



import com.megafiles.entity.Users;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface UserService {
    public UserDetailsService userDetailsService();
    Users updateProfile(String email, String name, String password, MultipartFile profilePicture) throws IOException;
}
