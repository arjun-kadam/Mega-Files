package com.megafiles.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {
    private Long id;
    private String name;
    private String profilePictureUrl;
    private LocalDateTime registerDate;
    private LocalDateTime lastProfileUpdate;
}
