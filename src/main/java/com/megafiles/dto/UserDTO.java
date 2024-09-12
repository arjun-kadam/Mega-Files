package com.megafiles.dto;

import com.megafiles.enums.Roles;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String password;
    private Roles role;

    // New field to store profile picture URL
    private String profilePictureUrl;
}
