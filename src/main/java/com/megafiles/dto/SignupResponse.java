package com.megafiles.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupResponse {
    private Long id;
    private String name;
    private String email;
    private String profilePictureUrl;
    private String status;
    private String role;
    private LocalDateTime registerDate;
    private LocalDateTime lastProfileUpdate;
}
