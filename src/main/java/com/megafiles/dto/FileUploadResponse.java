package com.megafiles.dto;

import com.megafiles.enums.FileStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadResponse {
    private Long id;
    private String name;
    private Long size;
    private FileStatus status;
    private String fileUrl;
    private String shortLink;
    private LocalDateTime uploadDate;
}
