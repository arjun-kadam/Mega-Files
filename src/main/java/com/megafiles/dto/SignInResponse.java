package com.megafiles.dto;

import com.megafiles.enums.Roles;
import com.megafiles.enums.UserStatus;
import lombok.Data;

@Data
public class SignInResponse {
    private String token;
    private String refreshToken;
    private Roles roles;
    private UserStatus status;
    private String profilePictureUrl;
}
