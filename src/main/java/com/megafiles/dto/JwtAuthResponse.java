package com.megafiles.dto;

import com.megafiles.enums.Roles;
import lombok.Data;

@Data
public class JwtAuthResponse {
    private String token;
    private String refreshToken;
    private Roles roles;

}
