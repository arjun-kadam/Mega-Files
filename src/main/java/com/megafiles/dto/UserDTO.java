package com.megafiles.dto;

import com.megafiles.enums.Roles;
import com.megafiles.enums.UserStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String password;
    private Roles role;
    private UserStatus status;
    private LocalDateTime registerDate;
    private LocalDateTime lastProfileUpdate;


    // New field to store profile picture URL
    private String profilePictureUrl;
}
